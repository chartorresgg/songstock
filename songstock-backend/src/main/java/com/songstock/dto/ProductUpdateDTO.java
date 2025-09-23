package com.songstock.dto;

import com.songstock.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductUpdateDTO {
    
    @DecimalMin(value = "0.01", message = "Precio debe ser mayor a 0")
    private BigDecimal price;
    
    @Min(value = 0, message = "Stock no puede ser negativo")
    private Integer stockQuantity;
    
    private ConditionType conditionType;
    private Boolean isActive;
    private Boolean featured;
    
    // Campos opcionales para actualizar
    private VinylSize vinylSize;
    private VinylSpeed vinylSpeed;
    private Integer weightGrams;
    private String fileFormat;
    private Integer fileSizeMb;
    
    // Constructors
    public ProductUpdateDTO() {}
    
    // Getters and Setters
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public ConditionType getConditionType() { return conditionType; }
    public void setConditionType(ConditionType conditionType) { this.conditionType = conditionType; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getFeatured() { return featured; }
    public void setFeatured(Boolean featured) { this.featured = featured; }
    
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