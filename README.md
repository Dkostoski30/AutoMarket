# AutoMarket

AutoMarket is a full-stack car marketplace platform built with **Spring Boot 3** and **Angular 17**. It allows users to browse, list, and manage car sales with features including JWT authentication, subscription plans, image uploads, listing moderation, and analytics.

## Architecture

```
AutoMarket/
├── automarket-backend/     # Spring Boot 3 REST API (Java 21)
├── automarket-frontend/    # Angular 17 SPA
├── AutoMarket/             # Legacy ASP.NET MVC 5 app (archived)
├── docker-compose.yml      # Local development stack
├── docker-compose.prod.yml # Production stack
└── .github/workflows/      # CI/CD pipeline
```

## Features

| Feature | Status |
|---|---|
| User registration & JWT auth | ✅ |
| Role-based access (USER / MODERATOR / ADMIN) | ✅ |
| Car listings with images | ✅ |
| Advanced search & filtering | ✅ |
| Favorites | ✅ |
| Buyer–seller inquiries | ✅ |
| Blog | ✅ |
| Subscription plans (FREE / PREMIUM) | ✅ |
| Stripe payments | ✅ (requires `STRIPE_ENABLED=true`) |
| Admin dashboard & moderation | ✅ |
| Analytics (listing views) | ✅ |
| Email notifications | ✅ |
| AWS S3 image storage | ✅ (requires `STORAGE_PROVIDER=s3`) |

## Quick Start

### Prerequisites

- Docker & Docker Compose
- Java 21 (for backend-only development)
- Node.js 20 (for frontend-only development)

### Run with Docker Compose

```bash
# Clone the repository
git clone https://github.com/Dkostoski30/AutoMarket.git
cd AutoMarket

# Start all services (PostgreSQL, Redis, MailHog, pgAdmin)
docker compose up -d

# The backend starts on http://localhost:8080
# The frontend starts on http://localhost:4200
```

### Environment Variables

Copy `.env.example` to `.env` and configure:

```env
# Database
DB_URL=jdbc:postgresql://localhost:5432/automarket
DB_USERNAME=automarket
DB_PASSWORD=secret

# JWT
JWT_SECRET=your-32-char-minimum-secret-key

# Stripe (optional — set STRIPE_ENABLED=true to activate)
STRIPE_ENABLED=false
STRIPE_SECRET_KEY=sk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...
STRIPE_PRICE_PREMIUM=price_...

# Storage (local or s3)
STORAGE_PROVIDER=local
# S3_BUCKET=automarket-uploads
# CDN_URL=https://cdn.automarket.com

# Mail
MAIL_HOST=localhost
MAIL_PORT=1025
```

## Development

See detailed instructions in each sub-project:

- [Backend README](automarket-backend/README.md)
- [Frontend README](automarket-frontend/README.md)

## Tech Stack

**Backend**
- Java 21, Spring Boot 3.2
- PostgreSQL 16, Redis 7
- Flyway (database migrations)
- Spring Security + JWT (JJWT)
- Stripe Java SDK
- AWS S3 SDK
- MapStruct, Lombok
- SpringDoc OpenAPI (Swagger UI)

**Frontend**
- Angular 17 (standalone components)
- Angular Material 17
- RxJS 7
- Tailwind CSS 3

**Infrastructure**
- Docker + Nginx
- GitHub Actions CI/CD

## API Documentation

When the backend is running, Swagger UI is available at:
`http://localhost:8080/swagger-ui.html`

## Local Development Services

| Service | URL | Credentials |
|---|---|---|
| Backend API | http://localhost:8080 | — |
| Frontend | http://localhost:4200 | — |
| Swagger UI | http://localhost:8080/swagger-ui.html | — |
| pgAdmin | http://localhost:5050 | admin@automarket.com / admin |
| MailHog | http://localhost:8025 | — |

## License

MIT
