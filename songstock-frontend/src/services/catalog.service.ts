import axiosInstance from './axios.instance';
import { API_ENDPOINTS } from '../config/api.config';
import { ApiResponse } from '../types/api.types';

export interface Genre {
  id: number;
  name: string;
  description?: string;
  isActive?: boolean;
}

export interface Category {
  id: number;
  name: string;
  description?: string;
  isActive?: boolean;
}

export interface Artist {
  id: number;
  name: string;
  biography?: string;
  imageUrl?: string;
}

class CatalogService {
  async getGenres() {
    try {
      const response = await axiosInstance.get<ApiResponse<Genre[]>>(
        API_ENDPOINTS.GENRES
      );
      
      // Tu backend devuelve { success: true, data: [...] }
      const genres = response.data.data || [];
      
      // Asegurarse de que sea un array
      return Array.isArray(genres) ? genres : [];
    } catch (error) {
      console.error('Error fetching genres:', error);
      return [];
    }
  }

  async getCategories() {
    try {
      const response = await axiosInstance.get<ApiResponse<Category[]>>(
        API_ENDPOINTS.CATEGORIES
      );
      
      const categories = response.data.data || [];
      return Array.isArray(categories) ? categories : [];
    } catch (error) {
      console.error('Error fetching categories:', error);
      return [];
    }
  }

  async getArtists() {
    try {
      const response = await axiosInstance.get<ApiResponse<Artist[]>>(
        API_ENDPOINTS.ARTISTS
      );
      
      const artists = response.data.data || [];
      return Array.isArray(artists) ? artists : [];
    } catch (error) {
      console.error('Error fetching artists:', error);
      return [];
    }
  }
}

export default new CatalogService();