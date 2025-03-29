package com.springapplication.userapp.core.adapters.api;

import com.springapplication.userapp.configuration.security.CustomAuthenticationSuccessHandler;
import com.springapplication.userapp.core.adapters.database.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import com.springapplication.userapp.controller.api.LoginApiController;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {LoginApiController.class})
@Import(LoginController.class)
public class LoginControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

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

    private static final String ENDPOINT = "/api/login";

    @Test
    public void GetLogin_ReturnsLogin() throws Exception {

        this.mockMvc
                .perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().string("login"))
                .andReturn();
    }

}
