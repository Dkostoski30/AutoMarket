export interface UserDto {
  id: string;
  email: string;
  name: string;
  phone?: string;
  cityName?: string;
  plan: 'FREE' | 'PREMIUM';
  roles: string[];
  createdAt: string;
}

export interface UserProfileDto {
  id: string;
  name: string;
  cityName?: string;
  memberSince: string;
  totalListings: number;
}

export interface TokenPair {
  accessToken: string;
  refreshToken: string;
  accessTokenExpiresInMs: number;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  name: string;
  phone?: string;
  cityId?: string;
}
