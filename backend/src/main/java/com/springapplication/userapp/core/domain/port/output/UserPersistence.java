package com.springapplication.userapp.core.domain.port.output;

import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.error.AdaptersError;
import io.vavr.control.Either;

import java.util.Optional;

public interface UserPersistence {

    Either<AdaptersError, Optional<User>> findByUsername(String username);

    Either<AdaptersError, Optional<User>> findByEmail(String email);

    Either<AdaptersError, User> save(User user);

    Either<AdaptersError, User> update(User user);

}
