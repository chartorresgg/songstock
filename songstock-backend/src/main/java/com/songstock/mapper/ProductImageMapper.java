package com.songstock.mapper;

import com.songstock.dto.ProductImageDTO;
import com.songstock.entity.Product;
import com.songstock.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductImageMapper {
    
    public ProductImageDTO toDTO(ProductImage productImage) {
        if (productImage == null) return null;
        
        ProductImageDTO dto = new ProductImageDTO();
        dto.setId(productImage.getId());
        dto.setImageUrl(productImage.getImageUrl());
        dto.setAltText(productImage.getAltText());
        dto.setIsPrimary(productImage.getIsPrimary());
        dto.setDisplayOrder(productImage.getDisplayOrder());
        dto.setCreatedAt(productImage.getCreatedAt());
        
        if (productImage.getProduct() != null) {
            dto.setProductId(productImage.getProduct().getId());
        }
        
        return dto;
    }
    
    public ProductImage toEntity(ProductImageDTO dto) {
        if (dto == null) return null;
        
        ProductImage productImage = new ProductImage();
        productImage.setId(dto.getId());
        productImage.setImageUrl(dto.getImageUrl());
        productImage.setAltText(dto.getAltText());
        productImage.setIsPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : false);
        productImage.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        
        return productImage;
    }
    
    public ProductImage toEntity(ProductImageDTO dto, Product product) {
        ProductImage productImage = toEntity(dto);
        if (productImage != null) {
            productImage.setProduct(product);
        }
        return productImage;
    }
    
    public void updateEntity(ProductImage productImage, ProductImageDTO dto) {
        if (productImage == null || dto == null) return;
        
        productImage.setImageUrl(dto.getImageUrl());
        productImage.setAltText(dto.getAltText());
        if (dto.getIsPrimary() != null) {
            productImage.setIsPrimary(dto.getIsPrimary());
        }
        if (dto.getDisplayOrder() != null) {
            productImage.setDisplayOrder(dto.getDisplayOrder());
        }
    }
    
    public List<ProductImageDTO> toDTOList(List<ProductImage> productImages) {
        if (productImages == null) return null;
        return productImages.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}