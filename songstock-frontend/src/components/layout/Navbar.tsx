import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { useCart } from '../../contexts/CartContext';
import { 
  Music, 
  ShoppingCart, 
  User, 
  ListMusic,
  LogOut, 
  Menu, 
  X,
  LayoutDashboard,
} from 'lucide-react';
import { useState } from 'react';

const Navbar = () => {
  const { isAuthenticated, user, logout, loading  } = useAuth();
  const navigate = useNavigate();
  const { itemCount } = useCart();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (loading) {
    return (
      <nav className="bg-white shadow-md sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16 items-center">
            <div className="flex items-center">
              <Music className="h-8 w-8 text-primary-900" />
              <span className="text-2xl font-bold text-primary-900 ml-2">
                Song<span className="text-secondary-500">Stock</span>
              </span>
            </div>
          </div>
        </div>
      </nav>
    );
  }

  const getDashboardLink = () => {
    if (!user) return '/';
    switch (user.role) {
      case 'ADMIN':
        return '/admin/dashboard';
      case 'PROVIDER':
        return '/provider/dashboard';
      default:
        return '/profile';
    }
  };

  return (
    <nav className="bg-white shadow-md sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          {/* Logo */}
          <div className="flex items-center">
            <Link to="/" className="flex items-center space-x-2">
              <Music className="h-8 w-8 text-primary-900" />
              <span className="text-2xl font-bold text-primary-900">
                Song<span className="text-secondary-500">Stock</span>
              </span>
            </Link>
          </div>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center space-x-8">
            <Link 
              to="/catalog" 
              className="text-gray-700 hover:text-primary-900 font-medium transition"
            >
              Catálogo
            </Link>

            {isAuthenticated && user?.role === 'CUSTOMER' && (
                <Link
                  to="/compilations"
                  className="block px-4 py-3 text-gray-700 hover:bg-gray-100 font-medium transition"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Mis Recopilaciones
                </Link>
              )}



            {isAuthenticated ? (
              <>
                {user?.role === 'CUSTOMER' && (
                  <Link 
                    to="/cart" 
                    className="text-gray-700 hover:text-primary-900 flex items-center space-x-1 transition relative"
                  >
                    <ShoppingCart className="h-5 w-5" />
                    <span>Carrito</span>
                    {itemCount > 0 && (
                      <span className="absolute -top-2 -right-2 bg-secondary-500 text-white text-xs font-bold rounded-full h-5 w-5 flex items-center justify-center">
                        {itemCount}
                      </span>
                    )}
                  </Link>
                )}

                {(user?.role === 'ADMIN' || user?.role === 'PROVIDER') && (
                  <Link 
                    to={getDashboardLink()} 
                    className="text-gray-700 hover:text-primary-900 flex items-center space-x-1 transition"
                  >
                    <LayoutDashboard className="h-5 w-5" />
                    <span>Dashboard</span>
                  </Link>
                )}

                <div className="relative group">
                  <button className="flex items-center space-x-2 text-gray-700 hover:text-primary-900 transition">
                    <User className="h-5 w-5" />
                    <span>{user?.firstName || user?.username}</span>
                  </button>
                  
                  {/* Dropdown Menu */}
                  <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 invisible group-hover:visible opacity-0 group-hover:opacity-100 transition-all duration-200">
                    <Link
                      to="/profile"
                      className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                    >
                      Mi Perfil
                    </Link>
                    {user?.role === 'CUSTOMER' && (
                      <Link
                        to="/my-orders"
                        className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                      >
                        Mis Pedidos
                      </Link>
                    )}
                    {user?.role === 'PROVIDER' && (
                      <Link
                        to="/provider/orders"
                        className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                      >
                        Gestionar Pedidos
                      </Link>
                    )}
                    <button
                      onClick={handleLogout}
                      className="block w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-100"
                    >
                      <div className="flex items-center space-x-2">
                        <LogOut className="h-4 w-4" />
                        <span>Cerrar Sesión</span>
                      </div>
                    </button>
                  </div>
                </div>
              </>
            ) : (
              <>
                <Link 
                  to="/login" 
                  className="text-gray-700 hover:text-primary-900 font-medium transition"
                >
                  Iniciar Sesión
                </Link>
                <Link 
                  to="/register" 
                  className="bg-primary-900 text-white px-6 py-2 rounded-lg hover:bg-primary-800 transition font-medium"
                >
                  Registrarse
                </Link>
              </>
            )}
          </div>

          {/* Mobile menu button */}
          <div className="md:hidden flex items-center">
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="text-gray-700 hover:text-primary-900"
            >
              {mobileMenuOpen ? (
                <X className="h-6 w-6" />
              ) : (
                <Menu className="h-6 w-6" />
              )}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Navigation */}
      {mobileMenuOpen && (
        <div className="md:hidden bg-white border-t">
          <div className="px-2 pt-2 pb-3 space-y-1">
            <Link
              to="/catalog"
              className="block px-3 py-2 text-gray-700 hover:bg-gray-100 rounded-md"
              onClick={() => setMobileMenuOpen(false)}
            >
              Catálogo
            </Link>
            
            {isAuthenticated ? (
              <>
                {user?.role === 'CUSTOMER' && (
                  <Link
                    to="/cart"
                    className="block px-3 py-2 text-gray-700 hover:bg-gray-100 rounded-md"
                    onClick={() => setMobileMenuOpen(false)}
                  >
                    <div className="flex items-center justify-between">
                      <span>Carrito</span>
                      {itemCount > 0 && (
                        <span className="bg-secondary-500 text-white text-xs font-bold rounded-full h-5 w-5 flex items-center justify-center">
                          {itemCount}
                        </span>
                      )}
                    </div>
                  </Link>
                )}
                
                <Link
                  to={getDashboardLink()}
                  className="block px-3 py-2 text-gray-700 hover:bg-gray-100 rounded-md"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Dashboard
                </Link>
                
                <Link
                  to="/profile"
                  className="block px-3 py-2 text-gray-700 hover:bg-gray-100 rounded-md"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Mi Perfil
                </Link>
                
                <button
                  onClick={() => {
                    handleLogout();
                    setMobileMenuOpen(false);
                  }}
                  className="block w-full text-left px-3 py-2 text-red-600 hover:bg-gray-100 rounded-md"
                >
                  Cerrar Sesión
                </button>
              </>
            ) : (
              <>
                <Link
                  to="/login"
                  className="block px-3 py-2 text-gray-700 hover:bg-gray-100 rounded-md"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Iniciar Sesión
                </Link>
                <Link
                  to="/register"
                  className="block px-3 py-2 text-primary-900 font-medium hover:bg-gray-100 rounded-md"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Registrarse
                </Link>
              </>
            )}
          </div>
        </div>
      )}
    </nav>
  );
};

export default Navbar;