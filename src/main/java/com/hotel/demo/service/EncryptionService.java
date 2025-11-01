package com.hotel.demo.service;

import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service for encrypting and decrypting PII fields.
 * Uses Jasypt for field-level encryption.
 */
@Service
public class EncryptionService {
    
    private static final Logger log = LoggerFactory.getLogger(EncryptionService.class);
    
    private final StringEncryptor encryptor;
    
    public EncryptionService(@Qualifier("jasyptStringEncryptor") StringEncryptor encryptor) {
        this.encryptor = encryptor;
    }
    
    /**
     * Encrypt a plain text value.
     * 
     * @param plainText Plain text to encrypt
     * @return Encrypted value
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        try {
            return encryptor.encrypt(plainText);
        } catch (Exception e) {
            log.error("Encryption failed", e);
            throw new RuntimeException("Failed to encrypt value", e);
        }
    }
    
    /**
     * Decrypt an encrypted value.
     * 
     * @param encryptedText Encrypted text
     * @return Decrypted plain text
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }
        try {
            return encryptor.decrypt(encryptedText);
        } catch (Exception e) {
            log.error("Decryption failed", e);
            throw new RuntimeException("Failed to decrypt value", e);
        }
    }
}
