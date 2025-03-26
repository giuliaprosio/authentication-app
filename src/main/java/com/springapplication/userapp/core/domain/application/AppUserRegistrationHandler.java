package com.springapplication.userapp.core.domain.application;

import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.error.AdaptersError;
import com.springapplication.userapp.core.domain.model.error.UserError;
import com.springapplication.userapp.core.domain.port.input.UserRegistrationHandler;
import com.springapplication.userapp.core.domain.port.output.UserPersistence;
import com.springapplication.userapp.providers.logging.Logger;
import com.springapplication.userapp.providers.logging.LoggerFactory;
import io.vavr.control.Either;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class AppUserRegistrationHandler implements UserRegistrationHandler {

    private final Logger logger = LoggerFactory.getLogger(AppUserRegistrationHandler.class);
    private final UserPersistence userPersistence;

    public AppUserRegistrationHandler(UserPersistence userPersistence) {
        this.userPersistence = userPersistence;
    }

    @Override
    public Either<UserError, User> handleUserRegistration(User user) {

        logger.info("Registering new user" + user);
        var result = Either.narrow(userPersistence.save(user));
        return result
                .mapLeft(this::mapError);
    }

    private UserError mapError(AdaptersError error) {
        return switch (error) {
            case AdaptersError.DatabaseError err -> mapThrowable(err.exception());
        };
    }

    private UserError mapThrowable(Optional<Throwable> throwable) {
        return throwable.<UserError>map(value -> switch (value) {
            case java.sql.SQLIntegrityConstraintViolationException __ -> new UserError.AlreadyInSystem();
            case org.springframework.dao.DuplicateKeyException __ -> new UserError.AlreadyInSystem();
            case Exception ex -> new UserError.GenericError(ex.getLocalizedMessage());
            default -> new UserError.GenericError("Internal Error");
        }).orElseGet(() -> new UserError.GenericError("Internal Error"));
    }

}
