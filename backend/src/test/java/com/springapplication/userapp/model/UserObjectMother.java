package com.springapplication.userapp.model;

import java.util.UUID;

public class UserObjectMother {

    public static User createValidUser() {
        User user = new User();
        user.setUsername(UUID.randomUUID().toString());
        user.setEmail(UUID.randomUUID().toString());
        user.setPassword(UUID.randomUUID().toString());

        return user;
    }

}
