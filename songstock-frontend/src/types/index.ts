export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: 'ADMIN' | 'PROVIDER' | 'CUSTOMER';
  isActive: boolean;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  type: string;
  userId: number;
  username: string;
  email: string;
  role: string;
  expiresIn: number;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
  timestamp?: string | number[];
}

export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

// Registro de Usuario Regular (Customer)
export interface UserRegistration {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
  role: 'CUSTOMER';
}

// Registro de Proveedor
export interface ProviderRegistration {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
  role: 'PROVIDER';
  businessName: string;
  taxId: string;
  address: string;
  city: string;
  state: string;
  country: string;
  postalCode: string;
}