package com.springapplication.userapp.core.adapters.api;

import com.springapplication.userapp.core.domain.model.RegisterRequestObjectMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class RegisterValidatorTest {

    RegisterValidator registerValidator = new RegisterValidator();

    @Test
    public void givenValidUserDTO_whenValidate_thenReturnsUserDTO() {

        var userDTO = RegisterRequestObjectMother.makeValidUserDTO();

        var result = registerValidator.validation(userDTO);
        var rightResult = result.get();

        assertThat(rightResult)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(userDTO);
    }

    @Test
    public void givenMissingUsername_whenValidate_thenReturnsError() {

        var userDTO = RegisterRequestObjectMother.userDTOMissingName();

        var result = registerValidator.validation(userDTO);

        Assertions.assertEquals("Username required.", result.getLeft().toString());

    }

    @Test
    public void givenMissingEmail_whenValidate_thenReturnsError() {

        var userDTO = RegisterRequestObjectMother.userDTOMissingEmail();

        var result = registerValidator.validation(userDTO);

        Assertions.assertEquals("Email required.", result.getLeft().toString());

    }

    @Test
    public void givenMissingPassword_whenValidate_thenReturnsError() {

        var userDTO = RegisterRequestObjectMother.userDTOMissingPassword();

        var result = registerValidator.validation(userDTO);

        Assertions.assertEquals("Password required.", result.getLeft().toString());
    }

    @Test
    public void givenMissingSecondPassword_whenValidate_thenReturnsError() {

        var userDTO = RegisterRequestObjectMother.userDTOMissingSecondPassword();

        var result = registerValidator.validation(userDTO);

        Assertions.assertEquals("Second password check required.", result.getLeft().toString());

    }

    @Test
    public void givenPasswordsNotMatching_whenValidate_thenReturnsError() {

        var userDTO = RegisterRequestObjectMother.userDTOPasswordsNotMatching();

        var result = registerValidator.validation(userDTO);

        Assertions.assertEquals("The passwords are not matching.", result.getLeft().toString());
    }

}
