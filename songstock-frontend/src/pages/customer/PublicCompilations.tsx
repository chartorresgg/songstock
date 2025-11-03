// src/pages/Compilations/PublicCompilations.tsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Music2, Search, Filter, Loader2, Users } from 'lucide-react';
import compilationService from '../../services/compilation.service';
import { Compilation } from '../../types/compilation.types';
import toast from 'react-hot-toast';

const PublicCompilations = () => {
  const navigate = useNavigate();
  const [compilations, setCompilations] = useState<Compilation[]>([]);
  const [filteredCompilations, setFilteredCompilations] = useState<Compilation[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [minSongs, setMinSongs] = useState<string>('');
  const [maxSongs, setMaxSongs] = useState<string>('');
  const [showFilters, setShowFilters] = useState(false);

  useEffect(() => {
    loadPublicCompilations();
  }, []);

  useEffect(() => {
    applyFilters();
  }, [searchTerm, minSongs, maxSongs, compilations]);

  const loadPublicCompilations = async () => {
    try {
      setLoading(true);
      const data = await compilationService.getPublicCompilations();
      setCompilations(data);
      setFilteredCompilations(data);
    } catch (error) {
      console.error('Error loading public compilations:', error);
      toast.error('Error al cargar recopilaciones públicas');
    } finally {
      setLoading(false);
    }
  };

  const applyFilters = () => {
    let filtered = [...compilations];

    // Filtro por nombre
    if (searchTerm.trim()) {
      filtered = filtered.filter(c => 
        c.name.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // Filtro por cantidad de canciones
    if (minSongs) {
      const min = parseInt(minSongs);
      filtered = filtered.filter(c => (c.songCount || 0) >= min);
    }

    if (maxSongs) {
      const max = parseInt(maxSongs);
      filtered = filtered.filter(c => (c.songCount || 0) <= max);
    }

    setFilteredCompilations(filtered);
  };

  const handleClearFilters = () => {
    setSearchTerm('');
    setMinSongs('');
    setMaxSongs('');
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('es-CO', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <Loader2 className="h-8 w-8 animate-spin text-primary-900" />
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="mb-8">
        <div className="flex items-center space-x-3 mb-2">
          <Users className="h-8 w-8 text-primary-900" />
          <h1 className="text-3xl font-bold text-gray-900">Explorar Recopilaciones</h1>
        </div>
        <p className="text-gray-600">Descubre recopilaciones públicas de otros usuarios</p>
      </div>

      {/* Búsqueda y Filtros */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
        <div className="space-y-4">
          {/* Búsqueda */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar por nombre..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-900 focus:border-transparent"
            />
          </div>

          {/* Toggle Filtros */}
          <button
            onClick={() => setShowFilters(!showFilters)}
            className="flex items-center space-x-2 text-primary-900 hover:text-primary-700 transition"
          >
            <Filter className="h-5 w-5" />
            <span className="font-medium">{showFilters ? 'Ocultar filtros' : 'Mostrar filtros'}</span>
          </button>

          {/* Filtros Avanzados */}
          {showFilters && (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-4 border-t">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Mínimo de canciones
                </label>
                <input
                  type="number"
                  min="0"
                  placeholder="Ej: 5"
                  value={minSongs}
                  onChange={(e) => setMinSongs(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-900 focus:border-transparent"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Máximo de canciones
                </label>
                <input
                  type="number"
                  min="0"
                  placeholder="Ej: 20"
                  value={maxSongs}
                  onChange={(e) => setMaxSongs(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-900 focus:border-transparent"
                />
              </div>

              <div className="md:col-span-2">
                <button
                  onClick={handleClearFilters}
                  className="text-sm text-gray-600 hover:text-gray-900 underline"
                >
                  Limpiar filtros
                </button>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Resultados */}
      <div className="mb-4 text-sm text-gray-600">
        {filteredCompilations.length} recopilación(es) encontrada(s)
      </div>

      {/* Lista de Compilaciones */}
      {filteredCompilations.length === 0 ? (
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
          <Music2 className="h-16 w-16 text-gray-300 mx-auto mb-4" />
          <h3 className="text-lg font-medium text-gray-900 mb-2">No se encontraron recopilaciones</h3>
          <p className="text-gray-600">Intenta ajustar los filtros de búsqueda</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredCompilations.map((compilation) => (
            <div
              key={compilation.id}
              onClick={() => navigate(`/compilations/public/${compilation.id}`)}
              className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 hover:shadow-md hover:border-primary-900 transition cursor-pointer"
            >
              <div className="flex items-start justify-between mb-3">
                <Music2 className="h-6 w-6 text-primary-900 flex-shrink-0" />
                <span className="text-xs bg-green-100 text-green-800 px-2 py-1 rounded">
                  Pública
                </span>
              </div>

              <h3 className="text-lg font-bold text-gray-900 mb-2 line-clamp-2">
                {compilation.name}
              </h3>

              {compilation.description && (
                <p className="text-sm text-gray-600 mb-3 line-clamp-2">
                  {compilation.description}
                </p>
              )}

              <div className="space-y-2 text-sm text-gray-500">
                <div className="flex items-center justify-between">
                  <span>Creada por:</span>
                  <span className="font-medium text-gray-900">
                    {compilation.creatorUsername || 'Usuario'}
                  </span>
                </div>

                <div className="flex items-center justify-between">
                  <span>Canciones:</span>
                  <span className="font-medium text-gray-900">
                    {compilation.songCount || 0}
                  </span>
                </div>

                {compilation.createdAt && (
                  <div className="flex items-center justify-between">
                    <span>Creada:</span>
                    <span className="font-medium text-gray-900">
                      {formatDate(compilation.createdAt)}
                    </span>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default PublicCompilations;