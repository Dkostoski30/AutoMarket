import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatStepperModule } from '@angular/material/stepper';
import { ApiService } from '../../core/http/api.service';
import { ReferenceItem } from '../../shared/models/listing.model';

@Component({
  selector: 'am-create-listing',
  standalone: true,
  imports: [
    CommonModule, RouterLink, ReactiveFormsModule,
    MatButtonModule, MatCardModule, MatInputModule, MatFormFieldModule,
    MatSelectModule, MatIconModule, MatProgressSpinnerModule,
    MatSnackBarModule, MatStepperModule,
  ],
  template: `
    <div class="create-page">
      <div class="create-header">
        <h1>Post Your Car Listing</h1>
        <p class="text-muted">Fill in the details below to list your car for sale.</p>
      </div>

      <form [formGroup]="form" (ngSubmit)="submit()">
        <mat-stepper orientation="vertical" linear #stepper>

          <!-- Step 1: Basic Info -->
          <mat-step label="Basic Information" [stepControl]="basicGroup">
            <div formGroupName="basic" class="step-content">
              <mat-form-field appearance="outline" class="full-width">
                <mat-label>Listing Title</mat-label>
                <input matInput formControlName="title" placeholder="e.g. 2020 BMW 3 Series 320d">
                <mat-error>Title is required (min 5 characters)</mat-error>
              </mat-form-field>

              <div class="form-row">
                <mat-form-field appearance="outline">
                  <mat-label>Price (€)</mat-label>
                  <input matInput type="number" formControlName="price" min="0">
                  <mat-error>Valid price is required</mat-error>
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>City</mat-label>
                  <mat-select formControlName="cityId">
                    <mat-option value="">Select city</mat-option>
                    @for (city of cities(); track city.id) {
                      <mat-option [value]="city.id">{{ city.name }}</mat-option>
                    }
                  </mat-select>
                </mat-form-field>
              </div>

              <mat-form-field appearance="outline" class="full-width">
                <mat-label>Description</mat-label>
                <textarea matInput formControlName="description" rows="5"
                          placeholder="Describe your car — service history, extras, condition..."></textarea>
                <mat-hint align="end">{{ basicGroup.get('description')?.value?.length || 0 }} chars</mat-hint>
              </mat-form-field>

              <div class="step-actions">
                <button mat-raised-button color="primary" matStepperNext type="button"
                        [disabled]="basicGroup.invalid">
                  Next <mat-icon>arrow_forward</mat-icon>
                </button>
              </div>
            </div>
          </mat-step>

          <!-- Step 2: Car Details -->
          <mat-step label="Car Details" [stepControl]="carGroup">
            <div formGroupName="car" class="step-content">
              <div class="form-row">
                <mat-form-field appearance="outline">
                  <mat-label>Brand</mat-label>
                  <mat-select formControlName="brandId">
                    @for (brand of brands(); track brand.id) {
                      <mat-option [value]="brand.id">{{ brand.name }}</mat-option>
                    }
                  </mat-select>
                  <mat-error>Brand is required</mat-error>
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>Model</mat-label>
                  <input matInput formControlName="model" placeholder="e.g. 320d">
                  <mat-error>Model is required</mat-error>
                </mat-form-field>
              </div>

              <div class="form-row">
                <mat-form-field appearance="outline">
                  <mat-label>Registration Year</mat-label>
                  <input matInput type="number" formControlName="registrationYear"
                         [min]="1950" [max]="currentYear">
                  <mat-error>Valid year required</mat-error>
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>Mileage (km)</mat-label>
                  <input matInput type="number" formControlName="kilometers" min="0">
                  <mat-error>Mileage is required</mat-error>
                </mat-form-field>
              </div>

              <div class="form-row">
                <mat-form-field appearance="outline">
                  <mat-label>Fuel Type</mat-label>
                  <mat-select formControlName="fuelTypeId">
                    @for (ft of fuelTypes(); track ft.id) {
                      <mat-option [value]="ft.id">{{ ft.name }}</mat-option>
                    }
                  </mat-select>
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>Body Type</mat-label>
                  <mat-select formControlName="bodyTypeId">
                    @for (bt of bodyTypes(); track bt.id) {
                      <mat-option [value]="bt.id">{{ bt.name }}</mat-option>
                    }
                  </mat-select>
                </mat-form-field>
              </div>

              <div class="form-row">
                <mat-form-field appearance="outline">
                  <mat-label>Transmission</mat-label>
                  <mat-select formControlName="transmissionTypeId">
                    @for (tt of transmissions(); track tt.id) {
                      <mat-option [value]="tt.id">{{ tt.name }}</mat-option>
                    }
                  </mat-select>
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>Condition</mat-label>
                  <mat-select formControlName="conditionTypeId">
                    @for (ct of conditions(); track ct.id) {
                      <mat-option [value]="ct.id">{{ ct.name }}</mat-option>
                    }
                  </mat-select>
                </mat-form-field>
              </div>

              <div class="form-row">
                <mat-form-field appearance="outline">
                  <mat-label>Power (kW)</mat-label>
                  <input matInput type="number" formControlName="kilowatts" min="0">
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>Doors</mat-label>
                  <input matInput type="number" formControlName="numDoors" min="2" max="6">
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>Seats</mat-label>
                  <input matInput type="number" formControlName="numSeats" min="1" max="9">
                </mat-form-field>
              </div>

              <div class="step-actions">
                <button mat-button matStepperPrevious type="button">
                  <mat-icon>arrow_back</mat-icon> Back
                </button>
                <button mat-raised-button color="primary" matStepperNext type="button"
                        [disabled]="carGroup.invalid">
                  Next <mat-icon>arrow_forward</mat-icon>
                </button>
              </div>
            </div>
          </mat-step>

          <!-- Step 3: Review & Submit -->
          <mat-step label="Review & Submit">
            <div class="step-content review-step">
              <div class="review-card">
                <h3>{{ basicGroup.get('title')?.value || '—' }}</h3>
                <p class="review-price">€{{ basicGroup.get('price')?.value | number:'1.0-0' }}</p>
                <div class="review-specs">
                  <span>{{ carGroup.get('registrationYear')?.value }} · {{ carGroup.get('kilometers')?.value | number }} km</span>
                </div>
              </div>

              @if (error()) {
                <div class="error-message">{{ error() }}</div>
              }

              <p class="review-note">
                <mat-icon inline>info</mat-icon>
                Your listing will be reviewed by our team before going live.
              </p>

              <div class="step-actions">
                <button mat-button matStepperPrevious type="button">
                  <mat-icon>arrow_back</mat-icon> Back
                </button>
                <button mat-raised-button color="primary" type="submit"
                        [disabled]="form.invalid || isSubmitting()">
                  @if (isSubmitting()) {
                    <mat-spinner diameter="20"></mat-spinner>
                  } @else {
                    <mat-icon>check</mat-icon> Post Listing
                  }
                </button>
              </div>
            </div>
          </mat-step>

        </mat-stepper>
      </form>
    </div>
  `,
  styles: [`
    .create-page { max-width: 800px; margin: 0 auto; padding: 32px 24px; }
    .create-header { margin-bottom: 32px; }
    .create-header h1 { margin-bottom: 8px; }
    .step-content { padding: 16px 0 24px; display: flex; flex-direction: column; gap: 0; }
    .full-width { width: 100%; }
    .form-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 16px; margin-bottom: 8px; }
    .step-actions { display: flex; gap: 12px; margin-top: 16px; }
    .review-card { background: #f8f9fa; border-radius: 8px; padding: 20px; margin-bottom: 16px; }
    .review-card h3 { margin-bottom: 8px; }
    .review-price { font-size: 1.5rem; font-weight: 700; color: #1a7f64; margin-bottom: 4px; }
    .review-specs { color: #666; font-size: 0.9rem; }
    .review-note { display: flex; align-items: center; gap: 6px; color: #666; font-size: 0.875rem; margin-bottom: 16px; }
    .review-note mat-icon { color: #1a7f64; font-size: 18px; }
    .error-message { color: #ef4444; font-size: 0.875rem; padding: 12px 16px; background: #fef2f2; border-radius: 6px; margin-bottom: 16px; }
    .text-muted { color: #6b7280; }
  `]
})
export class CreateListingComponent implements OnInit {
  private readonly api    = inject(ApiService);
  private readonly router = inject(Router);
  private readonly snack  = inject(MatSnackBar);
  private readonly fb     = inject(FormBuilder);

