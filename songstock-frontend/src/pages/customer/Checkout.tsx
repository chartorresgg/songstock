import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../../contexts/CartContext';
import { useAuth } from '../../contexts/AuthContext';
import orderService from '../../services/order.service';
import { 
  CreditCard, 
  MapPin, 
  User as UserIcon,
  Phone,
  Mail,
  Building,
  CheckCircle,
  ArrowLeft,
  Package
} from 'lucide-react';
import toast from 'react-hot-toast';

interface CheckoutFormData {
  // Información personal
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  
  // Dirección de envío
  address: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  
  // Método de pago
  paymentMethod: 'CREDIT_CARD' | 'DEBIT_CARD' | 'PSE';
  
  // Tarjeta (si aplica)
  cardNumber: string;
  cardName: string;
  cardExpiry: string;
  cardCvv: string;
}

const Checkout = () => {
  const navigate = useNavigate();
  const { items, total, clearCart } = useCart();
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const [orderPlaced, setOrderPlaced] = useState(false);
  
  const [formData, setFormData] = useState<CheckoutFormData>({
    firstName: user?.firstName || '',
    lastName: user?.lastName || '',
    email: user?.email || '',
    phone: '',
    address: '',
    city: '',
    state: '',
    postalCode: '',
    country: 'Colombia',
    paymentMethod: 'CREDIT_CARD',
    cardNumber: '',
    cardName: '',
    cardExpiry: '',
    cardCvv: '',
  });

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
    }).format(price);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const validateForm = (): boolean => {
    if (!formData.firstName || !formData.lastName) {
      toast.error('Por favor completa tu nombre');
      return false;
    }
    
    if (!formData.email) {
      toast.error('Por favor ingresa tu email');
      return false;
    }
    
    if (!formData.phone) {
      toast.error('Por favor ingresa tu teléfono');
      return false;
    }
    
    // Validar dirección solo para productos físicos
    const hasPhysicalProducts = items.some(item => item.product.productType === 'PHYSICAL');
    
    if (hasPhysicalProducts) {
      if (!formData.address || !formData.city || !formData.state) {
        toast.error('Por favor completa la dirección de envío');
        return false;
      }
    }
    
    // Validar tarjeta si es pago con tarjeta
    if (formData.paymentMethod !== 'PSE') {
      if (!formData.cardNumber || !formData.cardName || !formData.cardExpiry || !formData.cardCvv) {
        toast.error('Por favor completa los datos de la tarjeta');
        return false;
      }
    }
    
    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) return;
    
    setLoading(true);
    
    try {
      // Preparar datos de la orden
      const orderData = {
        items: items.map(item => ({
          productId: item.product.id,
          quantity: item.quantity
        })),
        paymentMethod: formData.paymentMethod,
        shippingAddress: formData.address,
        shippingCity: formData.city,
        shippingState: formData.state,
        shippingPostalCode: formData.postalCode,
        shippingCountry: formData.country
      };

      await orderService.createOrder(orderData);
      
      setOrderPlaced(true);
      clearCart();
      toast.success('¡Pedido realizado con éxito!');
      
      // Redirigir después de 3 segundos
      setTimeout(() => {
        navigate('/my-orders');
      }, 3000);
      
    } catch (error) {
      console.error('Error creating order:', error);
      toast.error('Error al procesar el pedido. Por favor intenta nuevamente.');
    } finally {
      setLoading(false);
    }
  };

  // Si el carrito está vacío, redirigir
  if (items.length === 0 && !orderPlaced) {
    navigate('/cart');
    return null;
  }

  // Pantalla de confirmación
  if (orderPlaced) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
        <div className="max-w-md w-full bg-white rounded-lg shadow-xl p-8 text-center">
          <div className="mb-6">
            <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto">
              <CheckCircle className="h-12 w-12 text-green-600" />
            </div>
          </div>
          
          <h2 className="text-3xl font-bold text-gray-900 mb-4">
            ¡Pedido Confirmado!
          </h2>
          
          <p className="text-gray-600 mb-8">
            Hemos recibido tu pedido correctamente. Recibirás un email de confirmación 
            con los detalles y el seguimiento.
          </p>
          
          <div className="bg-gray-50 rounded-lg p-4 mb-6">
            <div className="text-sm text-gray-600 mb-1">Total pagado</div>
            <div className="text-3xl font-bold text-primary-900">
              {formatPrice(total)}
            </div>
          </div>
          
          <div className="space-y-3">
            <button
              onClick={() => navigate('/my-orders')}
              className="w-full bg-primary-900 text-white py-3 rounded-lg font-semibold hover:bg-primary-800 transition"
            >
              Ver mis pedidos
            </button>
            <button
              onClick={() => navigate('/')}
              className="w-full bg-gray-200 text-gray-700 py-3 rounded-lg font-semibold hover:bg-gray-300 transition"
            >
              Volver al inicio
            </button>
          </div>
        </div>
      </div>
    );
  }

  const hasPhysicalProducts = items.some(item => item.product.productType === 'PHYSICAL');
  const hasDigitalProducts = items.some(item => item.product.productType === 'DIGITAL');

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <h1 className="text-3xl font-bold text-gray-900">Finalizar Compra</h1>
            <button
              onClick={() => navigate('/cart')}
              className="text-gray-600 hover:text-primary-900 transition flex items-center"
            >
              <ArrowLeft className="h-5 w-5 mr-2" />
              Volver al carrito
            </button>
          </div>
        </div>
      </div>

      <form onSubmit={handleSubmit}>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Formulario */}
            <div className="lg:col-span-2 space-y-6">
              {/* Información Personal */}
              <div className="bg-white rounded-lg shadow-md p-6">
                <div className="flex items-center space-x-2 mb-6">
                  <UserIcon className="h-6 w-6 text-primary-900" />
                  <h2 className="text-xl font-bold text-gray-900">
                    Información Personal
                  </h2>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Nombre *
                    </label>
                    <input
                      type="text"
                      name="firstName"
                      required
                      value={formData.firstName}
                      onChange={handleChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Apellido *
                    </label>
                    <input
                      type="text"
                      name="lastName"
                      required
                      value={formData.lastName}
                      onChange={handleChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      <Mail className="inline h-4 w-4 mr-1" />
                      Email *
                    </label>
                    <input
                      type="email"
                      name="email"
                      required
                      value={formData.email}
                      onChange={handleChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      <Phone className="inline h-4 w-4 mr-1" />
                      Teléfono *
                    </label>
                    <input
                      type="tel"
                      name="phone"
                      required
                      value={formData.phone}
                      onChange={handleChange}
                      placeholder="+57 300 123 4567"
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    />
                  </div>
                </div>
              </div>

              {/* Dirección de Envío (solo si hay productos físicos) */}
              {hasPhysicalProducts && (
                <div className="bg-white rounded-lg shadow-md p-6">
                  <div className="flex items-center space-x-2 mb-6">
                    <MapPin className="h-6 w-6 text-primary-900" />
                    <h2 className="text-xl font-bold text-gray-900">
                      Dirección de Envío
                    </h2>
                  </div>
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Dirección *
                      </label>
                      <input
                        type="text"
                        name="address"
                        required={hasPhysicalProducts}
                        value={formData.address}
                        onChange={handleChange}
                        placeholder="Calle 123 #45-67"
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                      />
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                          Ciudad *
                        </label>
                        <input
                          type="text"
                          name="city"
                          required={hasPhysicalProducts}
                          value={formData.city}
                          onChange={handleChange}
                          placeholder="Bogotá"
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                          Departamento *
                        </label>
                        <input
                          type="text"
                          name="state"
                          required={hasPhysicalProducts}
                          value={formData.state}
                          onChange={handleChange}
                          placeholder="Cundinamarca"
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                          Código Postal
                        </label>
                        <input
                          type="text"
                          name="postalCode"
                          value={formData.postalCode}
                          onChange={handleChange}
                          placeholder="110111"
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                          País *
                        </label>
                        <select
                          name="country"
                          required={hasPhysicalProducts}
                          value={formData.country}
                          onChange={handleChange}
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                        >
                          <option value="Colombia">Colombia</option>
                          <option value="México">México</option>
                          <option value="Argentina">Argentina</option>
                          <option value="Chile">Chile</option>
                        </select>
                      </div>
                    </div>
                  </div>
                </div>
              )}

              {/* Método de Pago */}
              <div className="bg-white rounded-lg shadow-md p-6">
                <div className="flex items-center space-x-2 mb-6">
                  <CreditCard className="h-6 w-6 text-primary-900" />
                  <h2 className="text-xl font-bold text-gray-900">
                    Método de Pago
                  </h2>
                </div>

                {/* Selector de método */}
                <div className="grid grid-cols-3 gap-4 mb-6">
                  <button
                    type="button"
                    onClick={() => setFormData(prev => ({ ...prev, paymentMethod: 'CREDIT_CARD' }))}
                    className={`p-4 border-2 rounded-lg transition ${
                      formData.paymentMethod === 'CREDIT_CARD'
                        ? 'border-primary-900 bg-primary-50'
                        : 'border-gray-200 hover:border-gray-300'
                    }`}
                  >
                    <CreditCard className="h-6 w-6 mx-auto mb-2" />
                    <div className="text-sm font-medium">Crédito</div>
                  </button>
                  <button
                    type="button"
                    onClick={() => setFormData(prev => ({ ...prev, paymentMethod: 'DEBIT_CARD' }))}
                    className={`p-4 border-2 rounded-lg transition ${
                      formData.paymentMethod === 'DEBIT_CARD'
                        ? 'border-primary-900 bg-primary-50'
                        : 'border-gray-200 hover:border-gray-300'
                    }`}
                  >
                    <CreditCard className="h-6 w-6 mx-auto mb-2" />
                    <div className="text-sm font-medium">Débito</div>
                  </button>
                  <button
                    type="button"
                    onClick={() => setFormData(prev => ({ ...prev, paymentMethod: 'PSE' }))}
                    className={`p-4 border-2 rounded-lg transition ${
                      formData.paymentMethod === 'PSE'
                        ? 'border-primary-900 bg-primary-50'
                        : 'border-gray-200 hover:border-gray-300'
                    }`}
                  >
                    <Building className="h-6 w-6 mx-auto mb-2" />
                    <div className="text-sm font-medium">PSE</div>
                  </button>
                </div>

                {/* Formulario de tarjeta */}
                {formData.paymentMethod !== 'PSE' && (
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Número de Tarjeta *
                      </label>
                      <input
                        type="text"
                        name="cardNumber"
                        required
                        value={formData.cardNumber}
                        onChange={handleChange}
                        placeholder="1234 5678 9012 3456"
                        maxLength={19}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Nombre en la Tarjeta *
                      </label>
                      <input
                        type="text"
                        name="cardName"
                        required
                        value={formData.cardName}
                        onChange={handleChange}
                        placeholder="JUAN PEREZ"
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                      />
                    </div>
                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                          Fecha de Expiración *
                        </label>
                        <input
                          type="text"
                          name="cardExpiry"
                          required
                          value={formData.cardExpiry}
                          onChange={handleChange}
                          placeholder="MM/AA"
                          maxLength={5}
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                          CVV *
                        </label>
                        <input
                          type="text"
                          name="cardCvv"
                          required
                          value={formData.cardCvv}
                          onChange={handleChange}
                          placeholder="123"
                          maxLength={4}
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                        />
                      </div>
                    </div>
                  </div>
                )}

                {formData.paymentMethod === 'PSE' && (
                  <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                    <p className="text-sm text-gray-700">
                      Serás redirigido a la plataforma PSE para completar el pago de forma segura.
                    </p>
                  </div>
                )}
              </div>
            </div>

            {/* Resumen del Pedido */}
            <div className="lg:col-span-1">
              <div className="bg-white rounded-lg shadow-md p-6 sticky top-4">
                <h2 className="text-xl font-bold text-gray-900 mb-6">
                  Resumen del Pedido
                </h2>

                {/* Productos */}
                <div className="space-y-4 mb-6 max-h-64 overflow-y-auto">
                  {items.map((item) => (
                    <div key={item.product.id} className="flex space-x-3">
                      <div className="w-16 h-16 bg-gray-100 rounded flex-shrink-0 flex items-center justify-center">
                        <Package className="h-8 w-8 text-gray-400" />
                      </div>
                      <div className="flex-grow">
                        <div className="font-medium text-sm text-gray-900 line-clamp-2">
                          {item.product.albumTitle}
                        </div>
                        <div className="text-sm text-gray-600">
                          Cantidad: {item.quantity}
                        </div>
                        <div className="text-sm font-medium text-primary-900">
                          {formatPrice(item.product.price * item.quantity)}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>

                {/* Info de productos digitales */}
                {hasDigitalProducts && (
                  <div className="bg-green-50 border border-green-200 rounded-lg p-3 mb-6">
                    <p className="text-sm text-green-800">
                      ✓ Los productos digitales estarán disponibles inmediatamente después del pago
                    </p>
                  </div>
                )}

                {/* Totales */}
                <div className="space-y-3 mb-6 border-t pt-4">
                  <div className="flex justify-between text-gray-600">
                    <span>Subtotal</span>
                    <span className="font-medium">{formatPrice(total)}</span>
                  </div>
                  {hasPhysicalProducts && (
                    <div className="flex justify-between text-gray-600">
                      <span>Envío</span>
                      <span className="text-green-600 font-medium">A calcular</span>
                    </div>
                  )}
                  <div className="border-t pt-3">
                    <div className="flex justify-between text-lg font-bold text-gray-900">
                      <span>Total</span>
                      <span className="text-primary-900">{formatPrice(total)}</span>
                    </div>
                  </div>
                </div>

                {/* Botón de pago */}
                <button
                  type="submit"
                  disabled={loading}
                  className="w-full bg-primary-900 text-white py-4 rounded-lg font-semibold hover:bg-primary-800 transition disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {loading ? 'Procesando...' : `Pagar ${formatPrice(total)}`}
                </button>

                {/* Seguridad */}
                <div className="mt-4 text-center text-sm text-gray-500">
                  🔒 Pago seguro y encriptado
                </div>
              </div>
            </div>
          </div>
        </div>
      </form>
    </div>
  );
};

export default Checkout;