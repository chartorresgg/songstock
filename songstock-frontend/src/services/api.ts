// ================= ARCHIVO: src/services/api.ts (COMPLETO) =================
import axios from 'axios';
import { API_BASE_URL } from '../constants/api';

// Tipos para las respuestas de la API
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: number[];
}

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 segundos de timeout
});

// Request interceptor para agregar token y debug
api.interceptors.request.use(
  (config) => {
    console.log(`🚀 API Request: ${config.method?.toUpperCase()} ${config.url}`);
    console.log('📝 Request data:', config.data);
    console.log('🌐 Full URL:', `${config.baseURL}${config.url}`);
    
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.error('❌ Request error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor para manejar errores y debug
api.interceptors.response.use(
  (response) => {
    console.log(`✅ API Response: ${response.status} ${response.config.url}`);
    console.log('📄 Response data:', response.data);
    return response;
  },
  (error) => {
    console.error('❌ API Error:', error);
    
    if (error.code === 'ECONNABORTED') {
      console.error('⏰ Request timeout');
    }
    
    if (error.code === 'ERR_NETWORK') {
      console.error('🌐 Network error - Backend might be down');
    }
    
    if (error.response) {
      console.error('📋 Error response:', error.response.data);
      console.error('📊 Error status:', error.response.status);
    }
    
    if (error.response?.status === 401) {
      console.log('🔐 Unauthorized - Redirecting to login');
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      // Solo redirigir si no estamos ya en login
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

// AGREGAR: Objeto authAPI con todos los métodos de autenticación
export const authAPI = {
  /**
   * Iniciar sesión
   */
  login: async (usernameOrEmail: string, password: string): Promise<ApiResponse<any>> => {
    try {
      const response = await api.post('/auth/login', {
        usernameOrEmail,
        password,
      });
      return response.data;
    } catch (error: any) {
      console.error('Error en login:', error);
      throw new Error(error.response?.data?.message || 'Error al iniciar sesión');
    }
  },

  /**
   * Registrar usuario regular
   */
  registerUser: async (userData: any): Promise<ApiResponse<string>> => {
    try {
      const response = await api.post('/auth/register-user', userData);
      return response.data;
    } catch (error: any) {
      console.error('Error en register user:', error);
      throw new Error(error.response?.data?.message || 'Error al registrar usuario');
    }
  },

  /**
   * Registrar proveedor
   */
  registerProvider: async (providerData: any): Promise<ApiResponse<string>> => {
    try {
      const response = await api.post('/auth/register-provider', providerData);
      return response.data;
    } catch (error: any) {
      console.error('Error en register provider:', error);
      throw new Error(error.response?.data?.message || 'Error al registrar proveedor');
    }
  },

  /**
   * Solicitar restablecimiento de contraseña
   */
  forgotPassword: async (email: string): Promise<ApiResponse<string>> => {
    try {
      const response = await api.post('/auth/forgot-password', { email });
      return response.data;
    } catch (error: any) {
      console.error('Error en forgotPassword:', error);
      throw new Error(error.response?.data?.message || 'Error al enviar solicitud de restablecimiento');
    }
  },

  /**
   * Restablecer contraseña con token
   */
  resetPassword: async (token: string, newPassword: string): Promise<ApiResponse<string>> => {
    try {
      const response = await api.post('/auth/reset-password', { 
        token, 
        newPassword 
      });
      return response.data;
    } catch (error: any) {
      console.error('Error en resetPassword:', error);
      throw new Error(error.response?.data?.message || 'Error al restablecer contraseña');
    }
  },

  /**
   * Validar token de restablecimiento (opcional)
   */
  validateResetToken: async (token: string): Promise<ApiResponse<any>> => {
    try {
      const response = await api.get(`/auth/validate-reset-token/${token}`);
      return response.data;
    } catch (error: any) {
      console.error('Error en validateResetToken:', error);
      throw new Error(error.response?.data?.message || 'Token no válido');
    }
  },

  /**
   * Cerrar sesión
   */
  logout: async (): Promise<ApiResponse<string>> => {
    try {
      const response = await api.post('/auth/logout');
      return response.data;
    } catch (error: any) {
      console.error('Error en logout:', error);
      throw new Error(error.response?.data?.message || 'Error al cerrar sesión');
    }
  }
};

export const adminUserAPI = {
  /**
   * Obtiene lista paginada de usuarios con filtros
   */
  getAllUsers: async (filters: {
    role?: string;
    isActive?: string;
    search?: string;
    verificationStatus?: string;
    sortBy?: string;
    sortDirection?: string;
    page?: number;
    size?: number;
  }): Promise<ApiResponse<any>> => {
    try {
      const params = new URLSearchParams();
      
      if (filters.role) params.append('role', filters.role);
      if (filters.isActive !== undefined) params.append('isActive', filters.isActive);
      if (filters.search) params.append('search', filters.search);
      if (filters.verificationStatus) params.append('verificationStatus', filters.verificationStatus);
      if (filters.sortBy) params.append('sortBy', filters.sortBy);
      if (filters.sortDirection) params.append('sortDirection', filters.sortDirection);
      if (filters.page !== undefined) params.append('page', filters.page.toString());
      if (filters.size !== undefined) params.append('size', filters.size.toString());

      const response = await api.get(`/admin/users?${params.toString()}`);
      return response.data;
    } catch (error: any) {
      console.error('Error getting users:', error);
      throw new Error(error.response?.data?.message || 'Error al obtener usuarios');
    }
  },

  /**
   * Obtiene un usuario específico por ID
   */
  getUserById: async (userId: number): Promise<ApiResponse<any>> => {
    try {
      const response = await api.get(`/admin/users/${userId}`);
      return response.data;
    } catch (error: any) {
      console.error('Error getting user by ID:', error);
      throw new Error(error.response?.data?.message || 'Error al obtener usuario');
    }
  },

  /**
   * Actualiza un usuario
   */
  updateUser: async (userId: number, userData: {
    username: string;
    email: string;
    firstName?: string;
    lastName?: string;
    phone?: string;
    role: string;
    isActive: boolean;
    newPassword?: string;
    updateReason: string;
  }): Promise<ApiResponse<any>> => {
    try {
      const response = await api.put(`/admin/users/${userId}`, userData);
      return response.data;
    } catch (error: any) {
      console.error('Error updating user:', error);
      throw new Error(error.response?.data?.message || 'Error al actualizar usuario');
    }
  },

  /**
   * Elimina un usuario (soft delete)
   */
  deleteUser: async (userId: number, reason: string): Promise<ApiResponse<string>> => {
    try {
      const response = await api.delete(`/admin/users/${userId}?reason=${encodeURIComponent(reason)}`);
      return response.data;
    } catch (error: any) {
      console.error('Error deleting user:', error);
      throw new Error(error.response?.data?.message || 'Error al eliminar usuario');
    }
  },

  /**
   * Activa o desactiva un usuario
   */
  toggleUserStatus: async (userId: number, reason: string): Promise<ApiResponse<any>> => {
    try {
      const response = await api.patch(`/admin/users/${userId}/toggle-status?reason=${encodeURIComponent(reason)}`);
      return response.data;
    } catch (error: any) {
      console.error('Error toggling user status:', error);
      throw new Error(error.response?.data?.message || 'Error al cambiar estado del usuario');
    }
  },

  /**
   * Obtiene estadísticas de usuarios
   */
  getStatistics: async (): Promise<ApiResponse<any>> => {
    try {
      const response = await api.get('/admin/users/statistics');
      return response.data;
    } catch (error: any) {
      console.error('Error getting user statistics:', error);
      throw new Error(error.response?.data?.message || 'Error al obtener estadísticas');
    }
  },

  /**
   * Busca usuarios por texto libre
   */
  searchUsers: async (query: string): Promise<ApiResponse<any[]>> => {
    try {
      const response = await api.get(`/admin/users/search?q=${encodeURIComponent(query)}`);
      return response.data;
    } catch (error: any) {
      console.error('Error searching users:', error);
      throw new Error(error.response?.data?.message || 'Error en búsqueda de usuarios');
    }
  },

  /**
   * Obtiene usuarios por rol específico
   */
  getUsersByRole: async (role: string): Promise<ApiResponse<any[]>> => {
    try {
      const response = await api.get(`/admin/users/by-role/${role}`);
      return response.data;
    } catch (error: any) {
      console.error('Error getting users by role:', error);
      throw new Error(error.response?.data?.message || 'Error al obtener usuarios por rol');
    }
  },

  /**
   * Obtiene proveedores pendientes de verificación
   */
  getPendingProviders: async (): Promise<ApiResponse<any[]>> => {
    try {
      const response = await api.get('/admin/users/providers/pending');
      return response.data;
    } catch (error: any) {
      console.error('Error getting pending providers:', error);
      throw new Error(error.response?.data?.message || 'Error al obtener proveedores pendientes');
    }
  },

  /**
   * Obtiene usuarios recientes
   */
  getRecentUsers: async (): Promise<ApiResponse<any[]>> => {
    try {
      const response = await api.get('/admin/users/recent');
      return response.data;
    } catch (error: any) {
      console.error('Error getting recent users:', error);
      throw new Error(error.response?.data?.message || 'Error al obtener usuarios recientes');
    }
  },

  /**
   * Endpoint de prueba para verificar acceso administrativo
   */
  testAdminAccess: async (): Promise<ApiResponse<string>> => {
    try {
      const response = await api.get('/admin/users/test');
      return response.data;
    } catch (error: any) {
      console.error('Error testing admin access:', error);
      throw new Error(error.response?.data?.message || 'Error de acceso administrativo');
    }
  }
};

// Exportar la instancia de axios por defecto
export default api;

// API helpers for tracks and vinyls (HU-19)
export const tracksAPI = {
  searchTracks: async (query: string) => {
    try {
      const response = await api.get(`/api/tracks?q=${encodeURIComponent(query)}`);
      return response.data;
    } catch (error: any) {
      console.error('Error searching tracks:', error);
      throw new Error(error.response?.data?.message || 'Error searching tracks');
    }
  },

  getVinylsForTrack: async (trackId: number) => {
    try {
      const response = await api.get(`/api/tracks/${trackId}/vinyls`);
      return response.data;
    } catch (error: any) {
      console.error('Error getting vinyls for track:', error);
      throw new Error(error.response?.data?.message || 'Error getting vinyls');
    }
  }
}