  readonly brands       = signal<ReferenceItem[]>([]);
  readonly fuelTypes    = signal<ReferenceItem[]>([]);
  readonly bodyTypes    = signal<ReferenceItem[]>([]);
  readonly transmissions = signal<ReferenceItem[]>([]);
  readonly conditions   = signal<ReferenceItem[]>([]);
  readonly cities       = signal<ReferenceItem[]>([]);
  readonly isSubmitting = signal(false);
  readonly error        = signal<string | null>(null);

  readonly currentYear = new Date().getFullYear();

  form!: FormGroup;

  get basicGroup() { return this.form.get('basic') as FormGroup; }
  get carGroup()   { return this.form.get('car') as FormGroup; }

  ngOnInit(): void {
    this.form = this.fb.group({
      basic: this.fb.group({
        title:       ['', [Validators.required, Validators.minLength(5)]],
        price:       [null, [Validators.required, Validators.min(0)]],
        cityId:      [''],
        description: [''],
      }),
      car: this.fb.group({
        brandId:           ['', Validators.required],
        model:             ['', Validators.required],
        registrationYear:  [null, [Validators.required, Validators.min(1950), Validators.max(this.currentYear)]],
        kilometers:        [null, [Validators.required, Validators.min(0)]],
        fuelTypeId:        [''],
        bodyTypeId:        [''],
        transmissionTypeId: [''],
        conditionTypeId:   [''],
        kilowatts:         [null],
        numDoors:          [null],
        numSeats:          [null],
      }),
    });

    // Load reference data in parallel
    this.api.getBrands().subscribe(b => this.brands.set(b));
    this.api.getFuelTypes().subscribe(f => this.fuelTypes.set(f));
    this.api.getBodyTypes().subscribe(b => this.bodyTypes.set(b));
    this.api.getTransmissionTypes().subscribe(t => this.transmissions.set(t));
    this.api.getConditionTypes().subscribe(c => this.conditions.set(c));
    this.api.getCities().subscribe(c => this.cities.set(c));
  }

