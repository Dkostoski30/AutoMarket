import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatBadgeModule } from '@angular/material/badge';
import { AuthStore } from './core/auth/auth.store';
import { AuthService } from './core/auth/auth.service';

@Component({
  selector: 'am-root',
  standalone: true,
  imports: [
    RouterOutlet, RouterLink, CommonModule,
    MatToolbarModule, MatButtonModule, MatMenuModule, MatIconModule, MatBadgeModule,
  ],
  template: `
    <mat-toolbar class="navbar">
      <a routerLink="/" class="brand">
        <span class="brand-logo">AutoMarket</span>
      </a>

      <span class="spacer"></span>

      <nav class="nav-links">
        <a routerLink="/listings" mat-button>Browse Cars</a>
        <a routerLink="/blog" mat-button>Blog</a>

        @if (authStore.isLoggedIn()) {
          <a routerLink="/listings/create" mat-raised-button color="accent">
            + Sell Your Car
          </a>

          <button mat-icon-button [matMenuTriggerFor]="userMenu">
            <mat-icon>account_circle</mat-icon>
          </button>

          <mat-menu #userMenu>
            <a routerLink="/profile" mat-menu-item>
              <mat-icon>person</mat-icon> My Profile
            </a>
            <a routerLink="/listings/my" mat-menu-item>
              <mat-icon>directions_car</mat-icon> My Listings
            </a>
            <a routerLink="/favorites" mat-menu-item>
              <mat-icon>favorite</mat-icon> Favorites
            </a>
            <a routerLink="/inquiries" mat-menu-item>
              <mat-icon>mail</mat-icon> Inquiries
            </a>

            @if (authStore.isModerator()) {
              <mat-divider></mat-divider>
              <a routerLink="/dashboard" mat-menu-item>
                <mat-icon>dashboard</mat-icon> Dashboard
              </a>
              <a routerLink="/moderation" mat-menu-item>
                <mat-icon>verified</mat-icon> Moderation Queue
              </a>
            }

            <mat-divider></mat-divider>
            <button mat-menu-item (click)="logout()">
              <mat-icon>logout</mat-icon> Logout
            </button>
          </mat-menu>
        } @else {
          <a routerLink="/auth/login" mat-button>Login</a>
          <a routerLink="/auth/register" mat-raised-button color="primary">Register</a>
        }
      </nav>
    </mat-toolbar>

    <main class="main-content">
      <router-outlet />
    </main>
  `,
  styles: [`
    .navbar {
      background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
      color: white;
      position: sticky;
      top: 0;
      z-index: 1000;
      box-shadow: 0 2px 8px rgba(0,0,0,0.3);
    }
    .brand { text-decoration: none; color: white; }
    .brand-logo { font-size: 1.5rem; font-weight: 700; letter-spacing: -0.5px; }
    .spacer { flex: 1; }
    .nav-links { display: flex; align-items: center; gap: 8px; }
    .main-content { min-height: calc(100vh - 64px); }
  `]
})
export class AppComponent implements OnInit {
  readonly authStore = inject(AuthStore);
  private readonly authService = inject(AuthService);

  ngOnInit(): void {
    // Restore session if refresh token exists
    if (this.authStore.hasStoredSession() && !this.authStore.isLoggedIn()) {
      this.authService.refresh().subscribe({ error: () => this.authStore.clearSession() });
    }
  }

  logout(): void {
    this.authService.logout();
  }
}
