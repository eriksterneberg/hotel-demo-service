# Data Model: Email-Based Hotel Order Search

**Date**: November 1, 2025  
**Feature**: 001-email-order-search  
**Purpose**: Define data entities, relationships, validation rules, and state transitions

## Entity Definitions

### 1. HotelBookingOrder (Primary Entity)

**Purpose**: Represents a customer's hotel reservation with complete booking details

**Cassandra Table**: `hotel_demo.booking_orders`

#### Fields

| Field Name | Type | Required | Encrypted | Indexed | Description |
|------------|------|----------|-----------|---------|-------------|
| `orderId` | UUID | Yes | No | PK | Unique order identifier |
| `customerEmail` | String | Yes | No | SASI | Searchable email (not logged) |
| `customerEmailEncrypted` | String | Yes | Yes | No | AES-256 encrypted email for storage |
| `bookingDateStart` | LocalDateTime | Yes | No | No | Check-in date and time |
| `bookingDateEnd` | LocalDateTime | Yes | No | No | Check-out date and time |
| `hotelName` | String | Yes | No | No | Name of the hotel |
| `hotelAddress` | String | Yes | No | No | Full address of hotel |
| `roomType` | String | Yes | No | No | Room type (e.g., "Deluxe Suite") |
| `guestNames` | List<String> | Yes | Yes | No | Primary and additional guest names |
| `paymentMethod` | String | Yes | Yes | No | Payment method (encrypted PII) |
| `totalAmount` | BigDecimal | Yes | No | No | Total booking cost |
| `orderStatus` | OrderStatus | Yes | No | Index | Current order status |
| `createdAt` | Instant | Yes | No | Index | Order creation timestamp |

#### Validation Rules

```java
@Table("booking_orders")
public class HotelBookingOrder {
    
    @PrimaryKey
    @NotNull
    private UUID orderId;
    
    @Column("customer_email")
    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String customerEmail;
    
    @Column("customer_email_encrypted")
    @NotBlank
    private String customerEmailEncrypted;
    
    @Column("booking_date_start")
    @NotNull(message = "Check-in date is required")
    @Future(message = "Check-in date must be in the future")  // For new bookings
    private LocalDateTime bookingDateStart;
    
    @Column("booking_date_end")
    @NotNull(message = "Check-out date is required")
    private LocalDateTime bookingDateEnd;
    
    @Column("hotel_name")
    @NotBlank(message = "Hotel name is required")
    @Size(max = 200, message = "Hotel name must not exceed 200 characters")
    private String hotelName;
    
    @Column("hotel_address")
    @NotBlank(message = "Hotel address is required")
    @Size(max = 500, message = "Hotel address must not exceed 500 characters")
    private String hotelAddress;
    
    @Column("room_type")
    @NotBlank(message = "Room type is required")
    @Size(max = 100, message = "Room type must not exceed 100 characters")
    private String roomType;
    
    @Column("guest_names")
    @NotEmpty(message = "At least one guest name is required")
    @Size(max = 10, message = "Maximum 10 guests per booking")
    private List<String> guestNames;
    
    @Column("payment_method")
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    
    @Column("total_amount")
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be positive")
    @Digits(integer = 10, fraction = 2, message = "Invalid amount format")
    private BigDecimal totalAmount;
    
    @Column("order_status")
    @NotNull
    private OrderStatus orderStatus;
    
    @Column("created_at")
    @NotNull
    private Instant createdAt;
    
    // Custom validation
    @AssertTrue(message = "Check-out date must be after check-in date")
    private boolean isValidDateRange() {
        return bookingDateEnd.isAfter(bookingDateStart);
    }
}
```

#### State Transitions

```
OrderStatus Enum:
- PENDING       → Initial state after booking creation
- CONFIRMED     → Payment processed, booking confirmed
- CHECKED_IN    → Guest checked in to hotel
- CHECKED_OUT   → Guest checked out, booking completed
- CANCELLED     → Booking cancelled by customer or system
- NO_SHOW       → Guest did not check in

Valid Transitions:
PENDING → CONFIRMED, CANCELLED
CONFIRMED → CHECKED_IN, CANCELLED, NO_SHOW
CHECKED_IN → CHECKED_OUT
(CANCELLED, CHECKED_OUT, NO_SHOW are terminal states)
```

---

### 2. SearchResult (DTO)

**Purpose**: Represents a single search result with confidence score

**Usage**: Returned by search service, not persisted to database

#### Fields

```java
public record SearchResult(
    @NotNull
    UUID orderId,
    
    @NotBlank
    @Email
    String customerEmail,
    
    @NotNull
    LocalDateTime bookingDateStart,
    
    @NotNull
    LocalDateTime bookingDateEnd,
    
    @NotBlank
    String hotelName,
    
    @NotBlank
    String hotelAddress,
    
    @NotBlank
    String roomType,
    
    @NotEmpty
    List<String> guestNames,
    
    @NotBlank
    String paymentMethod,
    
    @NotNull
    @DecimalMin("0.0")
    BigDecimal totalAmount,
    
    @NotNull
    OrderStatus orderStatus,
    
    @NotNull
    @Min(0) @Max(100)
    Integer confidenceScore  // 0-100% match quality
) {
    // Derived fields
    public long bookingDurationDays() {
        return ChronoUnit.DAYS.between(bookingDateStart, bookingDateEnd);
    }
    
    public boolean isExactMatch() {
        return confidenceScore == 100;
    }
    
    public boolean isFuzzyMatch() {
        return confidenceScore >= 50 && confidenceScore < 100;
    }
}
```

