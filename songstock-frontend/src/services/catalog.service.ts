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

class CatalogService {
  async getGenres(): Promise<Genre[]> {
    try {
      const response = await axiosInstance.get<ApiResponse<Genre[]>>(
        API_ENDPOINTS.GENRES
      );
      
      const genres = response.data.data || [];
      return Array.isArray(genres) ? genres : [];
    } catch (error) {
      console.error('Error fetching genres:', error);
      return [];
    }
  }

  async getCategories(): Promise<Category[]> {
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

  async getArtists(): Promise<Artist[]> {
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

  async getAlbums(): Promise<Album[]> {
    try {
      const response = await axiosInstance.get<ApiResponse<Album[]>>(
        API_ENDPOINTS.ALBUMS
      );
      
      const albums = response.data.data || [];
      return Array.isArray(albums) ? albums : [];
    } catch (error) {
      console.error('Error fetching albums:', error);
      return [];
    }
  }

  async getAlbumById(id: number): Promise<Album> {
    try {
      const response = await axiosInstance.get<ApiResponse<Album>>(
        `${API_ENDPOINTS.ALBUMS}/${id}`
      );
      
      return response.data.data;
    } catch (error) {
      console.error(`Error fetching album ${id}:`, error);
      throw error;
    }
  }
}

export default new CatalogService();