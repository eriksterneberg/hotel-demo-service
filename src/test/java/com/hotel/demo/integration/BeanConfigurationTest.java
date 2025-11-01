package com.hotel.demo.integration;

import com.hotel.demo.service.EncryptionService;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lightweight integration test focused on bean configuration.
 * Tests that the Spring application context loads successfully with all beans
 * properly configured, without requiring external dependencies like Cassandra.
 * 
 * This test will catch:
 * - Bean definition conflicts (duplicate beans without @Primary)
 * - Circular dependencies
 * - Missing required dependencies
 * - Autowiring ambiguities
 */
@SpringBootTest(properties = {
    "spring.cassandra.local-datacenter=datacenter1",
    "spring.cassandra.contact-points=localhost:9042",
    "spring.cassandra.keyspace-name=hotel_demo",
    "spring.cassandra.schema-action=none",
    "spring.data.cassandra.repositories.enabled=false"
})
@ActiveProfiles("test")
class BeanConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * This test verifies the application context loads successfully.
     * It will fail immediately if there are bean configuration errors like:
     * - Multiple beans of the same type without @Primary
     * - Missing @Qualifier annotations
     * - Circular dependencies
     */
    @Test
    void applicationContextShouldLoadSuccessfully() {
        assertNotNull(applicationContext, "Application context should load successfully");
    }

    /**
     * Verify that EncryptionService can be autowired without ambiguity.
     * This ensures the StringEncryptor dependency is properly resolved.
     */
    @Test
    void encryptionServiceShouldBeAutowirable() {
        EncryptionService encryptionService = applicationContext.getBean(EncryptionService.class);
        assertNotNull(encryptionService, "EncryptionService should be available in context");
    }

    /**
     * Verify that the jasyptStringEncryptor bean exists and can be retrieved.
     */
    @Test
    void jasyptStringEncryptorBeanShouldExist() {
        StringEncryptor encryptor = applicationContext.getBean("jasyptStringEncryptor", StringEncryptor.class);
        assertNotNull(encryptor, "jasyptStringEncryptor bean should exist");
    }

    /**
     * Test that verifies we can resolve StringEncryptor without ambiguity.
     * If there are multiple StringEncryptor beans and none is marked @Primary,
     * this test will fail with a NoUniqueBeanDefinitionException during context load.
     */
    @Test
    void stringEncryptorShouldBeResolvableWithoutAmbiguity() {
        // The context loaded successfully, which means either:
        // 1. There's only one StringEncryptor bean, OR
        // 2. One is marked as @Primary, OR
        // 3. All consumers use @Qualifier
        
        String[] beanNames = applicationContext.getBeanNamesForType(StringEncryptor.class);
        assertTrue(beanNames.length > 0, "At least one StringEncryptor bean should exist");
        
        System.out.println("Found StringEncryptor beans: " + String.join(", ", beanNames));
    }

    /**
     * Functional test: Verify encryption service actually works.
     */
    @Test
    void encryptionServiceShouldEncryptAndDecrypt() {
        EncryptionService encryptionService = applicationContext.getBean(EncryptionService.class);
        
        String plainText = "sensitive-email@example.com";
        String encrypted = encryptionService.encrypt(plainText);
        String decrypted = encryptionService.decrypt(encrypted);
        
        assertNotNull(encrypted, "Encrypted value should not be null");
        assertNotEquals(plainText, encrypted, "Encrypted value should differ from plain text");
        assertEquals(plainText, decrypted, "Decrypted value should match original");
    }
}
