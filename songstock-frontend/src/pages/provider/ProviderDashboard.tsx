import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { 
  Package, 
  Plus, 
  TrendingUp, 
  DollarSign,
  Eye,
  Edit,
  Trash2,
  Search
} from 'lucide-react';
import providerService from '../../services/provider.service';
import { Product } from '../../types/product.types';
import toast from 'react-hot-toast';

const ProviderDashboard = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [stats, setStats] = useState({
    totalProducts: 0,
    activeProducts: 0,
    totalRevenue: 0,
    totalOrders: 0
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      // Cargar productos del proveedor
      const productsData = await providerService.getMyProducts();
      console.log('Products loaded:', productsData); // Debug
      
      // Verificar que productsData sea un array
      if (!Array.isArray(productsData)) {
        console.error('Products data is not an array:', productsData);
        setProducts([]);
        calculateStatsFromProducts([]);
        toast.error('Error: formato de datos inválido');
        return;
      }
      
      setProducts(productsData);
  
      // Intentar cargar estadísticas del backend
      try {
        const statsData = await providerService.getProviderStats();
        if (statsData) {
          setStats({
            totalProducts: statsData.totalProducts || productsData.length,
            activeProducts: statsData.activeProducts || productsData.filter((p: Product) => p.isActive).length,
            totalRevenue: statsData.totalValue || productsData.reduce((sum: number, p: Product) => sum + (p.price * p.stockQuantity), 0),
            totalOrders: statsData.totalOrders || 0
          });
        } else {
          // Fallback: calcular desde los productos
          calculateStatsFromProducts(productsData);
        }
      } catch (statsError) {
        console.warn('Could not load stats from backend, calculating locally:', statsError);
        calculateStatsFromProducts(productsData);
      }
    } catch (error) {
      console.error('Error loading provider data:', error);
      toast.error('Error al cargar los datos');
      setProducts([]); // Asegurar que products sea un array vacío en caso de error
    } finally {
      setLoading(false);
    }
  };

