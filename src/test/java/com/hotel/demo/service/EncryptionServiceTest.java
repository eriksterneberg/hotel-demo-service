package com.hotel.demo.service;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for EncryptionService.
 */
@ExtendWith(MockitoExtension.class)
class EncryptionServiceTest {
    
    @Mock
    private StringEncryptor encryptor;
    
    private EncryptionService encryptionService;
    
    @BeforeEach
    void setUp() {
        encryptionService = new EncryptionService(encryptor);
    }
    
    @Test
    void testEncrypt_success() {
        String plainText = "test@example.com";
        String encrypted = "ENCRYPTED_VALUE";
        when(encryptor.encrypt(plainText)).thenReturn(encrypted);
        
        String result = encryptionService.encrypt(plainText);
        
        assertEquals(encrypted, result);
        verify(encryptor, times(1)).encrypt(plainText);
    }
    
    @Test
    void testEncrypt_nullValue() {
        String result = encryptionService.encrypt(null);
        
        assertNull(result);
        verify(encryptor, never()).encrypt(anyString());
    }
    
    @Test
    void testEncrypt_emptyString() {
        String result = encryptionService.encrypt("");
        
        assertEquals("", result);
        verify(encryptor, never()).encrypt(anyString());
    }
    
    @Test
    void testEncrypt_failure() {
        String plainText = "test@example.com";
        when(encryptor.encrypt(plainText)).thenThrow(new RuntimeException("Encryption failed"));
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            encryptionService.encrypt(plainText);
        });
        
        assertTrue(exception.getMessage().contains("Failed to encrypt value"));
    }
    
    @Test
    void testDecrypt_success() {
        String encryptedText = "ENCRYPTED_VALUE";
        String decrypted = "test@example.com";
        when(encryptor.decrypt(encryptedText)).thenReturn(decrypted);
        
        String result = encryptionService.decrypt(encryptedText);
        
        assertEquals(decrypted, result);
        verify(encryptor, times(1)).decrypt(encryptedText);
    }
    
    @Test
    void testDecrypt_nullValue() {
        String result = encryptionService.decrypt(null);
        
        assertNull(result);
        verify(encryptor, never()).decrypt(anyString());
    }
    
    @Test
    void testDecrypt_emptyString() {
        String result = encryptionService.decrypt("");
        
        assertEquals("", result);
        verify(encryptor, never()).decrypt(anyString());
    }
    
    @Test
    void testDecrypt_failure() {
        String encryptedText = "ENCRYPTED_VALUE";
        when(encryptor.decrypt(encryptedText)).thenThrow(new RuntimeException("Decryption failed"));
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            encryptionService.decrypt(encryptedText);
        });
        
        assertTrue(exception.getMessage().contains("Failed to decrypt value"));
    }
}
