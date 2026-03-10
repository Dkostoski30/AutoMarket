import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ApiService } from '../../core/http/api.service';
import { AuthStore } from '../../core/auth/auth.store';
import { ListingDetailDto } from '../../shared/models/listing.model';

@Component({
  selector: 'am-listing-detail',
  standalone: true,
  imports: [
    CommonModule, RouterLink, ReactiveFormsModule,
    MatButtonModule, MatCardModule, MatIconModule,
    MatProgressSpinnerModule, MatDividerModule,
    MatInputModule, MatFormFieldModule, MatSnackBarModule,
  ],
  template: `
    @if (isLoading()) {
      <div class="loading-center">
        <mat-spinner diameter="56"></mat-spinner>
      </div>
    } @else if (listing()) {
      <div class="detail-page">

        <!-- Image Gallery -->
        <div class="gallery-section">
          <div class="main-image">
            <img [src]="activeImage() || 'assets/no-image.png'"
                 [alt]="listing()!.title"
                 class="main-img">
            @if (listing()!.featured) {
              <span class="featured-badge-overlay">Featured</span>
            }
          </div>
          @if (listing()!.images.length > 1) {
            <div class="thumbnails">
              @for (img of listing()!.images; track img.id) {
                <img [src]="img.url"
                     [class.active]="activeImage() === img.url"
                     (click)="activeImage.set(img.url)"
                     class="thumb"
                     [alt]="listing()!.title">
              }
            </div>
          }
        </div>

        <!-- Content -->
        <div class="content-grid">

          <!-- Main info -->
          <div class="main-info">
            <div class="listing-header">
              <h1>{{ listing()!.title }}</h1>
              <div class="price-row">
                <span class="listing-price">€{{ listing()!.price | number:'1.0-0' }}</span>
                <button mat-icon-button
                        [color]="isFavorited() ? 'warn' : ''"
                        (click)="toggleFavorite()"
                        [title]="isFavorited() ? 'Remove from favorites' : 'Add to favorites'">
                  <mat-icon>{{ isFavorited() ? 'favorite' : 'favorite_border' }}</mat-icon>
                </button>
              </div>
            </div>

            <mat-divider></mat-divider>

            <!-- Specs Grid -->
            <div class="specs-grid">
              <div class="spec-item">
                <mat-icon>calendar_today</mat-icon>
                <div>
                  <span class="spec-label">Year</span>
                  <span class="spec-value">{{ listing()!.carDetails.registrationYear }}</span>
                </div>
              </div>
              <div class="spec-item">
                <mat-icon>speed</mat-icon>
                <div>
                  <span class="spec-label">Mileage</span>
                  <span class="spec-value">{{ listing()!.carDetails.kilometers | number }} km</span>
                </div>
              </div>
              <div class="spec-item">
                <mat-icon>local_gas_station</mat-icon>
                <div>
                  <span class="spec-label">Fuel</span>
                  <span class="spec-value">{{ listing()!.carDetails.fuelTypeName || '—' }}</span>
                </div>
              </div>
              <div class="spec-item">
                <mat-icon>directions_car</mat-icon>
                <div>
                  <span class="spec-label">Body</span>
                  <span class="spec-value">{{ listing()!.carDetails.bodyTypeName || '—' }}</span>
                </div>
              </div>
              <div class="spec-item">
                <mat-icon>settings</mat-icon>
                <div>
                  <span class="spec-label">Transmission</span>
                  <span class="spec-value">{{ listing()!.carDetails.transmissionTypeName || '—' }}</span>
                </div>
              </div>
              @if (listing()!.carDetails.kilowatts) {
                <div class="spec-item">
                  <mat-icon>bolt</mat-icon>
                  <div>
                    <span class="spec-label">Power</span>
                    <span class="spec-value">{{ listing()!.carDetails.kilowatts }} kW</span>
                  </div>
                </div>
              }
              @if (listing()!.carDetails.numDoors) {
                <div class="spec-item">
                  <mat-icon>door_front</mat-icon>
                  <div>
                    <span class="spec-label">Doors</span>
                    <span class="spec-value">{{ listing()!.carDetails.numDoors }}</span>
                  </div>
                </div>
              }
              @if (listing()!.carDetails.numSeats) {
                <div class="spec-item">
                  <mat-icon>airline_seat_recline_normal</mat-icon>
                  <div>
                    <span class="spec-label">Seats</span>
                    <span class="spec-value">{{ listing()!.carDetails.numSeats }}</span>
                  </div>
                </div>
              }
            </div>

            <mat-divider></mat-divider>

            <!-- Description -->
            @if (listing()!.description) {
              <div class="description-section">
                <h3>Description</h3>
                <p class="description-text">{{ listing()!.description }}</p>
              </div>
            }
          </div>

          <!-- Seller Card -->
          <aside class="seller-aside">
            <mat-card class="seller-card">
              <mat-card-header>
                <mat-icon mat-card-avatar class="seller-avatar">account_circle</mat-icon>
                <mat-card-title>{{ listing()!.seller.name }}</mat-card-title>
                @if (listing()!.seller.cityName) {
                  <mat-card-subtitle>
                    <mat-icon inline>location_on</mat-icon>
                    {{ listing()!.seller.cityName }}
                  </mat-card-subtitle>
                }
              </mat-card-header>

              <mat-card-content>
                @if (listing()!.seller.phone) {
                  <a [href]="'tel:' + listing()!.seller.phone" mat-stroked-button class="full-width contact-btn">
                    <mat-icon>phone</mat-icon>
                    {{ listing()!.seller.phone }}
                  </a>
                }

                <mat-divider class="divider-gap"></mat-divider>

                @if (authStore.isLoggedIn()) {
                  <form [formGroup]="inquiryForm" (ngSubmit)="sendInquiry()" class="inquiry-form">
                    <mat-form-field appearance="outline" class="full-width">
                      <mat-label>Send a message</mat-label>
                      <textarea matInput formControlName="message" rows="4"
                                placeholder="Hi, I'm interested in this car..."></textarea>
                      <mat-error>Message is required (min 10 chars)</mat-error>
                    </mat-form-field>
                    <button mat-raised-button color="primary" type="submit"
                            [disabled]="inquiryForm.invalid || sendingInquiry()" class="full-width">
                      @if (sendingInquiry()) {
                        <mat-spinner diameter="20"></mat-spinner>
                      } @else {
                        <mat-icon>send</mat-icon> Send Message
                      }
                    </button>
                  </form>
                } @else {
                  <p class="login-prompt">
                    <a routerLink="/auth/login">Log in</a> to contact the seller.
                  </p>
                }
              </mat-card-content>
            </mat-card>

            <div class="listing-meta-info">
              <mat-icon inline>access_time</mat-icon>
              Listed {{ listing()!.createdAt | date:'mediumDate' }}
            </div>
          </aside>

        </div>
      </div>
    } @else {
      <div class="empty-state">
        <mat-icon class="empty-icon">search_off</mat-icon>
        <h3>Listing not found</h3>
        <p>This listing may have been removed.</p>
        <a routerLink="/listings" mat-raised-button color="primary" style="margin-top:24px">Browse Listings</a>
      </div>
    }
  `,
  styles: [`
    .detail-page { max-width: 1280px; margin: 0 auto; padding: 32px 24px; }
    .gallery-section { margin-bottom: 32px; }
    .main-image { border-radius: 12px; overflow: hidden; background: #f3f4f6; position: relative; height: 480px; }
    .main-img { width: 100%; height: 100%; object-fit: cover; }
    .featured-badge-overlay { position: absolute; top: 16px; left: 16px; background: #f59e0b; color: white; padding: 4px 12px; border-radius: 999px; font-size: 0.8rem; font-weight: 600; }
    .thumbnails { display: flex; gap: 8px; margin-top: 8px; overflow-x: auto; padding-bottom: 4px; }
    .thumb { width: 90px; height: 65px; object-fit: cover; border-radius: 6px; cursor: pointer; border: 2px solid transparent; opacity: 0.7; transition: all 0.2s; flex-shrink: 0; }
    .thumb:hover, .thumb.active { opacity: 1; border-color: #1a7f64; }
    .content-grid { display: grid; grid-template-columns: 1fr 340px; gap: 32px; align-items: start; }
    .listing-header { margin-bottom: 20px; }
    .listing-header h1 { font-size: 1.75rem; margin-bottom: 12px; }
    .price-row { display: flex; align-items: center; justify-content: space-between; }
    .listing-price { font-size: 2rem; font-weight: 800; color: #1a7f64; }
    .specs-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(160px, 1fr)); gap: 16px; padding: 20px 0; }
    .spec-item { display: flex; align-items: center; gap: 10px; }
    .spec-item mat-icon { color: #1a7f64; }
    .spec-label { display: block; font-size: 0.75rem; color: #888; text-transform: uppercase; letter-spacing: 0.5px; }
    .spec-value { display: block; font-weight: 600; font-size: 0.95rem; }
    .description-section { padding: 20px 0; }
    .description-section h3 { margin-bottom: 12px; }
    .description-text { color: #374151; line-height: 1.8; white-space: pre-wrap; }
    .seller-aside { position: sticky; top: 80px; }
    .seller-card { border-radius: 12px !important; }
    .seller-avatar { font-size: 40px; width: 40px; height: 40px; color: #1a7f64; }
    .full-width { width: 100%; }
    .contact-btn { margin-bottom: 8px; }
    .divider-gap { margin: 16px 0 !important; }
    .inquiry-form { display: flex; flex-direction: column; gap: 12px; }
    .login-prompt { text-align: center; color: #888; font-size: 0.9rem; padding: 12px 0; }
    .listing-meta-info { display: flex; align-items: center; gap: 6px; color: #888; font-size: 0.85rem; margin-top: 12px; }
    .loading-center { display: flex; justify-content: center; padding: 100px 0; }
    .empty-state { text-align: center; padding: 100px 24px; color: #888; }
    .empty-icon { font-size: 80px; width: 80px; height: 80px; opacity: 0.2; display: block; margin: 0 auto 16px; }
    @media (max-width: 900px) {
      .content-grid { grid-template-columns: 1fr; }
      .seller-aside { position: static; }
      .main-image { height: 300px; }
    }
  `]
})
export class ListingDetailComponent implements OnInit {
  private readonly api    = inject(ApiService);
  private readonly route  = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly snack  = inject(MatSnackBar);
  private readonly fb     = inject(FormBuilder);
  readonly authStore      = inject(AuthStore);

