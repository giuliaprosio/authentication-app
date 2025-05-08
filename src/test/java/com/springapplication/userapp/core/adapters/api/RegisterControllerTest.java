package com.springapplication.userapp.core.adapters.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springapplication.userapp.configuration.security.CustomAuthenticationSuccessHandler;
import com.springapplication.userapp.configuration.security.ForwardingAuthenticationEntryPoint;
import com.springapplication.userapp.core.adapters.database.UserRepository;
import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.error.UserError;
import com.springapplication.userapp.core.domain.model.RegisterRequestObjectMother;
import com.springapplication.userapp.core.domain.port.input.UserRegistrationHandler;
import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import com.springapplication.userapp.controller.model.NewUserDTO;
import com.springapplication.userapp.controller.api.RegisterApiController;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {RegisterApiController.class})
@Import(RegisterController.class)
public class RegisterControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ForwardingAuthenticationEntryPoint forwardingAuthenticationEntryPoint;

    @MockBean
    RegisterValidator registerValidator;

    @MockBean
    RegisterMapper registerMapper;

    @MockBean
    UserRegistrationHandler registrationHandler;

    /**
     * Mock Beans to configure correctly the starting of the application
     */
    @MockBean
    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @MockBean
    UserRepository userRepository;
    /**
     *
     */

    private static final String ENDPOINT = "/api/register";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void GetRegister_ReturnsRegister() throws Exception {

        MvcResult response = this.mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().string("register"))
                .andReturn();


    }

    @Test
    public void givenValidData_whenRegister_thenReturnsAdded() throws Exception {

        var registerRequest = RegisterRequestObjectMother.makeValidUserDTO();
        var userMapped = RegisterRequestObjectMother.makeValidUserFromDTO(registerRequest);

        when(registerValidator.validation(any(NewUserDTO.class)))
                .thenReturn(Either.right(registerRequest));
        when(registerMapper.mapper(any(NewUserDTO.class)))
                .thenReturn(Either.right(userMapped));
        when(registrationHandler.handleUserRegistration(any(User.class)))
                .thenReturn(Either.right(userMapped));

        String requestBody = objectMapper.writeValueAsString(registerRequest);
        ResultActions result = this.mockMvc
                .perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        result.andExpect(status().isOk())
              .andExpect(content().string("added"));

    }

    @Test
    public void givenNoUsername_whenRegister_thenReturnUsernameError() throws Exception {
        var registerRequest = RegisterRequestObjectMother.makeValidUserDTO();
        registerRequest.setUsername("");
        var response = new UserError.NoUsername();

        when(registerValidator.validation(any(NewUserDTO.class))).thenReturn(Either.left(response));

        String requestBody = objectMapper.writeValueAsString(registerRequest);
        ResultActions result = this.mockMvc
                .perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        result.andExpect(status().isOk())
                .andExpect(content().string("Username required."));
    }

    @Test
    public void givenNoEmail_whenRegister_thenReturnEmailError() throws Exception {
        var registerRequest = RegisterRequestObjectMother.makeValidUserDTO();
        registerRequest.setEmail("");
        var response = new UserError.NoEmail();

        when(registerValidator.validation(any(NewUserDTO.class))).thenReturn(Either.left(response));

        String requestBody = objectMapper.writeValueAsString(registerRequest);
        ResultActions result = this.mockMvc
                .perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        result.andExpect(status().isOk())
                .andExpect(content().string("Email required."));
    }

    @Test
    public void givenNoPassword_whenRegister_thenReturnPasswordError() throws Exception {
        var registerRequest = RegisterRequestObjectMother.makeValidUserDTO();
        registerRequest.setPassword("");
        var response = new UserError.NoPassword();

        when(registerValidator.validation(any(NewUserDTO.class))).thenReturn(Either.left(response));

        String requestBody = objectMapper.writeValueAsString(registerRequest);
        ResultActions result = this.mockMvc
                .perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        result.andExpect(status().isOk())
                .andExpect(content().string("Password required."));
    }

    @Test
    public void givenNoSecondPassword_whenRegister_thenReturnSecondPasswordError() throws Exception {
        var registerRequest = RegisterRequestObjectMother.makeValidUserDTO();
        registerRequest.setSecondPassword("");
        var response = new UserError.NoSecondPassword();

        when(registerValidator.validation(any(NewUserDTO.class))).thenReturn(Either.left(response));

        String requestBody = objectMapper.writeValueAsString(registerRequest);
        ResultActions result = this.mockMvc
                .perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        result.andExpect(status().isOk())
                .andExpect(content().string("Second password check required."));
    }

    @Test
    public void givenNotMatchingPasswords_whenRegister_thenReturnNoMatchingError() throws Exception {
        var registerRequest = RegisterRequestObjectMother.makeValidUserDTO();
        var response = new UserError.SecondPasswordNoMatch();

        when(registerValidator.validation(any(NewUserDTO.class))).thenReturn(Either.left(response));

        String requestBody = objectMapper.writeValueAsString(registerRequest);
        ResultActions result = this.mockMvc
                .perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        result.andExpect(status().isOk())
                .andExpect(content().string("The passwords are not matching."));
    }

    @Test
    public void givenAlreadyUsedUsername_whenRegister_thenReturnsAlreadyInSystem() throws Exception {

        var registerRequest = RegisterRequestObjectMother.makeValidUserDTO();
        var userMapped = RegisterRequestObjectMother.makeValidUserFromDTO(registerRequest);
        var error = new UserError.AlreadyInSystem();

        when(registerValidator.validation(any(NewUserDTO.class)))
                .thenReturn(Either.right(registerRequest));
        when(registerMapper.mapper(any(NewUserDTO.class)))
                .thenReturn(Either.right(userMapped));
        when(registrationHandler.handleUserRegistration(userMapped))
                .thenReturn(Either.left(error));

        String requestBody = objectMapper.writeValueAsString(registerRequest);
        ResultActions result = this.mockMvc
                .perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        result.andExpect(status().isOk())
                .andExpect(content().string("Username or email already in system"));

    }

}
