import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './contexts/AuthContext';
import { CartProvider } from './contexts/CartContext';
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import Home from './pages/customer/Home';
import Catalog from './pages/customer/Catalog';
import ProductDetail from './pages/customer/ProductDetail';
import Cart from './pages/customer/Cart';
import Checkout from './pages/customer/Checkout';
import Profile from './pages/customer/Profile';  // ← NUEVO: Importamos el componente real
import MyOrders from './pages/customer/MyOrders'
import ProviderDashboard from './pages/provider/ProviderDashboard'
// Layout
import MainLayout from './components/layout/MainLayout';

// Protected Route
import ProtectedRoute from './components/common/ProtectedRoute';

// Provider Pages (placeholders por ahora)
const ProviderCatalog = () => <div className="container mx-auto px-4 py-8"><h1 className="text-3xl font-bold">Mi Catálogo - Próximamente</h1></div>;
const ProviderOrders = () => <div className="container mx-auto px-4 py-8"><h1 className="text-3xl font-bold">Gestión de Pedidos - Próximamente</h1></div>;

// Admin Pages (placeholders por ahora)
const AdminDashboard = () => <div className="container mx-auto px-4 py-8"><h1 className="text-3xl font-bold">Admin Dashboard - Próximamente</h1></div>;
const AdminUsers = () => <div className="container mx-auto px-4 py-8"><h1 className="text-3xl font-bold">Gestión Usuarios - Próximamente</h1></div>;
const AdminProviders = () => <div className="container mx-auto px-4 py-8"><h1 className="text-3xl font-bold">Gestión Proveedores - Próximamente</h1></div>;

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <CartProvider>
          <Toaster 
            position="top-right"
            toastOptions={{
              duration: 3000,
              style: {
                background: '#363636',
                color: '#fff',
              },
              success: {
                iconTheme: {
                  primary: '#10b981',
                  secondary: '#fff',
                },
              },
              error: {
                iconTheme: {
                  primary: '#ef4444',
                  secondary: '#fff',
                },
              },
            }}
          />
          
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />

            {/* Main Layout Routes */}
            <Route element={<MainLayout />}>
              {/* Public Pages */}
              <Route path="/" element={<Home />} />
              <Route path="/catalog" element={<Catalog />} />
              <Route path="/product/:id" element={<ProductDetail />} />

              {/* Customer Protected Routes */}
              <Route path="/cart" element={
                <ProtectedRoute allowedRoles={['CUSTOMER']}>
                  <Cart />
                </ProtectedRoute>
              } />

              <Route path="/checkout" element={
                <ProtectedRoute allowedRoles={['CUSTOMER']}>
                  <Checkout />
                </ProtectedRoute>
              } />

              <Route path="/profile" element={
                <ProtectedRoute>
                  <Profile />
                </ProtectedRoute>
              } />

              <Route path="/my-orders" element={
                <ProtectedRoute allowedRoles={['CUSTOMER']}>
                  <MyOrders />
                </ProtectedRoute>
              } />

              {/* Provider Protected Routes */}
              <Route path="/provider/dashboard" element={
                <ProtectedRoute allowedRoles={['PROVIDER']}>
                  <ProviderDashboard />
                </ProtectedRoute>
              } />
              <Route path="/provider/dashboard" element={
  <ProtectedRoute allowedRoles={['PROVIDER']}>
    <ProviderDashboard />
  </ProtectedRoute>
} />
              <Route path="/provider/catalog" element={
                <ProtectedRoute allowedRoles={['PROVIDER']}>
                  <ProviderCatalog />
                </ProtectedRoute>
              } />
              <Route path="/provider/orders" element={
                <ProtectedRoute allowedRoles={['PROVIDER']}>
                  <ProviderOrders />
                </ProtectedRoute>
              } />

              {/* Admin Protected Routes */}
              <Route path="/admin/dashboard" element={
                <ProtectedRoute allowedRoles={['ADMIN']}>
                  <AdminDashboard />
                </ProtectedRoute>
              } />
              <Route path="/admin/users" element={
                <ProtectedRoute allowedRoles={['ADMIN']}>
                  <AdminUsers />
                </ProtectedRoute>
              } />
              <Route path="/admin/providers" element={
                <ProtectedRoute allowedRoles={['ADMIN']}>
                  <AdminProviders />
                </ProtectedRoute>
              } />
            </Route>

            {/* Catch all */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </CartProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;