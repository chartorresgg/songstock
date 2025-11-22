import apiClient from './axios.instance';
import { Song } from '../types/api.types';

export interface SongSearchFilters {
  query?: string;
  genreId?: number;
}

const songService = {
  // ✅ AGREGAR ESTE MÉTODO
  searchAvailableSongs: async (filters: SongSearchFilters): Promise<Song[]> => {
    const params = new URLSearchParams();
    if (filters.query) params.append('q', filters.query);
    if (filters.genreId) params.append('genreId', filters.genreId.toString());
    
    const response = await apiClient.get(`/songs/search?${params}`);
    
    // Manejar respuesta con wrapper ApiResponse
    if (response.data && response.data.data) {
      return response.data.data;
    }
    return response.data || [];
  },

  // Buscar canciones (búsqueda general)
  searchSongs: async (query: string): Promise<Song[]> => {
    const response = await apiClient.get(`/songs/search`, {
      params: { q: query }
    });
    return response.data.data || [];
  },

  // Obtener vinilos disponibles para una canción
  getSongWithVinyls: async (songId: number): Promise<Song> => {
    const response = await apiClient.get(`/songs/${songId}/vinyls`);
    return response.data.data;
  },

  // Obtener canciones de un álbum
  getSongsByAlbum: async (albumId: number): Promise<Song[]> => {
    const response = await apiClient.get(`/songs/album/${albumId}`);
    return response.data.data || [];
  },

  // Obtener tracklist de un producto
  getProductTracklist: async (productId: number): Promise<Song[]> => {
    const response = await apiClient.get(`/products/${productId}/tracklist`);
    return response.data.data || [];
  },

  // Crear canción
  createSong: async (song: Partial<Song>): Promise<Song> => {
    const response = await apiClient.post('/songs', song);
    return response.data.data;
  },

  // Actualizar canción
  updateSong: async (id: number, song: Partial<Song>): Promise<Song> => {
    const response = await apiClient.put(`/songs/${id}`, song);
    return response.data.data;
  },

  // Eliminar canción
  deleteSong: async (id: number): Promise<void> => {
    await apiClient.delete(`/songs/${id}`);
  },

  // Crear múltiples canciones
  createSongsBatch: async (songs: Partial<Song>[]): Promise<Song[]> => {
    const response = await apiClient.post('/songs/batch', songs);
    return response.data.data || [];
  }
};

export default songService;