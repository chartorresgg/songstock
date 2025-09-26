// ================= ARCHIVO: src/components/layout/Header.tsx (ACTUALIZADO) =================
import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import Button from '../ui/Button';

const Header: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <header className="bg-white border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <div className="flex items-center">
            <Link to="/" className="text-xl font-bold text-gray-900">
              🎵 SongSto
            </Link>
          </div>
          
          <div className="flex items-center space-x-4">
            {user ? (
              <>
                <span className="text-sm text-gray-700">
                  Hola, {user.username} ({user.role})
                </span>
                <Button variant="secondary" size="sm" onClick={handleLogout}>
                  Cerrar Sesión
                </Button>
              </>
            ) : (
              <div className="space-x-2">
                <Link to="/login">
                  <Button variant="secondary" size="sm">
                    Iniciar Sesión
                  </Button>
                </Link>
                <Link to="/register-user">
                  <Button variant="primary" size="sm">
                    Registrarse
                  </Button>
                </Link>
                <Link to="/register-provider">
                  <Button 
                    variant="secondary" 
                    size="sm"
                    style={{ backgroundColor: '#059669', color: 'white' }}
                  >
                    Vender Discos
                  </Button>
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;