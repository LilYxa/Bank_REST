package com.example.bankcards.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.util.Base64;

@Configuration
public class EncryptionConfig {

    @Value("${encryption.password}")
    private String encryptionPassword;

    @Value("${encryption.salt}")
    private String encryptionSalt;

    @Bean
    public TextEncryptor textEncryptor() {
        return new TextEncryptor() {
            private final AesBytesEncryptor bytesEncryptor = new AesBytesEncryptor(encryptionPassword, encryptionSalt);

            @Override
            public String encrypt(String text) {
                byte[] encrypted = bytesEncryptor.encrypt(text.getBytes());
                return Base64.getEncoder().encodeToString(encrypted);
            }

            @Override
            public String decrypt(String encryptedText) {
                byte[] decoded = Base64.getDecoder().decode(encryptedText);
                byte[] decrypted = bytesEncryptor.decrypt(decoded);
                return new String(decrypted);
            }
        };
    }

}
