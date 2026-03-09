import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { ApiService } from '../../core/http/api.service';
import { ListingDetailDto } from '../../shared/models/listing.model';

@Component({
  selector: 'am-moderation-queue',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatProgressSpinnerModule, MatIconModule],
  template: `
    <div class="moderation-page">
      <h1>Moderation Queue</h1>
      @if (isLoading()) { <mat-spinner></mat-spinner> }
      @else if (listings().length === 0) {
        <div class="empty">
          <mat-icon>check_circle</mat-icon>
          <p>All clear! No pending listings.</p>
        </div>
      }
      @else {
        <div class="listings-list">
          @for (listing of listings(); track listing.id) {
            <mat-card class="listing-row">
              <mat-card-content>
                <div class="listing-info">
                  <img [src]="listing.images[0]?.url || 'assets/no-image.png'" [alt]="listing.title" class="thumbnail">
                  <div>
                    <h3>{{ listing.title }}</h3>
                    <p>€{{ listing.price | number }} · {{ listing.carDetails.brandName }} {{ listing.carDetails.model }} {{ listing.carDetails.registrationYear }}</p>
                    <p class="seller">by {{ listing.seller.name }}</p>
                  </div>
                </div>
                <div class="actions">
                  <button mat-raised-button color="primary" (click)="approve(listing.id)">
                    <mat-icon>check</mat-icon> Approve
                  </button>
                  <button mat-raised-button color="warn" (click)="reject(listing.id)">
                    <mat-icon>close</mat-icon> Reject
                  </button>
                </div>
              </mat-card-content>
            </mat-card>
          }
        </div>
      }
    </div>
  `,
  styles: [`
    .moderation-page { max-width: 900px; margin: 0 auto; padding: 32px 24px; }
    h1 { font-size: 2rem; font-weight: 700; margin-bottom: 24px; }
    .listings-list { display: flex; flex-direction: column; gap: 16px; }
    .listing-row mat-card-content { display: flex; justify-content: space-between; align-items: center; padding: 16px; }
    .listing-info { display: flex; gap: 16px; align-items: center; }
    .thumbnail { width: 100px; height: 70px; object-fit: cover; border-radius: 8px; }
    .seller { color: #888; font-size: 0.85rem; }
    .actions { display: flex; gap: 8px; }
    .empty { text-align: center; padding: 48px; color: #888; }
    .empty mat-icon { font-size: 48px; width: 48px; height: 48px; color: #10b981; }
  `]
})
export class ModerationQueueComponent implements OnInit {
  private readonly api = inject(ApiService);
  readonly listings = signal<ListingDetailDto[]>([]);
  readonly isLoading = signal(false);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading.set(true);
    this.api.getPendingListings().subscribe({
      next: r => { this.listings.set(r.content); this.isLoading.set(false); },
      error: () => this.isLoading.set(false),
    });
  }

  approve(id: string): void {
    this.api.approveListing(id).subscribe(() => this.load());
  }

  reject(id: string): void {
    const reason = prompt('Reason for rejection (optional):');
    this.api.rejectListing(id, reason ?? undefined).subscribe(() => this.load());
  }
}
