import { useState, useEffect } from 'react';
import { Filter, X } from 'lucide-react';
import catalogService, { Genre, Artist } from '../../services/catalog.service';

interface FiltersProps {
  onFilterChange: (filters: FilterState) => void;
}

export interface FilterState {
  search: string;
  productType: string;
  minPrice: string;
  maxPrice: string;
  categoryId: string;
}

const CatalogFilters = ({ onFilterChange }: FiltersProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const [genres, setGenres] = useState<Genre[]>([]);
  
  const [filters, setFilters] = useState<FilterState>({
    search: '',
    productType: '',
    minPrice: '',
    maxPrice: '',
    categoryId: '',  // Cambio aquí
  });

  useEffect(() => {
    loadGenres();
  }, []);

  const loadGenres = async () => {
    try {
        const data = await catalogService.getGenres();
        // Asegurarse de que data es un array
        if (Array.isArray(data)) {
          setGenres(data);
        } else {
          console.error('Genres data is not an array:', data);
          setGenres([]);
        }
      } catch (error) {
        console.error('Error loading genres:', error);
        setGenres([]);
      }
    };

  const handleFilterChange = (key: keyof FilterState, value: string) => {
    const newFilters = { ...filters, [key]: value };
    setFilters(newFilters);
    onFilterChange(newFilters);
  };

  const clearFilters = () => {
    const emptyFilters: FilterState = {
      search: '',
      productType: '',
      minPrice: '',
      maxPrice: '',
      categoryId: '',
    };
    setFilters(emptyFilters);
    onFilterChange(emptyFilters);
  };

  const hasActiveFilters = Object.values(filters).some(value => value !== '');

  return (
    <div className="bg-white rounded-lg shadow-md p-4 mb-6">
      {/* Mobile Toggle */}
      <div className="lg:hidden">
        <button
          onClick={() => setIsOpen(!isOpen)}
          className="flex items-center justify-between w-full text-gray-700 font-medium"
        >
          <span className="flex items-center">
            <Filter className="h-5 w-5 mr-2" />
            Filtros
          </span>
          {hasActiveFilters && (
            <span className="bg-primary-900 text-white text-xs px-2 py-1 rounded-full">
              Activos
            </span>
          )}
        </button>
      </div>

      {/* Filters */}
      <div className={`${isOpen ? 'block' : 'hidden'} lg:block mt-4 lg:mt-0`}>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
          {/* Search */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Buscar
            </label>
            <input
              type="text"
              value={filters.search}
              onChange={(e) => handleFilterChange('search', e.target.value)}
              placeholder="Nombre, artista..."
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>

          {/* Product Type */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Formato
            </label>
            <select
              value={filters.productType}
              onChange={(e) => handleFilterChange('productType', e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="">Todos</option>
              <option value="VINYL">Vinilo</option>
              <option value="DIGITAL">Digital</option>
            </select>
          </div>

          {/* Genre */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Género
            </label>
            <select
              value={filters.categoryId}
              onChange={(e) => handleFilterChange('categoryId', e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="">Todos</option>
              {genres.map((genre) => (
                <option key={genre.id} value={genre.id}>
                  {genre.name}
                </option>
              ))}
            </select>
          </div>

          {/* Min Price */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Precio Mín
            </label>
            <input
              type="number"
              value={filters.minPrice}
              onChange={(e) => handleFilterChange('minPrice', e.target.value)}
              placeholder="$0"
              min="0"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>

          {/* Max Price */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Precio Máx
            </label>
            <input
              type="number"
              value={filters.maxPrice}
              onChange={(e) => handleFilterChange('maxPrice', e.target.value)}
              placeholder="$999,999"
              min="0"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>
        </div>

        {/* Clear Filters */}
        {hasActiveFilters && (
          <div className="mt-4 flex justify-end">
            <button
              onClick={clearFilters}
              className="flex items-center space-x-2 text-sm text-gray-600 hover:text-primary-900 transition"
            >
              <X className="h-4 w-4" />
              <span>Limpiar filtros</span>
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default CatalogFilters;