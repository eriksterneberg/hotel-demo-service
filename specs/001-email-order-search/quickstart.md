# Quickstart Guide: Email-Based Hotel Order Search

**Feature**: 001-email-order-search  
**Date**: November 1, 2025  
**Purpose**: Step-by-step guide to set up, build, test, and run the hotel order search MCP tool

## Prerequisites

Before starting, ensure you have the following installed:

- **Java 21** or later ([Download](https://adoptium.net/temurin/releases/?version=21))
- **Docker** and **Docker Compose** ([Download](https://www.docker.com/products/docker-desktop))
- **Git** ([Download](https://git-scm.com/downloads))
- **Make** (typically pre-installed on macOS/Linux, Windows users can use WSL or install via Chocolatey)

Verify installations:
```bash
java -version    # Should show Java 21+
docker --version # Should show Docker 20.10+
git --version    # Should show Git 2.x+
make --version   # Should show GNU Make 3.8+
```

---

## Quick Start (5 Minutes)

### 1. Clone and Navigate to Repository

```bash
git clone https://github.com/eriksterneberg/hotel-demo-service.git
cd hotel-demo-service
git checkout 001-email-order-search
```

### 2. Start Cassandra Database

```bash
make docker-cassandra-start
```

This command:
- Pulls Cassandra 4.1 Docker image (if not already present)
- Starts Cassandra container on port 9042
- Waits for Cassandra to be ready (~30 seconds)
- Creates keyspace and tables from `src/main/resources/cassandra/schema.cql`

**Verify Cassandra is running:**
```bash
docker ps | grep cassandra
# Should show running cassandra container
```

### 3. Generate Test Data

```bash
make generate-test-data
```

This command:
- Generates 10 realistic hotel booking orders using Java Faker
- Inserts orders into Cassandra `hotel_demo.booking_orders` table
- Displays summary of generated data

**Expected output:**
```
Generating 10 test hotel booking orders...
✓ Generated order: john.doe@example.com - Grand Plaza Hotel
✓ Generated order: jane.smith@example.com - Seaside Resort
...
✓ Successfully inserted 10 orders into Cassandra
```

### 4. Build the Application

```bash
make build
```

This command:
- Compiles Java sources
- Runs Checkstyle, PMD, SpotBugs (code quality checks)
- Runs all unit and integration tests
- Generates JaCoCo coverage report
- Creates executable JAR: `build/libs/hotel-demo-service-1.0.0.jar`

**Expected build time:** 2-3 minutes (first build downloads dependencies)

### 5. Run the Application

```bash
make run
```

This command:
- Starts the Spring Boot application
- Exposes MCP tool on port 8080
- Connects to local Cassandra database
- Enables Spring Boot Actuator endpoints

**Verify application started:**
```
INFO  HotelDemoServiceApplication - Started HotelDemoServiceApplication in 3.456 seconds
INFO  MCP tool 'search_orders_by_email' registered successfully
```

Access health check: http://localhost:8080/actuator/health

### 6. Test the MCP Tool

**Example 1: Exact email match**
```bash
curl -X POST http://localhost:8080/mcp/tools/search_orders_by_email \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "customerId": "staff-12345"
  }'
```

**Expected response:**
```json
{
  "results": [
    {
      "orderId": "550e8400-e29b-41d4-a716-446655440000",
      "customerEmail": "john.doe@example.com",
      "bookingDateStart": "2025-12-24T15:00:00Z",
      "bookingDateEnd": "2025-12-27T11:00:00Z",
      "hotelName": "Grand Plaza Hotel",
      "confidenceScore": 100,
      ...
    }
  ],
  "searchMetadata": {
    "resultCount": 1,
    "exactMatchCount": 1,
    "fuzzyMatchCount": 0,
    "executionTimeMs": 45
  }
}
```

**Example 2: Fuzzy email match (with typo)**
```bash
curl -X POST http://localhost:8080/mcp/tools/search_orders_by_email \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jone.doe@example.com",
    "customerId": "staff-12345"
  }'
```

**Expected response:**
```json
{
  "results": [
    {
      "orderId": "550e8400-e29b-41d4-a716-446655440000",
      "customerEmail": "john.doe@example.com",
      "confidenceScore": 95,
      ...
    }
  ],
  "searchMetadata": {
    "resultCount": 1,
    "exactMatchCount": 0,
    "fuzzyMatchCount": 1,
    "executionTimeMs": 123
  }
}
```

---

## Makefile Targets Reference

The project uses a Makefile as the standard interface for all operations. Run `make help` to see all available targets:

### Essential Targets

| Command | Description | Example |
|---------|-------------|---------|
| `make build` | Build the application with all quality checks | `make build` |
| `make test` | Run all tests (unit + integration) | `make test` |
| `make run` | Start the application locally | `make run` |
| `make clean` | Clean build artifacts | `make clean` |
| `make check` | Run all quality checks (tests, coverage, static analysis) | `make check` |
| `make help` | Display all available targets | `make help` |

### Docker Targets

| Command | Description | Example |
|---------|-------------|---------|
| `make docker-build` | Build Docker image | `make docker-build` |
| `make docker-run` | Run application in Docker | `make docker-run` |
| `make docker-cassandra-start` | Start Cassandra container | `make docker-cassandra-start` |
| `make docker-cassandra-stop` | Stop Cassandra container | `make docker-cassandra-stop` |
| `make docker-compose-up` | Start all services (app + Cassandra) | `make docker-compose-up` |
| `make docker-compose-down` | Stop all services | `make docker-compose-down` |

### Development Targets

| Command | Description | Example |
|---------|-------------|---------|
| `make coverage` | Generate and open coverage report | `make coverage` |
| `make format` | Format code according to Google Java Style | `make format` |
| `make generate-test-data` | Generate 10 fake hotel orders | `make generate-test-data` |
| `make watch` | Run tests in watch mode (rerun on file change) | `make watch` |

### CI/CD Targets

| Command | Description | Example |
|---------|-------------|---------|
| `make ci` | Run full CI pipeline locally | `make ci` |
| `make sonar` | Upload analysis to SonarQube | `make sonar` |
| `make security-scan` | Run OWASP Dependency-Check | `make security-scan` |

---

## Directory Structure

```
hotel-demo-service/
├── src/
│   ├── main/
│   │   ├── java/com/hotel/demo/
│   │   │   ├── mcp/tools/           # MCP tool implementations
│   │   │   ├── service/             # Business logic
│   │   │   ├── repository/          # Data access
│   │   │   ├── model/               # Domain models and DTOs
│   │   │   └── config/              # Configuration classes
│   │   └── resources/
│   │       ├── application.yml      # Main configuration
│   │       ├── cassandra/
│   │       │   ├── schema.cql       # Database schema
│   │       │   └── test-data.cql    # Test data insert script
│   │       └── logback-spring.xml   # Logging configuration
│   └── test/
│       ├── java/com/hotel/demo/
│       │   ├── unit/                # Unit tests
│       │   ├── integration/         # Integration tests
│       │   └── contract/            # MCP contract tests
│       └── resources/
│           └── application-test.yml # Test configuration
├── specs/001-email-order-search/
│   ├── spec.md                      # Feature specification
│   ├── plan.md                      # Implementation plan
│   ├── research.md                  # Technical research
│   ├── data-model.md                # Data model documentation
│   ├── quickstart.md                # This file
│   └── contracts/
│       └── mcp-tool-schema.json     # MCP tool contract
├── build.gradle                     # Gradle build configuration
├── Makefile                         # Build interface (USE THIS!)
├── docker-compose.yml               # Multi-container orchestration
├── Dockerfile                       # Application container image
└── README.md                        # Project overview
```

---

## Configuration

### Environment Variables

The application can be configured using environment variables:

| Variable | Description | Default | Example |
|----------|-------------|---------|---------|
| `CASSANDRA_HOST` | Cassandra server hostname | `localhost` | `cassandra.example.com` |
| `CASSANDRA_PORT` | Cassandra server port | `9042` | `9042` |
| `CASSANDRA_KEYSPACE` | Cassandra keyspace name | `hotel_demo` | `hotel_demo_prod` |
| `CASSANDRA_USERNAME` | Cassandra username | (none) | `cassandra_user` |
| `CASSANDRA_PASSWORD` | Cassandra password | (none) | `secure_password` |
| `ENCRYPTION_KEY` | PII encryption key | (none) | `your-256-bit-key-here` |
| `SERVER_PORT` | Application HTTP port | `8080` | `8080` |
| `LOG_LEVEL` | Logging level | `INFO` | `DEBUG` |
| `OTEL_EXPORTER_ENDPOINT` | OpenTelemetry collector endpoint | (none) | `http://otel-collector:4317` |

**Example: Running with custom configuration**
```bash
CASSANDRA_HOST=cassandra-prod \
ENCRYPTION_KEY=my-secret-key \
LOG_LEVEL=DEBUG \
make run
```

### Application Profiles

The application supports Spring profiles for different environments:

- **default**: Development profile (used by `make run`)
- **test**: Test profile (used by `make test`)
- **prod**: Production profile (requires environment variables)

**Activate profile:**
```bash
SPRING_PROFILES_ACTIVE=prod make run
```

---

## Testing

### Run All Tests

```bash
make test
```

This runs:
- Unit tests (JUnit 5 + Mockito)
- Integration tests (Spring Boot Test + Testcontainers)
- Contract tests (MCP tool compliance)

**Expected output:**
```
> Task :test
EmailOrderSearchToolTest > testExactEmailMatch() PASSED
EmailOrderSearchToolTest > testFuzzyEmailMatch() PASSED
OrderSearchServiceTest > testSearchWithConfidenceFiltering() PASSED
...
BUILD SUCCESSFUL in 45s
```

### Run Specific Test Class

```bash
./gradlew test --tests EmailOrderSearchToolTest
```

### Check Test Coverage

```bash
make coverage
```

This generates a JaCoCo HTML report and opens it in your browser.

**Coverage requirements:**
- Line coverage: ≥ 95%
- Branch coverage: ≥ 90%

**View report manually:**
```bash
open build/reports/jacoco/test/html/index.html
```

### Run Integration Tests Only

```bash
./gradlew integrationTest
```

---

## Observability

### Metrics

**Access Prometheus metrics:**
```bash
curl http://localhost:8080/actuator/prometheus
```

**Key metrics:**
- `order_search_requests_total` - Total search requests
- `order_search_duration_seconds` - Search latency histogram
- `order_search_confidence_score` - Confidence score distribution
- `cassandra_queries_total` - Database query count
- `cassandra_query_duration_seconds` - Database query latency

### Distributed Tracing

**View traces (requires OpenTelemetry collector):**

1. Start OpenTelemetry + Jaeger:
```bash
docker-compose -f docker-compose-observability.yml up -d
```

2. Access Jaeger UI: http://localhost:16686

3. Search for service: `hotel-demo-service`

### Logs

**View application logs:**
```bash
tail -f logs/application.log
```

**Structured JSON logs example:**
```json
{
  "@timestamp": "2025-11-01T12:34:56.789Z",
  "level": "INFO",
  "thread": "http-nio-8080-exec-1",
  "logger": "com.hotel.demo.service.OrderSearchService",
  "message": "Search completed",
  "traceId": "550e8400e29b41d4a716446655440000",
  "spanId": "a716446655440000",
  "searchResultCount": 3,
  "executionTimeMs": 67
}
```

**Note:** Email addresses are NEVER logged (PII protection).

### Health Checks

**Application health:**
```bash
curl http://localhost:8080/actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "cassandra": { "status": "UP" },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

---

## Troubleshooting

### Problem: Cassandra connection refused

**Symptoms:**
```
org.springframework.data.cassandra.CassandraConnectionFailureException: 
Connection refused
```

**Solution:**
```bash
# Check if Cassandra is running
docker ps | grep cassandra

# If not running, start it
make docker-cassandra-start

# Wait 30 seconds for Cassandra to initialize

# Verify Cassandra is ready
docker exec -it cassandra cqlsh -e "DESCRIBE KEYSPACES;"
```

### Problem: Build fails with test coverage below threshold

**Symptoms:**
```
Rule violated for bundle hotel-demo-service: 
lines covered ratio is 0.94, but expected minimum is 0.95
```

**Solution:**
```bash
# View coverage report to identify untested code
make coverage

# Add missing tests to increase coverage
# Rerun build
make build
```

### Problem: PII appears in logs

**Symptoms:**
Email addresses or guest names visible in log files

**Solution:**
- This is a **CRITICAL** constitution violation
- Verify custom log masking is configured in `logback-spring.xml`
- Check `PiiMaskingConverter` is applied to all log statements
- Run security audit: `make security-scan`

### Problem: Fuzzy search returns no results

**Symptoms:**
Email with minor typo returns empty results

**Solution:**
```bash
# Check confidence threshold (default 50%)
# Try lowering threshold in request
curl -X POST http://localhost:8080/mcp/tools/search_orders_by_email \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jone.doe@example.com",
    "customerId": "staff-12345",
    "minConfidenceThreshold": 40
  }'

# Verify SASI index exists
docker exec -it cassandra cqlsh -e \
  "DESCRIBE INDEX hotel_demo.email_sasi_idx;"
```

### Problem: Docker Compose fails to start

**Symptoms:**
```
ERROR: Cannot start service cassandra: driver failed programming external 
connectivity on endpoint cassandra: Error starting userland proxy
```

**Solution:**
```bash
# Stop any running containers on port 9042
docker ps -a | grep 9042
docker stop <container_id>

# Clean up Docker
docker system prune

# Restart Docker Desktop

# Try again
make docker-compose-up
```

---

## Next Steps

### For Developers

1. **Explore the codebase:**
   - Start with `src/main/java/com/hotel/demo/mcp/tools/EmailOrderSearchTool.java`
   - Review `OrderSearchService` for business logic
   - Examine `FuzzyMatchingService` for Levenshtein implementation

2. **Add more features:**
   - See `/speckit.tasks` command output for task breakdown
   - Follow TDD workflow (Red → Green → Refactor)
   - Ensure constitution compliance (tests, docs, PII protection)

3. **Run quality checks:**
   ```bash
   make check  # Runs all quality gates
   ```

### For Demos/Presentations

1. **Prepare demo environment:**
   ```bash
   make docker-compose-up  # Start all services
   make generate-test-data  # Create realistic data
   ```

2. **Demo scenarios:**
   - Exact match: Use email from generated test data
   - Fuzzy match: Introduce 1-2 character typo
   - Multiple results: Search for frequent traveler email
   - No results: Use non-existent email

3. **Show observability:**
   - Open Jaeger UI (http://localhost:16686) for traces
   - Display Prometheus metrics (http://localhost:8080/actuator/prometheus)
   - Tail JSON logs to show structured logging

### For Operations/DevOps

1. **Deploy to production:**
   ```bash
   # Build production Docker image
   make docker-build
   
   # Tag and push to registry
   docker tag hotel-demo-service:latest your-registry/hotel-demo-service:1.0.0
   docker push your-registry/hotel-demo-service:1.0.0
   
   # Deploy to Kubernetes (example)
   kubectl apply -f k8s/deployment.yml
   ```

2. **Monitor in production:**
   - Configure OTEL_EXPORTER_ENDPOINT for trace collection
   - Set up Prometheus scraping for metrics
   - Configure log aggregation (ELK, Splunk, Datadog)

3. **Backup Cassandra data:**
   ```bash
   docker exec cassandra nodetool snapshot hotel_demo
   ```

---

## Additional Resources

- **Feature Specification:** [spec.md](spec.md)
- **Implementation Plan:** [plan.md](plan.md)
- **Technical Research:** [research.md](research.md)
- **Data Model:** [data-model.md](data-model.md)
- **MCP Tool Contract:** [contracts/mcp-tool-schema.json](contracts/mcp-tool-schema.json)
- **Constitution:** [../../.specify/memory/constitution.md](../../.specify/memory/constitution.md)
- **MCP Specification:** https://spec.modelcontextprotocol.io/
- **Spring Boot Docs:** https://docs.spring.io/spring-boot/docs/3.5.7/reference/htmlsingle/
- **Apache Cassandra:** https://cassandra.apache.org/doc/latest/

---

## Support

For questions or issues:
1. Check troubleshooting section above
2. Review constitution for compliance requirements
3. Open GitHub issue with reproduction steps
4. Contact: erik.sterneberg@example.com

**Happy coding! 🚀**
