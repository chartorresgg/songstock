package com.songstock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un producto en el sistema.
 * 
 * Un producto siempre está asociado a:
 * - Un {@link Album} (ej. "The Dark Side of the Moon").
 * - Un {@link Provider} (proveedor que lo vende).
 * - Una {@link Category} (ej. Vinilo, CD, Digital).
 * 
 * Además:
 * - Puede ser físico (vinilo, CD) o digital (descarga).
 * - Maneja inventario, precio, condición y metadatos específicos según el tipo.
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador único del producto en la BD

    // ================= Relaciones principales =================

    /** Álbum al que pertenece el producto */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    @NotNull(message = "El álbum es obligatorio")
    private Album album;

    /** Proveedor que ofrece este producto */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    @NotNull(message = "El proveedor es obligatorio")
    private Provider provider;

    /** Categoría del producto (ej. Vinilo, CD, Merchandising) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "La categoría es obligatoria")
    private Category category;

    // ================= Datos generales =================

    @NotBlank(message = "El SKU es obligatorio")
    @Size(max = 50, message = "El SKU no puede exceder 50 caracteres")
    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku; // Identificador único interno del producto

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    @NotNull(message = "El tipo de producto es obligatorio")
    private ProductType productType; // Tipo de producto: físico o digital

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type")
    private ConditionType conditionType = ConditionType.NEW; // Estado físico (solo aplica a productos físicos usados)

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "Máx. 8 dígitos enteros y 2 decimales")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // Precio del producto

    @NotNull(message = "La cantidad en stock es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0; // Inventario disponible

    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold = 5;

    // ================= Campos específicos para vinilos físicos =================

    @Enumerated(EnumType.STRING)
    @Column(name = "vinyl_size")
    private VinylSize vinylSize; // Tamaño del vinilo (7", 12", etc.)

    @Enumerated(EnumType.STRING)
    @Column(name = "vinyl_speed")
    private VinylSpeed vinylSpeed; // Velocidad de reproducción (33, 45 RPM)

    @Column(name = "weight_grams")
    private Integer weightGrams; // Peso en gramos del vinilo

    // ================= Campos específicos para productos digitales
    // =================

    @Size(max = 20, message = "El formato no puede exceder 20 caracteres")
    @Column(name = "file_format", length = 20)
    private String fileFormat; // Formato de archivo (ej. MP3, FLAC, WAV)

    @Column(name = "file_size_mb")
    private Integer fileSizeMb; // Tamaño del archivo en MB

    // ================= Estado y metadatos =================

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // Indica si el producto está activo

    @Column(name = "featured", nullable = false)
    private Boolean featured = false; // Marca si es producto destacado

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // Fecha en que fue creado

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Última fecha de actualización

    // ================= Relaciones secundarias =================

    /** Imágenes asociadas al producto (galería) */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProductImage> images = new ArrayList<>();

    // ================= Constructores =================

    public Product() {
    }

    public Product(Album album, Provider provider, Category category, String sku,
            ProductType productType, BigDecimal price, Integer stockQuantity) {
        this.album = album;
        this.provider = provider;
        this.category = category;
        this.sku = sku;
        this.productType = productType;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // ================= Getters y Setters =================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public VinylSize getVinylSize() {
        return vinylSize;
    }

    public void setVinylSize(VinylSize vinylSize) {
        this.vinylSize = vinylSize;
    }

    public VinylSpeed getVinylSpeed() {
        return vinylSpeed;
    }

    public void setVinylSpeed(VinylSpeed vinylSpeed) {
        this.vinylSpeed = vinylSpeed;
    }

    public Integer getWeightGrams() {
        return weightGrams;
    }

    public void setWeightGrams(Integer weightGrams) {
        this.weightGrams = weightGrams;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public Integer getFileSizeMb() {
        return fileSizeMb;
    }

    public void setFileSizeMb(Integer fileSizeMb) {
        this.fileSizeMb = fileSizeMb;
    }

    public Integer getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(Integer lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
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

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    // ================= Métodos de utilidad =================

    /**
     * Agrega una imagen al producto manteniendo la relación bidireccional.
     */
    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    /**
     * Elimina una imagen del producto y actualiza la relación bidireccional.
     */
    public void removeImage(ProductImage image) {
        images.remove(image);
        image.setProduct(null);
    }

    /**
     * Verifica si el producto tiene stock disponible.
     */
    public boolean isInStock() {
        return stockQuantity > 0;
    }

    /**
     * Verifica si el producto es digital.
     */
    public boolean isDigital() {
        return productType == ProductType.DIGITAL;
    }

    /**
     * Verifica si el producto es físico.
     */
    public boolean isPhysical() {
        return productType == ProductType.PHYSICAL;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", sku='" + sku + '\'' +
                ", productType=" + productType +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", isActive=" + isActive +
                '}';
    }
}
