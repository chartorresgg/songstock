import axiosInstance from './axios.instance';
import { API_ENDPOINTS } from '../config/api.config';
import { Product, Album } from '../types/product.types';
import { ApiResponse } from '../types/api.types';

// Interfaces para formatos alternativos
export interface FormatAvailability {
  productId: number;
  productType: 'PHYSICAL' | 'DIGITAL';
  price: number;
  stockQuantity: number;
  isActive: boolean;
  vinylSize?: string;
  vinylSpeed?: string;
  conditionType?: string;
  fileFormat?: string;
  audioQuality?: string;
}

export interface AlbumFormatsResponse {
  albumId: number;
  albumTitle: string;
  artistName: string;
  hasDigitalFormat: boolean;
  hasVinylFormat: boolean;
  availableFormats: FormatAvailability[];
}

class ProductService {
  async getProducts(params?: {
    page?: number;
    size?: number;
    search?: string;
    productType?: string;
    minPrice?: number;
    maxPrice?: number;
    genreId?: number;
    artistId?: number;
  }) {
    const response = await axiosInstance.get<ApiResponse<Product[]>>(
      API_ENDPOINTS.PRODUCTS,
      { params }
    );
    
    const products = response.data.data || [];
    
    return {
      content: products,
      totalElements: products.length,
      totalPages: 1,
      size: products.length,
      number: 0,
    };
  }

  async getProductById(id: number) {
    const response = await axiosInstance.get<ApiResponse<Product>>(
      `${API_ENDPOINTS.PRODUCTS}/${id}`
    );
    return response.data.data;
  }

  async getAlbumById(id: number) {
    const response = await axiosInstance.get<ApiResponse<Album>>(
      `${API_ENDPOINTS.ALBUMS}/${id}`
    );
    return response.data.data;
  }

  async getAlbums(params?: { page?: number; size?: number }) {
    const response = await axiosInstance.get<ApiResponse<Album[]>>(
      API_ENDPOINTS.ALBUMS,
      { params }
    );
    
    const albums = response.data.data || [];
    
    return {
      content: albums,
      totalElements: albums.length,
      totalPages: 1,
      size: albums.length,
      number: 0,
    };
  }

  // ==================== NUEVOS MÉTODOS PARA HU-21 ====================

  /**
   * Obtener formatos alternativos de un producto específico
   * Endpoint: GET /api/v1/products/{productId}/alternative-formats
   */
  async getAlternativeFormats(productId: number): Promise<Product[]> {
    try {
      const response = await axiosInstance.get<ApiResponse<Product[]>>(
        `${API_ENDPOINTS.PRODUCTS}/${productId}/alternative-formats`
      );
      return response.data.data || [];
    } catch (error) {
      console.error('Error fetching alternative formats:', error);
      return [];
    }
  }

  /**
   * Verificar si un producto tiene formatos alternativos
   * Endpoint: GET /api/v1/products/{productId}/has-alternative
   */
  async hasAlternativeFormat(productId: number): Promise<boolean> {
    try {
      const response = await axiosInstance.get<ApiResponse<boolean>>(
        `${API_ENDPOINTS.PRODUCTS}/${productId}/has-alternative`
      );
      return response.data.data || false;
    } catch (error) {
      console.error('Error checking alternative format:', error);
      return false;
    }
  }

  /**
   * Obtener todos los formatos disponibles de un álbum
   * Endpoint: GET /api/v1/products/album/{albumId}/all-formats
   */
  async getAllAlbumFormats(albumId: number): Promise<AlbumFormatsResponse | null> {
    try {
      const response = await axiosInstance.get<ApiResponse<AlbumFormatsResponse>>(
        `${API_ENDPOINTS.PRODUCTS}/album/${albumId}/all-formats`
      );
      return response.data.data || null;
    } catch (error) {
      console.error('Error fetching album formats:', error);
      return null;
    }
  }

  /**
   * Obtener productos digitales que tienen versión en vinilo
   * Endpoint: GET /api/v1/products/digital-with-vinyl
   */
  async getDigitalProductsWithVinylVersion(): Promise<Product[]> {
    try {
      const response = await axiosInstance.get<ApiResponse<Product[]>>(
        `${API_ENDPOINTS.PRODUCTS}/digital-with-vinyl`
      );
      return response.data.data || [];
    } catch (error) {
      console.error('Error fetching digital products with vinyl:', error);
      return [];
    }
  }

  /**
   * Obtener productos de vinilo que tienen versión digital
   * Endpoint: GET /api/v1/products/vinyl-with-digital
   */
  async getVinylProductsWithDigitalVersion(): Promise<Product[]> {
    try {
      const response = await axiosInstance.get<ApiResponse<Product[]>>(
        `${API_ENDPOINTS.PRODUCTS}/vinyl-with-digital`
      );
      return response.data.data || [];
    } catch (error) {
      console.error('Error fetching vinyl products with digital:', error);
      return [];
    }
  }
}

export default new ProductService();