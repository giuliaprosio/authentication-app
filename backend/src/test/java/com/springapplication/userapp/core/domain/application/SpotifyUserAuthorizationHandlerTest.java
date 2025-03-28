package com.springapplication.userapp.core.domain.application;

import com.springapplication.userapp.configuration.security.CustomAuthenticationSuccessHandler;
import com.springapplication.userapp.core.adapters.database.UserRepository;
import com.springapplication.userapp.core.domain.model.error.AdaptersError;
import com.springapplication.userapp.core.domain.model.error.UserError;
import com.springapplication.userapp.core.domain.model.UserObjectMother;
import com.springapplication.userapp.core.domain.port.output.SpotifyGateway;
import com.springapplication.userapp.core.domain.port.output.UserPersistence;
import com.springapplication.userapp.providers.encryption.CryptoUtils;
import com.springapplication.userapp.providers.encryption.EncryptionError;
import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.springapplication.userapp.client.model.AuthTokenDTO;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpotifyUserAuthorizationHandlerTest {

    @Mock
    private UserPersistence userPersistence;

    @Mock
    private SpotifyGateway spotifyGateway;

    @Mock
    private CryptoUtils cryptoUtils;

    /**
     * Mocks for the SpringBoot startup
     */
    @MockBean
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @MockBean
    private UserRepository userRepository;
    /**
     *
     */

    @InjectMocks
    private SpotifyUserAuthorizationHandler spotifyUserAuthorizationHandler;

    @Test
    void givenValidUserAlreadyAuthorized_whenHandleSpotifyAuthorization_thenReturnVoid() {
        var user = UserObjectMother.createValidUser();

        when(userPersistence.findByUsername(user.getUsername()))
                .thenReturn(Either.right(Optional.of(user)));

        var result = spotifyUserAuthorizationHandler.handleAuthorization(user.getUsername());
        verifyNoMoreInteractions(userPersistence);
        assertTrue(result.isRight());
        assertTrue(result.get());
    }

    @Test
    void givenNoSavedUser_whenHandleSpotifyAuthorization_thenReturnError(){
        var user = UserObjectMother.createValidUser();
        var error = new AdaptersError.DatabaseError("User is not in the system", Optional.empty());

        when(userPersistence.findByUsername(user.getUsername())).thenReturn(Either.left(error));

        var result = spotifyUserAuthorizationHandler.handleAuthorization(user.getUsername());

        verifyNoMoreInteractions(userPersistence);
        verifyNoInteractions(spotifyGateway);
        assertTrue(result.isLeft());
    }

    @Test
    void givenValidNonAuthorizedUser_whenHandleSpotifyAuthorization_thenUpdate() {
        var user = UserObjectMother.createValidUnauthorizedUser();

        when(userPersistence.findByUsername(user.getUsername())).thenReturn(Either.right(Optional.of(user)));

        var result = spotifyUserAuthorizationHandler.handleAuthorization(user.getUsername());
        verifyNoMoreInteractions(userPersistence);
        assertTrue(result.isRight());
        assertFalse(result.get());
    }

    @Test
    void givenValidCodeAndState_whenHandleSpotifyAuthorization_thenUpdateUser() {
        String code = "code";
        String state = "state";
        String redirect_uri = "redirect_uri";
        var user = UserObjectMother.createValidUnauthorizedUser();
        var authTokenDTO = new AuthTokenDTO();
        String refreshToken = "refresh_token";
        authTokenDTO.setRefreshToken(refreshToken);

        when(spotifyGateway.getRefreshToken(code, state, redirect_uri))
                .thenReturn(Either.right(authTokenDTO));
        when(cryptoUtils.decrypt(state)).thenReturn(Either.right(user.getUsername()));
        when(userPersistence.findByUsername(user.getUsername())).thenReturn(Either.right(Optional.of(user)));

        user.setRefreshToken(refreshToken);

        when(userPersistence.update(any())).thenReturn(Either.right(user));

        var result = spotifyUserAuthorizationHandler.handleAuthorization(code, state, redirect_uri);

        assertTrue(result.isRight());
        assertEquals(user, result.get());
    }

    @Test
    void givenSpotifyGatewayError_whenHandleSpotifyAuthorization_thenReturnError() {
        String code = "code";
        String state = "state";
        String redirect_uri = "redirect_uri";

        var error = new UserError.GenericError("Error calling Spotify API");

        when(spotifyGateway.getRefreshToken(code, state, redirect_uri)).thenReturn(Either.left(error));

        var result = spotifyUserAuthorizationHandler.handleAuthorization(code, state, redirect_uri);

        assertTrue(result.isLeft());
        verifyNoInteractions(userPersistence);
        verifyNoInteractions(cryptoUtils);
        assertEquals(error, result.getLeft());
    }

    @Test
    void givenCryptoError_whenHandleSpotifyAuthorization_thenReturnError() {
        String code = "code";
        String state = "state";
        String redirect_uri = "redirect_uri";
        var authTokenDTO = new AuthTokenDTO();
        String refreshToken = "refresh_token";
        authTokenDTO.setRefreshToken(refreshToken);

        var error = new EncryptionError("Encryption error");

        when(spotifyGateway.getRefreshToken(code, state, redirect_uri))
                .thenReturn(Either.right(authTokenDTO));
        when(cryptoUtils.decrypt(state)).thenReturn(Either.left(error));

        var result = spotifyUserAuthorizationHandler.handleAuthorization(code, state, redirect_uri);

        assertTrue(result.isLeft());
        verifyNoInteractions(userPersistence);
        assertEquals(UserError.GenericError.class, result.getLeft().getClass());
    }

    @Test
    void givenPersistenceError_whenHandleSpotifyAuthorization_thenReturnError() {
        String code = "code";
        String state = "state";
        String redirect_uri = "redirect_uri";
        var user = UserObjectMother.createValidUnauthorizedUser();
        var authTokenDTO = new AuthTokenDTO();
        String refreshToken = "refresh_token";
        authTokenDTO.setRefreshToken(refreshToken);

        var error = new AdaptersError.DatabaseError("", Optional.empty());

        when(spotifyGateway.getRefreshToken(code, state, redirect_uri))
                .thenReturn(Either.right(authTokenDTO));
        when(cryptoUtils.decrypt(state)).thenReturn(Either.right(user.getUsername()));
        when(userPersistence.findByUsername(user.getUsername())).thenReturn(Either.left(error));

        var result = spotifyUserAuthorizationHandler.handleAuthorization(code, state, redirect_uri);

        assertTrue(result.isLeft());
        assertEquals(AdaptersError.DatabaseError.class, result.getLeft().getClass());
        verifyNoMoreInteractions(userPersistence);
    }
}
