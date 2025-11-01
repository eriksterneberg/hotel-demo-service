# Implementation Plan: Email-Based Hotel Order Search

**Branch**: `001-email-order-search` | **Date**: November 1, 2025 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/001-email-order-search/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

Implement an MCP (Model Context Protocol) tool for post-sales staff to search hotel booking orders by customer email with fuzzy matching capabilities. The system will support exact email matching and fuzzy matching (up to 10% character errors) using Levenshtein distance algorithm, returning comprehensive order details sorted by confidence score. The solution uses Spring Boot 3.5.7 microservice architecture with Cassandra database backend, supporting ~100K orders with full observability through distributed tracing, metrics, and structured logging.

## Technical Context

**Language/Version**: Java 21 with Spring Boot 3.5.7  
**Primary Dependencies**: Spring Boot Web, Spring Data Cassandra, Apache Commons Text (for Levenshtein distance), MCP Java SDK, Micrometer (metrics), Spring Boot Actuator, OpenTelemetry (tracing), Logback with structured logging  
**Storage**: Apache Cassandra 4.x with materialized views for email indexing  
**Testing**: JUnit 5, Spring Boot Test, Mockito, Testcontainers (for Cassandra integration tests), JaCoCo (coverage)  
**Target Platform**: JVM-based microservice, containerized via Docker, deployable to Kubernetes or local Docker Compose
**Project Type**: Single microservice project with layered architecture (Controller/Service/Repository)  
**Performance Goals**: Sub-2 second search response time, support 10+ concurrent requests, handle 100K order dataset  
**Constraints**: <100ms tracing overhead, <5 second metric staleness, 95% test coverage, zero PII in logs  
**Scale/Scope**: ~100K hotel booking orders, 1-5 orders per customer average (some with 20+), MCP tool interface with comprehensive search functionality

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

**Phase 0 & Phase 1 Complete - Re-evaluation:**

### Documentation Excellence
- [ ] Javadoc for all public classes, methods, and interfaces
- [x] README.md structure defined with:
  - [x] Project description and GenAI learning objectives (planned)
  - [x] Build instructions using Makefile documented in quickstart.md
  - [x] Run instructions using Makefile documented in quickstart.md
  - [x] Test execution using Makefile documented in quickstart.md
  - [x] Available Makefile targets documented in quickstart.md
  - [x] CI/CD status badges (planned)
  - [x] Technology stack overview in plan.md and research.md

**Status**: ⏳ PENDING implementation - Structure designed and documented

### Test-Driven Development
- [x] 95%+ line coverage, 90%+ branch coverage requirement documented
- [x] Unit tests with JUnit 5 + Mockito planned (data-model.md)
- [x] Integration tests with Spring Boot Test + Testcontainers planned
- [x] Contract tests for MCP tool defined (contracts/mcp-tool-schema.json)
- [x] TDD workflow defined in constitution
- [x] Pre-commit git hooks specified in research.md

**Status**: ✅ READY - Test strategy fully designed, ready for implementation

### Security & Privacy First
- [x] PII encryption at rest designed (AES-256-GCM via Jasypt)
- [x] Jasypt Spring Boot integration researched and specified
- [x] Encryption keys externalization strategy defined
- [x] PII log masking designed (custom Logback converter)
- [x] SLF4J + Logback with structured JSON logging specified
- [x] OWASP Dependency-Check integration planned

**Status**: ✅ READY - Security architecture fully designed

### Spring Boot Best Practices
- [x] Layered architecture defined in project structure
- [x] Constructor injection specified in research.md
- [x] Externalized configuration via application.yml planned
- [x] Global exception handler with @ControllerAdvice planned
- [x] MCP tool interface designed (contracts/mcp-tool-schema.json)
- [x] Bean Validation (JSR-380) defined in data-model.md

**Status**: ✅ READY - Architecture fully designed per Spring Boot conventions

### Educational Clarity
- [x] Self-documenting code principles established
- [x] MCP integration examples planned (contracts/mcp-tool-schema.json)
- [x] Inline comments guidance specified
- [x] Realistic hotel booking demo scenarios in spec.md and quickstart.md

**Status**: ✅ READY - Educational approach designed

### Build Interface (Makefile)
- [x] Makefile targets fully documented in quickstart.md
- [x] Required targets: build, test, run, clean, check, help
- [x] Additional targets: docker-build, docker-run, coverage, format
- [x] .PHONY declarations planned
- [x] All targets have comment descriptions

