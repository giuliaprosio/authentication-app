package com.springapplication.userapp.core.domain.application;

import com.springapplication.userapp.configuration.security.CustomAuthenticationSuccessHandler;
import com.springapplication.userapp.core.adapters.database.UserRepository;
import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.UserError;
import com.springapplication.userapp.core.domain.model.UserObjectMother;
import com.springapplication.userapp.core.domain.port.output.UserPersistence;
import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppUserRegistrationHandlerTest {

    @Mock
    private UserPersistence userPersistence;

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
    private AppUserRegistrationHandler appUserRegistrationHandler;


    @Test
    public void givenValidUserToRegister_whenHandleRegistration_thenReturnUser(){
        User user = UserObjectMother.createValidUser();

        when(userPersistence.save(user)).thenReturn(Either.right(user));

        var result = appUserRegistrationHandler.handleUserRegistration(user);

        verify(userPersistence).save(user);
        assertTrue(result.isRight());
        assertEquals(user, result.get());
    }

    @Test
    public void givenExistingUser_whenHandleRegistration_thenReturnError(){
        User user = UserObjectMother.createValidUser();
        var cause = new java.sql.SQLIntegrityConstraintViolationException();
        when(userPersistence.save(user)).thenReturn(Either.left(new java.sql.SQLIntegrityConstraintViolationException(cause)));

        var result = appUserRegistrationHandler.handleUserRegistration(user);

        assertTrue(result.isLeft());
        assertEquals(UserError.AlreadyInSystem.class, result.getLeft().getClass());
    }

    @Test
    public void givenGenericError_whenHandleRegistration_returnError(){
        User user = UserObjectMother.createValidUser();

        var cause = new Exception();
        when(userPersistence.save(user)).thenReturn(Either.left(new Exception(cause)));

        var result = appUserRegistrationHandler.handleUserRegistration(user);

        assertTrue(result.isLeft());
        assertEquals(UserError.GenericError.class, result.getLeft().getClass());

    }




}
