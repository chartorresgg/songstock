export interface Artist {
    id: number;
    name: string;
    bio?: string;
    country?: string;
    formedYear?: number;
    isActive: boolean;
    createdAt: string;
    updatedAt: string;
    albumCount: number;
  }
  
  export interface Genre {
    id: number;
    name: string;
    description?: string;
    isActive: boolean;
    createdAt: string;
    albumCount: number;
  }
  
  export interface Album {
    id: number;
    title: string;
    artistId: number;
    artistName: string;
    genreId?: number;
    genreName?: string;
    releaseYear: number;
    label?: string;
    catalogNumber?: string;
    description?: string;
    durationMinutes?: number;
    isActive: boolean;
    createdAt: string;
    updatedAt: string;
    productCount: number;
    hasVinylVersion: boolean;
    hasDigitalVersion: boolean;
  }
  
  export interface Category {
    id: number;
    name: string;
    description?: string;
    isActive: boolean;
    createdAt: string;
    updatedAt: string;
  }
  
  export enum ProductType {
    DIGITAL = 'DIGITAL',
    PHYSICAL = 'PHYSICAL'
  }
  
  export enum ConditionType {
    NEW = 'NEW',
    LIKE_NEW = 'LIKE_NEW',
    VERY_GOOD = 'VERY_GOOD',
    GOOD = 'GOOD',
    FAIR = 'FAIR',
    POOR = 'POOR'
  }
  
  export enum VinylSize {
    SEVEN_INCH = '7_INCH',
    TEN_INCH = '10_INCH',
    TWELVE_INCH = '12_INCH'
  }
  
  export enum VinylSpeed {
    RPM_33 = '33_RPM',
    RPM_45 = '45_RPM',
    RPM_78 = '78_RPM'
  }
  
  export interface ProductCatalogCreate {
    albumId: number;
    categoryId: number;
    sku: string;
    productType: ProductType;
    conditionType: ConditionType;
    price: number;
    stockQuantity: number;
    // Campos específicos para vinilos físicos
    vinylSize?: VinylSize;
    vinylSpeed?: VinylSpeed;
    weightGrams?: number;
    // Campos específicos para productos digitales
    fileFormat?: string;
    fileSizeMb?: number;
    featured?: boolean;
    description?: string;
  }
  
  export interface ProductCatalogUpdate {
    sku?: string;
    conditionType?: ConditionType;
    price?: number;
    stockQuantity?: number;
    featured?: boolean;
    description?: string;
    vinylSize?: VinylSize;
    vinylSpeed?: VinylSpeed;
    weightGrams?: number;
    fileFormat?: string;
    fileSizeMb?: number;
    updateReason?: string;
  }
  
  export interface ProductCatalogResponse {
    id: number;
    sku: string;
    albumId: number;
    albumTitle: string;
    artistName: string;
    releaseYear: number;
    productType: string;
    conditionType: string;
    price: number;
    stockQuantity: number;
    isAvailable: boolean;
    vinylSize?: string;
    vinylSpeed?: string;
    weightGrams?: number;
    fileFormat?: string;
    fileSizeMb?: number;
    featured: boolean;
    isActive: boolean;
    providerId: number;
    providerBusinessName: string;
    categoryId: number;
    categoryName: string;
    genreId?: number;
    genreName?: string;
    createdAt: string;
    updatedAt: string;
  }
  
  export interface ProviderCatalogSummary {
    providerId: number;
    providerName: string;
    totalProducts: number;
    activeProducts: number;
    inactiveProducts: number;
    featuredProducts: number;
    productsInStock: number;
    productsOutOfStock: number;
    physicalProducts: number;
    digitalProducts: number;
    newProducts: number;
    usedProducts: number;
    averagePrice: number;
    totalCatalogValue: number;
    products: ProductCatalogResponse[];
  }
  
  export interface ApiResponse<T> {
    success: boolean;
    message: string;
    data: T;
  }