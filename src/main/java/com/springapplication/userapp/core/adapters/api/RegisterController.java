package com.springapplication.userapp.core.adapters.api;

import com.springapplication.userapp.core.domain.port.input.UserRegistrationHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.springapplication.userapp.controller.api.RegisterApiDelegate;
import com.springapplication.userapp.controller.model.NewUserDTO;

@Component
class RegisterController implements RegisterApiDelegate {

    private final RegisterValidator registerValidator;
    private final RegisterMapper registerMapper;
    private final UserRegistrationHandler userRegistrationHandler;


    public RegisterController(RegisterValidator registerValidator, RegisterMapper registerMapper, UserRegistrationHandler userRegistrationHandler) {
        this.registerValidator = registerValidator;
        this.registerMapper = registerMapper;
        this.userRegistrationHandler = userRegistrationHandler;
    }

    @Override
    public ResponseEntity<String> getRegisterPage() {
        return new ResponseEntity<>("register", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> submission(NewUserDTO userDTO) {
        return registerValidator.validation(userDTO)
                .flatMap(registerMapper::mapper)
                .flatMap(userRegistrationHandler::handleUserRegistration)
                .fold(
                        error -> new ResponseEntity<>(error.toString(), HttpStatus.OK),
                        succ -> new ResponseEntity<>("added", HttpStatus.OK)
                );
    }

}