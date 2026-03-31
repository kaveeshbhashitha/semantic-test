## Semantic JAR Change Test Project

This repository is a small **Spring Boot (Java 17)** application designed for generating **multiple JAR builds** while applying **semantic (behavioral) changes** step-by-step.

### What the app does
It exposes a simple Quote API that:
- Calculates totals (discounts, tax, shipping)
- Applies coupon rules
- Computes a risk score and approval decision
- Stores quotes in-memory

### API endpoints
- **POST** `\/api\/quotes`
- **GET** `\/api\/quotes\/{quoteId}`
- **GET** `\/api\/quotes` (list newest first)

### Build command (creates the JAR)
From the project root:

```bash
./mvnw clean package
```

The produced runnable JAR is in `target/` as:
- `target/semantic-app-commit-<N>.jar`

### Semantic change history (10 versions)

#### Change 1
- **Change**: Added list endpoint `GET /api/quotes` to return all quotes (newest first).
- **Files**:
  - `src/main/java/com/semantic/semantic_test/api/QuoteController.java`
  - `src/main/java/com/semantic/semantic_test/service/QuoteService.java`
  - `src/main/java/com/semantic/semantic_test/repo/QuoteRepository.java`
  - `src/main/java/com/semantic/semantic_test/repo/InMemoryQuoteRepository.java`

#### Change 2
- **Change**: Invalid/ineligible coupon codes now return **400** (instead of being ignored).
- **Files**:
  - `src/main/java/com/semantic/semantic_test/service/QuotePricingEngine.java`
  - `src/main/java/com/semantic/semantic_test/error/InvalidCouponException.java`
  - `src/main/java/com/semantic/semantic_test/error/ApiExceptionHandler.java`

#### Change 3
- **Change**: Normalize item SKU on create (`trim()` + `toUpperCase()`).
- **Files**:
  - `src/main/java/com/semantic/semantic_test/service/QuoteService.java`

#### Change 4
- **Change**: Added business limits validation:
  - Max **20** items per quote
  - Max **50** quantity per item
- **Files**:
  - `src/main/java/com/semantic/semantic_test/service/QuoteValidator.java`

#### Change 5
- **Change**: Added optional idempotency for quote creation via `Idempotency-Key`.
- **Files**:
  - `src/main/java/com/semantic/semantic_test/api/QuoteController.java`
  - `src/main/java/com/semantic/semantic_test/service/QuoteService.java`
  - `src/main/java/com/semantic/semantic_test/service/IdempotencyStore.java`

#### Change 6
- **Change**: GOLD customers have higher auto-approval threshold:
  - GOLD: approve if `riskScore < 50`
  - Others: approve if `riskScore < 40`
- **Files**:
  - `src/main/java/com/semantic/semantic_test/service/QuoteRiskEngine.java`

#### Change 7
- **Change**: Round each item `unitPrice` to 2 decimals before computing totals.
- **Files**:
  - `src/main/java/com/semantic/semantic_test/service/QuotePricingEngine.java`

#### Change 8
- **Change**: Added `BLACKFRIDAY` coupon:
  - 15% off, capped at 50.00
  - rejected if FOOD or MEDICAL items are present
- **Files**:
  - `src/main/java/com/semantic/semantic_test/service/QuotePricingEngine.java`

#### Change 9
- **Change**: Idempotency safety: reusing the same `Idempotency-Key` with a different `customerId` returns **409 CONFLICT**.
- **Files**:
  - `src/main/java/com/semantic/semantic_test/service/QuoteService.java`
  - `src/main/java/com/semantic/semantic_test/error/IdempotencyConflictException.java`
  - `src/main/java/com/semantic/semantic_test/error/ApiExceptionHandler.java`

#### Change 10
- **Change**: Request correlation:
  - Every HTTP response includes `X-Request-Id`
  - Uses incoming `X-Request-Id` if provided, otherwise generates one
  - Stores the id in MDC during request handling
- **Files**:
  - `src/main/java/com/semantic/semantic_test/config/RequestIdFilter.java`

