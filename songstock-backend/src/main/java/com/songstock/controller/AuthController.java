package com.songstock.controller;

import com.songstock.dto.*;
import com.songstock.entity.User; // AGREGAR ESTE IMPORT
import com.songstock.service.AuthService;
import com.songstock.service.ProviderService;
import com.songstock.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder; // AGREGAR ESTE IMPORT
import org.springframework.web.bind.annotation.*;
import com.songstock.entity.ProviderInvitation;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // AGREGAR ESTA INYECCIÓN

    @Autowired
    AuthService authService;

    @Autowired
    ProviderService providerService;

    // ENDPOINT DE PRUEBA SIMPLE
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        logger.info("Test endpoint called");
        return ResponseEntity.ok("AuthController is working!");
    }

    @PostMapping("/test-password")
    public ResponseEntity<String> testPassword(@RequestBody Map<String, String> request) {
        String rawPassword = request.get("password");
        String storedHash = "$2a$10$..."; // El hash de la BD

        boolean matches = passwordEncoder.matches(rawPassword, storedHash);
        return ResponseEntity.ok("Password matches: " + matches);
    }

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

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logoutUser(HttpServletRequest request) {
        String token = parseJwt(request);
        if (token != null) {
            authService.logout(token);
        }

        return ResponseEntity.ok(ApiResponse.success("Sesión cerrada exitosamente"));
    }

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

                // Verificar si el password es correcto
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

    @GetMapping("/generate-hash")
    public ResponseEntity<Map<String, String>> generateHash() {
        String rawPassword = "admin123";
        String hash = passwordEncoder.encode(rawPassword);

        Map<String, String> result = new HashMap<>();
        result.put("rawPassword", rawPassword);
        result.put("generatedHash", hash);
        result.put("verification", String.valueOf(passwordEncoder.matches(rawPassword, hash)));

        return ResponseEntity.ok(result);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    @GetMapping("/invitation/{token}")
    public ResponseEntity<ApiResponse<ProviderInvitation>> getInvitationInfo(@PathVariable String token) {
        ProviderInvitation invitation = providerService.getInvitationByToken(token);
        return ResponseEntity.ok(ApiResponse.success("Información de invitación obtenida", invitation));
    }

}