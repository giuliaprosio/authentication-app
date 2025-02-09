package com.springapplication.userapp.core.adapters.api;

import com.springapplication.userapp.core.domain.model.User;
import com.springapplication.userapp.core.domain.model.RegisterRequestObjectMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.springapplication.userapp.controller.model.NewUserDTO;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegisterMapperTest {

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    RegisterMapper registerMapper;

    @Test
    public void givenValidUserDTo_whenMapping_thenMapToUser() {

        NewUserDTO userDTO = RegisterRequestObjectMother.makeValidUserDTO();
        User user = RegisterRequestObjectMother.makeValidUserFromDTO(userDTO);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("password");
        var result = registerMapper.mapper(userDTO);

        Assertions.assertEquals(user.getUsername(), result.get().getUsername());
        Assertions.assertEquals(user.getEmail(), result.get().getEmail());
        Assertions.assertEquals(user.getPassword(), result.get().getPassword());

    }

}
