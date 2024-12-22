package com.springapplication.userapp.controller;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockJwtAuth;
import com.springapplication.userapp.client.SpotifyClient;
import com.springapplication.userapp.config.CustomAuthenticationSuccessHandler;
import com.springapplication.userapp.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(controllers = {com.springapplication.userapp.controller.api.HomeApiController.class, HomeController.class})
@Import(HomeController.class)
public class HomeControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @MockBean
    private SpotifyClient spotifyClient;

    private static final String ENDPOINT = "/api/home";
    private static final String AUTH_ENDPOINT = "/api/home/connect";

    @Test
    @WithMockJwtAuth
    public void getHome_returnHome() throws Exception {

        MvcResult result = this.mockMvc
                .perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().string("home"))
                .andReturn();
    }

    @Test
    public void getHomeUnauthorized_fails() throws Exception {

        MvcResult result = this.mockMvc
                .perform(get(ENDPOINT))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }
}
