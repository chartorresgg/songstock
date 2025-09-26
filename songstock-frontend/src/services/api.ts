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

// Exportar la instancia de axios por defecto
export default api;