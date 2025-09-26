package com.songstock.controller;

import com.songstock.dto.*;
import com.songstock.entity.User;
import com.songstock.service.AuthService;
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
     *
     * @param providerRequest datos del proveedor a registrar
     * @return mensaje de confirmación
     */
    @PostMapping("/register-provider")
    public ResponseEntity<ApiResponse<String>> registerProvider(
            @Valid @RequestBody ProviderRegistrationDTO providerRequest) {

        logger.info("Provider registration attempt for: {}", providerRequest.getUsername());

        try {
            providerService.registerProvider(providerRequest);
            logger.info("Provider registered successfully: {}", providerRequest.getUsername());

            return ResponseEntity.ok(ApiResponse.success(
                    "Proveedor registrado exitosamente. Pendiente de verificación por administrador."));
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
}
