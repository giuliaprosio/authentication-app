package com.springapplication.userapp.providers.encryption;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import java.security.NoSuchAlgorithmException;

import static java.util.UUID.randomUUID;

@Service
public class Encryptor {

    private final PasswordEncoder passwordEncoder;

    public Encryptor() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String encrypt(String state) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException {
        return encryptState(state);
    }

    private String encryptState(String state) {
        return passwordEncoder.encode(state + salt());
    }

    private String salt(){
        return randomUUID().toString();
    }
}
