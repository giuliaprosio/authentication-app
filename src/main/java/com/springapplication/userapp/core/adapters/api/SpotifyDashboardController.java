package com.springapplication.userapp.core.adapters.api;

import com.springapplication.userapp.controller.api.DashboardApiDelegate;
import com.springapplication.userapp.core.domain.model.UserError;
import com.springapplication.userapp.core.domain.port.input.DashboardHandler;
import com.springapplication.userapp.providers.logging.Logger;
import com.springapplication.userapp.providers.logging.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Controller
public class SpotifyDashboardController implements  DashboardApiDelegate {

    private final DashboardHandler handler;
    private static final Logger logger = LoggerFactory.getLogger(SpotifyDashboardController.class);

    public SpotifyDashboardController(DashboardHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/api/dashboard/spotify/data")
    public ResponseEntity<String> homeSpotify(){ return new ResponseEntity<>("dashboard/spotify/data", HttpStatus.OK); }


    @Override
    public ResponseEntity<Void> getSpotifyData(String username) {
        logger.info("Getting Spotify Data for user " + username);
        return handler.handleSpotifyData(username)
                .fold(this::mapErrorResponse, this::mapSuccessResponse);
    }

    private ResponseEntity<Void> mapErrorResponse(UserError error){
        return new ResponseEntity(error.toString(), HttpStatus.OK);
    }

    private ResponseEntity<Void> mapSuccessResponse(ArrayList<com.springapplication.userapp.controller.model.TopTrackDTO> topTracksDTO){
        return new ResponseEntity(topTracksDTO, HttpStatus.OK);
    }
}
