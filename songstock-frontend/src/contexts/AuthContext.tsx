import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import authService from '../services/auth.service';
import { User, LoginCredentials, RegisterData, AuthContextType } from '../types/auth.types';

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Intentar cargar el usuario desde localStorage al iniciar
    const loadUserFromStorage = async () => {
      try {
        const storedToken = localStorage.getItem('token');
        const storedUser = localStorage.getItem('user');

        console.log('Loading from storage - Token exists:', !!storedToken); // Debug
        console.log('Loading from storage - User exists:', !!storedUser); // Debug

        if (storedToken && storedUser) {
          try {
            const parsedUser = JSON.parse(storedUser);
            setToken(storedToken);
            setUser(parsedUser);
            console.log('User loaded from storage:', parsedUser); // Debug
          } catch (parseError) {
            console.error('Error parsing stored user:', parseError);
            // Si hay error al parsear, limpiar el storage
            localStorage.removeItem('token');
            localStorage.removeItem('user');
          }
        } else {
          console.log('No stored credentials found'); // Debug
        }
      } catch (error) {
        console.error('Error loading user from storage:', error);
        // En caso de error, limpiar todo
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setToken(null);
        setUser(null);
      } finally {
        setLoading(false);
      }
    };

    loadUserFromStorage();
  }, []);

  const login = async (credentials: LoginCredentials) => {
    try {
      console.log('Attempting login with:', credentials.username); // Debug
      
      const response = await authService.login(credentials);
      
      console.log('Login response received:', response); // Debug

      if (response.success && response.data) {
        const { user: userData, token: userToken } = response.data;
        
        // Guardar en estado
        setUser(userData);
        setToken(userToken);
        
        // Guardar en localStorage
        localStorage.setItem('token', userToken);
        localStorage.setItem('user', JSON.stringify(userData));
        
        console.log('Login successful, user set:', userData); // Debug
      } else {
        throw new Error(response.message || 'Login failed');
      }
    } catch (error: any) {
      console.error('Login error in context:', error);
      // Limpiar cualquier dato residual
      setUser(null);
      setToken(null);
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      throw error;
    }
  };

  const register = async (data: RegisterData) => {
    try {
      console.log('Attempting registration'); // Debug
      
      const response = await authService.register(data);
      
      console.log('Register response received:', response); // Debug

      if (response.success && response.data) {
        const { user: userData, token: userToken } = response.data;
        
        // Guardar en estado
        setUser(userData);
        setToken(userToken);
        
        // Guardar en localStorage
        localStorage.setItem('token', userToken);
        localStorage.setItem('user', JSON.stringify(userData));
        
        console.log('Registration successful, user set:', userData); // Debug
      } else {
        throw new Error(response.message || 'Registration failed');
      }
    } catch (error: any) {
      console.error('Registration error in context:', error);
      // Limpiar cualquier dato residual
      setUser(null);
      setToken(null);
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      throw error;
    }
  };

  const logout = () => {
    console.log('Logging out'); // Debug
    authService.logout();
    setUser(null);
    setToken(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!user && !!token,
        login,
        register,
        logout,
        loading,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};