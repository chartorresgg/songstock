import { useState, useEffect } from 'react';
import orderService from '../../services/order.service';
import { Order, OrderItemStatus } from '../../types/order.types';
import { Link } from 'react-router-dom';
import { 
  Package, 
  Plus, 
  TrendingUp, 
  DollarSign,
  Eye,
  Edit,
  Trash2,
  Search,
  ShoppingCart,
  Check,
  X
} from 'lucide-react';
import providerService from '../../services/provider.service';
import { Product } from '../../types/product.types';
import toast from 'react-hot-toast';

const ProviderDashboard = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [pendingOrders, setPendingOrders] = useState<Order[]>([]);
  const [processingItem, setProcessingItem] = useState<number | null>(null);
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
      const productsData = await providerService.getMyProducts();
      
      if (!Array.isArray(productsData)) {
        console.error('Products data is not an array:', productsData);
        setProducts([]);
        calculateStatsFromProducts([]);
        toast.error('Error: formato de datos inválido');
        return;
      }
      
      setProducts(productsData);

      // Cargar órdenes pendientes
      try {
        const ordersData = await orderService.getPendingOrders();
        setPendingOrders(ordersData);
      } catch (ordersError) {
        console.warn('Could not load pending orders:', ordersError);
        setPendingOrders([]);
      }
  
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
          calculateStatsFromProducts(productsData);
        }
      } catch (statsError) {
        console.warn('Could not load stats from backend, calculating locally:', statsError);
        calculateStatsFromProducts(productsData);
      }
    } catch (error) {
      console.error('Error loading provider data:', error);
      toast.error('Error al cargar los datos');
      setProducts([]);
    } finally {
      setLoading(false);
    }
  };

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

  const handleAcceptItem = async (itemId: number) => {
    try {
      setProcessingItem(itemId);
      await orderService.acceptOrderItem(itemId);
      toast.success('Pedido aceptado exitosamente');
      await loadData();
    } catch (error) {
      console.error('Error accepting item:', error);
      toast.error('Error al aceptar el pedido');
    } finally {
      setProcessingItem(null);
    }
  };

  const handleRejectItem = async (itemId: number) => {
    const reason = prompt('Motivo del rechazo:');
    if (!reason || reason.trim() === '') {
      toast.error('Debe proporcionar un motivo de rechazo');
      return;
    }

    try {
      setProcessingItem(itemId);
      await orderService.rejectOrderItem(itemId, reason);
      toast.success('Pedido rechazado');
      await loadData();
    } catch (error) {
      console.error('Error rejecting item:', error);
      toast.error('Error al rechazar el pedido');
    } finally {
      setProcessingItem(null);
    }
  };

  const handleDelete = async (id: number, title: string) => {
    if (!window.confirm(`¿Estás seguro de eliminar "${title}"?`)) {
      return;
    }

    try {
      await providerService.deleteProduct(id);
      toast.success('Producto eliminado exitosamente');
      loadData();
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

  const filteredProducts = products.filter(product =>
    product.albumTitle.toLowerCase().includes(searchTerm.toLowerCase()) ||
    product.artistName.toLowerCase().includes(searchTerm.toLowerCase()) ||
    product.sku.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const pendingItemsCount = pendingOrders.reduce((acc, order) => 
    acc + order.items.filter(item => item.status === OrderItemStatus.PENDING).length, 0
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
                <p className="text-sm text-gray-600 mb-1">Pedidos Pendientes</p>
                <p className="text-3xl font-bold text-yellow-600">{pendingItemsCount}</p>
              </div>
              <div className="bg-yellow-100 p-3 rounded-lg">
                <ShoppingCart className="h-8 w-8 text-yellow-600" />
              </div>
            </div>
          </div>
        </div>

        {/* Órdenes Pendientes */}
        {pendingOrders.length > 0 && (
          <div className="bg-white rounded-lg shadow-md mb-8">
            <div className="p-6 border-b">
              <div className="flex items-center justify-between">
                <h2 className="text-xl font-bold text-gray-900 flex items-center">
                  <ShoppingCart className="h-6 w-6 mr-2 text-primary-600" />
                  Órdenes Pendientes
                </h2>
                <span className="bg-yellow-100 text-yellow-800 text-sm font-semibold px-3 py-1 rounded-full">
                  {pendingItemsCount} items
                </span>
              </div>
            </div>

            <div className="p-6">
              <div className="space-y-4">
                {pendingOrders.map(order => {
                  const myPendingItems = order.items.filter(
                    item => item.status === OrderItemStatus.PENDING
                  );

                  if (myPendingItems.length === 0) return null;

                  return (
                    <div key={order.id} className="border rounded-lg p-4 hover:shadow-md transition">
                      <div className="flex justify-between items-start mb-3">
                        <div>
                          <h3 className="font-semibold text-gray-900">
                            Orden #{order.orderNumber}
                          </h3>
                          <p className="text-sm text-gray-500">
                            {new Date(order.createdAt).toLocaleDateString('es-CO', {
                              year: 'numeric',
                              month: 'long',
                              day: 'numeric',
                              hour: '2-digit',
                              minute: '2-digit'
                            })}
                          </p>
                        </div>
                        <span className="text-lg font-bold text-gray-900">
                          {formatPrice(order.total)}
                        </span>
                      </div>

                      {myPendingItems.map(item => (
                        <div key={item.id} className="bg-gray-50 rounded-lg p-4 mb-2">
                          <div className="flex justify-between items-center">
                            <div className="flex-1">
                              <p className="font-medium text-gray-900">{item.product.albumTitle}</p>
                              <p className="text-sm text-gray-600">{item.product.artistName}</p>
                              <div className="flex items-center space-x-4 mt-2">
                                <span className="text-sm text-gray-500">
                                  Cantidad: <span className="font-semibold">{item.quantity}</span>
                                </span>
                                <span className="text-sm text-gray-500">
                                  Precio: <span className="font-semibold">{formatPrice(item.price)}</span>
                                </span>
                                <span className="text-sm font-semibold text-primary-600">
                                  Subtotal: {formatPrice(item.subtotal)}
                                </span>
                              </div>
                            </div>
                            <div className="flex space-x-2 ml-4">
                              <button
                                onClick={() => handleAcceptItem(item.id)}
                                disabled={processingItem === item.id}
                                className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed transition flex items-center space-x-2"
                                title="Aceptar pedido"
                              >
                                <Check className="h-5 w-5" />
                                <span>Aceptar</span>
                              </button>
                              <button
                                onClick={() => handleRejectItem(item.id)}
                                disabled={processingItem === item.id}
                                className="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 disabled:opacity-50 disabled:cursor-not-allowed transition flex items-center space-x-2"
                                title="Rechazar pedido"
                              >
                                <X className="h-5 w-5" />
                                <span>Rechazar</span>
                              </button>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        )}

        {/* Tabla de Productos */}
        <div className="bg-white rounded-lg shadow-md">
          <div className="p-6 border-b">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-bold text-gray-900">Mis Productos</h2>
              <div className="text-sm text-gray-600">
                {filteredProducts.length} de {products.length} productos
              </div>
            </div>

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
                            className="text-gray-400 hover:text-primary-600 transition"
                            title="Ver"
                          >
                            <Eye className="h-5 w-5" />
                          </Link>
                          <Link
                            to={`/provider/products/${product.id}/edit`}
                            className="text-gray-400 hover:text-blue-600 transition"
                            title="Editar"
                          >
                            <Edit className="h-5 w-5" />
                          </Link>
                          <button
                            onClick={() => handleDelete(product.id, product.albumTitle)}
                            className="text-gray-400 hover:text-red-600 transition"
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