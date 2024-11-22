package com.springapplication.userapp.controller;

import com.springapplication.userapp.model.UserError;
import io.vavr.control.Either;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import com.springapplication.userapp.controller.model.NewUserDTO;

@Component
class RegisterValidator {

    public Either<UserError, NewUserDTO> validation(NewUserDTO userDTO) {

        if (Strings.isBlank(userDTO.getUsername())) {
            return Either.left(new UserError.NoUsername());
        }

        if(Strings.isBlank(userDTO.getEmail())) {
            return Either.left(new UserError.NoEmail());
        }

        if(Strings.isBlank(userDTO.getPassword())) {
            return Either.left(new UserError.NoPassword());
        }

        if(Strings.isBlank(userDTO.getSecondPassword())) {
            return Either.left(new UserError.NoSecondPassword());
        }

        if(!userDTO.getPassword().equals(userDTO.getSecondPassword())) {
            return Either.left(new UserError.SecondPasswordNoMatch());
        }

        return Either.right(userDTO);
    }
}
