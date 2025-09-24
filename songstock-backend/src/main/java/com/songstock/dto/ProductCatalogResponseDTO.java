package com.songstock.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para productos del catálogo
 * Incluye información completa del producto para mostrar en el catálogo
 */
public class ProductCatalogResponseDTO {

    private Long id;
    private String sku;

    // Información del álbum
    private Long albumId;
    private String albumTitle;
    private String artistName;
    private Integer releaseYear;
    private String albumCover; // URL de la imagen

    // Información del producto
    private String productType; // PHYSICAL, DIGITAL
    private String conditionType; // NEW, LIKE_NEW, etc.
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean isAvailable; // true si stock > 0

    // Información específica de vinilos
    private String vinylSize; // 7_INCH, 10_INCH, 12_INCH
    private String vinylSpeed; // 33_RPM, 45_RPM, 78_RPM
    private Integer weightGrams;

    // Información específica de digitales
    private String fileFormat; // MP3, FLAC, WAV
    private Integer fileSizeMb;

    // Información adicional
    private Boolean featured;
    private Boolean isActive;
    private String description;

    // Información del proveedor
    private Long providerId;
    private String providerBusinessName;

    // Información de categoría
    private Long categoryId;
    private String categoryName;

    // Información de género
    private Long genreId;
    private String genreName;

    // Metadatos
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor vacío
    public ProductCatalogResponseDTO() {
    }

    // Constructor completo
    public ProductCatalogResponseDTO(Long id, String sku, Long albumId, String albumTitle,
            String artistName, Integer releaseYear, String productType,
            String conditionType, BigDecimal price, Integer stockQuantity,
            Boolean featured, Boolean isActive, Long providerId,
            String providerBusinessName, Long categoryId, String categoryName,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.sku = sku;
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.artistName = artistName;
        this.releaseYear = releaseYear;
        this.productType = productType;
        this.conditionType = conditionType;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.isAvailable = stockQuantity != null && stockQuantity > 0;
        this.featured = featured;
        this.isActive = isActive;
        this.providerId = providerId;
        this.providerBusinessName = providerBusinessName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(String albumCover) {
        this.albumCover = albumCover;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
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
        this.isAvailable = stockQuantity != null && stockQuantity > 0;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getVinylSize() {
        return vinylSize;
    }

    public void setVinylSize(String vinylSize) {
        this.vinylSize = vinylSize;
    }

    public String getVinylSpeed() {
        return vinylSpeed;
    }

    public void setVinylSpeed(String vinylSpeed) {
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

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public String getProviderBusinessName() {
        return providerBusinessName;
    }

    public void setProviderBusinessName(String providerBusinessName) {
        this.providerBusinessName = providerBusinessName;
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

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
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

    @Override
    public String toString() {
        return "ProductCatalogResponseDTO{" +
                "id=" + id +
                ", sku='" + sku + '\'' +
                ", albumTitle='" + albumTitle + '\'' +
                ", artistName='" + artistName + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", isAvailable=" + isAvailable +
                ", featured=" + featured +
                '}';
    }
}