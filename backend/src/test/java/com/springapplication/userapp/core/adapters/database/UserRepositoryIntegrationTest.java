package com.springapplication.userapp.core.adapters.database;

import com.springapplication.userapp.core.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void givenUserSaved_whenGetByUsername_thenUserReturned() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("randomName");
        user.setEmail("randomEmail");
        user.setPassword("randomPassword");

        var result1 = userRepository.save(user);
        System.out.println(result1);

        var result = userRepository.findByUsername(user.getUsername());
        System.out.println(result);

        assertTrue(result.isRight());
        assertThat(result.get().get())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(user);
    }

    @Test
    void givenNoUserSaved_whenGetByEmail_thenNoUserReturned() {
        String email = "someEmail";

        var result = userRepository.findByEmail(email);

        assertTrue(result.isRight());
        assertEquals(Optional.empty(), result.get());
    }

}
