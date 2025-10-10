import axiosInstance from './axios.instance';
import { API_ENDPOINTS } from '../config/api.config';
import { Product, Album } from '../types/product.types';
import { ApiResponse } from '../types/api.types';

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
    
    // Tu backend devuelve: { success: true, message: "...", data: [...] }
    // Necesitamos convertir a formato paginado para el componente
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
}

export default new ProductService();