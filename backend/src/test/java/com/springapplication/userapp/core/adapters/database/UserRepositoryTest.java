package com.springapplication.userapp.core.adapters.database;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserRepositoryTest {

    // Integration Test
    @Autowired
    UserRepository userRepository;

    @Test
    void givenNoUserSaved_whenGetByUsername_thenNoUserReturned() {
        String username = "randomName";

        var result = userRepository.findByUsername(username);

        assertTrue(result.isRight());
        assertEquals(Optional.empty(), result.get());
    }

    @Test
    void givenNoUserSaved_whenGetByEmail_thenNoUserReturned() {
        String email = "randomEmail";

        var result = userRepository.findByEmail(email);

        assertTrue(result.isRight());
        assertEquals(Optional.empty(), result.get());
    }

}
