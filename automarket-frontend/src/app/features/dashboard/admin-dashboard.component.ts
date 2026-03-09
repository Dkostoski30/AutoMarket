import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ApiService } from '../../core/http/api.service';

@Component({
  selector: 'am-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, MatCardModule, MatButtonModule, MatIconModule],
  template: `
    <div class="dashboard-page">
      <h1>Dashboard</h1>
      <div class="stats-grid">
        @if (stats()) {
          @for (stat of statItems(); track stat.label) {
            <mat-card class="stat-card">
              <mat-icon [style.color]="stat.color">{{ stat.icon }}</mat-icon>
              <div class="stat-value">{{ stats()![stat.key] | number }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </mat-card>
          }
        }
      </div>
      <div class="quick-actions">
        <a routerLink="/moderation" mat-raised-button color="warn">Review Pending Listings</a>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-page { max-width: 1200px; margin: 0 auto; padding: 32px 24px; }
    h1 { font-size: 2rem; font-weight: 700; margin-bottom: 32px; }
    .stats-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 20px; }
    .stat-card { text-align: center; padding: 24px; }
    .stat-card mat-icon { font-size: 40px; width: 40px; height: 40px; }
    .stat-value { font-size: 2.5rem; font-weight: 700; margin: 8px 0; }
    .stat-label { color: #666; }
    .quick-actions { margin-top: 32px; }
  `]
})
export class AdminDashboardComponent implements OnInit {
  private readonly api = inject(ApiService);
  readonly stats = signal<Record<string, number> | null>(null);

  readonly statItems = signal([
    { key: 'pendingListings', label: 'Pending Approval', icon: 'pending', color: '#f59e0b' },
    { key: 'approvedListings', label: 'Approved Listings', icon: 'check_circle', color: '#10b981' },
    { key: 'totalUsers', label: 'Total Users', icon: 'people', color: '#3b82f6' },
    { key: 'totalBrands', label: 'Car Brands', icon: 'directions_car', color: '#8b5cf6' },
    { key: 'totalBlogs', label: 'Blog Posts', icon: 'article', color: '#ec4899' },
  ]);

  ngOnInit(): void {
    this.api.getDashboard().subscribe(s => this.stats.set(s));
  }
}
