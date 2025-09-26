package com.songstock.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa una sesión activa de un usuario en el sistema.
 * Almacena información sobre el token de sesión, refresh token,
 * expiración, estado, dirección IP, agente de usuario y marcas de tiempo.
 */
@Entity
@Table(name = "user_sessions")
public class UserSession {

    /** Identificador único de la sesión (Primary Key). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relación Many-to-One con el usuario.
     * Cada usuario puede tener múltiples sesiones abiertas (por ejemplo, en
     * distintos dispositivos).
     * - fetch = LAZY: se carga bajo demanda.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Token de sesión (ejemplo: JWT o UUID) que identifica de manera única
     * la sesión activa del usuario.
     */
    @Column(name = "session_token", nullable = false)
    private String sessionToken;

    /**
     * Refresh token opcional para renovar la sesión cuando el sessionToken expira.
     */
    @Column(name = "refresh_token")
    private String refreshToken;

    /**
     * Fecha y hora en la que la sesión expira.
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Estado de la sesión: activa o inactiva.
     * Por defecto se inicializa en true.
     */
    @Column(name = "is_active")
    private Boolean isActive = true;

    /**
     * Dirección IP desde la cual se inició la sesión.
     * Máximo 45 caracteres para soportar IPv4 e IPv6.
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * Información del agente de usuario (User-Agent) desde donde se conectó,
     * como navegador o aplicación cliente.
     */
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    /** Fecha de creación de la sesión (generada automáticamente). */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Fecha de última actualización de la sesión (actualizada automáticamente). */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // -----------------------------
    // Constructores
    // -----------------------------

    /** Constructor vacío requerido por JPA. */
    public UserSession() {
    }

    /**
     * Constructor con parámetros principales.
     * 
     * @param user         Usuario asociado a la sesión.
     * @param sessionToken Token de sesión.
     * @param expiresAt    Fecha de expiración.
     */
    public UserSession(User user, String sessionToken, LocalDateTime expiresAt) {
        this.user = user;
        this.sessionToken = sessionToken;
        this.expiresAt = expiresAt;
    }

    // -----------------------------
    // Getters y Setters
    // -----------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
