# AutoMarket Backend

Spring Boot 3 REST API for the AutoMarket car marketplace platform.

## Tech Stack

- **Java 21**
- **Spring Boot 3.2.3**
- **PostgreSQL 16** — primary database
- **Redis 7** — caching
- **Flyway** — database migrations
- **Spring Security + JWT** (JJWT 0.12.5)
- **Stripe Java SDK** (24.3.0) — subscription payments
- **AWS S3 SDK** — image storage in production
- **MapStruct** — entity/DTO mapping
- **Lombok** — boilerplate reduction
- **SpringDoc OpenAPI 2.3** — Swagger UI

## Project Structure

```
src/main/java/com/automarket/
├── controller/       REST controllers (auth, listings, users, blog, subscriptions, admin)
├── service/          Business logic services
├── entity/           JPA entities
├── dto/              Request/response DTOs
├── repository/       Spring Data JPA repositories
├── security/         JWT filter, UserDetails, SecurityConfig
├── config/           CORS, S3 storage, Redis cache, OpenAPI, Auditing
├── mapper/           MapStruct mappers
├── exception/        Custom exceptions and global exception handler
├── logging/          AOP request logging
└── specification/    JPA Specification for dynamic filtering
```

## Running Locally

### Prerequisites

- Java 21
- Maven 3.9+
- Docker (for PostgreSQL + Redis)

### Start infrastructure

```bash
# From project root
docker compose up -d postgres redis mailhog
```

### Run the application

```bash
cd automarket-backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The API starts at **http://localhost:8080**.
Swagger UI: **http://localhost:8080/swagger-ui.html**

### Environment Variables

| Variable | Default | Description |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/automarket` | JDBC connection URL |
| `DB_USERNAME` | `automarket` | Database username |
| `DB_PASSWORD` | `secret` | Database password |
| `REDIS_HOST` | `localhost` | Redis host |
| `REDIS_PORT` | `6379` | Redis port |
| `JWT_SECRET` | *(insecure default)* | At least 32-char secret for JWT signing |
| `FRONTEND_URL` | `http://localhost:4200` | Allowed CORS origin |
| `STORAGE_PROVIDER` | `local` | `local` or `s3` |
| `LOCAL_UPLOAD_DIR` | `./uploads` | Directory for uploaded images (local mode) |
| `S3_BUCKET` | — | S3 bucket name |
| `CDN_URL` | — | CDN base URL for images |
| `MAIL_HOST` | `localhost` | SMTP host |
| `MAIL_PORT` | `1025` | SMTP port |
| `STRIPE_ENABLED` | `false` | Enable Stripe payments |
| `STRIPE_SECRET_KEY` | — | Stripe secret key (`sk_...`) |
| `STRIPE_WEBHOOK_SECRET` | — | Stripe webhook endpoint secret (`whsec_...`) |
| `STRIPE_PRICE_PREMIUM` | — | Stripe Price ID for PREMIUM plan |

## Database Migrations

Flyway migrations run automatically on startup from `src/main/resources/db/migration/`:

| Migration | Description |
|---|---|
| `V1__core_schema.sql` | Users, roles, refresh tokens, cities |
| `V2__reference_data.sql` | Role seed data |
| `V3__listings_schema.sql` | Listings, images, car details |
| `V4__blog_schema.sql` | Blog posts |
| `V5__business_features.sql` | Subscriptions, analytics, inquiries, favorites |
| `V6__search_fulltext.sql` | Full-text search indexes |
| `V7__seed_reference_data.sql` | Reference data (brands, fuel types, body types, etc.) |

## API Endpoints

### Authentication
| Method | Path | Description |
|---|---|---|
| POST | `/api/v1/auth/register` | Register new user |
| POST | `/api/v1/auth/login` | Login, returns JWT pair |
| POST | `/api/v1/auth/refresh` | Refresh access token |
| POST | `/api/v1/auth/logout` | Revoke refresh token |

### Listings
| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/listings` | — | Paginated + filtered listing search |
| GET | `/api/v1/listings/featured` | — | Featured listings |
| GET | `/api/v1/listings/{slug}` | — | Listing detail by slug |
| POST | `/api/v1/listings` | USER | Create listing |
| PUT | `/api/v1/listings/{id}` | Owner | Update listing |
| DELETE | `/api/v1/listings/{id}` | Owner | Delete listing |
| POST | `/api/v1/listings/{id}/images` | Owner | Upload image |
| POST | `/api/v1/listings/{id}/favorite` | USER | Add to favorites |

### Subscriptions
| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/subscriptions/plans` | — | Available plans |
| POST | `/api/v1/subscriptions/checkout` | USER | Start Stripe checkout |
| POST | `/api/v1/subscriptions/webhook` | — | Stripe webhook receiver |

### Admin / Moderation
| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/admin/dashboard` | ADMIN | Dashboard stats |
| GET | `/api/v1/moderation/listings` | MODERATOR | Pending listings queue |
| POST | `/api/v1/moderation/listings/{id}/approve` | MODERATOR | Approve listing |
| POST | `/api/v1/moderation/listings/{id}/reject` | MODERATOR | Reject listing |

## Stripe Setup

1. Create a product and price in your [Stripe Dashboard](https://dashboard.stripe.com)
2. Set environment variables:
   ```env
   STRIPE_ENABLED=true
   STRIPE_SECRET_KEY=sk_test_...
   STRIPE_WEBHOOK_SECRET=whsec_...
   STRIPE_PRICE_PREMIUM=price_...
   ```
3. Configure a webhook endpoint in Stripe Dashboard pointing to `/api/v1/subscriptions/webhook`
4. Subscribe to events: `checkout.session.completed`, `customer.subscription.updated`, `customer.subscription.deleted`

## Running Tests

```bash
./mvnw test
```

## Building for Production

```bash
./mvnw package -DskipTests

# Or with Docker
docker build -t automarket-backend .
```

The Dockerfile uses a multi-stage build:
1. Build stage: Maven + JDK 21 Alpine
2. Runtime stage: JRE 21 Alpine (minimized image)
