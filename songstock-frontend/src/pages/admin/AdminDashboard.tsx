import { useState, useEffect } from 'react';
import { 
  Users, 
  Package, 
  Store,
  TrendingUp,
  DollarSign,
  AlertCircle,
  CheckCircle,
  XCircle,
  Search,
  Filter
} from 'lucide-react';
import adminService from '../../services/admin.service';
import toast from 'react-hot-toast';

const AdminDashboard = () => {
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'overview' | 'providers' | 'users' | 'products'>('overview');
  const [searchTerm, setSearchTerm] = useState('');
  
  // Estados para almacenar los datos del sistema
  const [stats, setStats] = useState({
    totalUsers: 0,
    totalProviders: 0,
    totalProducts: 0,
    totalOrders: 0,
    totalRevenue: 0,
    activeProducts: 0,
    pendingProviders: 0
  });
  
  const [providers, setProviders] = useState<any[]>([]);
  const [users, setUsers] = useState<any[]>([]);
  const [products, setProducts] = useState<any[]>([]);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      // Cargar todas las estadísticas y datos del sistema
      const [statsData, providersData, usersData, productsData] = await Promise.all([
        adminService.getSystemStats(),
        adminService.getAllProviders(),
        adminService.getAllUsers(),
        adminService.getAllProducts()
      ]);

      setStats(statsData);
      setProviders(providersData);
      setUsers(usersData);
      setProducts(productsData);
    } catch (error) {
      console.error('Error loading admin data:', error);
      toast.error('Error al cargar los datos del sistema');
    } finally {
      setLoading(false);
    }
  };

  const handleProviderStatusChange = async (providerId: number, status: string) => {
    try {
      await adminService.updateProviderStatus(providerId, status);
      toast.success('Estado del proveedor actualizado');
      loadData(); // Recargar datos
    } catch (error) {
      console.error('Error updating provider status:', error);
      toast.error('Error al actualizar el estado');
    }
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
    }).format(price);
  };

  const formatDate = (dateArray: number[]) => {
    if (!dateArray || dateArray.length < 3) return 'N/A';
    const [year, month, day] = dateArray;
    return new Date(year, month - 1, day).toLocaleDateString('es-CO');
  };

  // Función para obtener el color del badge según el estado de verificación
  const getStatusBadge = (status: string) => {
    const statusConfig: Record<string, { bg: string; text: string; icon: any }> = {
      'APPROVED': { bg: 'bg-green-100', text: 'text-green-800', icon: CheckCircle },
      'PENDING': { bg: 'bg-yellow-100', text: 'text-yellow-800', icon: AlertCircle },
      'REJECTED': { bg: 'bg-red-100', text: 'text-red-800', icon: XCircle }
    };

    const config = statusConfig[status] || statusConfig['PENDING'];
    const Icon = config.icon;

    return (
      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${config.bg} ${config.text}`}>
        <Icon className="h-3 w-3 mr-1" />
        {status === 'APPROVED' ? 'Aprobado' : status === 'PENDING' ? 'Pendiente' : 'Rechazado'}
      </span>
    );
  };

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
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Panel de Administración</h1>
            <p className="text-gray-600 mt-1">Supervisión y gestión del sistema SongStock</p>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Stats Overview - Siempre visible */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">Total Usuarios</p>
                <p className="text-3xl font-bold text-gray-900">{stats.totalUsers}</p>
              </div>
              <div className="bg-blue-100 p-3 rounded-lg">
                <Users className="h-8 w-8 text-blue-600" />
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">Proveedores</p>
                <p className="text-3xl font-bold text-gray-900">{stats.totalProviders}</p>
                {stats.pendingProviders > 0 && (
                  <p className="text-xs text-yellow-600 mt-1">
                    {stats.pendingProviders} pendientes
                  </p>
                )}
              </div>
              <div className="bg-purple-100 p-3 rounded-lg">
                <Store className="h-8 w-8 text-purple-600" />
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">Total Productos</p>
                <p className="text-3xl font-bold text-gray-900">{stats.totalProducts}</p>
                <p className="text-xs text-green-600 mt-1">
                  {stats.activeProducts} activos
                </p>
              </div>
              <div className="bg-primary-100 p-3 rounded-lg">
                <Package className="h-8 w-8 text-primary-900" />
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">Valor Total Inv.</p>
                <p className="text-2xl font-bold text-gray-900">{formatPrice(stats.totalRevenue)}</p>
              </div>
              <div className="bg-secondary-100 p-3 rounded-lg">
                <DollarSign className="h-8 w-8 text-secondary-600" />
              </div>
            </div>
          </div>
        </div>

        {/* Tabs Navigation */}
        <div className="bg-white rounded-lg shadow-md mb-6">
          <div className="border-b border-gray-200">
            <nav className="flex -mb-px">
              <button
                onClick={() => setActiveTab('overview')}
                className={`py-4 px-6 font-medium text-sm border-b-2 transition ${
                  activeTab === 'overview'
                    ? 'border-primary-900 text-primary-900'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                Vista General
              </button>
              <button
                onClick={() => setActiveTab('providers')}
                className={`py-4 px-6 font-medium text-sm border-b-2 transition ${
                  activeTab === 'providers'
                    ? 'border-primary-900 text-primary-900'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                Proveedores ({providers.length})
              </button>
              <button
                onClick={() => setActiveTab('users')}
                className={`py-4 px-6 font-medium text-sm border-b-2 transition ${
                  activeTab === 'users'
                    ? 'border-primary-900 text-primary-900'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                Usuarios ({users.length})
              </button>
              <button
                onClick={() => setActiveTab('products')}
                className={`py-4 px-6 font-medium text-sm border-b-2 transition ${
                  activeTab === 'products'
                    ? 'border-primary-900 text-primary-900'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                Productos ({products.length})
              </button>
            </nav>
          </div>

          {/* Tab Content */}
          <div className="p-6">
            {/* Overview Tab */}
            {activeTab === 'overview' && (
              <div className="space-y-6">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-4">
                    Resumen del Sistema
                  </h3>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="bg-gray-50 rounded-lg p-6">
                      <h4 className="font-medium text-gray-900 mb-4">Estado de Proveedores</h4>
                      <div className="space-y-3">
                        <div className="flex justify-between items-center">
                          <span className="text-gray-600">Aprobados</span>
                          <span className="font-semibold text-green-600">
                            {providers.filter(p => p.verificationStatus === 'APPROVED').length}
                          </span>
                        </div>
                        <div className="flex justify-between items-center">
                          <span className="text-gray-600">Pendientes</span>
                          <span className="font-semibold text-yellow-600">
                            {providers.filter(p => p.verificationStatus === 'PENDING').length}
                          </span>
                        </div>
                        <div className="flex justify-between items-center">
                          <span className="text-gray-600">Rechazados</span>
                          <span className="font-semibold text-red-600">
                            {providers.filter(p => p.verificationStatus === 'REJECTED').length}
                          </span>
                        </div>
                      </div>
                    </div>

                    <div className="bg-gray-50 rounded-lg p-6">
                      <h4 className="font-medium text-gray-900 mb-4">Distribución de Productos</h4>
                      <div className="space-y-3">
                        <div className="flex justify-between items-center">
                          <span className="text-gray-600">Productos Físicos</span>
                          <span className="font-semibold text-primary-900">
                            {products.filter(p => p.productType === 'PHYSICAL').length}
                          </span>
                        </div>
                        <div className="flex justify-between items-center">
                          <span className="text-gray-600">Productos Digitales</span>
                          <span className="font-semibold text-secondary-600">
                            {products.filter(p => p.productType === 'DIGITAL').length}
                          </span>
                        </div>
                        <div className="flex justify-between items-center">
                          <span className="text-gray-600">Productos Activos</span>
                          <span className="font-semibold text-green-600">
                            {stats.activeProducts}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="bg-blue-50 border border-blue-200 rounded-lg p-6">
                  <div className="flex items-start space-x-3">
                    <AlertCircle className="h-6 w-6 text-blue-600 mt-0.5" />
                    <div>
                      <h4 className="font-medium text-gray-900 mb-2">Información del Sistema</h4>
                      <p className="text-sm text-gray-700">
                        Este es el panel de administración de SongStock. Desde aquí puedes supervisar 
                        todos los aspectos del sistema, gestionar proveedores, usuarios y productos. 
                        Las estadísticas se actualizan en tiempo real basándose en los datos del sistema.
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* Providers Tab */}
            {activeTab === 'providers' && (
              <div>
                <div className="mb-4">
                  <div className="relative">
                    <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <input
                      type="text"
                      placeholder="Buscar proveedores..."
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
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                          Negocio
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                          NIT/RUT
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                          Ubicación
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                          Estado
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                          Fecha Registro
                        </th>
                        <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                          Acciones
                        </th>
                      </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                      {providers
                        .filter(p => 
                          p.businessName.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          p.taxId.toLowerCase().includes(searchTerm.toLowerCase())
                        )
                        .map((provider) => (
                          <tr key={provider.id} className="hover:bg-gray-50">
                            <td className="px-6 py-4">
                              <div className="font-medium text-gray-900">{provider.businessName}</div>
                            </td>
                            <td className="px-6 py-4">
                              <span className="text-sm font-mono text-gray-600">{provider.taxId}</span>
                            </td>
                            <td className="px-6 py-4">
                              <div className="text-sm text-gray-900">{provider.city || 'N/A'}</div>
                              <div className="text-sm text-gray-500">{provider.country || 'N/A'}</div>
                            </td>
                            <td className="px-6 py-4">
                              {getStatusBadge(provider.verificationStatus)}
                            </td>
                            <td className="px-6 py-4">
                              <span className="text-sm text-gray-600">
                                {formatDate(provider.createdAt)}
                              </span>
                            </td>
                            <td className="px-6 py-4 text-right">
                              {provider.verificationStatus === 'PENDING' && (
                                <div className="flex items-center justify-end space-x-2">
                                  <button
                                    onClick={() => handleProviderStatusChange(provider.id, 'APPROVED')}
                                    className="text-green-600 hover:text-green-700 text-sm font-medium"
                                  >
                                    Aprobar
                                  </button>
                                  <button
                                    onClick={() => handleProviderStatusChange(provider.id, 'REJECTED')}
                                    className="text-red-600 hover:text-red-700 text-sm font-medium"
                                  >
                                    Rechazar
                                  </button>
                                </div>
                              )}
                            </td>
                          </tr>
                        ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {/* Users Tab */}
            {activeTab === 'users' && (
              <div>
                <div className="mb-4">
                  <div className="relative">
                    <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <input
                      type="text"
                      placeholder="Buscar usuarios..."
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
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                          Usuario
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                          Email
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                          Rol
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                          Estado
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                          Fecha Registro
                        </th>
                      </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                      {users
                        .filter(u => 
                          u.username?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          u.email?.toLowerCase().includes(searchTerm.toLowerCase())
                        )
                        .map((user) => (
                          <tr key={user.id} className="hover:bg-gray-50">
                            <td className="px-6 py-4">
                              <div className="flex items-center space-x-3">
                                <div className="w-10 h-10 bg-gradient-to-br from-primary-100 to-secondary-100 rounded-full flex items-center justify-center">
                                  <Users className="h-5 w-5 text-primary-600" />
                                </div>
                                <div>
                                  <div className="font-medium text-gray-900">
                                    {user.firstName} {user.lastName}
                                  </div>
                                  <div className="text-sm text-gray-500">@{user.username}</div>
                                </div>
                              </div>
                            </td>
                            <td className="px-6 py-4">
                              <span className="text-sm text-gray-600">{user.email}</span>
                            </td>
                            <td className="px-6 py-4">
                              <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                                user.role === 'ADMIN' 
                                  ? 'bg-purple-100 text-purple-800'
                                  : user.role === 'PROVIDER'
                                  ? 'bg-blue-100 text-blue-800'
                                  : 'bg-gray-100 text-gray-800'
                              }`}>
                                {user.role}
                              </span>
                            </td>
                            <td className="px-6 py-4">
                              <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                                user.isActive
                                  ? 'bg-green-100 text-green-800'
                                  : 'bg-gray-100 text-gray-800'
                              }`}>
                                {user.isActive ? 'Activo' : 'Inactivo'}
                              </span>
                            </td>
                            <td className="px-6 py-4">
                              <span className="text-sm text-gray-600">
                                {formatDate(user.createdAt)}
                              </span>
                            </td>
                          </tr>
                        ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {/* Products Tab */}
            {activeTab === 'products' && (
              <div>
                <div className="mb-4">
                  <div className="relative">
                    <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <input
                      type="text"
                      placeholder="Buscar productos..."
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
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                          Producto
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                          Proveedor
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                          Categoría
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                          Tipo
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                          Precio
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                          Stock
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                          Estado
                        </th>
                      </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                      {products
                        .filter(p => 
                          p.albumTitle?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          p.artistName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          p.sku?.toLowerCase().includes(searchTerm.toLowerCase())
                        )
                        .map((product) => (
                          <tr key={product.id} className="hover:bg-gray-50">
                            <td className="px-6 py-4">
                              <div>
                                <div className="font-medium text-gray-900">{product.albumTitle}</div>
                                <div className="text-sm text-gray-500">{product.artistName}</div>
                              </div>
                            </td>
                            <td className="px-6 py-4">
                              <span className="text-sm text-gray-900">{product.providerName}</span>
                            </td>
                            <td className="px-6 py-4">
                              <span className="text-sm text-gray-600">{product.categoryName}</span>
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
                          </tr>
                        ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;