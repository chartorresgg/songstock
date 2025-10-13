import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Product } from '../../types/product.types';
import { useCart } from '../../contexts/CartContext';
import { ShoppingCart, Disc3, Music, Repeat } from 'lucide-react';
import productService from '../../services/product.service';

interface ProductCardProps {
  product: Product;
  onAddToCart?: (product: Product) => void;
}

const ProductCard = ({ product }: ProductCardProps) => {
  const { addItem } = useCart();
  const [hasAlternative, setHasAlternative] = useState(false);
  const [checkingAlternative, setCheckingAlternative] = useState(false);
  
  useEffect(() => {
    checkForAlternativeFormats();
  }, [product.id]);

  const checkForAlternativeFormats = async () => {
    setCheckingAlternative(true);
    try {
      const hasAlt = await productService.hasAlternativeFormat(product.id);
      setHasAlternative(hasAlt);
    } catch (error) {
      console.error('Error checking alternative formats:', error);
    } finally {
      setCheckingAlternative(false);
    }
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
    }).format(price);
  };

  const getProductTypeLabel = (type: string) => {
    return type === 'PHYSICAL' ? 'Vinilo' : 'Digital';
  };

  const getProductTypeIcon = (type: string) => {
    return type === 'PHYSICAL' ? (
      <Disc3 className="h-4 w-4" />
    ) : (
      <Music className="h-4 w-4" />
    );
  };

  const getConditionLabel = (condition?: string) => {
    const conditions: Record<string, string> = {
      NEW: 'Nuevo',
      LIKE_NEW: 'Como Nuevo',
      VERY_GOOD: 'Muy Bueno',
      GOOD: 'Bueno',
      ACCEPTABLE: 'Aceptable',
    };
    return condition ? conditions[condition] : '';
  };

  const getVinylSizeLabel = (size?: string | null) => {
    if (!size) return '';
    const sizes: Record<string, string> = {
      SEVEN_INCH: '7"',
      TEN_INCH: '10"',
      TWELVE_INCH: '12"',
    };
    return sizes[size] || size;
  };

  const getVinylSpeedLabel = (speed?: string | null) => {
    if (!speed) return '';
    return speed.replace('RPM_', '') + ' RPM';
  };

  return (
    <div className="group bg-white rounded-lg shadow-md hover:shadow-xl transition-shadow duration-300 overflow-hidden">
      {/* Image */}
      <Link to={`/product/${product.id}`} className="block relative overflow-hidden">
        <div className="aspect-square bg-gradient-to-br from-primary-100 to-secondary-100 flex items-center justify-center">
          {product.images ? (
            <img
              src={product.images}
              alt={product.albumTitle}
              className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
            />
          ) : (
            <Disc3 className="h-24 w-24 text-primary-300 group-hover:rotate-180 transition-transform duration-500" />
          )}
        </div>

        {/* Badge de tipo de producto */}
        <div className="absolute top-3 right-3">
          <span className={`flex items-center space-x-1 px-3 py-1 rounded-full text-xs font-semibold ${
            product.productType === 'PHYSICAL'
              ? 'bg-primary-900 text-white'
              : 'bg-secondary-500 text-white'
          }`}>
            {getProductTypeIcon(product.productType)}
            <span>{getProductTypeLabel(product.productType)}</span>
          </span>
        </div>

        {/* ==================== NUEVO: Badge de formato alternativo disponible ==================== */}
        {hasAlternative && !checkingAlternative && (
          <div className="absolute top-3 left-3">
            <span className="flex items-center space-x-1 px-3 py-1 rounded-full text-xs font-semibold bg-gradient-to-r from-blue-500 to-indigo-600 text-white shadow-lg animate-pulse">
              <Repeat className="h-3 w-3" />
              <span>Otro formato</span>
            </span>
          </div>
        )}

        {/* Badge de stock bajo */}
        {product.stockQuantity > 0 && product.stockQuantity <= 5 && (
          <div className="absolute bottom-3 left-3">
            <span className="bg-red-500 text-white px-2 py-1 rounded-full text-xs font-semibold">
              ¡Solo {product.stockQuantity}!
            </span>
          </div>
        )}

        {/* Badge sin stock */}
        {product.stockQuantity === 0 && (
          <div className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center">
            <span className="bg-red-600 text-white px-4 py-2 rounded-lg font-bold">
              AGOTADO
            </span>
          </div>
        )}
      </Link>

      {/* Content */}
      <div className="p-4">
        <Link to={`/product/${product.id}`}>
          <h3 className="font-semibold text-gray-900 mb-1 line-clamp-2 group-hover:text-primary-900 transition">
            {product.albumTitle}
          </h3>
        </Link>

        <p className="text-sm text-gray-600 mb-2">
          {product.artistName}
        </p>

        {/* Condición para vinilos físicos */}
        {product.productType === 'PHYSICAL' && product.conditionType && (
          <div className="mb-2">
            <span className="text-xs text-gray-500">
              Condición: <span className="font-medium text-gray-700">{getConditionLabel(product.conditionType)}</span>
            </span>
          </div>
        )}

        {/* Detalles técnicos de vinilo */}
        {product.productType === 'PHYSICAL' && (
          <div className="flex items-center space-x-3 text-xs text-gray-500 mb-3">
            {product.vinylSize && (
              <span className="flex items-center">
                <span className="font-medium">{getVinylSizeLabel(product.vinylSize)}</span>
              </span>
            )}
            {product.vinylSpeed && (
              <span className="flex items-center">
                <span className="font-medium">{getVinylSpeedLabel(product.vinylSpeed)}</span>
              </span>
            )}
          </div>
        )}

        {/* Detalles de formato digital */}
        {product.productType === 'DIGITAL' && product.fileFormat && (
          <div className="mb-2">
            <span className="text-xs text-gray-500">
              Formato: <span className="font-medium text-gray-700">{product.fileFormat}</span>
            </span>
          </div>
        )}

        {/* ==================== NUEVO: Indicador de formato alternativo ==================== */}
        {hasAlternative && !checkingAlternative && (
          <div className="mb-3 p-2 bg-blue-50 border border-blue-200 rounded-md">
            <p className="text-xs text-blue-700 font-medium flex items-center">
              <Repeat className="h-3 w-3 mr-1" />
              También disponible en {product.productType === 'PHYSICAL' ? 'digital' : 'vinilo'}
            </p>
          </div>
        )}

        {/* Price and Actions */}
        <div className="flex items-center justify-between pt-3 border-t">
          <div>
            <p className="text-xl font-bold text-primary-900">
              {formatPrice(product.price)}
            </p>
            {product.stockQuantity > 0 && (
              <p className="text-xs text-gray-500">
                {product.stockQuantity} disponible{product.stockQuantity !== 1 ? 's' : ''}
              </p>
            )}
          </div>

          {product.stockQuantity > 0 ? (
            <button
              onClick={() => addItem(product)}
              className="bg-primary-900 text-white p-2 rounded-lg hover:bg-primary-800 transition"
              title="Agregar al carrito"
            >
              <ShoppingCart className="h-5 w-5" />
            </button>
          ) : (
            <button
              disabled
              className="bg-gray-300 text-gray-500 p-2 rounded-lg cursor-not-allowed"
              title="No disponible"
            >
              <ShoppingCart className="h-5 w-5" />
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default ProductCard;