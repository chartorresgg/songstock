import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Save, ArrowLeft, Package, Music } from 'lucide-react';
import toast from 'react-hot-toast';
import providerService from '../../services/provider.service';
import catalogService, { Album, Category } from '../../services/catalog.service';
import { useAuth } from '../../contexts/AuthContext';
import SongManager from './SongManager';

interface ProductFormData {
  albumId: number | null;
  categoryId: number | null;
  sku: string;
  productType: 'PHYSICAL' | 'DIGITAL';
  conditionType: 'NEW' | 'LIKE_NEW' | 'VERY_GOOD' | 'GOOD' | 'ACCEPTABLE';
  price: number | string;
  stockQuantity: number | string;
  lowStockThreshold: number | string;
  vinylSize: 'SEVEN_INCH' | 'TEN_INCH' | 'TWELVE_INCH' | '';
  vinylSpeed: 'RPM_33' | 'RPM_45' | 'RPM_78' | '';
  weightGrams: number | string;
  fileFormat: string;
  fileSizeMb: number | string;
  featured: boolean;
  description: string;
}

const ProductForm = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditMode = !!id;
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';
  const [providers, setProviders] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [albums, setAlbums] = useState<Album[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [formData, setFormData] = useState<ProductFormData>({
    albumId: null,
    categoryId: null,
    sku: '',
    productType: 'PHYSICAL',
    conditionType: 'NEW',
    price: '',
    stockQuantity: '',
    lowStockThreshold: '5',
    vinylSize: '',
    vinylSpeed: '',
    weightGrams: '',
    fileFormat: '',
    fileSizeMb: '',
    featured: false,
    description: '',
  });

  useEffect(() => {
    loadInitialData();
    if (isEditMode) {
      loadProduct();
    }
  }, [id]);

  const loadInitialData = async () => {
    try {
      const [albumsData, categoriesData] = await Promise.all([
        catalogService.getAlbums(),
        catalogService.getCategories(),
      ]);
      setAlbums(albumsData);
      setCategories(categoriesData);
    } catch (error) {
      console.error('Error loading initial data:', error);
      toast.error('Error al cargar datos iniciales');
    }
  };

  const loadProduct = async () => {
    try {
      setLoading(true);
      const product = await providerService.getProduct(Number(id));
      
      setFormData({
        albumId: product.albumId,
        categoryId: product.categoryId,
        sku: product.sku,
        productType: product.productType,
        conditionType: product.conditionType,
        price: product.price,
        stockQuantity: product.stockQuantity,
        lowStockThreshold: product.lowStockThreshold || 5,
        vinylSize: product.vinylSize || '',
        vinylSpeed: product.vinylSpeed || '',
        weightGrams: product.weightGrams || '',
        fileFormat: product.fileFormat || '',
        fileSizeMb: product.fileSizeMb || '',
        featured: product.featured,
        description: '',
      });
    } catch (error) {
      console.error('Error loading product:', error);
      toast.error('Error al cargar el producto');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    const { name, value, type } = e.target;
    
    if (type === 'checkbox') {
      const target = e.target as HTMLInputElement;
      setFormData({ ...formData, [name]: target.checked });
    } else if (name === 'productType') {
      // Cuando cambia el tipo de producto, limpiar campos específicos
      if (value === 'PHYSICAL') {
        // Si cambia a PHYSICAL, limpiar campos digitales
        setFormData({
          ...formData,
          productType: value as 'PHYSICAL' | 'DIGITAL',
          fileFormat: '',
          fileSizeMb: '',
        });
      } else if (value === 'DIGITAL') {
        // Si cambia a DIGITAL, limpiar campos físicos
        setFormData({
          ...formData,
          productType: value as 'PHYSICAL' | 'DIGITAL',
          vinylSize: '',
          vinylSpeed: '',
          weightGrams: '',
        });
      }
    } else {
      setFormData({ ...formData, [name]: value });
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validaciones
    if (!formData.albumId) {
      toast.error('Selecciona un álbum');
      return;
    }
    if (!formData.categoryId) {
      toast.error('Selecciona una categoría');
      return;
    }
    if (!formData.sku.trim()) {
      toast.error('El SKU es requerido');
      return;
    }
    if (!formData.price || Number(formData.price) <= 0) {
      toast.error('El precio debe ser mayor a 0');
      return;
    }
    if (!formData.stockQuantity || Number(formData.stockQuantity) < 0) {
      toast.error('La cantidad en stock no puede ser negativa');
      return;
    }

    // Validaciones específicas por tipo de producto
    if (formData.productType === 'PHYSICAL') {
      if (!formData.vinylSize) {
        toast.error('Selecciona el tamaño del vinilo');
        return;
      }
      if (!formData.vinylSpeed) {
        toast.error('Selecciona la velocidad del vinilo');
        return;
      }
    } else if (formData.productType === 'DIGITAL') {
      if (!formData.fileFormat.trim()) {
        toast.error('El formato de archivo es requerido');
        return;
      }
      if (!formData.fileSizeMb || Number(formData.fileSizeMb) <= 0) {
        toast.error('El tamaño del archivo debe ser mayor a 0');
        return;
      }
    }

    try {
      setLoading(true);

      // Preparar datos para enviar
      const productData: any = {
        albumId: Number(formData.albumId),
        categoryId: Number(formData.categoryId),
        sku: formData.sku.trim(),
        productType: formData.productType,
        conditionType: formData.conditionType,
        price: Number(formData.price),
        stockQuantity: Number(formData.stockQuantity),
        lowStockThreshold: Number(formData.lowStockThreshold) || 5,
        featured: formData.featured,
        description: formData.description.trim() || null,
      };

      // Agregar campos específicos según tipo de producto
      if (formData.productType === 'PHYSICAL') {
        productData.vinylSize = formData.vinylSize;
        productData.vinylSpeed = formData.vinylSpeed;
        productData.weightGrams = formData.weightGrams ? Number(formData.weightGrams) : null;
      } else if (formData.productType === 'DIGITAL') {
        productData.fileFormat = formData.fileFormat.trim();
        productData.fileSizeMb = Number(formData.fileSizeMb);
      }

      if (isEditMode) {
        await providerService.updateProduct(Number(id), productData);
        toast.success('Producto actualizado exitosamente');
      } else {
        await providerService.createProduct(productData);
        toast.success('Producto creado exitosamente');
      }

      navigate('/provider/dashboard');
    } catch (error: any) {
      console.error('Error saving product:', error);
      const errorMessage = error.response?.data?.message || 'Error al guardar el producto';
      toast.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <button
            onClick={() => navigate('/provider/dashboard')}
            className="flex items-center text-gray-600 hover:text-gray-900 mb-4 transition"
          >
            <ArrowLeft className="h-5 w-5 mr-2" />
            Volver al Dashboard
          </button>
          <h1 className="text-3xl font-bold text-gray-900">
            {isEditMode ? 'Editar Producto' : 'Nuevo Producto'}
          </h1>
          <p className="text-gray-600 mt-2">
            {isEditMode
              ? 'Actualiza la información de tu producto'
              : 'Agrega un nuevo producto a tu catálogo'}
          </p>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-md p-6 space-y-6">
          {/* Información Básica */}
          <div>
            <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
              <Music className="h-5 w-5 mr-2" />
              Información Básica
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Álbum */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Álbum <span className="text-red-500">*</span>
                </label>
                <select
                  name="albumId"
                  value={formData.albumId || ''}
                  onChange={handleChange}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                >
                  <option value="">Selecciona un álbum</option>
                  {albums.map((album) => (
                    <option key={album.id} value={album.id}>
                      {album.title} - {album.artistName} ({album.releaseYear})
                    </option>
                  ))}
                </select>
              </div>

              {/* Categoría */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Categoría <span className="text-red-500">*</span>
                </label>
                <select
                  name="categoryId"
                  value={formData.categoryId || ''}
                  onChange={handleChange}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                >
                  <option value="">Selecciona una categoría</option>
                  {categories.map((category) => (
                    <option key={category.id} value={category.id}>
                      {category.name}
                    </option>
                  ))}
                </select>
              </div>

              {/* SKU */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  SKU <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  name="sku"
                  value={formData.sku}
                  onChange={handleChange}
                  required
                  maxLength={50}
                  placeholder="GENESIS-LAMB-VINYL-001"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                />
              </div>

              {/* Tipo de Producto */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Tipo de Producto <span className="text-red-500">*</span>
                </label>
                <select
                  name="productType"
                  value={formData.productType}
                  onChange={handleChange}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                >
                  <option value="PHYSICAL">Físico (Vinilo)</option>
                  <option value="DIGITAL">Digital (MP3)</option>
                </select>
              </div>

              {/* Condición */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Condición <span className="text-red-500">*</span>
                </label>
                <select
                  name="conditionType"
                  value={formData.conditionType}
                  onChange={handleChange}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                >
                  <option value="NEW">Nuevo</option>
                  <option value="LIKE_NEW">Como Nuevo</option>
                  <option value="VERY_GOOD">Muy Bueno</option>
                  <option value="GOOD">Bueno</option>
                  <option value="ACCEPTABLE">Aceptable</option>
                </select>
              </div>

              {/* Precio */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Precio (USD) <span className="text-red-500">*</span>
                </label>
                <input
                  type="number"
                  name="price"
                  value={formData.price}
                  onChange={handleChange}
                  required
                  min="0.01"
                  step="0.01"
                  placeholder="45.99"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                />
              </div>

              {/* Stock */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Cantidad en Stock <span className="text-red-500">*</span>
                </label>
                <input
                  type="number"
                  name="stockQuantity"
                  value={formData.stockQuantity}
                  onChange={handleChange}
                  required
                  min="0"
                  placeholder="15"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                />
              </div>

              {/* AGREGAR AQUÍ 👇 */}
              {/* Umbral de Stock Bajo */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Umbral de Alerta de Stock
                </label>
                <input
                  type="number"
                  name="lowStockThreshold"
                  value={formData.lowStockThreshold}
                  onChange={handleChange}
                  min="1"
                  placeholder="5"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                />
                <p className="text-xs text-gray-500 mt-1">
                  Recibirás una alerta cuando el stock sea igual o menor a este valor
                </p>
              </div>
              {/* FIN DEL NUEVO CAMPO 👆 */}

            </div>
          </div>

          {/* Campos Específicos para Vinilo */}
          {formData.productType === 'PHYSICAL' && (
            <div>
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <Package className="h-5 w-5 mr-2" />
                Especificaciones del Vinilo
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                {/* Tamaño */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Tamaño <span className="text-red-500">*</span>
                  </label>
                  <select
                    name="vinylSize"
                    value={formData.vinylSize}
                    onChange={handleChange}
                    required={formData.productType === 'PHYSICAL'}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  >
                    <option value="">Selecciona</option>
                    <option value="SEVEN_INCH">7 pulgadas</option>
                    <option value="TEN_INCH">10 pulgadas</option>
                    <option value="TWELVE_INCH">12 pulgadas</option>
                  </select>
                </div>

                {/* Velocidad */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Velocidad <span className="text-red-500">*</span>
                  </label>
                  <select
                    name="vinylSpeed"
                    value={formData.vinylSpeed}
                    onChange={handleChange}
                    required={formData.productType === 'PHYSICAL'}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  >
                    <option value="">Selecciona</option>
                    <option value="RPM_33">33 RPM</option>
                    <option value="RPM_45">45 RPM</option>
                    <option value="RPM_78">78 RPM</option>
                  </select>
                </div>

                {/* Peso */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Peso (gramos)
                  </label>
                  <input
                    type="number"
                    name="weightGrams"
                    value={formData.weightGrams}
                    onChange={handleChange}
                    min="0"
                    placeholder="200"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  />
                </div>
              </div>
            </div>
          )}

          {/* Campos Específicos para Digital */}
          {formData.productType === 'DIGITAL' && (
            <div>
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <Package className="h-5 w-5 mr-2" />
                Especificaciones Digitales
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {/* Formato */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Formato de Archivo <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="text"
                    name="fileFormat"
                    value={formData.fileFormat}
                    onChange={handleChange}
                    required={formData.productType === 'DIGITAL'}
                    maxLength={20}
                    placeholder="MP3, FLAC, WAV"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  />
                </div>

                {/* Tamaño de Archivo */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Tamaño (MB) <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="number"
                    name="fileSizeMb"
                    value={formData.fileSizeMb}
                    onChange={handleChange}
                    required={formData.productType === 'DIGITAL'}
                    min="1"
                    placeholder="120"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  />
                </div>
              </div>
            </div>
          )}

          {/* Información Adicional */}
          <div>
            <h2 className="text-xl font-semibold text-gray-900 mb-4">
              Información Adicional
            </h2>
            <div className="space-y-4">
              {/* Descripción */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Descripción
                </label>
                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  rows={4}
                  placeholder="Describe tu producto..."
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                />
              </div>

              {/* Destacado */}
              <div className="flex items-center">
                <input
                  type="checkbox"
                  name="featured"
                  checked={formData.featured}
                  onChange={handleChange}
                  className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                />
                <label className="ml-2 block text-sm text-gray-900">
                  Marcar como producto destacado
                </label>
              </div>
            </div>
          </div>

          {isEditMode && formData.productType === 'PHYSICAL' && formData.albumId && (
           <SongManager albumId={Number(formData.albumId)} />
)}

          {/* Botones */}
          <div className="flex items-center justify-end space-x-4 pt-6 border-t">
            <button
              type="button"
              onClick={() => navigate('/provider/dashboard')}
              className="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={loading}
              className="bg-primary-900 text-white px-6 py-2 rounded-lg font-semibold hover:bg-primary-800 transition flex items-center space-x-2 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <Save className="h-5 w-5" />
              <span>{loading ? 'Guardando...' : isEditMode ? 'Actualizar' : 'Crear Producto'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ProductForm;