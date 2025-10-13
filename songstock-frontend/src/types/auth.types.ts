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
  createdAt: string;
  updatedAt: string;
}

// Datos que se envían al hacer login
export interface LoginCredentials {
  username: string;
  password: string;
}

// ✅ Datos que se envían al registrarse - ACTUALIZADO
// Los nombres de los campos coinciden EXACTAMENTE con ProviderRegistrationDTO del backend
export interface RegisterData {
  // Datos del usuario
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string;              // ✅ Cambio: phoneNumber → phone
  
  // Datos del proveedor
  businessName?: string;       // ✅ Cambio: storeName → businessName (OBLIGATORIO en backend)
  taxId?: string;
  address?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  country?: string;            // ✅ Default: "Colombia" en el backend
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
  register: (data: RegisterData, userType?: 'customer' | 'provider') => Promise<void>;
  logout: () => void;
  loading: boolean;
}