import { Product } from './product.types';

/**
 * Esta enumeraciÃ³n define todos los posibles estados por los que puede pasar una orden
 * durante su ciclo de vida. Es importante mantener estos estados sincronizados con el backend.
 * 
 * El flujo tÃ­pico serÃ­a: PENDING â†’ CONFIRMED â†’ PROCESSING â†’ SHIPPED â†’ DELIVERED
 * En cualquier punto antes de SHIPPED, la orden puede ser CANCELLED.
 */
export enum OrderStatus {
  PENDING = 'PENDING',           // Orden creada, esperando confirmaciÃ³n de pago
  CONFIRMED = 'CONFIRMED',       // Pago confirmado exitosamente
  PROCESSING = 'PROCESSING',     // El proveedor estÃ¡ preparando el pedido
  SHIPPED = 'SHIPPED',           // El pedido ha sido enviado al cliente
  DELIVERED = 'DELIVERED',       // El cliente ha recibido el pedido
  CANCELLED = 'CANCELLED',       // La orden fue cancelada (por el cliente o por falta de stock)
  ACCEPTED = 'ACCEPTED',         // Aceptado por proveedor
  REJECTED = 'REJECTED',
  RECEIVED = 'RECEIVED'       // Rechazado por proveedor
}

export enum OrderItemStatus {
  PENDING = 'PENDING',
  ACCEPTED = 'ACCEPTED',
  REJECTED = 'REJECTED',
  PROCESSING = 'PROCESSING',
  SHIPPED = 'SHIPPED',
  DELIVERED = 'DELIVERED',
  RECEIVED = 'RECEIVED'
}
  

/**
 * Esta interfaz representa un producto individual dentro de una orden.
 * Es importante notar que guardamos el precio al momento de la compra,
 * porque el precio del producto puede cambiar con el tiempo, pero el precio
 * que el cliente pagÃ³ debe mantenerse constante en el registro de la orden.
 */
export interface OrderItem {
  shippedAt?: string | number[];    // Fecha de envío (string ISO o array de Spring Boot)
  id: number;                    // ID Ãºnico del item en la orden
  product: Product;              // InformaciÃ³n completa del producto
  quantity: number;              // Cantidad comprada de este producto
  price: number;                 // Precio unitario al momento de la compra (puede diferir del precio actual)
  subtotal: number;              // Precio total de este item (price Ã— quantity)
  providerId: number;            // ID del proveedor
  providerName: string;          // Nombre del proveedor
  status: OrderItemStatus;       // Estado del item
  rejectionReason?: string;      // RazÃ³n de rechazo (opcional)
}

/**
 * Esta interfaz representa la direcciÃ³n de envÃ­o para productos fÃ­sicos.
 * Solo se requiere cuando la orden incluye productos de tipo PHYSICAL.
 * Para productos digitales (tipo DIGITAL), esta informaciÃ³n es opcional.
 */
export interface ShippingAddress {
  address: string;               // DirecciÃ³n completa (calle, nÃºmero, etc.)
  city: string;                  // Ciudad de destino
  state: string;                 // Departamento o estado
  postalCode: string;            // CÃ³digo postal
  country: string;               // PaÃ­s de destino
}

/**
 * Esta es la interfaz principal que representa una orden completa en el sistema.
 * Una orden agrupa todos los productos que un cliente comprÃ³ en una sola transacciÃ³n,
 * junto con la informaciÃ³n de envÃ­o, pago y el estado actual de la orden.
 */
export interface Order {
  id: number;                    // ID Ãºnico de la orden en la base de datos
  orderNumber: string;           // NÃºmero de orden legible para el usuario (ej: ORD-2025-001)
  userId: number;                // ID del usuario que realizÃ³ la compra
  items: OrderItem[];            // Array de productos incluidos en la orden
  status: OrderStatus;           // Estado actual de la orden
  total: number;                 // Monto total de la orden (suma de todos los subtotales)
  shippingAddress?: ShippingAddress;  // DirecciÃ³n de envÃ­o (opcional para productos digitales)
  paymentMethod: string;         // MÃ©todo de pago utilizado (credit_card, debit_card, pse, etc.)
  createdAt: string;             // Fecha y hora de creaciÃ³n de la orden (formato ISO string)
  updatedAt: string;   
  shippedAt?: string | number[];    // Fecha de envío
  deliveredAt?: string | number[];  // Fecha de entrega          // Fecha y hora de la Ãºltima actualizaciÃ³n
  review?: OrderReview | null;          // ValoraciÃ³n de la orden
}

/**
 * Interfaz para la valoraciÃ³n de una orden
 */
export interface OrderReview {
    id: number;
    orderId: number;
    userId: number;
    userName: string;
    rating: number;
    comment?: string;
    createdAt: string;
    status: 'PENDING' | 'APPROVED' | 'REJECTED';
    moderatedAt?: string;
    moderatedByUsername?: string;
  }
  
  /**
   * DTO para crear una valoraciÃ³n
   */
  export interface CreateReviewRequest {
    rating: number;
    comment?: string;
  }
  

/**
 * Esta interfaz es Ãºtil para crear una nueva orden desde el frontend.
 * No incluye campos que son generados automÃ¡ticamente por el backend,
 * como el ID, orderNumber, o las fechas de creaciÃ³n.
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
 * Esta funciÃ³n auxiliar convierte el enum OrderStatus a texto legible en espaÃ±ol.
 * Es Ãºtil para mostrar el estado de la orden en la interfaz de usuario.
 */
export const getOrderStatusLabel = (status: OrderStatus): string => {
  const labels: Record<OrderStatus, string> = {
    [OrderStatus.PENDING]: 'Pendiente',
    [OrderStatus.CONFIRMED]: 'Confirmada',
    [OrderStatus.PROCESSING]: 'En PreparaciÃ³n',
    [OrderStatus.SHIPPED]: 'Enviada',
    [OrderStatus.DELIVERED]: 'Entregada',
    [OrderStatus.CANCELLED]: 'Cancelada',
    [OrderStatus.ACCEPTED]: 'Aceptada',
    [OrderStatus.REJECTED]: 'Rechazada',
    [OrderStatus.RECEIVED]: "Recibida"
  };
  return labels[status];
};

/**
 * Esta funciÃ³n determina el color que debe usarse para mostrar el estado
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
    [OrderStatus.ACCEPTED]: 'bg-green-100 text-green-800',
    [OrderStatus.REJECTED]: 'bg-red-100 text-red-800',
    [OrderStatus.RECEIVED]: 'bg-green-100 text-green-800'
    
  };
  return colors[status];
};