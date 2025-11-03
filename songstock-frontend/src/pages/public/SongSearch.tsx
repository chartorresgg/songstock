// src/pages/public/SongSearch.tsx
import { useState } from 'react';
import { Search, Music, Disc3, Loader2, ShoppingCart, Info } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../../contexts/CartContext';
import songService, { Song, VinylAvailability } from '../../services/song.service';
import toast from 'react-hot-toast';

const SongSearch = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [songs, setSongs] = useState<Song[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedSong, setSelectedSong] = useState<Song | null>(null);
  const navigate = useNavigate();
  const { addItem } = useCart();

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!searchQuery.trim()) return;

    setLoading(true);
    try {
      const results = await songService.searchSongs(searchQuery);
      setSongs(results);
      if (results.length === 0) {
        toast('No se encontraron canciones', { icon: 'ℹ️' });
      }
    } catch (error) {
      console.error('Error buscando canciones:', error);
      toast.error('Error al buscar canciones');
    } finally {
      setLoading(false);
    }
  };

  const handleViewVinyls = async (song: Song) => {
    try {
      const songWithVinyls = await songService.getSongWithVinyls(song.id);
      setSelectedSong(songWithVinyls);
    } catch (error) {
      console.error('Error obteniendo vinilos:', error);
      toast.error('Error al cargar vinilos disponibles');
    }
  };

  const formatDuration = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  const getConditionLabel = (condition: string) => {
    const labels: Record<string, string> = {
      NEW: 'Nuevo',
      LIKE_NEW: 'Como Nuevo',
      VERY_GOOD: 'Muy Bueno',
      GOOD: 'Bueno',
      ACCEPTABLE: 'Aceptable'
    };
    return labels[condition] || condition;
  };

  const getVinylSizeLabel = (size: string) => {
    const sizes: Record<string, string> = {
      SEVEN_INCH: '7"',
      TEN_INCH: '10"',
      TWELVE_INCH: '12"'
    };
    return sizes[size] || size;
  };

  const handleAddToCart = async (vinyl: VinylAvailability) => {
    try {
      // Simular producto para carrito
      const product: any = {
        id: vinyl.productId,
        sku: vinyl.sku,
        price: vinyl.price,
        stockQuantity: vinyl.stockQuantity,
        albumTitle: selectedSong?.albumTitle,
        artistName: selectedSong?.artistName,
        productType: 'PHYSICAL',
        conditionType: vinyl.conditionType,
        vinylSize: vinyl.vinylSize
      };
      addItem(product);
      toast.success('Vinilo agregado al carrito');
    } catch (error) {
      toast.error('Error al agregar al carrito');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-6xl mx-auto px-4">
        {/* Header */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <div className="flex items-center mb-4">
            <Music className="h-8 w-8 text-primary-900 mr-3" />
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Buscar Canciones</h1>
              <p className="text-gray-600 mt-1">Encuentra tu canción favorita y descubre los vinilos disponibles</p>
            </div>
          </div>

          {/* Search Form */}
          <form onSubmit={handleSearch} className="flex gap-3">
            <div className="flex-1 relative">
              <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Busca por título, álbum o artista..."
                className="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>
            <button
              type="submit"
              disabled={loading || !searchQuery.trim()}
              className="bg-primary-900 hover:bg-primary-800 text-white px-8 py-3 rounded-lg font-medium disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
            >
              {loading ? (
                <>
                  <Loader2 className="h-5 w-5 animate-spin" />
                  Buscando...
                </>
              ) : (
                <>
                  <Search className="h-5 w-5" />
                  Buscar
                </>
              )}
            </button>
          </form>
        </div>

        {/* Results */}
        {songs.length > 0 && (
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-xl font-bold text-gray-900 mb-4">
              Resultados ({songs.length} {songs.length === 1 ? 'canción' : 'canciones'})
            </h2>
            <div className="space-y-3">
              {songs.map((song) => (
                <div
                  key={song.id}
                  className="border border-gray-200 rounded-lg p-4 hover:border-primary-300 transition-colors"
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-3 mb-2">
                        <Music className="h-5 w-5 text-primary-900" />
                        <h3 className="font-bold text-gray-900">{song.title}</h3>
                        <span className="text-sm text-gray-500">
                          {formatDuration(song.durationSeconds)}
                        </span>
                      </div>
                      <p className="text-sm text-gray-600 mb-1">
                        <span className="font-medium">Álbum:</span> {song.albumTitle}
                      </p>
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Artista:</span> {song.artistName}
                      </p>
                    </div>
                    <button
                      onClick={() => handleViewVinyls(song)}
                      className="bg-primary-900 hover:bg-primary-800 text-white px-4 py-2 rounded-lg flex items-center gap-2 text-sm font-medium"
                    >
                      <Disc3 className="h-4 w-4" />
                      Ver Vinilos
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Vinyls Modal */}
        {selectedSong && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
              <div className="sticky top-0 bg-white border-b border-gray-200 p-6">
                <div className="flex items-start justify-between">
                  <div>
                    <h2 className="text-2xl font-bold text-gray-900 mb-2">
                      {selectedSong.title}
                    </h2>
                    <p className="text-gray-600">
                      {selectedSong.albumTitle} • {selectedSong.artistName}
                    </p>
                  </div>
                  <button
                    onClick={() => setSelectedSong(null)}
                    className="text-gray-400 hover:text-gray-600"
                  >
                    <span className="text-2xl">×</span>
                  </button>
                </div>
              </div>

              <div className="p-6">
                {selectedSong.availableVinyls.length === 0 ? (
                  <div className="text-center py-12">
                    <Info className="h-16 w-16 text-gray-400 mx-auto mb-4" />
                    <h3 className="text-xl font-semibold text-gray-900 mb-2">
                      No disponible en vinilo
                    </h3>
                    <p className="text-gray-600">
                      Esta canción no está disponible actualmente en formato vinilo.
                    </p>
                  </div>
                ) : (
                  <>
                    <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
                      <p className="text-sm text-blue-800">
                        <strong>💿 {selectedSong.availableVinyls.length}</strong>{' '}
                        {selectedSong.availableVinyls.length === 1 ? 'vinilo disponible' : 'vinilos disponibles'}{' '}
                        con esta canción
                      </p>
                    </div>

                    <div className="grid gap-4">
                      {selectedSong.availableVinyls.map((vinyl) => (
                        <div
                          key={vinyl.productId}
                          className="border-2 border-gray-200 rounded-lg p-5 hover:border-primary-300 transition-all"
                        >
                          <div className="flex items-start justify-between mb-4">
                            <div className="flex-1">
                              <div className="flex items-center gap-3 mb-3">
                                <Disc3 className="h-6 w-6 text-primary-900" />
                                <h3 className="font-bold text-gray-900">
                                  {selectedSong.albumTitle}
                                </h3>
                              </div>
                              <div className="grid grid-cols-2 gap-3 text-sm">
                                <div>
                                  <span className="text-gray-500">Proveedor:</span>
                                  <p className="font-medium text-gray-900">{vinyl.providerName}</p>
                                </div>
                                <div>
                                  <span className="text-gray-500">Condición:</span>
                                  <p className="font-medium text-gray-900">
                                    {getConditionLabel(vinyl.conditionType)}
                                  </p>
                                </div>
                                <div>
                                  <span className="text-gray-500">Tamaño:</span>
                                  <p className="font-medium text-gray-900">
                                    {getVinylSizeLabel(vinyl.vinylSize)}
                                  </p>
                                </div>
                                <div>
                                  <span className="text-gray-500">Stock:</span>
                                  <p className="font-medium text-gray-900">
                                    {vinyl.stockQuantity} unidades
                                  </p>
                                </div>
                              </div>
                            </div>
                            <div className="text-right ml-4">
                              <p className="text-3xl font-bold text-primary-900 mb-3">
                                ${vinyl.price.toLocaleString()}
                              </p>
                              {vinyl.stockQuantity > 0 ? (
                                <button
                                  onClick={() => handleAddToCart(vinyl)}
                                  className="bg-primary-900 hover:bg-primary-800 text-white px-6 py-2 rounded-lg flex items-center gap-2 font-medium"
                                >
                                  <ShoppingCart className="h-4 w-4" />
                                  Agregar
                                </button>
                              ) : (
                                <button
                                  disabled
                                  className="bg-gray-300 text-gray-500 px-6 py-2 rounded-lg cursor-not-allowed"
                                >
                                  Sin Stock
                                </button>
                              )}
                              <button
                                onClick={() => navigate(`/product/${vinyl.productId}`)}
                                className="mt-2 text-sm text-primary-900 hover:underline"
                              >
                                Ver detalles completos →
                              </button>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  </>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default SongSearch;