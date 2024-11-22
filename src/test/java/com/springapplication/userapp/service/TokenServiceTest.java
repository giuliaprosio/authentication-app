package com.springapplication.userapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TokenServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenCorrectAuth_whenGenerateToken_thenGenerateTokenWithCorrectClaims() {
        String expectedTokenValue = "dummy-jwt-token";
        Instant now = Instant.now();

        when(authentication.getName()).thenReturn("testUser");
        when(authentication.getAuthorities()).thenReturn(
                (Collection) List.of(
                (GrantedAuthority) () -> "ROLE_USER",
                (GrantedAuthority) () -> "ROLE_ADMIN"
        ));

        Jwt jwtMock = mock(Jwt.class);
        when(jwtMock.getTokenValue()).thenReturn(expectedTokenValue);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwtMock);

        ArgumentCaptor<JwtEncoderParameters> paramsCaptor = ArgumentCaptor.forClass(JwtEncoderParameters.class);

        String token = tokenService.generateToken(authentication);

        verify(jwtEncoder).encode(paramsCaptor.capture());
        JwtClaimsSet claimsSet = paramsCaptor.getValue().getClaims();

        assertEquals(expectedTokenValue, token);
        assertEquals("testUser", claimsSet.getSubject());
        assertEquals(now.truncatedTo(ChronoUnit.SECONDS), claimsSet.getIssuedAt().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(now.plus(1, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.SECONDS), claimsSet.getExpiresAt().truncatedTo(ChronoUnit.SECONDS));
        assertEquals("ROLE_USER ROLE_ADMIN", claimsSet.getClaim("scope"));
    }
}
