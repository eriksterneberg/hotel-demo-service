# Feature Specification: Email-Based Hotel Order Search

**Feature Branch**: `001-email-order-search`  
**Created**: November 1, 2025  
**Status**: Draft  
**Input**: User description: "Create a `Search Orders by Email` MCP tool where the post-sales staff can do a fuzzy match search on the email used in hotel bookings. Allow for maybe 10% spelling errors (implement any way you want), return ordered by descending confidence"

## Clarifications

### Session 2025-11-01

- Q: What minimum order information must be returned in search results for post-sales staff to effectively identify the correct customer booking? → A: Comprehensive order details including Order ID, customer email, booking dates, hotel name and address, room type, check-in/out times, guest names, payment method, and total amount
- Q: What database technology should be used for storing hotel booking orders? → A: Cassandra
- Q: What is the maximum number of search results to return, and what minimum confidence threshold should filter out low-quality matches? → A: Return all matches above 50% confidence (no limit on result count)
- Q: What observability capabilities (logging, metrics, tracing) should the system provide? → A: Full observability stack with distributed tracing, detailed metrics, and log aggregation
- Q: What is the expected order of magnitude for total orders in the system and typical orders per customer email? → A: Medium scale: ~100K total orders, 1-5 orders per customer average, some customers with 20+ orders

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Find Customer Orders by Exact Email (Priority: P1)

Post-sales staff member needs to look up a customer's hotel booking orders using the customer's email address when the email is spelled correctly.

**Why this priority**: This is the core functionality - exact email matching is the foundation for all search capabilities and the most common use case (90%+ of searches will be exact matches).

**Independent Test**: Can be fully tested by providing a known customer email and verifying the system returns all associated hotel booking orders. Delivers immediate value for standard customer service inquiries.

**Acceptance Scenarios**:

1. **Given** a customer has placed hotel booking orders with email "john.doe@example.com", **When** post-sales staff searches for "john.doe@example.com", **Then** system returns all orders associated with that email in descending confidence order (100% match)
2. **Given** multiple customers have placed orders, **When** post-sales staff searches for a specific email, **Then** system returns only orders matching that email, not other customers' orders
3. **Given** a customer has no orders in the system, **When** post-sales staff searches for that customer's email, **Then** system returns an empty result set with appropriate message

---

### User Story 2 - Find Orders with Fuzzy Email Match (Priority: P2)

Post-sales staff member needs to find customer orders when the customer provides their email verbally or the email may have minor typos (up to 10% character errors).

**Why this priority**: Enhances user experience by handling common real-world scenarios like typos, mishearings, or customer recall errors. Builds upon P1 exact matching.

**Independent Test**: Can be tested independently by providing emails with intentional typos (1-2 characters off) and verifying the system returns correct orders with confidence scores. Delivers value for situations where exact email is unknown.

**Acceptance Scenarios**:

1. **Given** a customer has orders with email "jane.smith@example.com", **When** post-sales staff searches for "jane.smth@example.com" (1 missing character), **Then** system returns the customer's orders with confidence score indicating ~90% match
2. **Given** a customer email "support@hotel.com", **When** post-sales staff searches for "suport@hotel.com" (1 character typo), **Then** system returns orders with appropriate confidence score
3. **Given** an email with >10% character errors, **When** post-sales staff searches, **Then** system either returns no results or results with very low confidence scores (below 50% actionable threshold)
4. **Given** fuzzy search returns multiple potential matches, **When** results are displayed, **Then** orders are sorted by descending confidence score (highest confidence first)

---

### User Story 3 - Authenticate Customer ID for Search (Priority: P3)

Post-sales staff member provides their customer ID to access the order search functionality.

**Why this priority**: Security and audit capability. In a production system this would be OAuth, but for demo purposes we implement basic ID-based access. This is lower priority as it's a simplified demo requirement.

**Independent Test**: Can be tested by attempting searches with and without customer ID input, verifying the system requires this input before processing searches.

**Acceptance Scenarios**:

1. **Given** post-sales staff has a valid customer ID, **When** they provide the ID and search for an email, **Then** system processes the search and returns results
2. **Given** post-sales staff provides a customer ID, **When** they perform multiple searches, **Then** system maintains their session context without requiring ID re-entry

---

### Edge Cases

