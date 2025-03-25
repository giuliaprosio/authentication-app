package com.springapplication.userapp.core.domain.application;

import com.springapplication.userapp.core.domain.model.UserError;
import com.springapplication.userapp.core.domain.port.input.DashboardHandler;
import com.springapplication.userapp.core.domain.port.output.SpotifyGateway;
import com.springapplication.userapp.core.domain.port.output.UserPersistence;
import io.vavr.control.Either;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
class SpotifyDashboardHandler implements DashboardHandler {

    private final UserPersistence userPersistence;
    private final SpotifyGateway spotifyGateway;

    public SpotifyDashboardHandler(UserPersistence userPersistence, SpotifyGateway spotifyGateway) {
        this.userPersistence = userPersistence;
        this.spotifyGateway = spotifyGateway;
    }

    @Override
    public Either<UserError, ArrayList<com.springapplication.userapp.controller.model.TopTrackDTO>> handleSpotifyData(String username){
        return userPersistence.findByUsername(username)
                .flatMap(user -> {
                    if(user.isEmpty()) return Either.left(new UserError.GenericError("No user found for request"));
                    return spotifyGateway.getTopTracks(user.get(), 10);
                });
    }


}
