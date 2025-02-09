package com.springapplication.userapp.core.domain.application;

import com.springapplication.userapp.controller.model.TopTrackDTO;
import com.springapplication.userapp.client.model.AuthTokenDTO;
import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.UserError;
import com.springapplication.userapp.core.domain.port.input.UserAuthorizationHandler;
import com.springapplication.userapp.core.domain.port.output.SpotifyGateway;
import com.springapplication.userapp.core.domain.port.output.UserPersistence;
import com.springapplication.userapp.providers.logging.Logger;
import com.springapplication.userapp.providers.logging.LoggerFactory;
import io.vavr.control.Either;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Specific handler for user authorization for our app to operate on behalf of them
 * for the Spotify API
 */
@Component
public class SpotifyUserAuthorizationHandler implements UserAuthorizationHandler {

    private final UserPersistence userPersistence;
    private final SpotifyGateway spotifyGateway;

    private final Logger logger = LoggerFactory.getLogger(SpotifyUserAuthorizationHandler.class);

    public SpotifyUserAuthorizationHandler(UserPersistence userPersistence, SpotifyGateway spotifyGateway) {
        this.userPersistence = userPersistence;
        this.spotifyGateway = spotifyGateway;
    }

    @Override
    public Either<UserError, Boolean> handleAuthorization(String username) {
        logger.info("Searching if user already authorized to operate on their behalf");
        return userPersistence.findByUsername(username)
                .flatMap(this::isAlreadyAuthorized);
    }

    @Override
    public Either<UserError, User> handleAuthorization(String code, String state, String redirect_uri) {

        // create a record for this object
        var maybeRefreshToken = spotifyGateway.getRefreshToken(code, state, redirect_uri);
        if(maybeRefreshToken.isLeft()) return Either.left(maybeRefreshToken.getLeft());

        return userPersistence.findByUsername(state)
                .flatMap(this::maybeUser)
                .flatMap(oldUser -> mergeUserInfo(oldUser, maybeRefreshToken.get()))
                .flatMap(updatedUser -> {
                    var result= userPersistence.update(updatedUser);
                    if(result.isLeft()) return Either.left(wrapThrowable(result.getLeft()));
                    return Either.right(result.get());
                });
    }

    @Override
    public Either<UserError, TopTrackDTO> handleSpotifyData(String username){
        return userPersistence.findByUsername(username)
                .flatMap(user -> {
                    if(user.isEmpty()) return Either.left(new UserError.GenericError("No user found for request"));
                    return spotifyGateway.getTopTrack(user.get());
                });
    }

    private Either<UserError, Boolean> isAlreadyAuthorized(Optional<User> user){
        if(user.isEmpty()) {
            logger.error("User not in the system");
            return Either.left(new UserError.GenericError("User is not in the system"));
        }
        if(user.get().getRefreshToken() == null){
            logger.info("User did not authorize third party operations yet");
            return Either.right(false);
        }
        return Either.right(true);
    }

    private UserError wrapThrowable(Throwable throwable){
        logger.error("DB error: " + throwable);
        return new UserError.GenericError("Database error: " + throwable);
    }

    private Either<UserError, User> maybeUser(Optional<User> user){
        if(user.isEmpty()){
            logger.info("No user in the system");
            return Either.left(new UserError.GenericError("No user in the system"));
        }
        return Either.right(user.get());
    }

    private Either<UserError, User> mergeUserInfo(User user, AuthTokenDTO dto){
        var updatedUser = new User();
        updatedUser.setUsername(user.getUsername());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setPassword(user.getPassword());
        updatedUser.setId(user.getId());
        updatedUser.setRefreshToken(dto.getRefreshToken());
        updatedUser.setAccessToken(dto.getAccessToken());
        logger.info("Updated user: " + updatedUser.getUsername());
        return Either.right(updatedUser);
    }
}
