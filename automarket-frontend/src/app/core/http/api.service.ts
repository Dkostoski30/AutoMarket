import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ListingDto, ListingDetailDto, ListingFilterRequest, PageResponse
} from '../../shared/models/listing.model';
import { ReferenceItem } from '../../shared/models/listing.model';

/**
 * Central API service — all HTTP calls go through here.
 * Feature services inject this and use domain-specific methods.
 */
@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;

  // ─── Listings ────────────────────────────────────────────────────

  getListings(filter: ListingFilterRequest = {}, page = 0, size = 12): Observable<PageResponse<ListingDto>> {
    const params = this.buildParams({ ...filter, page, size });
    return this.http.get<PageResponse<ListingDto>>(`${this.base}/listings`, { params });
  }

  getListingById(id: string): Observable<ListingDetailDto> {
    return this.http.get<ListingDetailDto>(`${this.base}/listings/${id}`);
  }

  getListingBySlug(slug: string): Observable<ListingDetailDto> {
    return this.http.get<ListingDetailDto>(`${this.base}/listings/slug/${slug}`);
  }

  getFeaturedListings(): Observable<ListingDto[]> {
    return this.http.get<ListingDto[]>(`${this.base}/listings/featured`);
  }

  getMyListings(page = 0, size = 10): Observable<PageResponse<ListingDto>> {
    return this.http.get<PageResponse<ListingDto>>(`${this.base}/listings/my`, {
      params: this.buildParams({ page, size })
    });
  }

  createListing(payload: unknown): Observable<ListingDetailDto> {
    return this.http.post<ListingDetailDto>(`${this.base}/listings`, payload);
  }

  updateListing(id: string, payload: unknown): Observable<ListingDetailDto> {
    return this.http.put<ListingDetailDto>(`${this.base}/listings/${id}`, payload);
  }

  deleteListing(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/listings/${id}`);
  }

  uploadImage(listingId: string, file: File): Observable<ListingDetailDto> {
    const form = new FormData();
    form.append('file', file);
    return this.http.post<ListingDetailDto>(`${this.base}/listings/${listingId}/images`, form);
  }

  deleteImage(listingId: string, imageId: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/listings/${listingId}/images/${imageId}`);
  }

  addFavorite(listingId: string): Observable<void> {
    return this.http.post<void>(`${this.base}/listings/${listingId}/favorite`, {});
  }

  removeFavorite(listingId: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/listings/${listingId}/favorite`);
  }

  // ─── Reference Data ──────────────────────────────────────────────

  getBrands(): Observable<ReferenceItem[]> {
    return this.http.get<ReferenceItem[]>(`${this.base}/reference/car-brands`);
  }

  getFuelTypes(): Observable<ReferenceItem[]> {
    return this.http.get<ReferenceItem[]>(`${this.base}/reference/fuel-types`);
  }

  getBodyTypes(): Observable<ReferenceItem[]> {
    return this.http.get<ReferenceItem[]>(`${this.base}/reference/body-types`);
  }

  getConditionTypes(): Observable<ReferenceItem[]> {
    return this.http.get<ReferenceItem[]>(`${this.base}/reference/condition-types`);
  }

  getTransmissionTypes(): Observable<ReferenceItem[]> {
    return this.http.get<ReferenceItem[]>(`${this.base}/reference/transmission-types`);
  }

  getCities(): Observable<ReferenceItem[]> {
    return this.http.get<ReferenceItem[]>(`${this.base}/reference/cities`);
  }

  // ─── Blog ─────────────────────────────────────────────────────────

  getBlogs(page = 0, size = 6): Observable<PageResponse<any>> {
    return this.http.get<PageResponse<any>>(`${this.base}/blog`, {
      params: this.buildParams({ page, size })
    });
  }

  getBlogById(id: string): Observable<any> {
    return this.http.get<any>(`${this.base}/blog/${id}`);
  }

  // ─── Inquiries ────────────────────────────────────────────────────

  sendInquiry(listingId: string, message: string): Observable<any> {
    return this.http.post<any>(`${this.base}/inquiries`, { listingId, message });
  }

  getReceivedInquiries(page = 0, size = 20): Observable<PageResponse<any>> {
    return this.http.get<PageResponse<any>>(`${this.base}/inquiries/received`, {
      params: this.buildParams({ page, size })
    });
  }

  // ─── Dashboard ────────────────────────────────────────────────────

  getDashboard(): Observable<Record<string, number>> {
    return this.http.get<Record<string, number>>(`${this.base}/admin/dashboard`);
  }

  // ─── Moderation ───────────────────────────────────────────────────

  getPendingListings(page = 0, size = 20): Observable<PageResponse<ListingDetailDto>> {
    return this.http.get<PageResponse<ListingDetailDto>>(`${this.base}/moderation/listings`, {
      params: this.buildParams({ page, size })
    });
  }

  approveListing(id: string): Observable<void> {
    return this.http.post<void>(`${this.base}/moderation/listings/${id}/approve`, {});
  }

  rejectListing(id: string, reason?: string): Observable<void> {
    return this.http.post<void>(`${this.base}/moderation/listings/${id}/reject`, { reason });
  }

  // ─── Helpers ──────────────────────────────────────────────────────

  private buildParams(obj: Record<string, any>): HttpParams {
    let params = new HttpParams();
    Object.entries(obj).forEach(([key, value]) => {
      if (value !== null && value !== undefined && value !== '') {
        if (Array.isArray(value)) {
          value.forEach(v => params = params.append(key, v));
        } else {
          params = params.set(key, String(value));
        }
      }
    });
    return params;
  }
}
