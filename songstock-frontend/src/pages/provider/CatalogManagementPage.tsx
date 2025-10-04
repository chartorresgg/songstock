// src/pages/provider/CatalogManagementPage.tsx

import React, { useState, useEffect } from 'react';
import { CatalogStatsCards } from '../../components/catalog/CatalogStatsCards';
import { CatalogTable } from '../../components/catalog/CatalogTable';
import { ProductFormModal } from '../../components/catalog/ProductFormModal';
import Button from '../../components/ui/Button';
import Input from '../../components/ui/Input';
import { useToast } from '../../hooks/useToast';
import catalogService from '../../services/catalogService';
import { 
  ProviderCatalogSummary, 
  ProductCatalogResponse,
  ProductType,
  ConditionType 
} from '../../types/catalog';

export const CatalogManagementPage: React.FC = () => {
  // Estados principales
  const [catalogSummary, setCatalogSummary] = useState<ProviderCatalogSummary | null>(null);
  const [filteredProducts, setFilteredProducts] = useState<ProductCatalogResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  // Estados para modal de producto
  const [productModal, setProductModal] = useState<{
    isOpen: boolean;
    mode: 'create' | 'edit';
    product: ProductCatalogResponse | null;
  }>({
    isOpen: false,
    mode: 'create',
    product: null
  });

  // Estados para filtros
  const [filters, setFilters] = useState({
    search: '',
    productType: '',
    conditionType: '',
    isActive: '',
    inStock: ''
  });

  const { showToast } = useToast();

  // Cargar datos iniciales
  useEffect(() => {
    loadCatalogData();
  }, []);

  // Aplicar filtros cuando cambien
  useEffect(() => {
    applyFilters();
  }, [filters, catalogSummary]);

  const loadCatalogData = async () => {
    try {
      setLoading(true);
      const summary = await catalogService.getProviderCatalog();
      setCatalogSummary(summary);
      setFilteredProducts(summary.products);
    } catch (error: any) {
      showToast('Error al cargar el catálogo: ' + (error.response?.data?.message || error.message), 'error');
    } finally {
      setLoading(false);
    }
  };

  const refreshCatalog = async () => {
    try {
      setRefreshing(true);
      const summary = await catalogService.getProviderCatalog();
      setCatalogSummary(summary);
      setFilteredProducts(summary.products);
      showToast('Catálogo actualizado exitosamente', 'success');
    } catch (error: any) {
      showToast('Error al actualizar el catálogo', 'error');
    } finally {
      setRefreshing(false);
    }
  };

  const applyFilters = () => {
    if (!catalogSummary) return;

    let filtered = [...catalogSummary.products];

    // Filtro de búsqueda por texto
    if (filters.search) {
      const searchLower = filters.search.toLowerCase();
      filtered = filtered.filter(product => 
        product.albumTitle.toLowerCase().includes(searchLower) ||
        product.artistName.toLowerCase().includes(searchLower) ||
        product.sku.toLowerCase().includes(searchLower) ||
        product.categoryName.toLowerCase().includes(searchLower)
      );
    }

    // Filtro por tipo de producto
    if (filters.productType) {
      filtered = filtered.filter(product => product.productType === filters.productType);
    }

    // Filtro por condición
    if (filters.conditionType) {
      filtered = filtered.filter(product => product.conditionType === filters.conditionType);
    }

    // Filtro por estado activo/inactivo
    if (filters.isActive) {
      const isActive = filters.isActive === 'true';
      filtered = filtered.filter(product => product.isActive === isActive);
    }

    // Filtro por stock
    if (filters.inStock) {
      if (filters.inStock === 'true') {
        filtered = filtered.filter(product => product.stockQuantity > 0);
      } else if (filters.inStock === 'false') {
        filtered = filtered.filter(product => product.stockQuantity === 0);
      } else if (filters.inStock === 'low') {
        filtered = filtered.filter(product => product.stockQuantity > 0 && product.stockQuantity <= 5);
      }
    }

    setFilteredProducts(filtered);
  };

  const handleCreateProduct = () => {
    setProductModal({
      isOpen: true,
      mode: 'create',
      product: null
    });
  };

  const handleEditProduct = (product: ProductCatalogResponse) => {
    setProductModal({
      isOpen: true,
      mode: 'edit',
      product
    });
  };

  const handleProductSave = async (savedProduct: ProductCatalogResponse) => {
    await refreshCatalog();
    showToast(
      productModal.mode === 'create' 
        ? 'Producto creado exitosamente' 
        : 'Producto actualizado exitosamente', 
      'success'
    );
  };

  const handleToggleStatus = async (productId: number) => {
    try {
      await catalogService.toggleProductStatus(productId);
      await refreshCatalog();
      showToast('Estado del producto actualizado', 'success');
    } catch (error: any) {
      showToast('Error al cambiar el estado del producto', 'error');
    }
  };

  const handleDeleteProduct = async (productId: number) => {
    try {
      await catalogService.deleteCatalogProduct(productId);
      await refreshCatalog();
      showToast('Producto eliminado del catálogo', 'success');
    } catch (error: any) {
      showToast('Error al eliminar el producto', 'error');
    }
  };

  const handleFilterChange = (field: string, value: string) => {
    setFilters(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const clearFilters = () => {
    setFilters({
      search: '',
      productType: '',
      conditionType: '',
      isActive: '',
      inStock: ''
    });
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Gestión de Catálogo</h1>
              <p className="mt-2 text-gray-600">
                {catalogSummary ? `${catalogSummary.providerName} - ${catalogSummary.totalProducts} productos` : 'Cargando...'}
              </p>
            </div>
            <div className="flex space-x-3">
              <Button
                variant="secondary"
                onClick={refreshCatalog}
                disabled={refreshing}
              >
                {refreshing ? 'Actualizando...' : 'Actualizar'}
              </Button>
              <Button
                variant="primary"
                onClick={handleCreateProduct}
              >
                Agregar Producto
              </Button>
            </div>
          </div>
        </div>

        {/* Estadísticas */}
        <CatalogStatsCards summary={catalogSummary} loading={loading} />

        {/* Filtros */}
        <div className="bg-white rounded-lg shadow-lg p-6 mb-6">
          <div className="flex flex-wrap items-center justify-between mb-4">
            <h3 className="text-lg font-medium text-gray-900">Filtros</h3>
            <Button
              variant="secondary"
              size="sm"
              onClick={clearFilters}
            >
              Limpiar Filtros
            </Button>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4">
            {/* Búsqueda por texto */}
            <div className="xl:col-span-2">
              <Input
                placeholder="Buscar por título, artista, SKU..."
                value={filters.search}
                onChange={(e) => handleFilterChange('search', e.target.value)}
              />
            </div>

            {/* Tipo de producto */}
            <div>
              <select
                value={filters.productType}
                onChange={(e) => handleFilterChange('productType', e.target.value)}
                className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="">Todos los tipos</option>
                <option value="PHYSICAL">Vinilo Físico</option>
                <option value="DIGITAL">Digital</option>
              </select>
            </div>

            {/* Condición */}
            <div>
              <select
                value={filters.conditionType}
                onChange={(e) => handleFilterChange('conditionType', e.target.value)}
                className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="">Todas las condiciones</option>
                <option value="NEW">Nuevo</option>
                <option value="LIKE_NEW">Como Nuevo</option>
                <option value="VERY_GOOD">Muy Bueno</option>
                <option value="GOOD">Bueno</option>
                <option value="FAIR">Regular</option>
                <option value="POOR">Malo</option>
              </select>
            </div>

            {/* Estado activo/inactivo */}
            <div>
              <select
                value={filters.isActive}
                onChange={(e) => handleFilterChange('isActive', e.target.value)}
                className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="">Todos los estados</option>
                <option value="true">Activos</option>
                <option value="false">Inactivos</option>
              </select>
            </div>

            {/* Stock */}
            <div>
              <select
                value={filters.inStock}
                onChange={(e) => handleFilterChange('inStock', e.target.value)}
                className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="">Todo el stock</option>
                <option value="true">Con stock</option>
                <option value="false">Sin stock</option>
                <option value="low">Stock bajo (≤5)</option>
              </select>
            </div>
          </div>

          {/* Resultados de filtros */}
          <div className="mt-4 text-sm text-gray-600">
            Mostrando {filteredProducts.length} de {catalogSummary?.totalProducts || 0} productos
          </div>
        </div>

        {/* Tabla de productos */}
        <CatalogTable
          products={filteredProducts}
          loading={loading}
          onEdit={handleEditProduct}
          onToggleStatus={handleToggleStatus}
          onDelete={handleDeleteProduct}
        />

        {/* Modal de producto */}
        <ProductFormModal
          isOpen={productModal.isOpen}
          onClose={() => setProductModal({ isOpen: false, mode: 'create', product: null })}
          onSave={handleProductSave}
          product={productModal.product}
          mode={productModal.mode}
        />
      </div>
    </div>
  );
};