package com.songstock.controller;

import com.songstock.dto.ProductDTO;
import com.songstock.entity.ProductType;
import com.songstock.service.ProductService;
import com.songstock.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.songstock.dto.ProductInventoryUpdateDTO;
import com.songstock.dto.ProductStockAdjustmentDTO;
import com.songstock.dto.ProductInventoryResponseDTO;
import com.songstock.dto.ProviderInventorySummaryDTO;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import com.songstock.entity.User;
import com.songstock.entity.Provider;
import com.songstock.entity.UserRole;
import com.songstock.entity.VerificationStatus;
import com.songstock.repository.UserRepository;
import com.songstock.repository.ProviderRepository;
import java.util.List;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Gestión de Productos (Vinilos y Digitales)")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProviderRepository providerRepository;

    /**
     * Crear un nuevo producto
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Crear producto", description = "Crear un nuevo producto (vinilo o digital)")
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        logger.info("REST request to create product: {}", productDTO.getSku());

        ProductDTO createdProduct = productService.createProduct(productDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Producto creado exitosamente", createdProduct));
    }

    /**
     * Obtener todos los productos activos
     */
    @GetMapping
    @Operation(summary = "Listar productos", description = "Obtener todos los productos activos")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts() {
        logger.info("REST request to get all active products");

        List<ProductDTO> products = productService.getAllActiveProducts();

        return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", products));
    }

    /**
     * Obtener productos con paginación
     */
    @GetMapping("/paginated")
    @Operation(summary = "Listar productos paginados", description = "Obtener productos con paginación")
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> getProducts(Pageable pageable) {
        logger.info("REST request to get products with pagination");

        Page<ProductDTO> productsPage = productService.getProducts(pageable);

        return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", productsPage));
    }

    /**
     * Obtener producto por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto", description = "Obtener un producto por su ID")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable Long id) {
        logger.info("REST request to get product by ID: {}", id);

        ProductDTO product = productService.getProductById(id);

        return ResponseEntity.ok(ApiResponse.success("Producto obtenido exitosamente", product));
    }

    /**
     * Obtener producto por SKU
     */
    @GetMapping("/sku/{sku}")
    @Operation(summary = "Obtener producto por SKU", description = "Obtener un producto por su SKU")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductBySku(@PathVariable String sku) {
        logger.info("REST request to get product by SKU: {}", sku);

        ProductDTO product = productService.getProductBySku(sku);

        return ResponseEntity.ok(ApiResponse.success("Producto obtenido exitosamente", product));
    }

    /**
     * ENDPOINT PRINCIPAL PARA LA HISTORIA DE USUARIO:
     * Obtener formatos alternativos de un producto
     */
    @GetMapping("/{id}/alternative-formats")
    @Operation(summary = "Obtener formatos alternativos", description = "Obtener formatos alternativos de un producto (si es digital muestra vinilo, y viceversa) - Historia de Usuario")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAlternativeFormats(@PathVariable Long id) {
        logger.info("REST request to get alternative formats for product ID: {}", id);

        List<ProductDTO> alternatives = productService.getAlternativeFormats(id);

        return ResponseEntity.ok(ApiResponse.success("Formatos alternativos obtenidos exitosamente", alternatives));
    }

    /**
     * Verificar si un producto tiene formato alternativo
     */
    @GetMapping("/{id}/has-alternative")
    @Operation(summary = "Verificar formato alternativo", description = "Verificar si un producto tiene formato alternativo disponible")
    public ResponseEntity<ApiResponse<Boolean>> hasAlternativeFormat(@PathVariable Long id) {
        logger.info("REST request to check if product {} has alternative format", id);

        boolean hasAlternative = productService.hasAlternativeFormat(id);

        return ResponseEntity.ok(ApiResponse.success("Verificación realizada", hasAlternative));
    }

    /**
     * Obtener todos los formatos de un álbum
     */
    @GetMapping("/album/{albumId}/all-formats")
    @Operation(summary = "Todos los formatos de álbum", description = "Obtener todos los formatos disponibles para un álbum específico")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllFormatsByAlbum(@PathVariable Long albumId) {
        logger.info("REST request to get all formats for album ID: {}", albumId);

        List<ProductDTO> products = productService.getAllFormatsByAlbum(albumId);

        return ResponseEntity.ok(ApiResponse.success("Formatos obtenidos exitosamente", products));
    }

    /**
     * Obtener productos por tipo
     */
    @GetMapping("/type/{type}")
    @Operation(summary = "Productos por tipo", description = "Obtener productos por tipo (DIGITAL o PHYSICAL)")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByType(@PathVariable ProductType type) {
        logger.info("REST request to get products by type: {}", type);

        List<ProductDTO> products = productService.getProductsByType(type);

        return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", products));
    }

    /**
     * Obtener productos por álbum
     */
    @GetMapping("/album/{albumId}")
    @Operation(summary = "Productos por álbum", description = "Obtener productos por álbum")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByAlbum(@PathVariable Long albumId) {
        logger.info("REST request to get products by album ID: {}", albumId);

        List<ProductDTO> products = productService.getProductsByAlbum(albumId);

        return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", products));
    }

    /**
     * Obtener productos por proveedor
     */
    @GetMapping("/provider/{providerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Productos por proveedor", description = "Obtener productos por proveedor")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByProvider(@PathVariable Long providerId) {
        logger.info("REST request to get products by provider ID: {}", providerId);

        List<ProductDTO> products = productService.getProductsByProvider(providerId);

        return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", products));
    }

    /**
     * Obtener productos destacados
     */
    @GetMapping("/featured")
    @Operation(summary = "Productos destacados", description = "Obtener productos destacados")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getFeaturedProducts() {
        logger.info("REST request to get featured products");

        List<ProductDTO> products = productService.getFeaturedProducts();

        return ResponseEntity.ok(ApiResponse.success("Productos destacados obtenidos exitosamente", products));
    }

    /**
     * Obtener productos en stock
     */
    @GetMapping("/in-stock")
    @Operation(summary = "Productos en stock", description = "Obtener productos que tienen stock disponible")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getInStockProducts() {
        logger.info("REST request to get in-stock products");

        List<ProductDTO> products = productService.getInStockProducts();

        return ResponseEntity.ok(ApiResponse.success("Productos en stock obtenidos exitosamente", products));
    }

    /**
     * Obtener productos con bajo stock
     */
    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Productos con bajo stock", description = "Obtener productos con stock bajo (umbral configurable)")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getLowStockProducts(
            @RequestParam(defaultValue = "5") Integer threshold) {
        logger.info("REST request to get low-stock products with threshold: {}", threshold);

        List<ProductDTO> products = productService.getLowStockProducts(threshold);

        return ResponseEntity.ok(ApiResponse.success("Productos con bajo stock obtenidos exitosamente", products));
    }

    /**
     * Buscar productos por rango de precios
     */
    @GetMapping("/price-range")
    @Operation(summary = "Productos por rango de precio", description = "Buscar productos en un rango de precios")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        logger.info("REST request to get products by price range: {} - {}", minPrice, maxPrice);

        List<ProductDTO> products = productService.getProductsByPriceRange(minPrice, maxPrice);

        return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", products));
    }

    /**
     * Productos digitales con versión en vinilo
     */
    @GetMapping("/digital-with-vinyl")
    @Operation(summary = "Digitales con vinilo", description = "Obtener productos digitales que tienen versión en vinilo")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getDigitalProductsWithVinylVersion() {
        logger.info("REST request to get digital products with vinyl version");

        List<ProductDTO> products = productService.getDigitalProductsWithVinylVersion();

        return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", products));
    }

    /**
     * Productos de vinilo con versión digital
     */
    @GetMapping("/vinyl-with-digital")
    @Operation(summary = "Vinilos con digital", description = "Obtener productos de vinilo que tienen versión digital")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getVinylProductsWithDigitalVersion() {
        logger.info("REST request to get vinyl products with digital version");

        List<ProductDTO> products = productService.getVinylProductsWithDigitalVersion();

        return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", products));
    }

    /**
     * Actualizar producto
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PROVIDER') and @productSecurityService.isOwner(authentication.name, #id))")
    @Operation(summary = "Actualizar producto", description = "Actualizar un producto existente")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO) {
        logger.info("REST request to update product ID: {}", id);

        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);

        return ResponseEntity.ok(ApiResponse.success("Producto actualizado exitosamente", updatedProduct));
    }

    /**
     * Actualizar stock
     */
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PROVIDER') and @productSecurityService.isOwner(authentication.name, #id))")
    @Operation(summary = "Actualizar stock", description = "Actualizar el stock de un producto")
    public ResponseEntity<ApiResponse<ProductDTO>> updateStock(@PathVariable Long id, @RequestParam Integer stock) {
        logger.info("REST request to update stock for product ID: {} to {}", id, stock);

        ProductDTO updatedProduct = productService.updateStock(id, stock);

        return ResponseEntity.ok(ApiResponse.success("Stock actualizado exitosamente", updatedProduct));
    }

    /**
     * Marcar/desmarcar como destacado
     */
    @PatchMapping("/{id}/featured")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PROVIDER') and @productSecurityService.isOwner(authentication.name, #id))")
    @Operation(summary = "Toggle destacado", description = "Marcar o desmarcar producto como destacado")
    public ResponseEntity<ApiResponse<ProductDTO>> toggleFeatured(@PathVariable Long id) {
        logger.info("REST request to toggle featured for product ID: {}", id);

        ProductDTO updatedProduct = productService.toggleFeatured(id);

        return ResponseEntity.ok(ApiResponse.success("Estado destacado actualizado exitosamente", updatedProduct));
    }

    /**
     * Eliminar producto
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PROVIDER') and @productSecurityService.isOwner(authentication.name, #id))")
    @Operation(summary = "Eliminar producto", description = "Eliminar un producto (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        logger.info("REST request to delete product ID: {}", id);

        productService.deleteProduct(id);

        return ResponseEntity.ok(ApiResponse.success("Producto eliminado exitosamente", null));
    }

    /**
     * Obtener estadísticas de productos
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Estadísticas de productos", description = "Obtener estadísticas generales de productos")
    public ResponseEntity<ApiResponse<ProductService.ProductStatisticsDTO>> getProductStatistics() {
        logger.info("REST request to get product statistics");

        ProductService.ProductStatisticsDTO stats = productService.getProductStatistics();

        return ResponseEntity.ok(ApiResponse.success("Estadísticas obtenidas exitosamente", stats));
    }

    /**
     * Actualizar stock de un producto específico
     * PUT /api/v1/products/{productId}/stock
     */
    @PutMapping("/{productId}/stock")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ProductInventoryResponseDTO>> updateProductStock(
            @PathVariable Long productId,
            @Valid @RequestBody ProductInventoryUpdateDTO updateDTO,
            Authentication authentication) {
        try {
            // Obtener el proveedor autenticado
            Long providerId = getProviderIdFromAuthentication(authentication);

            // Actualizar el stock
            ProductInventoryResponseDTO response = productService.updateProductStock(productId, providerId, updateDTO);

            ApiResponse<ProductInventoryResponseDTO> apiResponse = new ApiResponse<>(
                    true,
                    "Stock actualizado correctamente",
                    response);

            return ResponseEntity.ok(apiResponse);

        } catch (RuntimeException e) {
            ApiResponse<ProductInventoryResponseDTO> errorResponse = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            ApiResponse<ProductInventoryResponseDTO> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Ajustar stock de un producto (incrementar o decrementar)
     * PATCH /api/v1/products/{productId}/stock/adjust
     */
    @PatchMapping("/{productId}/stock/adjust")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ProductInventoryResponseDTO>> adjustProductStock(
            @PathVariable Long productId,
            @Valid @RequestBody ProductStockAdjustmentDTO adjustmentDTO,
            Authentication authentication) {
        try {
            // Obtener el proveedor autenticado
            Long providerId = getProviderIdFromAuthentication(authentication);

            // Ajustar el stock
            ProductInventoryResponseDTO response = productService.adjustProductStock(productId, providerId,
                    adjustmentDTO);

            String message = adjustmentDTO.getAdjustmentType() == ProductStockAdjustmentDTO.AdjustmentType.INCREMENT
                    ? "Stock incrementado correctamente"
                    : "Stock decrementado correctamente";

            ApiResponse<ProductInventoryResponseDTO> apiResponse = new ApiResponse<>(
                    true,
                    message,
                    response);

            return ResponseEntity.ok(apiResponse);

        } catch (RuntimeException e) {
            ApiResponse<ProductInventoryResponseDTO> errorResponse = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            ApiResponse<ProductInventoryResponseDTO> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtener información de inventario de un producto específico
     * GET /api/v1/products/{productId}/inventory
     */
    @GetMapping("/{productId}/inventory")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ProductInventoryResponseDTO>> getProductInventory(
            @PathVariable Long productId,
            Authentication authentication) {
        try {
            // Obtener el proveedor autenticado
            Long providerId = getProviderIdFromAuthentication(authentication);

            // Obtener la información del inventario
            ProductInventoryResponseDTO response = productService.getProductInventory(productId, providerId);

            ApiResponse<ProductInventoryResponseDTO> apiResponse = new ApiResponse<>(
                    true,
                    "Información de inventario obtenida correctamente",
                    response);

            return ResponseEntity.ok(apiResponse);

        } catch (RuntimeException e) {
            ApiResponse<ProductInventoryResponseDTO> errorResponse = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            ApiResponse<ProductInventoryResponseDTO> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtener resumen completo del inventario del proveedor autenticado
     * GET /api/v1/products/inventory/summary
     */
    @GetMapping("/inventory/summary")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ProviderInventorySummaryDTO>> getProviderInventorySummary(
            Authentication authentication) {
        try {
            // Obtener el proveedor autenticado
            Long providerId = getProviderIdFromAuthentication(authentication);

            // Obtener el resumen del inventario
            ProviderInventorySummaryDTO response = productService.getProviderInventorySummary(providerId);

            ApiResponse<ProviderInventorySummaryDTO> apiResponse = new ApiResponse<>(
                    true,
                    "Resumen de inventario obtenido correctamente",
                    response);

            return ResponseEntity.ok(apiResponse);

        } catch (RuntimeException e) {
            ApiResponse<ProviderInventorySummaryDTO> errorResponse = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            ApiResponse<ProviderInventorySummaryDTO> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtener productos con stock bajo del proveedor
     * GET /api/v1/products/inventory/low-stock?minStock=5
     */
    @GetMapping("/inventory/low-stock")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<ProductInventoryResponseDTO>>> getProductsWithLowStock(
            @RequestParam(defaultValue = "5") Integer minStock,
            Authentication authentication) {
        try {
            // Obtener el proveedor autenticado
            Long providerId = getProviderIdFromAuthentication(authentication);

            // Obtener productos con stock bajo
            List<ProductInventoryResponseDTO> response = productService.getProductsWithLowStock(providerId, minStock);

            String message = response.isEmpty()
                    ? "No hay productos con stock bajo"
                    : "Productos con stock bajo obtenidos correctamente";

            ApiResponse<List<ProductInventoryResponseDTO>> apiResponse = new ApiResponse<>(
                    true,
                    message,
                    response);

            return ResponseEntity.ok(apiResponse);

        } catch (RuntimeException e) {
            ApiResponse<List<ProductInventoryResponseDTO>> errorResponse = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            ApiResponse<List<ProductInventoryResponseDTO>> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtener productos sin stock del proveedor
     * GET /api/v1/products/inventory/out-of-stock
     */
    @GetMapping("/inventory/out-of-stock")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<ProductInventoryResponseDTO>>> getProductsOutOfStock(
            Authentication authentication) {
        try {
            // Obtener el proveedor autenticado
            Long providerId = getProviderIdFromAuthentication(authentication);

            // Obtener productos sin stock
            List<ProductInventoryResponseDTO> response = productService.getProductsOutOfStock(providerId);

            String message = response.isEmpty()
                    ? "No hay productos sin stock"
                    : "Productos sin stock obtenidos correctamente";

            ApiResponse<List<ProductInventoryResponseDTO>> apiResponse = new ApiResponse<>(
                    true,
                    message,
                    response);

            return ResponseEntity.ok(apiResponse);

        } catch (RuntimeException e) {
            ApiResponse<List<ProductInventoryResponseDTO>> errorResponse = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            ApiResponse<List<ProductInventoryResponseDTO>> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Método auxiliar para obtener el ID del proveedor desde la autenticación
     */
    private Long getProviderIdFromAuthentication(Authentication authentication) {
        logger.info("=== DEBUG AUTHENTICATION ===");

        // Obtener el usuario autenticado
        String username = authentication.getName();
        logger.info("Username desde authentication: {}", username);

        // Buscar el usuario y su proveedor asociado
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        logger.info("Usuario encontrado - ID: {}, Username: {}, Role: {}", user.getId(), user.getUsername(),
                user.getRole());

        // Validar que es un proveedor
        if (!user.getRole().equals(UserRole.PROVIDER)) {
            logger.error("Usuario no es PROVIDER, rol actual: {}", user.getRole());
            throw new RuntimeException("El usuario no es un proveedor");
        }

        // Obtener el proveedor asociado
        Provider provider = providerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Información de proveedor no encontrada"));

        logger.info("Proveedor encontrado - ID: {}, Business Name: {}, Status: {}",
                provider.getId(), provider.getBusinessName(), provider.getVerificationStatus());

        // Validar que el proveedor está verificado
        if (!provider.getVerificationStatus().equals(VerificationStatus.VERIFIED)) {
            logger.error("Proveedor no está verificado, status: {}", provider.getVerificationStatus());
            throw new RuntimeException("El proveedor debe estar verificado para gestionar inventario");
        }

        logger.info("=== PROVIDER ID RETORNADO: {} ===", provider.getId());
        return provider.getId();
    }

}