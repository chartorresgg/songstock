export const API_BASE_URL = 'http://localhost:8080/api/v1';

export const API_ENDPOINTS = {
  // Auth endpoints
  LOGIN: '/auth/login',
  REGISTER_USER: '/auth/register-user',
  REGISTER_PROVIDER: '/auth/register-provider',
  LOGOUT: '/auth/logout',
  TEST_CORS: '/auth/test-cors',
  TEST: '/auth/test',
  
  // User endpoints
  USERS: '/users',
  USER_PROFILE: '/users/profile',
  
  // Provider endpoints  
  PROVIDERS: '/providers',
  VERIFY_PROVIDER: '/admin/providers/{id}/verify',
  
  // Admin endpoints
  ADMIN_USERS: '/admin/users',
  ADMIN_PROVIDERS: '/admin/providers',
  
  // Product endpoints
  PRODUCTS: '/products',
  PRODUCTS_SEARCH: '/products/search',
  ALTERNATIVE_FORMATS: '/products/{id}/alternative-formats',
};
