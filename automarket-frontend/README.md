# AutoMarket Frontend

Angular 17 single-page application for the AutoMarket car marketplace.

## Tech Stack

- **Angular 17** — standalone components, signals
- **Angular Material 17** — UI component library
- **RxJS 7.8** — reactive programming
- **Tailwind CSS 3.4** — utility-first CSS
- **TypeScript 5.4**
- **Nginx** — production web server + API proxy

## Project Structure

```
src/
├── app/
│   ├── core/
│   │   ├── auth/           AuthService, AuthStore (signals-based state)
│   │   ├── guards/         AuthGuard, RoleGuard
│   │   └── http/           ApiService, JwtInterceptor, ErrorInterceptor
│   ├── features/
│   │   ├── auth/           Login, Register
│   │   ├── listings/       ListingList, ListingDetail, CreateListing, EditListing
│   │   ├── home/           Landing page
│   │   ├── blog/           BlogList, BlogDetail
│   │   ├── user/           UserProfile
│   │   ├── favorites/      Favorites list
│   │   ├── inquiries/      Buyer–seller messages
│   │   ├── dashboard/      Admin dashboard, Moderation queue
│   │   └── subscription/   Plans & Stripe checkout
│   ├── shared/
│   │   └── models/         TypeScript interfaces (listing, user)
│   ├── app.component.ts    Root component + navigation
│   ├── app.routes.ts       Lazy-loaded routes
│   └── app.config.ts       Angular providers
├── environments/           API URL configuration
└── styles/
    └── global.scss         Angular Material theme + global styles
```

## Running Locally

### Prerequisites

- Node.js 20+
- npm 10+
- Backend API running at `http://localhost:8080`

### Install & run

```bash
cd automarket-frontend
npm install
npm start
```

The app opens at **http://localhost:4200**. API calls are proxied to `http://localhost:8080` via `proxy.conf.json`.

## Environment Configuration

Edit `src/environments/environment.ts` for development:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1',
};
```

For production (`environment.prod.ts`), the `apiUrl` is typically set to `/api/v1` (relative, handled by Nginx).

## Features

| Page | Route | Auth Required |
|---|---|---|
| Home | `/` | — |
| Browse listings | `/listings` | — |
| Listing detail | `/listings/:slug` | — |
| Create listing | `/listings/create` | Yes |
| Edit listing | `/listings/:id/edit` | Yes (owner) |
| Login | `/auth/login` | — |
| Register | `/auth/register` | — |
| User profile | `/profile` | Yes |
| My listings | `/listings/my` | Yes |
| Favorites | `/favorites` | Yes |
| Inquiries | `/inquiries` | Yes |
| Subscription plans | `/subscription/plans` | — |
| Admin dashboard | `/dashboard` | ADMIN |
| Moderation queue | `/moderation` | MODERATOR |
| Blog | `/blog` | — |

## Authentication Flow

1. User logs in → `AuthService.login()` → backend returns `{ accessToken, refreshToken }`
2. Tokens are stored in `AuthStore` (localStorage for refresh token)
3. `JwtInterceptor` automatically attaches `Authorization: Bearer <accessToken>` to all API requests
4. `ErrorInterceptor` handles 401 responses by attempting a token refresh, then retrying the request
5. On app startup, if a refresh token exists, it is used to restore the session silently

## Building for Production

```bash
npm run build:prod
```

Output goes to `dist/automarket-frontend/`. The included `Dockerfile` builds the app and serves it with Nginx.

```bash
docker build -t automarket-frontend .
```

### Nginx Configuration

The `nginx.conf` handles:
- SPA routing (all 404s → `index.html`)
- API proxy (`/api/v1` → backend service)
- Static asset caching (`/assets` — 30 days)
- Security headers (CSP, HSTS, X-Frame-Options)
- Gzip compression

## Testing

```bash
# Unit tests (Karma + Jasmine)
npm test

# End-to-end tests (Cypress)
npm run e2e
```

## Styling

The app uses **Angular Material** theming with a custom dark-navy + teal color palette, defined in `src/styles/global.scss`. Global design tokens (colors, shadows, border-radius, transitions) are set as CSS custom properties on `:root` and reused throughout component styles.
