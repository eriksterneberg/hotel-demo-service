package com.hotel.demo.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Observability configuration for metrics, tracing, and monitoring.
 * Configures Micrometer for metrics collection and OpenTelemetry for distributed tracing.
 */
@Configuration
public class ObservabilityConfig {
    
    /**
     * Counter for total search requests.
     */
    @Bean
    public Counter searchRequestCounter(MeterRegistry registry) {
        return Counter.builder("hotel.search.requests.total")
            .description("Total number of search requests")
            .tag("type", "email")
            .register(registry);
    }
    
    /**
     * Counter for search errors.
     */
    @Bean
    public Counter searchErrorCounter(MeterRegistry registry) {
        return Counter.builder("hotel.search.errors.total")
            .description("Total number of search errors")
            .register(registry);
    }
    
    /**
     * Timer for search latency.
     */
    @Bean
    public Timer searchLatencyTimer(MeterRegistry registry) {
        return Timer.builder("hotel.search.latency")
            .description("Search operation latency")
            .register(registry);
    }
}
