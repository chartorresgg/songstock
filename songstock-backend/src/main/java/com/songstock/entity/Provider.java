package com.songstock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa a un proveedor dentro del sistema.
 * Un proveedor está vinculado a un usuario (relación OneToOne),
 * y puede tener múltiples productos asociados (relación OneToMany).
 *
 * Contiene información de negocio como nombre, identificación tributaria,
 * dirección, estado de verificación y tasa de comisión.
 */
@Entity
@Table(name = "providers")
public class Provider {

    /** Identificador único del proveedor (Primary Key). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relación uno a uno con la entidad User.
     * Cada proveedor está asociado a exactamente un usuario.
     * - fetch = LAZY: se carga bajo demanda.
     * - unique = true: un usuario no puede ser asociado a más de un proveedor.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    /**
     * Nombre del negocio del proveedor.
     * Campo requerido con máximo 100 caracteres.
     */
    @Column(name = "business_name", nullable = false, length = 100)
    @NotBlank(message = "Nombre del negocio es requerido")
    private String businessName;

    /** Número de identificación tributaria (NIT, RUT, etc.). */
    @Column(name = "tax_id", length = 50)
    private String taxId;

    /** Dirección completa del proveedor (campo de texto libre). */
    @Column(columnDefinition = "TEXT")
    private String address;

    /** Ciudad del proveedor. */
    @Column(length = 50)
    private String city;

    /** Departamento/estado/región del proveedor. */
    @Column(length = 50)
    private String state;

    /** Código postal del proveedor. */
    @Column(name = "postal_code", length = 10)
    private String postalCode;

    /** País del proveedor (por defecto "Colombia"). */
    @Column(length = 50)
    private String country = "Colombia";

    /**
     * Estado de verificación del proveedor.
     * Puede ser PENDING, APPROVED o REJECTED (definidos en VerificationStatus).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    /** Fecha en la que se verificó el proveedor (nullable). */
    @Column(name = "verification_date")
    private LocalDateTime verificationDate;

    /**
     * Porcentaje de comisión aplicada a las ventas del proveedor.
     * Ejemplo: 10.00 equivale al 10%.
     * precision = 5, scale = 2 → Máximo 999.99
     */
    @Column(name = "commission_rate", precision = 5, scale = 2)
    private BigDecimal commissionRate = new BigDecimal("10.00");

    /** Fecha de creación del registro (se genera automáticamente). */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha de última actualización del registro (se actualiza automáticamente).
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relación uno a muchos con la entidad Product.
     * Un proveedor puede tener múltiples productos asociados.
     * CascadeType.ALL → operaciones en Provider afectan a sus productos.
     * fetch = LAZY → se cargan bajo demanda.
     * JsonIgnore → evita bucles infinitos en la serialización JSON.
     */
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Product> products;

    // -----------------------------
    // Constructores
    // -----------------------------

    /** Constructor vacío requerido por JPA. */
    public Provider() {
    }

    /**
     * Constructor con parámetros básicos.
     * 
     * @param user         Usuario asociado.
     * @param businessName Nombre del negocio.
     */
    public Provider(User user, String businessName) {
        this.user = user;
        this.businessName = businessName;
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

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public LocalDateTime getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(LocalDateTime verificationDate) {
        this.verificationDate = verificationDate;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
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

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
