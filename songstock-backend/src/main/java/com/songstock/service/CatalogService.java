package com.songstock.service;

import com.songstock.dto.CatalogFilterDTO;
import com.songstock.dto.ProductCatalogResponseDTO;
import com.songstock.entity.Product;
import com.songstock.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio especializado para operaciones de catálogo público
 * Maneja la lógica de búsqueda y filtrado de productos para clientes
 */
@Service
@Transactional
public class CatalogService {

    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService; // Para reutilizar métodos de mapeo

    /**
     * Buscar productos en el catálogo público con paginación
     */
    @Transactional(readOnly = true)
    public Page<ProductCatalogResponseDTO> searchCatalogProducts(CatalogFilterDTO filterDTO, Pageable pageable) {
        logger.info("Buscando productos en catálogo con filtros: {}", filterDTO);

        // Usar el método de búsqueda con filtros del repository
        List<Product> products = productRepository.findWithFilters(
                filterDTO.getSearchQuery(),
                filterDTO.getCategoryId(),
                filterDTO.getGenreId(),
                filterDTO.getProductType() != null ? filterDTO.getProductType().toString() : null,
                filterDTO.getMinPrice(),
                filterDTO.getMaxPrice(),
                filterDTO.getMinYear(),
                filterDTO.getMaxYear(),
                filterDTO.getInStockOnly() != null ? filterDTO.getInStockOnly() : false,
                filterDTO.getFeaturedOnly() != null ? filterDTO.getFeaturedOnly() : false,
                filterDTO.getActiveOnly() != null ? filterDTO.getActiveOnly() : true,
                filterDTO.getSortBy() != null ? filterDTO.getSortBy() : "createdAt",
                filterDTO.getSortDirection() != null ? filterDTO.getSortDirection() : "desc");

        // Mapear a DTOs
        List<ProductCatalogResponseDTO> productDTOs = products.stream()
                .map(this::mapToCatalogResponseDTO)
                .collect(Collectors.toList());

        // Aplicar paginación manual (idealmente se haría en la query)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), productDTOs.size());

        List<ProductCatalogResponseDTO> pageContent = productDTOs.subList(start, end);

        return new PageImpl<>(pageContent, pageable, productDTOs.size());
    }

    /**
     * Obtener productos más recientes del catálogo
     */
    @Transactional(readOnly = true)
    public List<ProductCatalogResponseDTO> getLatestProducts(int limit) {
        List<Product> products = productRepository.findWithFilters(
                null, // searchQuery
                null, // categoryId
                null, // genreId
                null, // productType
                null, // minPrice
                null, // maxPrice
                null, // minYear
                null, // maxYear
                true, // inStockOnly
                false, // featuredOnly
                true, // activeOnly
                "createdAt", // sortBy
                "desc" // sortDirection
        );

        return products.stream()
                .limit(limit)
                .map(this::mapToCatalogResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener productos relacionados (mismo artista o género)
     */
    @Transactional(readOnly = true)
    public List<ProductCatalogResponseDTO> getRelatedProducts(Long productId, int limit) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return List.of();
        }

        // Buscar productos del mismo género
        List<Product> relatedProducts = productRepository.findWithFilters(
                null, // searchQuery
                null, // categoryId
                product.getAlbum().getGenre() != null ? product.getAlbum().getGenre().getId() : null, // genreId
                null, // productType
                null, // minPrice
                null, // maxPrice
                null, // minYear
                null, // maxYear
                true, // inStockOnly
                false, // featuredOnly
                true, // activeOnly
                "createdAt", // sortBy
                "desc" // sortDirection
        );

        return relatedProducts.stream()
                .filter(p -> !p.getId().equals(productId)) // Excluir el producto actual
                .limit(limit)
                .map(this::mapToCatalogResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener estadísticas del catálogo público
     */
    @Transactional(readOnly = true)
    public CatalogStatsDTO getCatalogStats() {
        List<Product> allActiveProducts = productRepository.findByIsActiveTrue();

        CatalogStatsDTO stats = new CatalogStatsDTO();
        stats.setTotalProducts(allActiveProducts.size());
        stats.setProductsInStock((int) allActiveProducts.stream()
                .filter(p -> p.getStockQuantity() > 0).count());
        stats.setFeaturedProducts((int) allActiveProducts.stream()
                .filter(Product::getFeatured).count());

        return stats;
    }

    /**
     * Mapear Product a ProductCatalogResponseDTO
     * (Reutiliza la lógica del ProductService o implementa aquí)
     */
    private ProductCatalogResponseDTO mapToCatalogResponseDTO(Product product) {
        ProductCatalogResponseDTO dto = new ProductCatalogResponseDTO();

        dto.setId(product.getId());
        dto.setSku(product.getSku());

        // Información del álbum
        dto.setAlbumId(product.getAlbum().getId());
        dto.setAlbumTitle(product.getAlbum().getTitle());
        dto.setArtistName(product.getAlbum().getArtist().getName());
        dto.setReleaseYear(product.getAlbum().getReleaseYear());

        // Información del producto
        dto.setProductType(product.getProductType().toString());
        dto.setConditionType(product.getConditionType().toString());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setIsAvailable(product.getStockQuantity() > 0);

        // Información específica de vinilos
        if (product.getVinylSize() != null) {
            dto.setVinylSize(product.getVinylSize().toString());
        }
        if (product.getVinylSpeed() != null) {
            dto.setVinylSpeed(product.getVinylSpeed().toString());
        }
        dto.setWeightGrams(product.getWeightGrams());

        // Información específica de digitales
        dto.setFileFormat(product.getFileFormat());
        dto.setFileSizeMb(product.getFileSizeMb());

        // Información adicional
        dto.setFeatured(product.getFeatured());
        dto.setIsActive(product.getIsActive());

        // Información del proveedor
        dto.setProviderId(product.getProvider().getId());
        dto.setProviderBusinessName(product.getProvider().getBusinessName());

        // Información de categoría
        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());

        // Información de género (si existe)
        if (product.getAlbum().getGenre() != null) {
            dto.setGenreId(product.getAlbum().getGenre().getId());
            dto.setGenreName(product.getAlbum().getGenre().getName());
        }

        // Metadatos
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        return dto;
    }

    /**
     * DTO para estadísticas del catálogo
     */
    public static class CatalogStatsDTO {
        private Integer totalProducts;
        private Integer productsInStock;
        private Integer featuredProducts;

        // Getters y setters
        public Integer getTotalProducts() {
            return totalProducts;
        }

        public void setTotalProducts(Integer totalProducts) {
            this.totalProducts = totalProducts;
        }

        public Integer getProductsInStock() {
            return productsInStock;
        }

        public void setProductsInStock(Integer productsInStock) {
            this.productsInStock = productsInStock;
        }

        public Integer getFeaturedProducts() {
            return featuredProducts;
        }

        public void setFeaturedProducts(Integer featuredProducts) {
            this.featuredProducts = featuredProducts;
        }
    }
}