import { useState, useEffect, useMemo } from 'react';
import orderService from '../../services/order.service';
import { Order, OrderItemStatus, OrderStatus } from '../../types/order.types';
import { Link, useLocation } from 'react-router-dom';
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
  X,
  Clock,
  CheckCircle,
  XCircle,
  List,
  Truck,
  BarChart3
} from 'lucide-react';
import providerService from '../../services/provider.service';
import { Product } from '../../types/product.types';
import toast from 'react-hot-toast';
import { useAuth } from '../../contexts/AuthContext';

type TabType = 'products' | 'pending' | 'history' | 'reports';

const ProviderDashboard = () => {
  const { user } = useAuth();
  const location = useLocation();
  const tabFromState = (location.state as any)?.tab;

  console.log('🎯 Location state:', location.state);
  console.log('🎯 Tab from state:', tabFromState);

  const initialTab = useMemo(() => {
    const result = (['products', 'pending', 'history', 'reports'] as TabType[]).includes(tabFromState as TabType) 
      ? (tabFromState as TabType) 
      : 'products';
    return result;
  }, [tabFromState]);
 
  const [activeTab, setActiveTab] = useState<TabType>(initialTab);

  const [products, setProducts] = useState<Product[]>([]);
  const [pendingOrders, setPendingOrders] = useState<Order[]>([]);
  const [allOrders, setAllOrders] = useState<Order[]>([]);
  const [salesReport, setSalesReport] = useState<any>(null);
  const [loadingReport, setLoadingReport] = useState(false);
  const [showShipModal, setShowShipModal] = useState(false);
  const [itemToShip, setItemToShip] = useState<number | null>(null);
  const [shipDate, setShipDate] = useState('');
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
    (window as any).debugOrders = allOrders;
  }, [allOrders]);

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

      try {
        const pendingOrdersData = await orderService.getPendingOrders();
        setPendingOrders(pendingOrdersData);
      } catch (ordersError) {
        console.warn('Could not load pending orders:', ordersError);
        setPendingOrders([]);
      }

      try {
        const allOrdersData = await orderService.getProviderOrders();
        setAllOrders(allOrdersData);
      } catch (ordersError) {
        console.warn('Could not load order history:', ordersError);
        setAllOrders([]);
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

  const loadSalesReport = async () => {
    if (!user?.providerId) {
      toast.error('No se pudo obtener el ID del proveedor');
      return;
    }
    
    setLoadingReport(true);
    try {
      const report = await providerService.getSalesReport(user.providerId);
      setSalesReport(report);
      toast.success('Reporte generado exitosamente');
    } catch (error) {
      console.error('Error loading sales report:', error);
      toast.error('Error al cargar el reporte de ventas');
    } finally {
      setLoadingReport(false);
    }
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

  const openShipModal = (itemId: number) => {
    setItemToShip(itemId);
    const now = new Date();
    const localDate = new Date(now.getTime() - now.getTimezoneOffset() * 60000)
      .toISOString()
      .slice(0, 16);
    setShipDate(localDate);
    setShowShipModal(true);
  };

  const handleShipItem = async () => {
    if (!itemToShip) return;

    setProcessingItem(itemToShip);
    try {
      const isoDate = new Date(shipDate).toISOString();
      await orderService.shipOrderItem(itemToShip, isoDate);
      toast.success('Item marcado como enviado');
      setShowShipModal(false);
      setItemToShip(null);
      setShipDate('');
      await loadData();
    } catch (error) {
      console.error('Error shipping item:', error);
      toast.error('Error al marcar como enviado');
    } finally {
      setProcessingItem(null);
    }
  };

  const handleDeliverItem = async (itemId: number) => {
    setProcessingItem(itemId);
    try {
      await orderService.deliverOrderItem(itemId);
      toast.success('Item marcado como entregado');
      await loadData();
    } catch (error) {
      console.error('Error delivering item:', error);
      toast.error('Error al marcar como entregado');
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

  const getStatusBadge = (status: OrderItemStatus) => {
    const badges = {
      PENDING: { color: 'bg-yellow-100 text-yellow-800', text: 'Pendiente' },
      ACCEPTED: { color: 'bg-green-100 text-green-800', text: 'Aceptada' },
      REJECTED: { color: 'bg-red-100 text-red-800', text: 'Rechazado' },
      PROCESSING: { color: 'bg-blue-100 text-blue-800', text: 'En Proceso' },
      SHIPPED: { color: 'bg-indigo-100 text-indigo-800', text: 'Enviado' },
      DELIVERED: { color: 'bg-green-100 text-green-800', text: 'Entregado' },
      RECEIVED: { color: 'bg-green-100 text-green-800', text: 'Recibido' },
    };
    const badge = badges[status] || badges.PENDING;
    return (
      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${badge.color}`}>
        {badge.text}
      </span>
    );
  };

  const getOrderStatusBadge = (status: OrderStatus) => {
    const badges: Record<OrderStatus, { color: string; text: string }> = {
      PENDING: { color: 'bg-yellow-100 text-yellow-800', text: 'Pendiente' },
      ACCEPTED: { color: 'bg-green-100 text-green-800', text: 'Aceptada' },
      CONFIRMED: { color: 'bg-blue-100 text-blue-800', text: 'Confirmada' },
      PROCESSING: { color: 'bg-blue-100 text-blue-800', text: 'En Proceso' },
      SHIPPED: { color: 'bg-indigo-100 text-indigo-800', text: 'Enviada' },
      DELIVERED: { color: 'bg-green-100 text-green-800', text: 'Entregada' },
      RECEIVED: { color: 'bg-green-200 text-green-900', text: '✓ Recibida por Cliente' },
      CANCELLED: { color: 'bg-red-100 text-red-800', text: 'Cancelada' },
      REJECTED: { color: 'bg-red-100 text-red-800', text: 'Rechazada' },
    };
    return (
      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${badges[status].color}`}>
        {badges[status].text}
      </span>
    );
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

        {/* TABS */}
        <div className="mb-6">
          <div className="flex space-x-2 border-b">
            <button
              onClick={() => setActiveTab('products')}
              className={`px-6 py-3 font-semibold transition-colors flex items-center space-x-2 ${
                activeTab === 'products'
                  ? 'border-b-2 border-primary-900 text-primary-900'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              <Package className="h-5 w-5" />
              <span>Productos</span>
            </button>
            
            <button
              onClick={() => setActiveTab('pending')}
              className={`px-6 py-3 font-semibold transition-colors flex items-center space-x-2 relative ${
                activeTab === 'pending'
                  ? 'border-b-2 border-primary-900 text-primary-900'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              <Clock className="h-5 w-5" />
              <span>Pendientes</span>
              {pendingItemsCount > 0 && (
                <span className="absolute -top-1 -right-1 bg-yellow-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
                  {pendingItemsCount}
                </span>
              )}
            </button>

            <button
              onClick={() => setActiveTab('history')}
              className={`px-6 py-3 font-semibold transition-colors flex items-center space-x-2 ${
                activeTab === 'history'
                  ? 'border-b-2 border-primary-900 text-primary-900'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              <List className="h-5 w-5" />
              <span>Historial</span>
            </button>

            <button
              onClick={() => setActiveTab('reports')}
              className={`px-6 py-3 font-semibold transition-colors flex items-center space-x-2 ${
                activeTab === 'reports'
                  ? 'border-b-2 border-primary-900 text-primary-900'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              <BarChart3 className="h-5 w-5" />
              <span>Reportes</span>
            </button>
          </div>
        </div>

        {/* TAB CONTENT: PRODUCTOS */}
        {activeTab === 'products' && (
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
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Producto</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">SKU</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tipo</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Precio</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Stock</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
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
                          <Link to="/provider/products/new" className="text-primary-900 hover:text-primary-700 font-medium">
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
                            product.productType === 'PHYSICAL' ? 'bg-primary-100 text-primary-800' : 'bg-secondary-100 text-secondary-800'
                          }`}>
                            {product.productType === 'PHYSICAL' ? 'Físico' : 'Digital'}
                          </span>
                        </td>
                        <td className="px-6 py-4">
                          <span className="text-sm font-semibold text-gray-900">{formatPrice(product.price)}</span>
                        </td>
                        <td className="px-6 py-4">
                          <span className={`text-sm font-medium ${
                            product.stockQuantity > 10 ? 'text-green-600' : product.stockQuantity > 0 ? 'text-yellow-600' : 'text-red-600'
                          }`}>
                            {product.stockQuantity}
                          </span>
                        </td>
                        <td className="px-6 py-4">
                          <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                            product.isActive ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                          }`}>
                            {product.isActive ? 'Activo' : 'Inactivo'}
                          </span>
                        </td>
                        <td className="px-6 py-4 text-right">
                          <div className="flex items-center justify-end space-x-2">
                            <Link to={`/product/${product.id}`} className="text-gray-400 hover:text-primary-600 transition" title="Ver">
                              <Eye className="h-5 w-5" />
                            </Link>
                            <Link to={`/provider/products/${product.id}/edit`} className="text-gray-400 hover:text-blue-600 transition" title="Editar">
                              <Edit className="h-5 w-5" />
                            </Link>
                            <button onClick={() => handleDelete(product.id, product.albumTitle)} className="text-gray-400 hover:text-red-600 transition" title="Eliminar">
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
        )}

        {/* TAB CONTENT: ÓRDENES PENDIENTES */}
        {activeTab === 'pending' && (
          <div className="bg-white rounded-lg shadow-md">
            <div className="p-6 border-b">
              <div className="flex items-center justify-between">
                <h2 className="text-xl font-bold text-gray-900 flex items-center">
                  <Clock className="h-6 w-6 mr-2 text-primary-600" />
                  Órdenes Pendientes
                </h2>
                <span className="bg-yellow-100 text-yellow-800 text-sm font-semibold px-3 py-1 rounded-full">
                  {pendingItemsCount} items
                </span>
              </div>
            </div>

            <div className="p-6">
              {pendingOrders.length === 0 ? (
                <div className="text-center py-12">
                  <CheckCircle className="h-16 w-16 text-green-300 mx-auto mb-4" />
                  <p className="text-gray-500">No hay órdenes pendientes</p>
                </div>
              ) : (
                <div className="space-y-4">
                  {pendingOrders.map(order => {
                    const myPendingItems = order.items.filter(item => item.status === OrderItemStatus.PENDING);
                    if (myPendingItems.length === 0) return null;

                    return (
                      <div key={order.id} className="border rounded-lg p-4 hover:shadow-md transition">
                        <div className="flex justify-between items-start mb-3">
                          <div>
                            <div className="flex items-center gap-3 mb-1">
                              <h3 className="font-semibold text-gray-900">Orden #{order.orderNumber}</h3>
                              {getOrderStatusBadge(order.status)}
                            </div>
                            <p className="text-sm text-gray-500">
                              {new Date(order.createdAt).toLocaleDateString('es-CO', {
                                year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit'
                              })}
                            </p>
                          </div>
                          <span className="text-lg font-bold text-gray-900">{formatPrice(order.total)}</span>
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
                                >
                                  <Check className="h-5 w-5" />
                                  <span>Aceptar</span>
                                </button>
                                <button
                                  onClick={() => handleRejectItem(item.id)}
                                  disabled={processingItem === item.id}
                                  className="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 disabled:opacity-50 disabled:cursor-not-allowed transition flex items-center space-x-2"
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
              )}
            </div>
          </div>
        )}

        {/* TAB CONTENT: HISTORIAL DE ÓRDENES */}
        {activeTab === 'history' && (
          <div className="bg-white rounded-lg shadow-md">
            <div className="p-6 border-b">
              <h2 className="text-xl font-bold text-gray-900 flex items-center">
                <List className="h-6 w-6 mr-2 text-primary-600" />
                Historial Completo de Órdenes
              </h2>
            </div>

            <div className="p-6">
              {allOrders.length === 0 ? (
                <div className="text-center py-12">
                  <Package className="h-16 w-16 text-gray-300 mx-auto mb-4" />
                  <p className="text-gray-500">No hay órdenes registradas</p>
                </div>
              ) : (
                <div className="space-y-4">
                  {allOrders.map(order => (
                    <div key={order.id} className="border rounded-lg p-4 hover:shadow-md transition">
                      <div className="flex justify-between items-start mb-3">
                        <div>
                          <div className="flex items-center gap-3 mb-1">
                            <h3 className="font-semibold text-gray-900">Orden #{order.orderNumber}</h3>
                            {getOrderStatusBadge(order.status)}
                          </div>
                          <p className="text-sm text-gray-500">
                            {new Date(order.createdAt).toLocaleDateString('es-CO', {
                              year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit'
                            })}
                          </p>
                          {order.status === OrderStatus.RECEIVED && (
                            <p className="text-xs text-green-700 mt-1 font-medium">
                              ✓ El cliente confirmó la recepción del pedido
                            </p>
                          )}
                        </div>
                        <span className="text-lg font-bold text-gray-900">{formatPrice(order.total)}</span>
                      </div>

                      <div className="space-y-2">
                        {order.items.map(item => (
                          <div key={item.id} className="bg-gray-50 rounded-lg p-4 flex justify-between items-center">
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
                            <div className="ml-4">
                              <div className="flex flex-col items-end space-y-2">
                                {getStatusBadge(item.status)}
                                
                                {item.status === OrderItemStatus.ACCEPTED && (
                                  <button
                                    onClick={() => openShipModal(item.id)}
                                    disabled={processingItem === item.id}
                                    className="bg-blue-600 text-white px-3 py-1 rounded text-sm hover:bg-blue-700 disabled:opacity-50 flex items-center space-x-1"
                                  >
                                    <Truck className="h-4 w-4" />
                                    <span>Enviar</span>
                                  </button>
                                )}
                                
                                {item.status === OrderItemStatus.SHIPPED && (
                                  <button
                                    onClick={() => handleDeliverItem(item.id)}
                                    disabled={processingItem === item.id}
                                    className="bg-green-600 text-white px-3 py-1 rounded text-sm hover:bg-green-700 disabled:opacity-50 flex items-center space-x-1"
                                  >
                                    <CheckCircle className="h-4 w-4" />
                                    <span>Entregar</span>
                                  </button>
                                )}
                                
                                {item.status === OrderItemStatus.REJECTED && item.rejectionReason && (
                                  <p className="text-xs text-red-600 mt-1">Motivo: {item.rejectionReason}</p>
                                )}
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        )}

        {/* TAB CONTENT: REPORTES DE VENTAS */}
        {activeTab === 'reports' && (
          <div className="bg-white rounded-lg shadow-md">
            <div className="p-6 border-b">
              <div className="flex justify-between items-center">
                <h2 className="text-xl font-bold text-gray-900 flex items-center">
                  <BarChart3 className="h-6 w-6 mr-2 text-primary-600" />
                  Reporte de Ventas
                </h2>
                <button
                  onClick={loadSalesReport}
                  disabled={loadingReport}
                  className="bg-primary-900 text-white px-6 py-2 rounded-lg font-semibold hover:bg-primary-800 disabled:bg-gray-400 transition"
                >
                  {loadingReport ? 'Generando...' : 'Generar Reporte'}
                </button>
              </div>
            </div>

            <div className="p-6">
              {!salesReport ? (
                <div className="text-center py-12">
                  <BarChart3 className="h-16 w-16 text-gray-300 mx-auto mb-4" />
                  <p className="text-gray-500 mb-4">Genera un reporte para ver tus métricas de ventas</p>
                  <button
                    onClick={loadSalesReport}
                    disabled={loadingReport}
                    className="bg-primary-900 text-white px-6 py-2 rounded-lg font-semibold hover:bg-primary-800 disabled:bg-gray-400 transition"
                  >
                    {loadingReport ? 'Generando...' : 'Generar Reporte'}
                  </button>
                </div>
              ) : (
                <div className="space-y-6">
                  {/* Métricas principales */}
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div className="bg-green-50 p-6 rounded-lg border border-green-200">
                      <p className="text-sm text-gray-600 mb-1">Ingresos Totales</p>
                      <p className="text-3xl font-bold text-green-600">
                        {formatPrice(salesReport.totalRevenue || 0)}
                      </p>
                      <p className="text-xs text-gray-500 mt-1">De items completados</p>
                    </div>
                    <div className="bg-blue-50 p-6 rounded-lg border border-blue-200">
                      <p className="text-sm text-gray-600 mb-1">Total Órdenes</p>
                      <p className="text-3xl font-bold text-blue-600">
                        {salesReport.totalOrders || 0}
                      </p>
                      <p className="text-xs text-gray-500 mt-1">Órdenes únicas</p>
                    </div>
                    <div className="bg-purple-50 p-6 rounded-lg border border-purple-200">
                      <p className="text-sm text-gray-600 mb-1">Promedio por Orden</p>
                      <p className="text-3xl font-bold text-purple-600">
                        {formatPrice(salesReport.averageOrderValue || 0)}
                      </p>
                      <p className="text-xs text-gray-500 mt-1">Valor promedio</p>
                    </div>
                  </div>

                  {/* Métricas adicionales */}
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div className="bg-gray-50 p-4 rounded-lg border">
                      <p className="text-sm text-gray-600 mb-1">Items Completados</p>
                      <p className="text-2xl font-semibold text-gray-900">{salesReport.completedItems || 0}</p>
                      <p className="text-xs text-gray-500 mt-1">Enviados/Entregados</p>
                    </div>
                    <div className="bg-gray-50 p-4 rounded-lg border">
                      <p className="text-sm text-gray-600 mb-1">Items Pendientes</p>
                      <p className="text-2xl font-semibold text-yellow-600">{salesReport.pendingItems || 0}</p>
                      <p className="text-xs text-gray-500 mt-1">En proceso</p>
                    </div>
                    <div className="bg-gray-50 p-4 rounded-lg border">
                      <p className="text-sm text-gray-600 mb-1">Ventas Totales</p>
                      <p className="text-2xl font-semibold text-gray-900">{formatPrice(salesReport.totalSales || 0)}</p>
                      <p className="text-xs text-gray-500 mt-1">Todos los items</p>
                    </div>
                  </div>

                  {/* Top Productos */}
                  {salesReport.topProducts && salesReport.topProducts.length > 0 && (
                    <div>
                      <h3 className="text-lg font-semibold mb-4 text-gray-900">Top 5 Productos Más Vendidos</h3>
                      <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-gray-200">
                          <thead className="bg-gray-50">
                            <tr>
                              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Álbum
                              </th>
                              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Artista
                              </th>
                              <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Unidades Vendidas
                              </th>
                              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Ingresos
                              </th>
                            </tr>
                          </thead>
                          <tbody className="bg-white divide-y divide-gray-200">
                            {salesReport.topProducts.map((product: any, index: number) => (
                              <tr key={product.productId} className="hover:bg-gray-50">
                                <td className="px-6 py-4 whitespace-nowrap">
                                  <div className="flex items-center">
                                    <span className="text-lg font-bold text-gray-400 mr-3">#{index + 1}</span>
                                    <span className="font-medium text-gray-900">{product.albumTitle}</span>
                                  </div>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-gray-600">
                                  {product.artistName}
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-center">
                                  <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
                                    {product.quantitySold}
                                  </span>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-right font-semibold text-green-600">
                                  {formatPrice(product.revenue || 0)}
                                </td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    </div>
                  )}

                  {salesReport.topProducts && salesReport.topProducts.length === 0 && (
                    <div className="text-center py-8">
                      <Package className="h-12 w-12 text-gray-300 mx-auto mb-3" />
                      <p className="text-gray-500">No hay productos vendidos aún</p>
                    </div>
                  )}
                </div>
              )}
            </div>
          </div>
        )}
      </div>

      {/* Modal para fecha de envío */}
      {showShipModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
            <h3 className="text-lg font-bold mb-4">Registrar Fecha de Envío</h3>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Fecha y hora de envío
              </label>
              <input
                type="datetime-local"
                value={shipDate}
                onChange={(e) => setShipDate(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
              />
            </div>
            <div className="flex justify-end space-x-3">
              <button
                onClick={() => {
                  setShowShipModal(false);
                  setItemToShip(null);
                  setShipDate('');
                }}
                className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
              >
                Cancelar
              </button>
              <button
                onClick={handleShipItem}
                disabled={!shipDate || processingItem === itemToShip}
                className="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 disabled:opacity-50"
              >
                {processingItem === itemToShip ? 'Enviando...' : 'Confirmar Envío'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProviderDashboard;