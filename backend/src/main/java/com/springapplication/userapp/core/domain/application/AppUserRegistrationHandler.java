package com.springapplication.userapp.core.domain.application;

import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.UserError;
import com.springapplication.userapp.core.domain.port.input.UserRegistrationHandler;
import com.springapplication.userapp.core.domain.port.output.UserPersistence;
import com.springapplication.userapp.providers.logging.Logger;
import com.springapplication.userapp.providers.logging.LoggerFactory;
import io.vavr.control.Either;
import org.springframework.stereotype.Component;

@Component
public class AppUserRegistrationHandler implements UserRegistrationHandler {

    private final Logger logger = LoggerFactory.getLogger(AppUserRegistrationHandler.class);
    private final UserPersistence userPersistence;

    public AppUserRegistrationHandler(UserPersistence userPersistence) {
        this.userPersistence = userPersistence;
    }

    @Override
    public Either<UserError, User> handleUserRegistration(User user) {

        logger.info("Registering new user" + user);
        return userPersistence.save(user)
                .mapLeft(this::mapThrowable);
    }

    private UserError mapThrowable(Throwable throwable) {
        return switch (throwable.getCause()) {
            case java.sql.SQLIntegrityConstraintViolationException __ -> new UserError.AlreadyInSystem();
            case Exception ex -> new UserError.GenericError(ex.getMessage());
            default -> new UserError.GenericError("Internal problem");
        };
    }

}
