import { useState, useEffect } from 'react';
import { 
  Users, 
  Package, 
  Store,
  AlertCircle,
  CheckCircle,
  XCircle,
  Search,
  Plus,
  Edit,
  Trash2,
  X,
  Power,
  ExternalLink,
  Eye,
  Disc3,
  Music,
  Filter
} from 'lucide-react';
import { Link } from 'react-router-dom';
import adminService from '../../services/admin.service';
import toast from 'react-hot-toast';

// ==================== INTERFACES ====================

interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  role: 'ADMIN' | 'PROVIDER' | 'CUSTOMER';
  isActive: boolean;
  createdAt: any;
  providerVerificationStatus?: string;
}

interface UserFormData {
  username: string;
  email: string;
  password?: string;
  firstName: string;
  lastName: string;
  phone: string;
  role: 'ADMIN' | 'PROVIDER' | 'CUSTOMER';
  isActive: boolean;
}

interface Product {
  id: number;
  sku: string;
  albumTitle: string;
  artistName: string;
  productType: 'PHYSICAL' | 'DIGITAL';
  price: number;
  stockQuantity: number;
  isActive: boolean;
  providerName?: string;
  providerId?: number;
  categoryName?: string;
  conditionType?: string;
  vinylSize?: string;
  vinylSpeed?: string;
  fileFormat?: string;
}

