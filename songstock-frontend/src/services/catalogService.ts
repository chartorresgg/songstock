// src/services/catalogService.ts

import  api  from './api';
import { 
  ProductCatalogCreate, 
  ProductCatalogUpdate, 
  ProductCatalogResponse, 
  ProviderCatalogSummary,
  Artist,
  Genre,
  Album,
  Category,
  ApiResponse
} from '../types/catalog';

export const catalogService = {
  // ============ GESTIÓN DE CATÁLOGO DEL PROVEEDOR ============

  /**
   * Obtener resumen completo del catálogo del proveedor autenticado
   */
  async getProviderCatalog(): Promise<ProviderCatalogSummary> {
    const response = await api.get<ApiResponse<ProviderCatalogSummary>>('/products/catalog/summary');
    return response.data.data;
  },

  /**
   * Crear un nuevo producto en el catálogo
   */
  async createCatalogProduct(productData: ProductCatalogCreate): Promise<ProductCatalogResponse> {
    const response = await api.post<ApiResponse<ProductCatalogResponse>>('/products/catalog', productData);
    return response.data.data;
  },

  /**
   * Actualizar un producto del catálogo
   */
  async updateCatalogProduct(productId: number, updateData: ProductCatalogUpdate): Promise<ProductCatalogResponse> {
    const response = await api.put<ApiResponse<ProductCatalogResponse>>(`/products/${productId}/catalog`, updateData);
    return response.data.data;
  },

  /**
   * Activar o desactivar un producto
   */
  async toggleProductStatus(productId: number): Promise<ProductCatalogResponse> {
    const response = await api.patch<ApiResponse<ProductCatalogResponse>>(`/products/${productId}/catalog/toggle-status`);
    return response.data.data;
  },

  /**
   * Eliminar un producto del catálogo (soft delete)
   */
  async deleteCatalogProduct(productId: number): Promise<void> {
    await api.delete(`/products/${productId}/catalog`);
  },

  // ============ GESTIÓN DE ENTIDADES RELACIONADAS ============

  /**
   * Obtener lista de artistas
   */
  async getArtists(): Promise<Artist[]> {
    const response = await api.get<ApiResponse<Artist[]>>('/artists');
    return response.data.data;
  },

  /**
   * Crear un nuevo artista
   */
  async createArtist(artistData: Partial<Artist>): Promise<Artist> {
    const response = await api.post<ApiResponse<Artist>>('/artists', artistData);
    return response.data.data;
  },

  /**
   * Obtener lista de géneros
   */
  async getGenres(): Promise<Genre[]> {
    const response = await api.get<ApiResponse<Genre[]>>('/genres');
    return response.data.data;
  },

  /**
   * Crear un nuevo género
   */
  async createGenre(genreData: Partial<Genre>): Promise<Genre> {
    const response = await api.post<ApiResponse<Genre>>('/genres', genreData);
    return response.data.data;
  },

  /**
   * Obtener lista de álbumes
   */
  async getAlbums(): Promise<Album[]> {
    const response = await api.get<ApiResponse<Album[]>>('/albums');
    return response.data.data;
  },

  /**
   * Crear un nuevo álbum
   */
  async createAlbum(albumData: Partial<Album>): Promise<Album> {
    const response = await api.post<ApiResponse<Album>>('/albums', albumData);
    return response.data.data;
  },

  /**
   * Obtener lista de categorías
   */
  async getCategories(): Promise<Category[]> {
    const response = await api.get<ApiResponse<Category[]>>('/categories');
    return response.data.data;
  },

  // ============ MÉTODOS DE UTILIDAD ============

  /**
   * Buscar álbumes por artista
   */
  async getAlbumsByArtist(artistId: number): Promise<Album[]> {
    const response = await api.get<ApiResponse<Album[]>>(`/albums/artist/${artistId}`);
    return response.data.data;
  },

  /**
   * Validar si un SKU está disponible
   */
  async validateSku(sku: string): Promise<boolean> {
    try {
      await api.get(`/products/sku/${sku}`);
      return false; // Si encuentra el producto, el SKU no está disponible
    } catch (error) {
      return true; // Si no lo encuentra (404), el SKU está disponible
    }
  },

  /**
   * Obtener productos con stock bajo
   */
  async getLowStockProducts(minStock: number = 5): Promise<any[]> {
    const response = await api.get<ApiResponse<any[]>>(`/products/inventory/low-stock?minStock=${minStock}`);
    return response.data.data;
  },

  /**
   * Obtener productos sin stock
   */
  async getOutOfStockProducts(): Promise<any[]> {
    const response = await api.get<ApiResponse<any[]>>('/products/inventory/out-of-stock');
    return response.data.data;
  },

  // ============ BÚSQUEDA Y FILTROS ============

  /**
   * Buscar productos en el catálogo público con filtros
   */
  async searchCatalogProducts(filters: {
    searchQuery?: string;
    categoryId?: number;
    genreId?: number;
    artistId?: number;
    productType?: string;
    conditionType?: string;
    minPrice?: number;
    maxPrice?: number;
    minYear?: number;
    maxYear?: number;
    inStockOnly?: boolean;
    featuredOnly?: boolean;
    activeOnly?: boolean;
    sortBy?: string;
    sortDirection?: string;
  }): Promise<ProductCatalogResponse[]> {
    const params = new URLSearchParams();
    
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        params.append(key, value.toString());
      }
    });

    const response = await api.get<ApiResponse<ProductCatalogResponse[]>>(`/products/catalog/search?${params.toString()}`);
    return response.data.data;
  },

  /**
   * Obtener productos destacados del catálogo público
   */
  async getFeaturedProducts(): Promise<ProductCatalogResponse[]> {
    const response = await api.get<ApiResponse<ProductCatalogResponse[]>>('/products/catalog/featured');
    return response.data.data;
  },

  /**
   * Obtener productos por categoría
   */
  async getProductsByCategory(categoryId: number, inStockOnly: boolean = true): Promise<ProductCatalogResponse[]> {
    const response = await api.get<ApiResponse<ProductCatalogResponse[]>>(
      `/products/catalog/category/${categoryId}?inStockOnly=${inStockOnly}`
    );
    return response.data.data;
  }
};

export default catalogService;