import { Injectable, computed, signal } from '@angular/core';
import { UserDto, TokenPair } from '../../shared/models/user.model';

interface AuthState {
  user: UserDto | null;
  accessToken: string | null;
  refreshToken: string | null;
  isLoading: boolean;
}

const REFRESH_TOKEN_KEY = 'am_refresh_token';
const USER_KEY = 'am_user';

/**
 * Signal-based auth store — single source of truth for authentication state.
 * Access token is held only in memory (not persisted) for security.
 * Refresh token is persisted in localStorage.
 */
@Injectable({ providedIn: 'root' })
export class AuthStore {
  private readonly state = signal<AuthState>({
    user: this.loadPersistedUser(),
    accessToken: null,
    refreshToken: localStorage.getItem(REFRESH_TOKEN_KEY),
    isLoading: false,
  });

  // Public computed signals
  readonly user    = computed(() => this.state().user);
  readonly isLoggedIn = computed(() => !!this.state().accessToken);
  readonly isLoading  = computed(() => this.state().isLoading);
  readonly accessToken = computed(() => this.state().accessToken);

  readonly isAdmin     = computed(() =>
    this.state().user?.roles.includes('ROLE_ADMIN') ?? false);
  readonly isModerator = computed(() =>
    this.state().user?.roles.includes('ROLE_MODERATOR') ?? false ||
    this.state().user?.roles.includes('ROLE_ADMIN') ?? false);
  readonly isPremium   = computed(() =>
    this.state().user?.plan === 'PREMIUM');

  readonly hasStoredSession = computed(() => !!this.state().refreshToken);

  setTokenPair(pair: TokenPair, user: UserDto): void {
    localStorage.setItem(REFRESH_TOKEN_KEY, pair.refreshToken);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    this.state.set({
      user,
      accessToken: pair.accessToken,
      refreshToken: pair.refreshToken,
      isLoading: false,
    });
  }

  setAccessToken(accessToken: string): void {
    this.state.update(s => ({ ...s, accessToken }));
  }

  getRefreshToken(): string | null {
    return this.state().refreshToken;
  }

  setLoading(isLoading: boolean): void {
    this.state.update(s => ({ ...s, isLoading }));
  }

  clearSession(): void {
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.state.set({ user: null, accessToken: null, refreshToken: null, isLoading: false });
  }

  private loadPersistedUser(): UserDto | null {
    try {
      const json = localStorage.getItem(USER_KEY);
      return json ? JSON.parse(json) : null;
    } catch {
      return null;
    }
  }
}
