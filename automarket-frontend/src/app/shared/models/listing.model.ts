export interface ListingDto {
  id: string;
  slug: string;
  title: string;
  price: number;
  featured: boolean;
  approved: boolean;
  createdAt: string;
  thumbnailUrl?: string;
  carDetails: CarDetailsSummary;
  seller: SellerSummary;
}

export interface ListingDetailDto {
  id: string;
  slug: string;
  title: string;
  description: string;
  price: number;
  featured: boolean;
  approved: boolean;
  createdAt: string;
  images: ListingImage[];
  carDetails: CarDetailsFull;
  seller: SellerDetail;
  condition?: ReferenceItem;
}

export interface CarDetailsSummary {
  brandName?: string;
  model: string;
  registrationYear: number;
  kilometers: number;
  fuelTypeName?: string;
  bodyTypeName?: string;
  transmissionTypeName?: string;
  kilowatts?: number;
}

export interface CarDetailsFull extends CarDetailsSummary {
  brandId?: string;
  fuelTypeId?: string;
  bodyTypeId?: string;
  transmissionTypeId?: string;
  numDoors?: number;
  numSeats?: number;
}

export interface SellerSummary {
  id: string;
  name: string;
  cityName?: string;
}

export interface SellerDetail extends SellerSummary {
  phone?: string;
  memberSince: string;
}

export interface ListingImage {
  id: string;
  url: string;
  displayOrder: number;
}

export interface ReferenceItem {
  id: string;
  name: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface ListingFilterRequest {
  search?: string;
  priceFrom?: number;
  priceTo?: number;
  yearFrom?: number;
  yearTo?: number;
  kilometersFrom?: number;
  kilometersTo?: number;
  kilowattsFrom?: number;
  kilowattsTo?: number;
  fuelTypeIds?: string[];
  bodyTypeIds?: string[];
  conditionTypeIds?: string[];
  transmissionTypeIds?: string[];
  brandIds?: string[];
  cityId?: string;
  featured?: boolean;
  sortBy?: 'price' | 'year' | 'createdAt';
  sortDir?: 'asc' | 'desc';
}
