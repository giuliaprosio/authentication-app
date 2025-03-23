package com.springapplication.userapp.core.adapters.api;

import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.UserError;
import com.springapplication.userapp.core.domain.port.input.UserAuthorizationHandler;
import com.springapplication.userapp.providers.logging.Logger;
import com.springapplication.userapp.providers.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.springapplication.userapp.controller.api.HomeApiDelegate;
import com.springapplication.userapp.controller.model.TopTrackDTO;

import java.util.ArrayList;

@Component
@Controller
class HomeController implements HomeApiDelegate {

    private final UserAuthorizationHandler userAuthorizationHandler;
    private final String redirect_uri;
    private final SpotifyRedirect spotifyRedirect;

    private final Logger logger = LoggerFactory.getLogger(HomeController.class);

    public HomeController(@Value("${my.redirect.uri}") String redirect_uri,
                          SpotifyRedirect spotifyRedirect,
                          UserAuthorizationHandler userAuthorizationHandler) {
        this.redirect_uri = redirect_uri;
        this.spotifyRedirect = spotifyRedirect;
        this.userAuthorizationHandler = userAuthorizationHandler;
    }

    @GetMapping("/api/home")
    @ResponseBody
    public ResponseEntity<String> home() {return new ResponseEntity<>("home", HttpStatus.OK);}

    /**
     * Request for the Spotify redirect so that a user can give permission to my app to get their data
     * @return the url
     */
    @Override
    public ResponseEntity<Void> getSpotifyAuthorize(String username) {
        logger.info("Getting user authorization for user " + username);
        return userAuthorizationHandler.handleAuthorization(username)
                .fold(this::mapErrorResponse, success -> mapSuccessResponse(success, username));
    }

    /**
     * Request for the Spotify Refresh token to send back to the user info about their listening patterns
     * @param code  (optional)
     * @param state  (optional)
     * @return DTO representing the top track
     */
    @Override
    public ResponseEntity<Void> getAuthorizationCode(String code, String state) {
        return userAuthorizationHandler.handleAuthorization(code, state, redirect_uri)
                .fold(this::mapErrorResponse, this::mapSuccessResponse);
    }

    private ResponseEntity<Void> mapErrorResponse(UserError error){
        return new ResponseEntity(error.toString(), HttpStatus.OK);
    }

    private ResponseEntity<Void> mapSuccessResponse(Boolean isAuthorized, String username){
        if(!isAuthorized){
            return spotifyRedirect.redirect(username)
                    .fold(this::mapErrorResponse, url -> new ResponseEntity(url, HttpStatus.OK));
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private ResponseEntity<Void> mapSuccessResponse(User user){
        return new ResponseEntity(HttpStatus.OK);
    }
}
