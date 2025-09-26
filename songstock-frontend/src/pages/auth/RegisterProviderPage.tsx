// ================= CORRECCIÓN: RegisterProviderPage.tsx =================
import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import Button from '../../components/ui/Button';
import Input from '../../components/ui/Input';
import Card from '../../components/ui/Card';

const RegisterProviderPage: React.FC = () => {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    email: '',
    fullName: '',
    businessName: '',
    phoneNumber: '',
    taxId: '',
    address: '',
    city: '',
    state: '',
    country: 'Colombia',
    postalCode: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  
  const { registerProvider } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    console.log('🔄 Campo cambiado:', e.target.name, '=', e.target.value);
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    console.log('🚀 INICIO handleSubmit - Formulario enviado');
    e.preventDefault();
    e.stopPropagation(); // Prevenir otros event handlers
    
    console.log('📋 FormData completo:', formData);
    
    // Validaciones básicas
    console.log('🔍 Verificando campos requeridos...');
    
    if (!formData.username || !formData.password || !formData.email || 
        !formData.fullName || !formData.businessName) {
      console.log('❌ Error: Campos requeridos faltantes');
      setError('Los campos marcados con * son requeridos');
      return;
    }

    if (formData.password.length < 6) {
      console.log('❌ Error: Contraseña muy corta');
      setError('La contraseña debe tener al menos 6 caracteres');
      return;
    }

    console.log('✅ Validaciones pasadas, iniciando registro...');
    setLoading(true);
    setError('');

    try {
      console.log('📤 Llamando registerProvider con datos:', formData);
      await registerProvider(formData);
      console.log('✅ registerProvider exitoso');
      setSuccess(true);
      setTimeout(() => {
        console.log('🔄 Navegando a login...');
        navigate('/login', { 
          state: { message: 'Registro exitoso. Tu cuenta está pendiente de verificación por un administrador.' }
        });
      }, 3000);
    } catch (err: any) {
      console.error('❌ Error en el formulario:', err);
      setError(err.message || 'Error al registrar proveedor');
    } finally {
      console.log('🏁 Finalizando handleSubmit');
      setLoading(false);
    }
  };

  // Función alternativa para probar manualmente
  const handleManualSubmit = () => {
    console.log('🔥 Manual submit triggered');
    const fakeEvent = {
      preventDefault: () => console.log('preventDefault called'),
      stopPropagation: () => console.log('stopPropagation called')
    } as React.FormEvent;
    handleSubmit(fakeEvent);
  };

  console.log('🎨 Renderizando RegisterProviderPage, loading:', loading, 'success:', success);

  if (success) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
        <Card className="max-w-md w-full text-center">
          <div style={{ color: '#059669', fontSize: '4rem', marginBottom: '1rem' }}>✓</div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#111827', marginBottom: '1rem' }}>
            ¡Registro Exitoso!
          </h2>
          <p style={{ color: '#6B7280', marginBottom: '1rem' }}>
            Tu cuenta de proveedor ha sido creada. Está pendiente de verificación por un administrador.
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
      <div className="max-w-4xl w-full space-y-6">
        <div className="text-center">
          <h2 style={{ fontSize: '1.875rem', fontWeight: 'bold', color: '#111827' }}>
            🎵 Registro de Proveedor - SongSto
          </h2>
          <p style={{ fontSize: '0.875rem', color: '#6B7280', marginTop: '0.5rem' }}>
            Crea tu cuenta para vender discos en nuestra plataforma
          </p>
        </div>
        
        <Card>
          {/* ⭐ FORM CON HANDLERS EXPLÍCITOS */}
          <form 
            className="space-y-6" 
            onSubmit={handleSubmit}
            noValidate
          >
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
              <strong>Nota:</strong> Tu cuenta será revisada por un administrador antes de ser activada.
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
              <br />Campos requeridos: {
                `username: ${!!formData.username}, password: ${!!formData.password}, email: ${!!formData.email}, fullName: ${!!formData.fullName}, businessName: ${!!formData.businessName}`
              }
              <br />Todos completos: {
                (formData.username && formData.password && formData.email && 
                 formData.fullName && formData.businessName).toString()
              }
            </div>
            
            {/* Información de Usuario */}
            <div>
              <h3 style={{ fontSize: '1.125rem', fontWeight: '600', color: '#111827', marginBottom: '1rem' }}>
                Información de Usuario
              </h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
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
                  label="Email Personal *"
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
              </div>
            </div>
            
            {/* Información del Negocio */}
            <div>
              <h3 style={{ fontSize: '1.125rem', fontWeight: '600', color: '#111827', marginBottom: '1rem' }}>
                Información del Negocio
              </h3>
              <div className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <Input
                    label="Nombre del Negocio *"
                    name="businessName"
                    type="text"
                    value={formData.businessName}
                    onChange={handleChange}
                    placeholder="Disquería Vintage"
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
                </div>
                
                <Input
                  label="NIT/RUC/Tax ID"
                  name="taxId"
                  type="text"
                  value={formData.taxId}
                  onChange={handleChange}
                  placeholder="123456789-0"
                  helperText="Número de identificación tributaria de tu negocio"
                />
              </div>
            </div>
            
            {/* Información de Dirección */}
            <div>
              <h3 style={{ fontSize: '1.125rem', fontWeight: '600', color: '#111827', marginBottom: '1rem' }}>
                Información de Dirección
              </h3>
              <div className="space-y-4">
                <Input
                  label="Dirección"
                  name="address"
                  type="text"
                  value={formData.address}
                  onChange={handleChange}
                  placeholder="Calle 123 #45-67"
                />
                
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <Input
                    label="Ciudad"
                    name="city"
                    type="text"
                    value={formData.city}
                    onChange={handleChange}
                    placeholder="Bogotá"
                  />
                  
                  <Input
                    label="Departamento/Estado"
                    name="state"
                    type="text"
                    value={formData.state}
                    onChange={handleChange}
                    placeholder="Cundinamarca"
                  />
                  
                  <Input
                    label="Código Postal"
                    name="postalCode"
                    type="text"
                    value={formData.postalCode}
                    onChange={handleChange}
                    placeholder="110111"
                  />
                </div>
                
                <div className="form-group">
                  <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '0.25rem' }}>
                    País
                  </label>
                  <select
                    name="country"
                    value={formData.country}
                    onChange={handleChange}
                    style={{ 
                      width: '100%', 
                      padding: '0.5rem 0.75rem', 
                      border: '1px solid #D1D5DB', 
                      borderRadius: '0.375rem',
                      fontSize: '0.875rem'
                    }}
                  >
                    <option value="Colombia">Colombia</option>
                    <option value="Costa Rica">Costa Rica</option>
                    <option value="México">México</option>
                    <option value="Argentina">Argentina</option>
                    <option value="Chile">Chile</option>
                    <option value="Perú">Perú</option>
                  </select>
                </div>
              </div>
            </div>
            
            {/* BOTONES DE DEBUG Y TESTING */}
            <div style={{ border: '2px dashed #9CA3AF', padding: '1rem', borderRadius: '0.375rem' }}>
              <h4 style={{ marginBottom: '0.5rem', fontWeight: 'bold' }}>Debug - Botones de Prueba:</h4>
              
              <button
                type="button"
                onClick={handleManualSubmit}
                style={{ 
                  padding: '0.5rem 1rem', 
                  marginRight: '0.5rem',
                  backgroundColor: '#EF4444', 
                  color: 'white', 
                  border: 'none', 
                  borderRadius: '0.25rem' 
                }}
              >
                🔥 Manual Submit Test
              </button>
              
              <button
                type="button"
                onClick={() => {
                  console.log('🧪 Validating form manually...');
                  const isValid = formData.username && formData.password && formData.email && 
                                 formData.fullName && formData.businessName;
                  console.log('Form is valid:', isValid);
                  console.log('Current formData:', formData);
                }}
                style={{ 
                  padding: '0.5rem 1rem',
                  backgroundColor: '#6B7280', 
                  color: 'white', 
                  border: 'none', 
                  borderRadius: '0.25rem' 
                }}
              >
                🧪 Validate Form
              </button>
            </div>
            
            {/* ⭐ BOTÓN PRINCIPAL SIN onClick CONFLICTIVO */}
            <Button
              type="submit"
              className="w-full"
              loading={loading}
              disabled={loading}
            >
              {loading ? 'Creando cuenta...' : 'Crear Cuenta de Proveedor'}
            </Button>
          </form>
          
          <div className="mt-6 text-center">
            <p style={{ fontSize: '0.875rem', color: '#6B7280' }}>
              ¿Ya tienes cuenta?{' '}
              <Link to="/login" style={{ color: '#2563EB', textDecoration: 'none' }}>
                Iniciar sesión
              </Link>
            </p>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default RegisterProviderPage;