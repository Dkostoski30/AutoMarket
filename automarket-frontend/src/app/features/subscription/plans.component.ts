import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { HttpClient } from '@angular/common/http';
import { AuthStore } from '../../core/auth/auth.store';
import { environment } from '../../../environments/environment';

interface Plan {
  id: string;
  name: string;
  maxListings: number;
  price: number;
  currency?: string;
  billingPeriod?: string;
  features: string[];
}

@Component({
  selector: 'am-plans',
  standalone: true,
  imports: [
    CommonModule, RouterLink,
    MatButtonModule, MatCardModule, MatIconModule,
    MatProgressSpinnerModule, MatSnackBarModule,
  ],
  template: `
    <div class="plans-page">
      <div class="plans-hero">
        <h1>Choose Your Plan</h1>
        <p>Start for free and upgrade when you're ready to grow</p>
      </div>

      <div class="plans-grid">
        @for (plan of plans(); track plan.id) {
          <mat-card class="plan-card" [class.plan-card--featured]="plan.id === 'PREMIUM'">
            @if (plan.id === 'PREMIUM') {
              <div class="popular-badge">Most Popular</div>
            }

            <mat-card-header>
              <mat-card-title class="plan-name">{{ plan.name }}</mat-card-title>
              <mat-card-subtitle>
                <div class="plan-price">
                  @if (plan.price === 0) {
                    <span class="price-amount">Free</span>
                  } @else {
                    <span class="price-amount">{{ plan.price | number }} MKD</span>
                    <span class="price-period">/month</span>
                  }
                </div>
              </mat-card-subtitle>
            </mat-card-header>

            <mat-card-content>
              <ul class="feature-list">
                @for (feature of plan.features; track feature) {
                  <li>
                    <mat-icon class="check-icon">check_circle</mat-icon>
                    {{ feature }}
                  </li>
                }
              </ul>

              <div class="listings-limit">
                @if (plan.maxListings === -1) {
                  <mat-icon>all_inclusive</mat-icon>
                  <span>Unlimited listings</span>
                } @else {
                  <mat-icon>directions_car</mat-icon>
                  <span>Up to {{ plan.maxListings }} active listings</span>
                }
              </div>
            </mat-card-content>

            <mat-card-actions>
              @if (plan.price === 0) {
                @if (authStore.isLoggedIn()) {
                  <div class="current-plan-label">
                    <mat-icon>check</mat-icon> Your current plan
                  </div>
                } @else {
                  <a routerLink="/auth/register" mat-stroked-button class="plan-btn">
                    Get Started Free
                  </a>
                }
              } @else {
                @if (!authStore.isLoggedIn()) {
                  <a routerLink="/auth/register" mat-raised-button color="primary" class="plan-btn">
                    Sign up to Upgrade
                  </a>
                } @else {
                  <button mat-raised-button color="primary" class="plan-btn"
                          [disabled]="checkingOut()"
                          (click)="checkout(plan)">
                    @if (checkingOut()) {
                      <mat-spinner diameter="20"></mat-spinner>
                    } @else {
                      Upgrade to {{ plan.name }}
                    }
                  </button>
                }
              }
            </mat-card-actions>
          </mat-card>
        }
      </div>

      <div class="faq-section">
        <h2>Frequently Asked Questions</h2>
        <div class="faq-grid">
          <div class="faq-item">
            <h4>Can I cancel anytime?</h4>
            <p>Yes, you can cancel your subscription at any time. Your plan remains active until the end of the billing period.</p>
          </div>
          <div class="faq-item">
            <h4>How does billing work?</h4>
            <p>We charge monthly. You'll be billed on the same day each month. Payments are processed securely via Stripe.</p>
          </div>
          <div class="faq-item">
            <h4>What happens to my listings if I downgrade?</h4>
            <p>Existing listings remain active but you won't be able to create new ones once you reach the free plan limit.</p>
          </div>
          <div class="faq-item">
            <h4>Is my payment information secure?</h4>
            <p>Yes. We never store your card details. All payments are handled securely by Stripe, a PCI-compliant payment processor.</p>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .plans-page { max-width: 1100px; margin: 0 auto; padding: 48px 24px; }
    .plans-hero { text-align: center; margin-bottom: 48px; }
    .plans-hero h1 { font-size: 2.5rem; margin-bottom: 12px; }
    .plans-hero p { color: #6b7280; font-size: 1.1rem; }

    .plans-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 24px; margin-bottom: 64px; }

    .plan-card { border-radius: 16px !important; position: relative; overflow: visible !important; padding: 8px; transition: transform 0.2s, box-shadow 0.2s; }
    .plan-card:hover { transform: translateY(-4px); box-shadow: 0 12px 32px rgba(0,0,0,0.12) !important; }
    .plan-card--featured { border: 2px solid #1a7f64 !important; box-shadow: 0 8px 24px rgba(26,127,100,0.15) !important; }

    .popular-badge { position: absolute; top: -12px; left: 50%; transform: translateX(-50%); background: #1a7f64; color: white; padding: 4px 20px; border-radius: 999px; font-size: 0.8rem; font-weight: 600; white-space: nowrap; }

    .plan-name { font-size: 1.5rem; font-weight: 700; }
    .plan-price { margin-top: 8px; }
    .price-amount { font-size: 2rem; font-weight: 800; color: #111; }
    .price-period { font-size: 1rem; color: #6b7280; margin-left: 4px; }

    .feature-list { list-style: none; padding: 0; margin: 16px 0; display: flex; flex-direction: column; gap: 10px; }
    .feature-list li { display: flex; align-items: center; gap: 10px; font-size: 0.95rem; }
    .check-icon { color: #1a7f64; font-size: 20px; width: 20px; height: 20px; }

    .listings-limit { display: flex; align-items: center; gap: 8px; color: #374151; font-weight: 500; padding: 12px 0; border-top: 1px solid #e5e7eb; margin-top: 8px; }
    .listings-limit mat-icon { color: #1a7f64; }

    .plan-btn { width: 100%; height: 48px; font-size: 1rem; margin: 0 !important; }
    .current-plan-label { display: flex; align-items: center; justify-content: center; gap: 6px; color: #1a7f64; font-weight: 600; padding: 12px 0; width: 100%; }

    .faq-section { padding-top: 32px; border-top: 1px solid #e5e7eb; }
    .faq-section h2 { font-size: 1.75rem; text-align: center; margin-bottom: 32px; }
    .faq-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 24px; }
    .faq-item { padding: 20px; background: #f8f9fa; border-radius: 8px; }
    .faq-item h4 { margin-bottom: 8px; font-size: 1rem; }
    .faq-item p { color: #6b7280; font-size: 0.9rem; line-height: 1.6; }
  `]
})
export class PlansComponent implements OnInit {
  private readonly http  = inject(HttpClient);
  private readonly snack = inject(MatSnackBar);
  readonly authStore     = inject(AuthStore);

