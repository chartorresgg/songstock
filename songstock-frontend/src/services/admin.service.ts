import axiosInstance from './axios.instance';
import { API_ENDPOINTS } from '../config/api.config';
import { ApiResponse } from '../types/api.types';

// Interfaz para representar un proveedor en el sistema
interface Provider {
  id: number;
  businessName: string;
  taxId: string;
  email?: string;
  phone?: string;
  city?: string;
  country?: string;
  verificationStatus: string;
  isActive?: boolean;
  createdAt: any;
  userId?: number;
}

// Interfaz para un usuario del sistema
interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  isActive: boolean;
  createdAt: any;
  phone?: string;
}

// Interfaz para las estadísticas generales del sistema
interface SystemStats {
  totalUsers: number;
  totalProviders: number;
  totalProducts: number;
  totalOrders: number;
  totalRevenue: number;
  activeProducts: number;
  pendingProviders: number;
}

// Este servicio maneja todas las operaciones administrativas del sistema
class AdminService {
  // Obtener todos los proveedores del sistema
  // IMPORTANTE: Usamos el endpoint de admin/users con filtro de rol PROVIDER
  async getAllProviders() {
    try {
      const response = await axiosInstance.get<ApiResponse<any>>(
        '/admin/users/by-role/PROVIDER'
      );
      
      // El backend puede devolver los datos en diferentes formatos
      // Intentamos ambos para mayor compatibilidad
      const providers = response.data.data || response.data || [];
      
      // Si es un array directo, lo usamos
      if (Array.isArray(providers)) {
        return providers;
      }
      
      // Si es un objeto paginado, extraemos el contenido
      if (providers.content && Array.isArray(providers.content)) {
        return providers.content;
      }
      
      return [];
    } catch (error) {
      console.error('Error fetching providers:', error);
      return [];
    }
  }

  // Obtener todos los usuarios del sistema
  async getAllUsers() {
    try {
      const response = await axiosInstance.get<ApiResponse<any>>(
        '/admin/users',
        {
          params: {
            page: 0,
            size: 1000 // Obtener muchos usuarios para las estadísticas
          }
        }
      );
      
      const users = response.data.data || response.data || [];
      
      // Si es un array directo
      if (Array.isArray(users)) {
        return users;
      }
      
      // Si es paginado
      if (users.content && Array.isArray(users.content)) {
        return users.content;
      }
      
      return [];
    } catch (error) {
      console.error('Error fetching users:', error);
      return [];
    }
  }

  // Obtener todos los productos del sistema (de todos los proveedores)
  async getAllProducts() {
    try {
      const response = await axiosInstance.get<ApiResponse<any[]>>(
        API_ENDPOINTS.PRODUCTS
      );
      return response.data.data || [];
    } catch (error) {
      console.error('Error fetching products:', error);
      return [];
    }
  }

  // Obtener todas las categorías
  async getAllCategories() {
    try {
      const response = await axiosInstance.get<ApiResponse<any[]>>(
        API_ENDPOINTS.CATEGORIES
      );
      return response.data.data || [];
    } catch (error) {
      console.error('Error fetching categories:', error);
      return [];
    }
  }

  // Obtener todos los géneros musicales
  async getAllGenres() {
    try {
      const response = await axiosInstance.get<ApiResponse<any[]>>(
        API_ENDPOINTS.GENRES
      );
      return response.data.data || [];
    } catch (error) {
      console.error('Error fetching genres:', error);
      return [];
    }
  }

  // Calcular estadísticas del sistema basadas en los datos obtenidos
  async getSystemStats(): Promise<SystemStats> {
    try {
      const [users, providers, products] = await Promise.all([
        this.getAllUsers(),
        this.getAllProviders(),
        this.getAllProducts()
      ]);
  
      // Calcular estadísticas con tipado explícito
      const activeProducts = products.filter((p: any) => p.isActive).length;
      const pendingProviders = providers.filter((p: any) => p.verificationStatus === 'PENDING').length;
      const totalRevenue = products.reduce((sum: number, p: any) => sum + (p.price * p.stockQuantity), 0);
  
      return {
        totalUsers: users.length,
        totalProviders: providers.length,
        totalProducts: products.length,
        totalOrders: 0,
        totalRevenue: totalRevenue,
        activeProducts: activeProducts,
        pendingProviders: pendingProviders
      };
    } catch (error) {
      console.error('Error calculating stats:', error);
      return {
        totalUsers: 0,
        totalProviders: 0,
        totalProducts: 0,
        totalOrders: 0,
        totalRevenue: 0,
        activeProducts: 0,
        pendingProviders: 0
      };
    }
  }

  // Actualizar el estado de verificación de un proveedor (aprobar/rechazar)
  async updateProviderStatus(providerId: number, status: string) {
    try {
      // Usamos el endpoint correcto del AdminUserController
      const response = await axiosInstance.put<ApiResponse<any>>(
        `/admin/users/${providerId}/provider/verification`,
        { 
          verificationStatus: status.toUpperCase()
        }
      );
      return response.data;
    } catch (error) {
      console.error('Error updating provider status:', error);
      throw error;
    }
  }

  // Activar o desactivar un usuario
  async toggleUserStatus(userId: number, isActive: boolean) {
    try {
      const response = await axiosInstance.patch<ApiResponse<any>>(
        `/admin/users/${userId}/toggle-status`,
        null, // No necesitamos body, el backend usa el toggle
        {
          params: {
            reason: 'Cambio de estado desde dashboard administrativo'
          }
        }
      );
      return response.data;
    } catch (error) {
      console.error('Error toggling user status:', error);
      throw error;
    }
  }

  // Método adicional: Obtener métricas rápidas del dashboard
  async getDashboardMetrics() {
    try {
      const response = await axiosInstance.get<ApiResponse<any>>(
        '/admin/users/metrics/quick'
      );
      return response.data.data;
    } catch (error) {
      console.error('Error fetching dashboard metrics:', error);
      return null;
    }
  }

  // Método adicional: Obtener proveedores pendientes
  async getPendingProviders() {
    try {
      const response = await axiosInstance.get<ApiResponse<any[]>>(
        '/admin/users/providers/pending'
      );
      return response.data.data || [];
    } catch (error) {
      console.error('Error fetching pending providers:', error);
      return [];
    }
  }
}

export default new AdminService();