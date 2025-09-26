package com.songstock.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa una invitación enviada a un posible proveedor.
 * Almacena información del negocio, datos personales y el estado del proceso de
 * invitación.
 */
@Entity
@Table(name = "provider_invitations")
public class ProviderInvitation {

    /** Identificador único de la invitación. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Correo electrónico del proveedor invitado. */
    @Column(nullable = false)
    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe ser válido")
    private String email;

    /** Nombre del negocio del proveedor invitado. */
    @Column(name = "business_name", nullable = false)
    @NotBlank(message = "Nombre del negocio es requerido")
    private String businessName;

    /** Nombre del contacto principal. */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /** Apellido del contacto principal. */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /** Teléfono de contacto del proveedor. */
    private String phone;

    /** Token único que identifica la invitación. */
    @Column(name = "invitation_token", unique = true, nullable = false)
    private String invitationToken;

    /** Fecha de expiración de la invitación. */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /** Estado actual de la invitación (PENDING, ACCEPTED, REJECTED). */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvitationStatus status = InvitationStatus.PENDING;

    /** ID del administrador que envió la invitación. */
    @Column(name = "invited_by", nullable = false)
    private Long invitedBy;

    /** ID del proveedor que completó el registro (cuando aplica). */
    @Column(name = "completed_by")
    private Long completedBy;

    /** Mensaje opcional de invitación. */
    @Column(columnDefinition = "TEXT")
    private String message;

    /** Fecha en que se creó la invitación. */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Fecha de última actualización de la invitación. */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Fecha en que la invitación fue completada. */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // -----------------------------
    // Constructores
    // -----------------------------

    /** Constructor vacío requerido por JPA. */
    public ProviderInvitation() {
    }

    /**
     * Constructor con los campos principales.
     */
    public ProviderInvitation(String email, String businessName, String firstName,
            String lastName, Long invitedBy, String invitationToken,
            LocalDateTime expiresAt) {
        this.email = email;
        this.businessName = businessName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.invitedBy = invitedBy;
        this.invitationToken = invitationToken;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
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

    public String getInvitationToken() {
        return invitationToken;
    }

    public void setInvitationToken(String invitationToken) {
        this.invitationToken = invitationToken;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public Long getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(Long invitedBy) {
        this.invitedBy = invitedBy;
    }

    public Long getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(Long completedBy) {
        this.completedBy = completedBy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
