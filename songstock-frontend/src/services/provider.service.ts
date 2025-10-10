import axiosInstance from './axios.instance';
import { API_ENDPOINTS } from '../config/api.config';
import { Product } from '../types/product.types';
import { ApiResponse } from '../types/api.types';

// Este servicio maneja todas las operaciones que un proveedor puede hacer con sus productos
class ProviderService {
  // Obtener todos los productos del proveedor actual
  async getMyProducts() {
    const response = await axiosInstance.get<ApiResponse<Product[]>>(
      `${API_ENDPOINTS.PROVIDERS}/my-products`
    );
    return response.data.data || [];
  }

  // Crear un nuevo producto
  async createProduct(productData: any) {
    const response = await axiosInstance.post<ApiResponse<Product>>(
      `${API_ENDPOINTS.PRODUCTS}`,
      productData
    );
    return response.data.data;
  }

  // Actualizar un producto existente
  async updateProduct(id: number, productData: any) {
    const response = await axiosInstance.put<ApiResponse<Product>>(
      `${API_ENDPOINTS.PRODUCTS}/${id}`,
      productData
    );
    return response.data.data;
  }

  // Eliminar un producto
  async deleteProduct(id: number) {
    const response = await axiosInstance.delete<ApiResponse<void>>(
      `${API_ENDPOINTS.PRODUCTS}/${id}`
    );
    return response.data;
  }

  // Obtener estadísticas del proveedor
  async getProviderStats() {
    const response = await axiosInstance.get<ApiResponse<any>>(
      `${API_ENDPOINTS.PROVIDER_STATS}`
    );
    return response.data.data;
  }

  // Obtener información del proveedor actual
  async getMyProviderInfo() {
    const response = await axiosInstance.get<ApiResponse<any>>(
      `${API_ENDPOINTS.PROVIDERS}/me`
    );
    return response.data.data;
  }
}

export default new ProviderService();