  readonly plans       = signal<Plan[]>([]);
  readonly checkingOut = signal(false);

  ngOnInit(): void {
    this.http.get<{ plans: Plan[] }>(`${environment.apiUrl}/subscriptions/plans`).subscribe({
      next: res => this.plans.set(res.plans),
      error: () => {
        // Fallback to hardcoded plans if API fails
        this.plans.set([
          {
            id: 'FREE',
            name: 'Free',
            maxListings: 3,
            price: 0,
            features: ['3 active listings', 'Basic search visibility', 'Buyer inquiries'],
          },
          {
            id: 'PREMIUM',
            name: 'Premium',
            maxListings: -1,
            price: 1499,
            currency: 'MKD',
            billingPeriod: 'monthly',
            features: ['Unlimited listings', 'Featured listing slots', 'Analytics dashboard', 'Priority support'],
          },
        ]);
      },
    });
  }

  checkout(plan: Plan): void {
    this.checkingOut.set(true);
    const successUrl = `${window.location.origin}/subscription/success`;
    const cancelUrl  = `${window.location.origin}/subscription/plans`;

    this.http.post<{ checkoutUrl: string }>(`${environment.apiUrl}/subscriptions/checkout`, {
      plan: plan.id,
      successUrl,
      cancelUrl,
    }).subscribe({
      next: res => {
        window.location.href = res.checkoutUrl;
      },
      error: err => {
        this.checkingOut.set(false);
        this.snack.open(
          err.error?.message || 'Payment not available. Please try again later.',
          'Close',
          { duration: 5000 }
        );
      },
    });
  }
}
