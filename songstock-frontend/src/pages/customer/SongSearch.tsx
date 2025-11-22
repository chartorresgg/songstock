import React, { useState, useEffect } from 'react';
import songService from '../../services/song.service';
import { Song } from '../../types/api.types'; // ✅ AGREGAR ESTA LÍNEA
import { useCart } from '../../contexts/CartContext';
import { Music, Search, ShoppingCart } from 'lucide-react';
import { toast } from 'react-hot-toast';

const SongSearch: React.FC = () => {
  const [songs, setSongs] = useState<Song[]>([]);
  const [query, setQuery] = useState('');
  const [loading, setLoading] = useState(false);
  const { addSongToCart } = useCart();

  useEffect(() => {
    loadSongs();
  }, []);

  const loadSongs = async () => {
    setLoading(true);
    try {
      const data = await songService.searchAvailableSongs({});
      console.log('✅ Canciones cargadas:', data);
      setSongs(Array.isArray(data) ? data : []); // ✅ Validación defensiva
    } catch (error) {
      toast.error('Error cargando canciones');
      console.error('❌ Error:', error);
      setSongs([]); // ✅ Set empty array on error
    } finally {
      setLoading(false);
    }
  };
  
  const handleSearch = async () => {
    if (!query.trim()) {
      loadSongs();
      return;
    }

    setLoading(true);
    try {
      const data = await songService.searchAvailableSongs({ query });
      console.log('✅ Búsqueda:', data);
      setSongs(Array.isArray(data) ? data : []); // ✅ Validación defensiva
    } catch (error) {
      toast.error('Error en búsqueda');
      console.error('❌ Error:', error);
      setSongs([]); // ✅ Set empty array on error
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = (song: Song) => {
    console.log('🛒 Agregando canción al carrito:', song);
    addSongToCart(song);
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
    }).format(price);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">
            Canciones Individuales MP3
          </h1>
          <p className="text-gray-600">
            Compra solo las canciones que quieres, sin necesidad de adquirir el álbum completo
          </p>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Barra de búsqueda */}
        <div className="mb-8 bg-white p-6 rounded-lg shadow-md">
          <div className="flex gap-3">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
              <input
                type="text"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder="Buscar por título de canción..."
                className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
              />
            </div>
            <button
              onClick={handleSearch}
              className="px-8 py-3 bg-primary-900 text-white font-semibold rounded-lg hover:bg-primary-800 transition"
            >
              Buscar
            </button>
          </div>
        </div>

        {/* Lista de canciones */}
        {loading ? (
          <div className="text-center py-12">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-primary-900"></div>
            <p className="mt-4 text-gray-600">Cargando canciones...</p>
          </div>
        ) : !songs || songs.length === 0 ? (
          <div className="text-center py-12 bg-white rounded-lg shadow-md">
            <Music className="h-16 w-16 text-gray-300 mx-auto mb-4" />
            <h3 className="text-xl font-semibold text-gray-900 mb-2">
              No se encontraron canciones
            </h3>
            <p className="text-gray-600">
              Intenta con otra búsqueda o explora nuestro catálogo completo
            </p>
          </div>
        ) : (
          <div className="grid gap-4">
            {songs.map((song) => (
              <div
                key={song.id}
                className="bg-white p-6 rounded-lg shadow-md hover:shadow-lg transition"
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-4 flex-1">
                    <div className="w-16 h-16 bg-gradient-to-br from-green-100 to-blue-100 rounded-lg flex items-center justify-center flex-shrink-0">
                      <Music className="h-8 w-8 text-green-600" />
                    </div>

                    <div className="flex-1 min-w-0">
                      <h3 className="text-lg font-semibold text-gray-900 truncate">
                        {song.title}
                      </h3>
                      <p className="text-gray-600">{song.artistName}</p>
                      <div className="flex items-center space-x-2 mt-1">
                        <span className="text-sm text-gray-500">{song.albumTitle}</span>
                        <span className="text-gray-300">•</span>
                        <span className="text-xs px-2 py-1 bg-green-100 text-green-800 rounded-full">
                          {song.format}
                        </span>
                      </div>
                    </div>
                  </div>

                  <div className="flex items-center space-x-6 ml-4">
                    <div className="text-right">
                      <div className="text-2xl font-bold text-primary-900">
                        {formatPrice(song.price)}
                      </div>
                      <div className="text-xs text-gray-500">Descarga instantánea</div>
                    </div>

                    <button
                      onClick={() => handleAddToCart(song)}
                      className="flex items-center space-x-2 px-6 py-3 bg-green-600 text-white font-semibold rounded-lg hover:bg-green-700 transition"
                    >
                      <ShoppingCart className="h-5 w-5" />
                      <span>Agregar</span>
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Info adicional */}
        {songs && songs.length > 0 && (
          <div className="mt-8 bg-blue-50 p-6 rounded-lg">
            <h3 className="font-semibold text-gray-900 mb-2">
              💡 Acerca de las compras de canciones individuales
            </h3>
            <ul className="text-sm text-gray-700 space-y-1">
              <li>• Formato MP3 de alta calidad (320 kbps)</li>
              <li>• Descarga inmediata después del pago</li>
              <li>• Sin DRM - reproduce en cualquier dispositivo</li>
              <li>• Guarda en tu biblioteca personal</li>
            </ul>
          </div>
        )}
      </div>
    </div>
  );
};

export default SongSearch;