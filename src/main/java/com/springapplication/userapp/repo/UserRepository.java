package com.springapplication.userapp.repo;

import com.springapplication.userapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.username = ?1")
    public User findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    public User findByEmail(String email);

}
