package com.springapplication.userapp.core.adapters.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.springapplication.userapp.client.model.AuthTokenDTO;
import com.springapplication.userapp.controller.model.TopTrackDTO;
import com.springapplication.userapp.core.domain.model.DTOsObjectMother;
import com.springapplication.userapp.client.model.TopTracksSpotifyResponseDTO;
import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.UserObjectMother;
import com.springapplication.userapp.core.domain.model.error.UserError;
import com.springapplication.userapp.providers.cache.CacheProvider;
import com.springapplication.userapp.client.model.TotalObjectDTO;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpotifyApiGatewayTest {

    @Mock
    private ClientBuilder clientBuilder;

    @Mock
    private CacheProvider cacheProvider;

    private final Cache<String, String> tokenCache = CacheBuilder.newBuilder().build();

    private final Cache<String, ArrayList<TopTrackDTO>> tracksCache = CacheBuilder.newBuilder().build();

    @Mock
    private MusicBrainzApiGateway musicBrainzApiGateway;

    private String clientId = "clientId";

    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private SpotifyApiGateway spotifyApiGateway;

    @BeforeEach
    void setup() {
        when(cacheProvider.generateTokenCache()).thenReturn(tokenCache);
        when(cacheProvider.generateTracksCache()).thenReturn(tracksCache);
        tokenCache.cleanUp();
        tracksCache.cleanUp();
        spotifyApiGateway = new SpotifyApiGateway(clientBuilder, cacheProvider, clientId, musicBrainzApiGateway);
    }

    @Test
    void givenValidData_whenGetRefreshToken_returnTokenDTO() {
        String code = "validCode";
        String state = "validState";
        String redirectUri = "http://redirect.uri";
        AuthTokenDTO authTokenDTO = DTOsObjectMother.createValidAuthTokenDTO();

        WebClient webClientMock = mock(WebClient.class);
        WebClient.RequestBodyUriSpec requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpecMock = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        when(clientBuilder.buildClient("/api/token", "auth")).thenReturn(webClientMock);
        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.contentType(any())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.body(any(BodyInserters.FormInserter.class))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(AuthTokenDTO.class)).thenReturn(Mono.just(authTokenDTO));

        Either<UserError, AuthTokenDTO> result = spotifyApiGateway.getRefreshToken(code, state, redirectUri);

        assertTrue(result.isRight());
        assertEquals(authTokenDTO.getAccessToken(), result.get().getAccessToken());
    }

    @Test
    void givenInvalidRequest_whenGetRefreshToken_thenReturnError() {
        String code = "invalidCode";
        String state = "validState";
        String redirectUri = "http://redirect.uri";

        WebClient webClientMock = mock(WebClient.class);
        WebClient.RequestBodyUriSpec requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpecMock = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        when(clientBuilder.buildClient("/api/token", "auth")).thenReturn(webClientMock);
        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.contentType(any())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.body(any(BodyInserters.FormInserter.class))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(AuthTokenDTO.class)).thenReturn(Mono.just(new AuthTokenDTO()));

        var result = spotifyApiGateway.getRefreshToken(code, state, redirectUri);

        assertTrue(result.isLeft());
        assertEquals(UserError.GenericError.class, result.getLeft().getClass());
    }

    @Test
    void givenValidUserWithCachedTracks_whenGetTracks_returnTracks() {
        User user = UserObjectMother.createValidUser();
        ArrayList<TopTrackDTO> mockTracks = new ArrayList<>();
        var trackDTO = new TopTrackDTO();
        mockTracks.add(trackDTO);
        tracksCache.put(user.getUsername(), mockTracks);

        var result = spotifyApiGateway.getTopTracks(user, 10);

        assertTrue(result.isRight());
        assertEquals(mockTracks, result.get());

        verifyNoInteractions(musicBrainzApiGateway);
    }

    @Test
    void givenValidUserWithToken_whenGetTracks_returnTracks() {
        User user = UserObjectMother.createValidUser();
        ArrayList<TopTrackDTO> mockTracks = new ArrayList<>();
        var trackDTO = new TopTrackDTO();
        mockTracks.add(trackDTO);
        var tracksResponseDTO = DTOsObjectMother.createValidSpotifyTracksResponseDTO();
        var totalResponseDTO = DTOsObjectMother.createValidTotalObjectDTO();
        tokenCache.put(user.getUsername(), "cachedAccessToken");

        var topTrackDTO = DTOsObjectMother.createValidTopTrackDTO();

        WebClient webClientMock = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        when(clientBuilder.buildClient("/top/tracks?time_range=medium_term&limit=1&offset=0", "user_analytics")).thenReturn(webClientMock);
        when(webClientMock.get()).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(anyString(), anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(TopTracksSpotifyResponseDTO.class)).thenReturn(Mono.just(tracksResponseDTO));

        when(clientBuilder.buildClient("/tracks/id", "analytics")).thenReturn(webClientMock);
        when(webClientMock.get()).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(anyString(), anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(TotalObjectDTO.class)).thenReturn(Mono.just(totalResponseDTO));

        when(musicBrainzApiGateway.getArtistCountry(totalResponseDTO)).thenReturn(Either.right(topTrackDTO));

        Either<UserError, ArrayList<TopTrackDTO>> result = spotifyApiGateway.getTopTracks(user, 1);

        assertTrue(result.isRight());
        assertEquals(1, result.get().size());
        assertEquals(topTrackDTO, result.get().get(0));
    }

    @Test
    void givenValidUserWithoutToken_whenGetTracks_thenReturnTracks() {
        User user = UserObjectMother.createValidUser();
        ArrayList<TopTrackDTO> mockTracks = new ArrayList<>();
        var trackDTO = new TopTrackDTO();
        mockTracks.add(trackDTO);
        var tracksResponseDTO = DTOsObjectMother.createValidSpotifyTracksResponseDTO();
        var totalResponseDTO = DTOsObjectMother.createValidTotalObjectDTO();

        var topTrackDTO = DTOsObjectMother.createValidTopTrackDTO();
        var authTokenDTO = DTOsObjectMother.createValidAuthTokenDTO();

        WebClient webClientMock = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);
        WebClient.RequestBodyUriSpec requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpecMock = mock(WebClient.RequestBodySpec.class);

        when(clientBuilder.buildClient("/top/tracks?time_range=medium_term&limit=1&offset=0", "user_analytics")).thenReturn(webClientMock);
        when(webClientMock.get()).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(anyString(), anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(TopTracksSpotifyResponseDTO.class)).thenReturn(Mono.just(tracksResponseDTO));

        when(clientBuilder.buildClient("/api/token", "auth")).thenReturn(webClientMock);
        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.contentType(any())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.body(any(BodyInserters.FormInserter.class))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(AuthTokenDTO.class)).thenReturn(Mono.just(authTokenDTO));


        when(clientBuilder.buildClient("/tracks/id", "analytics")).thenReturn(webClientMock);
        when(webClientMock.get()).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(anyString(), anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(TotalObjectDTO.class)).thenReturn(Mono.just(totalResponseDTO));

        when(musicBrainzApiGateway.getArtistCountry(totalResponseDTO)).thenReturn(Either.right(topTrackDTO));

        Either<UserError, ArrayList<TopTrackDTO>> result = spotifyApiGateway.getTopTracks(user, 1);

        assertTrue(result.isRight());
        assertEquals(1, result.get().size());
        assertEquals(topTrackDTO, result.get().get(0));
    }
}