---

### 3. SearchRequest (DTO)

**Purpose**: Input parameters for order search operation

**Usage**: MCP tool input, REST API request body (if applicable)

#### Fields

```java
public record SearchRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    String email,
    
    @NotBlank(message = "Customer ID is required for authentication")
    @Size(min = 1, max = 50, message = "Customer ID must be 1-50 characters")
    String customerId,
    
    @Min(value = 50, message = "Minimum confidence threshold is 50%")
    @Max(value = 100, message = "Maximum confidence threshold is 100%")
    Integer minConfidenceThreshold  // Optional, default 50
) {
    // Constructor with defaults
    public SearchRequest(String email, String customerId) {
        this(email, customerId, 50);  // Default 50% threshold
    }
    
    // Validation
    public SearchRequest {
        if (minConfidenceThreshold == null) {
            minConfidenceThreshold = 50;
        }
    }
}
```

---

### 4. CustomerSession (Domain Model)

**Purpose**: Represents post-sales staff session context for audit and tracking

**Persistence**: Optional - could be stored for audit log or maintained in-memory

#### Fields

```java
public record CustomerSession(
    @NotBlank
    String sessionId,
    
    @NotBlank
    String customerId,  // Post-sales staff identifier
    
    @NotNull
    Instant sessionStart,
    
    Instant sessionEnd,  // Nullable if session active
    
    @Min(0)
    Integer searchCount,  // Number of searches in session
    
    @NotNull
    List<String> searchedEmails  // Email addresses searched (for audit)
) {
    // Constructor for new session
    public static CustomerSession newSession(String customerId) {
        return new CustomerSession(
            UUID.randomUUID().toString(),
            customerId,
            Instant.now(),
            null,
            0,
            new ArrayList<>()
        );
    }
    
    // Add search to session
    public CustomerSession withSearch(String email) {
        List<String> updatedSearches = new ArrayList<>(searchedEmails);
        updatedSearches.add(email);
        return new CustomerSession(
            sessionId,
            customerId,
            sessionStart,
            sessionEnd,
            searchCount + 1,
            updatedSearches
        );
    }
}
```

---

### 5. FuzzyMatchCandidate (Internal Model)

**Purpose**: Intermediate representation during fuzzy matching process

**Usage**: Internal to FuzzyMatchingService, not exposed via API

#### Fields

```java
record FuzzyMatchCandidate(
    HotelBookingOrder order,
    String candidateEmail,
    int levenshteinDistance,
    double rawSimilarity,
    int confidenceScore
) {
    // Factory method
    static FuzzyMatchCandidate from(HotelBookingOrder order, String searchEmail) {
        String candidateEmail = order.getCustomerEmail();
        int distance = calculateLevenshteinDistance(searchEmail, candidateEmail);
        double similarity = calculateSimilarity(searchEmail, candidateEmail, distance);
        int confidence = (int) Math.round(similarity * 100);
        
        return new FuzzyMatchCandidate(
            order,
            candidateEmail,
            distance,
            similarity,
            confidence
        );
    }
    
    boolean meetsThreshold(int minConfidence) {
        return confidenceScore >= minConfidence;
    }
}
```

---

## Relationships

### Entity Relationship Diagram

```
┌─────────────────────────┐
│  HotelBookingOrder      │
│  (Cassandra Table)      │
├─────────────────────────┤
│ PK: orderId             │
│ customerEmail (indexed) │
│ customerEmailEncrypted  │
│ bookingDateStart        │
│ bookingDateEnd          │
│ hotelName               │
│ hotelAddress            │
│ roomType                │
│ guestNames[]            │
│ paymentMethod           │
│ totalAmount             │
│ orderStatus             │
│ createdAt (indexed)     │
└─────────────────────────┘
          │
          │ 1:N (one email can have many orders)
          │
          ▼
┌─────────────────────────┐
│   SearchResult (DTO)    │
├─────────────────────────┤
│ orderId                 │
│ customerEmail           │
│ ... (all order fields)  │
│ confidenceScore         │
└─────────────────────────┘
          ▲
          │ N:1 (search returns multiple results)
          │
┌─────────────────────────┐
│  SearchRequest (DTO)    │
├─────────────────────────┤
│ email                   │
│ customerId              │
│ minConfidenceThreshold  │
└─────────────────────────┘
          │
          │ 1:1 per search operation
          │
          ▼
┌─────────────────────────┐
│  CustomerSession        │
├─────────────────────────┤
│ sessionId               │
│ customerId              │
│ sessionStart            │
│ sessionEnd              │
│ searchCount             │
│ searchedEmails[]        │
└─────────────────────────┘
```

### Relationship Rules

