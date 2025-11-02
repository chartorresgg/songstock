import axiosInstance from './axios.instance';
import { Order, OrderReview, CreateReviewRequest } from '../types/order.types';
import { ApiResponse } from '../types/api.types';

class OrderService {
  /**
   * Obtener todas las órdenes del usuario actual (CUSTOMER)
   */
  async getMyOrders(): Promise<Order[]> {
    try {
      const response = await axiosInstance.get<ApiResponse<Order[]>>(
        '/orders/my-orders'
      );
      return response.data.data;
    } catch (error) {
      console.error('Error fetching orders:', error);
      throw error;
    }
  }

  /**
   * Obtener órdenes pendientes del proveedor (PROVIDER)
   */
  async getPendingOrders(): Promise<Order[]> {
    try {
      const response = await axiosInstance.get<ApiResponse<Order[]>>(
        '/orders/provider/pending'
      );
      return response.data.data;
    } catch (error) {
      console.error('Error fetching pending orders:', error);
      throw error;
    }
  }

  /**
   * Obtener una orden específica por su ID
   */
  async getOrderById(orderId: number): Promise<Order | null> {
    try {
      const response = await axiosInstance.get<ApiResponse<Order>>(
        `/orders/${orderId}`
      );
      return response.data.data;
    } catch (error) {
      console.error('Error fetching order:', error);
      return null;
    }
  }

  /**
   * Aceptar un item de orden (PROVIDER)
   */
  async acceptOrderItem(itemId: number): Promise<void> {
    try {
      await axiosInstance.put(`/orders/items/${itemId}/accept`);
    } catch (error) {
      console.error('Error accepting order item:', error);
      throw error;
    }
  }

  /**
   * Rechazar un item de orden (PROVIDER)
   */
  async rejectOrderItem(itemId: number, reason: string): Promise<void> {
    try {
      await axiosInstance.put(
        `/orders/items/${itemId}/reject`,
        null,
        { params: { reason } }
      );
    } catch (error) {
      console.error('Error rejecting order item:', error);
      throw error;
    }
  }

  /**
   * Marcar item como enviado (PROVIDER)
   */
  async shipOrderItem(itemId: number, shippedDate?: string): Promise<void> {
    try {
      await axiosInstance.put(`/orders/items/${itemId}/ship${shippedDate ? `?shippedDate=${shippedDate}` : ''}`);
    } catch (error) {
      console.error('Error shipping order item:', error);
      throw error;
    }
  }

  /**
   * Marcar item como entregado (PROVIDER)
   */
  async deliverOrderItem(itemId: number): Promise<void> {
    try {
      await axiosInstance.put(`/orders/items/${itemId}/deliver`);
    } catch (error) {
      console.error('Error delivering order item:', error);
      throw error;
    }
  }

  async getProviderOrders(): Promise<Order[]> {
    try {
      const response = await axiosInstance.get<ApiResponse<Order[]>>(
        '/orders/provider'
      );
      return response.data.data;
    } catch (error) {
      console.error('Error fetching provider orders:', error);
      throw error;
    }
  }

  /**
   * Crear nueva orden (CUSTOMER)
   */
  async createOrder(orderData: {
    items: { productId: number; quantity: number }[];
    paymentMethod: string;
    shippingAddress?: string;
    shippingCity?: string;
    shippingState?: string;
    shippingPostalCode?: string;
    shippingCountry?: string;
  }): Promise<Order> {
    try {
      const response = await axiosInstance.post<ApiResponse<Order>>(
        '/orders',
        orderData
      );
      return response.data.data;
    } catch (error) {
      console.error('Error creating order:', error);
      throw error;
    }
  }

  /**
   * Crear valoración de orden (CUSTOMER)
   */
  async createReview(orderId: number, reviewData: CreateReviewRequest): Promise<OrderReview> {
    try {
      const response = await axiosInstance.post<ApiResponse<OrderReview>>(
        `/orders/${orderId}/review`,
        reviewData
      );
      return response.data.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Error al crear valoración');
    }
  }

  /**
   * Obtener valoración de una orden
   */
  async getReview(orderId: number): Promise<OrderReview | null> {
    try {
      const response = await axiosInstance.get<ApiResponse<OrderReview>>(
        `/orders/${orderId}/review`
      );
      return response.data.data;
    } catch (error) {
      return null;
    }
  }
}

export default new OrderService();