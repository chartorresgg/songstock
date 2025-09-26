import api from './api';
import { API_ENDPOINTS } from '../constants/api';
import { AuthResponse, LoginRequest, ProviderRegistration, UserRegistration, ApiResponse } from '../types';

class AuthService {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    try {
      console.log('🔍 LOGIN DEBUG - Iniciando login');
      console.log('📨 Payload:', JSON.stringify(credentials, null, 2));

      const response = await api.post<ApiResponse<AuthResponse>>(API_ENDPOINTS.LOGIN, credentials);
      
      console.log('✅ Response exitoso:', response.status);
      
      if (response.data.success && response.data.data) {
        const authData = response.data.data;
        localStorage.setItem('token', authData.token);
        localStorage.setItem('user', JSON.stringify({
          id: authData.userId,
          username: authData.username,
          email: authData.email,
          role: authData.role
        }));
        return authData;
      }
      throw new Error(response.data.message || 'Error en la respuesta del servidor');
    } catch (error: any) {
      console.error('❌ ERROR EN LOGIN:', error);
      if (error.code === 'ERR_NETWORK') {
        throw new Error('Error de conexión. Verifica que el backend esté corriendo.');
      }
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error(error.message || 'Error al iniciar sesión');
    }
  }

  async registerUser(formData: any): Promise<void> {
    try {
      const backendData: UserRegistration = {
        username: formData.username,
        email: formData.email,
        password: formData.password,
        firstName: this.extractFirstName(formData.fullName),
        lastName: this.extractLastName(formData.fullName),
        phone: formData.phoneNumber || '',
        role: 'CUSTOMER'
      };

      console.log('🔍 USER REGISTER DEBUG:', JSON.stringify(backendData, null, 2));
      
      const response = await api.post<ApiResponse<any>>(API_ENDPOINTS.REGISTER_USER, backendData);
      
      console.log('✅ User register response:', response.data);
      
      if (!response.data.success) {
        throw new Error(response.data.message || 'Error al registrar usuario');
      }
      
      return;
    } catch (error: any) {
      console.error('❌ Error en registro de usuario:', error);
      if (error.code === 'ERR_NETWORK') {
        throw new Error('Error de conexión. Verifica que el backend esté corriendo.');
      }
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error(error.message || 'Error al registrar usuario');
    }
  }

  async registerProvider(formData: any): Promise<void> {
    try {
      const backendData: ProviderRegistration = {
        username: formData.username,
        email: formData.email,
        password: formData.password,
        firstName: this.extractFirstName(formData.fullName),
        lastName: this.extractLastName(formData.fullName),
        phone: formData.phoneNumber || '',
        role: 'PROVIDER',
        businessName: formData.businessName,
        taxId: formData.taxId || '',
        address: formData.address || '',
        city: formData.city || '',
        state: formData.state || '',
        country: formData.country || 'Colombia',
        postalCode: formData.postalCode || ''
      };

      console.log('🔍 PROVIDER REGISTER DEBUG:', JSON.stringify(backendData, null, 2));
      
      const response = await api.post<ApiResponse<any>>(API_ENDPOINTS.REGISTER_PROVIDER, backendData);
      
      console.log('✅ Provider register response:', response.data);
      
      if (!response.data.success) {
        throw new Error(response.data.message || 'Error al registrar proveedor');
      }
      
      return;
    } catch (error: any) {
      console.error('❌ Error en registro de proveedor:', error);
      if (error.code === 'ERR_NETWORK') {
        throw new Error('Error de conexión. Verifica que el backend esté corriendo.');
      }
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error(error.message || 'Error al registrar proveedor');
    }
  }

  private extractFirstName(fullName: string): string {
    if (!fullName) return '';
    const parts = fullName.trim().split(' ');
    return parts[0] || '';
  }

  private extractLastName(fullName: string): string {
    if (!fullName) return '';
    const parts = fullName.trim().split(' ');
    return parts.slice(1).join(' ') || '';
  }

  async logout(): Promise<void> {
    try {
      await api.post(API_ENDPOINTS.LOGOUT);
    } finally {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
  }

  getCurrentUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }
}

export default new AuthService();