1. **One Email → Many Orders**
   - A single customer email can be associated with multiple booking orders
   - Average: 1-5 orders per email
   - Maximum: 20+ orders per frequent customer

2. **One Search → Many Results**
   - A search request can return 0 to N matching orders
   - Results are filtered by 50% confidence threshold
   - No upper limit on result count (constitutional requirement)

3. **One Session → Many Searches**
   - A customer session can perform multiple searches
   - Session tracks search history for audit purposes
   - Session maintained in-memory (optional persistence)

---

## Data Constraints

### Business Rules

1. **Email Uniqueness**: NOT enforced (same email can have multiple orders)
2. **Order ID Uniqueness**: MUST be unique (UUID primary key)
3. **Date Logic**: `bookingDateEnd` MUST be after `bookingDateStart`
4. **Guest Count**: 1-10 guests per booking
5. **Payment Amount**: MUST be positive, max 10 digits + 2 decimals
6. **Email Format**: MUST conform to RFC 5322 email specification
7. **Status Transitions**: MUST follow defined state machine

### Performance Constraints

1. **Email Length**: Max 100 characters (performance optimization for Levenshtein)
2. **Hotel Name**: Max 200 characters
3. **Hotel Address**: Max 500 characters
4. **Guest List**: Max 10 guests (collection size limit)

### Security Constraints

1. **PII Encryption**: `customerEmailEncrypted`, `guestNames`, `paymentMethod` MUST be encrypted at rest
2. **Searchable Email**: `customerEmail` is NOT encrypted (required for SASI index) but MUST NOT be logged
3. **Key Rotation**: Encryption keys MUST be rotatable without data migration

---

## Cassandra-Specific Considerations

### Primary Key Design

```cql
PRIMARY KEY (order_id)
```

**Rationale**: 
- Order ID is unique identifier
- No composite key needed (no partitioning by customer required)
- Queries are by email (indexed), not by partition key traversal

### Secondary Indexes

```cql
-- SASI index for email search
CREATE CUSTOM INDEX email_sasi_idx ON booking_orders (customer_email)
USING 'org.apache.cassandra.index.sasi.SASIIndex'
WITH OPTIONS = {'mode': 'CONTAINS', 'case_sensitive': 'false'};

-- Regular index for status filtering
CREATE INDEX ON booking_orders (order_status);

-- Regular index for temporal queries
CREATE INDEX ON booking_orders (created_at);
```

**Performance Impact**:
- SASI index adds ~10-20% write overhead
- Read queries benefit from 10-100x speedup vs. full table scan
- Index size: ~10% of table size

### Data Distribution

**Replication Strategy**:
```cql
CREATE KEYSPACE hotel_demo 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
```

**Rationale**: Demo/dev environment uses SimpleStrategy with RF=1. Production would use NetworkTopologyStrategy with RF=3.

---

## Migration Strategy

### Initial Schema

```cql
-- Create keyspace
CREATE KEYSPACE IF NOT EXISTS hotel_demo 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

-- Create table
CREATE TABLE IF NOT EXISTS hotel_demo.booking_orders (
    order_id UUID PRIMARY KEY,
    customer_email TEXT,
    customer_email_encrypted TEXT,
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

-- Create indexes
CREATE CUSTOM INDEX IF NOT EXISTS email_sasi_idx 
ON hotel_demo.booking_orders (customer_email)
USING 'org.apache.cassandra.index.sasi.SASIIndex'
WITH OPTIONS = {'mode': 'CONTAINS', 'case_sensitive': 'false'};

CREATE INDEX IF NOT EXISTS status_idx ON hotel_demo.booking_orders (order_status);
CREATE INDEX IF NOT EXISTS created_idx ON hotel_demo.booking_orders (created_at);
```

### Test Data Generation

```sql
-- Executed by Makefile target: make generate-test-data
-- Uses Java Faker to generate 10 realistic orders
-- See: src/main/resources/cassandra/test-data.cql
```

---

## Validation Summary

| Requirement ID | Data Model Mapping | Validation Strategy |
|----------------|-------------------|---------------------|
| FR-001 | SearchRequest DTO | @NotBlank, @Email annotations |
| FR-002 | SASI index on customer_email | Query optimization with index |
| FR-003 | FuzzyMatchCandidate model | Levenshtein distance ≤ 10% |
| FR-004 | SearchResult confidenceScore | Sorting by confidence DESC |
| FR-005 | SearchResult confidenceScore | @Min(0) @Max(100) validation |
| FR-005a | SearchRequest minConfidenceThreshold | Default 50% filter |
| FR-005b | No pagination in SearchResult | Return all qualifying results |
| FR-006 | Spring Data Cassandra config | Connection validation |
| FR-007 | SearchRequest customerId | @NotBlank validation |
| FR-008 | SearchRequest email | @Email + regex validation |
| FR-011 | PII masking in logs | Custom Logback converter |
| FR-012 | SearchResult comprehensive fields | All 12 fields included |

---

## Next Steps

- ✅ Data model complete with validation rules
- ⏳ Create MCP tool contract schema (contracts/mcp-tool-schema.json)
- ⏳ Generate quickstart.md with setup instructions
- ⏳ Update agent context with data model decisions
