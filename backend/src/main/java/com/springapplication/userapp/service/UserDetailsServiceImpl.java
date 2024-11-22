package com.springapplication.userapp.service;

import com.springapplication.userapp.model.User;
import com.springapplication.userapp.model.UserError;
import com.springapplication.userapp.repo.UserRepository;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public Either<UserError, User> registerUser(User user) {

        return getUserByUsername(user.getUsername())
                .flatMap(u -> getUserByEmail(user.getEmail()))
                .flatMap(u -> saveInRepo(user));
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    private Either<UserError, String> getUserByUsername(String username) {
        return userRepository.findByUsername(username) == null ? Either.right(username) : Either.left(new UserError.DuplicatedUsername());
    }

    private Either<UserError, String> getUserByEmail(String email) {
        return userRepository.findByEmail(email) == null ? Either.right(email) : Either.left(new UserError.EmailAlreadyInSystem());
    }

    private Either<UserError, User> saveInRepo(User user) {
        userRepository.save(user);
        return Either.right(user);
    }
}
