import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import orderService from '../../services/order.service';
import { Order, OrderStatus, OrderReview } from '../../types/order.types';
import { Package, Calendar, CreditCard, MapPin, Eye, ShoppingBag, Truck, CheckCircle, Clock, X, Star } from 'lucide-react';
import { toast } from 'react-hot-toast';

const MyOrders = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [reviewModalOrder, setReviewModalOrder] = useState<Order | null>(null);
  const [rating, setRating] = useState(0);
  const [hoveredRating, setHoveredRating] = useState(0);
  const [comment, setComment] = useState('');
  const [submittingReview, setSubmittingReview] = useState(false);
  const [confirmingOrderId, setConfirmingOrderId] = useState<number | null>(null);
 

  useEffect(() => {
    loadOrders();
  }, []);

  const loadOrders = async () => {
    setLoading(true);
    try {
      let data = await orderService.getMyOrders();
      
      // Cargar reviews para cada orden
      const ordersWithReviews = await Promise.all(
        data.map(async (order) => {
          const review = await orderService.getReview(order.id);
          return { ...order, review };
        })
      );
      
      setOrders(ordersWithReviews);
    } catch (error) {
      console.error('Error loading orders:', error);
    } finally {
      setLoading(false);
    }
  };

    const handleConfirmReceipt = async (orderId: number) => {
        if (!confirm('¿Confirmas que recibiste este pedido?')) return;
        
        setConfirmingOrderId(orderId);
        try {
          await orderService.confirmOrderReceipt(orderId);
          toast.success('Pedido confirmado como recibido');
          await loadOrders();
        } catch (error: any) {
          console.error('Error:', error);
          toast.error(error.response?.data?.message || 'Error al confirmar recepción');
        } finally {
          setConfirmingOrderId(null);
        }
      };

  const handleSubmitReview = async () => {
    if (!reviewModalOrder || rating === 0) return;
    
    setSubmittingReview(true);
    try {
      await orderService.createReview(reviewModalOrder.id, {
        rating,
        comment: comment.trim() || undefined
      });
      
      alert('Â¡ValoraciÃ³n enviada exitosamente!');
      setReviewModalOrder(null);
      setRating(0);
      setComment('');
      loadOrders(); // Recargar Ã³rdenes
    } catch (error: any) {
      alert(error.message || 'Error al enviar valoraciÃ³n');
    } finally {
      setSubmittingReview(false);
    }
  };

  const canReview = (order: Order) => {
    return (order.status === OrderStatus.DELIVERED || order.status === OrderStatus.RECEIVED) && !order.review;
  };

  const renderStars = (currentRating: number, isInteractive: boolean = false) => {
    return (
      <div className="flex gap-1">
        {[1, 2, 3, 4, 5].map((star) => (
          <Star
            key={star}
            className={`w-6 h-6 ${
              star <= (isInteractive ? (hoveredRating || currentRating) : currentRating)
                ? 'fill-yellow-400 text-yellow-400'
                : 'text-gray-300'
            } ${isInteractive ? 'cursor-pointer hover:scale-110 transition-transform' : ''}`}
            onClick={() => isInteractive && setRating(star)}
            onMouseEnter={() => isInteractive && setHoveredRating(star)}
            onMouseLeave={() => isInteractive && setHoveredRating(0)}
          />
        ))}
      </div>
    );
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
    }).format(price);
  };

  const formatDate = (dateInput: string | number[]) => {
    if (!dateInput) return 'Fecha no disponible';
    
    try {
      let date: Date;
      
      // Si es un array [año, mes, día, hora, minuto, segundo]
      if (Array.isArray(dateInput)) {
        const [year, month, day, hour = 0, minute = 0, second = 0] = dateInput;
        // Los meses en JavaScript van de 0-11, Spring Boot los envía 1-12
        date = new Date(year, month - 1, day, hour, minute, second);
      } else {
        date = new Date(dateInput);
      }
      
      if (isNaN(date.getTime())) return 'Fecha inválida';
      
      return date.toLocaleDateString('es-CO', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch {
      return 'Fecha inválida';
    }
  };

  const getStatusLabel = (status: OrderStatus) => {
    const labels: Record<OrderStatus, string> = {
      [OrderStatus.PENDING]: 'Pendiente',
      [OrderStatus.CONFIRMED]: 'Confirmado',
      [OrderStatus.PROCESSING]: 'En Proceso',
      [OrderStatus.SHIPPED]: 'Enviado',
      [OrderStatus.DELIVERED]: 'Entregado',
      [OrderStatus.CANCELLED]: 'Cancelado',
      [OrderStatus.ACCEPTED]: 'Aceptado',
      [OrderStatus.REJECTED]: 'Rechazado',
      [OrderStatus.RECEIVED]: 'Recibida'
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
      [OrderStatus.ACCEPTED]: 'bg-green-100 text-green-800',
      [OrderStatus.REJECTED]: 'bg-red-100 text-red-800',
      [OrderStatus.RECEIVED]: 'bg-green-200 text-green-900',
    };
    return colors[status];
  };

  const getPaymentMethodLabel = (method: string) => {
    const methods: Record<string, string> = {
      'credit_card': 'Tarjeta de CrÃ©dito',
      'debit_card': 'Tarjeta de DÃ©bito',
      'pse': 'PSE',
    };
    return methods[method] || method;
  };

  // FunciÃ³n para obtener resumen de estados de items
  const getItemsStatusSummary = (items: any[]) => {
    const summary = items.reduce((acc, item) => {
      acc[item.status] = (acc[item.status] || 0) + 1;
      return acc;
    }, {} as Record<string, number>);

    return summary;
  };

  // FunciÃ³n para renderizar badges de resumen de items
  const renderItemsSummary = (items: any[]) => {
    const summary = getItemsStatusSummary(items);
    const badges = [];

    if (summary['REJECTED']) {
      badges.push(
        <span key="rejected" className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800">
          {summary['REJECTED']} rechazado{summary['REJECTED'] > 1 ? 's' : ''}
        </span>
      );
    }
    if (summary['ACCEPTED']) {
      badges.push(
        <span key="accepted" className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
          {summary['ACCEPTED']} aceptado{summary['ACCEPTED'] > 1 ? 's' : ''}
        </span>
      );
    }
    if (summary['PENDING']) {
      badges.push(
        <span key="pending" className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
          {summary['PENDING']} pendiente{summary['PENDING'] > 1 ? 's' : ''}
        </span>
      );
    }
    if (summary['SHIPPED']) {
      badges.push(
        <span key="shipped" className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-indigo-100 text-indigo-800">
          {summary['SHIPPED']} enviado{summary['SHIPPED'] > 1 ? 's' : ''}
        </span>
      );
    }
    if (summary['DELIVERED']) {
      badges.push(
        <span key="delivered" className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
          {summary['DELIVERED']} entregado{summary['DELIVERED'] > 1 ? 's' : ''}
        </span>
      );
    }

    return badges;
  };

  const TrackingTimeline = ({ order }: { order: Order }) => {
    // Calcular la fecha de envío más temprana de los items
    const getEarliestShippedDate = () => {
      if (!order.items || order.items.length === 0) return order.shippedAt;
      
      const shippedItems = order.items.filter(item => 
        item.shippedAt && (item.status === 'SHIPPED' || item.status === 'DELIVERED')
      );
      
      if (shippedItems.length === 0) return order.shippedAt;
      
      // Convertir todas las fechas a Date y encontrar la más temprana
      const dates = shippedItems
        .map(item => {
          if (Array.isArray(item.shippedAt)) {
            const [year, month, day, hour = 0, minute = 0, second = 0] = item.shippedAt;
            return new Date(year, month - 1, day, hour, minute, second);
          }
          return new Date(item.shippedAt!);
        })
        .filter(date => !isNaN(date.getTime()));
      
      if (dates.length === 0) return order.shippedAt;
      
      const earliestDate = new Date(Math.min(...dates.map(d => d.getTime())));
      // Convertir a array format para ser consistente con el backend
      return [
        earliestDate.getFullYear(),
        earliestDate.getMonth() + 1,
        earliestDate.getDate(),
        earliestDate.getHours(),
        earliestDate.getMinutes(),
        earliestDate.getSeconds()
      ];
    };

    const steps = [
      { 
        status: OrderStatus.PENDING, 
        label: 'Pedido Recibido', 
        icon: Clock,
        date: order.createdAt,
        completed: true 
      },
      { 
        status: OrderStatus.SHIPPED, 
        label: 'En Camino', 
        icon: Truck,
        date: getEarliestShippedDate(),
        completed: order.status === OrderStatus.SHIPPED || order.status === OrderStatus.DELIVERED 
      },
      { 
        status: OrderStatus.DELIVERED, 
        label: 'Entregado', 
        icon: CheckCircle,
        date: order.deliveredAt,
        completed: order.status === OrderStatus.DELIVERED 
      },
    ];

    return (
      <div className="py-6">
        <div className="flex justify-between items-start relative">
          {steps.map((step, index) => {
            const Icon = step.icon;
            return (
              <div key={step.status} className="flex flex-col items-center flex-1 relative">
                {/* LÃ­nea conectora */}
                {index < steps.length - 1 && (
                  <div 
                    className={`absolute top-6 left-1/2 w-full h-1 ${
                      step.completed ? 'bg-green-500' : 'bg-gray-300'
                    }`}
                    style={{ zIndex: 0 }}
                  />
                )}
                
                {/* Ãcono */}
                <div 
                  className={`w-12 h-12 rounded-full flex items-center justify-center mb-2 relative z-10 ${
                    step.completed 
                      ? 'bg-green-500 text-white' 
                      : 'bg-gray-300 text-gray-600'
                  }`}
                >
                  <Icon className="h-6 w-6" />
                </div>
                
                {/* Label */}
                <div className="text-center">
                  <p className={`text-sm font-semibold ${
                    step.completed ? 'text-gray-900' : 'text-gray-500'
                  }`}>
                    {step.label}
                  </p>
                  {step.date && (
                    <p className="text-xs text-gray-500 mt-1">
                      {formatDate(step.date)}
                    </p>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </div>
    );
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
          <div className="bg-white rounded-lg shadow-md p-12 text-center">
            <div className="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <ShoppingBag className="h-12 w-12 text-gray-400" />
            </div>
            <h2 className="text-2xl font-bold text-gray-900 mb-4">
              AÃºn no tienes pedidos
            </h2>
            <p className="text-gray-600 mb-8">
              Comienza a explorar nuestro catÃ¡logo y realiza tu primera compra
            </p>
            <Link
              to="/catalog"
              className="inline-flex items-center px-6 py-3 bg-primary-900 text-white font-semibold rounded-lg hover:bg-primary-800 transition"
            >
              Explorar CatÃ¡logo
            </Link>
          </div>
        ) : (
          <div className="space-y-6">
            {orders.map((order) => (
              <div key={order.id} className="bg-white rounded-lg shadow-md overflow-hidden">
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

                  {/* Resumen de estados de items */}
                  <div className="flex flex-wrap gap-2 mt-3">
                    {renderItemsSummary(order.items)}
                  </div>
                </div>

                <div className="p-6">
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

                  {order.shippingAddress && (
                    <div className="bg-gray-50 rounded-lg p-4 mb-6">
                      <div className="flex items-start space-x-3">
                        <MapPin className="h-5 w-5 text-gray-500 mt-0.5" />
                        <div>
                          <h4 className="font-medium text-gray-900 mb-1">
                            Dirección de Enví­o
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

                  <div className="flex items-center justify-between pt-6 border-t">
                    <div>
                      <span className="text-gray-600">Total del pedido</span>
                      <div className="text-2xl font-bold text-primary-900">
                        {formatPrice(order.total)}
                      </div>
                    </div>
                    
                    <div className="flex space-x-3">
                      <button 
                        onClick={() => setSelectedOrder(order)}
                        className="flex items-center space-x-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition"
                      >
                        <Eye className="h-4 w-4" />
                        <span>Ver Detalles</span>
                      </button>

                      {/* Botón confirmar recepción */}
                      {order.status === OrderStatus.DELIVERED && (
                        <button
                          onClick={() => handleConfirmReceipt(order.id)}
                          disabled={confirmingOrderId === order.id}
                          className="flex items-center space-x-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:opacity-50 transition"
                        >
                          <CheckCircle className="h-4 w-4" />
                          <span>{confirmingOrderId === order.id ? 'Confirmando...' : 'Confirmar Recepción'}</span>
                        </button>
                      )}

                      {order.review && (
                        <div className="flex-1 p-3 bg-yellow-50 rounded-lg border border-yellow-200">
                          <div className="flex items-center gap-2 mb-1">
                            <span className="text-sm font-medium text-gray-700">Tu valoraciÃ³n:</span>
                            <div className="flex gap-1">
                              {[1, 2, 3, 4, 5].map((star) => (
                                <Star
                                  key={star}
                                  className={`w-4 h-4 ${
                                    star <= order.review!.rating
                                      ? 'fill-yellow-400 text-yellow-400'
                                      : 'text-gray-300'
                                  }`}
                                />
                              ))}
                            </div>
                          </div>
                          {order.review.comment && (
                            <p className="text-sm text-gray-600">{order.review.comment}</p>
                          )}
                        </div>
                      )}

                      {canReview(order) && (
                        <button
                          onClick={() => setReviewModalOrder(order)}
                          className="flex items-center gap-2 px-4 py-2 bg-yellow-500 text-white rounded-lg hover:bg-yellow-600"
                        >
                          <Star className="w-4 h-4" />
                          Valorar Compra
                        </button>
                      )}
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

      {/* Modal de Detalle RediseÃ±ado */}
      {selectedOrder && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              {/* Header */}
              <div className="flex items-start justify-between mb-6 pb-4 border-b border-gray-200">
                <div>
                  <h3 className="text-2xl font-bold text-gray-900 mb-1">
                    Pedido #{selectedOrder.orderNumber}
                  </h3>
                  <p className="text-sm text-gray-500">
                    Realizado el {formatDate(selectedOrder.createdAt)}
                  </p>
                </div>
                <button
                  onClick={() => setSelectedOrder(null)}
                  className="text-gray-400 hover:text-gray-600 transition"
                >
                  <X className="h-6 w-6" />
                </button>
              </div>

              {/* InformaciÃ³n general en cards */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                <div className="bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg p-4">
                  <div className="flex items-center gap-2 mb-2">
                    <Package className="h-5 w-5 text-blue-600" />
                    <span className="text-sm font-medium text-blue-900">Estado</span>
                  </div>
                  <p className={`inline-block px-3 py-1 rounded-full text-sm font-semibold ${getStatusColor(selectedOrder.status)}`}>
                    {getStatusLabel(selectedOrder.status)}
                  </p>
                </div>

                <div className="bg-gradient-to-br from-green-50 to-green-100 rounded-lg p-4">
                  <div className="flex items-center gap-2 mb-2">
                    <CreditCard className="h-5 w-5 text-green-600" />
                    <span className="text-sm font-medium text-green-900">MÃ©todo de Pago</span>
                  </div>
                  <p className="text-base font-semibold text-gray-900">
                    {getPaymentMethodLabel(selectedOrder.paymentMethod)}
                  </p>
                </div>

                <div className="bg-gradient-to-br from-purple-50 to-purple-100 rounded-lg p-4">
                  <div className="flex items-center gap-2 mb-2">
                    <ShoppingBag className="h-5 w-5 text-purple-600" />
                    <span className="text-sm font-medium text-purple-900">Total</span>
                  </div>
                  <p className="text-xl font-bold text-gray-900">
                    {formatPrice(selectedOrder.total)}
                  </p>
                </div>
              </div>

              {/* Timeline de seguimiento */}
              {(selectedOrder.status === OrderStatus.SHIPPED || selectedOrder.status === OrderStatus.DELIVERED) && (
                <div className="bg-gray-50 rounded-xl p-6 mb-6">
                  <h4 className="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                    <Truck className="h-5 w-5 text-indigo-600" />
                    Estado del EnvÃ­o
                  </h4>
                  <TrackingTimeline order={selectedOrder} />
                </div>
              )}

              {/* Productos de la orden */}
              <div className="mb-6">
                <h4 className="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                  <Package className="h-5 w-5 text-gray-700" />
                  Productos ({selectedOrder.items.length})
                </h4>
                <div className="space-y-4">
                  {selectedOrder.items.map((item) => (
                    <div 
                      key={item.id} 
                      className={`border rounded-lg p-4 transition ${
                        item.status === 'REJECTED' 
                          ? 'border-red-200 bg-red-50' 
                          : 'border-gray-200 bg-white hover:shadow-md'
                      }`}
                    >
                      <div className="flex justify-between items-start gap-4">
                        <div className="flex-1">
                          <div className="flex items-start justify-between mb-2">
                            <div>
                              <h5 className="font-semibold text-gray-900 text-lg">
                                {item.product.albumTitle}
                              </h5>
                              <p className="text-sm text-gray-600">
                                {item.product.artistName}
                              </p>
                            </div>
                            <span className={`px-3 py-1 rounded-full text-xs font-semibold whitespace-nowrap ${
                              item.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                              item.status === 'ACCEPTED' ? 'bg-green-100 text-green-800' :
                              item.status === 'REJECTED' ? 'bg-red-100 text-red-800' :
                              item.status === 'PROCESSING' ? 'bg-blue-100 text-blue-800' :
                              item.status === 'SHIPPED' ? 'bg-indigo-100 text-indigo-800' :
                              item.status === 'DELIVERED' ? 'bg-green-100 text-green-800' :
                              'bg-gray-100 text-gray-800'
                            }`}>
                              {item.status === 'PENDING' ? 'Pendiente' :
                               item.status === 'ACCEPTED' ? 'Aceptado' :
                               item.status === 'REJECTED' ? 'Rechazado' :
                               item.status === 'PROCESSING' ? 'En Proceso' :
                               item.status === 'SHIPPED' ? 'Enviado' :
                               item.status === 'DELIVERED' ? 'Entregado' :
                               item.status}
                            </span>
                          </div>
                          
                          <div className="flex items-center gap-4 text-sm text-gray-600 mb-2">
                            <span className="flex items-center gap-1">
                              <Package className="h-4 w-4" />
                              Cantidad: {item.quantity}
                            </span>
                            <span className="font-semibold text-gray-900">
                              {formatPrice(item.subtotal)}
                            </span>
                          </div>

                          <p className="text-xs text-gray-500">
                            Proveedor: {item.providerName}
                          </p>

                          {/* Fecha de envío individual del item */}
                          {item.shippedAt && (item.status === 'SHIPPED' || item.status === 'DELIVERED') && (
                            <p className="text-xs text-indigo-600 font-medium mt-2 flex items-center gap-1">
                              <Truck className="h-3 w-3" />
                              Enviado: {formatDate(item.shippedAt)}
                            </p>
                          )}

                          {/* RazÃ³n de rechazo destacada */}
                          {item.status === 'REJECTED' && item.rejectionReason && (
                            <div className="mt-3 p-3 bg-red-100 border-l-4 border-red-500 rounded">
                              <div className="flex items-start gap-2">
                                <X className="h-5 w-5 text-red-600 mt-0.5 flex-shrink-0" />
                                <div className="flex-1">
                                  <p className="text-sm font-bold text-red-900 mb-1">
                                    Motivo del rechazo:
                                  </p>
                                  <p className="text-sm text-red-800 leading-relaxed">
                                    {item.rejectionReason}
                                  </p>
                                </div>
                              </div>
                            </div>
                          )}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Dirección de enví­o */}
              {selectedOrder.shippingAddress && (
                <div className="bg-gray-50 rounded-lg p-4 mb-6">
                  <h4 className="text-sm font-semibold text-gray-900 mb-2 flex items-center gap-2">
                    <MapPin className="h-4 w-4 text-gray-600" />
                    Dirección de enví­o
                  </h4>
                  <p className="text-sm text-gray-700">
                    {selectedOrder.shippingAddress.address}
                  </p>
                  <p className="text-sm text-gray-600">
                    {selectedOrder.shippingAddress.city}, {selectedOrder.shippingAddress.state}
                  </p>
                  <p className="text-sm text-gray-600">
                    {selectedOrder.shippingAddress.postalCode}, {selectedOrder.shippingAddress.country}
                  </p>
                </div>
              )}

              {/* BotÃ³n de cerrar */}
              <div className="flex justify-end pt-4 border-t border-gray-200">
                <button
                  onClick={() => setSelectedOrder(null)}
                  className="px-6 py-2.5 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition font-medium"
                >
                  Cerrar
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Modal de ValoraciÃ³n */}
      {reviewModalOrder && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-2xl max-w-md w-full p-6">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-xl font-bold text-gray-900">
                Valorar Compra
              </h3>
              <button
                onClick={() => {
                  setReviewModalOrder(null);
                  setRating(0);
                  setComment('');
                }}
                className="text-gray-400 hover:text-gray-600"
              >
                <X className="w-6 h-6" />
              </button>
            </div>

            <div className="space-y-4">
              <div>
                <p className="text-sm text-gray-600 mb-2">
                  Orden: <span className="font-semibold">{reviewModalOrder.orderNumber}</span>
                </p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  CalificaciÃ³n *
                </label>
                {renderStars(rating, true)}
                {rating > 0 && (
                  <p className="text-sm text-gray-500 mt-1">
                    {rating} de 5 estrellas
                  </p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Comentario (opcional)
                </label>
                <textarea
                  value={comment}
                  onChange={(e) => setComment(e.target.value)}
                  placeholder="CuÃ©ntanos sobre tu experiencia..."
                  maxLength={1000}
                  rows={4}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent resize-none"
                />
                <p className="text-xs text-gray-500 mt-1">
                  {comment.length}/1000 caracteres
                </p>
              </div>

              <div className="flex gap-3 pt-4">
                <button
                  onClick={() => {
                    setReviewModalOrder(null);
                    setRating(0);
                    setComment('');
                  }}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50"
                >
                  Cancelar
                </button>
                <button
                  onClick={handleSubmitReview}
                  disabled={rating === 0 || submittingReview}
                  className="flex-1 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 disabled:bg-gray-300 disabled:cursor-not-allowed"
                >
                  {submittingReview ? 'Enviando...' : 'Enviar ValoraciÃ³n'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default MyOrders;