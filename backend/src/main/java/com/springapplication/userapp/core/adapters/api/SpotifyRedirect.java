package com.springapplication.userapp.core.adapters.api;

import com.springapplication.userapp.core.domain.model.error.UserError;
import com.springapplication.userapp.providers.encryption.CryptoUtils;
import com.springapplication.userapp.providers.encryption.EncryptionError;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
class SpotifyRedirect {

    private final String client_id;
    private final String response_type = "code";
    private final String redirect_uri;
    private final String scope = "user-read-playback-state user-read-currently-playing playlist-read-private playlist-read-collaborative user-top-read user-read-recently-played user-read-private";
    private final CryptoUtils cryptoUtils;

    public SpotifyRedirect(
            @Value("${my.client.id}") String client_id,
            @Value("${my.redirect.uri}") String redirect_uri,
            CryptoUtils cryptoUtils) {
        this.client_id = client_id;
        this.redirect_uri = redirect_uri;
        this.cryptoUtils = cryptoUtils;
    }

    public Either<UserError, String> redirect(String state) {
        return cryptoUtils.encrypt(state)
                .mapLeft(this::wrapError)
                .flatMap(this::createURL);
    }

    private Either<UserError, String> createURL(String encryptedState){
        return encodeScope()
                .map(encScope -> String.format("https://accounts.spotify.com/authorize?client_id=%s&response_type=%s&redirect_uri=%s&state=%s&scope=%s", this.client_id, this.response_type, this.redirect_uri, encryptedState, encScope));
    }

    private Either<UserError, String> encodeScope(){
        try{
            var encoded = URLEncoder.encode(scope, StandardCharsets.UTF_8);
            return Either.right(encoded);
        } catch (Exception e){
            var error = new UserError.GenericError("Error encoding scope " + e);
            return Either.left(error);
        }
    }

    private UserError wrapError(EncryptionError e) {
        return new UserError.GenericError(e.message());
    }
}
