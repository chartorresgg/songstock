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
 * Servicio especializado en el catálogo público.
 * Permite búsqueda, filtrado, productos relacionados y estadísticas.
 */
@Service
@Transactional
public class CatalogService {

    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService; // Reutilización de lógica de mapeo

    /**
     * Buscar productos en el catálogo aplicando filtros y paginación.
     */
    /**
     * Buscar productos en el catálogo aplicando filtros y paginación.
     */
    @Transactional(readOnly = true)
    public Page<ProductCatalogResponseDTO> searchCatalogProducts(CatalogFilterDTO filterDTO, Pageable pageable) {
        logger.info("Buscando productos en catálogo con filtros: {}", filterDTO);

        // Obtener productos aplicando filtros con los parámetros correctos
        List<Product> products = productRepository.findWithFilters(
                filterDTO.getSearchQuery(),
                filterDTO.getCategoryId(),
                filterDTO.getGenreId(),
                null, // artistId - agregar a CatalogFilterDTO si lo necesitas
                filterDTO.getProductType(), // Ya es ProductType, no convertir a String
                filterDTO.getConditionType(),
                filterDTO.getMinPrice(),
                filterDTO.getMaxPrice(),
                filterDTO.getMinYear(),
                filterDTO.getMaxYear(),
                filterDTO.getInStockOnly() != null ? filterDTO.getInStockOnly() : false,
                filterDTO.getFeaturedOnly() != null ? filterDTO.getFeaturedOnly() : false,
                filterDTO.getActiveOnly() != null ? filterDTO.getActiveOnly() : true);

        // Aplicar ordenamiento manual (ya que el método del repository no lo soporta)
        if ("price".equals(filterDTO.getSortBy())) {
            if ("asc".equals(filterDTO.getSortDirection())) {
                products.sort((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()));
            } else {
                products.sort((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()));
            }
        } else if ("createdAt".equals(filterDTO.getSortBy())) {
            if ("asc".equals(filterDTO.getSortDirection())) {
                products.sort((p1, p2) -> p1.getCreatedAt().compareTo(p2.getCreatedAt()));
            } else {
                products.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
            }
        }

        // Mapear a DTOs
        List<ProductCatalogResponseDTO> productDTOs = products.stream()
                .map(this::mapToCatalogResponseDTO)
                .collect(Collectors.toList());

        // Aplicar paginación manual
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), productDTOs.size());
        List<ProductCatalogResponseDTO> pageContent = productDTOs.subList(start, end);

        return new PageImpl<>(pageContent, pageable, productDTOs.size());
    }

    /**
     * Obtener los productos más recientes (limitados por parámetro).
     */
    @Transactional(readOnly = true)
    public List<ProductCatalogResponseDTO> getLatestProducts(int limit) {
        List<Product> products = productRepository.findWithFilters(
                null, null, null, null, null, null,
                null, null, null, null,
                true, false, true);

        // Ordenar por fecha de creación (más recientes primero)
        products.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));

        return products.stream()
                .limit(limit)
                .map(this::mapToCatalogResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener productos relacionados con uno dado (mismo género/artista).
     */
    @Transactional(readOnly = true)
    public List<ProductCatalogResponseDTO> getRelatedProducts(Long productId, int limit) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return List.of();
        }

        // Buscar productos del mismo género
        List<Product> relatedProducts = productRepository.findWithFilters(
                null, null,
                product.getAlbum().getGenre() != null ? product.getAlbum().getGenre().getId() : null,
                null, null, null, null, null, null, null,
                true, false, true);

        // Ordenar por fecha de creación
        relatedProducts.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));

        return relatedProducts.stream()
                .filter(p -> !p.getId().equals(productId)) // Excluir el actual
                .limit(limit)
                .map(this::mapToCatalogResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener estadísticas generales del catálogo.
     */
    @Transactional(readOnly = true)
    public CatalogStatsDTO getCatalogStats() {
        List<Product> allActiveProducts = productRepository.findByIsActiveTrue();

        CatalogStatsDTO stats = new CatalogStatsDTO();
        stats.setTotalProducts(allActiveProducts.size());
        stats.setProductsInStock((int) allActiveProducts.stream().filter(p -> p.getStockQuantity() > 0).count());
        stats.setFeaturedProducts((int) allActiveProducts.stream().filter(Product::getFeatured).count());

        return stats;
    }

    /**
     * Conversión de entidad Product a DTO para catálogo.
     */
    private ProductCatalogResponseDTO mapToCatalogResponseDTO(Product product) {
        ProductCatalogResponseDTO dto = new ProductCatalogResponseDTO();

        dto.setId(product.getId());
        dto.setSku(product.getSku());

        // Información de álbum y artista
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

        // Campos específicos de vinilos
        if (product.getVinylSize() != null)
            dto.setVinylSize(product.getVinylSize().toString());
        if (product.getVinylSpeed() != null)
            dto.setVinylSpeed(product.getVinylSpeed().toString());
        dto.setWeightGrams(product.getWeightGrams());

        // Campos específicos de productos digitales
        dto.setFileFormat(product.getFileFormat());
        dto.setFileSizeMb(product.getFileSizeMb());

        // Info adicional
        dto.setFeatured(product.getFeatured());
        dto.setIsActive(product.getIsActive());

        // Info de proveedor
        dto.setProviderId(product.getProvider().getId());
        dto.setProviderBusinessName(product.getProvider().getBusinessName());

        // Info de categoría
        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());

        // Info de género
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
     * DTO auxiliar para estadísticas del catálogo.
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
