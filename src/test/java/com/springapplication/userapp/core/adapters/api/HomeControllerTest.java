package com.springapplication.userapp.core.adapters.api;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockJwtAuth;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springapplication.userapp.configuration.security.CustomAuthenticationSuccessHandler;
import com.springapplication.userapp.core.adapters.database.UserRepository;
import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.UserError;
import com.springapplication.userapp.core.domain.model.UserObjectMother;
import com.springapplication.userapp.core.domain.port.input.UserAuthorizationHandler;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
    private UserAuthorizationHandler userAuthorizationHandler;

    @MockBean
    private SpotifyRedirect spotifyRedirect;

    private static final String REDIRECT_URL = "REDIRECT_URI";

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

    private static final String ENDPOINT = "/api/home";
    private static final String AUTH_ENDPOINT =  ENDPOINT +"/connect";
    private static final String REDIRECT_ENDPOINT = ENDPOINT + "/redirect";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockJwtAuth
    public void getHome_returnHome() throws Exception {

        this.mockMvc
                .perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().string("home"))
                .andReturn();
    }

    @Test
    public void getHomeUnauthorized_fails() throws Exception {

        this.mockMvc
                .perform(get(ENDPOINT))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockJwtAuth
    public void givenValidUsernameWithNoSpotifyAuth_whenHandle_thenReturnRedirectString() throws Exception {
        String username = "username";

        when(userAuthorizationHandler.handleAuthorization(username)).thenReturn(Either.right(false));
        when(spotifyRedirect.redirect(username)).thenReturn(Either.right(REDIRECT_URL));

        Resource fileResource = new ClassPathResource("username");

        assertNotNull(fileResource);

        MockMultipartFile firstFile = new MockMultipartFile(
                "username",fileResource.getFilename(),
                MediaType.TEXT_PLAIN_VALUE,
                "username".getBytes());

        this.mockMvc.perform(multipart(AUTH_ENDPOINT)
                .file(firstFile))
                .andExpect(status().isOk())
                .andExpect(content().string(REDIRECT_URL))
                .andReturn();
    }

    @Test
    @WithMockJwtAuth
    public void givenValidAuthUsername_whenHandle_thenReturnVoid() throws Exception {
        String username = "username";

        when(userAuthorizationHandler.handleAuthorization(username))
                .thenReturn(Either.right(true));

        Resource fileResource = new ClassPathResource("username");

        assertNotNull(fileResource);

        MockMultipartFile firstFile = new MockMultipartFile(
                "username",fileResource.getFilename(),
                MediaType.TEXT_PLAIN_VALUE,
                "username".getBytes());

        this.mockMvc.perform(multipart(AUTH_ENDPOINT)
                        .file(firstFile))
                .andExpect(status().isNoContent())
                .andReturn();

    }

    @Test
    @WithMockJwtAuth
    public void givenSpotifyRedirectParams_whenHandle_thenReturnTopTrackDto() throws Exception {
        String code = "code";
        String state = "username";
        User user = UserObjectMother.createValidUser();

        when(userAuthorizationHandler.handleAuthorization(eq(code), eq(state), anyString()))
                .thenReturn(Either.right(user));

        Resource fileResource = new ClassPathResource("code");
        assertNotNull(fileResource);
        MockMultipartFile firstFile = new MockMultipartFile(
                "code",fileResource.getFilename(),
                MediaType.TEXT_PLAIN_VALUE,
                "code".getBytes());

        Resource secondFileResource = new ClassPathResource("state");
        assertNotNull(secondFileResource);
        MockMultipartFile secondFile = new MockMultipartFile(
                "state",secondFileResource.getFilename(),
                MediaType.TEXT_PLAIN_VALUE,
                "username".getBytes());

        this.mockMvc.perform(multipart(REDIRECT_ENDPOINT)
                .file(firstFile)
                .file(secondFile))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithMockJwtAuth
    public void givenInvalidRequest_whenHandleAuthorization_returnError() throws Exception {

        String code = "code";
        String state = "username";
        UserError error = new UserError.GenericError("error");

        when(userAuthorizationHandler.handleAuthorization(eq(code), eq(state), anyString()))
                .thenReturn(Either.left(error));

        Resource fileResource = new ClassPathResource("code");
        assertNotNull(fileResource);
        MockMultipartFile firstFile = new MockMultipartFile(
                "code",fileResource.getFilename(),
                MediaType.TEXT_PLAIN_VALUE,
                "code".getBytes());

        Resource secondFileResource = new ClassPathResource("state");
        assertNotNull(secondFileResource);
        MockMultipartFile secondFile = new MockMultipartFile(
                "state",secondFileResource.getFilename(),
                MediaType.TEXT_PLAIN_VALUE,
                "username".getBytes());

        this.mockMvc.perform(multipart(REDIRECT_ENDPOINT)
                        .file(firstFile)
                        .file(secondFile))
                .andExpect(status().isOk())
                .andExpect(content().string(error.toString()))
                .andReturn();

    }
}