- What happens when an email address contains special characters (e.g., plus addressing: "user+tag@example.com")?
- How does system handle searches with extremely long email addresses (>100 characters)?
- What happens when database connection fails during a search?
- How does system handle concurrent searches from multiple staff members?
- What happens when searching for an email that partially matches multiple customers (ambiguous fuzzy matches)?
- How does system handle non-standard email formats or malformed email input?
- What happens when a customer has changed their email but old orders still reference the previous email?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide an MCP tool endpoint that accepts an email address as search input
- **FR-002**: System MUST perform exact match search on email addresses in hotel booking orders
- **FR-003**: System MUST perform fuzzy match search allowing up to 10% character errors (calculated as edit distance relative to string length)
- **FR-004**: System MUST return search results ordered by descending confidence score (exact matches first, then fuzzy matches by similarity)
- **FR-005**: System MUST calculate and return confidence scores for each matched order (0-100% scale)
- **FR-005a**: System MUST return all matches that meet or exceed 50% confidence threshold
- **FR-005b**: System MUST NOT limit the number of results returned (all qualifying matches above threshold must be included)
- **FR-006**: System MUST connect to a Cassandra database containing hotel booking orders
- **FR-007**: System MUST accept customer ID as input parameter (for demo authentication purposes)
- **FR-008**: System MUST validate email input format before processing search
- **FR-009**: System MUST handle empty search results gracefully with appropriate messaging
- **FR-010**: System MUST handle database connection errors with appropriate error responses
- **FR-011**: System MUST NOT log customer email addresses in plain text (PII protection)
- **FR-012**: System MUST return comprehensive hotel booking order details including order ID, customer email, booking dates, hotel name and address, room type, check-in and check-out times, guest names, payment method, and total amount
- **FR-013**: System MUST return confidence score with each search result indicating match quality
- **FR-014**: System MUST provide distributed tracing capabilities to track request flows across system components
- **FR-015**: System MUST collect and expose detailed metrics including search request count, search latency (p50, p95, p99), error rates, confidence score distribution, and database query performance
- **FR-016**: System MUST implement structured log aggregation with searchable fields for debugging and audit purposes
- **FR-017**: System MUST emit trace context for correlation across logs, metrics, and traces

### Key Entities *(include if feature involves data)*

- **Hotel Booking Order**: Represents a customer's hotel reservation including order identifier, customer email (encrypted in storage), booking dates (check-in and check-out times), hotel details (name and full address), room type, guest names (primary and additional guests), payment method, total amount charged, order status, and booking timestamp
- **Search Result**: Represents a matched order with confidence score, containing complete order details and match quality metric
- **Customer Session**: Represents the post-sales staff's session context including customer ID for demo authentication
- **Data Scale**: System designed to handle approximately 100,000 total orders with typical customers having 1-5 orders, while some frequent customers may have 20+ orders

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Post-sales staff can find customer orders using exact email match in under 2 seconds
- **SC-002**: System successfully returns fuzzy matches for emails with up to 10% character errors with confidence scores indicating match quality
- **SC-003**: Search results are consistently ordered by confidence score (descending) with exact matches appearing first
- **SC-004**: System maintains 100% data security compliance by not logging PII in plain text
- **SC-005**: 95% of exact email searches return results within 1 second under normal load conditions
- **SC-006**: Post-sales staff can successfully complete order lookup workflow (enter email, review results, identify correct order) in under 30 seconds for 90% of searches
- **SC-007**: System handles at least 10 concurrent search requests without performance degradation
- **SC-008**: Zero customer emails are exposed in system logs or error messages
- **SC-009**: Distributed traces capture end-to-end request flows with <100ms tracing overhead
- **SC-010**: System metrics are collected and queryable with <5 second staleness
- **SC-011**: Log aggregation system indexes and makes logs searchable within 10 seconds of emission
- **SC-012**: System maintains sub-2 second search performance with up to 100,000 orders in database
- **SC-013**: Search performance remains consistent for customers with 20+ orders (no degradation compared to customers with 1-5 orders)

## Assumptions

- Cassandra database will contain hotel booking orders with customer email as a searchable field
- System is designed to handle medium-scale data volumes (~100K orders) with typical customers having 1-5 orders and some having 20+ orders
- Fuzzy matching algorithm will use edit distance (Levenshtein distance or similar) to calculate similarity
- 10% error threshold means for a 20-character email, up to 2 character differences are acceptable
- Customer ID authentication is simplified for demo purposes and does not require validation against an auth service
- Post-sales staff have appropriate permissions to access customer order data
- Email addresses are stored in encrypted format in the Cassandra database
- Search results will include sufficient order details for staff to identify and assist customers
- System will run as a standalone microservice with Cassandra database connectivity
- Performance targets assume Cassandra database with appropriate indexing strategy for email searches optimized for medium-scale data
- MCP tool follows standard Model Context Protocol patterns for tool exposure
- Observability infrastructure (tracing backend, metrics storage, log aggregation) is available for the system to integrate with
