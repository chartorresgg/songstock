// ================= ARCHIVO: src/services/authService.ts (CON DEBUG MEJORADO) =================
import api from './api';
import { API_ENDPOINTS } from '../constants/api';
import { AuthResponse, LoginRequest, ProviderRegistration, ApiResponse } from '../types';

class AuthService {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    try {
      // ⭐ AGREGAMOS LOGS DETALLADOS PARA DEBUG
      console.log('🔍 LOGIN DEBUG - Iniciando login');
      console.log('📨 Payload que se va a enviar:', JSON.stringify(credentials, null, 2));
      console.log('🌐 URL completa:', `${api.defaults.baseURL}${API_ENDPOINTS.LOGIN}`);
      console.log('📋 Headers que se van a enviar:', api.defaults.headers);

      const response = await api.post<ApiResponse<AuthResponse>>(API_ENDPOINTS.LOGIN, credentials);
      
      console.log('✅ Response exitoso recibido:', response.status);
      console.log('📄 Response data:', response.data);
      
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
      console.error('❌ ERROR COMPLETO EN LOGIN:', error);
      
      if (error.response) {
        console.error('📋 Error response data:', error.response.data);
        console.error('📊 Error response status:', error.response.status);
        console.error('📨 Error response headers:', error.response.headers);
      }
      
      if (error.request) {
        console.error('📤 Error request:', error.request);
      }
      
      if (error.code === 'ERR_NETWORK') {
        throw new Error('Error de conexión. Verifica que el backend esté corriendo.');
      }
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error(error.message || 'Error al iniciar sesión');
    }
  }

  async registerProvider(formData: any): Promise<void> {
    try {
      // Transformar los datos del formulario al formato que espera el backend
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

      console.log('🔍 REGISTER DEBUG - Payload:', JSON.stringify(backendData, null, 2));
      
      const response = await api.post<ApiResponse<any>>(API_ENDPOINTS.REGISTER_PROVIDER, backendData);
      
      console.log('✅ Register response:', response.data);
      
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