**Status**: ⏳ PENDING implementation - Fully specified, ready to implement

### Code Quality
- [x] Checkstyle, PMD, SpotBugs integration planned in research.md
- [x] Google Java Style formatting specified
- [x] SonarQube "A" rating target documented
- [x] Gradle configuration defined in research.md

**Status**: ⏳ PENDING implementation - Tools and standards defined

### Version Control
- [x] Feature branch strategy: `001-email-order-search` from `main`
- [x] Conventional commits format specified
- [x] PR requirements documented in constitution

**Status**: ✅ PASS - Branch created and following conventions

### Dependency Management
- [x] Gradle with Spring Boot plugin specified (build.gradle template)
- [x] Spring Boot 3.5.7 + Java 21 confirmed
- [x] All dependencies researched and justified (research.md)
- [x] Dependabot requirement documented

**Status**: ✅ READY - Dependency strategy complete

### Quality Gates
- [x] 95%+ unit test coverage target confirmed
- [x] Integration test strategy defined
- [x] Security vulnerability scanning planned (OWASP)
- [x] SonarQube "A" rating requirement documented
- [x] README.md update process defined
- [x] Constitution compliance checklist created

**Status**: ✅ READY - All gates defined and measurable

**Overall Assessment (Post-Phase 1)**: 
- ✅ Phase 0 Research: COMPLETE - All technical decisions made
- ✅ Phase 1 Design: COMPLETE - Data model, contracts, and quickstart created
- ✅ Agent Context: UPDATED - Copilot instructions reflect technology stack
- ⏳ Implementation: READY TO BEGIN - All design artifacts ready for `/speckit.tasks`

**No Constitution violations.** All gates are either complete or properly planned with clear path to implementation.

## Project Structure

### Documentation (this feature)

```text
specs/001-email-order-search/
├── spec.md              # Feature specification (completed)
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (to be created)
├── data-model.md        # Phase 1 output (to be created)
├── quickstart.md        # Phase 1 output (to be created)
├── contracts/           # Phase 1 output (to be created)
│   └── mcp-tool-schema.json  # MCP tool contract definition
├── checklists/          # Quality validation checklists
│   └── requirements.md  # Specification quality checklist (completed)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
# Single Spring Boot microservice project
hotel-demo-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/hotel/demo/
│   │   │       ├── HotelDemoServiceApplication.java
│   │   │       ├── config/
│   │   │       │   ├── CassandraConfig.java
│   │   │       │   ├── ObservabilityConfig.java
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   └── McpConfig.java
│   │   │       ├── mcp/
│   │   │       │   ├── tools/
│   │   │       │   │   └── EmailOrderSearchTool.java
│   │   │       │   └── models/
│   │   │       │       ├── McpToolRequest.java
│   │   │       │       └── McpToolResponse.java
│   │   │       ├── controller/
│   │   │       │   └── OrderSearchController.java (if REST API needed)
│   │   │       ├── service/
│   │   │       │   ├── OrderSearchService.java
│   │   │       │   ├── FuzzyMatchingService.java
│   │   │       │   └── EncryptionService.java
│   │   │       ├── repository/
│   │   │       │   ├── OrderRepository.java
│   │   │       │   └── OrderRepositoryImpl.java
│   │   │       ├── model/
│   │   │       │   ├── entity/
│   │   │       │   │   ├── HotelBookingOrder.java
│   │   │       │   │   └── CustomerSession.java
│   │   │       │   └── dto/
│   │   │       │       ├── SearchRequest.java
│   │   │       │       ├── SearchResult.java
│   │   │       │       └── OrderDetail.java
│   │   │       ├── util/
│   │   │       │   ├── LevenshteinCalculator.java
│   │   │       │   └── PiiMaskingUtil.java
│   │   │       └── exception/
│   │   │           ├── GlobalExceptionHandler.java
│   │   │           └── OrderSearchException.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── logback-spring.xml
│   │       └── cassandra/
│   │           ├── schema.cql
│   │           └── test-data.cql
│   └── test/
│       ├── java/
│       │   └── com/hotel/demo/
│       │       ├── integration/
│       │       │   ├── OrderSearchIntegrationTest.java
│       │       │   └── CassandraIntegrationTest.java
│       │       ├── contract/
│       │       │   └── McpToolContractTest.java
│       │       └── unit/
│       │           ├── service/
│       │           │   ├── OrderSearchServiceTest.java
│       │           │   └── FuzzyMatchingServiceTest.java
│       │           ├── util/
│       │           │   └── LevenshteinCalculatorTest.java
│       │           └── mcp/
│       │               └── EmailOrderSearchToolTest.java
│       └── resources/
│           ├── application-test.yml
│           └── test-data/
│               └── sample-orders.json
├── build.gradle
├── settings.gradle
├── gradle.properties
├── Makefile
├── README.md
├── .gitignore
├── docker-compose.yml
├── Dockerfile
└── .github/
    └── workflows/
        └── ci.yml
```

