package com.songstock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ProductImageDTO {
    
    private Long id;
    
    @NotNull(message = "El producto es obligatorio")
    private Long productId;
    
    @NotBlank(message = "La URL de la imagen es obligatoria")
    @Size(max = 500, message = "La URL no puede exceder 500 caracteres")
    private String imageUrl;
    
    @Size(max = 200, message = "El texto alternativo no puede exceder 200 caracteres")
    private String altText;
    
    private Boolean isPrimary = false;
    
    private Integer displayOrder = 0;
    
    private LocalDateTime createdAt;
    
    // Constructores
    public ProductImageDTO() {}
    
    public ProductImageDTO(Long productId, String imageUrl, String altText) {
        this.productId = productId;
        this.imageUrl = imageUrl;
        this.altText = altText;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getAltText() { return altText; }
    public void setAltText(String altText) { this.altText = altText; }
    
    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}