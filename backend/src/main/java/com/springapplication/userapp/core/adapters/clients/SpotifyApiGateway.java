package com.springapplication.userapp.core.adapters.clients;

import com.google.common.cache.Cache;
import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.error.UserError;
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
import com.springapplication.userapp.client.model.MusicBrainzDTO;
import com.springapplication.userapp.providers.countryISO.CountryISOCache;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class SpotifyApiGateway implements SpotifyGateway {

    private final ClientBuilder clientBuilder;
    private final Cache<String, String> tokenCache;
    private final String clientId;
    private final CountryISOCache countryISOCache;
    private final HashMap<String, ArrayList<TopTrackDTO>> topTracksCache;

    private final Logger logger = LoggerFactory.getLogger(SpotifyApiGateway.class);


    public SpotifyApiGateway(ClientBuilder clientBuilder, TokenCacheProvider tokenCacheProvider,
                             @Value("${my.client.id}") String clientId, CountryISOCache countryISOCache) {
        this.clientBuilder = clientBuilder;
        this.tokenCache = tokenCacheProvider.generateCache();
        this.clientId = clientId;
        this.countryISOCache = countryISOCache;
        this.topTracksCache = new HashMap<>();
    }

    @Override
    public Either<UserError, AuthTokenDTO> getRefreshToken(String code, String state, String redirect_uri) {
        return authorizationRequest(code, state, redirect_uri).block();
    }

    @Override
    public Either<UserError, ArrayList<TopTrackDTO>> getTopTracks(User user, int howMany) {

        if(topTracksCache.containsKey(user.getUsername())){
            return Either.right(topTracksCache.get(user.getUsername()));
        }
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
            var maybeCountry = syncTotalObject(artistId, user).flatMap(this::getArtistCountry);
            if(maybeCountry.isRight()) topTrackDTOs.add(maybeCountry.get());

            try {Thread.sleep(1000);} catch (Exception e){
                logger.error("Failed to call Spotify API for thread sleep error", e);
            }
        }

        return Either.right(topTrackDTOs);
    }

    private Either<UserError, TopTrackDTO> getArtistCountry(TotalObjectDTO dto){
        var topTrackDTO = new TopTrackDTO();
        topTrackDTO.setName(dto.getName());
        String artistName = dto.getArtists().get(0).getName();
        topTrackDTO.setArtist(artistName);
        topTrackDTO.setImg(dto.getAlbum().getImages().get(1).getUrl());

        var maybeCountry = syncMusicBrainz(artistName);

        return maybeCountry
                .flatMap(country -> {
                    var iso = setCountryISO(country);
                    if(iso.isLeft()) return Either.left(iso.getLeft());
                    topTrackDTO.setCountry(iso.get());
                    return Either.right(topTrackDTO);
                });

    }

    private Either<UserError, String> setCountryISO(String country){
        String iso = countryISOCache.getISOFromCountry(country);
        if(iso == null){
            logger.error("No ISO for country name: " + country);
            var error = new UserError.GenericError("No ISO for country name: " + country);
            return Either.left(error);
        }

        return Either.right(iso);
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

    private Either<UserError, String> syncMusicBrainz(String artist){
        return getCountrySync(artist).block();
    }

    private Mono<Either<UserError, String>> getCountrySync(String artist){
        return getArtistCountryDto(artist)
                .flatMap(dto -> {
                    var country = dto.getArtists().get(0).getArea().getName();
                    if(country == null){
                        var error = new UserError.GenericError("Error parsing Music Brainz");
                        logger.warn("Error parsing Music Brainz json");
                        return Mono.just(Either.left(error));
                    }
                    return Mono.just(Either.right(country));
                });
    }

    private Mono<MusicBrainzDTO> getArtistCountryDto(String artist){
        WebClient musicBrainzClient = clientBuilder.buildClient("/artist", "musicBrainz");

        return musicBrainzClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("query", artist)
                        .queryParam("fmt", "json")
                        .build())
                .retrieve()
                .bodyToMono(MusicBrainzDTO.class);
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
