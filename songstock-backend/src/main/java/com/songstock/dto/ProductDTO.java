package com.songstock.dto;

import com.songstock.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductDTO {

    private Long id;

    @NotNull(message = "El álbum es obligatorio")
    private Long albumId;

    private String albumTitle; // Para respuestas
    private String artistName; // Para respuestas

    private Long providerId;

    private String providerName; // Para respuestas

    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;

    private String categoryName; // Para respuestas

    @NotBlank(message = "El SKU es obligatorio")
    @Size(max = 50, message = "El SKU no puede exceder 50 caracteres")
    private String sku;

    @NotNull(message = "El tipo de producto es obligatorio")
    private ProductType productType;

    private ConditionType conditionType = ConditionType.NEW;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 8 dígitos enteros y 2 decimales")
    private BigDecimal price;

    @NotNull(message = "La cantidad en stock es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer stockQuantity = 0;

    // Campos específicos para vinilos físicos
    private VinylSize vinylSize;
    private VinylSpeed vinylSpeed;
    private Integer weightGrams;

    // Campos específicos para productos digitales
    @Size(max = 20, message = "El formato de archivo no puede exceder 20 caracteres")
    private String fileFormat;
    private Integer fileSizeMb;

    private Boolean isActive;
    private Boolean featured;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Información adicional para respuestas
    private List<ProductImageDTO> images;
    private List<ProductDTO> alternativeFormats; // Para la historia de usuario

    // Constructores
    public ProductDTO() {
    }

    public ProductDTO(Long albumId, Long providerId, Long categoryId, String sku,
            ProductType productType, BigDecimal price, Integer stockQuantity) {
        this.albumId = albumId;
        this.providerId = providerId;
        this.categoryId = categoryId;
        this.sku = sku;
        this.productType = productType;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public List<ProductImageDTO> getImages() {
        return images;
    }

    public void setImages(List<ProductImageDTO> images) {
        this.images = images;
    }

    public List<ProductDTO> getAlternativeFormats() {
        return alternativeFormats;
    }

    public void setAlternativeFormats(List<ProductDTO> alternativeFormats) {
        this.alternativeFormats = alternativeFormats;
    }

    // Métodos de utilidad
    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }

    public boolean isDigital() {
        return productType == ProductType.DIGITAL;
    }

    public boolean isPhysical() {
        return productType == ProductType.PHYSICAL;
    }
}
