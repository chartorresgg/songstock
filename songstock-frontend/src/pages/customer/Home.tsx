import { useState, useEffect } from 'react';
import { useCart } from '../../contexts/CartContext';

import { Link } from 'react-router-dom';
import { 
  Music, 
  Disc3, 
  TrendingUp, 
  Star,
  ShoppingBag,
  Headphones,
  Package,
  Shield,
  ArrowRight,
  Sparkles
} from 'lucide-react';
import ProductCard from '../../components/common/ProductCard';
import productService from '../../services/product.service';
import { Product } from '../../types/product.types';
import toast from 'react-hot-toast';

const Home = () => {
  const [featuredProducts, setFeaturedProducts] = useState<Product[]>([]);
  const { addItem } = useCart();
  const [loading, setLoading] = useState(true);
  

  useEffect(() => {
    loadFeaturedProducts();
  }, []);

  const loadFeaturedProducts = async () => {
    try {
      const data = await productService.getProducts({ size: 8 });
      setFeaturedProducts(data.content.slice(0, 8));
    } catch (error) {
      console.error('Error loading featured products:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = (product: Product) => {
    addItem(product);
  };

  return (
    <div className="min-h-screen">
      {/* Hero Section */}
      <section className="relative bg-gradient-to-br from-primary-900 via-primary-800 to-secondary-500 text-white overflow-hidden">
        {/* Animated background elements */}
        <div className="absolute inset-0 overflow-hidden">
          <div className="absolute -top-1/2 -left-1/4 w-96 h-96 bg-white opacity-5 rounded-full blur-3xl animate-pulse"></div>
          <div className="absolute -bottom-1/2 -right-1/4 w-96 h-96 bg-secondary-300 opacity-10 rounded-full blur-3xl animate-pulse delay-700"></div>
        </div>

        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 md:py-32">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
            {/* Left Content */}
            <div className="space-y-8">
              <div className="inline-flex items-center space-x-2 bg-white/10 backdrop-blur-sm px-4 py-2 rounded-full">
                <Sparkles className="h-5 w-5 text-secondary-400" />
                <span className="text-sm font-medium">La mejor colección de vinilos</span>
              </div>

              <h1 className="text-5xl md:text-6xl lg:text-7xl font-bold leading-tight">
                Tu música,
                <span className="block text-transparent bg-clip-text bg-gradient-to-r from-secondary-400 to-secondary-600">
                  en vinilo o digital
                </span>
              </h1>

              <p className="text-xl text-gray-200 max-w-xl">
                Descubre miles de álbumes clásicos y modernos. Compra vinilos vintage 
                o descarga música en alta calidad.
              </p>

              <div className="flex flex-col sm:flex-row gap-4">
                <Link
                  to="/catalog"
                  className="inline-flex items-center justify-center px-8 py-4 bg-white text-primary-900 font-semibold rounded-lg hover:bg-gray-100 transition group"
                >
                  Explorar Catálogo
                  <ArrowRight className="ml-2 h-5 w-5 group-hover:translate-x-1 transition-transform" />
                </Link>
                <Link
                  to="/register"
                  className="inline-flex items-center justify-center px-8 py-4 bg-transparent border-2 border-white text-white font-semibold rounded-lg hover:bg-white/10 transition"
                >
                  Vender Vinilos
                </Link>
              </div>

              {/* Stats */}
              <div className="grid grid-cols-3 gap-6 pt-8">
                <div>
                  <div className="text-3xl font-bold text-white">1000+</div>
                  <div className="text-sm text-gray-300">Productos</div>
                </div>
                <div>
                  <div className="text-3xl font-bold text-white">500+</div>
                  <div className="text-sm text-gray-300">Artistas</div>
                </div>
                <div>
                  <div className="text-3xl font-bold text-white">50+</div>
                  <div className="text-sm text-gray-300">Proveedores</div>
                </div>
              </div>
            </div>

            {/* Right Content - Floating Vinyl */}
            <div className="relative hidden lg:block">
              <div className="relative w-full h-96">
                <div className="absolute inset-0 flex items-center justify-center">
                  <div className="relative">
                    <Disc3 className="h-80 w-80 text-white/20 animate-spin-slow" />
                    <div className="absolute inset-0 flex items-center justify-center">
                      <div className="bg-white/10 backdrop-blur-md rounded-full p-8">
                        <Music className="h-24 w-24 text-white" />
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Wave divider */}
        <div className="absolute bottom-0 left-0 right-0">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 120" className="w-full h-16 fill-gray-50">
            <path d="M0,64L48,69.3C96,75,192,85,288,80C384,75,480,53,576,48C672,43,768,53,864,58.7C960,64,1056,64,1152,58.7C1248,53,1344,43,1392,37.3L1440,32L1440,120L1392,120C1344,120,1248,120,1152,120C1056,120,960,120,864,120C768,120,672,120,576,120C480,120,384,120,288,120C192,120,96,120,48,120L0,120Z"></path>
          </svg>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-20 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
              ¿Por qué elegir SongStock?
            </h2>
            <p className="text-xl text-gray-600 max-w-2xl mx-auto">
              La mejor experiencia para compradores y vendedores de música
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            {/* Feature 1 */}
            <div className="bg-white rounded-xl p-6 shadow-md hover:shadow-xl transition group">
              <div className="bg-primary-100 w-14 h-14 rounded-lg flex items-center justify-center mb-4 group-hover:scale-110 transition">
                <Disc3 className="h-7 w-7 text-primary-900" />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">
                Vinilos Auténticos
              </h3>
              <p className="text-gray-600">
                Colección verificada de vinilos vintage y modernos de proveedores confiables
              </p>
            </div>

            {/* Feature 2 */}
            <div className="bg-white rounded-xl p-6 shadow-md hover:shadow-xl transition group">
              <div className="bg-secondary-100 w-14 h-14 rounded-lg flex items-center justify-center mb-4 group-hover:scale-110 transition">
                <Headphones className="h-7 w-7 text-secondary-600" />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">
                Alta Calidad
              </h3>
              <p className="text-gray-600">
                Música digital en formatos MP3 y FLAC de máxima calidad
              </p>
            </div>

            {/* Feature 3 */}
            <div className="bg-white rounded-xl p-6 shadow-md hover:shadow-xl transition group">
              <div className="bg-primary-100 w-14 h-14 rounded-lg flex items-center justify-center mb-4 group-hover:scale-110 transition">
                <Package className="h-7 w-7 text-primary-900" />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">
                Envío Seguro
              </h3>
              <p className="text-gray-600">
                Empaque especializado para proteger tus vinilos durante el envío
              </p>
            </div>

            {/* Feature 4 */}
            <div className="bg-white rounded-xl p-6 shadow-md hover:shadow-xl transition group">
              <div className="bg-secondary-100 w-14 h-14 rounded-lg flex items-center justify-center mb-4 group-hover:scale-110 transition">
                <Shield className="h-7 w-7 text-secondary-600" />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">
                Compra Protegida
              </h3>
              <p className="text-gray-600">
                Sistema de calificaciones y garantía en todas tus compras
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Featured Products Section */}
      <section className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between mb-12">
            <div>
              <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-2">
                Productos Destacados
              </h2>
              <p className="text-xl text-gray-600">
                Descubre nuestras mejores ofertas
              </p>
            </div>
            <Link
              to="/catalog"
              className="hidden md:inline-flex items-center text-primary-900 font-semibold hover:text-primary-700 transition group"
            >
              Ver todos
              <ArrowRight className="ml-2 h-5 w-5 group-hover:translate-x-1 transition-transform" />
            </Link>
          </div>

          {loading ? (
            <div className="flex justify-center py-20">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-900"></div>
            </div>
          ) : (
            <>
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
                {featuredProducts.map((product) => (
                  <ProductCard
                    key={product.id}
                    product={product}
                    onAddToCart={handleAddToCart}
                  />
                ))}
              </div>

              <div className="text-center mt-12">
                <Link
                  to="/catalog"
                  className="inline-flex items-center px-8 py-4 bg-primary-900 text-white font-semibold rounded-lg hover:bg-primary-800 transition group"
                >
                  Ver Catálogo Completo
                  <ArrowRight className="ml-2 h-5 w-5 group-hover:translate-x-1 transition-transform" />
                </Link>
              </div>
            </>
          )}
        </div>
      </section>

      {/* Categories Section */}
      <section className="py-20 bg-gradient-to-br from-primary-900 to-primary-800 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4">
              Explora por Formato
            </h2>
            <p className="text-xl text-gray-200">
              Encuentra tu forma favorita de disfrutar la música
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            {/* Vinyl Card */}
            <Link
              to="/catalog?productType=PHYSICAL"
              className="group relative bg-white/10 backdrop-blur-sm rounded-2xl p-8 hover:bg-white/20 transition overflow-hidden"
            >
              <div className="absolute top-0 right-0 opacity-10">
                <Disc3 className="h-48 w-48 group-hover:rotate-180 transition-transform duration-700" />
              </div>
              <div className="relative z-10">
                <div className="bg-white/20 w-16 h-16 rounded-full flex items-center justify-center mb-6">
                  <Disc3 className="h-8 w-8 text-white" />
                </div>
                <h3 className="text-3xl font-bold mb-4">Vinilos Físicos</h3>
                <p className="text-gray-200 mb-6">
                  Colecciona vinilos auténticos de todas las épocas. 
                  Desde clásicos hasta ediciones limitadas.
                </p>
                <div className="inline-flex items-center text-white font-semibold group-hover:translate-x-2 transition-transform">
                  Explorar Vinilos
                  <ArrowRight className="ml-2 h-5 w-5" />
                </div>
              </div>
            </Link>

            {/* Digital Card */}
            <Link
              to="/catalog?productType=DIGITAL"
              className="group relative bg-white/10 backdrop-blur-sm rounded-2xl p-8 hover:bg-white/20 transition overflow-hidden"
            >
              <div className="absolute top-0 right-0 opacity-10">
                <Music className="h-48 w-48 group-hover:scale-110 transition-transform duration-700" />
              </div>
              <div className="relative z-10">
                <div className="bg-white/20 w-16 h-16 rounded-full flex items-center justify-center mb-6">
                  <Music className="h-8 w-8 text-white" />
                </div>
                <h3 className="text-3xl font-bold mb-4">Música Digital</h3>
                <p className="text-gray-200 mb-6">
                  Descarga instantánea en formatos de alta calidad. 
                  MP3 y FLAC disponibles.
                </p>
                <div className="inline-flex items-center text-white font-semibold group-hover:translate-x-2 transition-transform">
                  Explorar Digital
                  <ArrowRight className="ml-2 h-5 w-5" />
                </div>
              </div>
            </Link>
          </div>
        </div>
      </section>

      {/* CTA Section for Providers */}
      <section className="py-20 bg-gray-50">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <div className="bg-gradient-to-r from-primary-900 to-secondary-500 rounded-2xl p-12 text-white shadow-2xl">
            <div className="mb-6">
              <div className="inline-flex items-center justify-center w-20 h-20 bg-white/20 backdrop-blur-sm rounded-full mb-6">
                <ShoppingBag className="h-10 w-10" />
              </div>
            </div>
            <h2 className="text-3xl md:text-4xl font-bold mb-4">
              ¿Tienes vinilos para vender?
            </h2>
            <p className="text-xl text-gray-100 mb-8 max-w-2xl mx-auto">
              Únete a nuestra comunidad de proveedores y comienza a vender tus 
              vinilos a miles de coleccionistas.
            </p>
            <Link
              to="/register"
              className="inline-flex items-center px-8 py-4 bg-white text-primary-900 font-semibold rounded-lg hover:bg-gray-100 transition shadow-lg"
            >
              Registrarse como Proveedor
              <ArrowRight className="ml-2 h-5 w-5" />
            </Link>
          </div>
        </div>
      </section>

      {/* Testimonials or Trust Badges (Optional) */}
      <section className="py-16 bg-white border-t">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8 items-center opacity-50">
            <div className="flex items-center justify-center">
              <Star className="h-8 w-8 mr-2 text-yellow-500" />
              <div>
                <div className="font-bold text-2xl">4.8</div>
                <div className="text-sm text-gray-600">Calificación</div>
              </div>
            </div>
            <div className="flex items-center justify-center">
              <TrendingUp className="h-8 w-8 mr-2 text-green-500" />
              <div>
                <div className="font-bold text-2xl">100%</div>
                <div className="text-sm text-gray-600">Seguro</div>
              </div>
            </div>
            <div className="flex items-center justify-center">
              <Package className="h-8 w-8 mr-2 text-blue-500" />
              <div>
                <div className="font-bold text-2xl">24h</div>
                <div className="text-sm text-gray-600">Respuesta</div>
              </div>
            </div>
            <div className="flex items-center justify-center">
              <Shield className="h-8 w-8 mr-2 text-purple-500" />
              <div>
                <div className="font-bold text-2xl">SSL</div>
                <div className="text-sm text-gray-600">Protegido</div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Home;