import React, { useState, useEffect } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import Button from '../../components/ui/Button';
import Input from '../../components/ui/Input';
import Card from '../../components/ui/Card';
import { authAPI } from '../../services/api';

const ResetPasswordPage: React.FC = () => {
  const { token } = useParams<{ token: string }>();
  const navigate = useNavigate();
  
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');
  const [tokenValid, setTokenValid] = useState<boolean | null>(null);

  useEffect(() => {
    if (!token) {
      setError('Token de restablecimiento no proporcionado');
      setTokenValid(false);
      return;
    }

    setTokenValid(true);
  }, [token]);

  const validatePasswords = () => {
    if (!newPassword || !confirmPassword) {
      setError('Ambas contraseñas son requeridas');
      return false;
    }

    if (newPassword.length < 6) {
      setError('La contraseña debe tener al menos 6 caracteres');
      return false;
    }

    if (newPassword !== confirmPassword) {
      setError('Las contraseñas no coinciden');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validatePasswords()) {
      return;
    }

    if (!token) {
      setError('Token no válido');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await authAPI.resetPassword(token, newPassword);
      
      if (response.success) {
        setSuccess(true);
        setTimeout(() => {
          navigate('/login');
        }, 3000);
      } else {
        setError(response.message || 'Error al restablecer contraseña');
      }
    } catch (err: any) {
      console.error('Error en reset password:', err);
      setError(err.message || 'Error al procesar la solicitud');
    } finally {
      setLoading(false);
    }
  };

  if (tokenValid === false) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
        <div className="max-w-md w-full space-y-6">
          <div className="text-center">
            <div className="text-6xl mb-4">❌</div>
            <h2 className="text-3xl font-bold text-gray-900">
              Enlace no válido
            </h2>
            <p className="mt-2 text-sm text-gray-600">
              El enlace de restablecimiento no es válido o ha expirado
            </p>
          </div>
          
          <Card>
            <div className="text-center space-y-4">
              <div className="space-y-2 text-sm text-gray-600">
                <p>• El enlace puede haber expirado (1 hora)</p>
                <p>• El enlace ya fue utilizado</p>
                <p>• El enlace no es válido</p>
              </div>
              
              <div className="space-y-2">
                <Link to="/forgot-password">
                  <Button className="w-full">
                    Solicitar nuevo enlace
                  </Button>
                </Link>
                
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

  if (success) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
        <div className="max-w-md w-full space-y-6">
          <div className="text-center">
            <div className="text-6xl mb-4">✅</div>
            <h2 className="text-3xl font-bold text-gray-900">
              Contraseña restablecida
            </h2>
            <p className="mt-2 text-sm text-gray-600">
              Tu contraseña ha sido cambiada exitosamente
            </p>
          </div>
          
          <Card>
            <div className="text-center space-y-4">
              <p className="text-sm text-gray-600">
                Serás redirigido al inicio de sesión en unos segundos...
              </p>
              
              <Link to="/login">
                <Button className="w-full">
                  Ir al inicio de sesión
                </Button>
              </Link>
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
            Nueva contraseña
          </h2>
          <p className="mt-2 text-sm text-gray-600">
            Ingresa tu nueva contraseña
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
              label="Nueva contraseña"
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              placeholder="Ingresa tu nueva contraseña"
              required
              minLength={6}
            />
            
            <Input
              label="Confirmar contraseña"
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Confirma tu nueva contraseña"
              required
              minLength={6}
            />
            
            <div className="text-sm text-gray-600">
              La contraseña debe tener al menos 6 caracteres
            </div>
            
            <Button
              type="submit"
              className="w-full"
              loading={loading}
            >
              Restablecer contraseña
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

export default ResetPasswordPage;