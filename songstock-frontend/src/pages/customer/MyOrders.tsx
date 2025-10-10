import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import orderService from '../../services/order.service';
import { Order, OrderStatus } from '../../types/order.types';
import { Package, Calendar, CreditCard, MapPin, Eye, ShoppingBag } from 'lucide-react';

const MyOrders = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadOrders();
  }, []);

  const loadOrders = async () => {
    setLoading(true);
    try {
      const data = await orderService.getMyOrders();
      setOrders(data);
    } catch (error) {
      console.error('Error loading orders:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
    }).format(price);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('es-CO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getStatusLabel = (status: OrderStatus) => {
    const labels: Record<OrderStatus, string> = {
      [OrderStatus.PENDING]: 'Pendiente',
      [OrderStatus.CONFIRMED]: 'Confirmado',
      [OrderStatus.PROCESSING]: 'En Proceso',
      [OrderStatus.SHIPPED]: 'Enviado',
      [OrderStatus.DELIVERED]: 'Entregado',
      [OrderStatus.CANCELLED]: 'Cancelado',
    };
    return labels[status];
  };

  const getStatusColor = (status: OrderStatus) => {
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

  const getPaymentMethodLabel = (method: string) => {
    const methods: Record<string, string> = {
      'credit_card': 'Tarjeta de Crédito',
      'debit_card': 'Tarjeta de Débito',
      'pse': 'PSE',
    };
    return methods[method] || method;
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-900"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-gradient-to-r from-primary-900 to-primary-800 text-white py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center space-x-4">
            <div className="w-16 h-16 bg-white/20 backdrop-blur-sm rounded-full flex items-center justify-center">
              <ShoppingBag className="h-8 w-8" />
            </div>
            <div>
              <h1 className="text-3xl font-bold">Mis Pedidos</h1>
              <p className="text-gray-200 mt-1">
                Historial completo de tus compras
              </p>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {orders.length === 0 ? (
          // Estado vacío cuando no hay pedidos
          <div className="bg-white rounded-lg shadow-md p-12 text-center">
            <div className="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <ShoppingBag className="h-12 w-12 text-gray-400" />
            </div>
            <h2 className="text-2xl font-bold text-gray-900 mb-4">
              Aún no tienes pedidos
            </h2>
            <p className="text-gray-600 mb-8">
              Comienza a explorar nuestro catálogo y realiza tu primera compra
            </p>
            <Link
              to="/catalog"
              className="inline-flex items-center px-6 py-3 bg-primary-900 text-white font-semibold rounded-lg hover:bg-primary-800 transition"
            >
              Explorar Catálogo
            </Link>
          </div>
        ) : (
          // Lista de pedidos
          <div className="space-y-6">
            {orders.map((order) => (
              <div key={order.id} className="bg-white rounded-lg shadow-md overflow-hidden">
                {/* Header del pedido */}
                <div className="bg-gray-50 px-6 py-4 border-b">
                  <div className="flex flex-col md:flex-row md:items-center md:justify-between space-y-2 md:space-y-0">
                    <div className="flex items-center space-x-4">
                      <div>
                        <span className="text-sm text-gray-600">Pedido</span>
                        <h3 className="font-bold text-gray-900">#{order.orderNumber}</h3>
                      </div>
                      <div className={`px-3 py-1 rounded-full text-sm font-semibold ${getStatusColor(order.status)}`}>
                        {getStatusLabel(order.status)}
                      </div>
                    </div>
                    
                    <div className="flex items-center space-x-6 text-sm text-gray-600">
                      <div className="flex items-center space-x-2">
                        <Calendar className="h-4 w-4" />
                        <span>{formatDate(order.createdAt)}</span>
                      </div>
                      <div className="flex items-center space-x-2">
                        <CreditCard className="h-4 w-4" />
                        <span>{getPaymentMethodLabel(order.paymentMethod)}</span>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Contenido del pedido */}
                <div className="p-6">
                  {/* Items del pedido */}
                  <div className="space-y-4 mb-6">
                    {order.items.map((item) => (
                      <div key={item.id} className="flex items-center space-x-4">
                        <div className="w-16 h-16 bg-gray-100 rounded flex-shrink-0 flex items-center justify-center">
                          <Package className="h-8 w-8 text-gray-400" />
                        </div>
                        <div className="flex-grow">
                          <h4 className="font-semibold text-gray-900">
                            {item.product.albumTitle}
                          </h4>
                          <p className="text-sm text-gray-600">
                            {item.product.artistName}
                          </p>
                          <div className="flex items-center space-x-2 mt-1">
                            <span className={`text-xs px-2 py-1 rounded-full ${
                              item.product.productType === 'PHYSICAL'
                                ? 'bg-primary-100 text-primary-900'
                                : 'bg-secondary-100 text-secondary-900'
                            }`}>
                              {item.product.productType === 'PHYSICAL' ? 'Vinilo' : 'Digital'}
                            </span>
                            <span className="text-sm text-gray-600">
                              Cantidad: {item.quantity}
                            </span>
                          </div>
                        </div>
                        <div className="text-right">
                          <div className="font-semibold text-gray-900">
                            {formatPrice(item.subtotal)}
                          </div>
                          {item.quantity > 1 && (
                            <div className="text-sm text-gray-500">
                              {formatPrice(item.price)} c/u
                            </div>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>

                  {/* Dirección de envío (solo si existe) */}
                  {order.shippingAddress && (
                    <div className="bg-gray-50 rounded-lg p-4 mb-6">
                      <div className="flex items-start space-x-3">
                        <MapPin className="h-5 w-5 text-gray-500 mt-0.5" />
                        <div>
                          <h4 className="font-medium text-gray-900 mb-1">
                            Dirección de Envío
                          </h4>
                          <p className="text-sm text-gray-600">
                            {order.shippingAddress.address}<br />
                            {order.shippingAddress.city}, {order.shippingAddress.state}<br />
                            {order.shippingAddress.country} {order.shippingAddress.postalCode}
                          </p>
                        </div>
                      </div>
                    </div>
                  )}

                  {/* Footer con total y acciones */}
                  <div className="flex items-center justify-between pt-6 border-t">
                    <div>
                      <span className="text-gray-600">Total del pedido</span>
                      <div className="text-2xl font-bold text-primary-900">
                        {formatPrice(order.total)}
                      </div>
                    </div>
                    
                    <div className="flex space-x-3">
                      <button className="flex items-center space-x-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition">
                        <Eye className="h-4 w-4" />
                        <span>Ver Detalles</span>
                      </button>
                      
                      {order.status === OrderStatus.DELIVERED && (
                        <button className="px-4 py-2 bg-primary-900 text-white rounded-lg hover:bg-primary-800 transition">
                          Comprar de Nuevo
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default MyOrders;