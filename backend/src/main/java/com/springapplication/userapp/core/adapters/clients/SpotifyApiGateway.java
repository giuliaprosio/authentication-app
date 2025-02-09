package com.springapplication.userapp.core.adapters.clients;

import com.google.common.cache.Cache;
import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.UserError;
import com.springapplication.userapp.core.domain.port.output.SpotifyGateway;
import com.springapplication.userapp.providers.logging.Logger;
import com.springapplication.userapp.providers.logging.LoggerFactory;
import com.springapplication.userapp.providers.token.TokenCacheProvider;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import com.springapplication.userapp.controller.model.TopTrackDTO;
import com.springapplication.userapp.client.model.AuthTokenDTO;
import com.springapplication.userapp.client.model.TotalObjectDTO;
import com.springapplication.userapp.client.model.TopTracksSpotifyResponseDTO;
import reactor.core.publisher.Mono;

@Component
public class SpotifyApiGateway implements SpotifyGateway {

    private final ClientBuilder clientBuilder;
    private final Cache<String, String> tokenCache;
    private final String clientId;

    private final Logger logger = LoggerFactory.getLogger(SpotifyApiGateway.class);


    public SpotifyApiGateway(ClientBuilder clientBuilder, TokenCacheProvider tokenCacheProvider,
                             @Value("${my.client.id}") String clientId) {
        this.clientBuilder = clientBuilder;
        this.tokenCache = tokenCacheProvider.generateCache();
        this.clientId = clientId;
    }

    @Override
    public Either<UserError, AuthTokenDTO> getRefreshToken(String code, String state, String redirect_uri) {
        return authorizationRequest(code, state, redirect_uri).block();
    }

    @Override
    public Either<UserError, TopTrackDTO> getTopTrack(User user) {
        return spotifyTopArtistRequest(user);
    }

    private Either<UserError, TopTrackDTO> spotifyTopArtistRequest(User user) {
        return syncGetAccessTokenIfNecessary(user)
                .flatMap(__ -> syncSpotifyTopArtistRequest(user))
                .flatMap( dto ->  syncTotalObject(dto.getItems().get(0).getId(), user))
                .flatMap(el -> {
                    var dto = new TopTrackDTO();
                    dto.setName(el.getName());
                    dto.setImg(el.getAlbum().getImages().get(1).getUrl());
                    return Either.right(dto);
                });
    }

    private Mono<Either<UserError, AuthTokenDTO>> authorizationRequest(String code, String state, String redirect_uri){
        return authorizationRequestBuilder(code, state, redirect_uri)
                .map(response -> {
                    if(response == null) return Either.left(new UserError.GenericError("Error in calling Spotify API"));
                    tokenCache.put(state, response.getAccessToken());

                    return Either.right(response);
                });
    }

    private Mono<AuthTokenDTO> authorizationRequestBuilder(String code, String state, String redirect_uri){
        logger.info("Send to spotify request to an access token and refresh token");
        WebClient spotifyClient = clientBuilder.buildClient("/api/token", "auth");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirect_uri);

        return spotifyClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .bodyToMono(AuthTokenDTO.class)
                .doOnNext(response -> logger.info("Received response: " + response))
                .doOnError(error -> logger.error("Error during Spotify auth request", error));
    }

    private Either<UserError, String> syncGetAccessTokenIfNecessary(User user){
        return getAccessTokenIfNecessary(user).block();

    }

    private Either<UserError, TopTracksSpotifyResponseDTO> syncSpotifyTopArtistRequest(User user){
        return spotifyTopTracksRequest(user).block();
    }

    private Either<UserError, TotalObjectDTO> syncTotalObject(String id, User user){
        return getFinalObject(id, user).block();
    }

    private Mono<Either<UserError, TopTracksSpotifyResponseDTO>> spotifyTopTracksRequest(User user){
        return spotifyTopTracksRequestBuilder(user)
                .flatMap(dto -> {
                    if(dto == null) return Mono.just(Either.left(new UserError.GenericError("Error calling API")));
                    return Mono.just(Either.right(dto));
                });
    }

    private Mono<Either<UserError, String>> getAccessTokenIfNecessary(User user){
        String cachedToken = tokenCache.getIfPresent(user.getUsername());
        if(cachedToken != null){
            return Mono.just(Either.right(cachedToken));
        }
        logger.info("User does not have an access token, requesting a new one");

        return getAccessToken(user.getRefreshToken())
                .flatMap(dto -> {
                    tokenCache.put(user.getUsername(), dto.getAccessToken());
                    return Mono.just(Either.right(dto.getAccessToken()));
                });

    }

    private Mono<Either<UserError, TotalObjectDTO>> getFinalObject(String id, User user){
        return spotifyTotalObjectRequestBuilder(id, user)
                .flatMap(dto -> {
                    if(dto == null) return Mono.just(Either.left(new UserError.GenericError("Error calling API")));
                    return Mono.just(Either.right(dto));
                        }
                );
    }

    private Mono<TopTracksSpotifyResponseDTO> spotifyTopTracksRequestBuilder(User user) {
        String accessToken = tokenCache.getIfPresent(user.getUsername());
        WebClient topTracksClient = clientBuilder.buildClient("/top/tracks?time_range=medium_term&limit=1&offset=0", "user_analytics");
        return topTracksClient.get()
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(TopTracksSpotifyResponseDTO.class);
    }

    private Mono<TotalObjectDTO> spotifyTotalObjectRequestBuilder(String trackId, User user){
        var request = String.format("/tracks/%s", trackId);
        WebClient trackInfoClient = clientBuilder.buildClient(request, "analytics");
        String accessToken = tokenCache.getIfPresent(user.getUsername());
        return trackInfoClient.get()
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(TotalObjectDTO.class);
    }

    public Mono<AuthTokenDTO> getAccessToken(String refreshToken) {
        WebClient spotifyClient = clientBuilder.buildClient("/api/token", "auth");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", refreshToken);
        body.add("client_id", clientId);

        return spotifyClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .bodyToMono(AuthTokenDTO.class)
                .doOnError(e -> logger.error("Error calling API, " + e.getLocalizedMessage()));
    }
}
