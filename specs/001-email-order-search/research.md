# Research: Email-Based Hotel Order Search

**Date**: November 1, 2025  
**Feature**: 001-email-order-search  
**Purpose**: Resolve technical unknowns and establish best practices for implementation

## Research Tasks Completed

### 1. MCP (Model Context Protocol) Java SDK Integration

**Decision**: Use official MCP Java SDK with Spring Boot integration

**Rationale**:
- Model Context Protocol is a standardized protocol for AI tool integration
- MCP tools are stateless, invocable operations (vs. resources which are data sources)
- Email search is an active operation initiated by user → perfect fit for MCP tool
- Spring Boot integration available through community libraries or custom implementation

**Implementation Approach**:
- Implement `@McpTool` annotation or equivalent for tool registration
- Define tool schema with input parameters (email: string, customerId: string)
- Return structured response with order array and confidence scores
- Follow MCP JSON-RPC 2.0 protocol for request/response format

**Alternatives Considered**:
- REST API only: Rejected because requirement explicitly specifies MCP tool for GenAI integration
- MCP Resource: Rejected because search is an action (tool), not a data stream (resource)
- Custom protocol: Rejected in favor of standardized MCP for interoperability

**References**:
- MCP Specification: https://spec.modelcontextprotocol.io/
- MCP Java implementations: Review community SDKs or implement custom handler

---

### 2. Fuzzy String Matching with Levenshtein Distance

**Decision**: Apache Commons Text `LevenshteinDistance` for fuzzy email matching

**Rationale**:
- Well-tested, production-ready implementation
- Supports threshold-based matching (10% error tolerance)
- Part of Apache Commons ecosystem (trusted, maintained)
- Efficient algorithm for short strings (emails typically <50 chars)
- Can calculate both absolute distance and normalized similarity score

**Implementation Approach**:
```java
import org.apache.commons.text.similarity.LevenshteinDistance;

// Calculate distance with threshold
LevenshteinDistance distance = new LevenshteinDistance(maxDistance);
int dist = distance.apply(searchEmail, storedEmail);

// Convert to confidence score (0-100%)
double confidence = (1.0 - (double)dist / Math.max(searchEmail.length(), storedEmail.length())) * 100;

// 10% threshold: for 20-char email, allow max 2 character differences
int maxDistance = (int) Math.ceil(storedEmail.length() * 0.10);
```

**Performance Considerations**:
- O(m*n) complexity where m, n are string lengths
- For 100K orders with avg email length 25 chars: ~2.5M operations per search
- Optimization: Use Cassandra SASI index for prefix matching to reduce candidate set before fuzzy matching
- Further optimization: Consider phonetic algorithms (Soundex, Metaphone) for verbal typos

**Alternatives Considered**:
- Jaro-Winkler distance: Better for short strings but less intuitive for character-error threshold
- Soundex/Metaphone: Good for phonetic matching but doesn't handle typos as specified
- Full-text search engines (Elasticsearch): Over-engineering for demo scope; adds complexity
- Custom implementation: Reinventing wheel; Apache Commons is battle-tested

**Dependency**:
```gradle
implementation 'org.apache.commons:commons-text:1.11.0'
```

---

### 3. Cassandra Schema Design for Email Search

**Decision**: Use Cassandra table with SASI (SSTable Attached Secondary Index) for email column

**Rationale**:
- Cassandra chosen per clarification for scalability and distributed architecture demo
- SASI indexes support LIKE queries and case-insensitive searches
- Efficient for medium-scale data (100K orders)
- Demonstrates NoSQL data modeling patterns for GenAI training

**Schema Design**:
```cql
CREATE KEYSPACE hotel_demo 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE hotel_demo.booking_orders (
    order_id UUID PRIMARY KEY,
    customer_email TEXT,
    customer_email_encrypted TEXT,  -- Encrypted version for storage
    booking_date_start TIMESTAMP,
    booking_date_end TIMESTAMP,
    hotel_name TEXT,
    hotel_address TEXT,
    room_type TEXT,
    guest_names LIST<TEXT>,
    payment_method TEXT,
    total_amount DECIMAL,
    order_status TEXT,
    created_at TIMESTAMP
);

-- SASI index for efficient email prefix matching
CREATE CUSTOM INDEX email_sasi_idx ON hotel_demo.booking_orders (customer_email)
USING 'org.apache.cassandra.index.sasi.SASIIndex'
WITH OPTIONS = {
    'mode': 'CONTAINS',
    'analyzer_class': 'org.apache.cassandra.index.sasi.analyzer.StandardAnalyzer',
    'case_sensitive': 'false'
};

-- Additional indexes for common queries
CREATE INDEX ON hotel_demo.booking_orders (order_status);
CREATE INDEX ON hotel_demo.booking_orders (created_at);
```

