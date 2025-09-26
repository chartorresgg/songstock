import { useState, useEffect } from 'react';
import authService from '../services/authService';
import { User } from '../types';

export const useAuth = () => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const currentUser = authService.getCurrentUser();
    setUser(currentUser);
    setLoading(false);
  }, []);

  const login = async (usernameOrEmail: string, password: string) => {
    const response = await authService.login({ usernameOrEmail, password });
    const userData = authService.getCurrentUser();
    setUser(userData);
    return response;
  };

  const logout = async () => {
    await authService.logout();
    setUser(null);
  };

  const registerProvider = async (data: any) => {
    return await authService.registerProvider(data);
  };

  return {
    user,
    loading,
    login,
    logout,
    registerProvider,
    isAuthenticated: authService.isAuthenticated(),
  };
};