package com.springapplication.userapp.core.adapters.clients;

import com.springapplication.userapp.core.domain.model.DTOsObjectMother;
import com.springapplication.userapp.core.domain.model.error.UserError;
import com.springapplication.userapp.providers.countryISO.CountryISOCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MusicBrainzApiGatewayTest {

    @Mock
    private ClientBuilder clientBuilder;

    @Mock
    private CountryISOCache countryISOCache;

    @InjectMocks
    private MusicBrainzApiGateway musicBrainzApiGateway;

    @Test
    void givenValidArtist_whenGetArtistCountry_thenReturnUpdatedEntity() {
        var totalDTO = DTOsObjectMother.createValidTotalObjectDTO();
        var trackDTO = DTOsObjectMother.createValidTrackDTO(totalDTO);
        var brainzDTO = DTOsObjectMother.createValidMusicBrainzDTO();

        WebClient webClientMock = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        when(clientBuilder.buildClient("/artist", "musicBrainz")).thenReturn(webClientMock);
        when(webClientMock.get()).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.uri(ArgumentMatchers.<Function<UriBuilder, URI>>any()))
                .thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(com.springapplication.userapp.client.model.MusicBrainzDTO.class)).thenReturn(Mono.just(brainzDTO));
        when(countryISOCache.getISOFromCountry("england")).thenReturn("GBR");

        var result = musicBrainzApiGateway.getArtistCountry(totalDTO);

        assertTrue(result.isRight());
        assertThat(result.get())
                .usingRecursiveComparison()
                .ignoringFields("country")
                .isEqualTo(trackDTO);
        assertEquals("GBR", result.get().getCountry());
    }

    @Test
    void givenNoCountry_whenGetArtistCountry_thenReturnError() {
        var totalDTO = DTOsObjectMother.createValidTotalObjectDTO();
        var trackDTO = DTOsObjectMother.createValidTrackDTO(totalDTO);
        var brainzDTO = DTOsObjectMother.createInvalidMusicBrainzDTO();
        var error = new UserError.GenericError("Error parsing Music Brainz");

        WebClient webClientMock = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        when(clientBuilder.buildClient("/artist", "musicBrainz")).thenReturn(webClientMock);
        when(webClientMock.get()).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.uri(ArgumentMatchers.<Function<UriBuilder, URI>>any()))
                .thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(com.springapplication.userapp.client.model.MusicBrainzDTO.class)).thenReturn(Mono.just(brainzDTO));

        var result = musicBrainzApiGateway.getArtistCountry(totalDTO);

        assertTrue(result.isLeft());
        assertEquals(error, result.getLeft());
        verifyNoInteractions(countryISOCache);
    }

    @Test
    void givenNoISOCountry_whenGetArtistCountry_thenReturnError() {
        var totalDTO = DTOsObjectMother.createValidTotalObjectDTO();
        var brainzDTO = DTOsObjectMother.createValidMusicBrainzDTO();
        var error = new UserError.GenericError("No ISO for country name: england");

        WebClient webClientMock = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        when(clientBuilder.buildClient("/artist", "musicBrainz")).thenReturn(webClientMock);
        when(webClientMock.get()).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.uri(ArgumentMatchers.<Function<UriBuilder, URI>>any()))
                .thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(com.springapplication.userapp.client.model.MusicBrainzDTO.class)).thenReturn(Mono.just(brainzDTO));
        when(countryISOCache.getISOFromCountry("england")).thenReturn(null);

        var result = musicBrainzApiGateway.getArtistCountry(totalDTO);

        assertTrue(result.isLeft());
        assertEquals(error, result.getLeft());
    }

    @Test
    void givenAreaNameIsNull_whenGetArtistCountry_thenReturnError() {
        var totalDTO = DTOsObjectMother.createValidTotalObjectDTO();
        var brainzDTO = DTOsObjectMother.createValidMusicBrainzDTO();

        // Force missing area name → triggers last null-check in getCountrySync
        brainzDTO.getArtists().get(0).getArea().setName(null);

        var expectedError = new UserError.GenericError("Error parsing Music Brainz");

        WebClient webClientMock = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        when(clientBuilder.buildClient("/artist", "musicBrainz")).thenReturn(webClientMock);
        when(webClientMock.get()).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.uri(any(Function.class))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(com.springapplication.userapp.client.model.MusicBrainzDTO.class)).thenReturn(Mono.just(brainzDTO));

        var result = musicBrainzApiGateway.getArtistCountry(totalDTO);

        assertTrue(result.isLeft());
        assertEquals(expectedError, result.getLeft());
        verifyNoInteractions(countryISOCache);
    }

}
