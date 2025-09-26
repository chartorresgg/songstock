import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import api from '../../services/api';

interface DashboardStats {
  totalUsers: number;
  pendingProviders: number;
  verifiedProviders: number;
  totalProducts: number;
}

const AdminDashboard: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats>({
    totalUsers: 0,
    pendingProviders: 0,
    verifiedProviders: 0,
    totalProducts: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      // Estas llamadas son ejemplos, ajusta según tus endpoints reales
      const [usersRes, providersRes] = await Promise.all([
        api.get('/admin/users'),
        api.get('/admin/providers'),
      ]);

      const providers = providersRes.data.data || [];
      const users = usersRes.data.data || [];

      setStats({
        totalUsers: users.length,
        pendingProviders: providers.filter((p: any) => p.verificationStatus === 'PENDING').length,
        verifiedProviders: providers.filter((p: any) => p.verificationStatus === 'VERIFIED').length,
        totalProducts: 0 // Se puede agregar después
      });
    } catch (error) {
      console.error('Error loading dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="text-center py-8">Cargando dashboard...</div>;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard de Administrador</h1>
        <p className="text-gray-600">Gestiona usuarios y proveedores de la plataforma</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card>
          <div className="text-center">
            <div className="text-3xl font-bold text-blue-600">{stats.totalUsers}</div>
            <div className="text-sm text-gray-600">Total Usuarios</div>
          </div>
        </Card>
        
        <Card>
          <div className="text-center">
            <div className="text-3xl font-bold text-orange-600">{stats.pendingProviders}</div>
            <div className="text-sm text-gray-600">Proveedores Pendientes</div>
          </div>
        </Card>
        
        <Card>
          <div className="text-center">
            <div className="text-3xl font-bold text-green-600">{stats.verifiedProviders}</div>
            <div className="text-sm text-gray-600">Proveedores Verificados</div>
          </div>
        </Card>
        
        <Card>
          <div className="text-center">
            <div className="text-3xl font-bold text-purple-600">{stats.totalProducts}</div>
            <div className="text-sm text-gray-600">Total Productos</div>
          </div>
        </Card>
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card title="Gestión de Usuarios">
          <p className="text-gray-600 mb-4">
            Administra todos los usuarios de la plataforma
          </p>
          <div className="space-y-2">
            <Link to="/admin/users">
              <Button className="w-full" variant="primary">
                Ver Todos los Usuarios
              </Button>
            </Link>
            <Link to="/admin/providers">
              <Button className="w-full" variant="secondary">
                Gestionar Proveedores
              </Button>
            </Link>
          </div>
        </Card>

        <Card title="Proveedores Pendientes">
          <p className="text-gray-600 mb-4">
            {stats.pendingProviders > 0 
              ? `Hay ${stats.pendingProviders} proveedores esperando verificación`
              : 'No hay proveedores pendientes'
            }
          </p>
          <Link to="/admin/providers?status=pending">
            <Button 
              className="w-full" 
              variant={stats.pendingProviders > 0 ? "primary" : "secondary"}
            >
              {stats.pendingProviders > 0 ? 'Revisar Pendientes' : 'Ver Proveedores'}
            </Button>
          </Link>
        </Card>
      </div>

      {/* Recent Activity - Placeholder */}
      <Card title="Actividad Reciente">
        <div className="text-gray-500 text-center py-8">
          <p>Funcionalidad de actividad reciente próximamente...</p>
        </div>
      </Card>
    </div>
  );
};

export default AdminDashboard;