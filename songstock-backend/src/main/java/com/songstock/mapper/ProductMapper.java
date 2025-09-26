package com.songstock.mapper;

import com.songstock.dto.ProductDTO;
import com.songstock.dto.ProductImageDTO;
import com.songstock.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper encargado de transformar entre entidades {@link Product}
 * y sus DTOs {@link ProductDTO}.
 * 
 * Incluye la conversión de atributos propios y de relaciones con:
 * - Álbum
 * - Artista
 * - Proveedor
 * - Categoría
 * - Imágenes asociadas
 */
@Component
public class ProductMapper {

    @Autowired
    private ProductImageMapper productImageMapper;

    /**
     * Convierte una entidad {@link Product} a su DTO equivalente.
     */
    public ProductDTO toDTO(Product product) {
        if (product == null)
            return null;

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setSku(product.getSku());
        dto.setProductType(product.getProductType());
        dto.setConditionType(product.getConditionType());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setVinylSize(product.getVinylSize());
        dto.setVinylSpeed(product.getVinylSpeed());
        dto.setWeightGrams(product.getWeightGrams());
        dto.setFileFormat(product.getFileFormat());
        dto.setFileSizeMb(product.getFileSizeMb());
        dto.setIsActive(product.getIsActive());
        dto.setFeatured(product.getFeatured());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        // Relación con Álbum y Artista
        if (product.getAlbum() != null) {
            dto.setAlbumId(product.getAlbum().getId());
            dto.setAlbumTitle(product.getAlbum().getTitle());

            if (product.getAlbum().getArtist() != null) {
                dto.setArtistName(product.getAlbum().getArtist().getName());
            }
        }

        // Relación con Proveedor
        if (product.getProvider() != null) {
            dto.setProviderId(product.getProvider().getId());
            dto.setProviderName(product.getProvider().getBusinessName());
        }

        // Relación con Categoría
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }

        // Imágenes asociadas
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            List<ProductImageDTO> imageDTOs = product.getImages().stream()
                    .map(productImageMapper::toDTO)
                    .collect(Collectors.toList());
            dto.setImages(imageDTOs);
        }

        return dto;
    }

    /**
     * Convierte un DTO a entidad {@link Product}.
     */
    public Product toEntity(ProductDTO dto) {
        if (dto == null)
            return null;

        Product product = new Product();
        product.setId(dto.getId());
        product.setSku(dto.getSku());
        product.setProductType(dto.getProductType());
        product.setConditionType(dto.getConditionType());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setVinylSize(dto.getVinylSize());
        product.setVinylSpeed(dto.getVinylSpeed());
        product.setWeightGrams(dto.getWeightGrams());
        product.setFileFormat(dto.getFileFormat());
        product.setFileSizeMb(dto.getFileSizeMb());
        product.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        product.setFeatured(dto.getFeatured() != null ? dto.getFeatured() : false);

        return product;
    }

    /**
     * Convierte un DTO a entidad {@link Product},
     * asociándolo con Álbum, Proveedor y Categoría.
     */
    public Product toEntity(ProductDTO dto, Album album, Provider provider, Category category) {
        Product product = toEntity(dto);
        if (product != null) {
            product.setAlbum(album);
            product.setProvider(provider);
            product.setCategory(category);
        }
        return product;
    }

    /**
     * Actualiza los campos de una entidad {@link Product}
     * con la información de un DTO.
     */
    public void updateEntity(Product product, ProductDTO dto) {
        if (product == null || dto == null)
            return;

        product.setSku(dto.getSku());
        product.setProductType(dto.getProductType());
        product.setConditionType(dto.getConditionType());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setVinylSize(dto.getVinylSize());
        product.setVinylSpeed(dto.getVinylSpeed());
        product.setWeightGrams(dto.getWeightGrams());
        product.setFileFormat(dto.getFileFormat());
        product.setFileSizeMb(dto.getFileSizeMb());
        if (dto.getIsActive() != null) {
            product.setIsActive(dto.getIsActive());
        }
        if (dto.getFeatured() != null) {
            product.setFeatured(dto.getFeatured());
        }
    }

    /**
     * Convierte una lista de entidades {@link Product}
     * a una lista de DTOs.
     */
    public List<ProductDTO> toDTOList(List<Product> products) {
        if (products == null)
            return null;
        return products.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte un {@link Product} en un DTO simplificado,
     * solo con información básica (sin imágenes ni provider/category).
     */
    public ProductDTO toBasicDTO(Product product) {
        if (product == null)
            return null;

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setSku(product.getSku());
        dto.setProductType(product.getProductType());
        dto.setConditionType(product.getConditionType());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setIsActive(product.getIsActive());
        dto.setFeatured(product.getFeatured());

        if (product.getAlbum() != null) {
            dto.setAlbumId(product.getAlbum().getId());
            dto.setAlbumTitle(product.getAlbum().getTitle());
            if (product.getAlbum().getArtist() != null) {
                dto.setArtistName(product.getAlbum().getArtist().getName());
            }
        }

        return dto;
    }
}
