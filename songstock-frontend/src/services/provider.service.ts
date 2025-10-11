import axiosInstance from './axios.instance';
import { API_ENDPOINTS } from '../config/api.config';
import { Product } from '../types/product.types';
import { ApiResponse } from '../types/api.types';

class ProviderService {
  // Obtener todos los productos del proveedor actual usando el endpoint correcto
  async getMyProducts(): Promise<Product[]> {
    try {
      const response = await axiosInstance.get<ApiResponse<any>>(
        `${API_ENDPOINTS.PRODUCTS}/catalog/my-products`
      );
      
      console.log('Raw API response:', response.data); // Debug
      
      const data = response.data.data;
      
      // Caso 1: Si data tiene estructura de Page (content, totalElements, etc)
      if (data && typeof data === 'object' && 'content' in data) {
        const content = data.content;
        return Array.isArray(content) ? content : [];
      }
      
      // Caso 2: Si data ya es un array directamente
      if (Array.isArray(data)) {
        return data;
      }
      
      // Caso 3: Si no es ninguno de los anteriores, retornar array vacío
      console.warn('Unexpected data structure:', data);
      return [];
      
    } catch (error) {
      console.error('Error fetching products:', error);
      throw error; // Re-lanzar el error para que lo maneje el componente
    }
  }

  // Crear un nuevo producto
  async createProduct(productData: any): Promise<Product> {
    const response = await axiosInstance.post<ApiResponse<Product>>(
      `${API_ENDPOINTS.PRODUCTS}`,
      productData
    );
    return response.data.data;
  }

  // Actualizar un producto existente
  async updateProduct(id: number, productData: any): Promise<Product> {
    const response = await axiosInstance.put<ApiResponse<Product>>(
      `${API_ENDPOINTS.PRODUCTS}/${id}`,
      productData
    );
    return response.data.data;
  }

  // Eliminar un producto
  async deleteProduct(id: number): Promise<void> {
    await axiosInstance.delete<ApiResponse<void>>(
      `${API_ENDPOINTS.PRODUCTS}/${id}`
    );
  }

  // Obtener estadísticas del proveedor usando el endpoint de quick stats
  async getProviderStats(): Promise<any> {
    try {
      const response = await axiosInstance.get<ApiResponse<any>>(
        `${API_ENDPOINTS.PRODUCTS}/catalog/quick-stats`
      );
      return response.data.data;
    } catch (error) {
      console.error('Error getting provider stats:', error);
      // Retornar null para que el componente use el fallback
      return null;
    }
  }

  // Obtener información del proveedor actual
  async getMyProviderInfo(): Promise<any> {
    try {
      const response = await axiosInstance.get<ApiResponse<any>>(
        `${API_ENDPOINTS.PROVIDERS}/me`
      );
      return response.data.data;
    } catch (error) {
      console.error('Error getting provider info:', error);
      return null;
    }
  }
}

export default new ProviderService();