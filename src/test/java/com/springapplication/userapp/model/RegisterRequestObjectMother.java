package com.springapplication.userapp.model;

import com.springapplication.userapp.controller.model.NewUserDTO;

public class RegisterRequestObjectMother {

    public static NewUserDTO makeValidUserDTO() {

        var userDTO = new NewUserDTO();
        userDTO.setUsername("username");
        userDTO.setEmail("email");
        userDTO.setPassword("password");
        userDTO.setSecondPassword("password");

        return userDTO;

    }

    public static NewUserDTO userDTOMissingName() {

        var userDTO = makeValidUserDTO();
        userDTO.setUsername(null);

        return userDTO;
    }

    public static NewUserDTO userDTOMissingEmail() {

        var userDTO = makeValidUserDTO();
        userDTO.setEmail(null);

        return userDTO;
    }

    public static NewUserDTO userDTOMissingPassword() {

        var userDTO = makeValidUserDTO();
        userDTO.setPassword(null);

        return userDTO;
    }

    public static NewUserDTO userDTOMissingSecondPassword() {

        var userDTO = makeValidUserDTO();
        userDTO.setSecondPassword(null);

        return userDTO;
    }

    public static NewUserDTO userDTOPasswordsNotMatching() {

        var userDTO = makeValidUserDTO();
        userDTO.setSecondPassword("differentPassword");

        return userDTO;
    }

    public static User makeValidUserFromDTO(NewUserDTO userDTO) {

        var user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());

        return user;
    }
}
