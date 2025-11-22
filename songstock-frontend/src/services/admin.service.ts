import axiosInstance from './axios.instance';
import { ApiResponse } from '../types/api.types';
import { OrderReview } from '../types/order.types';

// ==================== INTERFACES ====================

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

interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  role: 'ADMIN' | 'PROVIDER' | 'CUSTOMER';
  isActive: boolean;
  createdAt: any;
  updatedAt?: any;
}

interface SystemStats {
  totalUsers: number;
  totalProviders: number;
  totalProducts: number;
  totalOrders: number;
  totalRevenue: number;
  activeProducts: number;
  pendingProviders: number;
}

// DTOs para crear/editar usuarios
interface UserCreateDTO {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string;
  role: 'ADMIN' | 'PROVIDER' | 'CUSTOMER';
}

interface UserEditDTO {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  phone?: string;
  role: 'ADMIN' | 'PROVIDER' | 'CUSTOMER';
  isActive: boolean;
  updateReason?: string;
}

// ==================== SERVICIO ====================

class AdminService {
  
  // ============ USUARIOS ============
  
  /**
   * Obtener todos los usuarios con filtros opcionales
   */
  async getAllUsers(params?: {
    page?: number;
    size?: number;
    searchQuery?: string;
    role?: string;
    isActive?: boolean;
  }): Promise<User[]> {
    try {
      const response = await axiosInstance.get<ApiResponse<any>>(
        '/admin/users',
        { params: params || { page: 0, size: 1000 } }
      );
      
      const users: any = response.data.data || response.data || [];
      
      if (Array.isArray(users)) {
        return users as User[];
      }
      
      if (users && users.content && Array.isArray(users.content)) {
        return users.content as User[];
      }
      
      return [];
    } catch (error) {
      console.error('Error fetching users:', error);
      return [];
    }
  }

  /**
   * Obtener un usuario por ID
   */
  async getUserById(userId: number): Promise<User | null> {
    try {
      const response = await axiosInstance.get<ApiResponse<User>>(
        `/admin/users/${userId}`
      );
      return response.data.data || null;
    } catch (error) {
      console.error('Error fetching user:', error);
      throw error;
    }
  }

  

  async deleteProduct(productId: number): Promise<void> {
    await axiosInstance.delete<ApiResponse<void>>(`/products/${productId}`);
  }

  /**
   * Crear un nuevo usuario
   * Endpoint: POST /api/v1/users (UserController)
   */
  async createUser(userData: UserCreateDTO) {
    try {
      const response = await axiosInstance.post<ApiResponse<User>>(
        '/users',
        userData
      );
      return response.data;
    } catch (error) {
      console.error('Error creating user:', error);
      throw error;
    }
  }

  /**
   * Actualizar un usuario existente
   * Endpoint: PUT /api/v1/admin/users/{userId}
   */
  async updateUser(userId: number, userData: UserEditDTO) {
    try {
      const response = await axiosInstance.put<ApiResponse<User>>(
        `/admin/users/${userId}`,
        userData
      );
      return response.data;
    } catch (error) {
      console.error('Error updating user:', error);
      throw error;
    }
  }

  /**
   * Activar/Desactivar usuario
   * Endpoint: PATCH /api/v1/admin/users/{userId}/toggle-status
   */
  async toggleUserStatus(userId: number, reason?: string) {
    try {
      const response = await axiosInstance.patch<ApiResponse<User>>(
        `/admin/users/${userId}/toggle-status`,
        null,
        {
          params: {
            reason: reason || 'Cambio de estado desde dashboard administrativo'
          }
        }
      );
      return response.data;
    } catch (error) {
      console.error('Error toggling user status:', error);
      throw error;
    }
  }

  /**
   * Eliminar un usuario (soft delete)
   * Endpoint: DELETE /api/v1/admin/users/{userId}
   */
  async deleteUser(userId: number, reason?: string) {
    try {
      const response = await axiosInstance.delete<ApiResponse<void>>(
        `/admin/users/${userId}`,
        {
          params: {
            reason: reason || 'Eliminación desde dashboard administrativo'
          }
        }
      );
      return response.data;
    } catch (error) {
      console.error('Error deleting user:', error);
      throw error;
    }
  }

  /**
   * Obtener usuarios por rol específico
   */
  async getUsersByRole(role: 'ADMIN' | 'PROVIDER' | 'CUSTOMER'): Promise<User[]> {
    try {
      const response = await axiosInstance.get<ApiResponse<any>>(
        `/admin/users/by-role/${role}`
      );
      
      const users: any = response.data.data || response.data || [];
      
      if (Array.isArray(users)) {
        return users as User[];
      }
      
      if (users && users.content && Array.isArray(users.content)) {
        return users.content as User[];
      }
      
      return [];
    } catch (error) {
      console.error('Error fetching users by role:', error);
      return [];
    }
  }

  // ============ PROVEEDORES ============
  
  /**
   * Obtener todos los proveedores
   */
  async getAllProviders(): Promise<any[]> {
    try {
      const response = await axiosInstance.get<ApiResponse<any>>('/providers');
      
      const providers = response.data.data || [];
      
      return Array.isArray(providers) 
        ? providers.filter(p => p.verificationStatus === 'VERIFIED')
        : [];
    } catch (error) {
      console.error('Error fetching providers:', error);
      return [];
    }
  }

