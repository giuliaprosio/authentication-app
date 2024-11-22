package com.springapplication.userapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springapplication.userapp.config.CustomAuthenticationSuccessHandler;
import com.springapplication.userapp.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import com.springapplication.userapp.controller.api.LoginApiController;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {LoginApiController.class})
@Import(LoginController.class)
public class LoginControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @MockBean
    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    private static final String ENDPOINT = "/api/login";

    @Test
    public void GetLogin_ReturnsLogin() throws Exception {

        MvcResult result = this.mockMvc
                .perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().string("login"))
                .andReturn();
    }

}
