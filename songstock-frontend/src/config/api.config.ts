export const API_CONFIG = {
    BASE_URL: 'http://localhost:8080/api/v1',
    TIMEOUT: 10000,
  };
  
  export const API_ENDPOINTS = {
    // Auth
    LOGIN: '/auth/login',
    REGISTER: '/auth/register-provider',
    LOGOUT: '/auth/logout',
    
    // Users
    USERS: '/users',
    USER_PROFILE: '/users/profile',
    
    // Products - CORREGIDO
    PRODUCTS: '/products',  // Este es el endpoint correcto
    
    // Providers
    PROVIDERS: '/providers',
    PROVIDER_STATS: '/providers/stats',
    PROVIDER_CATALOG: '/providers/catalog',
    
    // Albums
    ALBUMS: '/albums',
    
    // Artists
    ARTISTS: '/artists',
    
    // Genres
    GENRES: '/genres',
    
    // Categories
    CATEGORIES: '/categories',
  };