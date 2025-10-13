import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Disc3, Music, ChevronRight, Package, AlertCircle } from 'lucide-react';
import { Product } from '../../types/product.types';
import productService from '../../services/product.service';
import { useCart } from '../../contexts/CartContext';
import toast from 'react-hot-toast';

interface AlternativeFormatsProps {
  currentProduct: Product;
}

const AlternativeFormats = ({ currentProduct }: AlternativeFormatsProps) => {
  const [alternativeFormats, setAlternativeFormats] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const { addItem } = useCart();

  useEffect(() => {
    loadAlternativeFormats();
  }, [currentProduct.id]);

  const loadAlternativeFormats = async () => {
    setLoading(true);
    try {
      const alternatives = await productService.getAlternativeFormats(currentProduct.id);
      setAlternativeFormats(alternatives);
    } catch (error) {
      console.error('Error loading alternative formats:', error);
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

  const getFormatLabel = (type: string) => {
    return type === 'PHYSICAL' ? 'Vinilo Físico' : 'Digital MP3';
  };

  const getFormatIcon = (type: string) => {
    return type === 'PHYSICAL' ? Disc3 : Music;
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

  const getVinylSpecs = (product: Product) => {
    const specs = [];
    if (product.vinylSize) {
      const sizes: Record<string, string> = {
        SEVEN_INCH: '7"',
        TEN_INCH: '10"',
        TWELVE_INCH: '12"',
      };
      specs.push(sizes[product.vinylSize] || product.vinylSize);
    }
    if (product.vinylSpeed) {
      specs.push(product.vinylSpeed.replace('RPM_', '') + ' RPM');
    }
    return specs.join(' • ');
  };

  const handleAddToCart = (product: Product) => {
    addItem(product);
    toast.success('Producto agregado al carrito');
  };

  // Si no hay formatos alternativos, no mostrar nada
  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="animate-pulse space-y-4">
          <div className="h-6 bg-gray-200 rounded w-1/3"></div>
          <div className="h-20 bg-gray-200 rounded"></div>
        </div>
      </div>
    );
  }

  if (alternativeFormats.length === 0) {
    return null;
  }

  return (
    <div className="bg-gradient-to-br from-blue-50 to-indigo-50 rounded-lg shadow-md p-6 border-2 border-blue-200">
      {/* Header */}
      <div className="flex items-start justify-between mb-6">
        <div>
          <h3 className="text-xl font-bold text-gray-900 mb-2 flex items-center">
            <Package className="h-6 w-6 mr-2 text-blue-600" />
            Formatos Alternativos Disponibles
          </h3>
          <p className="text-sm text-gray-600">
            Este álbum está disponible en {currentProduct.productType === 'PHYSICAL' ? 'formato digital' : 'formato físico'}
          </p>
        </div>
      </div>

      {/* Alert informativo */}
      <div className="bg-blue-100 border border-blue-300 rounded-lg p-4 mb-6 flex items-start">
        <AlertCircle className="h-5 w-5 text-blue-600 mr-3 flex-shrink-0 mt-0.5" />
        <div className="text-sm text-blue-800">
          <p className="font-medium mb-1">💡 Tienes opciones</p>
          <p>
            {currentProduct.productType === 'PHYSICAL' 
              ? 'Si prefieres descarga instantánea, también tenemos este álbum en formato digital.'
              : 'Si eres coleccionista, también tenemos este álbum en vinilo físico.'}
          </p>
        </div>
      </div>

      {/* Lista de formatos alternativos */}
      <div className="space-y-4">
        {alternativeFormats.map((product) => {
          const FormatIcon = getFormatIcon(product.productType);
          
          return (
            <div 
              key={product.id}
              className="bg-white rounded-lg border-2 border-gray-200 hover:border-blue-400 transition-all duration-200 overflow-hidden"
            >
              <div className="p-5">
                <div className="flex items-start justify-between mb-4">
                  <div className="flex items-start space-x-4 flex-1">
                    {/* Icono del formato */}
                    <div className={`w-14 h-14 rounded-lg flex items-center justify-center flex-shrink-0 ${
                      product.productType === 'PHYSICAL' 
                        ? 'bg-primary-100' 
                        : 'bg-secondary-100'
                    }`}>
                      <FormatIcon className={`h-8 w-8 ${
                        product.productType === 'PHYSICAL' 
                          ? 'text-primary-900' 
                          : 'text-secondary-600'
                      }`} />
                    </div>

                    {/* Información del producto */}
                    <div className="flex-1">
                      <h4 className="text-lg font-bold text-gray-900 mb-1">
                        {getFormatLabel(product.productType)}
                      </h4>
                      
                      {/* Especificaciones según el tipo */}
                      {product.productType === 'PHYSICAL' ? (
                        <div className="space-y-1">
                          {product.conditionType && (
                            <p className="text-sm text-gray-600">
                              Condición: <span className="font-medium text-gray-900">
                                {getConditionLabel(product.conditionType)}
                              </span>
                            </p>
                          )}
                          {getVinylSpecs(product) && (
                            <p className="text-sm text-gray-600">
                              Especificaciones: <span className="font-medium text-gray-900">
                                {getVinylSpecs(product)}
                              </span>
                            </p>
                          )}
                        </div>
                      ) : (
                        <div className="space-y-1">
                          {product.fileFormat && (
                            <p className="text-sm text-gray-600">
                              Formato: <span className="font-medium text-gray-900">
                                {product.fileFormat}
                              </span>
                            </p>
                          )}
                          <p className="text-sm text-gray-600">
                            Descarga: <span className="font-medium text-green-600">
                              Inmediata
                            </span>
                          </p>
                        </div>
                      )}

                      {/* Stock */}
                      <div className="mt-2">
                        {product.stockQuantity > 0 ? (
                          <span className="inline-flex items-center text-sm text-green-600 font-medium">
                            ✓ Disponible ({product.stockQuantity} en stock)
                          </span>
                        ) : (
                          <span className="inline-flex items-center text-sm text-red-600 font-medium">
                            ✗ Agotado
                          </span>
                        )}
                      </div>
                    </div>
                  </div>

                  {/* Precio y acciones */}
                  <div className="text-right ml-4">
                    <p className="text-2xl font-bold text-primary-900 mb-3">
                      {formatPrice(product.price)}
                    </p>
                    
                    {product.stockQuantity > 0 ? (
                      <div className="space-y-2">
                        <Link
                          to={`/product/${product.id}`}
                          className="inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition text-sm font-medium"
                        >
                          Ver detalles
                          <ChevronRight className="h-4 w-4 ml-1" />
                        </Link>
                        <button
                          onClick={() => handleAddToCart(product)}
                          className="w-full px-4 py-2 bg-primary-900 text-white rounded-lg hover:bg-primary-800 transition text-sm font-semibold"
                        >
                          Agregar al carrito
                        </button>
                      </div>
                    ) : (
                      <button
                        disabled
                        className="px-4 py-2 bg-gray-200 text-gray-500 rounded-lg cursor-not-allowed text-sm font-medium"
                      >
                        No disponible
                      </button>
                    )}
                  </div>
                </div>

                {/* Comparación de ventajas */}
                <div className="mt-4 pt-4 border-t border-gray-200">
                  <div className="grid grid-cols-2 gap-4 text-xs">
                    {product.productType === 'PHYSICAL' ? (
                      <>
                        <div className="flex items-center text-gray-600">
                          <span className="text-green-500 mr-2">✓</span>
                          Experiencia tangible
                        </div>
                        <div className="flex items-center text-gray-600">
                          <span className="text-green-500 mr-2">✓</span>
                          Objeto de colección
                        </div>
                        <div className="flex items-center text-gray-600">
                          <span className="text-green-500 mr-2">✓</span>
                          Calidad de audio analógico
                        </div>
                        <div className="flex items-center text-gray-600">
                          <span className="text-green-500 mr-2">✓</span>
                          Arte de carátula grande
                        </div>
                      </>
                    ) : (
                      <>
                        <div className="flex items-center text-gray-600">
                          <span className="text-green-500 mr-2">✓</span>
                          Descarga instantánea
                        </div>
                        <div className="flex items-center text-gray-600">
                          <span className="text-green-500 mr-2">✓</span>
                          Sin esperar envío
                        </div>
                        <div className="flex items-center text-gray-600">
                          <span className="text-green-500 mr-2">✓</span>
                          Portable en dispositivos
                        </div>
                        <div className="flex items-center text-gray-600">
                          <span className="text-green-500 mr-2">✓</span>
                          Precio más accesible
                        </div>
                      </>
                    )}
                  </div>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Footer con información adicional */}
      <div className="mt-6 text-center">
        <p className="text-sm text-gray-600">
          💿 Puedes comprar ambos formatos si lo deseas. Cada uno se agregará por separado a tu carrito.
        </p>
      </div>
    </div>
  );
};

export default AlternativeFormats;