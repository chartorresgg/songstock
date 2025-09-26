import React from 'react';
import { useAuth } from '../../hooks/useAuth';
import AdminDashboard from './AdminDashboard';
import ProviderDashboard from './ProviderDashboard';
import CustomerDashboard from './CustomerDashboard';
import Layout from '../../components/layout/Layout';

const DashboardPage: React.FC = () => {
  const { user } = useAuth();

  if (!user) return null;

  const renderDashboard = () => {
    switch (user.role) {
      case 'ADMIN':
        return <AdminDashboard />;
      case 'PROVIDER':
        return <ProviderDashboard />;
      case 'CUSTOMER':
        return <CustomerDashboard />;
      default:
        return <div>Rol no reconocido</div>;
    }
  };

  return (
    <Layout>
      {renderDashboard()}
    </Layout>
  );
};

export default DashboardPage;
