import React from 'react';
import { Link } from 'react-router-dom';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';

const CustomerDashboard: React.FC = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard de Comprador</h1>
        <p className="text-gray-600">Explora y compra discos de vinilo</p>
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card title="Explorar Catálogo">
          <p className="text-gray-600 mb-4">
            Descubre nuevos vinilos y música digital
          </p>
          <Link to="/catalog">
            <Button className="w-full" variant="primary">
              Ver Catálogo
            </Button>
          </Link>
        </Card>

        <Card title="Mis Compras">
          <p className="text-gray-600 mb-4">
            Revisa tu historial de compras
          </p>
          <Button className="w-full" variant="secondary" disabled>
            Próximamente
          </Button>
        </Card>
      </div>

      {/* Featured Products - Placeholder */}
      <Card title="Productos Destacados">
        <div className="text-gray-500 text-center py-8">
          <p>Productos destacados próximamente...</p>
        </div>
      </Card>
    </div>
  );
};

export default CustomerDashboard;