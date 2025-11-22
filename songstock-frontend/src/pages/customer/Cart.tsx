import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../../contexts/CartContext';
import { Trash2, Plus, Minus, ShoppingBag, ArrowLeft, ArrowRight, Music } from 'lucide-react';
import { Product } from '../../types/product.types';

const Cart = () => {
  const navigate = useNavigate();
  const { items, total, removeItem, updateQuantity, clearCart, itemCount } = useCart();

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
    }).format(price);
  };

  const getPrimaryImageUrl = (product: Product) => {
    const primaryImage = product.images?.find(img => img.isPrimary);
    return primaryImage?.imageUrl || null;
  };

  const handleCheckout = () => {
    if (items.length === 0) return;
    navigate('/checkout');
  };

  if (items.length === 0) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center max-w-md mx-auto px-4">
          <div className="mb-6">
            <ShoppingBag className="h-24 w-24 text-gray-300 mx-auto" />
          </div>
          <h2 className="text-3xl font-bold text-gray-900 mb-4">
            Tu carrito está vacío
          </h2>
          <p className="text-gray-600 mb-8">
            Agrega productos al carrito para comenzar tu compra
          </p>
          <Link
            to="/catalog"
            className="inline-flex items-center px-6 py-3 bg-primary-900 text-white font-semibold rounded-lg hover:bg-primary-800 transition"
          >
            <ArrowLeft className="h-5 w-5 mr-2" />
            Ir al Catálogo
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Carrito de Compras</h1>
              <p className="text-gray-600 mt-1">
                {itemCount} {itemCount === 1 ? 'producto' : 'productos'} en tu carrito
              </p>
            </div>
            <button
              onClick={() => navigate(-1)}
              className="text-gray-600 hover:text-primary-900 transition"
            >
              <ArrowLeft className="h-6 w-6" />
            </button>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Cart Items */}
          <div className="lg:col-span-2 space-y-4">
            {items.map((item, index) => (
              <div
                key={`${item.product?.id || item.song?.id}-${index}`}
                className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition"
              >
                {/* Renderizar canción */}
                {item.song ? (
                  <div className="flex items-center space-x-4">
                    <div className="w-24 h-24 bg-gradient-to-br from-green-100 to-blue-100 rounded-lg flex items-center justify-center">
                      <Music className="h-12 w-12 text-green-600" />
                    </div>

                    <div className="flex-grow">
                      <h3 className="text-lg font-semibold text-gray-900">
                        🎵 {item.song.title}
                      </h3>
                      <p className="text-gray-600">{item.song.artistName}</p>
                      <p className="text-sm text-gray-500">{item.song.albumTitle}</p>
                      <span className="inline-block text-xs px-2 py-1 mt-1 rounded-full bg-green-100 text-green-900">
                        {item.song.format} - Descarga Digital
                      </span>
                    </div>

                    <div className="flex flex-col items-end space-y-4">
                      <div className="text-2xl font-bold text-primary-900">
                        {formatPrice(item.song.price)}
                      </div>

                      <button
                        onClick={() => removeItem(item.song!.id, 'song')}
                        className="text-red-600 hover:text-red-700 transition flex items-center space-x-1 text-sm"
                      >
                        <Trash2 className="h-4 w-4" />
                        <span>Eliminar</span>
                      </button>
                    </div>
                  </div>
                ) : (
                  /* Renderizar producto (álbum) */
                  <div className="flex items-center space-x-4">
                    <Link
                      to={`/product/${item.product!.id}`}
                      className="flex-shrink-0"
                    >
                      <div className="w-24 h-24 bg-gradient-to-br from-primary-100 to-secondary-100 rounded-lg flex items-center justify-center overflow-hidden">
                        {getPrimaryImageUrl(item.product!) ? (
                          <img
                            src={getPrimaryImageUrl(item.product!)!}
                            alt={item.product!.albumTitle}
                            className="w-full h-full object-cover"
                            loading="lazy"
                          />
                        ) : (
                          <ShoppingBag className="h-12 w-12 text-primary-300" />
                        )}
                      </div>
                    </Link>

                    <div className="flex-grow">
                      <Link
                        to={`/product/${item.product!.id}`}
                        className="text-lg font-semibold text-gray-900 hover:text-primary-900 transition"
                      >
                        {item.product!.albumTitle}
                      </Link>
                      <p className="text-gray-600">{item.product!.artistName}</p>
                      <div className="flex items-center space-x-2 mt-1">
                        <span className={`text-xs px-2 py-1 rounded-full ${
                          item.product!.productType === 'PHYSICAL'
                            ? 'bg-primary-100 text-primary-900'
                            : 'bg-secondary-100 text-secondary-900'
                        }`}>
                          {item.product!.productType === 'PHYSICAL' ? 'Vinilo' : 'Digital'}
                        </span>
                        {item.product!.stockQuantity <= 5 && (
                          <span className="text-xs text-red-600 font-medium">
                            ¡Solo {item.product!.stockQuantity} disponibles!
                          </span>
                        )}
                      </div>
                    </div>

                    <div className="flex flex-col items-end space-y-4">
                      <div className="text-right">
                        <div className="text-2xl font-bold text-primary-900">
                          {formatPrice(item.product!.price * item.quantity)}
                        </div>
                        {item.quantity > 1 && (
                          <div className="text-sm text-gray-500">
                            {formatPrice(item.product!.price)} c/u
                          </div>
                        )}
                      </div>

                      {/* Quantity Controls */}
                      <div className="flex items-center space-x-2">
                        <button
                          onClick={() => updateQuantity(item.product!.id, item.quantity - 1)}
                          className="p-1 rounded-lg border border-gray-300 hover:bg-gray-100 transition"
                        >
                          <Minus className="h-4 w-4" />
                        </button>
                        <span className="w-12 text-center font-medium">
                          {item.quantity}
                        </span>
                        <button
                          onClick={() => updateQuantity(item.product!.id, item.quantity + 1)}
                          disabled={item.quantity >= item.product!.stockQuantity}
                          className="p-1 rounded-lg border border-gray-300 hover:bg-gray-100 transition disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                          <Plus className="h-4 w-4" />
                        </button>
                      </div>

                      <button
                        onClick={() => removeItem(item.product!.id, 'product')}
                        className="text-red-600 hover:text-red-700 transition flex items-center space-x-1 text-sm"
                      >
                        <Trash2 className="h-4 w-4" />
                        <span>Eliminar</span>
                      </button>
                    </div>
                  </div>
                )}
              </div>
            ))}

            {/* Clear Cart Button */}
            <button
              onClick={clearCart}
              className="w-full py-3 text-red-600 hover:text-red-700 font-medium transition"
            >
              Vaciar Carrito
            </button>
          </div>

          {/* Order Summary */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-lg shadow-md p-6 sticky top-4">
              <h2 className="text-xl font-bold text-gray-900 mb-6">
                Resumen del Pedido
              </h2>

              <div className="space-y-4 mb-6">
                <div className="flex justify-between text-gray-600">
                  <span>Subtotal ({itemCount} {itemCount === 1 ? 'producto' : 'productos'})</span>
                  <span className="font-medium">{formatPrice(total)}</span>
                </div>

                <div className="flex justify-between text-gray-600">
                  <span>Envío</span>
                  <span className="text-green-600 font-medium">Calculado en checkout</span>
                </div>

                <div className="border-t pt-4">
                  <div className="flex justify-between text-lg font-bold text-gray-900">
                    <span>Total</span>
                    <span className="text-primary-900">{formatPrice(total)}</span>
                  </div>
                </div>
              </div>

              <button
                onClick={handleCheckout}
                className="w-full bg-primary-900 text-white py-4 rounded-lg font-semibold hover:bg-primary-800 transition mb-4 flex items-center justify-center"
              >
                Proceder al Checkout
                <ArrowRight className="ml-2 h-5 w-5" />
              </button>

              <Link
                to="/catalog"
                className="block text-center text-primary-900 hover:text-primary-700 font-medium transition"
              >
                Continuar Comprando
              </Link>

              <div className="mt-6 p-4 bg-blue-50 rounded-lg">
                <p className="text-sm text-gray-700">
                  <span className="font-semibold">💡 Tip:</span> Los productos digitales 
                  estarán disponibles inmediatamente después del pago.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Cart;