  /**
   * Actualizar estado de verificación de un proveedor
   */
  async updateProviderStatus(providerId: number, status: string) {
    try {
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

  /**
   * Obtener proveedores pendientes de verificación
   */
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

  // ============ PRODUCTOS ============
  
  /**
   * Obtener todos los productos del sistema
   */
  async getAllProducts(): Promise<any[]> {
    try {
      const response = await axiosInstance.get<ApiResponse<any>>(
        '/products',
        {
          params: {
            page: 0,
            size: 1000
          }
        }
      );
      
      const products: any = response.data.data || response.data || [];
      
      if (Array.isArray(products)) {
        return products;
      }
      
      if (products && products.content && Array.isArray(products.content)) {
        return products.content;
      }
      
      return [];
    } catch (error) {
      console.error('Error fetching products:', error);
      return [];
    }
  }

  /**
   * Crear un nuevo producto en el catálogo (ADMIN)
   */
  async createProduct(productData: any): Promise<any> {
    try {
      const response = await axiosInstance.post<ApiResponse<any>>(
        '/products/catalog',
        productData
      );
      return response.data.data;
    } catch (error) {
      console.error('Error creating product:', error);
      throw error;
    }
  }

  /**
   * Actualizar un producto existente (ADMIN)
   */
  async updateProduct(productId: number, productData: any): Promise<any> {
    try {
      const response = await axiosInstance.put<ApiResponse<any>>(
        `/products/${productId}/catalog`,
        productData
      );
      return response.data.data;
    } catch (error) {
      console.error('Error updating product:', error);
      throw error;
    }
  }

  /**
   * Obtener un producto por ID
   */
  async getProductById(productId: number): Promise<any> {
    try {
      const response = await axiosInstance.get<ApiResponse<any>>(
        `/products/${productId}`
      );
      return response.data.data;
    } catch (error) {
      console.error('Error fetching product:', error);
      throw error;
    }
  }

  /**
   * Obtener todas las categorías
   */
  async getAllCategories(): Promise<any[]> {
    try {
      const response = await axiosInstance.get<ApiResponse<any>>('/categories');
      return response.data.data;
    } catch (error) {
      console.error('Error fetching categories:', error);
      throw error;
    }
  }

  /**
   * Obtener todos los álbumes
   */
  async getAllAlbums(): Promise<any[]> {
    try {
      const response = await axiosInstance.get<ApiResponse<any>>('/albums');
      return response.data.data;
    } catch (error) {
      console.error('Error fetching albums:', error);
      throw error;
    }
  }

  // ============ ESTADÍSTICAS ============
  
  /**
   * Calcular estadísticas del sistema
   */
  async getSystemStats(): Promise<SystemStats> {
    try {
      const [users, providers, products] = await Promise.all([
        this.getAllUsers(),
        this.getAllProviders(),
        this.getAllProducts()
      ]);
  
      const activeProducts = products.filter((p: any) => p.isActive).length;
      const pendingProviders = providers.filter((p: any) => 
        p.verificationStatus === 'PENDING' || p.providerVerificationStatus === 'PENDING'
      ).length;
      const totalRevenue = products.reduce((sum: number, p: any) => 
        sum + ((p.price || 0) * (p.stockQuantity || 0)), 0
      );
  
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

  /**
   * Obtener métricas rápidas del dashboard
   */
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

  /**
   * Obtener estadísticas completas del dashboard
   */
  async getDashboardStatistics() {
    try {
      const response = await axiosInstance.get<ApiResponse<any>>(
        '/admin/users/dashboard/statistics'
      );
      return response.data.data;
    } catch (error) {
      console.error('Error fetching dashboard statistics:', error);
      // Si falla, calcular manualmente
      return this.getSystemStats();
    }
  }

  // ============ MODERACIÓN DE VALORACIONES ============
  
  /**
   * Obtener valoraciones pendientes de moderación
   */
  async getPendingReviews(): Promise<OrderReview[]> {
    try {
      const response = await axiosInstance.get<ApiResponse<OrderReview[]>>(
        '/admin/users/reviews/pending'
      );
      return response.data.data || [];
    } catch (error) {
      console.error('Error fetching pending reviews:', error);
      return [];
    }
  }

  /**
   * Aprobar una valoración
   */
  async approveReview(reviewId: number): Promise<OrderReview> {
    try {
      const response = await axiosInstance.put<ApiResponse<OrderReview>>(
        `/admin/users/reviews/${reviewId}/approve`
      );
      return response.data.data;
    } catch (error) {
      console.error('Error approving review:', error);
      throw error;
    }
  }

  /**
   * Rechazar una valoración
   */
  async rejectReview(reviewId: number): Promise<OrderReview> {
    try {
      const response = await axiosInstance.put<ApiResponse<OrderReview>>(
        `/admin/users/reviews/${reviewId}/reject`
      );
      return response.data.data;
    } catch (error) {
      console.error('Error rejecting review:', error);
      throw error;
    }
  }

}



export default new AdminService();