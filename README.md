# Hotel Demo Service - Email Order Search

A Spring Boot microservice demonstrating GenAI integration patterns for searching hotel booking orders by customer email with fuzzy matching capabilities.

## 🎯 Features

- **MCP Tool Interface**: Model Context Protocol tool for AI-assisted customer service (`search_orders_by_email`)
## Features

- ✅ **Exact Email Match Search**: Fast and accurate order retrieval using customer email
- ✅ **MCP Tool Integration**: Exposed as Model Context Protocol tool for AI assistant integration
- ✅ **Comprehensive Order Details**: Returns booking dates, hotel info, guest names, payment details
- ✅ **PII Protection**: Automatic email masking in logs with `PiiMaskingConverter`
- ✅ **Full Observability**: Prometheus metrics, OpenTelemetry tracing, structured logging
- **REST API**: HTTP endpoint for testing (MCP tool is the primary interface)

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

The primary interface is the MCP tool. Here's how it's used:

```java
// MCP Tool invocation (primary interface)
EmailOrderSearchTool mcpTool = ... // injected by Spring
McpToolRequest request = new McpToolRequest(
    "john.doe@example.com",  // email
    "staff-123",             // customerId
    50                       // minConfidenceThreshold (optional)
);
McpToolResponse response = mcpTool.execute(request);
```

### Test via REST API (for development)

```bash
# Search for orders by email (exact match)
curl -X POST http://localhost:8080/api/orders/search \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "customerId": "staff-123",
    "minConfidenceThreshold": 50
  }'
```

**Note**: The REST endpoint is provided for development/testing convenience. The MCP tool is the primary interface for AI integration.

## 📚 Technology Stack

- **Java 25** with Spring Boot 3.5.7
- **Gradle 9.0** build system
- **Apache Cassandra 4.1** for scalable data storage
- **Apache Commons Text** for fuzzy string matching (Levenshtein distance)
- **Jasypt** for PII encryption (AES-256-GCM)
- **Micrometer + OpenTelemetry** for observability
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

```bash
# Run all tests
make test

# Generate coverage report
make coverage

# View coverage report
open build/reports/jacoco/test/html/index.html
```

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

- **PII Masking**: Email addresses are masked in logs (e.g., `***@example.com`)
- **Encryption**: Sensitive fields encrypted at rest using Jasypt
- **Validation**: Input validation using Bean Validation (JSR-380)

## 📊 Observability

- **Metrics**: Available at `http://localhost:8080/actuator/prometheus`
- **Health**: Available at `http://localhost:8080/actuator/health`
- **Tracing**: OpenTelemetry traces with span context propagation

## 🎓 Learning Objectives

This project demonstrates:

1. **Spring Boot Best Practices**: Layered architecture, dependency injection, configuration externalization
2. **NoSQL Data Modeling**: Cassandra schema design with secondary indexes
3. **Observability Patterns**: Metrics, tracing, and structured logging
4. **Security Patterns**: PII encryption, log masking, secure configuration
5. **Test-Driven Development**: Unit tests, integration tests, coverage reporting
6. **GenAI Integration**: MCP tool patterns for AI-assisted customer service

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

**Status**: MVP Complete (User Story 1 - Exact Email Match) ✅

