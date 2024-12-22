package com.springapplication.userapp.controller;

import com.springapplication.userapp.client.SpotifyClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.springapplication.userapp.controller.api.HomeApiDelegate;
import com.springapplication.userapp.controller.model.TopTrackDTO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
@Controller
class HomeController implements HomeApiDelegate {

    private final String client_id;
    private final String response_type = "code";
    private final String redirect_uri = "http://localhost:5173/home";
    private final String scope = "user-read-playback-state user-read-currently-playing playlist-read-private playlist-read-collaborative user-top-read user-read-recently-played user-read-private";
    private final String encodedScope = URLEncoder.encode(scope,"UTF-8");
    private String REDIRECT_URL;
    private final SpotifyClient clientBuilder;

    public HomeController(@Value("${my.client.id}") String client_id, SpotifyClient clientBuilder) throws UnsupportedEncodingException {
        this.client_id = client_id;
        this.clientBuilder = clientBuilder;
    }

    @GetMapping("/api/home")
    @ResponseBody
    public ResponseEntity<String> home() {
        return new ResponseEntity<>("home", HttpStatus.OK);
    }


    /**
     * Request for the Spotify redirect so that a user can give permission to my app to get their data
     * @return the url
     */
    @Override
    public ResponseEntity<String> getSpotifyAuthorize(String username) {
        REDIRECT_URL = String.format("https://accounts.spotify.com/authorize?client_id=%s&response_type=%s&redirect_uri=%s&state=%s&scope=%s", this.client_id, this.response_type, this.redirect_uri, username, this.encodedScope);
        return new ResponseEntity<>(REDIRECT_URL, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TopTrackDTO> getAuthorizationCode(String code, String state) {
        String accessToken = clientBuilder.spotifyAuthorizationClientBuilder(code, state, redirect_uri);
        var topTrack = clientBuilder.spotifyTopArtist(accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(topTrack);
    }

}
