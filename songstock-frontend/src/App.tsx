// ================= ARCHIVO: src/App.tsx (CORREGIDO) =================
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './hooks/useAuth';
import ProtectedRoute from './components/ProtectedRoute';

// Auth Pages
import LoginPage from './pages/auth/LoginPage';
import RegisterUserPage from './pages/auth/RegisterUserPage';
import RegisterProviderPage from './pages/auth/RegisterProviderPage';
import ForgotPasswordPage from './pages/auth/ForgotPasswordPage';  // AGREGAR
import ResetPasswordPage from './pages/auth/ResetPasswordPage';    // AGREGAR

// Dashboard
import DashboardPage from './pages/dashboard/DashboardPage';

import AdminUsersManagementPage from './pages/admin/AdminUsersPage';

// Placeholder pages for routes
const AdminUsersPage = () => (
  <div className="p-8">
    <h1 className="text-2xl font-bold">Gestión de Usuarios (Admin)</h1>
    <p>Página en desarrollo...</p>
  </div>
);

const AdminProvidersPage = () => (
  <div className="p-8">
    <h1 className="text-2xl font-bold">Gestión de Proveedores (Admin)</h1>
    <p>Página en desarrollo...</p>
  </div>
);

const ProviderProductsPage = () => (
  <div className="p-8">
    <h1 className="text-2xl font-bold">Mis Productos (Proveedor)</h1>
    <p>Página en desarrollo...</p>
  </div>
);

const CatalogPage = () => (
  <div className="p-8">
    <h1 className="text-2xl font-bold">Catálogo de Productos</h1>
    <p>Página en desarrollo...</p>
  </div>
);

function App() {
  const { loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-lg">Cargando aplicación...</div>
      </div>
    );
  }

  return (
    <Router>
      <Routes>
        {/* Public Routes */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register-user" element={<RegisterUserPage />} />
        <Route path="/register-provider" element={<RegisterProviderPage />} />
        <Route path="/forgot-password" element={<ForgotPasswordPage />} />
        <Route path="/reset-password/:token" element={<ResetPasswordPage />} />
        
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
              <AdminUsersManagementPage />
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
            <div className="min-h-screen flex items-center justify-center">
              <div className="text-center">
                <h2 className="text-2xl font-bold">Página no encontrada</h2>
                <p className="text-gray-600 mt-2">La página que buscas no existe.</p>
                <div className="mt-4">
                  <a href="/login" className="text-blue-600 hover:text-blue-500">
                    Ir al Login
                  </a>
                </div>
              </div>
            </div>
          }
        />
      </Routes>
    </Router>
  );
}

export default App;