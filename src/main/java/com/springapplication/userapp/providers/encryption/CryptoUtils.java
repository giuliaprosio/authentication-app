package com.springapplication.userapp.providers.encryption;

import com.springapplication.userapp.core.domain.model.UserError;
import com.springapplication.userapp.providers.logging.Logger;
import com.springapplication.userapp.providers.logging.LoggerFactory;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import static java.util.UUID.randomUUID;

@Service
public class CryptoUtils {

    private static final Logger logger = LoggerFactory.getLogger(CryptoUtils.class);
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private final byte[] key;

    public CryptoUtils(@Value("${state-key}") String key) {
        this.key = Base64.getDecoder().decode(key);
    }

    public Either<UserError, String> encrypt(String state){
        return doCrypto(Cipher.ENCRYPT_MODE, state);
    }

    public Either<UserError, String> decrypt(String state){
        return doCrypto(Cipher.DECRYPT_MODE, state);
    }

    private Either<UserError, String> doCrypto(int cipherMode, String state) {
        try {
            Key secretKey = new SecretKeySpec(key, ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            byte[] input = state.getBytes();

            if (cipherMode == Cipher.ENCRYPT_MODE) {
                byte[] outputBytes = cipher.doFinal(input);
                return Either.right(Base64.getEncoder().encodeToString(outputBytes));
            } else {
                byte[] decodedInput = Base64.getDecoder().decode(state);
                byte[] decryptedBytes = cipher.doFinal(decodedInput);
                return Either.right(new String(decryptedBytes)); // Convert back to String
            }
        } catch (Exception e) {
            var error = new UserError.GenericError("Error encrypting/decrypting " + e.getLocalizedMessage());
            logger.error("Error encrypting/decrypting state: " + state + "\n" + e);
            return Either.left(error);
        }
    }
}
