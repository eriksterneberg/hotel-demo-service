<!--
Sync Impact Report:
- Version change: 1.0.1 → 1.1.0
- Modified sections: Added Makefile requirement as standardized interface
- Changes:
  - Documentation Excellence: Updated to require Makefile with standard targets (build, test, run, clean)
  - Development Standards: Added new "Build Interface" section mandating Makefile
  - Quality Gates: Updated CI Build gate to use Makefile
- New principle: Makefile MUST be the primary interface for all common operations
- Templates requiring updates:
  ✅ .specify/templates/plan-template.md (verified alignment with constitution principles)
  ✅ .specify/templates/spec-template.md (verified alignment with requirements)
  ✅ .specify/templates/tasks-template.md (verified alignment with TDD and quality gates)
- Follow-up TODOs: None
-->

# Hotel Demo Service Constitution

**Purpose**: This constitution governs the development of the Hotel Demo Service, a Model Context Protocol (MCP) Java Spring Boot application designed to show case GenAI best practices.

## Core Principles

### I. Documentation Excellence

**All code and features MUST maintain comprehensive, up-to-date documentation.**

- **Docstrings**: Every public class, method, and interface MUST include Javadoc documentation following Spring Boot conventions, explaining purpose, parameters, return values, and exceptions.
- **README.md**: MUST contain:
  - Clear project description and GenAI learning objectives
  - Complete build instructions using Makefile (`make build`)
  - Run instructions using Makefile (`make run`)
  - Test execution instructions using Makefile (`make test`)
  - Available Makefile targets documented in a table or list
  - Contribution guidelines
  - CI/CD status badges (build status, test coverage, code quality)
  - Technology stack and architecture overview
- **Living Documentation**: README.md and relevant documentation MUST be updated as part of every feature implementation. No PR can be merged with outdated documentation.

**Rationale**: For a demo/teaching application, documentation quality directly impacts understanding.

### II. Test-Driven Development (NON-NEGOTIABLE)

**Near 100% test coverage is MANDATORY. Tests MUST be written before implementation.**

- **Coverage Requirement**: Minimum 95% line coverage, 90% branch coverage. Measured via JaCoCo.
- **TDD Workflow**:
  1. Write unit test → Test MUST fail (Red)
  2. Implement minimum code to pass (Green)
  3. Refactor while maintaining passing tests
  4. Commit only when tests pass
- **Test Types Required**:
  - **Unit Tests**: JUnit 5 + Mockito for all service logic, utilities, and domain models
  - **Integration Tests**: Spring Boot Test with `@SpringBootTest` for API endpoints, database interactions, and component integration
  - **Contract Tests**: Verify MCP protocol compliance and API contracts
- **Pre-Commit Gate**: Tests MUST run and pass before any commit. Use Git hooks or CI to enforce.
- **No Skipping**: `@Disabled` or `@Ignore` annotations require explicit justification in PR description and must be resolved before merge.

**Rationale**: High test coverage ensures reliability for learning scenarios. Colleagues need confidence that the demo behaves as documented. TDD enforces testability and design thinking.

### III. Security & Privacy First

**Personally Identifiable Information (PII) MUST be protected with enterprise-grade security.**

- **Encryption at Rest**: Any PII stored in the database (customer names, email addresses, phone numbers, payment information) MUST use strong encryption (AES-256 or equivalent).
  - Use Spring Security Crypto or Jasypt for field-level encryption
  - Encryption keys MUST be externalized (environment variables, secrets manager)
  - Never commit encryption keys to source control
- **No PII in Logs**: Logging statements MUST NOT include PII fields. Specifically prohibited:
  - Usernames, customer names
  - Email addresses
  - Phone numbers
  - Credit card or payment details
  - Physical addresses
  - Any government ID numbers
- **Logging Standards**:
  - Use SLF4J with Logback
  - Implement custom log masking for any objects containing PII
  - Log request IDs and correlation IDs instead of user identifiers
  - Example: `log.info("Processing reservation for requestId={}", requestId)` ✅
  - Example: `log.info("Processing reservation for user={}", user.email)` ❌
- **Security Scanning**: OWASP Dependency-Check MUST run in CI/CD pipeline. Critical vulnerabilities block merge.

**Rationale**: Even in a demo environment, security best practices must be demonstrated. This teaches colleagues production-grade patterns and prevents accidental exposure of real data if the demo evolves.

### IV. Spring Boot Best Practices

**Follow Spring Boot idioms and enterprise patterns consistently.**

- **Layered Architecture**: Clear separation of concerns
  - Controllers: Handle HTTP requests/responses only
  - Services: Business logic and orchestration
  - Repositories: Data access via Spring Data JPA
  - DTOs: API contracts separate from domain models
- **Dependency Injection**: Use constructor injection (required dependencies) over field injection
- **Configuration**: Externalize via `application.yml` / `application.properties`
- **Error Handling**: Global exception handler with `@ControllerAdvice` for consistent API error responses
- **API Design**: Follow RESTful conventions, use appropriate HTTP status codes
- **Validation**: Bean Validation (JSR-380) with `@Valid` annotations

**Rationale**: Demonstrates professional Spring Boot development for teaching purposes.

