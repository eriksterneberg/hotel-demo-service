# Tasks: Email-Based Hotel Order Search

**Input**: Design documents from `/specs/001-email-order-search/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/mcp-tool-schema.json

**Organization**: Tasks grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Create Spring Boot 3.5.7 project with Java 21 using Spring Initializr (build.gradle, settings.gradle, gradle.properties)
- [ ] T002 [P] Configure Gradle build with dependencies in build.gradle (Spring Boot Web, Spring Data Cassandra, Apache Commons Text 1.11.0, Micrometer, OpenTelemetry, Logback, Jasypt, JUnit 5, Mockito, Testcontainers)
- [ ] T003 [P] Create Makefile with targets (build, test, run, clean, check, help, docker-build, docker-run, coverage, format) per constitution requirement
- [ ] T004 [P] Configure .gitignore for Java/Gradle/IDE files
- [ ] T005 [P] Create project package structure in src/main/java/com/hotel/demo/ (config/, mcp/, controller/, service/, repository/, model/, util/, exception/)
- [ ] T006 [P] Create test package structure in src/test/java/com/hotel/demo/ (integration/, contract/, unit/)
- [ ] T007 [P] Initialize README.md with project description, build/run/test commands using Makefile, technology stack, and learning objectives
- [ ] T008 [P] Create docker-compose.yml with Cassandra 4.1 service definition for local development

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T009 Create Cassandra schema in src/main/resources/cassandra/schema.cql (keyspace hotel_demo, booking_orders table with SASI index on customer_email)
- [ ] T010 [P] Create application.yml configuration with Cassandra connection settings, observability configuration, and encryption key references
- [ ] T011 [P] Create application-test.yml with Testcontainers Cassandra configuration for integration tests
- [ ] T012 [P] Implement CassandraConfig.java in src/main/java/com/hotel/demo/config/ (Spring Data Cassandra configuration, connection pool settings)
- [ ] T013 [P] Implement SecurityConfig.java with Jasypt encryption configuration (AES-256-GCM, key externalization via environment variables)
- [ ] T014 [P] Implement ObservabilityConfig.java with Micrometer registry, OpenTelemetry tracing setup, and custom metrics
- [ ] T015 [P] Create logback-spring.xml with structured JSON logging, PII masking converter, async appenders
- [ ] T016 [P] Implement PiiMaskingConverter.java in src/main/java/com/hotel/demo/util/ for email/PII log masking
- [ ] T017 [P] Create OrderStatus enum in src/main/java/com/hotel/demo/model/entity/ (PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED, NO_SHOW)
- [ ] T018 [P] Implement GlobalExceptionHandler.java in src/main/java/com/hotel/demo/exception/ with @ControllerAdvice for centralized error handling
- [ ] T019 [P] Create OrderSearchException.java custom exception classes (InvalidEmailException, DatabaseConnectionException, SearchTimeoutException)
- [ ] T020 [P] Create test-data.cql with 10 sample hotel booking orders in src/main/resources/cassandra/ (covering exact match, fuzzy match, multiple orders scenarios)
- [ ] T021 Create HotelDemoServiceApplication.java main application class in src/main/java/com/hotel/demo/
- [ ] T022 Verify Cassandra schema creation and test data loading using Makefile target (make db-setup)

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Find Customer Orders by Exact Email (Priority: P1) 🎯 MVP

**Goal**: Post-sales staff can search for orders using exact email match, returning all associated orders

**Independent Test**: Provide known customer email and verify system returns all orders with 100% confidence score

### Tests for User Story 1 (TDD - Write FIRST, ensure FAIL)

- [ ] T023 [P] [US1] Create HotelBookingOrder entity test in src/test/java/com/hotel/demo/unit/model/HotelBookingOrderTest.java (validation rules, state transitions, date range validation)
- [ ] T024 [P] [US1] Create SearchRequest DTO test in src/test/java/com/hotel/demo/unit/model/SearchRequestTest.java (email validation, customer ID validation, default threshold)
- [ ] T025 [P] [US1] Create SearchResult DTO test in src/test/java/com/hotel/demo/unit/model/SearchResultTest.java (confidence score validation, derived fields)
- [ ] T026 [P] [US1] Contract test for MCP tool exact match in src/test/java/com/hotel/demo/contract/EmailOrderSearchToolExactMatchTest.java (verify input/output schema matches mcp-tool-schema.json, test example 1 "Exact email match")
- [ ] T027 [P] [US1] Integration test for exact email search in src/test/java/com/hotel/demo/integration/ExactEmailSearchIntegrationTest.java (Testcontainers Cassandra, load test data, verify query results)
- [ ] T028 [P] [US1] Unit test for OrderRepository exact match in src/test/java/com/hotel/demo/unit/repository/OrderRepositoryTest.java (verify findByCustomerEmail query)

### Implementation for User Story 1

- [ ] T029 [P] [US1] Create HotelBookingOrder entity in src/main/java/com/hotel/demo/model/entity/HotelBookingOrder.java (all 13 fields per data-model.md, @Table annotation, Bean Validation annotations)
- [ ] T030 [P] [US1] Create SearchRequest DTO in src/main/java/com/hotel/demo/model/dto/SearchRequest.java (email, customerId, minConfidenceThreshold fields with validation)
- [ ] T031 [P] [US1] Create SearchResult record in src/main/java/com/hotel/demo/model/dto/SearchResult.java (all order fields + confidenceScore, derived methods)
- [ ] T032 [P] [US1] Create SearchMetadata record in src/main/java/com/hotel/demo/model/dto/SearchMetadata.java (searchEmail, resultCount, executionTimeMs, exactMatchCount, fuzzyMatchCount)
- [ ] T033 [P] [US1] Create CustomerSession record in src/main/java/com/hotel/demo/model/entity/CustomerSession.java (sessionId, customerId, sessionStart, searchCount, searchedEmails)
- [ ] T034 [US1] Create OrderRepository interface in src/main/java/com/hotel/demo/repository/OrderRepository.java extending CassandraRepository (findByCustomerEmail method)
- [ ] T035 [US1] Implement EncryptionService in src/main/java/com/hotel/demo/service/EncryptionService.java (encrypt/decrypt methods using Jasypt for PII fields)
- [ ] T036 [US1] Implement OrderSearchService in src/main/java/com/hotel/demo/service/OrderSearchService.java (searchByEmailExact method returning List<SearchResult>, maps entities to DTOs with 100% confidence, applies encryption/decryption)
- [ ] T037 [US1] Create OrderSearchController in src/main/java/com/hotel/demo/controller/OrderSearchController.java with POST /api/orders/search endpoint (REST endpoint for testing, delegates to OrderSearchService)
- [ ] T038 [US1] Implement EmailOrderSearchTool in src/main/java/com/hotel/demo/mcp/tools/EmailOrderSearchTool.java (MCP tool implementation, invokes OrderSearchService, formats response per mcp-tool-schema.json)
- [ ] T039 [US1] Create McpToolRequest record in src/main/java/com/hotel/demo/mcp/models/McpToolRequest.java (email, customerId, minConfidenceThreshold fields)
- [ ] T040 [US1] Create McpToolResponse record in src/main/java/com/hotel/demo/mcp/models/McpToolResponse.java (results array, searchMetadata)
- [ ] T041 [US1] Add metrics collection in OrderSearchService (search count, latency histogram, error count using Micrometer)
- [ ] T042 [US1] Add distributed tracing spans in OrderSearchService (OpenTelemetry, span for search operation with attributes)
- [ ] T043 [US1] Add structured logging in OrderSearchService (log search requests with masked email, log result counts, log errors)
- [ ] T044 [US1] Run all User Story 1 tests and verify they pass (make test)
- [ ] T045 [US1] Verify test coverage ≥95% for User Story 1 code (make coverage, check JaCoCo report)

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently - exact email search working end-to-end

---

## Phase 4: User Story 2 - Find Orders with Fuzzy Email Match (Priority: P2)

**Goal**: Enable fuzzy matching for emails with up to 10% character errors using Levenshtein distance, returning results sorted by confidence

**Independent Test**: Provide email with typo (1-2 chars different) and verify system returns correct orders with confidence scores 50-99%

### Tests for User Story 2 (TDD - Write FIRST, ensure FAIL)

- [ ] T046 [P] [US2] Unit test for LevenshteinCalculator in src/test/java/com/hotel/demo/unit/util/LevenshteinCalculatorTest.java (distance calculation, confidence score conversion, 10% threshold validation, edge cases)
- [ ] T047 [P] [US2] Unit test for FuzzyMatchingService in src/test/java/com/hotel/demo/unit/service/FuzzyMatchingServiceTest.java (candidate filtering, confidence scoring, sorting by confidence DESC, threshold application)
- [ ] T048 [P] [US2] Contract test for MCP tool fuzzy match in src/test/java/com/hotel/demo/contract/EmailOrderSearchToolFuzzyMatchTest.java (test example 2 "Fuzzy email match with typo" from mcp-tool-schema.json)
- [ ] T049 [P] [US2] Integration test for fuzzy email search in src/test/java/com/hotel/demo/integration/FuzzyEmailSearchIntegrationTest.java (test various typo patterns: missing char, extra char, wrong char, transposition)
- [ ] T050 [P] [US2] Integration test for multiple fuzzy matches in src/test/java/com/hotel/demo/integration/MultipleFuzzyMatchesIntegrationTest.java (verify sorting by confidence DESC, verify 50% threshold filtering)

### Implementation for User Story 2

- [ ] T051 [P] [US2] Create FuzzyMatchCandidate record in src/main/java/com/hotel/demo/model/FuzzyMatchCandidate.java (order, candidateEmail, levenshteinDistance, rawSimilarity, confidenceScore)
- [ ] T052 [US2] Implement LevenshteinCalculator utility in src/main/java/com/hotel/demo/util/LevenshteinCalculator.java (Apache Commons Text integration, calculate distance and confidence score, apply 10% threshold)
- [ ] T053 [US2] Implement FuzzyMatchingService in src/main/java/com/hotel/demo/service/FuzzyMatchingService.java (findFuzzyMatches method, calls repository for candidates via SASI LIKE query, applies Levenshtein filtering, sorts by confidence)
- [ ] T054 [US2] Update OrderRepository with findByCustomerEmailContaining method in src/main/java/com/hotel/demo/repository/OrderRepository.java (SASI LIKE query for prefix matching)
- [ ] T055 [US2] Update OrderSearchService to integrate FuzzyMatchingService in src/main/java/com/hotel/demo/service/OrderSearchService.java (searchByEmail method calls exact match first, then fuzzy match if needed, combines and sorts results)
- [ ] T056 [US2] Update EmailOrderSearchTool to support fuzzy matching in src/main/java/com/hotel/demo/mcp/tools/EmailOrderSearchTool.java (invokes updated OrderSearchService)
- [ ] T057 [US2] Add fuzzy match metrics in FuzzyMatchingService (fuzzy match count, candidate count, Levenshtein calculation time using Micrometer)
- [ ] T058 [US2] Add tracing for fuzzy match operations in FuzzyMatchingService (span for candidate retrieval, span for Levenshtein calculations)
- [ ] T059 [US2] Update test data with fuzzy match scenarios in src/main/resources/cassandra/test-data.cql (emails with common typo patterns)
- [ ] T060 [US2] Run all User Story 2 tests and verify they pass (make test)
- [ ] T061 [US2] Verify test coverage ≥95% for User Story 2 code (make coverage)
- [ ] T062 [US2] Performance test: verify sub-2 second search with 100K orders dataset (create large test dataset, run performance test)

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently - exact match returns 100% confidence, fuzzy match returns 50-99% confidence, all sorted descending

---

## Phase 5: User Story 3 - Authenticate Customer ID for Search (Priority: P3)

**Goal**: Require customer ID input for all searches, maintain session context for audit

**Independent Test**: Attempt search without customer ID (expect error), then with customer ID (expect success)

### Tests for User Story 3 (TDD - Write FIRST, ensure FAIL)

- [ ] T063 [P] [US3] Unit test for CustomerSession in src/test/java/com/hotel/demo/unit/model/CustomerSessionTest.java (session creation, search count increment, session end logic)
- [ ] T064 [P] [US3] Contract test for missing customer ID in src/test/java/com/hotel/demo/contract/EmailOrderSearchToolAuthTest.java (verify MISSING_CUSTOMER_ID error per mcp-tool-schema.json error codes)
- [ ] T065 [P] [US3] Integration test for customer ID validation in src/test/java/com/hotel/demo/integration/CustomerAuthenticationIntegrationTest.java (test with/without customerId, verify session tracking)
- [ ] T066 [P] [US3] Integration test for session tracking in src/test/java/com/hotel/demo/integration/SessionTrackingIntegrationTest.java (multiple searches in session, verify searchCount, verify searchedEmails list)

### Implementation for User Story 3

- [ ] T067 [US3] Implement SessionService in src/main/java/com/hotel/demo/service/SessionService.java (createSession, getSession, updateSessionWithSearch, endSession methods)
- [ ] T068 [US3] Update SearchRequest validation in src/main/java/com/hotel/demo/model/dto/SearchRequest.java (ensure @NotBlank on customerId enforced)
- [ ] T069 [US3] Update OrderSearchService to use SessionService in src/main/java/com/hotel/demo/service/OrderSearchService.java (create/update session on each search, track searches)
- [ ] T070 [US3] Update GlobalExceptionHandler to handle missing customer ID in src/main/java/com/hotel/demo/exception/GlobalExceptionHandler.java (return MISSING_CUSTOMER_ID error code per contract)
- [ ] T071 [US3] Add session metrics in SessionService (active sessions count, searches per session histogram using Micrometer)
- [ ] T072 [US3] Add audit logging for customer searches in SessionService (log customerId, search email (masked), timestamp using structured logging)
- [ ] T073 [US3] Run all User Story 3 tests and verify they pass (make test)
- [ ] T074 [US3] Verify test coverage ≥95% for User Story 3 code (make coverage)

**Checkpoint**: All user stories should now be independently functional - searches require authentication, sessions track audit trail

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories, final quality checks

- [ ] T075 [P] Create comprehensive integration test in src/test/java/com/hotel/demo/integration/EndToEndSearchTest.java (test all 4 MCP tool examples from mcp-tool-schema.json: exact match, fuzzy match, no results, multiple orders)
- [ ] T076 [P] Implement error handling tests in src/test/java/com/hotel/demo/integration/ErrorHandlingIntegrationTest.java (database connection error, query timeout, invalid email format, confidence threshold validation)
- [ ] T077 [P] Create PII masking verification test in src/test/java/com/hotel/demo/integration/PiiMaskingTest.java (verify emails never appear in logs, verify encryption at rest, verify decryption in responses)
- [ ] T078 [P] Configure code quality tools in build.gradle (Checkstyle with Google Java Style, PMD, SpotBugs, JaCoCo with 95% line coverage enforcement)
- [ ] T079 [P] Create .editorconfig for consistent code formatting
- [ ] T080 [P] Add Gradle task for dependency vulnerability scanning (OWASP Dependency-Check) in build.gradle
- [ ] T081 [P] Update README.md with complete documentation (architecture diagram, MCP tool usage examples, configuration guide, troubleshooting section)
- [ ] T082 [P] Create API documentation in docs/api.md (MCP tool contract, REST endpoints if applicable, error codes, examples)
- [ ] T083 [P] Add Javadoc comments to all public classes and methods (controller, service, repository, model classes)
- [ ] T084 [P] Create Dockerfile for containerization (multi-stage build with Java 21, optimized layers)
- [ ] T085 [P] Update docker-compose.yml with application service (depends on Cassandra, exposes ports, environment variables)
- [ ] T086 [P] Create CI/CD workflow in .github/workflows/ci.yml (build, test, code quality checks, security scan, Docker build)
- [ ] T087 [P] Add badges to README.md (CI status, test coverage, code quality, license)
- [ ] T088 Run full test suite and verify ≥95% coverage (make test && make coverage)
- [ ] T089 Run code quality checks and verify SonarQube "A" rating (make check)
- [ ] T090 Run security vulnerability scan and verify zero critical/high issues (make security-scan)
- [ ] T091 Validate quickstart.md instructions (follow setup steps in clean environment, verify all Makefile targets work)
- [ ] T092 Run performance validation: search latency <2s for 100K orders (make perf-test)
- [ ] T093 Verify observability stack integration (check Micrometer metrics endpoint, verify OpenTelemetry traces, check log aggregation)
- [ ] T094 Final constitution compliance check (verify all quality gates met, update checklist in specs/001-email-order-search/checklists/)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup (Phase 1) completion - BLOCKS all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational (Phase 2) completion - Independent of US2 and US3
- **User Story 2 (Phase 4)**: Depends on Foundational (Phase 2) completion - Builds on US1 OrderSearchService but should be independently testable
- **User Story 3 (Phase 5)**: Depends on Foundational (Phase 2) completion - Independent of US1 and US2
- **Polish (Phase 6)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Integrates with OrderSearchService from US1 but independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Independent of US1/US2

### Within Each User Story

1. Write tests FIRST (marked with TDD comment)
2. Run tests, verify they FAIL
3. Implement code to make tests pass
4. Models/entities before services
5. Services before controllers/MCP tools
6. Core implementation before metrics/tracing/logging
7. Story complete and independently testable before moving to next priority

### Parallel Opportunities

**Phase 1 (Setup)**: Tasks T002, T003, T004, T005, T006, T007, T008 can run in parallel

**Phase 2 (Foundational)**: Tasks T010-T020 can run in parallel (all different files)

**Phase 3 (User Story 1 - Tests)**: Tasks T023-T028 can run in parallel

**Phase 3 (User Story 1 - Models)**: Tasks T029-T033 can run in parallel

**Phase 4 (User Story 2 - Tests)**: Tasks T046-T050 can run in parallel

**Phase 4 (User Story 2 - Utils)**: Tasks T051-T052 can run in parallel

**Phase 5 (User Story 3 - Tests)**: Tasks T063-T066 can run in parallel

**Phase 6 (Polish)**: Tasks T075-T087 can run in parallel (different files, documentation tasks)

**Parallel User Stories**: Once Foundational (Phase 2) is complete, User Stories 1, 2, and 3 can be worked on in parallel by different team members

---

## Parallel Example: User Story 1 Models

```bash
# Launch all model creation tasks together:
Task T029: "Create HotelBookingOrder entity"
Task T030: "Create SearchRequest DTO"
Task T031: "Create SearchResult record"
Task T032: "Create SearchMetadata record"
Task T033: "Create CustomerSession record"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (8 tasks)
2. Complete Phase 2: Foundational (14 tasks) - **CRITICAL BLOCKER**
3. Complete Phase 3: User Story 1 (23 tasks)
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

