package com.springapplication.userapp.core.adapters.database;

import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.error.AdaptersError;
import com.springapplication.userapp.core.domain.port.output.UserPersistence;
import com.springapplication.userapp.providers.logging.Logger;
import com.springapplication.userapp.providers.logging.LoggerFactory;
import io.vavr.control.Either;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import io.vavr.control.Try;


import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository implements UserPersistence, UserDetailsService {

    private final JdbcTemplate jdbcTemplate;
    private static final RowMapper<DatabaseUser> ROW_MAPPER = new DataClassRowMapper<>(DatabaseUser.class);
    private final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Either<AdaptersError, Optional<User>> findByUsername(String username) {
        String sql = "SELECT * FROM users_table WHERE username = ?" ;

        return Try.of(() -> unsafeGet(sql, username))
                .toEither()
                .mapLeft(this::mapError)
                .map(this::mapper);
    }

    @Override
    public Either<AdaptersError, Optional<User>> findByEmail(String email) {
        String sql = "SELECT * FROM users_table WHERE email = ?";

        return Try.of(() -> unsafeGet(sql, email))
                .toEither()
                .mapLeft(this::mapError)
                .map(this::mapper);
    }

    @Override
    public Either<AdaptersError, User> save(User user) {
        String sql = "INSERT INTO users_table (id, username, email, password, refresh_token) " +
                "VALUES (?, ?, ?, ?, ?)";
        logger.info("Saving new user or updating a new one");

        return Try.of(() -> unsafeInsert(sql, user))
                .toEither()
                .mapLeft(this::mapError);
    }

    @Override
    public Either<AdaptersError, User> update(User user){
        String sql = "UPDATE users_table SET refresh_token = ? WHERE username = ?";
        logger.info("Updating user");
        return Try.of(() -> unsafeUpdate(sql, user))
                .toEither()
                .mapLeft(this::mapError);
    }

    // HORRIBLE let's see how I can change it
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username).get().get();
    }

    private AdaptersError mapError(Throwable throwable){
        logger.error("Generic error in the db: " + throwable);
        return new AdaptersError.DatabaseError("Generic error in db: " + throwable.getMessage(), Optional.of(throwable));
    }

    private User unsafeInsert(String sql, User user){
        jdbcTemplate.update(
            sql,
            uuidToBytes(user.getId()),
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            user.getRefreshToken());
        return user;
    }

    private User unsafeUpdate(String sql, User user){
        jdbcTemplate.update(
                sql,
                user.getRefreshToken(),
                user.getUsername());
        return user;
    }

    private Optional<DatabaseUser> unsafeGet(String sql, String username){
        var maybeUser =  jdbcTemplate.query(sql, ROW_MAPPER, username);
        return maybeUser.isEmpty() ? Optional.empty() : Optional.of(maybeUser.get(0));
    }

    private Optional<User> mapper(Optional<DatabaseUser> databaseUser){

        if(databaseUser.isPresent()){
            var user = new User();
            user.setId(bytesToUUID(databaseUser.get().id()));
            user.setUsername(databaseUser.get().username());
            user.setEmail(databaseUser.get().email());
            user.setPassword(databaseUser.get().password());
            user.setRefreshToken(databaseUser.get().refresh_token());
            return Optional.of(user);
        }
        return Optional.empty();
    }

    private record DatabaseUser(
            byte[] id,
            String username,
            String email,
            String password,
            String refresh_token
    ){}

    private byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    private UUID bytesToUUID(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        return new UUID(high, low);
    }
}
