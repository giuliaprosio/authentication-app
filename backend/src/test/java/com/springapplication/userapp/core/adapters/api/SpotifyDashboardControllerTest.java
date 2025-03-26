package com.springapplication.userapp.core.adapters.api;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockJwtAuth;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springapplication.userapp.configuration.security.CustomAuthenticationSuccessHandler;
import com.springapplication.userapp.core.adapters.database.UserRepository;
import com.springapplication.userapp.core.domain.model.error.UserError;
import com.springapplication.userapp.core.domain.port.input.DashboardHandler;
import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(controllers = {com.springapplication.userapp.controller.api.DashboardApiController.class, SpotifyDashboardController.class})
@Import(SpotifyDashboardController.class)
public class SpotifyDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    DashboardHandler dashboardHandler;

    /**
     * Mock Beans to configure correctly the starting of the application
     */
    @MockBean
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @MockBean
    private UserRepository userRepository;
    /**
     *
     */

    private static final String ENDPOINT = "/api";

    private static final String SPOTIFY_ENDPOINT = ENDPOINT + "/dashboard/spotify/data";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockJwtAuth
    public void givenValidRequest_whenGetSpotifyData_returnData() throws Exception {
        String username = randomUUID().toString();
        com.springapplication.userapp.controller.model.TopTrackDTO trackDTO = new com.springapplication.userapp.controller.model.TopTrackDTO();
        trackDTO.setImg(randomUUID().toString());
        trackDTO.setName(randomUUID().toString());
        var list = new ArrayList<com.springapplication.userapp.controller.model.TopTrackDTO>();
        list.add(trackDTO);

        Resource fileResource = new ClassPathResource("username");
        assertNotNull(fileResource);
        MockMultipartFile file = new MockMultipartFile(
                "username",fileResource.getFilename(),
                MediaType.TEXT_PLAIN_VALUE,
                username.getBytes());

        when(dashboardHandler.handleSpotifyData(username)).thenReturn(Either.right(list));

        this.mockMvc.perform(multipart(SPOTIFY_ENDPOINT)
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(list)))
                .andReturn();
    }

    @Test
    @WithMockJwtAuth
    public void getDashboard_returnDashboard() throws Exception {

        this.mockMvc
                .perform(get(SPOTIFY_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().string("dashboard/spotify/data"))
                .andReturn();
    }

    @Test
    public void getDashboardUnauthorized_fails() throws Exception {
        this.mockMvc
                .perform(get(SPOTIFY_ENDPOINT))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockJwtAuth
    public void givenInvalidRequest_whenGetSpotifyData_returnError() throws Exception {
        String username = randomUUID().toString();
        var error = new UserError.GenericError("No user found");

        Resource fileResource = new ClassPathResource("username");
        assertNotNull(fileResource);
        MockMultipartFile file = new MockMultipartFile(
                "username",fileResource.getFilename(),
                MediaType.TEXT_PLAIN_VALUE,
                username.getBytes());

        when(dashboardHandler.handleSpotifyData(username)).thenReturn(Either.left(error));

        this.mockMvc.perform(multipart(SPOTIFY_ENDPOINT)
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string(error.error()))
                .andReturn();
    }


}
