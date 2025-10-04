// src/components/catalog/CatalogStatsCards.tsx

import React from 'react';
import { ProviderCatalogSummary } from '../../types/catalog';
import Card from '../ui/Card';

interface CatalogStatsCardsProps {
  summary: ProviderCatalogSummary | null;
  loading: boolean;
}

export const CatalogStatsCards: React.FC<CatalogStatsCardsProps> = ({ summary, loading }) => {
  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0
    }).format(value);
  };

  const formatNumber = (value: number) => {
    return new Intl.NumberFormat('es-CO').format(value);
  };

  if (loading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        {[...Array(8)].map((_, index) => (
          <Card key={index} className="p-6">
            <div className="animate-pulse">
              <div className="h-4 bg-gray-200 rounded w-3/4 mb-2"></div>
              <div className="h-8 bg-gray-200 rounded w-1/2 mb-2"></div>
              <div className="h-3 bg-gray-200 rounded w-full"></div>
            </div>
          </Card>
        ))}
      </div>
    );
  }

  if (!summary) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <Card className="p-6 text-center">
          <p className="text-gray-500">No hay datos disponibles</p>
        </Card>
      </div>
    );
  }

  const stats = [
    {
      title: 'Total de Productos',
      value: formatNumber(summary.totalProducts),
      description: `${summary.activeProducts} activos, ${summary.inactiveProducts} inactivos`,
      icon: '📦',
      color: 'text-blue-600'
    },
    {
      title: 'Productos en Stock',
      value: formatNumber(summary.productsInStock),
      description: `${summary.productsOutOfStock} sin stock`,
      icon: '✅',
      color: 'text-green-600'
    },
    {
      title: 'Productos Destacados',
      value: formatNumber(summary.featuredProducts),
      description: `${((summary.featuredProducts / summary.totalProducts) * 100).toFixed(1)}% del catálogo`,
      icon: '⭐',
      color: 'text-yellow-600'
    },
    {
      title: 'Valor del Catálogo',
      value: formatCurrency(summary.totalCatalogValue),
      description: `Precio promedio: ${formatCurrency(summary.averagePrice)}`,
      icon: '💰',
      color: 'text-purple-600'
    },
    {
      title: 'Vinilos Físicos',
      value: formatNumber(summary.physicalProducts),
      description: `${((summary.physicalProducts / summary.totalProducts) * 100).toFixed(1)}% del catálogo`,
      icon: '🎵',
      color: 'text-indigo-600'
    },
    {
      title: 'Productos Digitales',
      value: formatNumber(summary.digitalProducts),
      description: `${((summary.digitalProducts / summary.totalProducts) * 100).toFixed(1)}% del catálogo`,
      icon: '💿',
      color: 'text-cyan-600'
    },
    {
      title: 'Productos Nuevos',
      value: formatNumber(summary.newProducts),
      description: `${summary.usedProducts} usados`,
      icon: '✨',
      color: 'text-emerald-600'
    },
    {
      title: 'Estado General',
      value: `${((summary.activeProducts / summary.totalProducts) * 100).toFixed(0)}%`,
      description: 'Productos activos',
      icon: '📊',
      color: summary.activeProducts / summary.totalProducts > 0.8 ? 'text-green-600' : 'text-orange-600'
    }
  ];

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      {stats.map((stat, index) => (
        <Card key={index} className="p-6 hover:shadow-lg transition-shadow duration-200">
          <div className="flex items-center justify-between">
            <div className="flex-1">
              <p className="text-sm font-medium text-gray-600 mb-1">{stat.title}</p>
              <p className={`text-2xl font-bold ${stat.color} mb-1`}>{stat.value}</p>
              <p className="text-xs text-gray-500">{stat.description}</p>
            </div>
            <div className="text-2xl ml-4">{stat.icon}</div>
          </div>
        </Card>
      ))}
    </div>
  );
};