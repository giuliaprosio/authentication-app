/*package com.springapplication.userapp.configuration.service;

import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.UserError;
import com.springapplication.userapp.core.domain.port.output.UserPersistence;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserPersistence userPersistence;

    public Either<UserError, User> registerUser(User user) {

        return getUserByUsername(user.getUsername())
                .flatMap(u -> getUserByEmail(user.getEmail()))
                .flatMap(u -> saveInRepo(user));
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userPersistence.findByUsername(username);
    }

    private Either<UserError, String> getUserByUsername(String username) {
        return userPersistence.findByUsername(username) == null ? Either.right(username) : Either.left(new UserError.DuplicatedUsername());
    }

    private Either<UserError, String> getUserByEmail(String email) {
        return userPersistence.findByEmail(email) == null ? Either.right(email) : Either.left(new UserError.EmailAlreadyInSystem());
    }

    private Either<UserError, User> saveInRepo(User user) {
        userPersistence.save(user);
        return Either.right(user);
    }
}
*/