**Query Strategy**:
1. Exact match: `SELECT * FROM booking_orders WHERE customer_email = ?`
2. Fuzzy match: 
   - First pass: SASI LIKE query with prefix to reduce candidates
   - Second pass: In-memory Levenshtein distance calculation on candidates
   - Sort by confidence score in application layer

**Data Encryption**:
- Store encrypted email in `customer_email_encrypted` column (AES-256)
- Store decrypted email in `customer_email` for search (indexed, but never logged)
- Use Spring Security Crypto `BytesEncryptor` for encryption/decryption
- Key management: externalize via environment variables or Spring Cloud Vault

**Alternatives Considered**:
- Materialized view: Adds complexity; SASI index sufficient for read-heavy workload
- Search-optimized database (Elasticsearch): Over-engineering for demo; adds operational complexity
- Denormalization with email-prefix partition key: Complex querying; SASI index more flexible

**Cassandra Driver**:
```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-cassandra'
```

---

### 4. Observability Stack Integration

**Decision**: Spring Boot Actuator + Micrometer + OpenTelemetry + Structured Logging

**Rationale**:
- Spring Boot Actuator: Built-in health checks and metrics endpoints
- Micrometer: Vendor-neutral metrics facade (supports Prometheus, Datadog, etc.)
- OpenTelemetry: Standardized distributed tracing (supports Jaeger, Zipkin, etc.)
- Logback: Structured JSON logging for log aggregation systems
- Demonstrates production-grade observability for GenAI education

**Implementation Approach**:

**Metrics** (Micrometer):
```java
// Custom metrics in OrderSearchService
@Timed(value = "order.search", description = "Time taken to search orders")
public List<SearchResult> searchByEmail(String email) {
    meterRegistry.counter("order.search.requests").increment();
    // ... search logic ...
    meterRegistry.summary("order.search.results").record(results.size());
    meterRegistry.summary("order.search.confidence").record(avgConfidence);
}
```

**Distributed Tracing** (OpenTelemetry):
```java
// Automatic instrumentation via Spring Boot starter
// Manual spans for fuzzy matching logic
@WithSpan("fuzzy-email-match")
public double calculateConfidence(String search, String stored) {
    Span.current().setAttribute("search.email.length", search.length());
    // ... calculation ...
    return confidence;
}
```

**Structured Logging** (Logback):
```xml
<!-- logback-spring.xml -->
<appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="net.logstash.logback.encoder.LogstashEncoder">
        <includeMdcKeyName>traceId</includeMdcKeyName>
        <includeMdcKeyName>spanId</includeMdcKeyName>
        <fieldNames>
            <timestamp>@timestamp</timestamp>
        </fieldNames>
    </encoder>
</appender>
```

**PII Masking**:
```java
// Custom converter to mask PII in logs
public class PiiMaskingConverter extends ClassicConverter {
    public String convert(ILoggingEvent event) {
        return event.getMessage().replaceAll(EMAIL_PATTERN, "***@***.***");
    }
}
```

**Dependencies**:
```gradle
implementation 'org.springframework.boot:spring-boot-starter-actuator'
implementation 'io.micrometer:micrometer-registry-prometheus'
implementation 'io.opentelemetry:opentelemetry-api'
implementation 'io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter'
implementation 'net.logstash.logback:logstash-logback-encoder:7.4'
```

**Alternatives Considered**:
- Spring Cloud Sleuth: Deprecated in favor of OpenTelemetry
- Custom metrics framework: Reinventing wheel; Micrometer is industry standard
- Log4j2: Logback more common in Spring Boot ecosystem

---

### 5. PII Encryption Strategy

**Decision**: Jasypt Spring Boot integration with AES-256-GCM encryption

**Rationale**:
- Jasypt: Battle-tested, Spring Boot-friendly encryption library
- AES-256-GCM: Strong encryption with authenticated encryption mode (prevents tampering)
- Field-level encryption: Encrypt PII columns before persistence
- Key rotation support via externalized configuration
- Transparent to application logic via custom Cassandra converters

