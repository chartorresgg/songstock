// ================= ARCHIVO: src/pages/admin/AdminUsersPage.tsx =================
import React, { useState, useEffect } from 'react';
import { useToast } from '../../hooks/useToast';
import Button from '../../components/ui/Button';
import Input from '../../components/ui/Input';
import Card from '../../components/ui/Card';
import { adminUserAPI } from '../../services/api';
import UserTable from '../../components/admin/UserTable';
import UserStatsCards from '../../components/admin/UserStatsCards';
import UserEditModal from '../../components/admin/UserEditModal';
import ConfirmModal from '../../components/ui/ConfirmModal';

// Tipos
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

interface UserStats {
  totalUsers: number;
  totalAdmins: number;
  totalProviders: number;
  totalCustomers: number;
  activeUsers: number;
  inactiveUsers: number;
  pendingProviders: number;
  verifiedProviders: number;
  rejectedProviders: number;
  newUsersThisMonth: number;
  newUsersThisWeek: number;
}

interface Filters {
  role: string;
  isActive: string;
  search: string;
  verificationStatus: string;
  sortBy: string;
  sortDirection: string;
  page: number;
  size: number;
}

const AdminUsersPage: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [stats, setStats] = useState<UserStats | null>(null);
  const [loading, setLoading] = useState(false);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  
  // Estados de modales
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [deletingUser, setDeletingUser] = useState<User | null>(null);
  const [toggleStatusUser, setToggleStatusUser] = useState<User | null>(null);

  // Estados de filtros
  const [filters, setFilters] = useState<Filters>({
    role: '',
    isActive: '',
    search: '',
    verificationStatus: '',
    sortBy: 'createdAt',
    sortDirection: 'DESC',
    page: 0,
    size: 10
  });

  const { showToast } = useToast();

  // Cargar datos iniciales
  useEffect(() => {
    loadUsers();
    loadStats();
  }, [filters]);

  const loadUsers = async () => {
    setLoading(true);
    try {
      const response = await adminUserAPI.getAllUsers(filters);
      if (response.success) {
        setUsers(response.data.content);
        setTotalPages(response.data.totalPages);
        setTotalElements(response.data.totalElements);
      }
    } catch (error: any) {
      showToast('Error al cargar usuarios: ' + error.message, 'error');
    } finally {
      setLoading(false);
    }
  };

  const loadStats = async () => {
    try {
      const response = await adminUserAPI.getStatistics();
      if (response.success) {
        setStats(response.data);
      }
    } catch (error: any) {
      console.error('Error loading stats:', error);
    }
  };

  const handleFilterChange = (field: keyof Filters, value: string | number) => {
    setFilters(prev => ({
      ...prev,
      [field]: value,
      page: field !== 'page' ? 0 : Number(value) // Reset page when other filters change
    }));
  };

  const handleSearch = (searchTerm: string) => {
    handleFilterChange('search', searchTerm);
  };

  const handleEditUser = (user: User) => {
    setEditingUser(user);
  };

  const handleDeleteUser = (user: User) => {
    setDeletingUser(user);
  };

  const handleToggleStatus = (user: User) => {
    setToggleStatusUser(user);
  };

  const confirmDelete = async () => {
    if (!deletingUser) return;

    try {
      const response = await adminUserAPI.deleteUser(deletingUser.id, 'Eliminado por administrador');
      if (response.success) {
        showToast('Usuario eliminado exitosamente', 'success');
        loadUsers();
        loadStats();
      }
    } catch (error: any) {
      showToast('Error al eliminar usuario: ' + error.message, 'error');
    } finally {
      setDeletingUser(null);
    }
  };

  const confirmToggleStatus = async () => {
    if (!toggleStatusUser) return;

    try {
      const response = await adminUserAPI.toggleUserStatus(
        toggleStatusUser.id, 
        `${toggleStatusUser.isActive ? 'Desactivado' : 'Activado'} por administrador`
      );
      if (response.success) {
        showToast(
          `Usuario ${toggleStatusUser.isActive ? 'desactivado' : 'activado'} exitosamente`, 
          'success'
        );
        loadUsers();
        loadStats();
      }
    } catch (error: any) {
      showToast('Error al cambiar estado: ' + error.message, 'error');
    } finally {
      setToggleStatusUser(null);
    }
  };

  const handleUserSaved = () => {
    loadUsers();
    loadStats();
    setEditingUser(null);
    showToast('Usuario actualizado exitosamente', 'success');
  };

  return (
    <div className="p-6 max-w-7xl mx-auto">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Gestión de Usuarios</h1>
        <p className="text-gray-600 mt-2">
          Administra compradores, proveedores y administradores del sistema
        </p>
      </div>

      {/* Estadísticas */}
      {stats && <UserStatsCards stats={stats} />}

      {/* Filtros y búsqueda */}
      <Card className="mb-6">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
          {/* Búsqueda */}
          <div className="lg:col-span-2">
            <Input
              placeholder="Buscar por usuario, email o nombre..."
              value={filters.search}
              onChange={(e) => handleSearch(e.target.value)}
            />
          </div>

          {/* Filtro por rol */}
          <select
            value={filters.role}
            onChange={(e) => handleFilterChange('role', e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">Todos los roles</option>
            <option value="ADMIN">Administradores</option>
            <option value="PROVIDER">Proveedores</option>
            <option value="CUSTOMER">Compradores</option>
          </select>

          {/* Filtro por estado */}
          <select
            value={filters.isActive}
            onChange={(e) => handleFilterChange('isActive', e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">Todos los estados</option>
            <option value="true">Activos</option>
            <option value="false">Inactivos</option>
          </select>

          {/* Filtro por verificación (solo proveedores) */}
          <select
            value={filters.verificationStatus}
            onChange={(e) => handleFilterChange('verificationStatus', e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">Estado verificación</option>
            <option value="PENDING">Pendiente</option>
            <option value="VERIFIED">Verificado</option>
            <option value="REJECTED">Rechazado</option>
          </select>
        </div>

        {/* Información de resultados */}
        <div className="mt-4 text-sm text-gray-600">
          Mostrando {users.length} de {totalElements} usuarios
        </div>
      </Card>

      {/* Tabla de usuarios */}
      <UserTable
        users={users}
        loading={loading}
        onEdit={handleEditUser}
        onDelete={handleDeleteUser}
        onToggleStatus={handleToggleStatus}
        currentPage={filters.page}
        totalPages={totalPages}
        onPageChange={(page) => handleFilterChange('page', page)}
        onSort={(field, direction) => {
          handleFilterChange('sortBy', field);
          handleFilterChange('sortDirection', direction);
        }}
      />

      {/* Modal de edición */}
      {editingUser && (
        <UserEditModal
          user={editingUser}
          isOpen={!!editingUser}
          onClose={() => setEditingUser(null)}
          onSave={handleUserSaved}
        />
      )}

      {/* Modal de confirmación de eliminación */}
      {deletingUser && (
        <ConfirmModal
          isOpen={!!deletingUser}
          title="Eliminar Usuario"
          message={`¿Estás seguro de que quieres eliminar al usuario "${deletingUser.username}"? Esta acción no se puede deshacer.`}
          confirmText="Eliminar"
          cancelText="Cancelar"
          onConfirm={confirmDelete}
          onCancel={() => setDeletingUser(null)}
          type="danger"
        />
      )}

      {/* Modal de confirmación de cambio de estado */}
      {toggleStatusUser && (
        <ConfirmModal
          isOpen={!!toggleStatusUser}
          title={`${toggleStatusUser.isActive ? 'Desactivar' : 'Activar'} Usuario`}
          message={`¿Estás seguro de que quieres ${toggleStatusUser.isActive ? 'desactivar' : 'activar'} al usuario "${toggleStatusUser.username}"?`}
          confirmText={toggleStatusUser.isActive ? 'Desactivar' : 'Activar'}
          cancelText="Cancelar"
          onConfirm={confirmToggleStatus}
          onCancel={() => setToggleStatusUser(null)}
          type={toggleStatusUser.isActive ? 'danger' : 'primary'}
        />
      )}
    </div>
  );
};

export default AdminUsersPage;