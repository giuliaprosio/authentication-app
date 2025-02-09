package com.springapplication.userapp.core.adapters.api;

import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.UserError;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.springapplication.userapp.controller.model.NewUserDTO;

import java.util.Random;
import java.util.UUID;
import java.util.random.RandomGenerator;

@Component
class RegisterMapper {

    @Autowired
    PasswordEncoder passwordEncoder;

    public Either<UserError, User>  mapper(NewUserDTO userDTO) {

        // FIX: instead of this, create database versioning (flyway)
        // so that I have automated primary key generation in the database
        User user = new User();
        user.setId(new Random().nextInt());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        return Either.right(user);

    }

}
