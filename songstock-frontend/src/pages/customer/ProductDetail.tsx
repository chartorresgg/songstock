import { useState, useEffect } from 'react';
import { useCart } from '../../contexts/CartContext';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { Product, ProductImage } from '../../types/product.types';

import { 
  Disc3, 
  ShoppingCart, 
  ArrowLeft, 
  Music,
  ListMusic,
  Package,
  Clock,
  HardDrive,
  Gauge,
  Weight,
  Tag,
  Store,
  Check
} from 'lucide-react';
import productService from '../../services/product.service';
import compilationService from '../../services/compilation.service';
import songService from '../../services/song.service';
import { Song } from '../../types/compilation.types';
import AlternativeFormats from '../../components/common/AlternativeFormat';
import CompilationSelector from '../../components/customer/CompilationSelector';
import toast from 'react-hot-toast';

const ProductDetail = () => {
    const getPrimaryImageUrl = (product: Product | null) => {
        if (!product) return null;
        const primaryImage = product.images?.find((img: ProductImage) => img.isPrimary);
    return primaryImage?.imageUrl || null;
  };

  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAuth();
  const { addItem } = useCart();
  const [product, setProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);
  const [quantity, setQuantity] = useState(1);
  const [songs, setSongs] = useState<Song[]>([]);
  const [loadingSongs, setLoadingSongs] = useState(false);
  const [selectedSong, setSelectedSong] = useState<Song | null>(null);
  const [showCompilationSelector, setShowCompilationSelector] = useState(false);
  const [tracklist, setTracklist] = useState<Song[]>([]);
  const [activeTab, setActiveTab] = useState<'details' | 'tracklist'>('details');

  useEffect(() => {
    if (id) {
      loadProduct(parseInt(id));
    }
  }, [id]);

  const loadProduct = async (productId: number) => {
    setLoading(true);
    try {
      const data = await productService.getProductById(productId);
      setProduct(data);
      
      // Cargar canciones según tipo
      if (data.productType === 'DIGITAL') {
        loadSongs(data.albumId);
      } else if (data.productType === 'PHYSICAL') {
        // Cargar tracklist para productos físicos
        try {
          const tracklistData = await songService.getProductTracklist(productId);
          setTracklist(tracklistData);
        } catch (error) {
          console.error('Error loading tracklist:', error);
          // No mostrar error, solo no mostrar el tab
        }
      }
    } catch (error) {
      console.error('Error loading product:', error);
      toast.error('Error al cargar el producto');
      navigate('/catalog');
    } finally {
      setLoading(false);
    }
  };

  const loadSongs = async (albumId: number) => {
    try {
      setLoadingSongs(true);
      const data = await compilationService.getSongsByAlbum(albumId);
      setSongs(data);
    } catch (error) {
      console.error('Error loading songs:', error);
    } finally {
      setLoadingSongs(false);
    }
  };

  const handleAddToCompilation = (song: Song) => {
    if (!isAuthenticated) {
      toast.error('Debes iniciar sesión para crear recopilaciones');
      navigate('/login');
      return;
    }
    
    if (user?.role !== 'CUSTOMER') {
      toast.error('Solo los clientes pueden crear recopilaciones');
      return;
    }
    
    setSelectedSong(song);
    setShowCompilationSelector(true);
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
    }).format(price);
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
      SEVEN_INCH: '7 pulgadas',
      TEN_INCH: '10 pulgadas',
      TWELVE_INCH: '12 pulgadas',
    };
    return sizes[size] || size;
  };

  const getVinylSpeedLabel = (speed?: string | null) => {
    if (!speed) return '';
    return speed.replace('RPM_', '') + ' RPM';
  };

  const handleAddToCart = () => {
    if (!product) return;
    addItem(product, quantity);
  };

  const handleBuyNow = () => {
    if (!product) return;
    addItem(product, quantity);
    navigate('/cart');
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-900"></div>
      </div>
    );
  }

  if (!product) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Producto no encontrado</h2>
          <Link to="/catalog" className="text-primary-900 hover:underline">
            Volver al catálogo
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Breadcrumb */}
      <div className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex items-center space-x-2 text-sm">
            <Link to="/" className="text-gray-500 hover:text-primary-900">Inicio</Link>
            <span className="text-gray-400">/</span>
            <Link to="/catalog" className="text-gray-500 hover:text-primary-900">Catálogo</Link>
            <span className="text-gray-400">/</span>
            <span className="text-gray-900 font-medium">{product.albumTitle}</span>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Back Button */}
        <button
          onClick={() => navigate(-1)}
          className="flex items-center text-gray-600 hover:text-primary-900 mb-6 transition"
        >
          <ArrowLeft className="h-5 w-5 mr-2" />
          Volver
        </button>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-12">
          {/* Image Section */}
          <div className="bg-white rounded-lg shadow-lg p-8">
            <div className="aspect-square bg-gradient-to-br from-primary-100 to-secondary-100 rounded-lg flex items-center justify-center relative overflow-hidden">
            {getPrimaryImageUrl(product) ? (
                <img
                src={getPrimaryImageUrl(product)!}
                  alt={product.albumTitle}
                  className="w-full h-full object-cover"
                   loading="lazy"
                />
              ) : (
                <Disc3 className="h-48 w-48 text-primary-300" />
              )}

              {/* Badge de tipo */}
              <div className="absolute top-4 right-4">
                <span className={`flex items-center space-x-1 px-4 py-2 rounded-full text-sm font-semibold ${
                  product.productType === 'PHYSICAL'
                    ? 'bg-primary-900 text-white'
                    : 'bg-secondary-500 text-white'
                }`}>
                  {product.productType === 'PHYSICAL' ? (
                    <><Disc3 className="h-5 w-5" /><span>Vinilo Físico</span></>
                  ) : (
                    <><Music className="h-5 w-5" /><span>Digital</span></>
                  )}
                </span>
              </div>

              {/* Badge de stock */}
              {product.stockQuantity === 0 && (
                <div className="absolute inset-0 bg-black bg-opacity-60 flex items-center justify-center">
                  <span className="bg-red-600 text-white px-6 py-3 rounded-lg font-bold text-lg">
                    AGOTADO
                  </span>
                </div>
              )}
            </div>
          </div>

          {/* Info Section */}
          <div className="space-y-6">
            {/* Tabs - Solo mostrar si hay tracklist */}
            {product.productType === 'PHYSICAL' && tracklist.length > 0 && (
              <div className="flex border-b border-gray-200">
                <button
                  onClick={() => setActiveTab('details')}
                  className={`px-6 py-3 font-medium ${activeTab === 'details' ? 'border-b-2 border-primary-900 text-primary-900' : 'text-gray-500'}`}
                >
                  Detalles
                </button>
                <button
                  onClick={() => setActiveTab('tracklist')}
                  className={`px-6 py-3 font-medium flex items-center gap-2 ${activeTab === 'tracklist' ? 'border-b-2 border-primary-900 text-primary-900' : 'text-gray-500'}`}
                >
                  <ListMusic className="h-4 w-4" />
                  Tracklist ({tracklist.length})
                </button>
              </div>
            )}

            {activeTab === 'details' ? (
              <div>
                <h1 className="text-4xl font-bold text-gray-900 mb-2">
                  {product.albumTitle}
                </h1>
                <p className="text-2xl text-gray-600 mb-4">
                  {product.artistName}
                </p>

                {/* Price */}
                <div className="flex items-baseline space-x-4 mb-6">
                  <span className="text-4xl font-bold text-primary-900">
                    {formatPrice(product.price)}
                  </span>
                  {product.stockQuantity > 0 && product.stockQuantity <= 10 && (
                    <span className="text-sm text-red-600 font-medium">
                      ¡Solo quedan {product.stockQuantity}!
                    </span>
                  )}
                </div>

                {/* Category */}
                <div className="flex items-center space-x-2 mb-4">
                  <Tag className="h-5 w-5 text-gray-500" />
                  <span className="text-gray-700">{product.categoryName}</span>
                </div>

                {/* Provider */}
                <div className="flex items-center space-x-2 mb-6">
                  <Store className="h-5 w-5 text-gray-500" />
                  <span className="text-gray-700">Vendido por: <span className="font-medium">{product.providerName}</span></span>
                </div>

                {/* Specifications */}
                <div className="bg-gray-50 rounded-lg p-6">
                  <h3 className="text-lg font-semibold text-gray-900 mb-4">Especificaciones</h3>
                  
                  <div className="space-y-3">
                    {/* Condición */}
                    {product.conditionType && (
                      <div className="flex items-center justify-between">
                        <span className="text-gray-600 flex items-center">
                          <Check className="h-4 w-4 mr-2" />
                          Condición:
                        </span>
                        <span className="font-medium text-gray-900">
                          {getConditionLabel(product.conditionType)}
                        </span>
                      </div>
                    )}

                    {/* Stock */}
                    <div className="flex items-center justify-between">
                      <span className="text-gray-600 flex items-center">
                        <Package className="h-4 w-4 mr-2" />
                        Disponibilidad:
                      </span>
                      <span className={`font-medium ${product.inStock ? 'text-green-600' : 'text-red-600'}`}>
                        {product.inStock ? `${product.stockQuantity} en stock` : 'Agotado'}
                      </span>
                    </div>

                    {/* Physical Specs */}
                    {product.productType === 'PHYSICAL' && (
                      <>
                        {product.vinylSize && (
                          <div className="flex items-center justify-between">
                            <span className="text-gray-600 flex items-center">
                              <Disc3 className="h-4 w-4 mr-2" />
                              Tamaño:
                            </span>
                            <span className="font-medium text-gray-900">
                              {getVinylSizeLabel(product.vinylSize)}
                            </span>
                          </div>
                        )}

                        {product.vinylSpeed && (
                          <div className="flex items-center justify-between">
                            <span className="text-gray-600 flex items-center">
                              <Gauge className="h-4 w-4 mr-2" />
                              Velocidad:
                            </span>
                            <span className="font-medium text-gray-900">
                              {getVinylSpeedLabel(product.vinylSpeed)}
                            </span>
                          </div>
                        )}

                        {product.weightGrams && (
                          <div className="flex items-center justify-between">
                            <span className="text-gray-600 flex items-center">
                              <Weight className="h-4 w-4 mr-2" />
                              Peso:
                            </span>
                            <span className="font-medium text-gray-900">
                              {product.weightGrams}g
                            </span>
                          </div>
                        )}
                      </>
                    )}

                    {/* Digital Specs */}
                    {product.productType === 'DIGITAL' && (
                      <>
                        {product.fileFormat && (
                          <div className="flex items-center justify-between">
                            <span className="text-gray-600 flex items-center">
                              <Music className="h-4 w-4 mr-2" />
                              Formato:
                            </span>
                            <span className="font-medium text-gray-900">
                              {product.fileFormat}
                            </span>
                          </div>
                        )}

                        {product.fileSizeMb && (
                          <div className="flex items-center justify-between">
                            <span className="text-gray-600 flex items-center">
                              <HardDrive className="h-4 w-4 mr-2" />
                              Tamaño:
                            </span>
                            <span className="font-medium text-gray-900">
                              {product.fileSizeMb} MB
                            </span>
                          </div>
                        )}

                        <div className="flex items-center justify-between">
                          <span className="text-gray-600 flex items-center">
                            <Clock className="h-4 w-4 mr-2" />
                            Descarga:
                          </span>
                          <span className="font-medium text-green-600">
                            Inmediata
                          </span>
                        </div>
                      </>
                    )}

                    {/* SKU */}
                    <div className="flex items-center justify-between pt-3 border-t">
                      <span className="text-gray-600">SKU:</span>
                      <span className="font-mono text-sm text-gray-900">
                        {product.sku}
                      </span>
                    </div>
                  </div>
                </div>

                {/* Quantity Selector (only for in-stock items) */}
                {product.inStock && (
                  <div className="flex items-center space-x-4">
                    <label className="text-gray-700 font-medium">Cantidad:</label>
                    <div className="flex items-center border border-gray-300 rounded-lg">
                      <button
                        onClick={() => setQuantity(Math.max(1, quantity - 1))}
                        className="px-4 py-2 hover:bg-gray-100 transition"
                      >
                        -
                      </button>
                      <span className="px-6 py-2 border-x border-gray-300 font-medium">
                        {quantity}
                      </span>
                      <button
                        onClick={() => setQuantity(Math.min(product.stockQuantity, quantity + 1))}
                        className="px-4 py-2 hover:bg-gray-100 transition"
                      >
                        +
                      </button>
                    </div>
                  </div>
                )}

                {/* Action Buttons */}
                <div className="space-y-3">
                  {product.inStock ? (
                    <>
                      <button
                        onClick={handleBuyNow}
                        className="w-full bg-primary-900 text-white py-4 rounded-lg font-semibold hover:bg-primary-800 transition text-lg"
                      >
                        Comprar Ahora
                      </button>
                      <button
                        onClick={handleAddToCart}
                        className="w-full bg-secondary-500 text-white py-4 rounded-lg font-semibold hover:bg-secondary-600 transition text-lg flex items-center justify-center space-x-2"
                      >
                        <ShoppingCart className="h-6 w-6" />
                        <span>Agregar al Carrito</span>
                      </button>
                    </>
                  ) : (
                    <button
                      disabled
                      className="w-full bg-gray-300 text-gray-600 py-4 rounded-lg font-semibold cursor-not-allowed text-lg"
                    >
                      Producto Agotado
                    </button>
                  )}
                </div>

                {/* Additional Info */}
                <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                  <div className="flex items-start space-x-3">
                    <Package className="h-5 w-5 text-blue-600 mt-0.5" />
                    <div>
                      <h4 className="font-medium text-gray-900 mb-1">Información de Envío</h4>
                      <p className="text-sm text-gray-600">
                        {product.productType === 'PHYSICAL' 
                          ? 'Envío a todo el país. El proveedor coordinará la entrega después de confirmar tu pedido.'
                          : 'Descarga digital inmediata después de la compra.'}
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            ) : (
              <div className="space-y-3">
                <h2 className="text-xl font-bold text-gray-900 mb-4">Lista de Canciones</h2>
                {tracklist.map((song, index) => (
                  <div
                    key={song.id}
                    className="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition"
                  >
                    <div className="flex items-center gap-4">
                      <span className="text-lg font-bold text-gray-400 w-8">
                        {song.trackNumber || index + 1}
                      </span>
                      <div>
                        <p className="font-medium text-gray-900">{song.title}</p>
                        <p className="text-sm text-gray-500">{song.artistName}</p>
                      </div>
                    </div>
                    {song.durationSeconds && (
                      <span className="text-sm text-gray-500">
                        {Math.floor(song.durationSeconds / 60)}:{(song.durationSeconds % 60).toString().padStart(2, '0')}
                      </span>
                    )}
                  </div>
                ))}
                <div className="mt-4 p-4 bg-blue-50 rounded-lg">
                  <p className="text-sm text-blue-800">
                    <strong>Duración total:</strong> {Math.floor(tracklist.reduce((acc, s) => acc + (s.durationSeconds || 0), 0) / 60)} minutos
                  </p>
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Songs List for Digital Products */}
        {product.productType === 'DIGITAL' && (
          <div className="bg-white rounded-lg shadow-lg p-6 mb-12">
            <h2 className="text-xl font-bold text-gray-900 mb-4 flex items-center">
              <Music className="h-5 w-5 mr-2 text-primary-900" />
              Lista de Canciones
            </h2>
            
            {loadingSongs ? (
              <div className="flex justify-center py-8">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-900"></div>
              </div>
            ) : songs.length > 0 ? (
              <div className="space-y-2">
                {songs.map((song) => (
                  <div
                    key={song.id}
                    className="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg transition group"
                  >
                    <div className="flex items-center space-x-3 flex-1">
                      <span className="text-sm font-medium text-gray-500 w-6">
                        {song.trackNumber}
                      </span>
                      <div className="flex-1">
                        <p className="font-medium text-gray-900">{song.title}</p>
                        {song.durationSeconds && (
                          <p className="text-xs text-gray-500">
                            {Math.floor(song.durationSeconds / 60)}:{(song.durationSeconds % 60).toString().padStart(2, '0')}
                          </p>
                        )}
                      </div>
                    </div>
                    {isAuthenticated && user?.role === 'CUSTOMER' && (
                      <button
                        onClick={() => handleAddToCompilation(song)}
                        className="opacity-0 group-hover:opacity-100 transition-opacity flex items-center space-x-1 text-primary-900 hover:text-primary-800 text-sm font-medium px-3 py-1.5 rounded-md hover:bg-primary-50"
                      >
                        <ListMusic className="h-4 w-4" />
                        <span>Añadir a recopilación</span>
                      </button>
                    )}
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-500 text-center py-4">
                No hay canciones disponibles para este álbum
              </p>
            )}
          </div>
        )}

        {/* Alternative Formats Section */}
        <div className="mb-12">
          <AlternativeFormats currentProduct={product} />
        </div>
      </div>

      {/* Compilation Selector Modal */}
      {showCompilationSelector && selectedSong && (
        <CompilationSelector
          song={selectedSong}
          onClose={() => {
            setShowCompilationSelector(false);
            setSelectedSong(null);
          }}
        />
      )}
    
    </div>
    
  );
};

export default ProductDetail;