package com.springapplication.userapp.controller;

import com.springapplication.userapp.model.RegisterRequestObjectMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:mysql://localhost:3306/mydb",  // Or your preferred DB URL
        "spring.datasource.username=root",
        "spring.datasource.password=root",
        "spring.datasource.platform=mysql",
        "spring.sql.init.mode=never", // Disable SQL initialization (no connection made)
        "spring.jpa.hibernate.ddl-auto=none"  // Don't perform schema creation
})
public class RegisterValidatorTest {

    @Autowired
    RegisterValidator registerValidator;

    @Test
    public void givenValidUserDTO_whenValidate_thenReturnsUserDTO() {

        var userDTO = RegisterRequestObjectMother.makeValidUserDTO();

        var result = registerValidator.validation(userDTO);

        Assertions.assertEquals(userDTO.getUsername(), result.get().getUsername());
        Assertions.assertEquals(userDTO.getEmail(), result.get().getEmail());
        Assertions.assertEquals(userDTO.getPassword(), result.get().getPassword());
        Assertions.assertEquals(userDTO.getSecondPassword(), result.get().getSecondPassword());

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
