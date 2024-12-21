package com.springapplication.userapp.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ClientBuilder {

    public WebClient buildClient(String url) {
        return WebClient.builder()
                .baseUrl(url)
                .build();
    }

}
