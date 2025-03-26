package com.springapplication.userapp.core.domain.port.input;

import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.error.UserError;
import io.vavr.control.Either;

/**
 * Handler for the user registration
 */
public interface UserRegistrationHandler {

    Either<UserError, User> handleUserRegistration(User user);

}
