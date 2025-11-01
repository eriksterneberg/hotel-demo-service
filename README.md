# Hotel Demo Service - Email Order Search

A Spring Boot microservice demonstrating GenAI integration patterns for searching hotel booking orders by customer email.

## 🎯 Features

- ✅ **Exact Email Match Search**: Fast and accurate order retrieval using customer email
- ✅ **MCP Tool Integration**: Exposed as Model Context Protocol tool for AI assistant integration
- ✅ **Comprehensive Order Details**: Returns booking dates, hotel info, guest names, payment details
- ✅ **PII Protection**: Automatic email masking in logs with `PiiMaskingConverter`
- ✅ **Field-Level Encryption**: Jasypt encryption for sensitive customer data (AES-256-GCM)
- ✅ **Bean Configuration Testing**: Integration tests to catch Spring context errors
- ✅ **Full Observability**: Prometheus metrics, OpenTelemetry tracing, structured logging
- ✅ **REST API**: HTTP endpoint for testing (MCP tool is the primary interface)

## 🚀 Quick Start

### Prerequisites

- Java 25
- Docker & Docker Compose
- Make

### Build and Run

```bash
# 1. Start Cassandra database
make db-setup

# 2. Build the project
make build

# 3. Run the application
make run
```

The service will be available at `http://localhost:8080`

### Use the MCP Tool

The primary interface is the MCP tool, which can be invoked by AI assistants like Claude via the Model Context Protocol:

**Tool Name**: `search_orders_by_email`

**Parameters**:
- `email` (required): Customer email address to search for
- `customerId` (required): ID of the staff member performing the search
- `minConfidenceThreshold` (optional): Minimum confidence score (0-100), defaults to 70

**Example Usage** (via AI assistant):
```
Find hotel orders for john.doe@example.com
```

The AI assistant will automatically call the tool and format the results for you.

### Test via REST API (for development)

```bash
# Search for orders by email (exact match)
curl -X POST http://localhost:8080/api/orders/search \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "customerId": "staff-123"
  }'

# Example response
{
  "results": [
    {
      "orderId": null,
      "customerEmail": "john.doe@example.com",
      "bookingDateStart": "2025-12-24T16:00:00",
      "bookingDateEnd": "2025-12-27T12:00:00",
      "hotelName": "Grand Plaza Hotel",
      "hotelAddress": "123 Main Street, New York, NY 10001, USA",
      "roomType": "Deluxe Suite",
      "guestNames": ["John Doe", "Jane Doe"],
      "paymentMethod": "Credit Card - Visa",
      "totalAmount": 899.99,
      "orderStatus": "CONFIRMED",
      "confidenceScore": 100,
      "exactMatch": true,
      "fuzzyMatch": false
    }
  ],
  "searchMetadata": {
    "searchEmail": "***@example.com",
    "resultCount": 1,
    "minConfidenceThreshold": 70,
    "executionTimeMs": 52,
    "exactMatchCount": 1,
    "fuzzyMatchCount": 0
  }
}
```

**Note**: The REST endpoint is provided for development/testing convenience. The MCP tool is the primary interface for AI integration.

## 📚 Technology Stack

- **Java 25** with Spring Boot 3.5.7
- **Gradle 9.0** build system
- **Apache Cassandra 4.1** for scalable data storage
- **Spring Data Cassandra** for database integration
- **Jasypt** for PII encryption (AES-256-GCM)
- **MCP Java SDK** for Model Context Protocol integration
- **Micrometer + OpenTelemetry** for observability
- **Logback** with structured JSON logging
- **JUnit 5 + Mockito** for testing

## 🏗️ Architecture

```
┌─────────────────┐
│ MCP Tool        │ (EmailOrderSearchTool - PRIMARY INTERFACE)
└────────┬────────┘
         │
┌────────▼────────┐
│ REST API        │ (OrderSearchController - for testing)
└────────┬────────┘
         │
┌────────▼────────┐
│ Service Layer   │ (OrderSearchService)
└────────┬────────┘
         │
┌────────▼────────┐
│ Repository      │ (OrderRepository)
└────────┬────────┘
         │
┌────────▼────────┐
│ Cassandra DB    │ (with SASI indexes)
└─────────────────┘
```

