import axiosInstance from './axios.instance';
import { API_ENDPOINTS } from '../config/api.config';
import { LoginCredentials, RegisterData, AuthResponse, User } from '../types/auth.types';

class AuthService {
  // Función auxiliar para convertir el formato de fecha de Java a ISO string
  private convertJavaDateToISO(dateArray: number[] | string): string {
    // Si ya es un string, devolverlo tal cual
    if (typeof dateArray === 'string') {
      return dateArray;
    }
    
    // Si es un array de números [year, month, day, hour, minute, second]
    // convertirlo a formato ISO string
    if (Array.isArray(dateArray) && dateArray.length >= 3) {
      const [year, month, day, hour = 0, minute = 0, second = 0] = dateArray;
      const date = new Date(year, month - 1, day, hour, minute, second);
      return date.toISOString();
    }
    
    // Si no es ninguno de los anteriores, devolver la fecha actual
    return new Date().toISOString();
  }

  // Función auxiliar para normalizar el objeto User que viene del backend
  private normalizeUser(user: any): User {
    return {
      ...user,
      createdAt: this.convertJavaDateToISO(user.createdAt),
      updatedAt: this.convertJavaDateToISO(user.updatedAt)
    };
  }

  async login(credentials: LoginCredentials): Promise<AuthResponse> {
    const response = await axiosInstance.post<any>(
      API_ENDPOINTS.LOGIN,
      credentials
    );

    // Normalizar el usuario antes de devolverlo
    return {
      success: response.data.success,
      message: response.data.message,
      data: {
        user: this.normalizeUser(response.data.data.user),
        token: response.data.data.token
      }
    };
  }

  async register(data: RegisterData): Promise<AuthResponse> {
    const response = await axiosInstance.post<any>(
      API_ENDPOINTS.REGISTER,
      data
    );

    // Normalizar el usuario antes de devolverlo
    return {
      success: response.data.success,
      message: response.data.message,
      data: {
        user: this.normalizeUser(response.data.data.user),
        token: response.data.data.token
      }
    };
  }

  async getCurrentUser(): Promise<User> {
    const response = await axiosInstance.get<any>(
      API_ENDPOINTS.USER_PROFILE
    );
    
    // Normalizar el usuario antes de devolverlo
    return this.normalizeUser(response.data.data);
  }

  logout(): void {
    // Limpiar el token del localStorage
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }
}

export default new AuthService();