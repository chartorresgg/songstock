import axiosInstance from './axios.instance';
import { API_ENDPOINTS } from '../config/api.config';
import { User } from '../types/auth.types';
import { ApiResponse } from '../types/api.types';

/**
 * Esta interfaz define exactamente los campos que se pueden actualizar en el perfil.
 * Es importante que los nombres de las propiedades coincidan con lo que el backend espera.
 * 
 * Nota: No incluimos campos como 'id', 'username', 'role', 'isActive', 'createdAt' o 'updatedAt'
 * porque estos son manejados automáticamente por el backend y no deben ser modificados por el usuario.
 */
interface UpdateProfileData {
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;                   // Cambié 'phoneNumber' a 'phone' para coincidir con el backend
}

class UserService {
  /**
   * Este método obtiene la información completa del perfil del usuario autenticado.
   * El backend utiliza el token JWT para identificar qué usuario está solicitando su perfil.
   */
  async getProfile() {
    const response = await axiosInstance.get<ApiResponse<User>>(
      API_ENDPOINTS.USER_PROFILE
    );
    return response.data.data;
  }

  /**
   * Este método actualiza la información del perfil del usuario.
   * Solo se pueden modificar ciertos campos (firstName, lastName, email, phone).
   * 
   * @param userId - El ID del usuario cuyo perfil se va a actualizar
   * @param data - Los datos actualizados del perfil
   */
  async updateProfile(userId: number, data: UpdateProfileData) {
    const response = await axiosInstance.put<ApiResponse<User>>(
      `${API_ENDPOINTS.USERS}/${userId}`,
      data
    );
    return response.data.data;
  }

  /**
   * Este método permite cambiar la contraseña del usuario.
   * Requiere la contraseña antigua para verificar la identidad del usuario.
   * 
   * @param oldPassword - La contraseña actual del usuario
   * @param newPassword - La nueva contraseña deseada
   */
  async changePassword(oldPassword: string, newPassword: string) {
    const response = await axiosInstance.post<ApiResponse<any>>(
      '/auth/change-password',
      { oldPassword, newPassword }
    );
    return response.data;
  }
}

export default new UserService();