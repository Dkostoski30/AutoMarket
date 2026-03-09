import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { ApiService } from '../../core/http/api.service';
import { ListingDto, ListingFilterRequest, PageResponse, ReferenceItem } from '../../shared/models/listing.model';

@Component({
  selector: 'am-listing-list',
  standalone: true,
  imports: [
    CommonModule, RouterLink, ReactiveFormsModule,
    MatCardModule, MatButtonModule, MatInputModule, MatSelectModule,
    MatChipsModule, MatPaginatorModule, MatProgressSpinnerModule, MatIconModule,
  ],
  template: `
    <div class="listings-page">
      <!-- Filter Panel -->
      <aside class="filter-panel">
        <h3>Filter Cars</h3>
        <form [formGroup]="filterForm" class="filter-form">

          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Search</mat-label>
            <input matInput formControlName="search" placeholder="Brand, model, keywords...">
            <mat-icon matSuffix>search</mat-icon>
          </mat-form-field>

          <div class="range-row">
            <mat-form-field appearance="outline">
              <mat-label>Price from</mat-label>
              <input matInput type="number" formControlName="priceFrom" min="0">
            </mat-form-field>
            <mat-form-field appearance="outline">
              <mat-label>Price to</mat-label>
              <input matInput type="number" formControlName="priceTo" min="0">
            </mat-form-field>
          </div>

          <div class="range-row">
            <mat-form-field appearance="outline">
              <mat-label>Year from</mat-label>
              <input matInput type="number" formControlName="yearFrom">
            </mat-form-field>
            <mat-form-field appearance="outline">
              <mat-label>Year to</mat-label>
              <input matInput type="number" formControlName="yearTo">
            </mat-form-field>
          </div>

          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Brand</mat-label>
            <mat-select formControlName="brandIds" multiple>
              @for (brand of brands(); track brand.id) {
                <mat-option [value]="brand.id">{{ brand.name }}</mat-option>
              }
            </mat-select>
          </mat-form-field>

          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Fuel Type</mat-label>
            <mat-select formControlName="fuelTypeIds" multiple>
              @for (ft of fuelTypes(); track ft.id) {
                <mat-option [value]="ft.id">{{ ft.name }}</mat-option>
              }
            </mat-select>
          </mat-form-field>

          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Body Type</mat-label>
            <mat-select formControlName="bodyTypeIds" multiple>
              @for (bt of bodyTypes(); track bt.id) {
                <mat-option [value]="bt.id">{{ bt.name }}</mat-option>
              }
            </mat-select>
          </mat-form-field>

          <mat-form-field appearance="outline" class="full-width">
            <mat-label>City</mat-label>
            <mat-select formControlName="cityId">
              <mat-option value="">Any city</mat-option>
              @for (city of cities(); track city.id) {
                <mat-option [value]="city.id">{{ city.name }}</mat-option>
              }
            </mat-select>
          </mat-form-field>

          <button mat-stroked-button type="button" (click)="clearFilters()" class="full-width">
            Clear Filters
          </button>
        </form>
      </aside>

      <!-- Results -->
      <section class="results-panel">
        <div class="results-header">
          <span class="results-count">
            @if (!isLoading()) {
              {{ page().totalElements | number }} cars found
            }
          </span>
          <mat-form-field appearance="outline" class="sort-select">
            <mat-label>Sort by</mat-label>
            <mat-select [value]="sortBy()" (selectionChange)="onSortChange($event.value)">
              <mat-option value="createdAt">Latest</mat-option>
              <mat-option value="price_asc">Price: Low to High</mat-option>
              <mat-option value="price_desc">Price: High to Low</mat-option>
              <mat-option value="year_desc">Newest Year</mat-option>
            </mat-select>
          </mat-form-field>
        </div>

        @if (isLoading()) {
          <div class="loading-center">
            <mat-spinner diameter="48"></mat-spinner>
          </div>
        } @else if (listings().length === 0) {
          <div class="empty-state">
            <mat-icon class="empty-icon">search_off</mat-icon>
            <h3>No listings found</h3>
            <p>Try adjusting your filters</p>
          </div>
        } @else {
          <div class="listings-grid">
            @for (listing of listings(); track listing.id) {
              <mat-card class="listing-card" [routerLink]="['/listings', listing.slug]">
                @if (listing.featured) {
                  <div class="featured-badge">Featured</div>
                }
                <div class="card-image">
                  <img [src]="listing.thumbnailUrl || 'assets/no-image.png'"
                       [alt]="listing.title"
                       loading="lazy">
                </div>
                <mat-card-content>
                  <h4 class="listing-title">{{ listing.title }}</h4>
                  <p class="listing-price">€{{ listing.price | number:'1.0-0' }}</p>
                  <div class="listing-meta">
                    <span>{{ listing.carDetails.registrationYear }}</span>
                    <span>·</span>
                    <span>{{ listing.carDetails.kilometers | number }}km</span>
                    <span>·</span>
                    <span>{{ listing.carDetails.fuelTypeName }}</span>
                  </div>
                  @if (listing.seller.cityName) {
                    <p class="listing-city">
                      <mat-icon inline>location_on</mat-icon>
                      {{ listing.seller.cityName }}
                    </p>
                  }
                </mat-card-content>
              </mat-card>
            }
          </div>

          <mat-paginator
            [length]="page().totalElements"
            [pageSize]="pageSize()"
            [pageIndex]="currentPage()"
            [pageSizeOptions]="[6, 12, 24]"
            (page)="onPageChange($event)"
            showFirstLastButtons>
          </mat-paginator>
        }
      </section>
    </div>
  `,
  styles: [`
    .listings-page { display: flex; gap: 24px; max-width: 1400px; margin: 0 auto; padding: 24px; }
    .filter-panel { width: 280px; flex-shrink: 0; }
    .filter-panel h3 { margin: 0 0 16px; font-weight: 600; }
    .filter-form { display: flex; flex-direction: column; gap: 8px; }
    .full-width { width: 100%; }
    .range-row { display: flex; gap: 8px; }
    .range-row mat-form-field { flex: 1; }
    .results-panel { flex: 1; min-width: 0; }
    .results-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
    .results-count { color: #666; font-size: 0.9rem; }
    .sort-select { width: 200px; }
    .listings-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px; }
    .listing-card { cursor: pointer; transition: transform 0.2s, box-shadow 0.2s; position: relative; overflow: hidden; }
    .listing-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(0,0,0,0.15); }
    .featured-badge { position: absolute; top: 12px; right: 12px; background: #f59e0b; color: white;
                      padding: 2px 10px; border-radius: 12px; font-size: 0.75rem; font-weight: 600; z-index: 1; }
    .card-image { height: 180px; overflow: hidden; }
    .card-image img { width: 100%; height: 100%; object-fit: cover; }
    .listing-title { margin: 8px 0 4px; font-weight: 600; font-size: 0.95rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    .listing-price { color: #1a7f64; font-size: 1.25rem; font-weight: 700; margin: 4px 0; }
    .listing-meta { color: #666; font-size: 0.8rem; display: flex; gap: 6px; }
    .listing-city { color: #888; font-size: 0.8rem; display: flex; align-items: center; gap: 2px; margin: 4px 0 0; }
    .loading-center { display: flex; justify-content: center; padding: 80px 0; }
    .empty-state { text-align: center; padding: 80px 0; color: #666; }
    .empty-icon { font-size: 64px; width: 64px; height: 64px; opacity: 0.3; }
  `]
})
export class ListingListComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly fb  = inject(FormBuilder);

  readonly listings = signal<ListingDto[]>([]);
  readonly page     = signal<PageResponse<ListingDto>>({ content: [], page: 0, size: 12, totalElements: 0, totalPages: 0, first: true, last: true });
  readonly isLoading = signal(false);
  readonly currentPage = signal(0);
  readonly pageSize    = signal(12);
  readonly sortBy      = signal('createdAt');

  readonly brands         = signal<ReferenceItem[]>([]);
  readonly fuelTypes      = signal<ReferenceItem[]>([]);
  readonly bodyTypes      = signal<ReferenceItem[]>([]);
  readonly cities         = signal<ReferenceItem[]>([]);

  filterForm!: FormGroup;

  ngOnInit(): void {
    this.filterForm = this.fb.group({
      search: [''],
      priceFrom: [null], priceTo: [null],
      yearFrom: [null], yearTo: [null],
      brandIds: [[]], fuelTypeIds: [[]], bodyTypeIds: [[]], cityId: [''],
    });

    // Load reference data in parallel
    this.api.getBrands().subscribe(b => this.brands.set(b));
    this.api.getFuelTypes().subscribe(f => this.fuelTypes.set(f));
    this.api.getBodyTypes().subscribe(b => this.bodyTypes.set(b));
    this.api.getCities().subscribe(c => this.cities.set(c));

    // React to filter changes with debounce
    this.filterForm.valueChanges.pipe(
      debounceTime(400),
      distinctUntilChanged()
    ).subscribe(() => {
      this.currentPage.set(0);
      this.loadListings();
    });

    this.loadListings();
  }

  loadListings(): void {
    this.isLoading.set(true);
    const filter = this.buildFilter();

    this.api.getListings(filter, this.currentPage(), this.pageSize()).subscribe({
      next: result => {
        this.page.set(result);
        this.listings.set(result.content);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false),
    });
  }

  onPageChange(event: PageEvent): void {
    this.currentPage.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadListings();
  }

  onSortChange(value: string): void {
    this.sortBy.set(value);
    this.loadListings();
  }

  clearFilters(): void {
    this.filterForm.reset({ search: '', brandIds: [], fuelTypeIds: [], bodyTypeIds: [], cityId: '' });
  }

  private buildFilter(): ListingFilterRequest {
    const v = this.filterForm.value;
    const [sortField, sortDir] = this.sortBy().includes('_')
      ? this.sortBy().split('_') : [this.sortBy(), 'desc'];

    return {
      search: v.search || undefined,
      priceFrom: v.priceFrom || undefined, priceTo: v.priceTo || undefined,
      yearFrom: v.yearFrom || undefined, yearTo: v.yearTo || undefined,
      brandIds: v.brandIds?.length ? v.brandIds : undefined,
      fuelTypeIds: v.fuelTypeIds?.length ? v.fuelTypeIds : undefined,
      bodyTypeIds: v.bodyTypeIds?.length ? v.bodyTypeIds : undefined,
      cityId: v.cityId || undefined,
      sortBy: sortField as any,
      sortDir: sortDir as any,
    };
  }
}
