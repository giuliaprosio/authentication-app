package com.springapplication.userapp.core.domain.model;

import java.util.UUID;

public class UserObjectMother {

    public static User createValidUser() {
        User user = new User();
        user.setUsername(UUID.randomUUID().toString());
        user.setEmail(UUID.randomUUID().toString());
        user.setPassword(UUID.randomUUID().toString());
        user.setRefreshToken(UUID.randomUUID().toString());

        return user;
    }

    public static User createValidUnauthorizedUser(){
        User user = new User();
        user.setUsername(UUID.randomUUID().toString());
        user.setEmail(UUID.randomUUID().toString());
        user.setPassword(UUID.randomUUID().toString());

        return user;
    }

    public static User updateUnauthorizedUser(String refreshToken, User user){
        var usr = new User();
        usr.setUsername(user.getUsername());
        usr.setEmail(user.getEmail());
        usr.setPassword(user.getPassword());
        usr.setRefreshToken(refreshToken);

        return usr;
    }

}
