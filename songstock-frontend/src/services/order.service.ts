import axiosInstance from './axios.instance';
import { Order, OrderStatus, OrderItemStatus } from '../types/order.types';
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
}

export default new OrderService();