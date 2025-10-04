// src/components/catalog/ProductFormModal.tsx

import React, { useState, useEffect } from 'react';
import  Modal  from '../ui/Modal';
import  Button  from '../ui/Button';
import  Input  from '../ui/Input';
import { 
  ProductCatalogCreate, 
  ProductCatalogUpdate, 
  ProductCatalogResponse,
  Artist, 
  Genre, 
  Album, 
  Category,
  ProductType,
  ConditionType,
  VinylSize,
  VinylSpeed
} from '../../types/catalog';
import catalogService from '../../services/catalogService';

interface ProductFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (product: ProductCatalogResponse) => void;
  product?: ProductCatalogResponse | null;
  mode: 'create' | 'edit';
}

export const ProductFormModal: React.FC<ProductFormModalProps> = ({
  isOpen,
  onClose,
  onSave,
  product,
  mode
}) => {
  // Estados para entidades relacionadas
  const [artists, setArtists] = useState<Artist[]>([]);
  const [genres, setGenres] = useState<Genre[]>([]);
  const [albums, setAlbums] = useState<Album[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [albumsByArtist, setAlbumsByArtist] = useState<Album[]>([]);

  // Estados del formulario
  const [formData, setFormData] = useState<ProductCatalogCreate>({
    albumId: 0,
    categoryId: 0,
    sku: '',
    productType: ProductType.PHYSICAL,
    conditionType: ConditionType.NEW,
    price: 0,
    stockQuantity: 0,
    featured: false
  });

  // Estados de control
  const [selectedArtist, setSelectedArtist] = useState<number>(0);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  // Cargar datos iniciales
  useEffect(() => {
    if (isOpen) {
      loadInitialData();
    }
  }, [isOpen]);

  // Configurar formulario para edición
  useEffect(() => {
    if (mode === 'edit' && product) {
      setFormData({
        albumId: product.albumId,
        categoryId: product.categoryId,
        sku: product.sku,
        productType: product.productType as ProductType,
        conditionType: product.conditionType as ConditionType,
        price: product.price,
        stockQuantity: product.stockQuantity,
        vinylSize: product.vinylSize as VinylSize,
        vinylSpeed: product.vinylSpeed as VinylSpeed,
        weightGrams: product.weightGrams,
        fileFormat: product.fileFormat,
        fileSizeMb: product.fileSizeMb,
        featured: product.featured
      });
    } else if (mode === 'create') {
      // Resetear formulario para creación
      setFormData({
        albumId: 0,
        categoryId: 0,
        sku: '',
        productType: ProductType.PHYSICAL,
        conditionType: ConditionType.NEW,
        price: 0,
        stockQuantity: 0,
        featured: false
      });
    }
  }, [mode, product]);

  const loadInitialData = async () => {
    try {
      setLoading(true);
      const [artistsData, genresData, albumsData, categoriesData] = await Promise.all([
        catalogService.getArtists(),
        catalogService.getGenres(),
        catalogService.getAlbums(),
        catalogService.getCategories()
      ]);

      setArtists(artistsData);
      setGenres(genresData);
      setAlbums(albumsData);
      setCategories(categoriesData);
    } catch (error) {
      console.error('Error cargando datos iniciales:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleArtistChange = async (artistId: number) => {
    setSelectedArtist(artistId);
    if (artistId > 0) {
      try {
        const albumsData = await catalogService.getAlbumsByArtist(artistId);
        setAlbumsByArtist(albumsData);
      } catch (error) {
        console.error('Error cargando álbumes del artista:', error);
        setAlbumsByArtist([]);
      }
    } else {
      setAlbumsByArtist([]);
    }
    // Reset album selection when artist changes
    setFormData(prev => ({ ...prev, albumId: 0 }));
  };

  const handleInputChange = (field: keyof ProductCatalogCreate, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    // Limpiar error del campo al modificarlo
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  const validateForm = () => {
    const newErrors: Record<string, string> = {};

    if (!formData.albumId || formData.albumId === 0) {
      newErrors.albumId = 'Debe seleccionar un álbum';
    }
    if (!formData.categoryId || formData.categoryId === 0) {
      newErrors.categoryId = 'Debe seleccionar una categoría';
    }
    if (!formData.sku.trim()) {
      newErrors.sku = 'El SKU es obligatorio';
    }
    if (formData.price <= 0) {
      newErrors.price = 'El precio debe ser mayor a 0';
    }
    if (formData.stockQuantity < 0) {
      newErrors.stockQuantity = 'El stock no puede ser negativo';
    }

    // Validaciones específicas por tipo de producto
    if (formData.productType === ProductType.PHYSICAL) {
      if (!formData.vinylSize) {
        newErrors.vinylSize = 'Debe seleccionar el tamaño del vinilo';
      }
      if (!formData.vinylSpeed) {
        newErrors.vinylSpeed = 'Debe seleccionar la velocidad del vinilo';
      }
    } else if (formData.productType === ProductType.DIGITAL) {
      if (!formData.fileFormat?.trim()) {
        newErrors.fileFormat = 'El formato de archivo es obligatorio';
      }
      if (!formData.fileSizeMb || formData.fileSizeMb <= 0) {
        newErrors.fileSizeMb = 'El tamaño del archivo debe ser mayor a 0';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    try {
      setLoading(true);
      
      let savedProduct: ProductCatalogResponse;
      
      if (mode === 'create') {
        savedProduct = await catalogService.createCatalogProduct(formData);
      } else {
        const updateData: ProductCatalogUpdate = {
          sku: formData.sku,
          conditionType: formData.conditionType,
          price: formData.price,
          stockQuantity: formData.stockQuantity,
          featured: formData.featured,
          vinylSize: formData.vinylSize,
          vinylSpeed: formData.vinylSpeed,
          weightGrams: formData.weightGrams,
          fileFormat: formData.fileFormat,
          fileSizeMb: formData.fileSizeMb
        };
        savedProduct = await catalogService.updateCatalogProduct(product!.id, updateData);
      }

      onSave(savedProduct);
      onClose();
    } catch (error: any) {
      console.error('Error guardando producto:', error);
      setErrors({ submit: error.response?.data?.message || 'Error al guardar el producto' });
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setFormData({
      albumId: 0,
      categoryId: 0,
      sku: '',
      productType: ProductType.PHYSICAL,
      conditionType: ConditionType.NEW,
      price: 0,
      stockQuantity: 0,
      featured: false
    });
    setSelectedArtist(0);
    setAlbumsByArtist([]);
    setErrors({});
    onClose();
  };

  if (!isOpen) return null;

  return (
    <Modal 
      isOpen={isOpen} 
      onClose={handleClose}
      title={mode === 'create' ? 'Agregar Producto al Catálogo' : 'Editar Producto'}
      size="lg"
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Selección de Artista y Álbum */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Artista
            </label>
            <select
              value={selectedArtist}
              onChange={(e) => handleArtistChange(Number(e.target.value))}
              className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              disabled={mode === 'edit'}
            >
              <option value={0}>Seleccionar artista</option>
              {artists.map(artist => (
                <option key={artist.id} value={artist.id}>
                  {artist.name}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Álbum *
            </label>
            <select
              value={formData.albumId}
              onChange={(e) => handleInputChange('albumId', Number(e.target.value))}
              className={`w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${
                errors.albumId ? 'border-red-500' : 'border-gray-300'
              }`}
              disabled={mode === 'edit' || albumsByArtist.length === 0}
            >
              <option value={0}>Seleccionar álbum</option>
              {albumsByArtist.map(album => (
                <option key={album.id} value={album.id}>
                  {album.title} ({album.releaseYear})
                </option>
              ))}
            </select>
            {errors.albumId && <p className="mt-1 text-sm text-red-600">{errors.albumId}</p>}
          </div>
        </div>

        {/* Información básica del producto */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Categoría *
            </label>
            <select
              value={formData.categoryId}
              onChange={(e) => handleInputChange('categoryId', Number(e.target.value))}
              className={`w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${
                errors.categoryId ? 'border-red-500' : 'border-gray-300'
              }`}
            >
              <option value={0}>Seleccionar categoría</option>
              {categories.map(category => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
            {errors.categoryId && <p className="mt-1 text-sm text-red-600">{errors.categoryId}</p>}
          </div>

          <Input
            label="SKU *"
            type="text"
            value={formData.sku}
            onChange={(e) => handleInputChange('sku', e.target.value)}
            placeholder="Ej: DSOTM-VINYL-12-001"
            error={errors.sku}
          />
        </div>

        {/* Tipo de producto y condición */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Tipo de Producto *
            </label>
            <select
              value={formData.productType}
              onChange={(e) => handleInputChange('productType', e.target.value as ProductType)}
              className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              disabled={mode === 'edit'}
            >
              <option value={ProductType.PHYSICAL}>Vinilo Físico</option>
              <option value={ProductType.DIGITAL}>Digital</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Condición *
            </label>
            <select
              value={formData.conditionType}
              onChange={(e) => handleInputChange('conditionType', e.target.value as ConditionType)}
              className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value={ConditionType.NEW}>Nuevo</option>
              <option value={ConditionType.LIKE_NEW}>Como Nuevo</option>
              <option value={ConditionType.VERY_GOOD}>Muy Bueno</option>
              <option value={ConditionType.GOOD}>Bueno</option>
              <option value={ConditionType.FAIR}>Regular</option>
              <option value={ConditionType.POOR}>Malo</option>
            </select>
          </div>
        </div>

        {/* Precio y stock */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="Precio *"
            type="number"
            step="0.01"
            min="0"
            value={formData.price}
            onChange={(e) => handleInputChange('price', parseFloat(e.target.value) || 0)}
            placeholder="0.00"
            error={errors.price}
          />

          <Input
            label="Stock *"
            type="number"
            min="0"
            value={formData.stockQuantity}
            onChange={(e) => handleInputChange('stockQuantity', parseInt(e.target.value) || 0)}
            placeholder="0"
            error={errors.stockQuantity}
          />
        </div>

        {/* Campos específicos para vinilos físicos */}
        {formData.productType === ProductType.PHYSICAL && (
          <div className="border-t pt-4">
            <h4 className="text-lg font-medium text-gray-900 mb-3">Información del Vinilo</h4>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Tamaño *
                </label>
                <select
                  value={formData.vinylSize || ''}
                  onChange={(e) => handleInputChange('vinylSize', e.target.value as VinylSize)}
                  className={`w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${
                    errors.vinylSize ? 'border-red-500' : 'border-gray-300'
                  }`}
                >
                  <option value="">Seleccionar</option>
                  <option value={VinylSize.SEVEN_INCH}>7 pulgadas</option>
                  <option value={VinylSize.TEN_INCH}>10 pulgadas</option>
                  <option value={VinylSize.TWELVE_INCH}>12 pulgadas</option>
                </select>
                {errors.vinylSize && <p className="mt-1 text-sm text-red-600">{errors.vinylSize}</p>}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Velocidad *
                </label>
                <select
                  value={formData.vinylSpeed || ''}
                  onChange={(e) => handleInputChange('vinylSpeed', e.target.value as VinylSpeed)}
                  className={`w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${
                    errors.vinylSpeed ? 'border-red-500' : 'border-gray-300'
                  }`}
                >
                  <option value="">Seleccionar</option>
                  <option value={VinylSpeed.RPM_33}>33 RPM</option>
                  <option value={VinylSpeed.RPM_45}>45 RPM</option>
                  <option value={VinylSpeed.RPM_78}>78 RPM</option>
                </select>
                {errors.vinylSpeed && <p className="mt-1 text-sm text-red-600">{errors.vinylSpeed}</p>}
              </div>

              <Input
                label="Peso (gramos)"
                type="number"
                min="0"
                value={formData.weightGrams || ''}
                onChange={(e) => handleInputChange('weightGrams', parseInt(e.target.value) || undefined)}
                placeholder="200"
              />
            </div>
          </div>
        )}

        {/* Campos específicos para productos digitales */}
        {formData.productType === ProductType.DIGITAL && (
          <div className="border-t pt-4">
            <h4 className="text-lg font-medium text-gray-900 mb-3">Información Digital</h4>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Formato de Archivo *
                </label>
                <select
                  value={formData.fileFormat || ''}
                  onChange={(e) => handleInputChange('fileFormat', e.target.value)}
                  className={`w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${
                    errors.fileFormat ? 'border-red-500' : 'border-gray-300'
                  }`}
                >
                  <option value="">Seleccionar formato</option>
                  <option value="MP3">MP3</option>
                  <option value="FLAC">FLAC</option>
                  <option value="WAV">WAV</option>
                  <option value="AAC">AAC</option>
                  <option value="OGG">OGG</option>
                </select>
                {errors.fileFormat && <p className="mt-1 text-sm text-red-600">{errors.fileFormat}</p>}
              </div>

              <Input
                label="Tamaño del Archivo (MB) *"
                type="number"
                min="1"
                value={formData.fileSizeMb || ''}
                onChange={(e) => handleInputChange('fileSizeMb', parseInt(e.target.value) || undefined)}
                placeholder="285"
                error={errors.fileSizeMb}
              />
            </div>
          </div>
        )}

        {/* Producto destacado */}
        <div className="flex items-center">
          <input
            type="checkbox"
            id="featured"
            checked={formData.featured || false}
            onChange={(e) => handleInputChange('featured', e.target.checked)}
            className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
          />
          <label htmlFor="featured" className="ml-2 block text-sm text-gray-900">
            Marcar como producto destacado
          </label>
        </div>

        {/* Error general */}
        {errors.submit && (
          <div className="p-3 bg-red-50 border border-red-200 rounded-lg">
            <p className="text-sm text-red-600">{errors.submit}</p>
          </div>
        )}

        {/* Botones */}
        <div className="flex justify-end space-x-3 pt-4 border-t">
          <Button
            type="button"
            variant="secondary"
            onClick={handleClose}
            disabled={loading}
          >
            Cancelar
          </Button>
          <Button
            type="submit"
            variant="primary"
            disabled={loading}
          >
            {loading ? 'Guardando...' : mode === 'create' ? 'Crear Producto' : 'Actualizar Producto'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};