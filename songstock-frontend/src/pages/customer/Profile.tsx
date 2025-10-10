import { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import userService from '../../services/user.service';
import { User as UserIcon, Mail, Phone, Calendar, Shield, Edit2, Save, X } from 'lucide-react';
import toast from 'react-hot-toast';

const Profile = () => {
  const { user, isAuthenticated } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
  });
  const [loading, setLoading] = useState(false);

  /**
   * Este efecto se ejecuta cuando el componente se monta o cuando el usuario cambia.
   * Inicializa el formulario con los datos actuales del usuario.
   * 
   * Es importante inicializar todos los campos, incluso si algunos están vacíos,
   * para evitar el warning de React sobre componentes controlados que se vuelven no controlados.
   */
  useEffect(() => {
    if (user) {
      setFormData({
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        email: user.email || '',
        phone: user.phone || '',           // Ahora usa 'phone' correctamente
      });
    }
  }, [user]);

  /**
   * Esta función maneja los cambios en cualquier campo del formulario.
   * React requiere que actualicemos el estado de forma inmutable,
   * por eso usamos el spread operator (...prev) para copiar el estado anterior
   * y solo modificar el campo que cambió.
   */
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  /**
   * Esta función maneja el envío del formulario cuando el usuario guarda sus cambios.
   * Previene el comportamiento por defecto del formulario (que recarga la página)
   * y en su lugar envía los datos al backend mediante una llamada API.
   */
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!user) return;
    
    setLoading(true);
    
    try {
      // Enviamos los datos al backend
      // Nota: Usamos 'phone' en lugar de 'phoneNumber' para coincidir con el backend
      await userService.updateProfile(user.id, {
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        phone: formData.phone,             // Corregido: ahora usa 'phone'
      });
      
      setIsEditing(false);
      toast.success('Perfil actualizado correctamente');
    } catch (error) {
      console.error('Error updating profile:', error);
      toast.error('Error al actualizar el perfil');
    } finally {
      setLoading(false);
    }
  };

  /**
   * Esta función cancela la edición y restaura los valores originales del formulario.
   * Es importante restaurar los valores para que el usuario no pierda los cambios
   * no guardados si accidentalmente hace clic en cancelar.
   */
  const handleCancel = () => {
    if (user) {
      setFormData({
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        email: user.email || '',
        phone: user.phone || '',           // Corregido: ahora usa 'phone'
      });
    }
    setIsEditing(false);
  };

  /**
   * Esta función convierte el array de números que devuelve el backend en una fecha legible.
   * 
   * El backend de Spring Boot devuelve LocalDateTime como un array:
   * [año, mes, día, hora, minuto, segundo, nanosegundos]
   * 
   * Nosotros extraemos solo las primeras 3 posiciones (año, mes, día) y las convertimos
   * a un objeto Date de JavaScript para formatearlas de forma amigable.
   */
  const formatDate = (dateArray?: number[]) => {
    if (!dateArray || dateArray.length < 3) return 'N/A';
    
    // Extraemos año, mes y día del array
    const [year, month, day] = dateArray;
    
    // Creamos un objeto Date
    // Nota: Restamos 1 al mes porque JavaScript cuenta los meses desde 0 (enero = 0, febrero = 1, etc.)
    // pero el backend cuenta desde 1 (enero = 1, febrero = 2, etc.)
    return new Date(year, month - 1, day).toLocaleDateString('es-CO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  /**
   * Esta función convierte el rol del usuario (que viene en inglés y mayúsculas del backend)
   * a texto legible en español para mostrar en la interfaz.
   */
  const getRoleLabel = (role?: string) => {
    const roles: Record<string, string> = {
      ADMIN: 'Administrador',
      PROVIDER: 'Proveedor',
      CUSTOMER: 'Cliente',
    };
    return role ? roles[role] : 'Usuario';
  };

  // Si el usuario no está autenticado o los datos aún se están cargando,
  // mostramos un indicador de carga
  if (!isAuthenticated || !user) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-gray-600">Cargando perfil...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header con información del usuario */}
      <div className="bg-gradient-to-r from-primary-900 to-primary-800 text-white py-12">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center space-x-4">
            <div className="w-20 h-20 bg-white/20 backdrop-blur-sm rounded-full flex items-center justify-center">
              <UserIcon className="h-10 w-10" />
            </div>
            <div>
              <h1 className="text-3xl font-bold">Mi Perfil</h1>
              <p className="text-gray-200 mt-1">
                Gestiona tu información personal
              </p>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {/* Sidebar con información básica de solo lectura */}
          <div className="md:col-span-1">
            <div className="bg-white rounded-lg shadow-md p-6">
              <div className="text-center mb-6">
                <div className="w-24 h-24 bg-gradient-to-br from-primary-100 to-secondary-100 rounded-full flex items-center justify-center mx-auto mb-4">
                  <UserIcon className="h-12 w-12 text-primary-900" />
                </div>
                <h2 className="text-xl font-bold text-gray-900">
                  {user.firstName} {user.lastName}
                </h2>
                <p className="text-gray-600">@{user.username}</p>
              </div>

              <div className="space-y-4">
                <div className="flex items-center space-x-3 text-gray-700">
                  <Shield className="h-5 w-5 text-primary-900" />
                  <div>
                    <div className="text-sm text-gray-500">Rol</div>
                    <div className="font-medium">{getRoleLabel(user.role)}</div>
                  </div>
                </div>

                <div className="flex items-center space-x-3 text-gray-700">
                  <Calendar className="h-5 w-5 text-primary-900" />
                  <div>
                    <div className="text-sm text-gray-500">Miembro desde</div>
                    <div className="font-medium">{formatDate(user.createdAt)}</div>
                  </div>
                </div>

                {/* Indicador del estado de la cuenta */}
                <div className="pt-4 border-t">
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-gray-600">Estado</span>
                    <span className={`px-3 py-1 rounded-full text-xs font-semibold ${
                      user.isActive                    // Corregido: ahora usa 'isActive'
                        ? 'bg-green-100 text-green-800' 
                        : 'bg-red-100 text-red-800'
                    }`}>
                      {user.isActive ? 'Activa' : 'Inactiva'}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Formulario de edición */}
          <div className="md:col-span-2">
            <div className="bg-white rounded-lg shadow-md p-6">
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-xl font-bold text-gray-900">
                  Información Personal
                </h2>
                
                {!isEditing && (
                  <button
                    onClick={() => setIsEditing(true)}
                    className="flex items-center space-x-2 text-primary-900 hover:text-primary-700 transition"
                  >
                    <Edit2 className="h-5 w-5" />
                    <span>Editar</span>
                  </button>
                )}
              </div>

              <form onSubmit={handleSubmit}>
                <div className="space-y-6">
                  {/* Nombre y Apellido en dos columnas */}
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Nombre
                      </label>
                      {isEditing ? (
                        <input
                          type="text"
                          name="firstName"
                          value={formData.firstName}
                          onChange={handleChange}
                          required
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                        />
                      ) : (
                        <div className="px-4 py-2 bg-gray-50 rounded-lg text-gray-900">
                          {user.firstName}
                        </div>
                      )}
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Apellido
                      </label>
                      {isEditing ? (
                        <input
                          type="text"
                          name="lastName"
                          value={formData.lastName}
                          onChange={handleChange}
                          required
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                        />
                      ) : (
                        <div className="px-4 py-2 bg-gray-50 rounded-lg text-gray-900">
                          {user.lastName}
                        </div>
                      )}
                    </div>
                  </div>

                  {/* Email */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      <Mail className="inline h-4 w-4 mr-1" />
                      Email
                    </label>
                    {isEditing ? (
                      <input
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                      />
                    ) : (
                      <div className="px-4 py-2 bg-gray-50 rounded-lg text-gray-900">
                        {user.email}
                      </div>
                    )}
                  </div>

                  {/* Teléfono */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      <Phone className="inline h-4 w-4 mr-1" />
                      Teléfono
                    </label>
                    {isEditing ? (
                      <input
                        type="tel"
                        name="phone"
                        value={formData.phone}
                        onChange={handleChange}
                        placeholder="+57 300 123 4567"
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                      />
                    ) : (
                      <div className="px-4 py-2 bg-gray-50 rounded-lg text-gray-900">
                        {user.phone || 'No especificado'}
                      </div>
                    )}
                  </div>

                  {/* Username (solo lectura) */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Nombre de Usuario
                    </label>
                    <div className="px-4 py-2 bg-gray-100 rounded-lg text-gray-600">
                      {user.username}
                      <span className="text-xs ml-2">(no se puede cambiar)</span>
                    </div>
                  </div>

                  {/* Botones de acción (solo visibles en modo edición) */}
                  {isEditing && (
                    <div className="flex space-x-4 pt-4 border-t">
                      <button
                        type="submit"
                        disabled={loading}
                        className="flex-1 bg-primary-900 text-white py-3 rounded-lg font-semibold hover:bg-primary-800 transition disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center"
                      >
                        <Save className="h-5 w-5 mr-2" />
                        {loading ? 'Guardando...' : 'Guardar Cambios'}
                      </button>
                      <button
                        type="button"
                        onClick={handleCancel}
                        disabled={loading}
                        className="flex-1 bg-gray-200 text-gray-700 py-3 rounded-lg font-semibold hover:bg-gray-300 transition disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center"
                      >
                        <X className="h-5 w-5 mr-2" />
                        Cancelar
                      </button>
                    </div>
                  )}
                </div>
              </form>

              {/* Sección de seguridad (solo visible cuando no se está editando) */}
              {!isEditing && (
                <div className="mt-8 pt-8 border-t">
                  <h3 className="text-lg font-semibold text-gray-900 mb-4">
                    Seguridad
                  </h3>
                  <button className="text-primary-900 hover:text-primary-700 font-medium transition">
                    Cambiar contraseña →
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Profile;