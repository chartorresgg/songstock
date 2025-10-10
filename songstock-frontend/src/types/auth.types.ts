// Este archivo define todos los tipos relacionados con autenticación

// Interfaz que representa un usuario en el sistema
export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  role: 'ADMIN' | 'PROVIDER' | 'CUSTOMER';
  isActive: boolean;
  createdAt: string; // Cambiado de number[] a string para usar formato ISO
  updatedAt: string; // Cambiado de number[] a string para usar formato ISO
}

// Datos que se envían al hacer login
export interface LoginCredentials {
  username: string;
  password: string;
}

// Datos que se envían al registrarse
export interface RegisterData {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string;
  role?: 'CUSTOMER' | 'PROVIDER';
  
  // Campos adicionales para proveedores
  businessName?: string;
  taxId?: string;
  address?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  country?: string;
}

// Respuesta que devuelve el backend después del login/register
export interface AuthResponse {
  success: boolean;
  message: string;
  data: {
    user: User;
    token: string;
  };
}

// Contexto de autenticación que se comparte en toda la app
export interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (credentials: LoginCredentials) => Promise<void>;
  register: (data: RegisterData) => Promise<void>;
  logout: () => void;
  loading: boolean;
}