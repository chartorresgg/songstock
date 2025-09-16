package com.songstock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "SKU es requerido")
    @Size(max = 50, message = "SKU no puede exceder 50 caracteres")
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private ProductType productType;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type")
    private ConditionType conditionType = ConditionType.NEW;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Precio es requerido")
    @DecimalMin(value = "0.01", message = "Precio debe ser mayor a 0")
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    @Min(value = 0, message = "Stock no puede ser negativo")
    private Integer stockQuantity = 0;

    // Campos específicos para vinilos físicos
    @Enumerated(EnumType.STRING)
    @Column(name = "vinyl_size")
    private VinylSize vinylSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "vinyl_speed")
    private VinylSpeed vinylSpeed;

    @Column(name = "weight_grams")
    private Integer weightGrams;

    // Campos específicos para productos digitales
    @Column(name = "file_format", length = 20)
    private String fileFormat;

    @Column(name = "file_size_mb")
    private Integer fileSizeMb;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "featured")
    private Boolean featured = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProductImage> productImages;

    // Constructors
    public Product() {
    }

    public Product(Album album, Provider provider, Category category, String sku, ProductType productType,
            BigDecimal price) {
        this.album = album;
        this.provider = provider;
        this.category = category;
        this.sku = sku;
        this.productType = productType;
        this.price = price;
    }

    // Getters and Setters
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

    public List<ProductImage> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<ProductImage> productImages) {
        this.productImages = productImages;
    }
}