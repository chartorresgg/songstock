package com.songstock.service;

import com.songstock.dto.AuthResponseDTO;
import com.songstock.dto.LoginRequestDTO;
import com.songstock.entity.User;
import com.songstock.entity.UserSession;
import com.songstock.repository.UserSessionRepository;
import com.songstock.security.JwtUtils;
import com.songstock.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * Servicio de autenticación de usuarios.
 * Maneja login, generación de tokens JWT, creación de sesiones y logout.
 */
@Service
@Transactional
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    UserSessionRepository userSessionRepository;

    @Autowired
    JwtUtils jwtUtils;

    /**
     * Autenticar usuario y generar tokens (JWT + Refresh).
     */
    public AuthResponseDTO authenticateUser(LoginRequestDTO loginRequest, HttpServletRequest request) {
        // Autenticación con Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Datos del usuario autenticado
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Generación de tokens
        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails.getUsername());

        // Obtener usuario desde BD
        User user = userService.getUserById(userDetails.getId());

        // Crear sesión en base de datos
        UserSession session = new UserSession();
        session.setUser(user);
        session.setSessionToken(jwt);
        session.setRefreshToken(refreshToken);
        session.setExpiresAt(LocalDateTime.now().plusSeconds(jwtUtils.getExpirationTime() / 1000));
        session.setIpAddress(getClientIpAddress(request));
        session.setUserAgent(request.getHeader("User-Agent"));

        userSessionRepository.save(session);

        // Retornar DTO con tokens y datos del usuario
        return new AuthResponseDTO(
                jwt,
                refreshToken,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                user.getRole(),
                jwtUtils.getExpirationTime());
    }

    /**
     * Cerrar sesión actual (invalida un token específico).
     */
    public void logout(String token) {
        userSessionRepository.deactivateSession(token);
    }

    /**
     * Cerrar todas las sesiones activas de un usuario.
     */
    public void logoutAllSessions(Long userId) {
        userSessionRepository.deactivateAllUserSessions(userId);
    }

    /**
     * Obtener IP del cliente, considerando cabecera X-Forwarded-For.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }
}
