package com.springapplication.userapp.core.domain.port.input;

import com.springapplication.userapp.controller.model.TopTrackDTO;
import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.UserError;
import io.vavr.control.Either;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Handler for the authorization for our app to operate on behalf of a user
 */
public interface UserAuthorizationHandler {

    Either<UserError, Boolean> handleAuthorization(String username);

    Either<UserError, User> handleAuthorization(String code, String state, String redirect_uri);

}
