package com.springapplication.userapp.core.adapters.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.HashMap;

@Component
class ClientBuilder {

    private final HashMap<String, String> apiCallType = new HashMap<>();
    private final String header;

    ClientBuilder(@Value("${spotify.auth}") String authUrl,
                  @Value("${spotify.user.analytics}") String userAnalyticsUrl,
                  @Value("${spotify.analytics}") String analyticsUrl,
                  @Value("${my.client.id}") String client_id,
                  @Value("${my.client.secret}") String client_secret,
                  @Value("${music-brainz.base.url}") String musicBrainzUrl) {
        apiCallType.put("auth", authUrl);
        apiCallType.put("user_analytics", userAnalyticsUrl);
        apiCallType.put("analytics", analyticsUrl);
        apiCallType.put("musicBrainz", musicBrainzUrl);
        header = Base64.getEncoder().encodeToString(String.format(client_id + ":" + client_secret).getBytes());
    }

    public WebClient buildClient(String uri, String type) {
        if(apiCallType.containsKey(type) && type.equals("auth")){
            return WebClient.builder()
                    .baseUrl(apiCallType.get(type) + uri)
                    .defaultHeader("Authorization", "Basic " + header)
                    .build();
        } else if(apiCallType.containsKey(type) && type.equals("musicBrainz")){
            return WebClient.builder()
                    .baseUrl(apiCallType.get(type) + uri)
                    .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader(HttpHeaders.USER_AGENT, "MyMusicApp/1.0 giulia.prosio@gmail.com")
                    .build();

        } else if(apiCallType.containsKey(type)){
            return WebClient.builder()
                    .baseUrl(apiCallType.get(type) + uri)
                    .build();
        }
        return null;
    }

}
