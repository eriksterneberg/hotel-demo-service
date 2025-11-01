package com.hotel.demo.integration;

import com.hotel.demo.service.EncryptionService;
import com.hotel.demo.service.OrderSearchService;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test to verify the Spring application context loads successfully.
 * This test catches bean configuration errors, circular dependencies, and other
 * startup issues that might not be caught by unit tests.
 */
@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext, "Application context should load successfully");
    }

    @Test
    void verifyEncryptionServiceBeanExists() {
        EncryptionService encryptionService = applicationContext.getBean(EncryptionService.class);
        assertNotNull(encryptionService, "EncryptionService bean should be available");
    }

    @Test
    void verifyStringEncryptorBeanExists() {
        // This should get the jasyptStringEncryptor bean
        StringEncryptor encryptor = applicationContext.getBean("jasyptStringEncryptor", StringEncryptor.class);
        assertNotNull(encryptor, "jasyptStringEncryptor bean should be available");
    }

    @Test
    void verifyOrderSearchServiceBeanExists() {
        OrderSearchService orderSearchService = applicationContext.getBean(OrderSearchService.class);
        assertNotNull(orderSearchService, "OrderSearchService bean should be available");
    }

    @Test
    void verifyNoDuplicateStringEncryptorBeans() {
        // This test will fail if there are multiple StringEncryptor beans without @Primary
        // or if the beans are not properly qualified
        String[] beanNames = applicationContext.getBeanNamesForType(StringEncryptor.class);
        
        // We expect specific beans - should not cause ambiguity
        assertNotNull(beanNames, "Should be able to query StringEncryptor beans");
        assertTrue(beanNames.length > 0, "At least one StringEncryptor bean should exist");
        
        // Try to get the bean - this will fail if there's ambiguity and no @Primary
        try {
            applicationContext.getBean(StringEncryptor.class);
            // If we get here without exception, either there's only one bean or one is marked @Primary
        } catch (Exception e) {
            fail("Should be able to resolve StringEncryptor bean without ambiguity. " +
                 "Found beans: " + String.join(", ", beanNames) + ". Error: " + e.getMessage());
        }
    }

    @Test
    void verifyEncryptionServiceCanEncryptAndDecrypt() {
        EncryptionService encryptionService = applicationContext.getBean(EncryptionService.class);
        
        String plainText = "test@example.com";
        String encrypted = encryptionService.encrypt(plainText);
        String decrypted = encryptionService.decrypt(encrypted);
        
        assertNotEquals(plainText, encrypted, "Encrypted value should differ from plain text");
        assertEquals(plainText, decrypted, "Decrypted value should match original plain text");
    }
}
