import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import { useAuth } from '../../hooks/useAuth';
import api from '../../services/api';

interface ProviderStats {
  totalProducts: number;
  activeProducts: number;
  totalStock: number;
  verificationStatus: string;
}

const ProviderDashboard: React.FC = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState<ProviderStats>({
    totalProducts: 0,
    activeProducts: 0,
    totalStock: 0,
    verificationStatus: 'PENDING'
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadProviderData();
  }, []);

  const loadProviderData = async () => {
    try {
      // Cargar datos del proveedor
      const [providerRes, productsRes] = await Promise.all([
        api.get('/providers/profile'),
        api.get('/products/by-provider'),
      ]);

      const provider = providerRes.data.data;
      const products = productsRes.data.data || [];

      setStats({
        totalProducts: products.length,
        activeProducts: products.filter((p: any) => p.isActive).length,
        totalStock: products.reduce((sum: number, p: any) => sum + (p.stockQuantity || 0), 0),
        verificationStatus: provider.verificationStatus
      });
    } catch (error) {
      console.error('Error loading provider data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="text-center py-8">Cargando dashboard...</div>;
  }

  const getStatusBadge = (status: string) => {
    const styles = {
      'VERIFIED': 'bg-green-100 text-green-800',
      'PENDING': 'bg-yellow-100 text-yellow-800',
      'REJECTED': 'bg-red-100 text-red-800',
    };
    
    const labels = {
      'VERIFIED': 'Verificado',
      'PENDING': 'Pendiente',
      'REJECTED': 'Rechazado',
    };

    return (
      <span className={`px-2 py-1 rounded-full text-xs font-medium ${styles[status as keyof typeof styles]}`}>
        {labels[status as keyof typeof labels]}
      </span>
    );
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Dashboard de Proveedor</h1>
          <p className="text-gray-600">Gestiona tu catálogo e inventario</p>
        </div>
        <div>
          {getStatusBadge(stats.verificationStatus)}
        </div>
      </div>

      {/* Verification Status Alert */}
      {stats.verificationStatus !== 'VERIFIED' && (
        <div className="bg-yellow-50 border border-yellow-200 rounded-md p-4">
          <div className="flex">
            <div className="text-yellow-400">⚠️</div>
            <div className="ml-3">
              <h3 className="text-sm font-medium text-yellow-800">
                Cuenta en Revisión
              </h3>
              <p className="text-sm text-yellow-700 mt-1">
                Tu cuenta está siendo revisada por un administrador. 
                Podrás agregar productos una vez que sea verificada.
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card>
          <div className="text-center">
            <div className="text-3xl font-bold text-blue-600">{stats.totalProducts}</div>
            <div className="text-sm text-gray-600">Total Productos</div>
          </div>
        </Card>
        
        <Card>
          <div className="text-center">
            <div className="text-3xl font-bold text-green-600">{stats.activeProducts}</div>
            <div className="text-sm text-gray-600">Productos Activos</div>
          </div>
        </Card>
        
        <Card>
          <div className="text-center">
            <div className="text-3xl font-bold text-purple-600">{stats.totalStock}</div>
            <div className="text-sm text-gray-600">Unidades en Stock</div>
          </div>
        </Card>
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card title="Mis Productos">
          <p className="text-gray-600 mb-4">
            Gestiona tu catálogo de productos
          </p>
          <div className="space-y-2">
            <Link to="/provider/products">
              <Button className="w-full" variant="primary">
                Ver Mis Productos
              </Button>
            </Link>
            {stats.verificationStatus === 'VERIFIED' && (
              <Link to="/provider/products/new">
                <Button className="w-full" variant="secondary">
                  + Agregar Producto
                </Button>
              </Link>
            )}
          </div>
        </Card>

        <Card title="Inventario">
          <p className="text-gray-600 mb-4">
            Controla el stock de tus productos
          </p>
          <Link to="/provider/inventory">
            <Button className="w-full" variant="primary">
              Gestionar Inventario
            </Button>
          </Link>
        </Card>
      </div>

      {/* Recent Products - Placeholder */}
      <Card title="Productos Recientes">
        <div className="text-gray-500 text-center py-8">
          <p>Lista de productos recientes próximamente...</p>
        </div>
      </Card>
    </div>
  );
};

export default ProviderDashboard;