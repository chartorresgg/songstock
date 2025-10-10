import axiosInstance from './axios.instance';
import { API_ENDPOINTS } from '../config/api.config';
import { Order, OrderStatus } from '../types/order.types';
import { ApiResponse } from '../types/api.types';

class OrderService {
  // Este método obtiene todas las órdenes del usuario actual
  async getMyOrders(): Promise<Order[]> {
    try {
      // Intentar obtener del backend (cuando esté implementado)
      const response = await axiosInstance.get<ApiResponse<Order[]>>(
        '/orders/my-orders'
      );
      return response.data.data;
    } catch (error) {
      // Si falla (porque no está implementado), retornar datos de ejemplo
      // Esto permite que la interfaz funcione mientras se desarrolla el backend
      console.log('Orders endpoint not available, using mock data');
      return this.getMockOrders();
    }
  }

  // Este método obtiene una orden específica por su ID
  async getOrderById(orderId: number): Promise<Order | null> {
    try {
      const response = await axiosInstance.get<ApiResponse<Order>>(
        `/orders/${orderId}`
      );
      return response.data.data;
    } catch (error) {
      console.log('Order detail not available');
      return null;
    }
  }

  // Este método genera datos de ejemplo para demostración
  // En producción, estos datos vendrían del backend
  private getMockOrders(): Order[] {
    // Creamos un array con órdenes de ejemplo que simulan diferentes estados
    return [
      {
        id: 1,
        orderNumber: 'ORD-2025-001',
        userId: 1,
        items: [
          {
            id: 1,
            product: {
              id: 1,
              albumTitle: 'The Dark Side of the Moon',
              artistName: 'Pink Floyd',
              price: 89990,
              productType: 'PHYSICAL',
              stockQuantity: 5,
              images: null,
              // Otros campos del producto...
            } as any,
            quantity: 1,
            price: 89990,
            subtotal: 89990,
          },
        ],
        status: OrderStatus.DELIVERED,
        total: 89990,
        shippingAddress: {
          address: 'Calle 123 #45-67',
          city: 'Bogotá',
          state: 'Cundinamarca',
          postalCode: '110111',
          country: 'Colombia',
        },
        paymentMethod: 'credit_card',
        createdAt: '2025-01-15T10:00:00Z',
        updatedAt: '2025-01-18T14:30:00Z',
      },
      {
        id: 2,
        orderNumber: 'ORD-2025-002',
        userId: 1,
        items: [
          {
            id: 2,
            product: {
              id: 5,
              albumTitle: 'Back in Black',
              artistName: 'AC/DC',
              price: 12990,
              productType: 'DIGITAL',
              stockQuantity: 9999,
              images: null,
            } as any,
            quantity: 1,
            price: 12990,
            subtotal: 12990,
          },
        ],
        status: OrderStatus.PROCESSING,
        total: 12990,
        paymentMethod: 'debit_card',
        createdAt: '2025-01-20T15:30:00Z',
        updatedAt: '2025-01-20T15:30:00Z',
      },
    ];
  }
}

export default new OrderService();