**Implementation Approach**:

**Configuration**:
```yaml
# application.yml
jasypt:
  encryptor:
    algorithm: PBEWithHMACSHA512AndAES_256
    iv-generator-classname: org.jasypt.iv.RandomIvGenerator
    password: ${ENCRYPTION_KEY}  # Externalized via environment variable
```

**Entity-level Encryption**:
```java
@Table("booking_orders")
public class HotelBookingOrder {
    @PrimaryKey
    private UUID orderId;
    
    @Encrypted  // Custom annotation
    @Column("customer_email_encrypted")
    private String customerEmail;
    
    @Encrypted
    @Column("guest_names_encrypted")
    private List<String> guestNames;
    
    // Unencrypted for search (but never logged)
    @Column("customer_email_searchable")
    private String customerEmailSearchable;
}

// Custom Cassandra converter
@WritingConverter
public class EncryptingConverter implements Converter<String, String> {
    @Autowired
    private StringEncryptor encryptor;
    
    public String convert(String source) {
        return encryptor.encrypt(source);
    }
}
```

**Key Management**:
- Development: Environment variable `ENCRYPTION_KEY`
- Production: AWS Secrets Manager, Azure Key Vault, or HashiCorp Vault
- Key rotation: Dual-write pattern (old + new key) during transition

**Dependencies**:
```gradle
implementation 'com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.5'
```

**Alternatives Considered**:
- Spring Security Crypto: Less feature-rich than Jasypt, no Spring Boot auto-configuration
- Cassandra-level encryption: Requires enterprise Cassandra or custom setup
- Application-level manual encryption: Error-prone, Jasypt handles edge cases
- Database-level TDE (Transparent Data Encryption): Not available in open-source Cassandra

---

### 6. Spring Boot 3.5.7 Best Practices

**Decision**: Follow Spring Boot 3.5.7 conventions with Java 21 features

**Key Patterns**:
- **Virtual Threads**: Use for I/O-bound operations (Cassandra queries)
  ```yaml
  spring:
    threads:
      virtual:
        enabled: true
  ```
- **Record DTOs**: Use Java records for immutable data transfer objects
  ```java
  public record SearchRequest(String email, String customerId) {}
  public record SearchResult(UUID orderId, String customerEmail, 
                             LocalDateTime bookingStart, int confidenceScore) {}
  ```
- **@Configuration Properties**: Type-safe configuration
  ```java
  @ConfigurationProperties(prefix = "hotel-demo.search")
  public record SearchConfig(int fuzzyThreshold, int maxResults) {}
  ```
- **Problem Details**: RFC 7807 error responses
  ```java
  @ControllerAdvice
  public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
      @ExceptionHandler(OrderSearchException.class)
      public ProblemDetail handleSearchException(OrderSearchException ex) {
          return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
      }
  }
  ```

**Build Configuration**:
```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.5.7'
    id 'io.spring.dependency-management' version '1.1.4'
    id 'jacoco'
    id 'checkstyle'
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-cassandra'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    // ... other dependencies from above research tasks
}
```

---

### 7. Test Data Generation Strategy

**Decision**: Testcontainers + Java Faker for realistic test data

**Rationale**:
- Testcontainers: Spin up real Cassandra in Docker for integration tests
- Java Faker: Generate realistic customer data (names, emails, addresses)
- Repeatable: Seeded random generation for consistent test runs
- Constitutional requirement: Script to generate 10 rows of fake data

**Implementation**:
```java
// Integration test setup
@Testcontainers
@SpringBootTest
class OrderSearchIntegrationTest {
    @Container
    static CassandraContainer<?> cassandra = new CassandraContainer<>("cassandra:4.1")
        .withInitScript("schema.cql");
}

// Data generation script
public class TestDataGenerator {
    private static final Faker faker = new Faker(new Random(42)); // Seeded
    
    public static List<HotelBookingOrder> generateOrders(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> new HotelBookingOrder(
                UUID.randomUUID(),
                faker.internet().emailAddress(),
                faker.date().past(365, TimeUnit.DAYS),
                faker.company().name() + " Hotel",
                // ... other fields
            ))
            .toList();
    }
}
```

**Makefile Target**:
```makefile
.PHONY: generate-test-data
generate-test-data: ## Generate 10 rows of fake data and insert into Cassandra
	@echo "Generating test data..."
	./gradlew generateTestData
```