### MCP Tool Pattern

The `EmailOrderSearchTool` implements the Model Context Protocol (MCP) tool pattern:
- **Tool Name**: `search_orders_by_email`
- **Tool Version**: `1.0.0`
- **Input Schema**: Defined in `specs/001-email-order-search/contracts/mcp-tool-schema.json`
- **Output Schema**: Returns `McpToolResponse` with results and metadata
- **Usage**: Designed for AI agents to assist post-sales staff with customer queries

## 🧪 Testing

The project includes comprehensive test coverage:

- **Unit Tests**: Service layer logic, encryption, validation
- **Integration Tests**: Spring context loading, bean configuration
- **Component Tests**: DTO serialization, entity mapping

```bash
# Run all tests
make test

# Generate coverage report
make coverage

# View coverage report
open build/reports/jacoco/test/html/index.html
```

### Key Integration Tests

- `BeanConfigurationTest`: Catches Spring bean configuration errors (e.g., duplicate beans without @Primary)
- `EncryptionServiceTest`: Validates Jasypt encryption/decryption
- `OrderSearchServiceTest`: Tests order search logic with mocked repository

## 📋 Makefile Targets

```bash
make help          # Show all available commands
make build         # Build the project
make test          # Run tests
make run           # Run the application
make clean         # Clean build artifacts
make check         # Run code quality checks
make coverage      # Generate test coverage report
make docker-build  # Build Docker image
make docker-run    # Run with Docker Compose
make db-setup      # Start Cassandra and load schema
make db-stop       # Stop Cassandra
```

## 🔒 Security

- **PII Masking**: Email addresses are masked in logs (e.g., `***@example.com`) using custom Logback converter
- **Field-Level Encryption**: Customer emails encrypted at rest using Jasypt (AES-256-GCM with PBKDF2)
- **Secure Configuration**: Encryption password externalized via environment variables
- **Validation**: Input validation using Bean Validation (JSR-380)
- **Bean Isolation**: `@Primary` annotation ensures correct bean selection for encryption service

## 📊 Observability

- **Metrics**: Available at `http://localhost:8080/actuator/prometheus`
- **Health**: Available at `http://localhost:8080/actuator/health`
- **Tracing**: OpenTelemetry traces with span context propagation

## 🎓 Learning Objectives

This project demonstrates:

1. **Spring Boot Best Practices**: Layered architecture, dependency injection, configuration externalization, bean lifecycle management
2. **NoSQL Data Modeling**: Cassandra schema design with partition keys and clustering columns
3. **Observability Patterns**: Metrics, tracing, and structured JSON logging with PII masking
4. **Security Patterns**: Field-level encryption with Jasypt, log masking, secure configuration
5. **Test-Driven Development**: Unit tests, integration tests, bean configuration tests, coverage reporting
6. **GenAI Integration**: MCP tool patterns for AI-assisted customer service
7. **Error Prevention**: Integration tests that catch Spring context errors during CI/CD

## 📖 Documentation

- [Feature Specification](specs/001-email-order-search/spec.md)
- [Implementation Plan](specs/001-email-order-search/plan.md)
- [Data Model](specs/001-email-order-search/data-model.md)
- [Task Breakdown](specs/001-email-order-search/tasks.md)

## 🤝 Contributing

This is a demo project for learning purposes. See `specs/001-email-order-search/` for the complete specification and implementation plan.

## 📝 License

MIT License - See LICENSE file for details

---

## 🐛 Known Limitations

- **Fuzzy Matching**: Not yet implemented (exact email match only)
- **Order ID**: Currently not returned in search results (will be added in future iterations)
- **Pagination**: All matching results returned (consider pagination for production use)

---

**Status**: MVP Complete (User Story 1 - Exact Email Match) ✅  
**Note**: Fuzzy matching capabilities to be implemented in a future iteration

