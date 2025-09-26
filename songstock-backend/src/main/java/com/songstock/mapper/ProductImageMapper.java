package com.songstock.mapper;

import com.songstock.dto.ProductImageDTO;
import com.songstock.entity.Product;
import com.songstock.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper encargado de convertir entre entidades {@link ProductImage}
 * y sus respectivos DTOs {@link ProductImageDTO}.
 * 
 * Se utiliza para aislar la lógica de transformación entre capas.
 */
@Component
public class ProductImageMapper {

    /**
     * Convierte una entidad ProductImage a su representación DTO.
     */
    public ProductImageDTO toDTO(ProductImage productImage) {
        if (productImage == null)
            return null;

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

    /**
     * Convierte un DTO a su entidad correspondiente.
     */
    public ProductImage toEntity(ProductImageDTO dto) {
        if (dto == null)
            return null;

        ProductImage productImage = new ProductImage();
        productImage.setId(dto.getId());
        productImage.setImageUrl(dto.getImageUrl());
        productImage.setAltText(dto.getAltText());
        productImage.setIsPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : false);
        productImage.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);

        return productImage;
    }

    /**
     * Convierte un DTO a entidad asociándolo a un {@link Product}.
     */
    public ProductImage toEntity(ProductImageDTO dto, Product product) {
        ProductImage productImage = toEntity(dto);
        if (productImage != null) {
            productImage.setProduct(product);
        }
        return productImage;
    }

    /**
     * Actualiza los campos de una entidad existente con datos de un DTO.
     */
    public void updateEntity(ProductImage productImage, ProductImageDTO dto) {
        if (productImage == null || dto == null)
            return;

        productImage.setImageUrl(dto.getImageUrl());
        productImage.setAltText(dto.getAltText());
        if (dto.getIsPrimary() != null) {
            productImage.setIsPrimary(dto.getIsPrimary());
        }
        if (dto.getDisplayOrder() != null) {
            productImage.setDisplayOrder(dto.getDisplayOrder());
        }
    }

    /**
     * Convierte una lista de entidades en una lista de DTOs.
     */
    public List<ProductImageDTO> toDTOList(List<ProductImage> productImages) {
        if (productImages == null)
            return null;
        return productImages.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
