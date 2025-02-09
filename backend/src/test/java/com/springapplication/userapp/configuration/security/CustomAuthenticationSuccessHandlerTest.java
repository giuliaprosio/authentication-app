package com.springapplication.userapp.configuration.security;

import com.springapplication.userapp.configuration.security.CustomAuthenticationSuccessHandler;
import com.springapplication.userapp.providers.token.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomAuthenticationSuccessHandlerTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    @Test
    public void givenSuccessCredentials_whenAuthenticate_thenReturnSuccess() throws ServletException, IOException {
        String token = "testToken";

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenService.generateToken(any(Authentication.class)))
                .thenReturn(token);

        authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals("Authentication successful", response.getContentAsString());
    }
}