const AdminDashboard = () => {
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'overview' | 'providers' | 'users' | 'products'>('overview');
  const [searchTerm, setSearchTerm] = useState('');
  
  // Estados para los datos
  const [stats, setStats] = useState({
    totalUsers: 0,
    totalProviders: 0,
    totalProducts: 0,
    totalOrders: 0,
    totalRevenue: 0,
    activeProducts: 0,
    pendingProviders: 0
  });
  
  const [providers, setProvidersData] = useState<any[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [products, setProducts] = useState<Product[]>([]);

  // Estados para filtros de productos
  const [productTypeFilter, setProductTypeFilter] = useState<'ALL' | 'PHYSICAL' | 'DIGITAL'>('ALL');
  const [activeOnlyFilter, setActiveOnlyFilter] = useState(false);

  // Estados para modales
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [showProductDetailModal, setShowProductDetailModal] = useState(false);
  const [showDeleteProductModal, setShowDeleteProductModal] = useState(false);
  
 // Estados para gestión de productos
  const [showCreateProductModal, setShowCreateProductModal] = useState(false);
  const [showEditProductModal, setShowEditProductModal] = useState(false);
  const [productFormData, setProductFormData] = useState<any>({});
  
  // Estado para el formulario
  const [formData, setFormData] = useState<UserFormData>({
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    phone: '',
    role: 'CUSTOMER',
    isActive: true
  });

    // Estados para datos de catálogo
    const [categories, setCategories] = useState<any[]>([]);
    const [albums, setAlbums] = useState<any[]>([]);
    const [allProviders, setAllProviders] = useState<any[]>([]);
  // Cargar datos al montar el componente
  useEffect(() => {
    loadData();
    loadCatalogData();
  }, []);

  const loadCatalogData = async () => {
    try {
      const [categoriesData, albumsData, providersData] = await Promise.all([
        adminService.getAllCategories(),
        adminService.getAllAlbums(),
        adminService.getAllProviders()
      ]);
      
      setCategories(categoriesData);
      setAlbums(albumsData);
      setAllProviders(providersData.filter((p: any) => p.verificationStatus === 'VERIFIED'));
    } catch (error) {
      console.error('Error loading catalog data:', error);
    }
  };



  const loadData = async () => {
    setLoading(true);
    try {
      const [statsData, providersData, usersData, productsData] = await Promise.all([
        adminService.getSystemStats(),
        adminService.getAllProviders(),
        adminService.getAllUsers(),
        adminService.getAllProducts()
      ]);

      setStats(statsData);
      setProvidersData(providersData);
      setUsers(usersData);
      setProducts(productsData);
    } catch (error) {
      console.error('Error loading admin data:', error);
      toast.error('Error al cargar los datos del sistema');
    } finally {
      setLoading(false);
    }
  };

  // ==================== HANDLERS CRUD USUARIOS ====================

  const handleCreateUser = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.password || formData.password.length < 6) {
      toast.error('La contraseña debe tener al menos 6 caracteres');
      return;
    }

    try {
      await adminService.createUser({
        username: formData.username,
        email: formData.email,
        password: formData.password!,
        firstName: formData.firstName,
        lastName: formData.lastName,
        phone: formData.phone,
        role: formData.role
      });

      toast.success('Usuario creado exitosamente');
      setShowCreateModal(false);
      resetForm();
      loadData();
    } catch (error: any) {
      console.error('Error creating user:', error);
      const errorMessage = error.response?.data?.message || 'Error al crear el usuario';
      toast.error(errorMessage);
    }
  };

  const handleEditUser = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!selectedUser) return;

    try {
      await adminService.updateUser(selectedUser.id, {
        firstName: formData.firstName,
        lastName: formData.lastName,
        username: formData.username,
        email: formData.email,
        phone: formData.phone,
        role: formData.role,
        isActive: formData.isActive,
        updateReason: 'Actualización desde dashboard administrativo'
      });

      toast.success('Usuario actualizado exitosamente');
      setShowEditModal(false);
      setSelectedUser(null);
      resetForm();
      loadData();
    } catch (error: any) {
      console.error('Error updating user:', error);
      const errorMessage = error.response?.data?.message || 'Error al actualizar el usuario';
      toast.error(errorMessage);
    }
  };

  const handleToggleUserStatus = async (userId: number) => {
    try {
      await adminService.toggleUserStatus(userId);
      toast.success('Estado del usuario actualizado');
      loadData();
    } catch (error: any) {
      console.error('Error toggling user status:', error);
      const errorMessage = error.response?.data?.message || 'Error al cambiar el estado';
      toast.error(errorMessage);
    }
  };

  const handleDeleteUser = async () => {
    if (!selectedUser) return;

    try {
      await adminService.deleteUser(
        selectedUser.id, 
        'Eliminación desde dashboard administrativo'
      );

      toast.success('Usuario eliminado exitosamente');
      setShowDeleteModal(false);
      setSelectedUser(null);
      loadData();
    } catch (error: any) {
      console.error('Error deleting user:', error);
      const errorMessage = error.response?.data?.message || 'Error al eliminar el usuario';
      toast.error(errorMessage);
    }
  };

  const handleProviderStatusChange = async (providerId: number, status: string) => {
    try {
      await adminService.updateProviderStatus(providerId, status);
      toast.success('Estado del proveedor actualizado');
      loadData();
    } catch (error) {
      console.error('Error updating provider status:', error);
      toast.error('Error al actualizar el estado');
    }
  };

  // ==================== HANDLERS PRODUCTOS ====================

  const handleViewProductDetail = (product: Product) => {
    setSelectedProduct(product);
    setShowProductDetailModal(true);
  };

  const handleDeleteProduct = async () => {
    if (!selectedProduct) return;

    try {
      await adminService.deleteProduct(selectedProduct.id);
      toast.success('Producto eliminado exitosamente');
      setShowDeleteProductModal(false);
      setSelectedProduct(null);
      loadData();
    } catch (error: any) {
      console.error('Error deleting product:', error);
      const errorMessage = error.response?.data?.message || 'Error al eliminar el producto';
      toast.error(errorMessage);
    }
  };

  const openDeleteProductModal = (product: Product) => {
    setSelectedProduct(product);
    setShowDeleteProductModal(true);
  };
  const handleCreateProduct = async (e: React.FormEvent) => {
    e.preventDefault();
    
    try {
      await adminService.createProduct({
        albumId: parseInt(productFormData.albumId),
        providerId: parseInt(productFormData.providerId),
        categoryId: parseInt(productFormData.categoryId),
        sku: productFormData.sku,
        productType: productFormData.productType,
        conditionType: productFormData.conditionType || 'NEW',
        price: parseFloat(productFormData.price),
        stockQuantity: parseInt(productFormData.stockQuantity),
        featured: productFormData.featured || false,
        vinylSize: productFormData.vinylSize,
        vinylSpeed: productFormData.vinylSpeed,
        fileFormat: productFormData.fileFormat,
        fileSizeMB: productFormData.fileSizeMB ? parseFloat(productFormData.fileSizeMB) : null,
      });

      toast.success('Producto creado exitosamente');
      setShowCreateProductModal(false);
      setProductFormData({});
      loadData();
    } catch (error: any) {
      console.error('Error creating product:', error);
      const errorMessage = error.response?.data?.message || 'Error al crear el producto';
      toast.error(errorMessage);
    }
  };

  const handleEditProduct = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!selectedProduct) return;

    try {
      await adminService.updateProduct(selectedProduct.id, {
        categoryId: parseInt(productFormData.categoryId),
        providerId: parseInt(productFormData.providerId),
        sku: productFormData.sku,
        price: parseFloat(productFormData.price),
        stockQuantity: parseInt(productFormData.stockQuantity),
        featured: productFormData.featured,
        conditionType: productFormData.conditionType,
        vinylSize: productFormData.vinylSize,
        vinylSpeed: productFormData.vinylSpeed,
        fileFormat: productFormData.fileFormat,
        fileSizeMB: productFormData.fileSizeMB ? parseFloat(productFormData.fileSizeMB) : null,
      });

      toast.success('Producto actualizado exitosamente');
      setShowEditProductModal(false);
      setSelectedProduct(null);
      setProductFormData({});
      loadData();
    } catch (error: any) {
      console.error('Error updating product:', error);
      const errorMessage = error.response?.data?.message || 'Error al actualizar el producto';
      toast.error(errorMessage);
    }
  };

  const openCreateProductModal = () => {
    setProductFormData({
      productType: 'PHYSICAL',
      conditionType: 'NEW',
      featured: false,
      stockQuantity: 0,
    });
    setShowCreateProductModal(true);
  };

  const openEditProductModal = async (product: Product) => {
    setSelectedProduct(product);
    setProductFormData({ ...product });
    setShowEditProductModal(true);
  };



  // ==================== UTILIDADES ====================

  const resetForm = () => {
    setFormData({
      username: '',
      email: '',
      password: '',
      firstName: '',
      lastName: '',
      phone: '',
      role: 'CUSTOMER',
      isActive: true
    });
  };

  const openEditModal = (user: User) => {
    setSelectedUser(user);
    setFormData({
      username: user.username,
      email: user.email,
      password: '',
      firstName: user.firstName,
      lastName: user.lastName,
      phone: user.phone || '',
      role: user.role,
      isActive: user.isActive
    });
    setShowEditModal(true);
  };

  const openDeleteModal = (user: User) => {
    setSelectedUser(user);
    setShowDeleteModal(true);
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
    }).format(price);
  };

  const getStatusBadge = (status: string) => {
    const statusConfig: Record<string, { bg: string; text: string; label: string }> = {
      'PENDING': { bg: 'bg-yellow-100', text: 'text-yellow-800', label: 'Pendiente' },
      'APPROVED': { bg: 'bg-green-100', text: 'text-green-800', label: 'Aprobado' },
      'REJECTED': { bg: 'bg-red-100', text: 'text-red-800', label: 'Rechazado' }
    };

    const config = statusConfig[status] || statusConfig['PENDING'];

    return (
      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${config.bg} ${config.text}`}>
        {config.label}
      </span>
    );
  };

  const getRoleBadge = (role: string) => {
    const roleConfig: Record<string, { bg: string; text: string }> = {
      'ADMIN': { bg: 'bg-purple-100', text: 'text-purple-800' },
      'PROVIDER': { bg: 'bg-blue-100', text: 'text-blue-800' },
      'CUSTOMER': { bg: 'bg-gray-100', text: 'text-gray-800' }
    };

    const config = roleConfig[role] || roleConfig['CUSTOMER'];

    return (
      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${config.bg} ${config.text}`}>
        {role}
      </span>
    );
  };

  // Filtrar usuarios según búsqueda
  const filteredUsers = users.filter(u => 
    u.username?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    u.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    u.firstName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    u.lastName?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Filtrar productos según búsqueda y filtros
  const filteredProducts = products.filter(p => {
    const matchesSearch = 
      p.albumTitle?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.artistName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.sku?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.providerName?.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesType = productTypeFilter === 'ALL' || p.productType === productTypeFilter;
    const matchesActive = !activeOnlyFilter || p.isActive;

    return matchesSearch && matchesType && matchesActive;
  });

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
        {/* Stats Overview */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">Total Usuarios</p>
                <p className="text-3xl font-bold text-gray-900">{stats.totalUsers}</p>
              </div>
              <div className="bg-blue-100 p-3 rounded-full">
                <Users className="h-6 w-6 text-blue-600" />
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">Proveedores</p>
                <p className="text-3xl font-bold text-gray-900">{stats.totalProviders}</p>
              </div>
              <div className="bg-green-100 p-3 rounded-full">
                <Store className="h-6 w-6 text-green-600" />
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">Productos</p>
                <p className="text-3xl font-bold text-gray-900">{stats.totalProducts}</p>
              </div>
              <div className="bg-purple-100 p-3 rounded-full">
                <Package className="h-6 w-6 text-purple-600" />
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">Proveedores Pendientes</p>
                <p className="text-3xl font-bold text-gray-900">{stats.pendingProviders}</p>
              </div>
              <div className="bg-yellow-100 p-3 rounded-full">
                <AlertCircle className="h-6 w-6 text-yellow-600" />
              </div>
            </div>
          </div>
        </div>

        {/* Tabs Navigation */}
        <div className="bg-white rounded-lg shadow-md mb-8">
          <div className="border-b border-gray-200">
            <nav className="flex -mb-px">
              {[
                { id: 'overview', label: 'Vista General' },
                { id: 'users', label: 'Usuarios' },
                { id: 'providers', label: 'Proveedores' },
                { id: 'products', label: 'Productos' }
              ].map(tab => (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id as any)}
                  className={`px-6 py-4 text-sm font-medium border-b-2 transition-colors ${
                    activeTab === tab.id
                      ? 'border-primary-600 text-primary-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }`}
                >
                  {tab.label}
                </button>
              ))}
            </nav>
          </div>

          <div className="p-6">
            {/* ==================== PRODUCTS TAB (NUEVO) ==================== */}
            {activeTab === 'products' && (
              <div>
                {/* Barra de búsqueda y filtros */}
                <div className="mb-6 space-y-4">
                  {/* Botón Crear Producto */}
                  <div className="flex justify-between items-center">
                    <h3 className="text-lg font-semibold text-gray-900">Catálogo de Productos</h3>
                    <button
  onClick={openCreateProductModal}
  className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg flex items-center gap-2 transition-colors"
>
  <Plus className="w-4 h-4" />
  Crear Producto
</button>
                  </div>

                  <div className="flex flex-col md:flex-row gap-4">
                    {/* Búsqueda */}
                    <div className="relative flex-1">
                      <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                      <input
                        type="text"
                        placeholder="Buscar por álbum, artista, SKU o proveedor..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                      />
                    </div>

                    {/* Filtro por tipo */}
                    <select
                      value={productTypeFilter}
                      onChange={(e) => setProductTypeFilter(e.target.value as any)}
                      className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    >
                      <option value="ALL">Todos los tipos</option>
                      <option value="PHYSICAL">Solo Vinilos</option>
                      <option value="DIGITAL">Solo Digitales</option>
                    </select>

                    {/* Filtro activos */}
                    <label className="flex items-center space-x-2 px-4 py-2 border border-gray-300 rounded-lg cursor-pointer hover:bg-gray-50">
                      <input
                        type="checkbox"
                        checked={activeOnlyFilter}
                        onChange={(e) => setActiveOnlyFilter(e.target.checked)}
                        className="rounded text-primary-600 focus:ring-primary-500"
                      />
                      <span className="text-sm text-gray-700">Solo activos</span>
                    </label>
                  </div>

                  {/* Estadísticas de filtros */}
                  <div className="flex items-center justify-between text-sm text-gray-600">
                    <p>
                      Mostrando {filteredProducts.length} de {products.length} productos
                    </p>
                    {(searchTerm || productTypeFilter !== 'ALL' || activeOnlyFilter) && (
                      <button
                        onClick={() => {
                          setSearchTerm('');
                          setProductTypeFilter('ALL');
                          setActiveOnlyFilter(false);
                        }}
                        className="text-primary-600 hover:text-primary-700 font-medium"
                      >
                        Limpiar filtros
                      </button>
                    )}
                  </div>
                </div>

                {/* Tabla de productos */}
                <div className="overflow-x-auto">
                  <table className="w-full">
                    <thead className="bg-gray-50 border-b">
                      <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Producto
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Proveedor
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
                              {searchTerm || productTypeFilter !== 'ALL' || activeOnlyFilter
                                ? 'No se encontraron productos con los filtros aplicados'
                                : 'No hay productos registrados'}
                            </p>
                          </td>
                        </tr>
                      ) : (
                        filteredProducts.map((product) => (
                          <tr key={product.id} className="hover:bg-gray-50 transition">
                            <td className="px-6 py-4">
                              <div className="flex items-center space-x-3">
                                <div className={`w-12 h-12 rounded-lg flex items-center justify-center flex-shrink-0 ${
                                  product.productType === 'PHYSICAL' 
                                    ? 'bg-primary-100' 
                                    : 'bg-secondary-100'
                                }`}>
                                  {product.productType === 'PHYSICAL' ? (
                                    <Disc3 className="h-6 w-6 text-primary-600" />
                                  ) : (
                                    <Music className="h-6 w-6 text-secondary-600" />
                                  )}
                                </div>
                                <div>
                                  <div className="font-medium text-gray-900">{product.albumTitle}</div>
                                  <div className="text-sm text-gray-500">{product.artistName}</div>
                                  <div className="text-xs text-gray-400 font-mono mt-1">SKU: {product.sku}</div>
                                </div>
                              </div>
                            </td>
                            <td className="px-6 py-4">
                              <div>
                                <div className="text-sm font-medium text-gray-900">
                                  {product.providerName || 'N/A'}
                                </div>
                                {product.providerId && (
                                  <div className="text-xs text-gray-500">ID: {product.providerId}</div>
                                )}
                              </div>
                            </td>
                            <td className="px-6 py-4">
                              <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                                product.productType === 'PHYSICAL'
                                  ? 'bg-primary-100 text-primary-800'
                                  : 'bg-secondary-100 text-secondary-800'
                              }`}>
                                {product.productType === 'PHYSICAL' ? 'Vinilo' : 'Digital'}
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
                                  : 'bg-red-100 text-red-800'
                              }`}>
                                {product.isActive ? 'Activo' : 'Inactivo'}
                              </span>
                            </td>
                            <td className="px-6 py-4 text-right">
                              <div className="flex items-center justify-end space-x-2">
                                <button
                                  onClick={() => handleViewProductDetail(product)}
                                  className="text-blue-600 hover:bg-blue-50 p-2 rounded-lg transition-colors"
                                  title="Ver detalles"
                                >
                                  <Eye className="h-4 w-4" />
                                </button>
                                <Link
                                  to={`/product/${product.id}`}
                                  target="_blank"
                                  className="text-gray-600 hover:bg-gray-100 p-2 rounded-lg transition-colors"
                                  title="Ver en tienda"
                                >
                                  <ExternalLink className="h-4 w-4" />
                                </Link>
                                <button
                                onClick={() => openEditProductModal(product)}
                                                        className="text-blue-600 hover:text-blue-800 p-1"
                                                        title="Editar producto"
                                                      >
                                                        <Edit className="w-4 h-4" />
                                                      </button>
                                                      <button
                                  onClick={() => openDeleteProductModal(product)}
                                  className="text-red-600 hover:bg-red-50 p-2 rounded-lg transition-colors"
                                  title="Eliminar"
                                >
                                  <Trash2 className="h-4 w-4" />
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

            {/* ==================== USERS TAB ==================== */}
            {activeTab === 'users' && (
              <div>
                <div className="flex justify-between items-center mb-6">
                  <div className="relative flex-1 max-w-md">
                    <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <input
                      type="text"
                      placeholder="Buscar usuarios..."
                      value={searchTerm}
                      onChange={(e) => setSearchTerm(e.target.value)}
                      className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    />
                  </div>
                  <button
                    onClick={() => {
                      resetForm();
                      setShowCreateModal(true);
                    }}
                    className="ml-4 bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-lg font-medium transition-colors flex items-center space-x-2"
                  >
                    <Plus className="h-5 w-5" />
                    <span>Crear Usuario</span>
                  </button>
                </div>

                <div className="overflow-x-auto">
                  <table className="w-full">
                    <thead className="bg-gray-50">
                      <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Usuario</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Rol</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Estado</th>
                        <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Acciones</th>
                      </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                      {filteredUsers.map((user) => (
                        <tr key={user.id} className="hover:bg-gray-50">
                          <td className="px-6 py-4">
                            <div>
                              <div className="font-medium text-gray-900">{user.username}</div>
                              <div className="text-sm text-gray-500">{user.firstName} {user.lastName}</div>
                            </div>
                          </td>
                          <td className="px-6 py-4 text-sm text-gray-900">{user.email}</td>
                          <td className="px-6 py-4">{getRoleBadge(user.role)}</td>
                          <td className="px-6 py-4">
                            <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                              user.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                            }`}>
                              {user.isActive ? 'Activo' : 'Inactivo'}
                            </span>
                          </td>
                          <td className="px-6 py-4 text-right">
                            <div className="flex items-center justify-end space-x-2">
                              <button
                                onClick={() => handleToggleUserStatus(user.id)}
                                className={`p-2 rounded-lg transition-colors ${
                                  user.isActive
                                    ? 'text-gray-600 hover:bg-gray-100'
                                    : 'text-green-600 hover:bg-green-50'
                                }`}
                                title={user.isActive ? 'Desactivar' : 'Activar'}
                              >
                                <Power className="h-4 w-4" />
                              </button>
                              <button
                                onClick={() => openEditModal(user)}
                                className="text-blue-600 hover:bg-blue-50 p-2 rounded-lg transition-colors"
                                title="Editar"
                              >
                                <Edit className="h-4 w-4" />
                              </button>
                              <button
                                onClick={() => openDeleteModal(user)}
                                className="text-red-600 hover:bg-red-50 p-2 rounded-lg transition-colors"
                                title="Eliminar"
                              >
                                <Trash2 className="h-4 w-4" />
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>

                  {filteredUsers.length === 0 && (
                    <div className="text-center py-12">
                      <Users className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                      <p className="text-gray-500">No se encontraron usuarios</p>
                    </div>
                  )}
                </div>
              </div>
            )}

            {/* ==================== OVERVIEW TAB ==================== */}
            {activeTab === 'overview' && (
              <div className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="bg-gray-50 rounded-lg p-6">
                    <h4 className="font-medium text-gray-900 mb-4">Distribución de Usuarios</h4>
                    <div className="space-y-3">
                      <div className="flex justify-between items-center">
                        <span className="text-gray-600">Administradores</span>
                        <span className="font-semibold">{users.filter(u => u.role === 'ADMIN').length}</span>
                      </div>
                      <div className="flex justify-between items-center">
                        <span className="text-gray-600">Proveedores</span>
                        <span className="font-semibold">{users.filter(u => u.role === 'PROVIDER').length}</span>
                      </div>
                      <div className="flex justify-between items-center">
                        <span className="text-gray-600">Clientes</span>
                        <span className="font-semibold">{users.filter(u => u.role === 'CUSTOMER').length}</span>
                      </div>
                    </div>
                  </div>

                  <div className="bg-gray-50 rounded-lg p-6">
                    <h4 className="font-medium text-gray-900 mb-4">Distribución de Productos</h4>
                    <div className="space-y-3">
                      <div className="flex justify-between items-center">
                        <span className="text-gray-600">Vinilos Físicos</span>
                        <span className="font-semibold">{products.filter(p => p.productType === 'PHYSICAL').length}</span>
                      </div>
                      <div className="flex justify-between items-center">
                        <span className="text-gray-600">Productos Digitales</span>
                        <span className="font-semibold">{products.filter(p => p.productType === 'DIGITAL').length}</span>
                      </div>
                      <div className="flex justify-between items-center">
                        <span className="text-gray-600">Productos Activos</span>
                        <span className="font-semibold text-green-600">{products.filter(p => p.isActive).length}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* ==================== PROVIDERS TAB ==================== */}
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
                    <thead className="bg-gray-50">
                      <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Proveedor</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Estado</th>
                        <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Acciones</th>
                      </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                      {providers
                        .filter(p => 
                          p.firstName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          p.email?.toLowerCase().includes(searchTerm.toLowerCase())
                        )
                        .map((provider) => (
                          <tr key={provider.id} className="hover:bg-gray-50">
                            <td className="px-6 py-4">
                              <div>
                                <div className="font-medium text-gray-900">
                                  {provider.firstName} {provider.lastName}
                                </div>
                                <div className="text-sm text-gray-500">{provider.username}</div>
                              </div>
                            </td>
                            <td className="px-6 py-4 text-sm text-gray-900">{provider.email}</td>
                            <td className="px-6 py-4">
                              {getStatusBadge(provider.providerVerificationStatus || 'PENDING')}
                            </td>
                            <td className="px-6 py-4 text-right">
                              {provider.providerVerificationStatus === 'PENDING' && (
                                <div className="flex items-center justify-end space-x-2">
                                  <button
                                    onClick={() => handleProviderStatusChange(provider.id, 'APPROVED')}
                                    className="bg-green-100 text-green-700 hover:bg-green-200 px-3 py-1 rounded-lg text-sm font-medium transition-colors"
                                  >
                                    Aprobar
                                  </button>
                                  <button
                                    onClick={() => handleProviderStatusChange(provider.id, 'REJECTED')}
                                    className="bg-red-100 text-red-700 hover:bg-red-200 px-3 py-1 rounded-lg text-sm font-medium transition-colors"
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
          </div>
        </div>
      </div>

      {/* ==================== MODAL DE DETALLE DE PRODUCTO ==================== */}
      {showProductDetailModal && selectedProduct && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-2xl font-bold text-gray-900">Detalle del Producto</h3>
                <button
                  onClick={() => setShowProductDetailModal(false)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <X className="h-6 w-6" />
                </button>
              </div>

              <div className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="text-sm font-medium text-gray-500">Álbum</label>
                    <p className="text-base text-gray-900 font-medium">{selectedProduct.albumTitle}</p>
                  </div>
                  <div>
                    <label className="text-sm font-medium text-gray-500">Artista</label>
                    <p className="text-base text-gray-900">{selectedProduct.artistName}</p>
                  </div>
                  <div>
                    <label className="text-sm font-medium text-gray-500">SKU</label>
                    <p className="text-base text-gray-900 font-mono">{selectedProduct.sku}</p>
                  </div>
                  <div>
                    <label className="text-sm font-medium text-gray-500">Tipo de Producto</label>
                    <p className="text-base text-gray-900">
                      {selectedProduct.productType === 'PHYSICAL' ? 'Vinilo Físico' : 'Digital MP3'}
                    </p>
                  </div>
                  <div>
                    <label className="text-sm font-medium text-gray-500">Precio</label>
                    <p className="text-base text-gray-900 font-semibold">{formatPrice(selectedProduct.price)}</p>
                  </div>
                  <div>
                    <label className="text-sm font-medium text-gray-500">Stock</label>
                    <p className="text-base text-gray-900">{selectedProduct.stockQuantity} unidades</p>
                  </div>
                  <div>
                    <label className="text-sm font-medium text-gray-500">Proveedor</label>
                    <p className="text-base text-gray-900">{selectedProduct.providerName || 'N/A'}</p>
                  </div>
                  <div>
                    <label className="text-sm font-medium text-gray-500">Categoría</label>
                    <p className="text-base text-gray-900">{selectedProduct.categoryName || 'N/A'}</p>
                  </div>
                  
                  {selectedProduct.productType === 'PHYSICAL' && (
                    <>
                      {selectedProduct.vinylSize && (
                        <div>
                          <label className="text-sm font-medium text-gray-500">Tamaño</label>
                          <p className="text-base text-gray-900">{selectedProduct.vinylSize}</p>
                        </div>
                      )}
                      {selectedProduct.vinylSpeed && (
                        <div>
                          <label className="text-sm font-medium text-gray-500">Velocidad</label>
                          <p className="text-base text-gray-900">{selectedProduct.vinylSpeed}</p>
                        </div>
                      )}
                      {selectedProduct.conditionType && (
                        <div>
                          <label className="text-sm font-medium text-gray-500">Condición</label>
                          <p className="text-base text-gray-900">{selectedProduct.conditionType}</p>
                        </div>
                      )}
                    </>
                  )}

                  {selectedProduct.productType === 'DIGITAL' && selectedProduct.fileFormat && (
                    <div>
                      <label className="text-sm font-medium text-gray-500">Formato de Archivo</label>
                      <p className="text-base text-gray-900">{selectedProduct.fileFormat}</p>
                    </div>
                  )}

                  <div>
                    <label className="text-sm font-medium text-gray-500">Estado</label>
                    <p className={`text-base font-semibold ${selectedProduct.isActive ? 'text-green-600' : 'text-red-600'}`}>
                      {selectedProduct.isActive ? 'Activo' : 'Inactivo'}
                    </p>
                  </div>
                </div>

                <div className="pt-4 border-t flex justify-end space-x-3">
                  <Link
                    to={`/product/${selectedProduct.id}`}
                    target="_blank"
                    className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors flex items-center space-x-2"
                  >
                    <ExternalLink className="h-4 w-4" />
                    <span>Ver en Tienda</span>
                  </Link>
                  <button
                    onClick={() => setShowProductDetailModal(false)}
                    className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
                  >
                    Cerrar
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* ==================== MODAL DE ELIMINACIÓN DE PRODUCTO ==================== */}
      {showDeleteProductModal && selectedProduct && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg max-w-md w-full p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-xl font-bold text-gray-900">Eliminar Producto</h3>
              <button
                onClick={() => setShowDeleteProductModal(false)}
                className="text-gray-400 hover:text-gray-600"
              >
                <X className="h-6 w-6" />
              </button>
            </div>

            <div className="mb-6">
              <p className="text-gray-600 mb-4">
                ¿Estás seguro de que deseas eliminar este producto?
              </p>
              <div className="bg-gray-50 rounded-lg p-4">
                <p className="font-medium text-gray-900">{selectedProduct.albumTitle}</p>
                <p className="text-sm text-gray-600">{selectedProduct.artistName}</p>
                <p className="text-xs text-gray-500 font-mono mt-2">SKU: {selectedProduct.sku}</p>
              </div>
              <div className="mt-4 bg-yellow-50 border border-yellow-200 rounded-lg p-4">
                <p className="text-sm text-yellow-800">
                  <strong>Advertencia:</strong> Esta acción no se puede deshacer.
                </p>
              </div>
            </div>

            <div className="flex justify-end space-x-3">
              <button
                onClick={() => setShowDeleteProductModal(false)}
                className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
              >
                Cancelar
              </button>
              <button
                onClick={handleDeleteProduct}
                className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
              >
                Eliminar
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ==================== MODAL DE CREAR USUARIO ==================== */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-2xl font-bold text-gray-900">Crear Nuevo Usuario</h3>
                <button
                  onClick={() => setShowCreateModal(false)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <X className="h-6 w-6" />
                </button>
              </div>

              <form onSubmit={handleCreateUser} className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Nombre de Usuario *
                    </label>
                    <input
                      type="text"
                      required
                      value={formData.username}
                      onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                      placeholder="usuario123"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Email *
                    </label>
                    <input
                      type="email"
                      required
                      value={formData.email}
                      onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                      placeholder="usuario@example.com"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Nombre *
                    </label>
                    <input
                      type="text"
                      required
                      value={formData.firstName}
                      onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                      placeholder="Juan"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Apellido *
                    </label>
                    <input
                      type="text"
                      required
                      value={formData.lastName}
                      onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                      placeholder="Pérez"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Teléfono
                    </label>
                    <input
                      type="tel"
                      value={formData.phone}
                      onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                      placeholder="+57 300 123 4567"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Rol *
                    </label>
                    <select
                      required
                      value={formData.role}
                      onChange={(e) => setFormData({ ...formData, role: e.target.value as any })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    >
                      <option value="CUSTOMER">Cliente</option>
                      <option value="PROVIDER">Proveedor</option>
                      <option value="ADMIN">Administrador</option>
                    </select>
                  </div>

                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Contraseña *
                    </label>
                    <input
                      type="password"
                      required
                      value={formData.password}
                      onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                      placeholder="Mínimo 6 caracteres"
                      minLength={6}
                    />
                    <p className="text-xs text-gray-500 mt-1">
                      La contraseña debe tener al menos 6 caracteres
                    </p>
                  </div>
                </div>

                <div className="flex items-center space-x-2 pt-4 border-t">
                  <input
                    type="checkbox"
                    id="isActiveCreate"
                    checked={formData.isActive}
                    onChange={(e) => setFormData({ ...formData, isActive: e.target.checked })}
                    className="rounded text-primary-600 focus:ring-primary-500"
                  />
                  <label htmlFor="isActiveCreate" className="text-sm text-gray-700">
                    Usuario activo
                  </label>
                </div>

                <div className="flex justify-end space-x-3 pt-4 border-t">
                  <button
                    type="button"
                    onClick={() => setShowCreateModal(false)}
                    className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors flex items-center space-x-2"
                  >
                    <Plus className="h-4 w-4" />
                    <span>Crear Usuario</span>
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {/* ==================== MODAL DE EDITAR USUARIO ==================== */}
      {showEditModal && selectedUser && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-2xl font-bold text-gray-900">Editar Usuario</h3>
                <button
                  onClick={() => setShowEditModal(false)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <X className="h-6 w-6" />
                </button>
              </div>

              <form onSubmit={handleEditUser} className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Nombre de Usuario *
                    </label>
                    <input
                      type="text"
                      required
                      value={formData.username}
                      onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Email *
                    </label>
                    <input
                      type="email"
                      required
                      value={formData.email}
                      onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Nombre *
                    </label>
                    <input
                      type="text"
                      required
                      value={formData.firstName}
                      onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Apellido *
                    </label>
                    <input
                      type="text"
                      required
                      value={formData.lastName}
                      onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Teléfono
                    </label>
                    <input
                      type="tel"
                      value={formData.phone}
                      onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Rol *
                    </label>
                    <select
                      required
                      value={formData.role}
                      onChange={(e) => setFormData({ ...formData, role: e.target.value as any })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    >
                      <option value="CUSTOMER">Cliente</option>
                      <option value="PROVIDER">Proveedor</option>
                      <option value="ADMIN">Administrador</option>
                    </select>
                  </div>
                </div>

                <div className="flex items-center space-x-2 pt-4 border-t">
                  <input
                    type="checkbox"
                    id="isActiveEdit"
                    checked={formData.isActive}
                    onChange={(e) => setFormData({ ...formData, isActive: e.target.checked })}
                    className="rounded text-primary-600 focus:ring-primary-500"
                  />
                  <label htmlFor="isActiveEdit" className="text-sm text-gray-700">
                    Usuario activo
                  </label>
                </div>

                <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                  <p className="text-sm text-blue-800">
                    <strong>Nota:</strong> La contraseña no se puede cambiar desde aquí. 
                    El usuario debe usar la función "Olvidé mi contraseña".
                  </p>
                </div>

                <div className="flex justify-end space-x-3 pt-4 border-t">
                  <button
                    type="button"
                    onClick={() => setShowEditModal(false)}
                    className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors flex items-center space-x-2"
                  >
                    <Edit className="h-4 w-4" />
                    <span>Guardar Cambios</span>
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {/* ==================== MODAL DE ELIMINAR USUARIO ==================== */}
      {showDeleteModal && selectedUser && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg max-w-md w-full p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-xl font-bold text-gray-900">Eliminar Usuario</h3>
              <button
                onClick={() => setShowDeleteModal(false)}
                className="text-gray-400 hover:text-gray-600"
              >
                <X className="h-6 w-6" />
              </button>
            </div>

            <div className="mb-6">
              <p className="text-gray-600 mb-4">
                ¿Estás seguro de que deseas eliminar este usuario?
              </p>
              <div className="bg-gray-50 rounded-lg p-4">
                <p className="font-medium text-gray-900">
                  {selectedUser.firstName} {selectedUser.lastName}
                </p>
                <p className="text-sm text-gray-600">{selectedUser.email}</p>
                <p className="text-sm text-gray-600">@{selectedUser.username}</p>
                <div className="mt-2">
                  {getRoleBadge(selectedUser.role)}
                </div>
              </div>
              <div className="mt-4 bg-yellow-50 border border-yellow-200 rounded-lg p-4">
                <p className="text-sm text-yellow-800">
                  <strong>Advertencia:</strong> Esta acción desactivará el usuario pero no 
                  eliminará sus datos permanentemente. El usuario no podrá iniciar sesión.
                </p>
              </div>
            </div>

            <div className="flex justify-end space-x-3">
              <button
                onClick={() => setShowDeleteModal(false)}
                className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
              >
                Cancelar
              </button>
              <button
                onClick={handleDeleteUser}
                className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors flex items-center space-x-2"
              >
                <Trash2 className="h-4 w-4" />
                <span>Eliminar Usuario</span>
              </button>
            </div>
          </div>
        </div>
      )}

         {/* Modal Crear Producto */}
         {showCreateProductModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6 border-b border-gray-200 flex justify-between items-center">
              <h2 className="text-2xl font-bold text-gray-900">Crear Producto</h2>
              <button
                onClick={() => setShowCreateProductModal(false)}
                className="text-gray-400 hover:text-gray-600"
              >
                <X className="w-6 h-6" />
              </button>
            </div>

            <form onSubmit={handleCreateProduct} className="p-6 space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Álbum *
                  </label>
                  <select
                    required
                    value={productFormData.albumId || ''}
                    onChange={(e) => setProductFormData({...productFormData, albumId: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  >
                    <option value="">Seleccionar álbum</option>
                    {albums.map((album: any) => (
                      <option key={album.id} value={album.id}>
                        {album.title} - {album.artistName}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Proveedor *
                  </label>
                  <select
                    required
                    value={productFormData.providerId || ''}
                    onChange={(e) => setProductFormData({...productFormData, providerId: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  >
                    <option value="">Seleccionar proveedor</option>
                    {allProviders.map((prov: any) => (
                      <option key={prov.id} value={prov.id}>
                        {prov.businessName}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    SKU *
                  </label>
                  <input
                    type="text"
                    required
                    value={productFormData.sku || ''}
                    onChange={(e) => setProductFormData({...productFormData, sku: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Categoría *
                  </label>
                  <select
                    required
                    value={productFormData.categoryId || ''}
                    onChange={(e) => setProductFormData({...productFormData, categoryId: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  >
                    <option value="">Seleccionar categoría</option>
                    {categories.map((cat: any) => (
                      <option key={cat.id} value={cat.id}>{cat.name}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Tipo *
                  </label>
                  <select
                    required
                    value={productFormData.productType || 'PHYSICAL'}
                    onChange={(e) => setProductFormData({...productFormData, productType: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  >
                    <option value="PHYSICAL">Físico (Vinilo)</option>
                    <option value="DIGITAL">Digital</option>
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Precio *
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={productFormData.price || ''}
                    onChange={(e) => setProductFormData({...productFormData, price: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Stock *
                  </label>
                  <input
                    type="number"
                    required
                    value={productFormData.stockQuantity || 0}
                    onChange={(e) => setProductFormData({...productFormData, stockQuantity: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>

              {productFormData.productType === 'PHYSICAL' && (
                <div className="grid grid-cols-3 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Condición</label>
                    <select
                      value={productFormData.conditionType || 'NEW'}
                      onChange={(e) => setProductFormData({...productFormData, conditionType: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                    >
                      <option value="NEW">Nuevo</option>
                      <option value="USED_LIKE_NEW">Usado - Como Nuevo</option>
                      <option value="USED_GOOD">Usado - Buen Estado</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Tamaño</label>
                    <select
                      value={productFormData.vinylSize || ''}
                      onChange={(e) => setProductFormData({...productFormData, vinylSize: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                    >
                      <option value="">Seleccionar</option>
                      <option value="SEVEN_INCH">7"</option>
                      <option value="TEN_INCH">10"</option>
                      <option value="TWELVE_INCH">12"</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Velocidad</label>
                    <select
                      value={productFormData.vinylSpeed || ''}
                      onChange={(e) => setProductFormData({...productFormData, vinylSpeed: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                    >
                      <option value="">Seleccionar</option>
                      <option value="RPM_33">33 RPM</option>
                      <option value="RPM_45">45 RPM</option>
                    </select>
                  </div>
                </div>
              )}

              {productFormData.productType === 'DIGITAL' && (
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Formato</label>
                    <select
                      value={productFormData.fileFormat || ''}
                      onChange={(e) => setProductFormData({...productFormData, fileFormat: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                    >
                      <option value="">Seleccionar</option>
                      <option value="MP3">MP3</option>
                      <option value="FLAC">FLAC</option>
                      <option value="WAV">WAV</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Tamaño (MB)</label>
                    <input
                      type="number"
                      step="0.1"
                      value={productFormData.fileSizeMB || ''}
                      onChange={(e) => setProductFormData({...productFormData, fileSizeMB: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                    />
                  </div>
                </div>
              )}

              <div className="flex items-center gap-2">
                <input
                  type="checkbox"
                  checked={productFormData.featured || false}
                  onChange={(e) => setProductFormData({...productFormData, featured: e.target.checked})}
                  className="w-4 h-4"
                />
                <label className="text-sm text-gray-700">Producto destacado</label>
              </div>

              <div className="flex gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => setShowCreateProductModal(false)}
                  className="flex-1 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="flex-1 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg"
                >
                  Crear Producto
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Editar Producto - Similar estructura, adaptado */}
      {showEditProductModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6 border-b border-gray-200 flex justify-between items-center">
              <h2 className="text-2xl font-bold text-gray-900">Editar Producto</h2>
              <button
                onClick={() => setShowEditProductModal(false)}
                className="text-gray-400 hover:text-gray-600"
              >
                <X className="w-6 h-6" />
              </button>
            </div>

            <form onSubmit={handleEditProduct} className="p-6 space-y-4">
              {/* Similar al formulario de crear, pero sin albumId (no editable) */}
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    SKU *
                  </label>
                  <input
                    type="text"
                    required
                    value={productFormData.sku || ''}
                    onChange={(e) => setProductFormData({...productFormData, sku: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Categoría *
                  </label>
                  <select
                    required
                    value={productFormData.categoryId || ''}
                    onChange={(e) => setProductFormData({...productFormData, categoryId: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  >
                    <option value="">Seleccionar categoría</option>
                    {categories.map((cat: any) => (
                      <option key={cat.id} value={cat.id}>{cat.name}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Precio *
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={productFormData.price || ''}
                    onChange={(e) => setProductFormData({...productFormData, price: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Stock *
                  </label>
                  <input
                    type="number"
                    required
                    value={productFormData.stockQuantity || 0}
                    onChange={(e) => setProductFormData({...productFormData, stockQuantity: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Proveedor *
                  </label>
                  <select
                    required
                    value={productFormData.providerId || ''}
                    onChange={(e) => setProductFormData({...productFormData, providerId: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  >
                    <option value="">Seleccionar</option>
                    {allProviders.map((prov: any) => (
                      <option key={prov.id} value={prov.id}>
                        {prov.businessName}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              {selectedProduct?.productType === 'PHYSICAL' && (
                <div className="grid grid-cols-3 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Condición</label>
                    <select
                      value={productFormData.conditionType || 'NEW'}
                      onChange={(e) => setProductFormData({...productFormData, conditionType: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                    >
                      <option value="NEW">Nuevo</option>
                      <option value="USED_LIKE_NEW">Usado - Como Nuevo</option>
                      <option value="USED_GOOD">Usado - Buen Estado</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Tamaño</label>
                    <select
                      value={productFormData.vinylSize || ''}
                      onChange={(e) => setProductFormData({...productFormData, vinylSize: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                    >
                      <option value="">Seleccionar</option>
                                            <option value="SEVEN_INCH">7"</option>
                      <option value="TEN_INCH">10"</option>
                      <option value="TWELVE_INCH">12"</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Velocidad</label>
                    <select
                      value={productFormData.vinylSpeed || ''}
                      onChange={(e) => setProductFormData({...productFormData, vinylSpeed: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                    >
                      <option value="">Seleccionar</option>
                                            <option value="RPM_33">33 RPM</option>
                                           <option value="RPM_45">45 RPM</option>
                    </select>
                  </div>
                </div>
              )}

              {selectedProduct?.productType === 'DIGITAL' && (
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Formato</label>
                    <select
                      value={productFormData.fileFormat || ''}
                      onChange={(e) => setProductFormData({...productFormData, fileFormat: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                    >
                      <option value="">Seleccionar</option>
                      <option value="MP3">MP3</option>
                      <option value="FLAC">FLAC</option>
                      <option value="WAV">WAV</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Tamaño (MB)</label>
                    <input
                      type="number"
                      step="0.1"
                      value={productFormData.fileSizeMB || ''}
                      onChange={(e) => setProductFormData({...productFormData, fileSizeMB: e.target.value})}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                    />
                  </div>
                </div>
              )}

              <div className="flex items-center gap-2">
                <input
                  type="checkbox"
                  checked={productFormData.featured || false}
                  onChange={(e) => setProductFormData({...productFormData, featured: e.target.checked})}
                  className="w-4 h-4"
                />
                <label className="text-sm text-gray-700">Producto destacado</label>
              </div>

              <div className="flex gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => setShowEditProductModal(false)}
                  className="flex-1 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="flex-1 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg"
                >
                  Actualizar Producto
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminDashboard;