package com.songstock.controller;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import com.songstock.dto.ProductDTO;
import com.songstock.entity.Product;
import com.songstock.entity.ProductType;
import org.springframework.http.HttpStatus;
import com.songstock.service.ProductService;
import com.songstock.repository.ProductRepository;
import com.songstock.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.songstock.exception.ResourceNotFoundException;
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
import org.springframework.security.core.Authentication;
import com.songstock.entity.User;
import com.songstock.entity.Provider;
import com.songstock.entity.UserRole;
import com.songstock.entity.VerificationStatus;
import com.songstock.repository.UserRepository;
import com.songstock.repository.ProviderRepository;
import com.songstock.dto.ProductCatalogCreateDTO;
import com.songstock.dto.ProductCatalogUpdateDTO;
import com.songstock.dto.ProductCatalogResponseDTO;
import com.songstock.dto.QuickMetricsDTO;
import com.songstock.dto.ProductBulkUpdateDTO;
import com.songstock.dto.ProviderCatalogSummaryDTO;
import org.springframework.web.bind.annotation.PostMapping;
import com.songstock.dto.CatalogFilterDTO;
import com.songstock.entity.ConditionType;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.math.BigDecimal;
import java.util.List;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.songstock.dto.AlbumFormatsResponseDTO;
import com.songstock.dto.AlbumFormatComparisonDTO;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Gestión de Productos (Vinilos y Digitales)")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

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
     * Actualizar producto
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PROVIDER') and @productSecurityService.isOwner(authentication.name, #id))")
    @Operation(summary = "Actualizar producto", description = "Actualizar un producto existente")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO,
            Authentication authentication) {
        logger.info("REST request to update product ID: {}", id);

        // Si es PROVIDER, inyectar providerId del usuario autenticado
        if (authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PROVIDER"))) {

            Long providerId = getProviderIdFromAuthentication(authentication);
            productDTO.setProviderId(providerId);
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        }

        // Si es PROVIDER, validar ownership antes de actualizar
        if (authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PROVIDER"))) {

            Long providerId = getProviderIdFromAuthentication(authentication);
            Product existingProduct = productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

            if (!existingProduct.getProvider().getId().equals(providerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("No tienes permisos para actualizar este producto", null));
            }
        }

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
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
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

    /**
     * Obtener providerId considerando rol ADMIN o PROVIDER
     */
    private Long getProviderIdForAdminOrProvider(Authentication authentication, ProductCatalogCreateDTO createDTO) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        logger.info("Usuario autenticado - Username: {}, Role: {}", username, user.getRole());

        // Si es ADMIN, debe especificar el providerId en el DTO
        if (user.getRole().equals(UserRole.ADMIN)) {
            if (createDTO.getProviderId() == null) {
                throw new RuntimeException("El administrador debe especificar el providerId");
            }
            // Validar que el proveedor existe y está verificado
            Provider provider = providerRepository.findById(createDTO.getProviderId())
                    .orElseThrow(
                            () -> new RuntimeException("Proveedor no encontrado con ID: " + createDTO.getProviderId()));

            if (!provider.getVerificationStatus().equals(VerificationStatus.VERIFIED)) {
                throw new RuntimeException("El proveedor debe estar verificado");
            }
            return provider.getId();
        }

        // Si es PROVIDER, usar el método existente
        return getProviderIdFromAuthentication(authentication);
    }

    /**
     * Crear un nuevo producto en el catálogo del proveedor
     * POST /api/v1/products/catalog
     */
    @PostMapping("/catalog")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Crear producto en catálogo", description = "Crear un nuevo producto en el catálogo del proveedor")
    public ResponseEntity<ApiResponse<ProductCatalogResponseDTO>> createCatalogProduct(
            @Valid @RequestBody ProductCatalogCreateDTO createDTO,
            Authentication authentication) {
        try {
            // Obtener el proveedor autenticado
            Long providerId = getProviderIdForAdminOrProvider(authentication, createDTO);

            // Crear el producto
            ProductCatalogResponseDTO response = productService.createCatalogProduct(providerId, createDTO);

            ApiResponse<ProductCatalogResponseDTO> apiResponse = new ApiResponse<>(
                    true,
                    "Producto creado exitosamente en el catálogo",
                    response);

            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);

        } catch (RuntimeException e) {
            ApiResponse<ProductCatalogResponseDTO> errorResponse = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            ApiResponse<ProductCatalogResponseDTO> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Actualizar un producto del catálogo
     * PUT /api/v1/products/{productId}/catalog
     */
    @PutMapping("/{productId}/catalog")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Actualizar producto del catálogo", description = "Actualizar información de un producto en el catálogo")
    public ResponseEntity<ApiResponse<ProductCatalogResponseDTO>> updateCatalogProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductCatalogUpdateDTO updateDTO,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

            Long providerId;
            if (user.getRole().equals(UserRole.ADMIN)) {
                // ADMIN puede actualizar cualquier producto sin restricción de ownership
                providerId = null; // Se manejará en el servicio
            } else {
                // PROVIDER solo puede actualizar sus propios productos
                providerId = getProviderIdFromAuthentication(authentication);
            }

            // Actualizar el producto
            ProductCatalogResponseDTO response = productService.updateCatalogProduct(providerId, productId, updateDTO);

            ApiResponse<ProductCatalogResponseDTO> apiResponse = new ApiResponse<>(
                    true,
                    "Producto actualizado exitosamente",
                    response);

            return ResponseEntity.ok(apiResponse);

        } catch (RuntimeException e) {
            ApiResponse<ProductCatalogResponseDTO> errorResponse = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            ApiResponse<ProductCatalogResponseDTO> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtener el catálogo completo del proveedor autenticado
     * GET /api/v1/products/catalog/summary
     */
    @GetMapping("/catalog/summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Obtener catálogo del proveedor", description = "Obtener el catálogo completo con estadísticas del proveedor")
    public ResponseEntity<ApiResponse<ProviderCatalogSummaryDTO>> getProviderCatalog(
            Authentication authentication) {
        try {
            // Obtener el proveedor autenticado
            Long providerId = getProviderIdFromAuthentication(authentication);

            // Obtener el catálogo
            ProviderCatalogSummaryDTO response = productService.getProviderCatalog(providerId);

            ApiResponse<ProviderCatalogSummaryDTO> apiResponse = new ApiResponse<>(
                    true,
                    "Catálogo obtenido exitosamente",
                    response);

            return ResponseEntity.ok(apiResponse);

        } catch (RuntimeException e) {
            ApiResponse<ProviderCatalogSummaryDTO> errorResponse = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            ApiResponse<ProviderCatalogSummaryDTO> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Buscar productos en el catálogo público con filtros
     * GET /api/v1/products/catalog/search
     */
    @GetMapping("/catalog/search")
    @Operation(summary = "Buscar productos en catálogo", description = "Buscar productos públicos con filtros avanzados")
    public ResponseEntity<ApiResponse<List<ProductCatalogResponseDTO>>> searchCatalogProducts(
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
            @RequestParam(defaultValue = "false") Boolean inStockOnly,
            @RequestParam(defaultValue = "false") Boolean featuredOnly,
            @RequestParam(defaultValue = "true") Boolean activeOnly,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            // Crear el DTO de filtros
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
            filterDTO.setActiveOnly(activeOnly);
            filterDTO.setSortBy(sortBy);
            filterDTO.setSortDirection(sortDirection);

            // Buscar productos
            List<ProductCatalogResponseDTO> response = productService.searchCatalogProducts(filterDTO);

            String message = response.isEmpty()
                    ? "No se encontraron productos con los filtros especificados"
                    : "Búsqueda realizada exitosamente";

            ApiResponse<List<ProductCatalogResponseDTO>> apiResponse = new ApiResponse<>(
                    true,
                    message,
                    response);

            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            ApiResponse<List<ProductCatalogResponseDTO>> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Activar/Desactivar producto del catálogo
     * PATCH /api/v1/products/{productId}/catalog/toggle-status
     */
    @PatchMapping("/{productId}/catalog/toggle-status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Activar/Desactivar producto", description = "Cambiar el estado activo/inactivo de un producto")
    public ResponseEntity<ApiResponse<ProductCatalogResponseDTO>> toggleProductStatus(
            @PathVariable Long productId,
            Authentication authentication) {
        try {
            // Obtener el proveedor autenticado
            Long providerId = getProviderIdFromAuthentication(authentication);

            // Cambiar estado del producto
            ProductCatalogResponseDTO response = productService.toggleProductStatus(providerId, productId);

            String message = response.getIsActive()
                    ? "Producto activado exitosamente"
                    : "Producto desactivado exitosamente";

            ApiResponse<ProductCatalogResponseDTO> apiResponse = new ApiResponse<>(
                    true,
                    message,
                    response);

            return ResponseEntity.ok(apiResponse);

        } catch (RuntimeException e) {
            ApiResponse<ProductCatalogResponseDTO> errorResponse = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            ApiResponse<ProductCatalogResponseDTO> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Eliminar producto del catálogo (soft delete)
     * DELETE /api/v1/products/{productId}/catalog
     */
    @DeleteMapping("/{productId}/catalog")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Eliminar producto del catálogo", description = "Eliminar un producto del catálogo (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteCatalogProduct(
            @PathVariable Long productId,
            Authentication authentication) {
        try {
            // Obtener el proveedor autenticado
            Long providerId = getProviderIdFromAuthentication(authentication);

            // Eliminar el producto
            productService.deleteCatalogProduct(providerId, productId);

            ApiResponse<Void> apiResponse = new ApiResponse<>(
                    true,
                    "Producto eliminado exitosamente del catálogo",
                    null);

            return ResponseEntity.ok(apiResponse);

        } catch (RuntimeException e) {
            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            ApiResponse<Void> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtener productos destacados del catálogo público
     * GET /api/v1/products/catalog/featured
     */
    @GetMapping("/catalog/featured")
    @Operation(summary = "Productos destacados", description = "Obtener productos destacados del catálogo público")
    public ResponseEntity<ApiResponse<List<ProductCatalogResponseDTO>>> getFeaturedCatalogProducts() {
        try {
            // Crear filtro para productos destacados
            CatalogFilterDTO filterDTO = new CatalogFilterDTO();
            filterDTO.setFeaturedOnly(true);
            filterDTO.setActiveOnly(true);
            filterDTO.setInStockOnly(true);
            filterDTO.setSortBy("createdAt");
            filterDTO.setSortDirection("desc");

            // Buscar productos destacados
            List<ProductCatalogResponseDTO> response = productService.searchCatalogProducts(filterDTO);

            ApiResponse<List<ProductCatalogResponseDTO>> apiResponse = new ApiResponse<>(
                    true,
                    "Productos destacados obtenidos exitosamente",
                    response);

            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            ApiResponse<List<ProductCatalogResponseDTO>> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtener productos disponibles por categoría
     * GET /api/v1/products/catalog/category/{categoryId}
     */
    @GetMapping("/catalog/category/{categoryId}")
    @Operation(summary = "Productos por categoría", description = "Obtener productos disponibles de una categoría específica")
    public ResponseEntity<ApiResponse<List<ProductCatalogResponseDTO>>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "true") Boolean inStockOnly) {
        try {
            // Crear filtro por categoría
            CatalogFilterDTO filterDTO = new CatalogFilterDTO();
            filterDTO.setCategoryId(categoryId);
            filterDTO.setActiveOnly(true);
            filterDTO.setInStockOnly(inStockOnly);
            filterDTO.setSortBy("price");
            filterDTO.setSortDirection("asc");

            // Buscar productos
            List<ProductCatalogResponseDTO> response = productService.searchCatalogProducts(filterDTO);

            String message = response.isEmpty()
                    ? "No hay productos disponibles en esta categoría"
                    : "Productos de la categoría obtenidos exitosamente";

            ApiResponse<List<ProductCatalogResponseDTO>> apiResponse = new ApiResponse<>(
                    true,
                    message,
                    response);

            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            ApiResponse<List<ProductCatalogResponseDTO>> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * ENDPOINT PRINCIPAL PARA LA HISTORIA DE USUARIO:
     * Obtener formatos alternativos de un producto específico
     * GET /api/v1/products/{productId}/alternative-formats
     */
    @GetMapping("/{productId}/alternative-formats")
    @Operation(summary = "Obtener formatos alternativos de un producto", description = "Permite al comprador ver si un disco MP3 tiene versión en vinilo y viceversa")
    @ApiResponses(value = {
    })
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAlternativeFormats(
            @Parameter(description = "ID del producto", required = true) @PathVariable Long productId) {

        logger.info("REST request para obtener formatos alternativos del producto ID: {}", productId);

        try {
            List<ProductDTO> alternativeFormats = productService.getAlternativeFormats(productId);

            String message = alternativeFormats.isEmpty()
                    ? "Este producto no tiene formatos alternativos disponibles"
                    : String.format("Se encontraron %d formato(s) alternativo(s)", alternativeFormats.size());

            return ResponseEntity.ok(
                    ApiResponse.success(message, alternativeFormats));

        } catch (Exception e) {
            logger.error("Error al obtener formatos alternativos para producto ID: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener formatos alternativos: " + e.getMessage(), null));
        }
    }

    /**
     * Verificar si un producto tiene formato alternativo
     * GET /api/v1/products/{productId}/has-alternative
     */
    @GetMapping("/{productId}/has-alternative")
    @Operation(summary = "Verificar disponibilidad de formato alternativo", description = "Verifica rápidamente si existe una versión alternativa del producto")
    public ResponseEntity<ApiResponse<Boolean>> hasAlternativeFormat(
            @PathVariable Long productId) {

        logger.info("REST request para verificar formato alternativo del producto ID: {}", productId);

        try {
            boolean hasAlternative = productService.hasAlternativeFormat(productId);

            String message = hasAlternative
                    ? "Este producto tiene formato(s) alternativo(s) disponible(s)"
                    : "Este producto no tiene formatos alternativos";

            return ResponseEntity.ok(
                    ApiResponse.success(message, hasAlternative));

        } catch (Exception e) {
            logger.error("Error al verificar formato alternativo para producto ID: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al verificar formato alternativo: " + e.getMessage(), null));
        }
    }

    /**
     * Obtener todos los formatos disponibles de un álbum
     * GET /api/v1/products/album/{albumId}/all-formats
     */
    @GetMapping("/album/{albumId}/all-formats")
    @Operation(summary = "Obtener todos los formatos de un álbum", description = "Muestra todas las versiones disponibles (digital y vinilo) de un álbum específico")
    public ResponseEntity<ApiResponse<AlbumFormatsResponseDTO>> getAllAlbumFormats(
            @Parameter(description = "ID del álbum", required = true) @PathVariable Long albumId) {

        logger.info("REST request para obtener todos los formatos del álbum ID: {}", albumId);

        try {
            List<ProductDTO> allFormats = productService.getAllFormatsByAlbum(albumId);

            if (allFormats.isEmpty()) {
                AlbumFormatsResponseDTO emptyResponse = new AlbumFormatsResponseDTO();
                emptyResponse.setAlbumId(albumId);
                return ResponseEntity.ok(
                        ApiResponse.success("No se encontraron productos para este álbum", emptyResponse));
            }

            // Crear respuesta estructurada
            ProductDTO firstProduct = allFormats.get(0);
            AlbumFormatsResponseDTO response = new AlbumFormatsResponseDTO();
            response.setAlbumId(albumId);
            response.setAlbumTitle(firstProduct.getAlbumTitle());
            response.setArtistName(firstProduct.getArtistName());

            // Convertir productos a formatos disponibles
            List<AlbumFormatsResponseDTO.FormatAvailabilityDTO> availableFormats = allFormats.stream()
                    .map(product -> {
                        AlbumFormatsResponseDTO.FormatAvailabilityDTO format = new AlbumFormatsResponseDTO.FormatAvailabilityDTO();
                        format.setProductId(product.getId());
                        format.setProductType(product.getProductType());
                        format.setPrice(product.getPrice());
                        format.setStockQuantity(product.getStockQuantity());
                        format.setIsActive(product.getIsActive());

                        // Campos específicos por tipo
                        if (product.isPhysical()) {
                            format.setVinylSize(
                                    product.getVinylSize() != null ? product.getVinylSize().toString() : null);
                            format.setVinylSpeed(
                                    product.getVinylSpeed() != null ? product.getVinylSpeed().toString() : null);
                            format.setConditionType(
                                    product.getConditionType() != null ? product.getConditionType().toString() : null);
                        } else if (product.isDigital()) {
                            format.setFileFormat(product.getFileFormat());
                            // Mapear calidad de audio basado en formato
                            format.setAudioQuality(mapFileFormatToQuality(product.getFileFormat()));
                        }

                        return format;
                    })
                    .toList();

            response.setAvailableFormats(availableFormats);

            String message = String.format("Álbum disponible en %d formato(s): %s%s",
                    availableFormats.size(),
                    response.hasDigitalFormat() ? "Digital" : "",
                    response.hasVinylFormat() ? (response.hasDigitalFormat() ? " y Vinilo" : "Vinilo") : "");

            return ResponseEntity.ok(
                    ApiResponse.success(message, response));

        } catch (Exception e) {
            logger.error("Error al obtener formatos del álbum ID: {}", albumId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener formatos del álbum: " + e.getMessage(), null));
        }
    }

    /**
     * Buscar productos digitales que tienen versión en vinilo
     * GET /api/v1/products/digital-with-vinyl
     */
    @GetMapping("/digital-with-vinyl")
    @Operation(summary = "Productos digitales con versión en vinilo", description = "Lista productos digitales que también están disponibles en formato vinilo")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getDigitalProductsWithVinylVersion() {

        logger.info("REST request para obtener productos digitales con versión en vinilo");

        try {
            List<ProductDTO> digitalWithVinyl = productService.getDigitalProductsWithVinylVersion();

            String message = digitalWithVinyl.isEmpty()
                    ? "No se encontraron productos digitales con versión en vinilo"
                    : String.format("Se encontraron %d producto(s) digital(es) con versión en vinilo",
                            digitalWithVinyl.size());

            return ResponseEntity.ok(
                    ApiResponse.success(message, digitalWithVinyl));

        } catch (Exception e) {
            logger.error("Error al obtener productos digitales con versión en vinilo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al buscar productos: " + e.getMessage(), null));
        }
    }

    /**
     * Buscar productos de vinilo que tienen versión digital
     * GET /api/v1/products/vinyl-with-digital
     */
    @GetMapping("/vinyl-with-digital")
    @Operation(summary = "Productos de vinilo con versión digital", description = "Lista productos de vinilo que también están disponibles en formato digital")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getVinylProductsWithDigitalVersion() {

        logger.info("REST request para obtener productos de vinilo con versión digital");

        try {
            List<ProductDTO> vinylWithDigital = productService.getVinylProductsWithDigitalVersion();

            String message = vinylWithDigital.isEmpty()
                    ? "No se encontraron productos de vinilo con versión digital"
                    : String.format("Se encontraron %d producto(s) de vinilo con versión digital",
                            vinylWithDigital.size());

            return ResponseEntity.ok(
                    ApiResponse.success(message, vinylWithDigital));

        } catch (Exception e) {
            logger.error("Error al obtener productos de vinilo con versión digital", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al buscar productos: " + e.getMessage(), null));
        }
    }

    /**
     * Endpoint para comparar formatos de un álbum específico
     * GET /api/v1/products/album/{albumId}/format-comparison
     */
    @GetMapping("/album/{albumId}/format-comparison")
    @Operation(summary = "Comparar formatos disponibles de un álbum", description = "Compara precios y características entre formatos digital y vinilo del mismo álbum")
    public ResponseEntity<ApiResponse<AlbumFormatComparisonDTO>> compareAlbumFormats(
            @PathVariable Long albumId) {

        logger.info("REST request para comparar formatos del álbum ID: {}", albumId);

        try {
            List<ProductDTO> allFormats = productService.getAllFormatsByAlbum(albumId);

            if (allFormats.isEmpty()) {
                return ResponseEntity.ok(
                        ApiResponse.<AlbumFormatComparisonDTO>success("No se encontraron productos para este álbum",
                                null));
            }
            AlbumFormatComparisonDTO comparison = buildFormatComparison(allFormats);

            return ResponseEntity.ok(
                    ApiResponse.success("Comparación de formatos generada exitosamente", comparison));

        } catch (Exception e) {
            logger.error("Error al comparar formatos del álbum ID: {}", albumId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al comparar formatos: " + e.getMessage(), null));
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Mapear formato de archivo a calidad de audio
     */
    private String mapFileFormatToQuality(String fileFormat) {
        if (fileFormat == null)
            return "Desconocida";

        return switch (fileFormat.toUpperCase()) {
            case "FLAC" -> "Sin pérdida (Lossless)";
            case "MP3" -> "Comprimido (320kbps)";
            case "WAV" -> "Sin compresión";
            case "AAC" -> "Alta calidad comprimida";
            case "OGG" -> "Compresión libre";
            default -> fileFormat.toUpperCase();
        };
    }

    /**
     * Construir comparación de formatos
     */
    private AlbumFormatComparisonDTO buildFormatComparison(List<ProductDTO> allFormats) {
        ProductDTO firstProduct = allFormats.get(0);

        AlbumFormatComparisonDTO comparison = new AlbumFormatComparisonDTO();
        comparison.setAlbumId(firstProduct.getAlbumId());
        comparison.setAlbumTitle(firstProduct.getAlbumTitle());
        comparison.setArtistName(firstProduct.getArtistName());

        // Separar formatos
        List<ProductDTO> digitalFormats = allFormats.stream()
                .filter(ProductDTO::isDigital)
                .toList();

        List<ProductDTO> vinylFormats = allFormats.stream()
                .filter(ProductDTO::isPhysical)
                .toList();

        // Encontrar mejores opciones
        if (!digitalFormats.isEmpty()) {
            ProductDTO bestDigital = digitalFormats.stream()
                    .filter(ProductDTO::isInStock)
                    .min((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()))
                    .orElse(digitalFormats.get(0));
            comparison.setBestDigitalOption(bestDigital);
        }

        if (!vinylFormats.isEmpty()) {
            ProductDTO bestVinyl = vinylFormats.stream()
                    .filter(ProductDTO::isInStock)
                    .min((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()))
                    .orElse(vinylFormats.get(0));
            comparison.setBestVinylOption(bestVinyl);
        }

        comparison.setHasDigitalVersion(!digitalFormats.isEmpty());
        comparison.setHasVinylVersion(!vinylFormats.isEmpty());
        comparison.setHasBothFormats(!digitalFormats.isEmpty() && !vinylFormats.isEmpty());

        // Calcular diferencia de precio si ambos formatos están disponibles
        if (comparison.isHasBothFormats()) {
            BigDecimal priceDifference = comparison.getBestVinylOption().getPrice()
                    .subtract(comparison.getBestDigitalOption().getPrice());
            comparison.setPriceDifference(priceDifference);
        }

        return comparison;
    }

    // Agregar estos endpoints al ProductController existente

    /**
     * Obtener catálogo completo del proveedor con paginación
     * GET /api/v1/products/catalog/my-products
     */
    @GetMapping("/catalog/my-products")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Mi catálogo de productos", description = "Obtener todos los productos del proveedor autenticado con paginación")
    public ResponseEntity<ApiResponse<Page<ProductCatalogResponseDTO>>> getMyProducts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) Boolean activeOnly,
            Authentication authentication) {
        try {
            // Obtener el proveedor autenticado
            Long providerId = getProviderIdFromAuthentication(authentication);

            // Crear Pageable
            Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            // Obtener productos con filtros
            Page<ProductCatalogResponseDTO> products = productService.getProviderProductsWithPagination(
                    providerId, pageable, searchQuery, activeOnly);

            ApiResponse<Page<ProductCatalogResponseDTO>> apiResponse = new ApiResponse<>(
                    true,
                    "Catálogo obtenido exitosamente",
                    products);

            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            logger.error("Error al obtener catálogo del proveedor", e);
            ApiResponse<Page<ProductCatalogResponseDTO>> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtener estadísticas rápidas del catálogo del proveedor
     * GET /api/v1/products/catalog/quick-stats
     */
    @GetMapping("/catalog/quick-stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Estadísticas rápidas del catálogo", description = "Obtener métricas básicas del catálogo del proveedor")
    public ResponseEntity<ApiResponse<QuickMetricsDTO>> getQuickCatalogStats(
            Authentication authentication) {
        try {
            Long providerId = getProviderIdFromAuthentication(authentication);
            QuickMetricsDTO stats = productService.getProviderQuickStats(providerId);

            ApiResponse<QuickMetricsDTO> apiResponse = new ApiResponse<>(
                    true,
                    "Estadísticas obtenidas exitosamente",
                    stats);

            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            logger.error("Error al obtener estadísticas del proveedor", e);
            ApiResponse<QuickMetricsDTO> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Actualización masiva de precios
     * PATCH /api/v1/products/catalog/bulk-update-prices
     */
    @PatchMapping("/catalog/bulk-update-prices")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Actualización masiva de precios", description = "Actualizar precios de múltiples productos")
    public ResponseEntity<ApiResponse<List<ProductCatalogResponseDTO>>> bulkUpdatePrices(
            @Valid @RequestBody ProductBulkUpdateDTO bulkUpdateDTO,
            Authentication authentication) {
        try {
            Long providerId = getProviderIdFromAuthentication(authentication);
            List<ProductCatalogResponseDTO> updatedProducts = productService.bulkUpdatePrices(
                    providerId, bulkUpdateDTO);

            ApiResponse<List<ProductCatalogResponseDTO>> apiResponse = new ApiResponse<>(
                    true,
                    String.format("Se actualizaron %d productos exitosamente", updatedProducts.size()),
                    updatedProducts);

            return ResponseEntity.ok(apiResponse);

        } catch (RuntimeException e) {
            ApiResponse<List<ProductCatalogResponseDTO>> errorResponse = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Error en actualización masiva de precios", e);
            ApiResponse<List<ProductCatalogResponseDTO>> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Duplicar producto (crear copia con SKU diferente)
     * POST /api/v1/products/{productId}/duplicate
     */
    @PostMapping("/{productId}/duplicate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROVIDER')")
    @Operation(summary = "Duplicar producto", description = "Crear una copia de un producto existente")
    public ResponseEntity<ApiResponse<ProductCatalogResponseDTO>> duplicateProduct(
            @PathVariable Long productId,
            @RequestParam String newSku,
            Authentication authentication) {
        try {
            Long providerId = getProviderIdFromAuthentication(authentication);
            ProductCatalogResponseDTO duplicatedProduct = productService.duplicateProduct(
                    providerId, productId, newSku);

            ApiResponse<ProductCatalogResponseDTO> apiResponse = new ApiResponse<>(
                    true,
                    "Producto duplicado exitosamente",
                    duplicatedProduct);

            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);

        } catch (RuntimeException e) {
            ApiResponse<ProductCatalogResponseDTO> errorResponse = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Error al duplicar producto", e);
            ApiResponse<ProductCatalogResponseDTO> errorResponse = new ApiResponse<>(
                    false,
                    "Error interno del servidor",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}