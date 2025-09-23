package com.songstock.dto;

import com.songstock.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductCreateDTO {
    
    @NotNull(message = "ID del álbum es requerido")
    private Long albumId;
    
    @NotNull(message = "ID de la categoría es requerido")
    private Long categoryId;
    
    @NotBlank(message = "SKU es requerido")
    @Size(max = 50, message = "SKU no puede exceder 50 caracteres")
    private String sku;
    
    @NotNull(message = "Tipo de producto es requerido")
    private ProductType productType;
    
    private ConditionType conditionType = ConditionType.NEW;
    
    @NotNull(message = "Precio es requerido")
    @DecimalMin(value = "0.01", message = "Precio debe ser mayor a 0")
    private BigDecimal price;
    
    @NotNull(message = "Cantidad en stock es requerida")
    @Min(value = 0, message = "Stock no puede ser negativo")
    private Integer stockQuantity;
    
    // Campos específicos para vinilos físicos
    private VinylSize vinylSize;
    private VinylSpeed vinylSpeed;
    private Integer weightGrams;
    
    // Campos específicos para productos digitales
    private String fileFormat;
    private Integer fileSizeMb;
    
    // Constructors
    public ProductCreateDTO() {}
    
    // Getters and Setters
    public Long getAlbumId() { return albumId; }
    public void setAlbumId(Long albumId) { this.albumId = albumId; }
    
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    
    public ProductType getProductType() { return productType; }
    public void setProductType(ProductType productType) { this.productType = productType; }
    
    public ConditionType getConditionType() { return conditionType; }
    public void setConditionType(ConditionType conditionType) { this.conditionType = conditionType; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public VinylSize getVinylSize() { return vinylSize; }
    public void setVinylSize(VinylSize vinylSize) { this.vinylSize = vinylSize; }
    
    public VinylSpeed getVinylSpeed() { return vinylSpeed; }
    public void setVinylSpeed(VinylSpeed vinylSpeed) { this.vinylSpeed = vinylSpeed; }
    
    public Integer getWeightGrams() { return weightGrams; }
    public void setWeightGrams(Integer weightGrams) { this.weightGrams = weightGrams; }
    
    public String getFileFormat() { return fileFormat; }
    public void setFileFormat(String fileFormat) { this.fileFormat = fileFormat; }
    
    public Integer getFileSizeMb() { return fileSizeMb; }
    public void setFileSizeMb(Integer fileSizeMb) { this.fileSizeMb = fileSizeMb; }
}