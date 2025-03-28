package com.springapplication.userapp.core.domain.application;

import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.controller.model.TopTrackDTO;
import com.springapplication.userapp.core.domain.model.error.UserError;
import com.springapplication.userapp.core.domain.port.input.DashboardHandler;
import com.springapplication.userapp.core.domain.port.output.SpotifyGateway;
import com.springapplication.userapp.core.domain.port.output.UserPersistence;
import io.vavr.control.Either;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
class SpotifyDashboardHandler implements DashboardHandler {

    private final UserPersistence userPersistence;
    private final SpotifyGateway spotifyGateway;

    public SpotifyDashboardHandler(UserPersistence userPersistence, SpotifyGateway spotifyGateway) {
        this.userPersistence = userPersistence;
        this.spotifyGateway = spotifyGateway;
    }

    @Override
    public Either<UserError, ArrayList<TopTrackDTO>> handleSpotifyData(String username){

        Either<UserError, Optional<User>> byUsername = Either.narrow(userPersistence.findByUsername(username));
        return byUsername
                .flatMap(user -> {
                    if(user.isEmpty()) return Either.left(new UserError.GenericError("No user found for request"));
                    return spotifyGateway.getTopTracks(user.get(), 10);
                });
    }
}
