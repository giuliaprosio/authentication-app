package com.springapplication.userapp.client;

import com.springapplication.userapp.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import com.springapplication.userapp.controller.model.TopArtistSpotifyResponseDTO;

import java.util.Base64;

@Component
public class SpotifyClient {

    private final String client_id;
    private final String client_secret;
    private final ClientBuilder clientBuilder;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public SpotifyClient(@Value("${my.client.id}") String client_id,
                         @Value("${my.client.secret}") String client_secret,
                         ClientBuilder clientBuilder,
                         PasswordEncoder passwordEncoder,
                         UserRepository userRepository) {
        this.client_id = client_id;
        this.client_secret = client_secret;
        this.clientBuilder = clientBuilder;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public String spotifyAuthorizationClientBuilder(String code, String state, String redirect_uri) {
        WebClient spotifyClient = clientBuilder.buildClient("https://accounts.spotify.com");

        String original_input = client_id + ":" + client_secret;
        String authHeader = Base64.getEncoder().encodeToString(original_input.getBytes());

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirect_uri);

        var response = spotifyClient.post()
                .uri("/api/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(body))
                .header("Authorization", "Basic " + authHeader)
                .retrieve()
                .bodyToMono(AccessTokenResponseDTO.class)
                .block();

        var maybeUser = userRepository.findByUsername(state);

        maybeUser.setRefreshToken(response.getRefreshToken());
        maybeUser.setAccessToken(response.getAccessToken());

        return response.getAccessToken();
    }

    public String spotifyTopArtist(String accessToken) {
        WebClient spotifyClient = clientBuilder.buildClient("https://api.spotify.com/v1/me/top/tracks?time_range=medium_term&limit=1&offset=0");
        String authHeader = "Bearer " + accessToken;

        var response = spotifyClient.get()
                .header("Authorization", authHeader)
                .retrieve()
                .bodyToMono(TopArtistSpotifyResponseDTO.class)
                .block();

        return response.getItems().get(0).getName();
    }
}
