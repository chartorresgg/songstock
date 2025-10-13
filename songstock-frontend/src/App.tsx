import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './contexts/AuthContext';
import { CartProvider } from './contexts/CartContext';

// Auth Pages
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import ForgotPassword from './pages/auth/ForgotPassword';
import ResetPassword from './pages/auth/ResetPassword';

// Public Pages
import Home from './pages/customer/Home';
import Catalog from './pages/customer/Catalog';
import AboutUs from './pages/public/AboutUs';
import ProductDetail from './pages/customer/ProductDetail';

// Customer Pages
import Cart from './pages/customer/Cart';
import Checkout from './pages/customer/Checkout';
import Profile from './pages/customer/Profile';
import MyOrders from './pages/customer/MyOrders';

// Provider Pages
import ProviderDashboard from './pages/provider/ProviderDashboard';

// Admin Pages
import AdminDashboard from './pages/admin/AdminDashboard';

// Layout
import MainLayout from './components/layout/MainLayout';

// Protected Route
import ProtectedRoute from './components/common/ProtectedRoute';

// Placeholders para páginas pendientes
const ProviderCatalog = () => (
  <div className="container mx-auto px-4 py-8">
    <h1 className="text-3xl font-bold">Mi Catálogo - Próximamente</h1>
  </div>
);

const ProviderOrders = () => (
  <div className="container mx-auto px-4 py-8">
    <h1 className="text-3xl font-bold">Gestión de Pedidos - Próximamente</h1>
  </div>
);

const AdminUsers = () => (
  <div className="container mx-auto px-4 py-8">
    <h1 className="text-3xl font-bold">Gestión Usuarios - Próximamente</h1>
  </div>
);

const AdminProviders = () => (
  <div className="container mx-auto px-4 py-8">
    <h1 className="text-3xl font-bold">Gestión Proveedores - Próximamente</h1>
  </div>
);

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
            {/* ==================== RUTAS PÚBLICAS DE AUTENTICACIÓN ==================== */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/reset-password" element={<ResetPassword />} />

            {/* ==================== RUTAS CON MAIN LAYOUT ==================== */}
            <Route element={<MainLayout />}>
              {/* Páginas Públicas */}
              <Route path="/" element={<Home />} />
              <Route path="/catalog" element={<Catalog />} />
              <Route path="/product/:id" element={<ProductDetail />} />
              <Route path="/about-us" element={<AboutUs />} />

              {/* ==================== RUTAS PROTEGIDAS DE CUSTOMER ==================== */}
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

              <Route path="/my-orders" element={
                <ProtectedRoute allowedRoles={['CUSTOMER']}>
                  <MyOrders />
                </ProtectedRoute>
              } />

              {/* ==================== RUTAS PROTEGIDAS DE PROVIDER ==================== */}
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

              {/* ==================== RUTAS PROTEGIDAS DE ADMIN ==================== */}
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

              {/* ==================== PERFIL (TODOS LOS ROLES) ==================== */}
              <Route path="/profile" element={
                <ProtectedRoute>
                  <Profile />
                </ProtectedRoute>
              } />
            </Route>

            {/* ==================== CATCH ALL ==================== */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </CartProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;