// Función auxiliar para calcular stats localmente
const calculateStatsFromProducts = (productsData: Product[]) => {
    const active = productsData.filter(p => p.isActive).length;
    const totalRevenue = productsData.reduce((sum, p) => sum + (p.price * p.stockQuantity), 0);
    
    setStats({
      totalProducts: productsData.length,
      activeProducts: active,
      totalRevenue: totalRevenue,
      totalOrders: 0
    });
  };

  const handleDelete = async (id: number, title: string) => {
    if (!window.confirm(`¿Estás seguro de eliminar "${title}"?`)) {
      return;
    }

    try {
      await providerService.deleteProduct(id);
      toast.success('Producto eliminado exitosamente');
      loadData(); // Recargar la lista
    } catch (error) {
      console.error('Error deleting product:', error);
      toast.error('Error al eliminar el producto');
    }
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
    }).format(price);
  };

  // Filtrar productos según el término de búsqueda
  const filteredProducts = products.filter(product =>
    product.albumTitle.toLowerCase().includes(searchTerm.toLowerCase()) ||
    product.artistName.toLowerCase().includes(searchTerm.toLowerCase()) ||
    product.sku.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-900"></div>
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
              <h1 className="text-3xl font-bold text-gray-900">Dashboard de Proveedor</h1>
              <p className="text-gray-600 mt-1">Gestiona tus productos y ventas</p>
            </div>
            <Link
              to="/provider/products/new"
              className="bg-primary-900 text-white px-6 py-3 rounded-lg font-semibold hover:bg-primary-800 transition flex items-center space-x-2"
            >
              <Plus className="h-5 w-5" />
              <span>Nuevo Producto</span>
            </Link>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">Total Productos</p>
                <p className="text-3xl font-bold text-gray-900">{stats.totalProducts}</p>
              </div>
              <div className="bg-primary-100 p-3 rounded-lg">
                <Package className="h-8 w-8 text-primary-900" />
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">Productos Activos</p>
                <p className="text-3xl font-bold text-green-600">{stats.activeProducts}</p>
              </div>
              <div className="bg-green-100 p-3 rounded-lg">
                <TrendingUp className="h-8 w-8 text-green-600" />
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">Valor Inventario</p>
                <p className="text-2xl font-bold text-gray-900">{formatPrice(stats.totalRevenue)}</p>
              </div>
              <div className="bg-secondary-100 p-3 rounded-lg">
                <DollarSign className="h-8 w-8 text-secondary-600" />
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">Pedidos (Demo)</p>
                <p className="text-3xl font-bold text-gray-900">{stats.totalOrders}</p>
              </div>
              <div className="bg-blue-100 p-3 rounded-lg">
                <Package className="h-8 w-8 text-blue-600" />
              </div>
            </div>
          </div>
        </div>

        {/* Products Table */}
        <div className="bg-white rounded-lg shadow-md">
          <div className="p-6 border-b">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-bold text-gray-900">Mis Productos</h2>
              <div className="text-sm text-gray-600">
                {filteredProducts.length} de {products.length} productos
              </div>
            </div>

            {/* Search Bar */}
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
              <input
                type="text"
                placeholder="Buscar por título, artista o SKU..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>
          </div>

          {/* Table */}
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50 border-b">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Producto
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    SKU
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Tipo
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Precio
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Stock
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Estado
                  </th>
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Acciones
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {filteredProducts.length === 0 ? (
                  <tr>
                    <td colSpan={7} className="px-6 py-12 text-center">
                      <Package className="h-12 w-12 text-gray-300 mx-auto mb-4" />
                      <p className="text-gray-500 mb-2">
                        {searchTerm ? 'No se encontraron productos' : 'No tienes productos todavía'}
                      </p>
                      {!searchTerm && (
                        <Link
                          to="/provider/products/new"
                          className="text-primary-900 hover:text-primary-700 font-medium"
                        >
                          Crear tu primer producto
                        </Link>
                      )}
                    </td>
                  </tr>
                ) : (
                  filteredProducts.map((product) => (
                    <tr key={product.id} className="hover:bg-gray-50 transition">
                      <td className="px-6 py-4">
                        <div className="flex items-center space-x-3">
                          <div className="w-12 h-12 bg-gradient-to-br from-primary-100 to-secondary-100 rounded flex items-center justify-center flex-shrink-0">
                            <Package className="h-6 w-6 text-primary-600" />
                          </div>
                          <div>
                            <div className="font-medium text-gray-900">{product.albumTitle}</div>
                            <div className="text-sm text-gray-500">{product.artistName}</div>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <span className="text-sm font-mono text-gray-600">{product.sku}</span>
                      </td>
                      <td className="px-6 py-4">
                        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                          product.productType === 'PHYSICAL'
                            ? 'bg-primary-100 text-primary-800'
                            : 'bg-secondary-100 text-secondary-800'
                        }`}>
                          {product.productType === 'PHYSICAL' ? 'Físico' : 'Digital'}
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        <span className="text-sm font-semibold text-gray-900">
                          {formatPrice(product.price)}
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        <span className={`text-sm font-medium ${
                          product.stockQuantity > 10
                            ? 'text-green-600'
                            : product.stockQuantity > 0
                            ? 'text-yellow-600'
                            : 'text-red-600'
                        }`}>
                          {product.stockQuantity}
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                          product.isActive
                            ? 'bg-green-100 text-green-800'
                            : 'bg-gray-100 text-gray-800'
                        }`}>
                          {product.isActive ? 'Activo' : 'Inactivo'}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-right">
                        <div className="flex items-center justify-end space-x-2">
                          <Link
                            to={`/product/${product.id}`}
                            className="text-gray-600 hover:text-primary-900 transition"
                            title="Ver"
                          >
                            <Eye className="h-5 w-5" />
                          </Link>
                          <Link
                            to={`/provider/products/edit/${product.id}`}
                            className="text-blue-600 hover:text-blue-700 transition"
                            title="Editar"
                          >
                            <Edit className="h-5 w-5" />
                          </Link>
                          <button
                            onClick={() => handleDelete(product.id, product.albumTitle)}
                            className="text-red-600 hover:text-red-700 transition"
                            title="Eliminar"
                          >
                            <Trash2 className="h-5 w-5" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProviderDashboard;