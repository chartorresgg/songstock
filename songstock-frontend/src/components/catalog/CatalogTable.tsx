// src/components/catalog/CatalogTable.tsx

import React, { useState } from 'react';
import { ProductCatalogResponse } from '../../types/catalog';
import  Button  from '../ui/Button';
import ConfirmModal from '../ui/ConfirmModal';

interface CatalogTableProps {
  products: ProductCatalogResponse[];
  loading: boolean;
  onEdit: (product: ProductCatalogResponse) => void;
  onToggleStatus: (productId: number) => void;
  onDelete: (productId: number) => void;
}

export const CatalogTable: React.FC<CatalogTableProps> = ({
  products,
  loading,
  onEdit,
  onToggleStatus,
  onDelete
}) => {
  const [deleteModal, setDeleteModal] = useState<{
    isOpen: boolean;
    productId: number | null;
    productName: string;
  }>({
    isOpen: false,
    productId: null,
    productName: ''
  });

  const handleDeleteClick = (product: ProductCatalogResponse) => {
    setDeleteModal({
      isOpen: true,
      productId: product.id,
      productName: `${product.artistName} - ${product.albumTitle}`
    });
  };

  const handleDeleteConfirm = () => {
    if (deleteModal.productId) {
      onDelete(deleteModal.productId);
      setDeleteModal({ isOpen: false, productId: null, productName: '' });
    }
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0
    }).format(price);
  };

  const getConditionBadge = (condition: string) => {
    const conditionColors = {
      'NEW': 'bg-green-100 text-green-800',
      'LIKE_NEW': 'bg-blue-100 text-blue-800',
      'VERY_GOOD': 'bg-yellow-100 text-yellow-800',
      'GOOD': 'bg-orange-100 text-orange-800',
      'FAIR': 'bg-red-100 text-red-800',
      'POOR': 'bg-gray-100 text-gray-800'
    };

    const conditionLabels = {
      'NEW': 'Nuevo',
      'LIKE_NEW': 'Como Nuevo',
      'VERY_GOOD': 'Muy Bueno',
      'GOOD': 'Bueno',
      'FAIR': 'Regular',
      'POOR': 'Malo'
    };

    return (
      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
        conditionColors[condition as keyof typeof conditionColors] || 'bg-gray-100 text-gray-800'
      }`}>
        {conditionLabels[condition as keyof typeof conditionLabels] || condition}
      </span>
    );
  };

  const getProductTypeBadge = (type: string) => {
    return (
      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
        type === 'PHYSICAL' 
          ? 'bg-purple-100 text-purple-800' 
          : 'bg-cyan-100 text-cyan-800'
      }`}>
        {type === 'PHYSICAL' ? '🎵 Vinilo' : '💿 Digital'}
      </span>
    );
  };

  const getStockStatus = (stock: number, isActive: boolean) => {
    if (!isActive) {
      return <span className="text-gray-500">Inactivo</span>;
    }
    
    if (stock === 0) {
      return <span className="text-red-600 font-medium">Sin stock</span>;
    } else if (stock <= 5) {
      return <span className="text-yellow-600 font-medium">Stock bajo ({stock})</span>;
    } else {
      return <span className="text-green-600 font-medium">{stock} unidades</span>;
    }
  };

  if (loading) {
    return (
      <div className="bg-white shadow-lg rounded-lg overflow-hidden">
        <div className="p-6">
          <div className="animate-pulse space-y-4">
            {[...Array(5)].map((_, index) => (
              <div key={index} className="flex space-x-4">
                <div className="h-4 bg-gray-200 rounded w-1/4"></div>
                <div className="h-4 bg-gray-200 rounded w-1/4"></div>
                <div className="h-4 bg-gray-200 rounded w-1/4"></div>
                <div className="h-4 bg-gray-200 rounded w-1/4"></div>
              </div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  if (products.length === 0) {
    return (
      <div className="bg-white shadow-lg rounded-lg overflow-hidden">
        <div className="p-6 text-center">
          <div className="mx-auto h-12 w-12 text-gray-400">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} 
                d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2M4 13h2" />
            </svg>
          </div>
          <h3 className="mt-2 text-sm font-medium text-gray-900">No hay productos en el catálogo</h3>
          <p className="mt-1 text-sm text-gray-500">
            Comienza agregando tu primer producto al catálogo.
          </p>
        </div>
      </div>
    );
  }

  return (
    <>
      <div className="bg-white shadow-lg rounded-lg overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Producto
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Tipo / Condición
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Precio
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Stock
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Estado
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Acciones
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {products.map((product) => (
                <tr key={product.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <div>
                        <div className="text-sm font-medium text-gray-900">
                          {product.artistName} - {product.albumTitle}
                        </div>
                        <div className="text-sm text-gray-500">
                          SKU: {product.sku}
                        </div>
                        <div className="text-xs text-gray-400">
                          {product.releaseYear} • {product.categoryName}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="space-y-1">
                      {getProductTypeBadge(product.productType)}
                      {getConditionBadge(product.conditionType)}
                      {product.featured && (
                        <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-yellow-100 text-yellow-800">
                          ⭐ Destacado
                        </span>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {formatPrice(product.price)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    {getStockStatus(product.stockQuantity, product.isActive)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      product.isActive 
                        ? 'bg-green-100 text-green-800' 
                        : 'bg-red-100 text-red-800'
                    }`}>
                      {product.isActive ? 'Activo' : 'Inactivo'}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <div className="flex justify-end space-x-2">
                      <Button
                        variant="secondary"
                        size="sm"
                        onClick={() => onEdit(product)}
                      >
                        Editar
                      </Button>
                      <Button
                        variant={product.isActive ? "secondary" : "primary"}
                        size="sm"
                        onClick={() => onToggleStatus(product.id)}
                      >
                        {product.isActive ? 'Desactivar' : 'Activar'}
                      </Button>
                      <Button
                        variant="danger"
                        size="sm"
                        onClick={() => handleDeleteClick(product)}
                      >
                        Eliminar
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <ConfirmModal
        isOpen={deleteModal.isOpen}
        onClose={() => setDeleteModal({ isOpen: false, productId: null, productName: '' })}
        onConfirm={handleDeleteConfirm}
        title="Eliminar Producto"
        message={`¿Estás seguro de que quieres eliminar "${deleteModal.productName}" del catálogo? Esta acción no se puede deshacer.`}
        confirmText="Eliminar"
        cancelText="Cancelar"
        type="danger"
      />
    </>
  );
};