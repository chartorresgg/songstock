import React from 'react';
import Card from '../ui/Card';

interface UserStats {
  totalUsers: number;
  totalAdmins: number;
  totalProviders: number;
  totalCustomers: number;
  activeUsers: number;
  inactiveUsers: number;
  pendingProviders: number;
  verifiedProviders: number;
  rejectedProviders: number;
  newUsersThisMonth: number;
  newUsersThisWeek: number;
}

interface UserStatsCardsProps {
  stats: UserStats;
}

const UserStatsCards: React.FC<UserStatsCardsProps> = ({ stats }) => {
  const statsCards = [
    {
      title: 'Total Usuarios',
      value: stats.totalUsers,
      icon: '👥',
      color: 'bg-blue-50 text-blue-700',
      bgColor: 'bg-blue-500'
    },
    {
      title: 'Administradores',
      value: stats.totalAdmins,
      icon: '🛡️',
      color: 'bg-purple-50 text-purple-700',
      bgColor: 'bg-purple-500'
    },
    {
      title: 'Proveedores',
      value: stats.totalProviders,
      icon: '🏪',
      color: 'bg-green-50 text-green-700',
      bgColor: 'bg-green-500'
    },
    {
      title: 'Compradores',
      value: stats.totalCustomers,
      icon: '🛒',
      color: 'bg-orange-50 text-orange-700',
      bgColor: 'bg-orange-500'
    },
    {
      title: 'Usuarios Activos',
      value: stats.activeUsers,
      icon: '✅',
      color: 'bg-emerald-50 text-emerald-700',
      bgColor: 'bg-emerald-500'
    },
    {
      title: 'Usuarios Inactivos',
      value: stats.inactiveUsers,
      icon: '❌',
      color: 'bg-red-50 text-red-700',
      bgColor: 'bg-red-500'
    },
    {
      title: 'Proveedores Pendientes',
      value: stats.pendingProviders,
      icon: '⏳',
      color: 'bg-yellow-50 text-yellow-700',
      bgColor: 'bg-yellow-500'
    },
    {
      title: 'Nuevos Esta Semana',
      value: stats.newUsersThisWeek,
      icon: '📈',
      color: 'bg-indigo-50 text-indigo-700',
      bgColor: 'bg-indigo-500'
    }
  ];

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      {statsCards.map((stat, index) => (
        <Card key={index} className="relative overflow-hidden">
          <div className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600 mb-1">
                  {stat.title}
                </p>
                <p className="text-3xl font-bold text-gray-900">
                  {stat.value.toLocaleString()}
                </p>
              </div>
              <div className={`text-2xl p-3 rounded-full ${stat.color}`}>
                {stat.icon}
              </div>
            </div>
            
            {/* Indicador visual */}
            <div className={`absolute bottom-0 left-0 right-0 h-1 ${stat.bgColor}`}></div>
          </div>
        </Card>
      ))}
    </div>
  );
};

export default UserStatsCards;