import React from 'react';

interface Product {
  id: number;
  sku?: string;
  price?: number;
  stockQuantity?: number;
  productType?: string;
}

interface Props {
  products: Product[];
}

const VinylList: React.FC<Props> = ({ products }) => {
  if (!products || products.length === 0) {
    return <div className="mt-4 text-gray-600">No hay vinilos disponibles para la pista seleccionada.</div>;
  }

  return (
    <div className="grid md:grid-cols-2 gap-4 mt-4">
      {products.map((p) => (
        <div key={p.id} className="card">
          <div className="flex items-center justify-between">
            <div>
              <div className="font-semibold">SKU: {p.sku}</div>
              <div className="text-sm text-gray-600">Tipo: {p.productType}</div>
            </div>
            <div className="text-right">
              <div className="font-semibold">${p.price?.toFixed(2)}</div>
              <div className="text-sm text-gray-600">Stock: {p.stockQuantity}</div>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default VinylList;
