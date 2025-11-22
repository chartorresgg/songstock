export interface ProductImage {
    id: number;
    imageUrl: string;
    altText?: string;
    isPrimary: boolean;
    displayOrder: number;
  }

export interface Product {
    id: number;
    albumId: number;
    albumTitle: string;
    artistName: string;
    providerId: number;
    providerName: string;
    categoryId: number;
    categoryName: string;
    sku: string;
    productType: 'PHYSICAL' | 'DIGITAL';
    conditionType: 'NEW' | 'LIKE_NEW' | 'VERY_GOOD' | 'GOOD' | 'ACCEPTABLE';
    price: number;
    stockQuantity: number;
    lowStockThreshold?: number;
    vinylSize?: 'SEVEN_INCH' | 'TEN_INCH' | 'TWELVE_INCH' | null;
    vinylSpeed?: 'RPM_33' | 'RPM_45' | 'RPM_78' | null;
    weightGrams?: number | null;
    fileFormat?: string | null;
    fileSizeMb?: number | null;
    isActive: boolean;
    featured: boolean;
    createdAt: number[];
    updatedAt: number[];
    images?: ProductImage[];
    alternativeFormats: any;
    physical: boolean;
    digital: boolean;
    inStock: boolean;
  }
  
  export interface Album {
    id: number;
    title: string;
    releaseYear: number;
    coverImageUrl?: string;
    artistId: number;
    artistName: string;
    genreId: number;
    genreName: string;
  }
  
  export interface CartItem {
    product: Product;
    quantity: number;
  }

  export interface ProductCatalogResponse {
    id: number;
    albumId: number;
    albumTitle: string;
    artistName: string;
    providerId: number;
    providerName: string;
    categoryId: number;
    categoryName: string;
    sku: string;
    productType: 'PHYSICAL' | 'DIGITAL';
    conditionType: 'NEW' | 'LIKE_NEW' | 'VERY_GOOD' | 'GOOD' | 'ACCEPTABLE';
    price: number;
    stockQuantity: number;
    vinylSize?: string | null;
    vinylSpeed?: string | null;
    weightGrams?: number | null;
    fileFormat?: string | null;
    fileSizeMb?: number | null;
    isActive: boolean;
    featured: boolean;
    createdAt: string | number[];
    updatedAt: string | number[];
    physical: boolean;
    digital: boolean;
    inStock: boolean;
  }