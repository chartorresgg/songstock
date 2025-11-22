import axiosInstance from './axios.instance';
import { Compilation, Song } from '../types/compilation.types';
import { ApiResponse } from '../types/api.types';

class CompilationService {
  
  async getMyCompilations(): Promise<Compilation[]> {
    const response = await axiosInstance.get<ApiResponse<Compilation[]>>('/compilations');
    return response.data.data;
  }

  async getCompilationById(id: number): Promise<Compilation> {
    const response = await axiosInstance.get<ApiResponse<Compilation>>(`/compilations/${id}`);
    return response.data.data;
  }

  async createCompilation(data: { name: string; description?: string; isPublic?: boolean }): Promise<Compilation> {
    const response = await axiosInstance.post<ApiResponse<Compilation>>('/compilations', data);
    return response.data.data;
  }

    async updateCompilation(id: number, data: { name?: string; description?: string; isPublic?: boolean }): Promise<Compilation> {
        const response = await axiosInstance.put<ApiResponse<Compilation>>(`/compilations/${id}`, data);
        return response.data.data;
      }
    

  async addSongToCompilation(compilationId: number, songId: number): Promise<Compilation> {
    const response = await axiosInstance.post<ApiResponse<Compilation>>(
      `/compilations/${compilationId}/songs/${songId}`
    );
    return response.data.data;
  }

  async removeSongFromCompilation(compilationId: number, songId: number): Promise<void> {
    await axiosInstance.delete(`/compilations/${compilationId}/songs/${songId}`);
  }

  async deleteCompilation(id: number): Promise<void> {
    await axiosInstance.delete(`/compilations/${id}`);
  }

    async getPublicCompilations(params?: { 
        name?: string; 
        minSongs?: number; 
        maxSongs?: number; 
      }): Promise<Compilation[]> {
        const response = await axiosInstance.get<ApiResponse<Compilation[]>>('/compilations/public', { params });
        return response.data.data;
      }
    
      async getPublicCompilationById(id: number): Promise<Compilation> {
        const response = await axiosInstance.get<ApiResponse<Compilation>>(`/compilations/public/${id}`);
        return response.data.data;
      }
    
      async cloneCompilation(id: number): Promise<Compilation> {
        const response = await axiosInstance.post<ApiResponse<Compilation>>(`/compilations/${id}/clone`);
        return response.data.data;
      }
    

  async getSongsByAlbum(albumId: number): Promise<Song[]> {
    const response = await axiosInstance.get<ApiResponse<Song[]>>(`/songs/album/${albumId}`);
    return response.data.data;
  }
}

export default new CompilationService();