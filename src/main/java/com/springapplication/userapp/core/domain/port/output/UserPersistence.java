package com.springapplication.userapp.core.domain.port.output;

import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.UserError;
import io.vavr.control.Either;

import java.util.Optional;

public interface UserPersistence {

    Either<UserError, Optional<User>> findByUsername(String username);

    Either<UserError, Optional<User>> findByEmail(String email);

    Either<Throwable, User> save(User user);

    Either<Throwable, User> update(User user);

}
