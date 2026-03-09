import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { ApiService } from '../../core/http/api.service';
import { ListingDto } from '../../shared/models/listing.model';

@Component({
  selector: 'am-home',
  standalone: true,
  imports: [CommonModule, RouterLink, MatButtonModule, MatCardModule, MatIconModule],
  template: `
    <div class="hero">
      <div class="hero-content">
        <h1>Find Your Perfect Car</h1>
        <p>Browse thousands of verified listings from trusted sellers</p>
        <div class="hero-actions">
          <a routerLink="/listings" mat-raised-button color="primary" class="hero-btn">Browse Cars</a>
          <a routerLink="/listings/create" mat-stroked-button class="hero-btn">Sell Your Car</a>
        </div>
      </div>
    </div>

    @if (featured().length > 0) {
      <section class="section">
        <h2>Featured Listings</h2>
        <div class="featured-grid">
          @for (listing of featured(); track listing.id) {
            <mat-card class="listing-card" [routerLink]="['/listings', listing.slug]">
              <div class="featured-badge">Featured</div>
              <div class="card-image">
                <img [src]="listing.thumbnailUrl || 'assets/no-image.png'" [alt]="listing.title" loading="lazy">
              </div>
              <mat-card-content>
                <h4>{{ listing.title }}</h4>
                <p class="price">€{{ listing.price | number:'1.0-0' }}</p>
                <p class="meta">{{ listing.carDetails.registrationYear }} · {{ listing.carDetails.kilometers | number }}km</p>
              </mat-card-content>
            </mat-card>
          }
        </div>
      </section>
    }

    <section class="stats-section">
      <div class="stat"><mat-icon>directions_car</mat-icon><span>10,000+ listings</span></div>
      <div class="stat"><mat-icon>verified_user</mat-icon><span>Verified sellers</span></div>
      <div class="stat"><mat-icon>support_agent</mat-icon><span>24/7 support</span></div>
      <div class="stat"><mat-icon>lock</mat-icon><span>Secure platform</span></div>
    </section>
  `,
  styles: [`
    .hero { background: linear-gradient(135deg, #1a1a2e, #0f3460); color: white; text-align: center; padding: 80px 24px; }
    .hero h1 { font-size: 3rem; font-weight: 800; margin: 0 0 16px; }
    .hero p { font-size: 1.2rem; opacity: 0.8; margin: 0 0 32px; }
    .hero-actions { display: flex; gap: 16px; justify-content: center; }
    .hero-btn { height: 48px; padding: 0 32px; font-size: 1rem; }
    .section { max-width: 1200px; margin: 48px auto; padding: 0 24px; }
    .section h2 { font-size: 1.75rem; font-weight: 700; margin-bottom: 24px; }
    .featured-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 20px; }
    .listing-card { cursor: pointer; transition: transform 0.2s; position: relative; overflow: hidden; }
    .listing-card:hover { transform: translateY(-4px); }
    .featured-badge { position: absolute; top: 12px; right: 12px; background: #f59e0b; color: white; padding: 2px 10px; border-radius: 12px; font-size: 0.75rem; font-weight: 600; }
    .card-image { height: 170px; overflow: hidden; }
    .card-image img { width: 100%; height: 100%; object-fit: cover; }
    .price { color: #1a7f64; font-weight: 700; font-size: 1.1rem; }
    .meta { color: #888; font-size: 0.8rem; }
    .stats-section { display: flex; justify-content: center; gap: 48px; padding: 48px 24px; background: #f8f9fa; }
    .stat { display: flex; align-items: center; gap: 8px; font-weight: 500; }
    .stat mat-icon { color: #1a7f64; }
  `]
})
export class HomeComponent implements OnInit {
  private readonly api = inject(ApiService);
  readonly featured = signal<ListingDto[]>([]);

  ngOnInit(): void {
    this.api.getFeaturedListings().subscribe(f => this.featured.set(f));
  }
}
