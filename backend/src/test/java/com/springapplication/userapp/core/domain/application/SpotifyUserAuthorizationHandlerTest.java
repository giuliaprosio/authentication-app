package com.springapplication.userapp.core.domain.application;

import com.springapplication.userapp.configuration.security.CustomAuthenticationSuccessHandler;
import com.springapplication.userapp.core.adapters.database.UserRepository;
import com.springapplication.userapp.core.domain.model.DTOsObjectMother;
import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.UserError;
import com.springapplication.userapp.core.domain.model.UserObjectMother;
import com.springapplication.userapp.core.domain.port.output.SpotifyGateway;
import com.springapplication.userapp.core.domain.port.output.UserPersistence;
import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpotifyUserAuthorizationHandlerTest {

    @Mock
    private UserPersistence userPersistence;

    @Mock
    private SpotifyGateway spotifyGateway;

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
    public void givenValidUserAlreadyAuthorized_whenHandleSpotifyAuthorization_thenReturnVoid() {
        var user = UserObjectMother.createValidUser();

        when(userPersistence.findByUsername(user.getUsername()))
                .thenReturn(Either.right(Optional.of(user)));

        var result = spotifyUserAuthorizationHandler.handleAuthorization(user.getUsername());
        verifyNoMoreInteractions(userPersistence);
        assertTrue(result.isRight());
    }

    @Test
    public void givenNoSavedUser_whenHandleSpotifyAuthorization_thenReturnError(){
        var user = UserObjectMother.createValidUser();
        var error = new UserError.GenericError("User is not in the system");

        when(userPersistence.findByUsername(user.getUsername())).thenReturn(Either.left(error));

        var result = spotifyUserAuthorizationHandler.handleAuthorization(user.getUsername());

        verifyNoMoreInteractions(userPersistence);
        verifyNoInteractions(spotifyGateway);
        assertTrue(result.isLeft());
    }
}