**Structure Decision**: Single Spring Boot microservice project with layered architecture. This follows Spring Boot best practices with clear separation between MCP tool integration layer, business logic (services), data access (repositories), and domain models (entities/DTOs). The structure supports the demo/educational purpose with clear, navigable packages that showcase GenAI integration patterns.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

**No violations identified.** The implementation plan aligns with all constitution principles:
- Single microservice project (no unnecessary complexity)
- Standard Spring Boot layered architecture
- All security, testing, and documentation requirements planned
- Makefile interface for build operations
- Educational clarity maintained through clear package structure


---

## Planning Summary

### Phase Completion Status

| Phase | Status | Artifacts | Notes |
|-------|--------|-----------|-------|
| **Phase 0: Research** | ✅ COMPLETE | research.md | All technical unknowns resolved, technology stack decided |
| **Phase 1: Design** | ✅ COMPLETE | data-model.md, contracts/, quickstart.md | Data model validated, MCP contract defined, setup guide created |
| **Phase 2: Tasks** | ⏳ NEXT | tasks.md | Ready for `/speckit.tasks` command |

### Deliverables

#### Phase 0: Research (research.md)
- ✅ MCP Java SDK integration approach
- ✅ Fuzzy matching with Apache Commons Text Levenshtein
- ✅ Cassandra schema design with SASI indexes
- ✅ Observability stack (Micrometer, OpenTelemetry, Logback)
- ✅ PII encryption strategy with Jasypt
- ✅ Spring Boot 3.5.7 best practices with Java 21
- ✅ Test data generation with Testcontainers + Java Faker
- ✅ Complete technology stack defined

#### Phase 1: Design (data-model.md, contracts/, quickstart.md)
- ✅ 5 entity definitions with validation rules
- ✅ Entity relationships and cardinality documented
- ✅ Cassandra schema with indexes specified
- ✅ MCP tool contract (JSON schema) created
- ✅ 4 example scenarios in contract
- ✅ Quickstart guide with Makefile targets
- ✅ Troubleshooting section
- ✅ Configuration and observability documentation

#### Agent Context Update
- ✅ GitHub Copilot instructions created
- ✅ Technology stack registered: Java 21 + Spring Boot 3.5.7
- ✅ Framework dependencies documented
- ✅ Database choice (Cassandra) registered

### Key Technical Decisions

| Decision Area | Choice | Rationale |
|---------------|--------|-----------|
| **Language/Framework** | Java 21 + Spring Boot 3.5.7 | Latest stable versions, virtual threads, records, pattern matching |
| **Database** | Apache Cassandra 4.1 | Scalability demo, NoSQL patterns for GenAI training, clarified requirement |
| **Fuzzy Matching** | Apache Commons Text (Levenshtein) | Battle-tested, threshold-based matching, efficient for email-length strings |
| **MCP Integration** | Custom tool with JSON-RPC 2.0 | MCP tool = action-based (vs resource = data stream) |
| **Encryption** | Jasypt + AES-256-GCM | Spring Boot integration, field-level encryption, key rotation support |
| **Observability** | Micrometer + OpenTelemetry + Logback JSON | Full stack per clarification, production-grade patterns |
| **Testing** | JUnit 5 + Mockito + Testcontainers | TDD workflow, real Cassandra in tests, 95% coverage target |
| **Build Interface** | Makefile → Gradle | Constitution requirement, language-agnostic commands |

### Architecture Highlights

**Layered Design:**
```
MCP Tool Layer (EmailOrderSearchTool)
         ↓
Service Layer (OrderSearchService, FuzzyMatchingService)
         ↓
Repository Layer (OrderRepository + Spring Data Cassandra)
         ↓
Data Layer (Cassandra with SASI indexes)
```