  submit(): void {
    if (this.form.invalid) return;
    this.isSubmitting.set(true);
    this.error.set(null);

    const basic = this.basicGroup.value;
    const car   = this.carGroup.value;

    const payload = {
      title:       basic.title,
      description: basic.description,
      price:       basic.price,
      cityId:      basic.cityId || undefined,
      carDetails: {
        brandId:            car.brandId,
        model:              car.model,
        registrationYear:   car.registrationYear,
        kilometers:         car.kilometers,
        fuelTypeId:         car.fuelTypeId || undefined,
        bodyTypeId:         car.bodyTypeId || undefined,
        transmissionTypeId: car.transmissionTypeId || undefined,
        conditionTypeId:    car.conditionTypeId || undefined,
        kilowatts:          car.kilowatts || undefined,
        numDoors:           car.numDoors || undefined,
        numSeats:           car.numSeats || undefined,
      },
    };

    this.api.createListing(payload).subscribe({
      next: listing => {
        this.snack.open('Listing created! It will be reviewed shortly.', 'OK', { duration: 5000 });
        this.router.navigate(['/listings', listing.slug]);
      },
      error: err => {
        this.error.set(err.error?.message || 'Failed to create listing. Please try again.');
        this.isSubmitting.set(false);
      },
    });
  }
}
