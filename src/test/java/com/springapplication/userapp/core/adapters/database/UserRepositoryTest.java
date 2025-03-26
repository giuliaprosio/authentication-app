package com.springapplication.userapp.core.adapters.database;

import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.UserObjectMother;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// to finish this I have to do a make deps thing
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
