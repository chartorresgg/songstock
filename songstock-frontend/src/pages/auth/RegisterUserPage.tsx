// ================= ARCHIVO: src/pages/auth/RegisterUserPage.tsx =================
import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import Button from '../../components/ui/Button';
import Input from '../../components/ui/Input';
import Card from '../../components/ui/Card';

const RegisterUserPage: React.FC = () => {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    email: '',
    fullName: '',
    phoneNumber: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  
  const { registerUser } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    console.log('Campo cambiado:', e.target.name, '=', e.target.value);
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    console.log('INICIO handleSubmit - Registro de usuario');
    e.preventDefault();
    e.stopPropagation();
    
    console.log('FormData completo:', formData);
    
    // Validaciones básicas
    if (!formData.username || !formData.password || !formData.email || !formData.fullName) {
      console.log('Error: Campos requeridos faltantes');
      setError('Los campos marcados con * son requeridos');
      return;
    }

    if (formData.password.length < 6) {
      console.log('Error: Contraseña muy corta');
      setError('La contraseña debe tener al menos 6 caracteres');
      return;
    }

    console.log('Validaciones pasadas, iniciando registro...');
    setLoading(true);
    setError('');

    try {
      console.log('Llamando registerUser con datos:', formData);
      await registerUser(formData);
      console.log('registerUser exitoso');
      setSuccess(true);
      setTimeout(() => {
        console.log('Navegando a login...');
        navigate('/login', { 
          state: { message: 'Registro exitoso. Ya puedes iniciar sesión.' }
        });
      }, 3000);
    } catch (err: any) {
      console.error('Error en el formulario:', err);
      setError(err.message || 'Error al registrar usuario');
    } finally {
      console.log('Finalizando handleSubmit');
      setLoading(false);
    }
  };

  console.log('Renderizando RegisterUserPage, loading:', loading, 'success:', success);

  if (success) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
        <Card className="max-w-md w-full text-center">
          <div style={{ color: '#059669', fontSize: '4rem', marginBottom: '1rem' }}>✓</div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#111827', marginBottom: '1rem' }}>
            ¡Registro Exitoso!
          </h2>
          <p style={{ color: '#6B7280', marginBottom: '1rem' }}>
            Tu cuenta de usuario ha sido creada exitosamente. Ya puedes iniciar sesión.
          </p>
          <p style={{ fontSize: '0.875rem', color: '#9CA3AF' }}>
            Serás redirigido al login en unos segundos...
          </p>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
      <div className="max-w-md w-full space-y-6">
        <div className="text-center">
          <h2 style={{ fontSize: '1.875rem', fontWeight: 'bold', color: '#111827' }}>
            Crear Cuenta de Usuario - SongSto
          </h2>
          <p style={{ fontSize: '0.875rem', color: '#6B7280', marginTop: '0.5rem' }}>
            Únete para explorar y comprar discos de vinilo
          </p>
        </div>
        
        <Card>
          <form className="space-y-4" onSubmit={handleSubmit} noValidate>
            {error && (
              <div className="alert alert-error" style={{ 
                backgroundColor: '#FEF2F2', 
                border: '1px solid #FECACA', 
                color: '#DC2626',
                padding: '0.75rem',
                borderRadius: '0.375rem'
              }}>
                {error}
              </div>
            )}
            
            <div className="alert alert-info" style={{
              backgroundColor: '#EFF6FF',
              border: '1px solid #DBEAFE',
              color: '#1D4ED8',
              padding: '0.75rem',
              borderRadius: '0.375rem'
            }}>
              <strong>Nota:</strong> Crea tu cuenta para acceder al catálogo completo de vinilos.
            </div>
            
            {/* DEBUG INFO */}
            <div style={{
              backgroundColor: '#F3F4F6',
              border: '1px solid #D1D5DB',
              padding: '0.75rem',
              borderRadius: '0.375rem',
              fontSize: '0.75rem'
            }}>
              <strong>Debug Info:</strong>
              <br />Loading: {loading.toString()}
              <br />Error: {error || 'none'}
              <br />Campos completos: {
                (formData.username && formData.password && formData.email && formData.fullName).toString()
              }
            </div>
            
            <Input
              label="Usuario *"
              name="username"
              type="text"
              value={formData.username}
              onChange={handleChange}
              placeholder="usuario123"
              required
            />
            
            <Input
              label="Contraseña *"
              name="password"
              type="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Mínimo 6 caracteres"
              required
            />
            
            <Input
              label="Email *"
              name="email"
              type="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="tu@email.com"
              required
            />
            
            <Input
              label="Nombre Completo *"
              name="fullName"
              type="text"
              value={formData.fullName}
              onChange={handleChange}
              placeholder="Tu nombre completo"
              required
            />
            
            <Input
              label="Teléfono"
              name="phoneNumber"
              type="tel"
              value={formData.phoneNumber}
              onChange={handleChange}
              placeholder="+57 300 123 4567"
            />
            
            <Button
              type="submit"
              className="w-full"
              loading={loading}
              disabled={loading}
            >
              {loading ? 'Creando cuenta...' : 'Crear Cuenta de Usuario'}
            </Button>
          </form>
          
          <div className="mt-6 text-center">
            <p style={{ fontSize: '0.875rem', color: '#6B7280' }}>
              ¿Ya tienes cuenta?{' '}
              <Link to="/login" style={{ color: '#2563EB', textDecoration: 'none' }}>
                Iniciar sesión
              </Link>
            </p>
            <p style={{ fontSize: '0.875rem', color: '#6B7280', marginTop: '0.5rem' }}>
              ¿Quieres vender discos?{' '}
              <Link to="/register-provider" style={{ color: '#2563EB', textDecoration: 'none' }}>
                Registrarse como proveedor
              </Link>
            </p>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default RegisterUserPage;    