  readonly listing        = signal<ListingDetailDto | null>(null);
  readonly isLoading      = signal(true);
  readonly isFavorited    = signal(false);
  readonly sendingInquiry = signal(false);
  readonly activeImage    = signal<string>('');

  inquiryForm = this.fb.group({
    message: ['', [Validators.required, Validators.minLength(10)]],
  });

  ngOnInit(): void {
    const slug = this.route.snapshot.paramMap.get('slug');
    if (!slug) { this.router.navigate(['/listings']); return; }

    this.api.getListingBySlug(slug).subscribe({
      next: listing => {
        this.listing.set(listing);
        if (listing.images.length > 0) {
          this.activeImage.set(listing.images[0].url);
        }
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      },
    });
  }

  toggleFavorite(): void {
    if (!this.authStore.isLoggedIn()) {
      this.router.navigate(['/auth/login']);
      return;
    }
    const id = this.listing()?.id;
    if (!id) return;

    if (this.isFavorited()) {
      this.api.removeFavorite(id).subscribe(() => this.isFavorited.set(false));
    } else {
      this.api.addFavorite(id).subscribe(() => this.isFavorited.set(true));
    }
  }

  sendInquiry(): void {
    if (this.inquiryForm.invalid) return;
    const listingId = this.listing()?.id;
    if (!listingId) return;

    this.sendingInquiry.set(true);
    this.api.sendInquiry(listingId, this.inquiryForm.value.message!).subscribe({
      next: () => {
        this.sendingInquiry.set(false);
        this.inquiryForm.reset();
        this.snack.open('Message sent to seller!', 'OK', { duration: 4000 });
      },
      error: () => {
        this.sendingInquiry.set(false);
        this.snack.open('Failed to send message. Please try again.', 'Close', { duration: 4000 });
      },
    });
  }
}