**Deliverable**: Exact email search with comprehensive order details, 100% confidence score, full observability

### Incremental Delivery

1. Foundation: Setup + Foundational (22 tasks)
2. MVP: User Story 1 (23 tasks) → Test independently → Deploy/Demo ✅
3. Enhanced: User Story 2 (17 tasks) → Test independently → Deploy/Demo ✅
4. Complete: User Story 3 (12 tasks) → Test independently → Deploy/Demo ✅
5. Production-Ready: Polish (20 tasks) → Full validation → Deploy ✅

**Each story adds value without breaking previous stories**

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together (22 tasks)
2. Once Foundational is done:
   - Developer A: User Story 1 (23 tasks)
   - Developer B: User Story 2 (17 tasks) - can start entities/tests
   - Developer C: User Story 3 (12 tasks) - can start session model/tests
3. Stories complete and integrate independently
4. Team collaborates on Polish phase (20 tasks)

---

## Task Statistics

| Phase | Task Count | Can Parallelize | Estimated Effort |
|-------|-----------|-----------------|------------------|
| Phase 1: Setup | 8 | 7 of 8 | 3-4 hours |
| Phase 2: Foundational | 14 | 11 of 14 | 6-8 hours |
| Phase 3: User Story 1 (P1) | 23 | 8 tests, 8 models in parallel | 10-12 hours |
| Phase 4: User Story 2 (P2) | 17 | 5 tests, 2 utils in parallel | 8-10 hours |
| Phase 5: User Story 3 (P3) | 12 | 4 tests in parallel | 4-6 hours |
| Phase 6: Polish | 20 | 13 of 20 | 8-10 hours |
| **TOTAL** | **94 tasks** | **~50% parallelizable** | **39-50 hours** |

