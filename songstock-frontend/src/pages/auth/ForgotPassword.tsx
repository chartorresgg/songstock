import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Mail, ArrowLeft, CheckCircle } from 'lucide-react';
import axiosInstance from '../../services/axios.instance';
import { ApiResponse } from '../../types/api.types';
import toast from 'react-hot-toast';

const ForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [isSubmitted, setIsSubmitted] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!email || !email.includes('@')) {
      toast.error('Por favor ingresa un email válido');
      return;
    }

    setIsLoading(true);

    try {
      const response = await axiosInstance.post<ApiResponse<string>>(
        '/auth/forgot-password',
        { email }
      );

      console.log('Forgot password response:', response.data);
      
      // ⚠️ IMPORTANTE: En desarrollo, el token se muestra en la consola del backend
      // En producción, esto debería enviarse por email
      toast.success('Si el email existe, recibirás instrucciones para restablecer tu contraseña');
      
      setIsSubmitted(true);
      
    } catch (error: any) {
      console.error('Forgot password error:', error);
      // Por seguridad, mostramos el mismo mensaje aunque falle
      toast.success('Si el email existe, recibirás instrucciones para restablecer tu contraseña');
      setIsSubmitted(true);
    } finally {
      setIsLoading(false);
    }
  };

  if (isSubmitted) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-primary-900 via-primary-800 to-secondary-500 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-md w-full">
          <div className="bg-white rounded-lg shadow-xl p-8">
            <div className="text-center">
              <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <CheckCircle className="h-10 w-10 text-green-600" />
              </div>
              
              <h2 className="text-2xl font-bold text-gray-900 mb-4">
                Revisa tu email
              </h2>
              
              <p className="text-gray-600 mb-6">
                Si existe una cuenta con el email <strong>{email}</strong>, recibirás instrucciones 
                para restablecer tu contraseña.
              </p>

              <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-6">
                <p className="text-sm text-yellow-800 font-medium mb-2">
                  🔧 Modo Desarrollo:
                </p>
                <p className="text-sm text-yellow-700">
                  El token se muestra en la consola del backend. Revisa los logs del servidor 
                  para obtener el token y acceder al enlace de restablecimiento.
                </p>
                <p className="text-xs text-yellow-600 mt-2">
                  En producción, esto se enviará automáticamente por email.
                </p>
              </div>

              <div className="space-y-3">
                <Link
                  to="/login"
                  className="block w-full bg-primary-900 text-white py-3 rounded-lg font-semibold hover:bg-primary-800 transition text-center"
                >
                  Volver al login
                </Link>
                
                <button
                  onClick={() => setIsSubmitted(false)}
                  className="block w-full text-primary-600 hover:text-primary-500 font-medium"
                >
                  Intentar con otro email
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-900 via-primary-800 to-secondary-500 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full">
        {/* Back button */}
        <Link
          to="/login"
          className="inline-flex items-center text-white hover:text-gray-200 mb-6 transition"
        >
          <ArrowLeft className="h-5 w-5 mr-2" />
          Volver al login
        </Link>

        <div className="text-center mb-8">
          <div className="flex justify-center mb-4">
            <div className="bg-white p-3 rounded-full">
              <Mail className="h-12 w-12 text-primary-900" />
            </div>
          </div>
          <h2 className="text-3xl font-bold text-white mb-2">
            ¿Olvidaste tu contraseña?
          </h2>
          <p className="text-gray-200">
            Ingresa tu email y te enviaremos instrucciones para restablecerla
          </p>
        </div>

        <div className="bg-white rounded-lg shadow-xl p-8">
          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
                Correo electrónico
              </label>
              <input
                id="email"
                name="email"
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition"
                placeholder="tu@email.com"
              />
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className="w-full bg-primary-900 text-white py-3 rounded-lg font-semibold hover:bg-primary-800 transition disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isLoading ? 'Enviando...' : 'Enviar instrucciones'}
            </button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">
              ¿Recordaste tu contraseña?{' '}
              <Link to="/login" className="text-primary-600 hover:text-primary-500 font-medium">
                Inicia sesión
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ForgotPassword;