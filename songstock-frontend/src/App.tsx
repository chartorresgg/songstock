import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './hooks/useAuth';
import ProtectedRoute from './components/ProtectedRoute'; // Importación corregida

// Pages
import LoginPage from './pages/auth/LoginPage';
import RegisterProviderPage from './pages/auth/RegisterProviderPage';
import DashboardPage from './pages/dashboard/DashboardPage';
import Layout from './components/layout/Layout';

// Admin Pages (placeholder for now)
const AdminUsersPage = () => (
  <Layout>
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">Gestión de Usuarios</h1>
      <div className="bg-blue-50 border border-blue-200 rounded-md p-4">
        <p className="text-blue-700">
          Página en desarrollo. Aquí podrás gestionar todos los usuarios de la plataforma.
        </p>
      </div>
    </div>
  </Layout>
);

const AdminProvidersPage = () => (
  <Layout>
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">Gestión de Proveedores</h1>
      <div className="bg-blue-50 border border-blue-200 rounded-md p-4">
        <p className="text-blue-700">
          Página en desarrollo. Aquí podrás verificar y gestionar proveedores.
        </p>
      </div>
    </div>
  </Layout>
);

// Provider Pages (placeholder for now)
const ProviderProductsPage = () => (
  <Layout>
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">Mis Productos</h1>
      <div className="bg-green-50 border border-green-200 rounded-md p-4">
        <p className="text-green-700">
          Página en desarrollo. Aquí podrás gestionar tu catálogo de productos.
        </p>
      </div>
    </div>
  </Layout>
);

const CatalogPage = () => (
  <Layout>
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">Catálogo de Productos</h1>
      <div className="bg-purple-50 border border-purple-200 rounded-md p-4">
        <p className="text-purple-700">
          Página en desarrollo. Aquí podrás explorar todos los productos disponibles.
        </p>
      </div>
    </div>
  </Layout>
);

function App() {
  const { loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="text-4xl mb-4">🎵</div>
          <div className="text-lg text-gray-600">Cargando SongSto...</div>
        </div>
      </div>
    );
  }

  return (
    <Router>
      <Routes>
        {/* Public Routes */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register-provider" element={<RegisterProviderPage />} />
        
        {/* Protected Routes */}
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          }
        />
        
        {/* Admin Routes */}
        <Route
          path="/admin/users"
          element={
            <ProtectedRoute requiredRole="ADMIN">
              <AdminUsersPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/providers"
          element={
            <ProtectedRoute requiredRole="ADMIN">
              <AdminProvidersPage />
            </ProtectedRoute>
          }
        />
        
        {/* Provider Routes */}
        <Route
          path="/provider/products"
          element={
            <ProtectedRoute requiredRole="PROVIDER">
              <ProviderProductsPage />
            </ProtectedRoute>
          }
        />
        
        {/* Public Catalog */}
        <Route path="/catalog" element={<CatalogPage />} />
        
        {/* Default redirect */}
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        
        {/* 404 */}
        <Route
          path="*"
          element={
            <Layout>
              <div className="text-center py-12">
                <div className="text-6xl mb-4">🎵</div>
                <h2 className="text-2xl font-bold text-gray-900">Página no encontrada</h2>
                <p className="text-gray-600 mt-2">La página que buscas no existe.</p>
                <div className="mt-6">
                  <button 
                    onClick={() => window.history.back()}
                    className="text-blue-600 hover:text-blue-500"
                  >
                    ← Volver atrás
                  </button>
                </div>
              </div>
            </Layout>
          }
        />
      </Routes>
    </Router>
  );
}

export default App;