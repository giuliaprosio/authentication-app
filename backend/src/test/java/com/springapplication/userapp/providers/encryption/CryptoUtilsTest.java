package com.springapplication.userapp.providers.encryption;

import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilsTest {

    private CryptoUtils cryptoUtils;

    @BeforeEach
    void setUp() {
        String base64Key = "MDEyMzQ1Njc4OWFiY2RlZg=="; // "0123456789abcdef"
        cryptoUtils = new CryptoUtils(base64Key);
    }

    @Test
    void testEncryptAndDecrypt() {
        String input = "Hello World!";

        Either<EncryptionError, String> encrypted = cryptoUtils.encrypt(input);
        assertTrue(encrypted.isRight(), "Encryption should succeed");

        Either<EncryptionError, String> decrypted =
                cryptoUtils.decrypt(encrypted.get());

        assertTrue(decrypted.isRight(), "Decryption should succeed");
        assertEquals(input, decrypted.get(), "Decrypted text should match original");
    }

    @Test
    void testDecryptInvalidBase64Fails() {
        Either<EncryptionError, String> result = cryptoUtils.decrypt("not-base64!");

        assertTrue(result.isLeft(), "Should return error for invalid data");
        assertTrue(result.getLeft().message().contains("Error"), "Error message should be populated");
    }

    @Test
    void testEncryptNotNull() {
        Either<EncryptionError, String> encrypted = cryptoUtils.encrypt("data");

        assertTrue(encrypted.isRight());
        assertNotNull(encrypted.get());
        assertNotEquals("data", encrypted.get(), "Encrypted output should not be plain text");
    }
}

