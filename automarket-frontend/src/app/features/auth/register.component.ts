import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../core/auth/auth.service';
import { ApiService } from '../../core/http/api.service';
import { ReferenceItem } from '../../shared/models/listing.model';

@Component({
  selector: 'am-register',
  standalone: true,
  imports: [
    CommonModule, RouterLink, ReactiveFormsModule,
    MatCardModule, MatInputModule, MatButtonModule, MatSelectModule,
    MatProgressSpinnerModule, MatIconModule,
  ],
  template: `
    <div class="auth-page">
      <mat-card class="auth-card">
        <mat-card-header>
          <mat-card-title>Create your account</mat-card-title>
          <mat-card-subtitle>Join AutoMarket to buy and sell cars</mat-card-subtitle>
        </mat-card-header>

        <mat-card-content>
          <form [formGroup]="form" (ngSubmit)="submit()" class="auth-form">

            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Full Name</mat-label>
              <input matInput formControlName="name" autocomplete="name">
              <mat-error>Name must be at least 2 characters</mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Email</mat-label>
              <input matInput type="email" formControlName="email" autocomplete="email">
              <mat-error>Valid email is required</mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Password</mat-label>
              <input matInput [type]="showPassword() ? 'text' : 'password'"
                     formControlName="password" autocomplete="new-password">
              <button mat-icon-button matSuffix type="button" (click)="showPassword.update(v => !v)">
                <mat-icon>{{ showPassword() ? 'visibility_off' : 'visibility' }}</mat-icon>
              </button>
              <mat-hint>Minimum 8 characters</mat-hint>
              <mat-error>Password must be at least 8 characters</mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Phone (optional)</mat-label>
              <input matInput formControlName="phone" type="tel" autocomplete="tel">
            </mat-form-field>

            <mat-form-field appearance="outline" class="full-width">
              <mat-label>City (optional)</mat-label>
              <mat-select formControlName="cityId">
                <mat-option value="">Select a city</mat-option>
                @for (city of cities(); track city.id) {
                  <mat-option [value]="city.id">{{ city.name }}</mat-option>
                }
              </mat-select>
            </mat-form-field>

            @if (error()) {
              <div class="error-message">{{ error() }}</div>
            }

            <button mat-raised-button color="primary" type="submit"
                    [disabled]="form.invalid || isLoading()" class="full-width submit-btn">
              @if (isLoading()) {
                <mat-spinner diameter="20"></mat-spinner>
              } @else {
                Create Account
              }
            </button>

          </form>
        </mat-card-content>

        <mat-card-actions>
          <p class="auth-link">
            Already have an account? <a routerLink="/auth/login">Log in</a>
          </p>
        </mat-card-actions>
      </mat-card>
    </div>
  `,
  styles: [`
    .auth-page { display: flex; justify-content: center; align-items: center; min-height: calc(100vh - 64px); padding: 24px; }
    .auth-card { width: 100%; max-width: 440px; padding: 8px; }
    .auth-form { display: flex; flex-direction: column; gap: 12px; margin-top: 16px; }
    .full-width { width: 100%; }
    .submit-btn { height: 48px; font-size: 1rem; }
    .error-message { color: #ef4444; font-size: 0.875rem; padding: 8px 12px; background: #fef2f2; border-radius: 4px; }
    .auth-link { text-align: center; color: #666; margin: 8px 0 0; }
    .auth-link a { color: #1a7f64; font-weight: 500; }
  `]
})
export class RegisterComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly apiService  = inject(ApiService);
  private readonly router      = inject(Router);
  private readonly fb          = inject(FormBuilder);

  readonly isLoading   = signal(false);
  readonly error       = signal<string | null>(null);
  readonly showPassword = signal(false);
  readonly cities       = signal<ReferenceItem[]>([]);

  form: FormGroup = this.fb.group({
    name:     ['', [Validators.required, Validators.minLength(2)]],
    email:    ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    phone:    [''],
    cityId:   [''],
  });

  ngOnInit(): void {
    this.apiService.getCities().subscribe(c => this.cities.set(c));
  }

  submit(): void {
    if (this.form.invalid) return;
    this.isLoading.set(true);
    this.error.set(null);

    this.authService.register(this.form.value).subscribe({
      next: () => this.router.navigate(['/']),
      error: err => {
        this.error.set(err.error?.message || 'Registration failed. Please try again.');
        this.isLoading.set(false);
      },
    });
  }
}
