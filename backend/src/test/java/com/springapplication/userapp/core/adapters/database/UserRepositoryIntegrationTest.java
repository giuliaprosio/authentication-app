package com.springapplication.userapp.core.adapters.database;

import com.springapplication.userapp.core.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;
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

    @Test
    void givenUserSaved_whenGetByEmail_thenUserReturned() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("findEmail");
        user.setEmail("findEmail@test.com");
        user.setPassword("pass");

        var saved = userRepository.save(user);
        assertTrue(saved.isRight());

        var result = userRepository.findByEmail("findEmail@test.com");

        assertTrue(result.isRight());
        assertThat(result.get().get())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(user);
    }

    @Test
    void givenUserSaved_whenUpdateRefreshToken_thenRefreshTokenUpdated() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("updateTestUser");
        user.setEmail("update@test.com");
        user.setPassword("pw");

        userRepository.save(user);

        user.setRefreshToken("newRefreshToken");

        var updated = userRepository.update(user);
        assertTrue(updated.isRight());

        var result = userRepository.findByUsername("updateTestUser");
        assertTrue(result.isRight());
        assertEquals("newRefreshToken", result.get().get().getRefreshToken());
    }

    @Test
    void loadUserByUsername_whenUserExists_thenReturnUserDetails() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("loadUser");
        user.setEmail("load@test.com");
        user.setPassword("secret");

        userRepository.save(user);

        var details = userRepository.loadUserByUsername("loadUser");
        assertEquals("loadUser", details.getUsername());
    }

    @Test
    void loadUserByUsername_whenUserMissing_thenThrow() {
        assertThrows(NoSuchElementException.class,
                () -> userRepository.loadUserByUsername("no_such_user"));
    }
}
