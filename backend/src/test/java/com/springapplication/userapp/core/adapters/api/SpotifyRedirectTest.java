package com.springapplication.userapp.core.adapters.api;

import com.springapplication.userapp.core.domain.model.error.UserError;
import com.springapplication.userapp.providers.encryption.CryptoUtils;
import com.springapplication.userapp.providers.encryption.EncryptionError;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpotifyRedirectTest {

    @Mock
    private CryptoUtils cryptoUtils;

    private String client_id;
    private String redirect_uri;
    private String scope;

    private SpotifyRedirect spotifyRedirect;

    @BeforeEach
    void setup(){
        client_id = randomUUID().toString();
        redirect_uri = randomUUID().toString();
        scope = randomUUID().toString();

        spotifyRedirect = new SpotifyRedirect(client_id, redirect_uri, scope, cryptoUtils);

    }

    @Test
    void givenValidRequest_whenSpotifyRedirect_returnRedirect(){
        String state = randomUUID().toString();
        String encState = randomUUID().toString();

        when(cryptoUtils.encrypt(state)).thenReturn(Either.right(encState));

        var result = spotifyRedirect.redirect(state);

        String expectedRedirectURL = String.format("https://accounts.spotify.com/authorize?client_id=%s&response_type=code&redirect_uri=%s&state=%s&scope=", client_id, redirect_uri, encState);


        assertTrue(result.isRight());
        var rightResult = result.get();

        assertEquals(expectedRedirectURL, rightResult.substring(0, expectedRedirectURL.length()));
    }

    @Test
    void givenEncryptionError_whenSpotifyRedirect_returnError(){
        String state = randomUUID().toString();
        var error = new EncryptionError("Encryption Error");

        when(cryptoUtils.encrypt(state)).thenReturn(Either.left(error));

        var result = spotifyRedirect.redirect(state);

        assertTrue(result.isLeft());
        assertEquals(UserError.GenericError.class, result.getLeft().getClass());
    }

    @Test
    void givenEncodingScopeError_whenSpotifyRedirect_returnGenericError(){
        spotifyRedirect = new SpotifyRedirect(client_id, redirect_uri, null, cryptoUtils);

        String state = randomUUID().toString();

        when(cryptoUtils.encrypt(state)).thenReturn(Either.right(null));

        var result = spotifyRedirect.redirect(state);

        assertTrue(result.isLeft());
        assertEquals(UserError.GenericError.class, result.getLeft().getClass());

    }
}
