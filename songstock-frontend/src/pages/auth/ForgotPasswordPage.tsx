import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import Button from '../../components/ui/Button';
import Input from '../../components/ui/Input';
import Card from '../../components/ui/Card';
import { authAPI } from '../../services/api';

const ForgotPasswordPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!email) {
      setError('El email es requerido');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await authAPI.forgotPassword(email);
      
      if (response.success) {
        setSuccess(true);
      } else {
        setError(response.message || 'Error al enviar solicitud');
      }
    } catch (err: any) {
      console.error('Error en forgot password:', err);
      setError('Error al procesar la solicitud. Intenta nuevamente.');
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
        <div className="max-w-md w-full space-y-6">
          <div className="text-center">
            <div className="text-6xl mb-4">📧</div>
            <h2 className="text-3xl font-bold text-gray-900">
              Revisa tu email
            </h2>
            <p className="mt-2 text-sm text-gray-600">
              Si el email existe en nuestro sistema, recibirás instrucciones para restablecer tu contraseña.
            </p>
          </div>
          
          <Card>
            <div className="text-center space-y-4">
              <p className="text-sm text-gray-600">
                Hemos enviado un enlace de restablecimiento a:
              </p>
              <p className="font-medium text-gray-900">{email}</p>
              
              <div className="space-y-2 text-sm text-gray-600">
                <p>• Revisa tu bandeja de entrada</p>
                <p>• Verifica la carpeta de spam</p>
                <p>• El enlace expira en 1 hora</p>
              </div>
              
              <div className="pt-4">
                <Link to="/login">
                  <Button variant="secondary" className="w-full">
                    Volver al inicio de sesión
                  </Button>
                </Link>
              </div>
            </div>
          </Card>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
      <div className="max-w-md w-full space-y-6">
        <div className="text-center">
          <h2 className="text-3xl font-bold text-gray-900">
            ¿Olvidaste tu contraseña?
          </h2>
          <p className="mt-2 text-sm text-gray-600">
            Ingresa tu email y te enviaremos instrucciones para restablecer tu contraseña
          </p>
        </div>
        
        <Card>
          <form className="space-y-4" onSubmit={handleSubmit}>
            {error && (
              <div className="alert alert-error">
                {error}
              </div>
            )}
            
            <Input
              label="Email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Ingresa tu email"
              required
            />
            
            <Button
              type="submit"
              className="w-full"
              loading={loading}
            >
              Enviar instrucciones
            </Button>
          </form>
          
          <div className="mt-6 text-center">
            <Link to="/login" className="text-sm text-blue-600 hover:text-blue-500">
              ← Volver al inicio de sesión
            </Link>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default ForgotPasswordPage;