**Dependencies**:
```gradle
testImplementation 'org.testcontainers:testcontainers:1.19.3'
testImplementation 'org.testcontainers:cassandra:1.19.3'
testImplementation 'com.github.javafaker:javafaker:1.0.2'
```

---

## Technology Stack Summary

### Core Framework
- **Spring Boot**: 3.5.7
- **Java**: 21 (with virtual threads, records, pattern matching)
- **Build Tool**: Gradle 8.5+

### Data & Storage
- **Database**: Apache Cassandra 4.1
- **Driver**: Spring Data Cassandra 4.x
- **Connection Pooling**: Built into DataStax driver

### Search & Matching
- **Fuzzy Matching**: Apache Commons Text (LevenshteinDistance)
- **Indexing**: Cassandra SASI (SSTable Attached Secondary Index)

### Security & Encryption
- **Field Encryption**: Jasypt Spring Boot Starter 3.0.5
- **Algorithm**: AES-256-GCM
- **Key Management**: Externalized via environment variables

### Observability
- **Metrics**: Micrometer + Prometheus registry
- **Tracing**: OpenTelemetry + Spring Boot instrumentation
- **Logging**: Logback with Logstash JSON encoder
- **Monitoring**: Spring Boot Actuator

### Testing
- **Unit Tests**: JUnit 5 + Mockito
- **Integration Tests**: Spring Boot Test + Testcontainers
- **Coverage**: JaCoCo (target: 95% line coverage)
- **Test Data**: Java Faker

### Code Quality
- **Static Analysis**: Checkstyle, PMD, SpotBugs
- **Formatting**: Google Java Format or Spring conventions
- **Dependency Security**: OWASP Dependency-Check

### Build & Development
- **Build Interface**: Makefile (delegates to Gradle)
- **Containerization**: Docker + Docker Compose
- **CI/CD**: GitHub Actions

### MCP Integration
- **Protocol**: Model Context Protocol (JSON-RPC 2.0)
- **Tool Type**: MCP Tool (not Resource)
- **SDK**: Custom Spring Boot integration or community library

---

## Performance Optimization Strategies

### Database Query Optimization
1. **SASI Index**: Reduce full table scan to prefix-matched candidates
2. **Limit Candidate Set**: Query top 1000 matching prefixes, then fuzzy match in-memory
3. **Cassandra Tuning**: Appropriate read consistency level (LOCAL_ONE for demo)
4. **Connection Pooling**: Configure optimal pool size for virtual threads

### Fuzzy Matching Optimization
1. **Early Termination**: Skip candidates with >10% length difference
2. **Threshold Pruning**: Use Apache Commons Text threshold parameter
3. **Parallel Processing**: Use Java parallel streams for independent calculations
4. **Caching**: Cache distance calculations for repeated searches (optional)

### JVM Optimization
1. **Virtual Threads**: Enable for I/O operations (Cassandra, observability)
2. **GC Tuning**: Use G1GC with appropriate heap sizes
3. **Ahead-of-Time Compilation**: Consider GraalVM native image for startup time (optional)

---

## Risk Mitigation

### Technical Risks
1. **Cassandra Learning Curve**: Mitigated by comprehensive documentation and examples
2. **Fuzzy Match Performance**: Mitigated by SASI index pre-filtering and threshold optimization
3. **PII Logging**: Mitigated by custom log masking and comprehensive testing
4. **MCP Protocol Complexity**: Mitigated by following spec and implementing comprehensive contract tests

### Constitutional Compliance Risks
1. **Test Coverage < 95%**: Mitigated by TDD workflow and JaCoCo enforcement
2. **Documentation Lag**: Mitigated by PR checklist requiring README updates
3. **Security Vulnerabilities**: Mitigated by OWASP checks in CI/CD pipeline
4. **Code Quality**: Mitigated by Checkstyle/PMD/SpotBugs in build process

---

## Next Steps (Phase 1)

1. ✅ Research complete - All technical unknowns resolved
2. ⏳ Create data-model.md with entity relationships and validation rules
3. ⏳ Generate MCP tool contract in contracts/mcp-tool-schema.json
4. ⏳ Create quickstart.md with step-by-step setup instructions
5. ⏳ Update agent context with technology decisions
6. ⏳ Re-verify Constitution Check with design decisions

**Research Phase Complete**: All NEEDS CLARIFICATION items resolved. Proceeding to Phase 1: Design & Contracts.
