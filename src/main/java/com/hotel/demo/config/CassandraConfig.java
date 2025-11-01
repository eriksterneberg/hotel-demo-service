package com.hotel.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

/**
 * Cassandra configuration for hotel booking order storage.
 * Configures connection settings, keyspace, and repository scanning.
 */
@Configuration
@EnableCassandraRepositories(basePackages = "com.hotel.demo.repository")
public class CassandraConfig extends AbstractCassandraConfiguration {
    
    @Value("${spring.cassandra.keyspace-name}")
    private String keyspaceName;
    
    @Value("${spring.cassandra.contact-points}")
    private String contactPoints;
    
    @Value("${spring.cassandra.port}")
    private int port;
    
    @Value("${spring.cassandra.local-datacenter}")
    private String localDatacenter;
    
    @Override
    protected String getKeyspaceName() {
        return keyspaceName;
    }
    
    @Override
    protected String getContactPoints() {
        return contactPoints;
    }
    
    @Override
    protected int getPort() {
        return port;
    }
    
    @Override
    protected String getLocalDataCenter() {
        return localDatacenter;
    }
    
    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.NONE; // Schema managed via CQL scripts
    }
}
