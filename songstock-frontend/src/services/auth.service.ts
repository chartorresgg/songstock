import axiosInstance from './axios.instance';
import { API_ENDPOINTS } from '../config/api.config';
import { LoginCredentials, AuthResponse, User } from '../types/auth.types';

class AuthService {
  // Función auxiliar para convertir el formato de fecha de Java a ISO string
  private convertJavaDateToISO(dateArray: number[] | string | undefined): string {
    if (!dateArray) {
      return new Date().toISOString();
    }
    
    if (typeof dateArray === 'string') {
      return dateArray;
    }
    
    if (Array.isArray(dateArray) && dateArray.length >= 3) {
      const [year, month, day, hour = 0, minute = 0, second = 0] = dateArray;
      const date = new Date(year, month - 1, day, hour, minute, second);
      return date.toISOString();
    }
    
    return new Date().toISOString();
  }

  // Función auxiliar para normalizar el objeto User que viene del backend
  private normalizeUser(user: any): User {
    if (!user) {
      throw new Error('User data is missing from response');
    }

    return {
      id: user.id,
      username: user.username,
      email: user.email,
      firstName: user.firstName || user.first_name || '',
      lastName: user.lastName || user.last_name || '',
      phone: user.phone || '',
      role: user.role,
      isActive: user.isActive !== undefined ? user.isActive : user.is_active !== undefined ? user.is_active : true,
      createdAt: this.convertJavaDateToISO(user.createdAt || user.created_at),
      updatedAt: this.convertJavaDateToISO(user.updatedAt || user.updated_at)
    };
  }

  async login(credentials: LoginCredentials): Promise<AuthResponse> {
    try {
      const response = await axiosInstance.post<any>(
        API_ENDPOINTS.LOGIN,
        credentials
      );

      console.log('Login response:', response.data);

      if (!response.data || !response.data.data) {
        throw new Error('Invalid response structure from server');
      }

      const userData = response.data.data.user || response.data.data;
      const token = response.data.data.token;

      if (!userData) {
        throw new Error('User data is missing from login response');
      }

      if (!token) {
        throw new Error('Token is missing from login response');
      }

      return {
        success: response.data.success !== undefined ? response.data.success : true,
        message: response.data.message || 'Login successful',
        data: {
          user: this.normalizeUser(userData),
          token: token
        }
      };
    } catch (error: any) {
      console.error('Login error details:', error.response?.data || error.message);
      throw error;
    }
  }

  async register(data: any): Promise<AuthResponse> {
    try {
      // Determinar el endpoint según los datos
      const isProvider = data.businessName !== undefined && data.businessName !== '';
      const endpoint = isProvider ? API_ENDPOINTS.REGISTER : '/auth/register-customer';
      
      console.log('Registering as:', isProvider ? 'PROVIDER' : 'CUSTOMER');
      console.log('Using endpoint:', endpoint);
      
      const response = await axiosInstance.post<any>(endpoint, data);

      console.log('Register response:', response.data);

      if (!response.data || !response.data.data) {
        throw new Error('Invalid response structure from server');
      }

      const userData = response.data.data.user || response.data.data;
      const token = response.data.data.token;

      if (!userData) {
        throw new Error('User data is missing from register response');
      }

      if (!token) {
        throw new Error('Token is missing from register response');
      }

      return {
        success: response.data.success !== undefined ? response.data.success : true,
        message: response.data.message || 'Registration successful',
        data: {
          user: this.normalizeUser(userData),
          token: token
        }
      };
    } catch (error: any) {
      console.error('Register error details:', error.response?.data || error.message);
      throw error;
    }
  }

  async getCurrentUser(): Promise<User> {
    try {
      const response = await axiosInstance.get<any>(
        API_ENDPOINTS.USER_PROFILE
      );
      
      console.log('Current user response:', response.data);

      const userData = response.data.data || response.data;
      return this.normalizeUser(userData);
    } catch (error: any) {
      console.error('Get current user error:', error.response?.data || error.message);
      throw error;
    }
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }
}

export default new AuthService();