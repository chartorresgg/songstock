package com.songstock.controller;

import com.songstock.dto.*;
import com.songstock.entity.User;
import com.songstock.service.AuthService;
import com.songstock.dto.CustomerRegistrationDTO;
import com.songstock.dto.LoginRequestDTO;
import com.songstock.dto.ProviderRegistrationDTO;
import com.songstock.dto.AuthResponseDTO;
import com.songstock.dto.UserRegistrationDTO;
import com.songstock.entity.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import com.songstock.service.UserService;
import com.songstock.service.PasswordResetService;
import com.songstock.entity.UserRole;
import com.songstock.service.ProviderService;
import com.songstock.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.songstock.entity.ProviderInvitation;
import com.songstock.dto.PasswordResetDTO;
import com.songstock.dto.ResetPasswordDTO;
import com.songstock.entity.PasswordResetToken;
import com.songstock.repository.PasswordResetTokenRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import java.time.LocalDateTime;

import java.util.Map;
import java.util.HashMap;

/**
 * Controlador encargado de gestionar la autenticación, registro de proveedores
 * y endpoints relacionados con la seguridad de usuarios.
 */
@RestController
@RequestMapping("/auth") // Prefijo base para todos los endpoints de autenticación
// 🔹 Anotaciones CORS removidas, ya que el filtro global maneja la
// configuración
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    // Repositorio de usuarios para consultas directas
    @Autowired
    private UserRepository userRepository;

    // Encoder de contraseñas (BCrypt, etc.)
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Servicio de autenticación (maneja login, logout, JWT)
    @Autowired
    AuthService authService;

    // Servicio de proveedores (invitaciones, registro, validación)
    @Autowired
    ProviderService providerService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordResetService passwordResetService;

    /**
     * Endpoint de prueba CORS.
     * Verifica que las configuraciones CORS estén funcionando correctamente.
     *
     * @return respuesta con mensaje y metadata
     */
    @PostMapping("/test-cors")
    public ResponseEntity<Map<String, String>> testCors() {
        logger.info("Test CORS endpoint called");
        Map<String, String> response = new HashMap<>();
        response.put("message", "CORS funcionando correctamente");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de prueba general del controlador.
     *
     * @return mensaje simple confirmando funcionamiento
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        logger.info("Test endpoint called");
        return ResponseEntity.ok("AuthController is working!");
    }

    /**
     * Autenticación de usuario.
     * Recibe credenciales (username/email + password) y devuelve un token JWT.
     *
     * @param loginRequest credenciales de inicio de sesión
     * @param request      request HTTP para obtener metadata (ej. IP, headers)
     * @return token JWT si las credenciales son válidas
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> authenticateUser(
            @Valid @RequestBody LoginRequestDTO loginRequest,
            HttpServletRequest request) {

        logger.info("Login attempt for user: {}", loginRequest.getUsernameOrEmail());

        try {
            AuthResponseDTO authResponse = authService.authenticateUser(loginRequest, request);
            logger.info("Login successful for user: {}", loginRequest.getUsernameOrEmail());

            return ResponseEntity.ok(ApiResponse.success(
                    "Usuario autenticado exitosamente",
                    authResponse));
        } catch (Exception e) {
            logger.error("Login failed for user: {}", loginRequest.getUsernameOrEmail(), e);
            return ResponseEntity.status(401).body(
                    ApiResponse.error("Credenciales inválidas"));
        }
    }

    /**
     * Registro de proveedor.
     * Permite a un usuario registrarse como proveedor, quedando pendiente
     * de verificación por un administrador.
     * Después del registro exitoso, autentica automáticamente al usuario.
     *
     * @param providerRequest datos del proveedor a registrar
     * @param request         request HTTP para obtener metadata
     * @return AuthResponseDTO con usuario y token JWT
     */
    @PostMapping("/register-provider")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> registerProvider(
            @Valid @RequestBody ProviderRegistrationDTO providerRequest,
            HttpServletRequest request) {

        logger.info("Provider registration attempt for: {}", providerRequest.getUsername());

        try {
            // 1. Registrar el proveedor
            providerService.registerProvider(providerRequest);
            logger.info("Provider registered successfully: {}", providerRequest.getUsername());

            // 2. Autenticar automáticamente al usuario recién registrado
            LoginRequestDTO loginRequest = new LoginRequestDTO();
            loginRequest.setUsernameOrEmail(providerRequest.getUsername());
            loginRequest.setPassword(providerRequest.getPassword());

            // 3. Obtener el token JWT y los datos del usuario
            AuthResponseDTO authResponse = authService.authenticateUser(loginRequest, request);

            logger.info("Provider auto-authenticated successfully: {}", providerRequest.getUsername());

            // 4. Devolver la respuesta con usuario y token
            return ResponseEntity.ok(ApiResponse.success(
                    "Proveedor registrado exitosamente. Pendiente de verificación por administrador.",
                    authResponse));

        } catch (Exception e) {
            logger.error("Provider registration failed for: {}", providerRequest.getUsername(), e);
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Error en registro: " + e.getMessage()));
        }
    }

    /**
     * Cierra la sesión del usuario.
     * Elimina o invalida el token JWT del request.
     *
     * @param request request HTTP con encabezado de autorización
     * @return mensaje de confirmación
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logoutUser(HttpServletRequest request) {
        String token = parseJwt(request);
        if (token != null) {
            authService.logout(token);
        }

        return ResponseEntity.ok(ApiResponse.success("Sesión cerrada exitosamente"));
    }

    /**
     * Extrae el token JWT desde el header Authorization del request.
     *
     * @param request request HTTP
     * @return token JWT sin el prefijo "Bearer ", o null si no existe
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    /**
     * Obtiene la información de una invitación de proveedor.
     *
     * @param token token único de invitación
     * @return información de la invitación
     */
    @GetMapping("/invitation/{token}")
    public ResponseEntity<ApiResponse<ProviderInvitation>> getInvitationInfo(@PathVariable String token) {
        ProviderInvitation invitation = providerService.getInvitationByToken(token);
        return ResponseEntity.ok(ApiResponse.success("Información de invitación obtenida", invitation));
    }

    /**
     * Endpoint de depuración para verificar existencia del usuario "admin".
     * Devuelve información sensible (⚠️ solo para DEBUG, remover en producción).
     *
     * @return información del usuario admin si existe
     */
    @GetMapping("/debug-admin")
    public ResponseEntity<Map<String, Object>> debugAdmin() {
        logger.info("Debug admin user called");

        try {
            User admin = userRepository.findByUsername("admin").orElse(null);
            Map<String, Object> debug = new HashMap<>();

            if (admin != null) {
                debug.put("found", true);
                debug.put("id", admin.getId());
                debug.put("username", admin.getUsername());
                debug.put("email", admin.getEmail());
                debug.put("role", admin.getRole().toString());
                debug.put("isActive", admin.getIsActive());
                debug.put("passwordStartsWith", admin.getPassword().substring(0, 10) + "...");
                debug.put("passwordIsBCrypt", admin.getPassword().startsWith("$2"));

                boolean passwordMatches = passwordEncoder.matches("admin123", admin.getPassword());
                debug.put("passwordMatches", passwordMatches);
            } else {
                debug.put("found", false);
            }

            return ResponseEntity.ok(debug);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("errorClass", e.getClass().getSimpleName());
            logger.error("Error in debug-admin endpoint", e);
            return ResponseEntity.status(500).body(error);
        }
    }

    // ================= AGREGAR ESTE MÉTODO AL AuthController.java
    // =================

    /**
     * Registro de usuario regular (CUSTOMER).
     * Permite a un usuario registrarse como cliente/comprador.
     * No requiere verificación de administrador.
     *
     * @param userRequest datos del usuario a registrar
     * @return mensaje de confirmación
     */
    @PostMapping("/register-user")
    public ResponseEntity<ApiResponse<String>> registerUser(
            @Valid @RequestBody UserRegistrationDTO userRequest) {

        logger.info("User registration attempt for: {}", userRequest.getUsername());

        try {
            // Asegurar que el role sea CUSTOMER
            userRequest.setRole(UserRole.CUSTOMER);

            // Usar el UserService para crear el usuario
            userService.createUser(userRequest);

            logger.info("User registered successfully: {}", userRequest.getUsername());

            return ResponseEntity.ok(ApiResponse.success(
                    "Usuario registrado exitosamente. Ya puedes iniciar sesión."));

        } catch (Exception e) {
            logger.error("User registration failed for: {}", userRequest.getUsername(), e);
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Error en registro: " + e.getMessage()));
        }
    }

    // Agregar estas inyecciones junto con las otras @Autowired:
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    /**
     * Solicitar restablecimiento de contraseña.
     * Envía un token de recuperación al email del usuario.
     *
     * @param request datos con el email del usuario
     * @return mensaje de confirmación
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody PasswordResetDTO request) {

        logger.info("Password reset request for email: {}", request.getEmail());

        try {
            // USAR EL SERVICIO en lugar del repositorio directamente
            passwordResetService.createPasswordResetToken(request.getEmail());

            return ResponseEntity.ok(ApiResponse.success(
                    "Si el email existe en nuestro sistema, recibirás instrucciones para restablecer tu contraseña."));

        } catch (Exception e) {
            logger.error("Error processing password reset for: {}", request.getEmail(), e);
            return ResponseEntity.status(500).body(
                    ApiResponse.error("Error interno del servidor"));
        }
    }

    /**
     * Restablecer contraseña usando token.
     *
     * @param request datos con token y nueva contraseña
     * @return mensaje de confirmación
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordDTO request) {

        logger.info("Password reset attempt with token: {}", request.getToken());

        try {
            // USAR EL SERVICIO en lugar del repositorio directamente
            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());

            return ResponseEntity.ok(ApiResponse.success(
                    "Contraseña restablecida exitosamente. Ya puedes iniciar sesión con tu nueva contraseña."));

        } catch (Exception e) {
            logger.error("Error resetting password with token: {}", request.getToken(), e);
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Limpiar tokens expirados y usados.
     * Este método se puede llamar periódicamente o mediante un scheduler.
     *
     * @return cantidad de tokens eliminados
     */
    @PostMapping("/cleanup-tokens")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> cleanupExpiredTokens() {
        try {
            passwordResetTokenRepository.deleteExpiredAndUsedTokens(LocalDateTime.now());
            logger.info("Expired and used password reset tokens cleaned up");

            return ResponseEntity.ok(ApiResponse.success("Tokens expirados eliminados exitosamente"));
        } catch (Exception e) {
            logger.error("Error cleaning up expired tokens", e);
            return ResponseEntity.status(500).body(
                    ApiResponse.error("Error interno del servidor"));
        }
    }

    /**
     * Generar un token seguro para restablecimiento de contraseña.
     *
     * @return token aleatorio de 32 caracteres
     */
    private String generateSecureToken() {
        // Generar token aleatorio seguro
        java.security.SecureRandom random = new java.security.SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Registro de usuario comprador (CUSTOMER).
     * Permite a un usuario registrarse como comprador/cliente.
     * Después del registro exitoso, autentica automáticamente al usuario.
     *
     * @param customerRequest datos del comprador a registrar
     * @param request         request HTTP para obtener metadata
     * @return AuthResponseDTO con usuario y token JWT
     */
    @PostMapping("/register-customer")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> registerCustomer(
            @Valid @RequestBody CustomerRegistrationDTO customerRequest,
            HttpServletRequest request) {

        logger.info("Customer registration attempt for: {}", customerRequest.getUsername());

        try {
            // 1. Crear el usuario con rol CUSTOMER
            User user = userService.createUser(new UserRegistrationDTO(
                    customerRequest.getUsername(),
                    customerRequest.getEmail(),
                    customerRequest.getPassword(),
                    customerRequest.getFirstName(),
                    customerRequest.getLastName(),
                    UserRole.CUSTOMER));

            logger.info("Customer registered successfully: {}", customerRequest.getUsername());

            // 2. Autenticar automáticamente al usuario recién registrado
            LoginRequestDTO loginRequest = new LoginRequestDTO();
            loginRequest.setUsernameOrEmail(customerRequest.getUsername());
            loginRequest.setPassword(customerRequest.getPassword());

            // 3. Obtener el token JWT y los datos del usuario
            AuthResponseDTO authResponse = authService.authenticateUser(loginRequest, request);

            logger.info("Customer auto-authenticated successfully: {}", customerRequest.getUsername());

            // 4. Devolver la respuesta con usuario y token
            return ResponseEntity.ok(ApiResponse.success(
                    "Usuario registrado exitosamente. ¡Bienvenido a SongStock!",
                    authResponse));

        } catch (Exception e) {
            logger.error("Customer registration failed for: {}", customerRequest.getUsername(), e);
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Error en registro: " + e.getMessage()));
        }
    }
}