**Team of 3**: ~15-20 hours elapsed time if User Stories 1, 2, 3 run in parallel after Foundational phase

---

## Notes

- **[P]** tasks = different files, no dependencies, can run in parallel
- **[Story]** label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- **TDD workflow**: Write test → Fail → Implement → Pass → Refactor
- Verify tests fail before implementing (validates test quality)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Constitution gates enforced at Phase 6 (Polish) before merge
- All file paths are relative to repository root: `src/main/java/...`, `src/test/java/...`

---

## Quality Gates (Pre-Merge Checklist)

- [ ] All 94 tasks completed
- [ ] Test coverage ≥95% line coverage, ≥90% branch coverage (JaCoCo)
- [ ] All tests passing: unit + integration + contract (make test)
- [ ] Code quality: SonarQube "A" rating (make check)
- [ ] Security: Zero critical/high vulnerabilities (OWASP)
- [ ] PII protection: No emails in logs verified (make test, check PiiMaskingTest)
- [ ] Performance: <2s search latency for 100K orders (make perf-test)
- [ ] Observability: Metrics, tracing, and logs validated
- [ ] Documentation: README.md updated, Javadocs complete
- [ ] CI/CD: GitHub Actions workflow passing
- [ ] Makefile: All targets tested and documented
- [ ] Constitution: All gates passed (checklist in specs/001-email-order-search/checklists/)

---

**Ready to begin implementation**: Start with Phase 1 (Setup) tasks T001-T008
