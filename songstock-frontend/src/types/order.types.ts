import { Product } from './product.types';

/**
 * Esta enumeración define todos los posibles estados por los que puede pasar una orden
 * durante su ciclo de vida. Es importante mantener estos estados sincronizados con el backend.
 * 
 * El flujo típico sería: PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED
 * En cualquier punto antes de SHIPPED, la orden puede ser CANCELLED.
 */
export enum OrderStatus {
  PENDING = 'PENDING',           // Orden creada, esperando confirmación de pago
  CONFIRMED = 'CONFIRMED',       // Pago confirmado exitosamente
  PROCESSING = 'PROCESSING',     // El proveedor está preparando el pedido
  SHIPPED = 'SHIPPED',           // El pedido ha sido enviado al cliente
  DELIVERED = 'DELIVERED',       // El cliente ha recibido el pedido
  CANCELLED = 'CANCELLED',       // La orden fue cancelada (por el cliente o por falta de stock)
}

/**
 * Esta interfaz representa un producto individual dentro de una orden.
 * Es importante notar que guardamos el precio al momento de la compra,
 * porque el precio del producto puede cambiar con el tiempo, pero el precio
 * que el cliente pagó debe mantenerse constante en el registro de la orden.
 */
export interface OrderItem {
  id: number;                    // ID único del item en la orden
  product: Product;              // Información completa del producto
  quantity: number;              // Cantidad comprada de este producto
  price: number;                 // Precio unitario al momento de la compra (puede diferir del precio actual)
  subtotal: number;              // Precio total de este item (price × quantity)
}

/**
 * Esta interfaz representa la dirección de envío para productos físicos.
 * Solo se requiere cuando la orden incluye productos de tipo PHYSICAL.
 * Para productos digitales (tipo DIGITAL), esta información es opcional.
 */
export interface ShippingAddress {
  address: string;               // Dirección completa (calle, número, etc.)
  city: string;                  // Ciudad de destino
  state: string;                 // Departamento o estado
  postalCode: string;            // Código postal
  country: string;               // País de destino
}

/**
 * Esta es la interfaz principal que representa una orden completa en el sistema.
 * Una orden agrupa todos los productos que un cliente compró en una sola transacción,
 * junto con la información de envío, pago y el estado actual de la orden.
 */
export interface Order {
  id: number;                    // ID único de la orden en la base de datos
  orderNumber: string;           // Número de orden legible para el usuario (ej: ORD-2025-001)
  userId: number;                // ID del usuario que realizó la compra
  items: OrderItem[];            // Array de productos incluidos en la orden
  status: OrderStatus;           // Estado actual de la orden
  total: number;                 // Monto total de la orden (suma de todos los subtotales)
  shippingAddress?: ShippingAddress;  // Dirección de envío (opcional para productos digitales)
  paymentMethod: string;         // Método de pago utilizado (credit_card, debit_card, pse, etc.)
  createdAt: string;             // Fecha y hora de creación de la orden (formato ISO string)
  updatedAt: string;             // Fecha y hora de la última actualización
}

/**
 * Esta interfaz es útil para crear una nueva orden desde el frontend.
 * No incluye campos que son generados automáticamente por el backend,
 * como el ID, orderNumber, o las fechas de creación.
 */
export interface CreateOrderRequest {
  items: {
    productId: number;
    quantity: number;
  }[];
  shippingAddress?: ShippingAddress;
  paymentMethod: string;
}

/**
 * Esta función auxiliar convierte el enum OrderStatus a texto legible en español.
 * Es útil para mostrar el estado de la orden en la interfaz de usuario.
 */
export const getOrderStatusLabel = (status: OrderStatus): string => {
  const labels: Record<OrderStatus, string> = {
    [OrderStatus.PENDING]: 'Pendiente',
    [OrderStatus.CONFIRMED]: 'Confirmada',
    [OrderStatus.PROCESSING]: 'En Preparación',
    [OrderStatus.SHIPPED]: 'Enviada',
    [OrderStatus.DELIVERED]: 'Entregada',
    [OrderStatus.CANCELLED]: 'Cancelada',
  };
  return labels[status];
};

/**
 * Esta función determina el color que debe usarse para mostrar el estado
 * en la interfaz. Cada estado tiene un color asociado que indica visualmente
 * su importancia o progreso.
 */
export const getOrderStatusColor = (status: OrderStatus): string => {
  const colors: Record<OrderStatus, string> = {
    [OrderStatus.PENDING]: 'bg-yellow-100 text-yellow-800',
    [OrderStatus.CONFIRMED]: 'bg-blue-100 text-blue-800',
    [OrderStatus.PROCESSING]: 'bg-purple-100 text-purple-800',
    [OrderStatus.SHIPPED]: 'bg-indigo-100 text-indigo-800',
    [OrderStatus.DELIVERED]: 'bg-green-100 text-green-800',
    [OrderStatus.CANCELLED]: 'bg-red-100 text-red-800',
  };
  return colors[status];
};