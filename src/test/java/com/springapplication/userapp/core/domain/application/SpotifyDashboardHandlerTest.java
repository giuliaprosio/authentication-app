package com.springapplication.userapp.core.domain.application;

import com.springapplication.userapp.configuration.security.CustomAuthenticationSuccessHandler;
import com.springapplication.userapp.core.adapters.database.UserRepository;
import com.springapplication.userapp.core.domain.model.UserObjectMother;
import com.springapplication.userapp.core.domain.model.error.UserError;
import com.springapplication.userapp.core.domain.port.output.SpotifyGateway;
import com.springapplication.userapp.core.domain.port.output.UserPersistence;
import com.springapplication.userapp.controller.model.TopTrackDTO;
import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpotifyDashboardHandlerTest {

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
    private SpotifyDashboardHandler dashboardHandler;

    @Test
    void givenValidUsername_whenHandleSpotifyData_thenReturnListTopTrack() {
        TopTrackDTO dto = new TopTrackDTO();
        dto.setName("name");
        dto.setCountry("country");
        dto.setArtist("artist");
        var list = new ArrayList<TopTrackDTO>();
        list.add(dto);
        var user = UserObjectMother.createValidUser();

        when(userPersistence.findByUsername(user.getUsername()))
                .thenReturn(Either.right(Optional.of(user)));
        when(spotifyGateway.getTopTracks(user, 10))
                .thenReturn(Either.right(list));

        var result = dashboardHandler.handleSpotifyData(user.getUsername());

        assertTrue(result.isRight());
        assertEquals(list, result.get());
    }

    @Test
    void givenInvalidUsername_whenHandleSpotifyData_thenReturnError() {
        String username = "username";
        var error = new UserError.GenericError("No user found for request");

        when(userPersistence.findByUsername(username))
                .thenReturn(Either.right(Optional.empty()));

        var result = dashboardHandler.handleSpotifyData(username);

        assertTrue(result.isLeft());
        assertEquals(error, result.getLeft());
    }

}