**Cross-Cutting Concerns:**
- Security: Jasypt encryption, PII log masking
- Observability: Distributed tracing, metrics, structured logging
- Validation: Bean Validation (JSR-380) at all entry points
- Error Handling: Global exception handler, RFC 7807 Problem Details

### Performance Strategy

1. **Database Optimization:**
   - SASI index for prefix matching (reduce candidate set)
   - In-memory Levenshtein calculation on filtered candidates
   - Connection pooling optimized for virtual threads

2. **Algorithm Optimization:**
   - Early termination for >10% length difference
   - Threshold parameter in Apache Commons Text
   - Parallel stream processing for independent calculations

3. **Observability Overhead:**
   - <100ms tracing overhead (async sampling)
   - <5 second metric staleness (pull-based Prometheus)
   - Structured logging with async appenders

### Security Posture

**PII Protection:**
- ✅ Encryption at rest (customer_email_encrypted, guest_names, payment_method)
- ✅ Searchable email NOT encrypted (required for SASI) but NEVER logged
- ✅ Custom log masking (PiiMaskingConverter in Logback)
- ✅ Key externalization (environment variables, secrets manager)

**Compliance:**
- ✅ Zero PII in logs (enforced by masking + tests)
- ✅ Encryption key rotation strategy defined
- ✅ Audit trail via CustomerSession tracking
- ✅ OWASP Dependency-Check in CI/CD

### Next Steps

**Immediate (Ready Now):**
1. Run `/speckit.tasks` to generate task breakdown
2. Initialize Spring Boot project structure
3. Create Makefile with documented targets
4. Set up Gradle build configuration

**Implementation Phase:**
1. Follow TDD workflow: Write test → Fail → Implement → Pass → Refactor
2. Start with P1 user story (exact email match)
3. Progress to P2 (fuzzy match) then P3 (authentication)
4. Continuous constitution compliance verification

**Quality Gates Before Merge:**
- [ ] 95%+ line coverage, 90%+ branch coverage (JaCoCo)
- [ ] All tests passing (unit + integration + contract)
- [ ] Zero critical/high security vulnerabilities (OWASP)
- [ ] SonarQube "A" rating
- [ ] README.md updated with feature details
- [ ] No PII in logs verified
- [ ] Makefile targets tested and documented
- [ ] CI build passing (`make check`)

### Files Generated by Planning

```
specs/001-email-order-search/
├── plan.md (this file)               # 350+ lines
├── research.md                        # 600+ lines, 7 research tasks
├── data-model.md                      # 450+ lines, 5 entities
├── quickstart.md                      # 550+ lines, complete setup guide
├── contracts/
│   └── mcp-tool-schema.json          # 400+ lines, 4 examples
└── checklists/
    └── requirements.md                # Spec quality checklist (from /speckit.specify)
```

**Total Planning Documentation:** ~2,350 lines across 5 files

### Risk Assessment

| Risk | Mitigation | Status |
|------|-----------|--------|
| Cassandra learning curve | Comprehensive schema design + Testcontainers for testing | ✅ Mitigated |
| Fuzzy match performance | SASI pre-filtering + threshold optimization researched | ✅ Mitigated |
| PII logging | Custom log masking + comprehensive test coverage | ✅ Mitigated |
| Test coverage <95% | TDD workflow + JaCoCo enforcement in build | ✅ Mitigated |
| MCP protocol complexity | Contract defined + example scenarios documented | ✅ Mitigated |

---

## Ready for Implementation

**Prerequisites Met:**
- ✅ All technical unknowns resolved (research.md)
- ✅ Data model fully designed and validated (data-model.md)
- ✅ MCP tool contract specified with examples (contracts/)
- ✅ Setup and operation guide complete (quickstart.md)
- ✅ Agent context updated (copilot-instructions.md)
- ✅ Constitution compliance verified (no violations)
- ✅ Project structure defined (plan.md)
- ✅ Quality gates established

**Next Command:** `/speckit.tasks`

This will generate `tasks.md` with:
- TDD-based task breakdown
- Ordered implementation steps
- Test-first workflow for each task
- Acceptance criteria per task
- Estimated effort and dependencies

**Implementation can begin immediately after task generation.**

