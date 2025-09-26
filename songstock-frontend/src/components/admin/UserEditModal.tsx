// ================= ARCHIVO: src/components/admin/UserEditModal.tsx =================
import React, { useState, useEffect } from 'react';
import Button from '../ui/Button';
import Input from '../ui/Input';
import Modal from '../ui/Modal';
import { adminUserAPI } from '../../services/api';
import { useToast } from '../../hooks/useToast';

interface User {
  id: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  phone?: string;
  role: 'ADMIN' | 'PROVIDER' | 'CUSTOMER';
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  businessName?: string;
  verificationStatus?: string;
  verificationDate?: string;
  fullName: string;
}

interface UserEditModalProps {
  user: User;
  isOpen: boolean;
  onClose: () => void;
  onSave: () => void;
}

interface FormData {
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phone: string;
  role: 'ADMIN' | 'PROVIDER' | 'CUSTOMER';
  isActive: boolean;
  newPassword: string;
  updateReason: string;
}

const UserEditModal: React.FC<UserEditModalProps> = ({
  user,
  isOpen,
  onClose,
  onSave
}) => {
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const { showToast } = useToast();

  const [formData, setFormData] = useState<FormData>({
    username: '',
    email: '',
    firstName: '',
    lastName: '',
    phone: '',
    role: 'CUSTOMER',
    isActive: true,
    newPassword: '',
    updateReason: ''
  });

  // Cargar datos del usuario cuando cambie
  useEffect(() => {
    if (user) {
      setFormData({
        username: user.username || '',
        email: user.email || '',
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        phone: user.phone || '',
        role: user.role || 'CUSTOMER',
        isActive: user.isActive ?? true,
        newPassword: '',
        updateReason: ''
      });
      setErrors({});
    }
  }, [user]);

  const handleInputChange = (field: keyof FormData, value: string | boolean) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
    
    // Limpiar error del campo al empezar a escribir
    if (errors[field]) {
      setErrors(prev => ({
        ...prev,
        [field]: ''
      }));
    }
  };

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.username.trim()) {
      newErrors.username = 'El nombre de usuario es requerido';
    } else if (formData.username.length < 3) {
      newErrors.username = 'El nombre de usuario debe tener al menos 3 caracteres';
    }

    if (!formData.email.trim()) {
      newErrors.email = 'El email es requerido';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = 'El formato del email no es válido';
    }

    if (formData.newPassword && formData.newPassword.length < 6) {
      newErrors.newPassword = 'La contraseña debe tener al menos 6 caracteres';
    }

    if (!formData.updateReason.trim()) {
      newErrors.updateReason = 'La razón del cambio es requerida';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setLoading(true);

    try {
      const updateData = {
        username: formData.username,
        email: formData.email,
        firstName: formData.firstName || undefined,
        lastName: formData.lastName || undefined,
        phone: formData.phone || undefined,
        role: formData.role,
        isActive: formData.isActive,
        newPassword: formData.newPassword || undefined,
        updateReason: formData.updateReason
      };

      const response = await adminUserAPI.updateUser(user.id, updateData);

      if (response.success) {
        onSave();
        onClose();
      }
    } catch (error: any) {
      showToast('Error al actualizar usuario: ' + error.message, 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    if (!loading) {
      onClose();
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} title="Editar Usuario">
      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Información básica */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="Nombre de usuario *"
            type="text"
            value={formData.username}
            onChange={(e) => handleInputChange('username', e.target.value)}
            error={errors.username}
            required
          />
          
          <Input
            label="Email *"
            type="email"
            value={formData.email}
            onChange={(e) => handleInputChange('email', e.target.value)}
            error={errors.email}
            required
          />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="Nombre"
            type="text"
            value={formData.firstName}
            onChange={(e) => handleInputChange('firstName', e.target.value)}
          />
          
          <Input
            label="Apellido"
            type="text"
            value={formData.lastName}
            onChange={(e) => handleInputChange('lastName', e.target.value)}
          />
        </div>

        <Input
          label="Teléfono"
          type="tel"
          value={formData.phone}
          onChange={(e) => handleInputChange('phone', e.target.value)}
        />

        {/* Rol y estado */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Rol *
            </label>
            <select
              value={formData.role}
              onChange={(e) => handleInputChange('role', e.target.value as FormData['role'])}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            >
              <option value="CUSTOMER">Comprador</option>
              <option value="PROVIDER">Proveedor</option>
              <option value="ADMIN">Administrador</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Estado
            </label>
            <div className="flex items-center space-x-4 mt-2">
              <label className="flex items-center">
                <input
                  type="radio"
                  name="isActive"
                  checked={formData.isActive === true}
                  onChange={() => handleInputChange('isActive', true)}
                  className="mr-2"
                />
                Activo
              </label>
              <label className="flex items-center">
                <input
                  type="radio"
                  name="isActive"
                  checked={formData.isActive === false}
                  onChange={() => handleInputChange('isActive', false)}
                  className="mr-2"
                />
                Inactivo
              </label>
            </div>
          </div>
        </div>

        {/* Cambio de contraseña */}
        <div className="border-t pt-4">
          <h4 className="text-sm font-medium text-gray-900 mb-4">
            Cambio de Contraseña (Opcional)
          </h4>
          <Input
            label="Nueva contraseña"
            type="password"
            value={formData.newPassword}
            onChange={(e) => handleInputChange('newPassword', e.target.value)}
            error={errors.newPassword}
            placeholder="Dejar vacío para mantener la actual"
          />
        </div>

        {/* Razón del cambio */}
        <div className="border-t pt-4">
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Razón del cambio *
          </label>
          <textarea
            value={formData.updateReason}
            onChange={(e) => handleInputChange('updateReason', e.target.value)}
            className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
              errors.updateReason ? 'border-red-500' : 'border-gray-300'
            }`}
            rows={3}
            placeholder="Explica por qué se está realizando este cambio..."
            required
          />
          {errors.updateReason && (
            <p className="mt-1 text-sm text-red-600">{errors.updateReason}</p>
          )}
        </div>

        {/* Botones */}
        <div className="flex justify-end space-x-3 pt-6 border-t">
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
            loading={loading}
            disabled={loading}
          >
            Guardar Cambios
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default UserEditModal;