### V. Educational Clarity

**Code MUST be optimized for learning and comprehension.**

- **Self-Documenting Code**: Variable and method names clearly express intent without requiring comments
- **Example-Driven**: Include working examples for each GenAI integration point
- **Incremental Complexity**: Simple features first, advanced patterns introduced gradually
- **Commented Decisions**: Use inline comments to explain "why" (not "what") for GenAI-specific choices
- **Demo Scenarios**: Each feature MUST include at least one realistic hotel booking scenario

**Rationale**: The primary purpose is teaching. Code clarity trumps clever optimizations.

## Development Standards

### Build Interface

**A Makefile MUST be the primary interface for all common development operations.**

- **Required Targets**: The Makefile MUST include at minimum:
  - `make build` - Build the application (delegates to `./gradlew build`)
  - `make test` - Run all tests (delegates to `./gradlew test`)
  - `make run` - Start the application locally (delegates to `./gradlew bootRun`)
  - `make clean` - Clean build artifacts (delegates to `./gradlew clean`)
  - `make check` - Run all quality checks including tests, coverage, and static analysis
  - `make help` - Display all available targets with descriptions
- **Additional Targets**: Recommended for enhanced workflow:
  - `make docker-build` - Build Docker image
  - `make docker-run` - Run application in Docker
  - `make coverage` - Generate and open coverage report
  - `make format` - Format code according to style guidelines
- **Documentation**: Each target MUST include a comment description. Use `.PHONY` declarations for non-file targets.
- **Simplicity**: Makefile keeps operations consistent and discoverable. New team members run `make help` to see all options.

**Rationale**: Makefile provides a standard, language-agnostic interface that simplifies onboarding and reduces cognitive load. Colleagues learn one command structure (`make <target>`) regardless of underlying build tool.

### Code Quality

- **Static Analysis**: Checkstyle, PMD, and SpotBugs MUST pass with zero violations in default configuration
- **Code Formatting**: Use consistent style (Google Java Style or Spring conventions). Enforce via Gradle plugin.
- **SonarQube**: Maintain "A" rating for maintainability, reliability, and security
- **No Compiler Warnings**: Build with compiler warnings enabled in `build.gradle`. Fix or suppress with justification.

### Version Control

- **Branch Strategy**: Feature branches from `main`, named `feature/###-description` per Spec Kit conventions
- **Commit Messages**: Follow conventional commits format:
  - `feat: add room availability check endpoint`
  - `fix: resolve NPE in booking service`
  - `docs: update README with deployment instructions`
  - `test: add integration tests for payment processing`
- **PR Requirements**: 
  - Link to feature specification
  - Tests included and passing
  - Documentation updated
  - No merge conflicts

### Dependency Management

- **Gradle**: Use Spring Boot Gradle plugin and dependency management plugin for consistent versions
- **Minimal Dependencies**: Only add dependencies that are essential. Justify each addition.
- **Security Updates**: Dependabot enabled. Critical security updates applied within 48 hours.
- **Bill of Materials**: Use Spring Cloud BOM platform for consistent versions across Spring ecosystem

## Quality Gates

**The following gates MUST pass before any merge to `main`:**

1. **Unit Tests**: 95%+ line coverage, all tests passing (`make test`)
2. **Integration Tests**: All API contracts verified, database migrations successful
3. **Security Scan**: No critical or high-severity vulnerabilities (OWASP Dependency-Check)
4. **Code Quality**: SonarQube "A" rating maintained
5. **Documentation**: README.md updated with feature details and Makefile targets
6. **Peer Review**: At least one approved review from team member
7. **CI Build**: All quality checks pass successfully (`make check`)
8. **Constitution Compliance**: Manual verification that feature aligns with all principles

**Pre-Deployment Checklist**:
- [ ] All quality gates passed
- [ ] Demo scenarios tested manually
- [ ] Documentation reviewed for clarity
- [ ] No PII in logs verified
- [ ] Encryption implemented for any new PII fields
- [ ] GitHub badges reflect passing status

## Governance

**This constitution supersedes all other development practices and style guides.**

### Amendment Process

1. **Proposal**: Document proposed change with rationale in GitHub issue
2. **Discussion**: Minimum 3 business days for team feedback
3. **Approval**: Requires consensus from project maintainers
4. **Migration**: For breaking changes, provide migration guide and timeline
5. **Version Update**: Follow semantic versioning for constitution updates
   - MAJOR: Principle removal or incompatible governance change
   - MINOR: New principle added or material expansion
   - PATCH: Clarifications, wording improvements, typo fixes

### Compliance & Enforcement

- **PR Reviews**: All reviewers MUST verify compliance with this constitution
- **Automated Checks**: CI pipeline enforces testable rules (coverage, security, formatting)
- **Complexity Justification**: Any deviation from principles MUST be documented in `plan.md` Complexity Tracking section with approved justification
- **Quarterly Review**: Constitution effectiveness reviewed every quarter, amendments proposed as needed

### Runtime Guidance

For AI assistants and development tools working with this codebase, consult `.github/prompts/` for command-specific guidance aligned with these principles.

**Version**: 1.1.0 | **Ratified**: 2025-11-01 | **Last Amended**: 2025-11-01
