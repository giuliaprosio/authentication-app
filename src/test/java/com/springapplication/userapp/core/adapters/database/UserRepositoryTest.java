package com.springapplication.userapp.core.adapters.database;

import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.error.AdaptersError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserRepositoryTest {

    JdbcTemplate jdbcTemplate;
    UserRepository repository;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        repository = new UserRepository(jdbcTemplate);
    }

    @Test
    void givenUserInDb_whenFindByUsername_thenReturnUser() {
        UUID id = UUID.randomUUID();
        byte[] idBytes = uuidToBytes(id);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any()))
                .thenReturn(List.of(new UserRepository.DatabaseUser(
                        idBytes, "john", "john@mail", "pass", "refTok"
                )));

        var result = repository.findByUsername("john");

        assertTrue(result.isRight());
        User user = result.get().get();
        assertEquals("john", user.getUsername());
        assertEquals("john@mail", user.getEmail());
        assertEquals("pass", user.getPassword());
        assertEquals("refTok", user.getRefreshToken());
        assertEquals(id, user.getId());
    }

    @Test
    void givenNoUser_whenFindByUsername_thenReturnEmpty() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any()))
                .thenReturn(List.of());

        var result = repository.findByUsername("unknown");

        assertTrue(result.isRight());
        assertEquals(Optional.empty(), result.get());
    }

    @Test
    void givenUserInDb_whenFindByEmail_thenReturnUser() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any()))
                .thenReturn(List.of(new UserRepository.DatabaseUser(
                        uuidToBytes(UUID.randomUUID()),
                        "mailUser", "mail@a", "pw", "rt"
                )));

        var result = repository.findByEmail("mail@a");

        assertTrue(result.isRight());
        assertTrue(result.get().isPresent());
        assertEquals("mailUser", result.get().get().getUsername());
    }

    @Test
    void givenUser_whenSave_thenJdbcUpdateIsCalledAndReturnsUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("a");
        user.setEmail("b");
        user.setPassword("c");
        user.setRefreshToken("d");

        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any()))
                .thenReturn(1);

        var result = repository.save(user);

        assertTrue(result.isRight());
        assertEquals(user, result.get());
        verify(jdbcTemplate).update(anyString(), any(), eq("a"), eq("b"), eq("c"), eq("d"));
    }

    @Test
    void givenUser_whenUpdate_thenJdbcUpdateOccurs() {
        User user = new User();
        user.setUsername("myUser");
        user.setRefreshToken("newToken");

        when(jdbcTemplate.update(anyString(), any(), any()))
                .thenReturn(1);

        var result = repository.update(user);

        assertTrue(result.isRight());
        assertEquals(user, result.get());

        verify(jdbcTemplate).update(anyString(), eq("newToken"), eq("myUser"));
    }

    @Test
    void givenDbError_whenFindByUsername_thenLeftIsReturned() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any()))
                .thenThrow(new RuntimeException("DB DOWN"));

        var result = repository.findByUsername("x");

        assertTrue(result.isLeft());
        assertInstanceOf(AdaptersError.DatabaseError.class, result.getLeft());
        assertTrue(((AdaptersError.DatabaseError) result.getLeft()).message().contains("DB DOWN"));
    }

    @Test
    void givenUser_whenLoadUserByUsername_thenReturnsUserDetails() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("abc");
        user.setEmail("e");
        user.setPassword("p");

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any()))
                .thenReturn(List.of(new UserRepository.DatabaseUser(
                        uuidToBytes(user.getId()),
                        "abc", "e", "p", null
                )));

        var details = repository.loadUserByUsername("abc");

        assertEquals("abc", details.getUsername());
        assertEquals("p", details.getPassword());
    }

    @Test
    void givenNoUser_whenLoadUserByUsername_thenThrowsException() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any()))
                .thenReturn(List.of());

        assertThrows(RuntimeException.class, () ->
                repository.loadUserByUsername("missing")
        );
    }

    @Test
    void uuidConversionRoundTrip() {
        UUID id = UUID.randomUUID();

        byte[] bytes = uuidToBytes(id);
        UUID restored = bytesToUUID(bytes);

        assertEquals(id, restored);
    }

    private byte[] uuidToBytes(UUID uuid) {
        return new UserRepository(jdbcTemplate).uuidToBytes(uuid);
    }

    private UUID bytesToUUID(byte[] bytes) {
        return new UserRepository(jdbcTemplate).bytesToUUID(bytes);
    }
}
