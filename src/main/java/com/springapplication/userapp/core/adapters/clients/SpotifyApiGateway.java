package com.springapplication.userapp.core.adapters.clients;

import com.google.common.cache.Cache;
import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.error.UserError;
import com.springapplication.userapp.core.domain.port.output.SpotifyGateway;
import com.springapplication.userapp.providers.logging.Logger;
import com.springapplication.userapp.providers.logging.LoggerFactory;
import com.springapplication.userapp.providers.cache.CacheProvider;
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

import java.util.ArrayList;
import java.util.HashMap;

@Component
class SpotifyApiGateway implements SpotifyGateway {

    private final ClientBuilder clientBuilder;
    private final Cache<String, String> tokenCache;
    private final String clientId;
    private final MusicBrainzApiGateway musicBrainzApiGateway;
    private final Cache<String, ArrayList<TopTrackDTO>> topTracksCache;

    private final Logger logger = LoggerFactory.getLogger(SpotifyApiGateway.class);


    public SpotifyApiGateway(ClientBuilder clientBuilder, CacheProvider cacheProvider,
                             @Value("${my.client.id}") String clientId, MusicBrainzApiGateway musicBrainzApiGateway) {
        this.clientBuilder = clientBuilder;
        this.tokenCache = cacheProvider.generateTokenCache();
        this.clientId = clientId;
        this.musicBrainzApiGateway = musicBrainzApiGateway;
        this.topTracksCache = cacheProvider.generateTracksCache();
    }

    @Override
    public Either<UserError, AuthTokenDTO> getRefreshToken(String code, String state, String redirect_uri) {
        return authorizationRequest(code, state, redirect_uri).block();
    }

    @Override
    public Either<UserError, ArrayList<TopTrackDTO>> getTopTracks(User user, int howMany) {

        var tracks = topTracksCache.getIfPresent(user.getUsername());

        if(tracks != null) return Either.right(tracks);

        var maybeTopTrack = spotifyTopArtistsRequest(user, howMany);
        if(maybeTopTrack.isRight()){
            topTracksCache.put(user.getUsername(), maybeTopTrack.get());
            return maybeTopTrack;
        }
        return maybeTopTrack;
    }

    private Either<UserError, ArrayList<TopTrackDTO>> spotifyTopArtistsRequest(User user, int howMany) {
        return syncGetAccessTokenIfNecessary(user)
                .flatMap(__ -> syncSpotifyTopArtistRequest(user, howMany))
                .flatMap(dto -> getTops(dto, user, howMany));
    }

    private Either<UserError, ArrayList<TopTrackDTO>> getTops(TopTracksSpotifyResponseDTO dto, User user, int howMany){
        var topTrackDTOs = new ArrayList<TopTrackDTO>();

        for(int i = 0; i < howMany; i++){
            var artistId = dto.getItems().get(i).getId();
            var maybeCountry = syncTotalObject(artistId, user).flatMap(musicBrainzApiGateway::getArtistCountry);
            if(maybeCountry.isRight()) topTrackDTOs.add(maybeCountry.get());

            try {Thread.sleep(500);} catch (Exception e){
                logger.error("Failed to call Spotify API for thread sleep error", e);
            }
        }

        return Either.right(topTrackDTOs);
    }

    private Mono<Either<UserError, AuthTokenDTO>> authorizationRequest(String code, String state, String redirect_uri){
        return authorizationRequestBuilder(code, state, redirect_uri)
                .map(response -> {
                    if(response == null || response.getAccessToken() == null) return Either.left(new UserError.GenericError("Error in calling Spotify API"));
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
                .doOnNext(response -> logger.info("Received response with tokens"))
                .doOnError(error -> logger.error("Error during Spotify auth request", error));
    }

    private Either<UserError, String> syncGetAccessTokenIfNecessary(User user){
        return getAccessTokenIfNecessary(user).block();

    }

    private Either<UserError, TopTracksSpotifyResponseDTO> syncSpotifyTopArtistRequest(User user, int howMany){
        return spotifyTopTracksRequest(user, howMany).block();
    }

    private Either<UserError, TotalObjectDTO> syncTotalObject(String id, User user){
        return getFinalObject(id, user).block();
    }

    private Mono<Either<UserError, TopTracksSpotifyResponseDTO>> spotifyTopTracksRequest(User user, int howMany){
        return spotifyTopTracksRequestBuilder(user, howMany)
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

    private Mono<TopTracksSpotifyResponseDTO> spotifyTopTracksRequestBuilder(User user, int howMany) {
        String accessToken = tokenCache.getIfPresent(user.getUsername());
        String uri = String.format("/top/tracks?time_range=medium_term&limit=%s&offset=0", howMany);
        WebClient topTracksClient = clientBuilder.buildClient(uri, "user_analytics");
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
