package com.springapplication.userapp.configuration.service;

import com.springapplication.userapp.core.domain.model.UserObjectMother;
import com.springapplication.userapp.core.domain.port.output.UserPersistence;
import io.vavr.control.Either;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/*@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:mysql://localhost:3306/mydb",
        "spring.datasource.username=root",
        "spring.datasource.password=root",
        "spring.datasource.platform=mysql",
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=none"
})
public class UserDetailsServiceImplTest {

    @MockBean
    private UserPersistence userPersistence;


    @Test
    public void givenValidCredentials_whenRegisterUser_thenSaveInRepo() {
        var user = UserObjectMother.createValidUser();

        //var result = userDetailsService.registerUser(user);

       // Assertions.assertEquals(user, result.get());
    }

    @Test
    public void givenValidUsername_whenLoadUser_thenReturnUser() {
        var user = UserObjectMother.createValidUser();

        when(userPersistence.findByUsername(anyString())).thenReturn(Either.right(Optional.of(user)));
        //var result = userDetailsService.loadUserByUsername(user.getUsername());

        //Assertions.assertEquals(user.getUsername(), result.getUsername());

    }

}*/
