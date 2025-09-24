package com.songstock.controller;

import com.songstock.dto.CatalogFilterDTO;
import com.songstock.dto.ProductCatalogResponseDTO;
import com.songstock.entity.ProductType;
import com.songstock.entity.ConditionType;
import com.songstock.service.CatalogService;
import com.songstock.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/catalog")
@Tag(name = "Catalog", description = "Catálogo público de productos")
public class ProductCatalogController {

    private static final Logger logger = LoggerFactory.getLogger(ProductCatalogController.class);

    @Autowired
    private CatalogService catalogService;

    /**
     * Buscar productos en el catálogo público con paginación
     * GET /api/v1/catalog/search
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar productos", description = "Buscar productos en el catálogo público con filtros y paginación")
    public ResponseEntity<ApiResponse<Page<ProductCatalogResponseDTO>>> searchProducts(
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Long artistId,
            @RequestParam(required = false) ProductType productType,
            @RequestParam(required = false) ConditionType conditionType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minYear,
            @RequestParam(required = false) Integer maxYear,
            @RequestParam(defaultValue = "true") Boolean inStockOnly,
            @RequestParam(defaultValue = "false") Boolean featuredOnly,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            Pageable pageable) {

        logger.info("Búsqueda en catálogo - Query: '{}', Categoría: {}, Página: {}",
                searchQuery, categoryId, pageable.getPageNumber());

        try {
            // Crear filtros
            CatalogFilterDTO filterDTO = new CatalogFilterDTO();
            filterDTO.setSearchQuery(searchQuery);
            filterDTO.setCategoryId(categoryId);
            filterDTO.setGenreId(genreId);
            filterDTO.setArtistId(artistId);
            filterDTO.setProductType(productType);
            filterDTO.setConditionType(conditionType);
            filterDTO.setMinPrice(minPrice);
            filterDTO.setMaxPrice(maxPrice);
            filterDTO.setMinYear(minYear);
            filterDTO.setMaxYear(maxYear);
            filterDTO.setInStockOnly(inStockOnly);
            filterDTO.setFeaturedOnly(featuredOnly);
            filterDTO.setActiveOnly(true); // Solo productos activos en catálogo público
            filterDTO.setSortBy(sortBy);
            filterDTO.setSortDirection(sortDirection);

            Page<ProductCatalogResponseDTO> products = catalogService.searchCatalogProducts(filterDTO, pageable);

            String message = products.isEmpty()
                    ? "No se encontraron productos con los criterios especificados"
                    : String.format("Se encontraron %d productos", products.getTotalElements());

            ApiResponse<Page<ProductCatalogResponseDTO>> response = new ApiResponse<>(
                    true,
                    message,
                    products);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error en búsqueda de catálogo: ", e);
            ApiResponse<Page<ProductCatalogResponseDTO>> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Obtener productos destacados
     * GET /api/v1/catalog/featured
     */
    @GetMapping("/featured")
    @Operation(summary = "Productos destacados", description = "Obtener productos destacados del catálogo")
    public ResponseEntity<ApiResponse<List<ProductCatalogResponseDTO>>> getFeaturedProducts(
            @RequestParam(defaultValue = "12") int limit) {

        logger.info("Obteniendo productos destacados - Límite: {}", limit);

        try {
            CatalogFilterDTO filterDTO = new CatalogFilterDTO();
            filterDTO.setFeaturedOnly(true);
            filterDTO.setInStockOnly(true);
            filterDTO.setActiveOnly(true);
            filterDTO.setSortBy("createdAt");
            filterDTO.setSortDirection("desc");

            List<ProductCatalogResponseDTO> products = catalogService
                    .searchCatalogProducts(filterDTO, Pageable.unpaged())
                    .getContent()
                    .stream()
                    .limit(limit)
                    .toList();

            ApiResponse<List<ProductCatalogResponseDTO>> response = new ApiResponse<>(
                    true,
                    "Productos destacados obtenidos exitosamente",
                    products);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error obteniendo productos destacados: ", e);
            ApiResponse<List<ProductCatalogResponseDTO>> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Obtener productos más recientes
     * GET /api/v1/catalog/latest
     */
    @GetMapping("/latest")
    @Operation(summary = "Productos recientes", description = "Obtener los productos más recientes del catálogo")
    public ResponseEntity<ApiResponse<List<ProductCatalogResponseDTO>>> getLatestProducts(
            @RequestParam(defaultValue = "20") int limit) {

        logger.info("Obteniendo productos recientes - Límite: {}", limit);

        try {
            List<ProductCatalogResponseDTO> products = catalogService.getLatestProducts(limit);

            ApiResponse<List<ProductCatalogResponseDTO>> response = new ApiResponse<>(
                    true,
                    "Productos recientes obtenidos exitosamente",
                    products);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error obteniendo productos recientes: ", e);
            ApiResponse<List<ProductCatalogResponseDTO>> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Obtener productos por categoría
     * GET /api/v1/catalog/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Productos por categoría", description = "Obtener productos de una categoría específica")
    public ResponseEntity<ApiResponse<Page<ProductCatalogResponseDTO>>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "true") Boolean inStockOnly,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            Pageable pageable) {

        logger.info("Obteniendo productos por categoría - ID: {}, Página: {}", categoryId, pageable.getPageNumber());

        try {
            CatalogFilterDTO filterDTO = new CatalogFilterDTO();
            filterDTO.setCategoryId(categoryId);
            filterDTO.setInStockOnly(inStockOnly);
            filterDTO.setActiveOnly(true);
            filterDTO.setSortBy(sortBy);
            filterDTO.setSortDirection(sortDirection);

            Page<ProductCatalogResponseDTO> products = catalogService.searchCatalogProducts(filterDTO, pageable);

            String message = products.isEmpty()
                    ? "No hay productos disponibles en esta categoría"
                    : String.format("Se encontraron %d productos en la categoría", products.getTotalElements());

            ApiResponse<Page<ProductCatalogResponseDTO>> response = new ApiResponse<>(
                    true,
                    message,
                    products);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error obteniendo productos por categoría: ", e);
            ApiResponse<Page<ProductCatalogResponseDTO>> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Obtener detalles de un producto específico
     * GET /api/v1/catalog/product/{productId}
     */
    @GetMapping("/product/{productId}")
    @Operation(summary = "Detalles del producto", description = "Obtener detalles completos de un producto específico")
    public ResponseEntity<ApiResponse<ProductCatalogResponseDTO>> getProductDetails(
            @PathVariable Long productId) {

        logger.info("Obteniendo detalles del producto - ID: {}", productId);

        try {
            CatalogFilterDTO filterDTO = new CatalogFilterDTO();
            filterDTO.setActiveOnly(true);

            List<ProductCatalogResponseDTO> products = catalogService
                    .searchCatalogProducts(filterDTO, Pageable.unpaged())
                    .getContent()
                    .stream()
                    .filter(p -> p.getId().equals(productId))
                    .toList();

            if (products.isEmpty()) {
                ApiResponse<ProductCatalogResponseDTO> notFoundResponse = new ApiResponse<>(
                        false,
                        "Producto no encontrado o no disponible",
                        null);
                return ResponseEntity.notFound().build();
            }

            ApiResponse<ProductCatalogResponseDTO> response = new ApiResponse<>(
                    true,
                    "Detalles del producto obtenidos exitosamente",
                    products.get(0));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error obteniendo detalles del producto: ", e);
            ApiResponse<ProductCatalogResponseDTO> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Obtener productos relacionados
     * GET /api/v1/catalog/product/{productId}/related
     */
    @GetMapping("/product/{productId}/related")
    @Operation(summary = "Productos relacionados", description = "Obtener productos relacionados (mismo artista o género)")
    public ResponseEntity<ApiResponse<List<ProductCatalogResponseDTO>>> getRelatedProducts(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "8") int limit) {

        logger.info("Obteniendo productos relacionados - Product ID: {}, Límite: {}", productId, limit);

        try {
            List<ProductCatalogResponseDTO> relatedProducts = catalogService.getRelatedProducts(productId, limit);

            ApiResponse<List<ProductCatalogResponseDTO>> response = new ApiResponse<>(
                    true,
                    "Productos relacionados obtenidos exitosamente",
                    relatedProducts);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error obteniendo productos relacionados: ", e);
            ApiResponse<List<ProductCatalogResponseDTO>> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Obtener estadísticas del catálogo
     * GET /api/v1/catalog/stats
     */
    @GetMapping("/stats")
    @Operation(summary = "Estadísticas del catálogo", description = "Obtener estadísticas generales del catálogo público")
    public ResponseEntity<ApiResponse<CatalogService.CatalogStatsDTO>> getCatalogStats() {

        logger.info("Obteniendo estadísticas del catálogo");

        try {
            CatalogService.CatalogStatsDTO stats = catalogService.getCatalogStats();

            ApiResponse<CatalogService.CatalogStatsDTO> response = new ApiResponse<>(
                    true,
                    "Estadísticas del catálogo obtenidas exitosamente",
                    stats);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error obteniendo estadísticas del catálogo: ", e);
            ApiResponse<CatalogService.CatalogStatsDTO> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}