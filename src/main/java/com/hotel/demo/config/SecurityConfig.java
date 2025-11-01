package com.hotel.demo.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Security configuration for PII encryption.
 * Uses Jasypt for field-level encryption of sensitive data.
 */
@Configuration
@EnableEncryptableProperties
public class SecurityConfig {
    
    @Value("${jasypt.encryptor.password}")
    private String encryptorPassword;
    
    @Value("${jasypt.encryptor.algorithm}")
    private String algorithm;
    
    @Value("${jasypt.encryptor.key-obtention-iterations}")
    private String keyObtentionIterations;
    
    @Value("${jasypt.encryptor.pool-size}")
    private String poolSize;
    
    /**
     * Creates the Jasypt String Encryptor bean for encrypting/decrypting sensitive data.
     */
    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(encryptorPassword);
        config.setAlgorithm(algorithm);
        config.setKeyObtentionIterations(keyObtentionIterations);
        config.setPoolSize(poolSize);
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }
}

