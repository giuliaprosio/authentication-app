package com.springapplication.userapp.core.adapters.clients;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ClientBuilderTest {
    private ClientBuilder clientBuilder;

    private final String authUrl = "https://auth.spotify.com";
    private final String userAnalyticsUrl = "https://user.analytics.spotify.com";
    private final String analyticsUrl = "https://analytics.spotify.com";
    private final String musicBrainzUrl = "https://musicbrainz.org";
    private final String clientId = "testClient";
    private final String clientSecret = "testSecret";
    private final String email = "test@example.com";

    @BeforeEach
    void setUp() {
        clientBuilder = new ClientBuilder(
                authUrl,
                userAnalyticsUrl,
                analyticsUrl,
                clientId,
                clientSecret,
                email,
                musicBrainzUrl
        );
    }

    @Test
    void testAuthClientBuildsCorrectly() throws Exception {
        String uri = "/token";
        WebClient client = clientBuilder.buildClient(uri, "auth");
        assertNotNull(client);

        String expectedHeader = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes());

        Field headerField = ClientBuilder.class.getDeclaredField("header");
        headerField.setAccessible(true);
        String actualHeader = (String) headerField.get(clientBuilder);

        assertEquals(expectedHeader, actualHeader);
    }

    @Test
    void testMusicBrainzClientBuildsCorrectly() {
        String uri = "/artist";
        WebClient client = clientBuilder.buildClient(uri, "musicBrainz");
        assertNotNull(client);
    }

    @Test
    void testUserAnalyticsClientBuildsCorrectly() {
        String uri = "/data";
        WebClient client = clientBuilder.buildClient(uri, "user_analytics");
        assertNotNull(client);
    }

    @Test
    void testAnalyticsClientBuildsCorrectly() {
        String uri = "/reports";
        WebClient client = clientBuilder.buildClient(uri, "analytics");
        assertNotNull(client);
    }

    @Test
    void testInvalidTypeReturnsNull() {
        WebClient client = clientBuilder.buildClient("/test", "unknown_type");
        assertNull(client);
    }

    @Test
    void testApiCallTypeMapInitializedProperly() throws Exception {
        Field field = ClientBuilder.class.getDeclaredField("apiCallType");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        HashMap<String, String> apiCallType = (HashMap<String, String>) field.get(clientBuilder);

        assertEquals(authUrl, apiCallType.get("auth"));
        assertEquals(userAnalyticsUrl, apiCallType.get("user_analytics"));
        assertEquals(analyticsUrl, apiCallType.get("analytics"));
        assertEquals(musicBrainzUrl, apiCallType.get("musicBrainz"));
    }
}

