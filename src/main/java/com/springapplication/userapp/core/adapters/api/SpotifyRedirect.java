package com.springapplication.userapp.core.adapters.api;

import com.springapplication.userapp.core.adapters.clients.StateCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
class SpotifyRedirect {

    private final String client_id;
    private final String response_type = "code";
    private final String redirect_uri;
    private final String scope = "user-read-playback-state user-read-currently-playing playlist-read-private playlist-read-collaborative user-top-read user-read-recently-played user-read-private";
    private final String encodedScope = URLEncoder.encode(scope,"UTF-8");
    private final StateCache stateCache;

    public SpotifyRedirect(
            @Value("${my.client.id}") String client_id,
            @Value("${my.redirect.uri}") String redirect_uri,
            StateCache stateCache) throws UnsupportedEncodingException {
        this.client_id = client_id;
        this.redirect_uri = redirect_uri;
        this.stateCache = stateCache;
    }

    public String redirect(String state) {
        String encryptedState = state; // TBD: how encrypted
        return String.format("https://accounts.spotify.com/authorize?client_id=%s&response_type=%s&redirect_uri=%s&state=%s&scope=%s", this.client_id, this.response_type, this.redirect_uri, encryptedState, this.encodedScope);
    }
}
