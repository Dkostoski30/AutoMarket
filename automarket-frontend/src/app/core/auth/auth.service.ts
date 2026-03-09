import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthStore } from './auth.store';
import { LoginRequest, RegisterRequest, TokenPair, UserDto } from '../../shared/models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http   = inject(HttpClient);
  private readonly store  = inject(AuthStore);
  private readonly router = inject(Router);
  private readonly base   = `${environment.apiUrl}/auth`;

  login(credentials: LoginRequest): Observable<TokenPair> {
    return this.http.post<TokenPair>(`${this.base}/login`, credentials).pipe(
      tap(pair => this.onAuthSuccess(pair))
    );
  }

  register(request: RegisterRequest): Observable<TokenPair> {
    return this.http.post<TokenPair>(`${this.base}/register`, request).pipe(
      tap(pair => this.onAuthSuccess(pair))
    );
  }

  refresh(): Observable<TokenPair> {
    const refreshToken = this.store.getRefreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token'));
    }
    return this.http.post<TokenPair>(`${this.base}/refresh`, { refreshToken }).pipe(
      tap(pair => this.store.setAccessToken(pair.accessToken))
    );
  }

  logout(): void {
    this.http.post(`${this.base}/logout`, {}).subscribe({ error: () => {} });
    this.store.clearSession();
    this.router.navigate(['/']);
  }

  loadCurrentUser(): Observable<UserDto> {
    return this.http.get<UserDto>(`${environment.apiUrl}/users/me`).pipe(
      tap(user => {
        if (this.store.accessToken()) {
          this.store.setTokenPair(
            { accessToken: this.store.accessToken()!, refreshToken: this.store.getRefreshToken()!, accessTokenExpiresInMs: 0 },
            user
          );
        }
      })
    );
  }

  private onAuthSuccess(pair: TokenPair): void {
    // Temporarily set access token so the next /users/me call is authenticated
    this.store.setAccessToken(pair.accessToken);
    this.loadCurrentUser().subscribe({
      next: user => this.store.setTokenPair(pair, user),
      error: () => this.store.clearSession(),
    });
  }
}
