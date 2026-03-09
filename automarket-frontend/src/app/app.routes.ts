import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/home/home.component').then(m => m.HomeComponent),
  },
  {
    path: 'listings',
    loadComponent: () =>
      import('./features/listings/listing-list.component').then(m => m.ListingListComponent),
  },
  {
    path: 'listings/create',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/listings/create-listing.component').then(m => m.CreateListingComponent),
  },
  {
    path: 'listings/:slug',
    loadComponent: () =>
      import('./features/listings/listing-detail.component').then(m => m.ListingDetailComponent),
  },
  {
    path: 'listings/:id/edit',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/listings/edit-listing.component').then(m => m.EditListingComponent),
  },
  {
    path: 'auth',
    children: [
      {
        path: 'login',
        loadComponent: () =>
          import('./features/auth/login.component').then(m => m.LoginComponent),
      },
      {
        path: 'register',
        loadComponent: () =>
          import('./features/auth/register.component').then(m => m.RegisterComponent),
      },
    ],
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/user/profile.component').then(m => m.ProfileComponent),
  },
  {
    path: 'users/:id',
    loadComponent: () =>
      import('./features/user/user-profile.component').then(m => m.UserProfileComponent),
  },
  {
    path: 'favorites',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/favorites/favorites.component').then(m => m.FavoritesComponent),
  },
  {
    path: 'inquiries',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/inquiries/inquiries.component').then(m => m.InquiriesComponent),
  },
  {
    path: 'blog',
    loadComponent: () =>
      import('./features/blog/blog-list.component').then(m => m.BlogListComponent),
  },
  {
    path: 'blog/:id',
    loadComponent: () =>
      import('./features/blog/blog-detail.component').then(m => m.BlogDetailComponent),
  },
  {
    path: 'subscription',
    loadComponent: () =>
      import('./features/subscription/plans.component').then(m => m.PlansComponent),
  },
  {
    path: 'dashboard',
    canActivate: [authGuard, roleGuard('MODERATOR', 'ADMIN')],
    loadComponent: () =>
      import('./features/dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent),
  },
  {
    path: 'moderation',
    canActivate: [authGuard, roleGuard('MODERATOR', 'ADMIN')],
    loadComponent: () =>
      import('./features/dashboard/moderation-queue.component').then(m => m.ModerationQueueComponent),
  },
  {
    path: '**',
    redirectTo: '',
  },
];
