import { Music, Disc3, Users, Target, Heart, TrendingUp, Award, Shield } from 'lucide-react';

const AboutUs = () => {
  return (
    <div className="min-h-screen bg-gray-50">
      {/* Hero Section */}
      <section className="relative bg-gradient-to-br from-primary-900 via-primary-800 to-secondary-500 text-white py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center">
            <div className="inline-flex items-center justify-center w-20 h-20 bg-white/20 backdrop-blur-sm rounded-full mb-6">
              <Music className="h-10 w-10" />
            </div>
            <h1 className="text-4xl md:text-5xl font-bold mb-4">
              Sobre SongStock
            </h1>
            <p className="text-xl text-gray-200 max-w-3xl mx-auto">
              Conectamos a coleccionistas de música con proveedores especializados en vinilos 
              y música digital de alta calidad
            </p>
          </div>
        </div>
      </section>

      {/* Mission Section */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
            <div>
              <h2 className="text-3xl font-bold text-gray-900 mb-6">
                Nuestra Misión
              </h2>
              <p className="text-lg text-gray-700 mb-4">
                En SongStock, creemos que la música tiene un valor incalculable y que merece 
                ser preservada, compartida y disfrutada en sus mejores formatos. Nuestra misión 
                es crear un espacio donde los amantes de la música puedan encontrar, comprar y 
                vender vinilos auténticos y música digital de la más alta calidad.
              </p>
              <p className="text-lg text-gray-700 mb-4">
                Nos esforzamos por ser el puente entre coleccionistas apasionados y proveedores 
                confiables, garantizando que cada transacción sea segura, transparente y 
                satisfactoria para ambas partes.
              </p>
              <p className="text-lg text-gray-700">
                Desde clásicos del rock hasta ediciones limitadas de jazz, en SongStock 
                encontrarás un catálogo cuidadosamente curado que celebra la diversidad 
                musical en todas sus formas.
              </p>
            </div>
            <div className="bg-gradient-to-br from-primary-100 to-secondary-100 rounded-2xl p-8">
              <div className="space-y-6">
                <div className="flex items-start space-x-4">
                  <div className="bg-primary-900 p-3 rounded-lg">
                    <Target className="h-6 w-6 text-white" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900 mb-2">Autenticidad Garantizada</h3>
                    <p className="text-gray-700">
                      Verificamos cada proveedor para asegurar que los productos sean 
                      auténticos y de la calidad prometida.
                    </p>
                  </div>
                </div>
                <div className="flex items-start space-x-4">
                  <div className="bg-secondary-500 p-3 rounded-lg">
                    <Heart className="h-6 w-6 text-white" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900 mb-2">Pasión por la Música</h3>
                    <p className="text-gray-700">
                      Somos músicos, coleccionistas y amantes de la música que entienden 
                      el valor de cada álbum.
                    </p>
                  </div>
                </div>
                <div className="flex items-start space-x-4">
                  <div className="bg-primary-600 p-3 rounded-lg">
                    <Shield className="h-6 w-6 text-white" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900 mb-2">Compra Segura</h3>
                    <p className="text-gray-700">
                      Sistema de pagos protegido y políticas claras que protegen tanto 
                      a compradores como vendedores.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Story Section */}
      <section className="py-16 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="max-w-4xl mx-auto text-center mb-12">
            <h2 className="text-3xl font-bold text-gray-900 mb-6">
              Nuestra Historia
            </h2>
            <p className="text-lg text-gray-700">
              SongStock nació de la frustración de buscar vinilos auténticos en mercados 
              desorganizados y de la necesidad de crear un espacio confiable donde 
              coleccionistas y proveedores pudieran conectarse sin intermediarios innecesarios.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="bg-white rounded-lg shadow-md p-8 text-center">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-primary-100 rounded-full mb-6">
                <Disc3 className="h-8 w-8 text-primary-900" />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-4">2024</h3>
              <p className="text-gray-700">
                Lanzamiento de la plataforma con un grupo selecto de proveedores 
                especializados en vinilos clásicos.
              </p>
            </div>

            <div className="bg-white rounded-lg shadow-md p-8 text-center">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-secondary-100 rounded-full mb-6">
                <TrendingUp className="h-8 w-8 text-secondary-600" />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-4">Crecimiento</h3>
              <p className="text-gray-700">
                Expansión del catálogo para incluir música digital de alta calidad 
                y ediciones limitadas.
              </p>
            </div>

            <div className="bg-white rounded-lg shadow-md p-8 text-center">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-primary-100 rounded-full mb-6">
                <Award className="h-8 w-8 text-primary-900" />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-4">Hoy</h3>
              <p className="text-gray-700">
                Plataforma líder que conecta a miles de amantes de la música con 
                los mejores proveedores del mercado.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Values Section */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-gray-900 mb-4">
              Nuestros Valores
            </h2>
            <p className="text-xl text-gray-600 max-w-3xl mx-auto">
              Los principios que guían cada decisión que tomamos en SongStock
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            <div className="text-center">
              <div className="bg-gradient-to-br from-primary-500 to-primary-700 w-20 h-20 rounded-2xl flex items-center justify-center mx-auto mb-4 transform hover:scale-110 transition">
                <Users className="h-10 w-10 text-white" />
              </div>
              <h3 className="text-lg font-bold text-gray-900 mb-2">Comunidad</h3>
              <p className="text-gray-600">
                Construimos una comunidad de personas apasionadas por la música
              </p>
            </div>

            <div className="text-center">
              <div className="bg-gradient-to-br from-secondary-400 to-secondary-600 w-20 h-20 rounded-2xl flex items-center justify-center mx-auto mb-4 transform hover:scale-110 transition">
                <Shield className="h-10 w-10 text-white" />
              </div>
              <h3 className="text-lg font-bold text-gray-900 mb-2">Confianza</h3>
              <p className="text-gray-600">
                Verificamos cada transacción para garantizar seguridad total
              </p>
            </div>

            <div className="text-center">
              <div className="bg-gradient-to-br from-primary-500 to-primary-700 w-20 h-20 rounded-2xl flex items-center justify-center mx-auto mb-4 transform hover:scale-110 transition">
                <Heart className="h-10 w-10 text-white" />
              </div>
              <h3 className="text-lg font-bold text-gray-900 mb-2">Pasión</h3>
              <p className="text-gray-600">
                Amamos la música tanto como tú y eso se refleja en nuestro servicio
              </p>
            </div>

            <div className="text-center">
              <div className="bg-gradient-to-br from-secondary-400 to-secondary-600 w-20 h-20 rounded-2xl flex items-center justify-center mx-auto mb-4 transform hover:scale-110 transition">
                <Award className="h-10 w-10 text-white" />
              </div>
              <h3 className="text-lg font-bold text-gray-900 mb-2">Calidad</h3>
              <p className="text-gray-600">
                Solo trabajamos con productos que cumplen los más altos estándares
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16 bg-gradient-to-r from-primary-900 to-secondary-500 text-white">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-3xl md:text-4xl font-bold mb-6">
            ¿Listo para Comenzar tu Colección?
          </h2>
          <p className="text-xl text-gray-100 mb-8">
            Únete a nuestra comunidad y descubre miles de vinilos y música digital
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <a
              href="/catalog"
              className="inline-flex items-center justify-center px-8 py-4 bg-white text-primary-900 font-semibold rounded-lg hover:bg-gray-100 transition"
            >
              Explorar Catálogo
            </a>
            <a
              href="/register"
              className="inline-flex items-center justify-center px-8 py-4 bg-transparent border-2 border-white text-white font-semibold rounded-lg hover:bg-white/10 transition"
            >
              Registrarse como Proveedor
            </a>
          </div>
        </div>
      </section>
    </div>
  );
};

export default AboutUs;