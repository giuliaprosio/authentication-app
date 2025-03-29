package com.springapplication.userapp.core.domain.application;

import com.springapplication.userapp.client.model.AuthTokenDTO;
import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.error.UserError;
import com.springapplication.userapp.core.domain.port.input.UserAuthorizationHandler;
import com.springapplication.userapp.core.domain.port.output.SpotifyGateway;
import com.springapplication.userapp.core.domain.port.output.UserPersistence;
import com.springapplication.userapp.providers.encryption.CryptoUtils;
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
class SpotifyUserAuthorizationHandler implements UserAuthorizationHandler {

    private final UserPersistence userPersistence;
    private final SpotifyGateway spotifyGateway;
    private final CryptoUtils cryptoUtils;

    private final Logger logger = LoggerFactory.getLogger(SpotifyUserAuthorizationHandler.class);

    public SpotifyUserAuthorizationHandler(UserPersistence userPersistence, SpotifyGateway spotifyGateway, CryptoUtils cryptoUtils) {
        this.userPersistence = userPersistence;
        this.spotifyGateway = spotifyGateway;
        this.cryptoUtils = cryptoUtils;
    }

    @Override
    public Either<UserError, Boolean> handleAuthorization(String username) {
        logger.info("Searching if user already authorized to operate on their behalf");
        Either<UserError, Optional<User>> byUsername = Either.narrow(userPersistence.findByUsername(username));

        return isAlreadyAuthorized(byUsername);
    }

    @Override
    public Either<UserError, User> handleAuthorization(String code, String state, String redirect_uri) {

        // create a record for this object
        var maybeRefreshToken = spotifyGateway.getRefreshToken(code, state, redirect_uri);
        if(maybeRefreshToken.isLeft()) return Either.left(maybeRefreshToken.getLeft());
        var checkState = cryptoUtils.decrypt(state);
        if(checkState.isLeft()) return Either.left(new UserError.GenericError(checkState.getLeft().message()));
        var username = checkState.get();

        Either<UserError, Optional<User>> byUsername = Either.narrow(userPersistence.findByUsername(username));

        return maybeUser(byUsername)
                .flatMap(oldUser -> mergeUserInfo(oldUser, maybeRefreshToken.get()))
                .flatMap(updatedUser -> {
                    return Either.narrow(userPersistence.update(updatedUser));
                });
    }

    private Either<UserError, Boolean> isAlreadyAuthorized( Either<UserError, Optional<User>> user){
        if(user.isLeft()) return Either.left(user.getLeft());

        if(user.get().isEmpty()) {
            logger.error("User not in the system");
            return Either.left(new UserError.GenericError("User is not in the system"));
        }
        if(user.get().get().getRefreshToken() == null){
            logger.info("User did not authorize third party operations yet");
            return Either.right(false);
        }
        return Either.right(true);
    }

    private Either<UserError, User> maybeUser( Either<UserError, Optional<User>> maybeUser){
        if(maybeUser.isLeft()) return Either.left(maybeUser.getLeft());
        if(maybeUser.get().isEmpty()){
            logger.info("No user in the system");
            return Either.left(new UserError.GenericError("No user in the system"));
        }
        return Either.right(maybeUser.get().get());
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
