package com.songstock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa a los usuarios dentro del sistema.
 * Un usuario puede ser de tipo ADMIN, PROVIDER o CUSTOMER (según
 * {@link UserRole}).
 * Contiene información personal, credenciales y relaciones con proveedores y
 * sesiones.
 */
@Entity
@Table(name = "users")
public class User {

    /** Identificador único del usuario (Primary Key). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre de usuario único. */
    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "Username es requerido")
    @Size(min = 3, max = 50, message = "Username debe tener entre 3 y 50 caracteres")
    private String username;

    /** Correo electrónico único y obligatorio. */
    @Column(unique = true, nullable = false, length = 100)
    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe ser válido")
    private String email;

    /**
     * Contraseña encriptada del usuario.
     * Se marca con {@link JsonProperty.Access#WRITE_ONLY} para que no se exponga en
     * respuestas JSON.
     */
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Password es requerido")
    @Size(min = 6, message = "Password debe tener al menos 6 caracteres")
    private String password;

    /** Nombre del usuario. */
    @Column(name = "first_name", nullable = false, length = 50)
    @NotBlank(message = "Nombre es requerido")
    private String firstName;

    /** Apellido del usuario. */
    @Column(name = "last_name", nullable = false, length = 50)
    @NotBlank(message = "Apellido es requerido")
    private String lastName;

    /** Teléfono de contacto del usuario. */
    @Column(length = 20)
    private String phone;

    /** Rol del usuario dentro del sistema (ADMIN, PROVIDER o CUSTOMER). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.CUSTOMER;

    /** Estado de la cuenta (activa o inactiva). */
    @Column(name = "is_active")
    private Boolean isActive = true;

    /** Fecha de creación del registro (se asigna automáticamente). */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha de última actualización del registro (se actualiza automáticamente).
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Relación 1:1 con {@link Provider}, si el usuario es un proveedor. */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Provider provider;

    /**
     * Relación 1:N con {@link UserSession}, para manejar sesiones activas del
     * usuario.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserSession> sessions;

    // -----------------------------
    // Constructores
    // -----------------------------

    /** Constructor vacío requerido por JPA. */
    public User() {
    }

    /**
     * Constructor con parámetros principales.
     */
    public User(String username, String email, String password, String firstName, String lastName, UserRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public List<UserSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<UserSession> sessions) {
        this.sessions = sessions;
    }
}
