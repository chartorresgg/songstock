package com.songstock.service;

import com.songstock.dto.ProductDTO;
import java.math.RoundingMode;
import java.util.stream.Collectors;
import java.util.ArrayList;
import com.songstock.entity.*;
import com.songstock.exception.ResourceNotFoundException;
import com.songstock.exception.DuplicateResourceException;
import com.songstock.exception.BusinessException;
import com.songstock.mapper.ProductMapper;
import com.songstock.repository.*;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.songstock.dto.ProductInventoryUpdateDTO;
import com.songstock.dto.ProductStockAdjustmentDTO;
import com.songstock.dto.ProductInventoryResponseDTO;
import com.songstock.dto.ProviderInventorySummaryDTO;
import com.songstock.dto.ProductCatalogCreateDTO;
import com.songstock.dto.ProductCatalogUpdateDTO;
import com.songstock.dto.ProductCatalogResponseDTO;
import com.songstock.dto.ProviderCatalogSummaryDTO;
import com.songstock.dto.ProductBulkUpdateDTO;
import com.songstock.dto.CatalogFilterDTO;
import com.songstock.dto.QuickMetricsDTO;
import com.songstock.entity.Album;
import com.songstock.entity.Category;
import com.songstock.entity.Provider;
import com.songstock.entity.VerificationStatus;
import com.songstock.entity.ProductType;
import com.songstock.entity.ConditionType;
import com.songstock.repository.AlbumRepository;
import com.songstock.repository.CategoryRepository;
import java.time.LocalDateTime;
import com.songstock.dto.AlbumFormatsDTO;
import com.songstock.dto.AlbumFormatsResponseDTO;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductMapper productMapper;

    /**
     * Crear un nuevo producto
     */
    public ProductDTO createProduct(ProductDTO productDTO) {
        logger.info("Creando nuevo producto: {}", productDTO.getSku());

        // Verificar si ya existe un producto con el mismo SKU
        Optional<Product> existingProduct = productRepository.findBySkuAndIsActiveTrue(productDTO.getSku());
        if (existingProduct.isPresent()) {
            throw new DuplicateResourceException("Ya existe un producto con el SKU: " + productDTO.getSku());
        }

        // Obtener entidades relacionadas
        Album album = albumRepository.findById(productDTO.getAlbumId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Álbum no encontrado con ID: " + productDTO.getAlbumId()));

        Provider provider = providerRepository.findById(productDTO.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proveedor no encontrado con ID: " + productDTO.getProviderId()));

        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría no encontrada con ID: " + productDTO.getCategoryId()));

        // Validar campos específicos según el tipo de producto
        validateProductTypeSpecificFields(productDTO);

        Product product = productMapper.toEntity(productDTO, album, provider, category);
        Product savedProduct = productRepository.save(product);

        logger.info("Producto creado exitosamente con ID: {}", savedProduct.getId());
        return productMapper.toDTO(savedProduct);
    }

    /**
     * Obtener todos los productos activos
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllActiveProducts() {
        logger.info("Obteniendo todos los productos activos");
        List<Product> products = productRepository.findByIsActiveTrue();
        return productMapper.toDTOList(products);
    }

    /**
     * Obtener productos con paginación
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProducts(Pageable pageable) {
        logger.info("Obteniendo productos con paginación");
        Page<Product> productPage = productRepository.findByIsActiveTrue(pageable);
        return productPage.map(productMapper::toDTO);
    }

    /**
     * Obtener producto por ID
     */
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        logger.info("Obteniendo producto por ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return productMapper.toDTO(product);
    }

    /**
     * Obtener producto por SKU
     */
    @Transactional(readOnly = true)
    public ProductDTO getProductBySku(String sku) {
        logger.info("Obteniendo producto por SKU: {}", sku);
        Product product = productRepository.findBySkuAndIsActiveTrue(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con SKU: " + sku));
        return productMapper.toDTO(product);
    }

    /**
     * Obtener productos por tipo
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByType(ProductType productType) {
        logger.info("Obteniendo productos por tipo: {}", productType);
        List<Product> products = productRepository.findByProductTypeAndIsActiveTrue(productType);
        return productMapper.toDTOList(products);
    }

    /**
     * Obtener productos por álbum
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByAlbum(Long albumId) {
        logger.info("Obteniendo productos por álbum ID: {}", albumId);
        List<Product> products = productRepository.findByAlbumIdAndIsActiveTrue(albumId);
        return productMapper.toDTOList(products);
    }

    /**
     * Obtener productos por proveedor
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByProvider(Long providerId) {
        logger.info("Obteniendo productos por proveedor ID: {}", providerId);
        List<Product> products = productRepository.findByProviderIdAndIsActiveTrue(providerId);
        return productMapper.toDTOList(products);
    }

    /**
     * Obtener productos por categoría
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByCategory(Long categoryId) {
        logger.info("Obteniendo productos por categoría ID: {}", categoryId);
        List<Product> products = productRepository.findByCategoryIdAndIsActiveTrue(categoryId);
        return productMapper.toDTOList(products);
    }

    /**
     * Obtener productos destacados
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getFeaturedProducts() {
        logger.info("Obteniendo productos destacados");
        List<Product> products = productRepository.findByFeaturedTrueAndIsActiveTrue();
        return productMapper.toDTOList(products);
    }

    /**
     * Obtener productos en stock
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getInStockProducts() {
        logger.info("Obteniendo productos en stock");
        List<Product> products = productRepository.findInStockProducts();
        return productMapper.toDTOList(products);
    }

    /**
     * Obtener productos con bajo stock
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getLowStockProducts(Integer threshold) {
        logger.info("Obteniendo productos con bajo stock (umbral: {})", threshold);
        List<Product> products = productRepository.findLowStockProducts(threshold);
        return productMapper.toDTOList(products);
    }

    /**
     * Buscar productos por rango de precios
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        logger.info("Obteniendo productos por rango de precios: {} - {}", minPrice, maxPrice);
        List<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice);
        return productMapper.toDTOList(products);
    }

    /**
     * Actualizar producto
     */
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        logger.info("Actualizando producto con ID: {}", id);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        // Verificar si el nuevo SKU ya existe en otro producto
        if (!existingProduct.getSku().equals(productDTO.getSku())) {
            Optional<Product> duplicateProduct = productRepository.findBySkuAndIsActiveTrue(productDTO.getSku());
            if (duplicateProduct.isPresent()) {
                throw new DuplicateResourceException("Ya existe un producto con el SKU: " + productDTO.getSku());
            }
        }

        // Actualizar entidades relacionadas si cambiaron
        if (!existingProduct.getAlbum().getId().equals(productDTO.getAlbumId())) {
            Album newAlbum = albumRepository.findById(productDTO.getAlbumId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Álbum no encontrado con ID: " + productDTO.getAlbumId()));
            existingProduct.setAlbum(newAlbum);
        }

        if (!existingProduct.getProvider().getId().equals(productDTO.getProviderId())) {
            Provider newProvider = providerRepository.findById(productDTO.getProviderId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Proveedor no encontrado con ID: " + productDTO.getProviderId()));
            existingProduct.setProvider(newProvider);
        }

        if (!existingProduct.getCategory().getId().equals(productDTO.getCategoryId())) {
            Category newCategory = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Categoría no encontrada con ID: " + productDTO.getCategoryId()));
            existingProduct.setCategory(newCategory);
        }

        // Validar campos específicos según el tipo de producto
        validateProductTypeSpecificFields(productDTO);

        productMapper.updateEntity(existingProduct, productDTO);
        Product updatedProduct = productRepository.save(existingProduct);

        logger.info("Producto actualizado exitosamente con ID: {}", updatedProduct.getId());
        return productMapper.toDTO(updatedProduct);
    }

    /**
     * Eliminar producto (soft delete)
     */
    public void deleteProduct(Long id) {
        logger.info("Eliminando producto con ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        product.setIsActive(false);
        productRepository.save(product);

        logger.info("Producto eliminado exitosamente con ID: {}", id);
    }

    /**
     * Actualizar stock de producto
     */
    public ProductDTO updateStock(Long id, Integer newStock) {
        logger.info("Actualizando stock del producto ID: {} a {}", id, newStock);

        if (newStock < 0) {
            throw new BusinessException("El stock no puede ser negativo");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        product.setStockQuantity(newStock);
        Product updatedProduct = productRepository.save(product);

        logger.info("Stock actualizado exitosamente para producto ID: {}", id);
        return productMapper.toDTO(updatedProduct);
    }

    /**
     * Marcar/desmarcar producto como destacado
     */
    public ProductDTO toggleFeatured(Long id) {
        logger.info("Cambiando estado destacado del producto ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        product.setFeatured(!product.getFeatured());
        Product updatedProduct = productRepository.save(product);

        logger.info("Estado destacado cambiado para producto ID: {} -> {}", id, updatedProduct.getFeatured());
        return productMapper.toDTO(updatedProduct);
    }

    // ================= MÉTODOS PARA LA HISTORIA DE USUARIO =================

    /**
     * MÉTODO PRINCIPAL PARA LA HISTORIA DE USUARIO:
     * Obtener formatos alternativos de un producto (si es digital, mostrar vinilo y
     * viceversa)
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getAlternativeFormats(Long productId) {
        logger.info("Obteniendo formatos alternativos para producto ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productId));

        List<Product> alternativeProducts = productRepository.findAlternativeFormats(
                product.getAlbum().getId(),
                product.getProductType());

        List<ProductDTO> alternatives = productMapper.toDTOList(alternativeProducts);
        logger.info("Encontrados {} formatos alternativos", alternatives.size());

        return alternatives;
    }

    /**
     * Verificar si un producto tiene versión alternativa (vinilo para digital o
     * viceversa)
     */
    @Transactional(readOnly = true)
    public boolean hasAlternativeFormat(Long productId) {
        logger.info("Verificando si producto ID: {} tiene formato alternativo", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productId));

        List<Product> alternatives = productRepository.findAlternativeFormats(
                product.getAlbum().getId(),
                product.getProductType());

        boolean hasAlternative = !alternatives.isEmpty();
        logger.info("Producto ID: {} {} formato alternativo", productId, hasAlternative ? "tiene" : "no tiene");

        return hasAlternative;
    }

    /**
     * Obtener todos los formatos de un álbum específico
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllFormatsByAlbum(Long albumId) {
        logger.info("Obteniendo todos los formatos para álbum ID: {}", albumId);

        List<Product> products = productRepository.findAllFormatsByAlbumId(albumId);
        List<ProductDTO> productDTOs = productMapper.toDTOList(products);

        // Agregar información de formatos alternativos para cada producto
        for (ProductDTO productDTO : productDTOs) {
            List<ProductDTO> alternatives = getAlternativeFormats(productDTO.getId());
            productDTO.setAlternativeFormats(alternatives);
        }

        logger.info("Encontrados {} formatos para álbum ID: {}", productDTOs.size(), albumId);
        return productDTOs;
    }

    /**
     * Buscar productos digitales que tienen versión en vinilo
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getDigitalProductsWithVinylVersion() {
        logger.info("Obteniendo productos digitales que tienen versión en vinilo");

        List<Product> digitalProducts = productRepository.findByProductTypeAndIsActiveTrue(ProductType.DIGITAL);

        return digitalProducts.stream()
                .filter(product -> productRepository.hasVinylVersion(product.getAlbum().getId()))
                .map(productMapper::toDTO)
                .toList();
    }

    /**
     * Buscar productos de vinilo que tienen versión digital
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getVinylProductsWithDigitalVersion() {
        logger.info("Obteniendo productos de vinilo que tienen versión digital");

        List<Product> vinylProducts = productRepository.findByProductTypeAndIsActiveTrue(ProductType.PHYSICAL);

        return vinylProducts.stream()
                .filter(product -> productRepository.hasDigitalVersion(product.getAlbum().getId()))
                .map(productMapper::toDTO)
                .toList();
    }

    // ================= MÉTODOS DE UTILIDAD =================

    /**
     * Validar campos específicos según el tipo de producto
     */
    private void validateProductTypeSpecificFields(ProductDTO productDTO) {
        if (productDTO.getProductType() == ProductType.PHYSICAL) {
            // Validar campos requeridos para productos físicos
            if (productDTO.getVinylSize() == null) {
                throw new BusinessException("El tamaño del vinilo es obligatorio para productos físicos");
            }
            if (productDTO.getVinylSpeed() == null) {
                throw new BusinessException("La velocidad del vinilo es obligatoria para productos físicos");
            }

            // Limpiar campos que no aplican para productos físicos
            productDTO.setFileFormat(null);
            productDTO.setFileSizeMb(null);

        } else if (productDTO.getProductType() == ProductType.DIGITAL) {
            // Validar campos requeridos para productos digitales
            if (productDTO.getFileFormat() == null || productDTO.getFileFormat().trim().isEmpty()) {
                throw new BusinessException("El formato de archivo es obligatorio para productos digitales");
            }
            if (productDTO.getFileSizeMb() == null || productDTO.getFileSizeMb() <= 0) {
                throw new BusinessException("El tamaño del archivo debe ser mayor a 0 para productos digitales");
            }

            // Limpiar campos que no aplican para productos digitales
            productDTO.setVinylSize(null);
            productDTO.setVinylSpeed(null);
            productDTO.setWeightGrams(null);

            // Para productos digitales, el stock debería ser ilimitado o muy alto
            if (productDTO.getStockQuantity() < 999) {
                productDTO.setStockQuantity(9999); // Stock "ilimitado" para productos digitales
            }
        }
    }

    /**
     * Obtener estadísticas de productos
     */
    @Transactional(readOnly = true)
    public ProductStatisticsDTO getProductStatistics() {
        logger.info("Obteniendo estadísticas de productos");

        Long totalProducts = (long) productRepository.findByIsActiveTrue().size();
        Long digitalProducts = productRepository.countByProductType(ProductType.DIGITAL);
        Long physicalProducts = productRepository.countByProductType(ProductType.PHYSICAL);
        Long inStockProducts = (long) productRepository.findInStockProducts().size();
        Long outOfStockProducts = (long) productRepository.findOutOfStockProducts().size();

        ProductStatisticsDTO stats = new ProductStatisticsDTO();
        stats.setTotalProducts(totalProducts);
        stats.setDigitalProducts(digitalProducts);
        stats.setPhysicalProducts(physicalProducts);
        stats.setInStockProducts(inStockProducts);
        stats.setOutOfStockProducts(outOfStockProducts);

        return stats;
    }

    // DTO interno para estadísticas
    public static class ProductStatisticsDTO {
        private Long totalProducts;
        private Long digitalProducts;
        private Long physicalProducts;
        private Long inStockProducts;
        private Long outOfStockProducts;

        // Getters y Setters
        public Long getTotalProducts() {
            return totalProducts;
        }

        public void setTotalProducts(Long totalProducts) {
            this.totalProducts = totalProducts;
        }

        public Long getDigitalProducts() {
            return digitalProducts;
        }

        public void setDigitalProducts(Long digitalProducts) {
            this.digitalProducts = digitalProducts;
        }

        public Long getPhysicalProducts() {
            return physicalProducts;
        }

        public void setPhysicalProducts(Long physicalProducts) {
            this.physicalProducts = physicalProducts;
        }

        public Long getInStockProducts() {
            return inStockProducts;
        }

        public void setInStockProducts(Long inStockProducts) {
            this.inStockProducts = inStockProducts;
        }

        public Long getOutOfStockProducts() {
            return outOfStockProducts;
        }

        public void setOutOfStockProducts(Long outOfStockProducts) {
            this.outOfStockProducts = outOfStockProducts;
        }
    }

    /**
     * Actualizar el stock de un producto específico
     * Solo el proveedor dueño puede actualizar el stock
     */
    @Transactional
    public ProductInventoryResponseDTO updateProductStock(Long productId, Long providerId,
            ProductInventoryUpdateDTO updateDTO) {
        logger.info("=== DEBUG INVENTORY ===");
        logger.info("Recibido - productId: {}, providerId: {}", productId, providerId);

        // Validar que el producto existe
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

        logger.info("Producto encontrado - ID: {}, Provider ID del producto: {}", product.getId(),
                product.getProvider().getId());

        // Validar que el producto pertenece al proveedor
        if (!product.getProvider().getId().equals(providerId)) {
            logger.error("Error de ownership - Producto provider ID: {}, Usuario provider ID: {}",
                    product.getProvider().getId(), providerId);
            throw new RuntimeException("No tienes permisos para actualizar este producto");
        }

        logger.info("Ownership validado correctamente");

        // Validar que el producto está activo
        if (!product.getIsActive()) {
            throw new RuntimeException("No se puede actualizar stock de un producto inactivo");
        }

        // Actualizar el stock
        Integer previousStock = product.getStockQuantity();
        product.setStockQuantity(updateDTO.getStockQuantity());
        product.setUpdatedAt(LocalDateTime.now());

        // Guardar el producto
        Product savedProduct = productRepository.save(product);

        logger.info("Stock actualizado - Producto: {}, Stock anterior: {}, Stock nuevo: {}, Proveedor: {}, Razón: {}",
                productId, previousStock, updateDTO.getStockQuantity(), providerId, updateDTO.getUpdateReason());

        return mapToInventoryResponseDTO(savedProduct);
    }

    /**
     * Ajustar el stock de un producto (incrementar o decrementar)
     */
    @Transactional
    public ProductInventoryResponseDTO adjustProductStock(Long productId, Long providerId,
            ProductStockAdjustmentDTO adjustmentDTO) {
        // Validar que el producto existe
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

        // Validar que el producto pertenece al proveedor
        if (!product.getProvider().getId().equals(providerId)) {
            throw new RuntimeException("No tienes permisos para ajustar este producto");
        }

        // Validar que el producto está activo
        if (!product.getIsActive()) {
            throw new RuntimeException("No se puede ajustar stock de un producto inactivo");
        }

        Integer currentStock = product.getStockQuantity();
        Integer newStock;

        // Calcular el nuevo stock según el tipo de ajuste
        switch (adjustmentDTO.getAdjustmentType()) {
            case INCREMENT:
                newStock = currentStock + adjustmentDTO.getQuantity();
                break;
            case DECREMENT:
                newStock = currentStock - adjustmentDTO.getQuantity();
                // Validar que no sea negativo
                if (newStock < 0) {
                    throw new RuntimeException("El stock no puede ser negativo. Stock actual: " + currentStock +
                            ", Cantidad a decrementar: " + adjustmentDTO.getQuantity());
                }
                break;
            default:
                throw new RuntimeException("Tipo de ajuste no válido: " + adjustmentDTO.getAdjustmentType());
        }

        // Actualizar el stock
        product.setStockQuantity(newStock);
        product.setUpdatedAt(LocalDateTime.now());

        // Guardar el producto
        Product savedProduct = productRepository.save(product);

        // Log del cambio
        logger.info(
                "Stock ajustado - Producto: {}, Tipo: {}, Cantidad: {}, Stock anterior: {}, Stock nuevo: {}, Razón: {}",
                productId, adjustmentDTO.getAdjustmentType(), adjustmentDTO.getQuantity(),
                currentStock, newStock, adjustmentDTO.getReason());

        return mapToInventoryResponseDTO(savedProduct);
    }

    /**
     * Obtener información de inventario de un producto específico
     */
    @Transactional(readOnly = true)
    public ProductInventoryResponseDTO getProductInventory(Long productId, Long providerId) {
        // Validar que el producto existe
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

        // Validar que el producto pertenece al proveedor
        if (!product.getProvider().getId().equals(providerId)) {
            throw new RuntimeException("No tienes permisos para ver este producto");
        }

        return mapToInventoryResponseDTO(product);
    }

    /**
     * Obtener resumen completo del inventario de un proveedor
     */
    @Transactional(readOnly = true)
    public ProviderInventorySummaryDTO getProviderInventorySummary(Long providerId) {
        // Validar que el proveedor existe
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + providerId));

        // Obtener todos los productos del proveedor
        List<Product> providerProducts = productRepository.findByProviderIdAndIsActiveTrue(providerId);

        // Mapear a DTOs de respuesta
        List<ProductInventoryResponseDTO> productInventoryList = providerProducts.stream()
                .map(this::mapToInventoryResponseDTO)
                .collect(Collectors.toList());

        // Calcular estadísticas
        Integer totalProducts = providerProducts.size();
        Integer totalUnitsInStock = providerProducts.stream()
                .mapToInt(Product::getStockQuantity)
                .sum();
        Integer productsWithStock = (int) providerProducts.stream()
                .filter(p -> p.getStockQuantity() > 0)
                .count();
        Integer productsOutOfStock = totalProducts - productsWithStock;

        // Crear el DTO de resumen
        ProviderInventorySummaryDTO summary = new ProviderInventorySummaryDTO(
                providerId,
                provider.getBusinessName(),
                totalProducts,
                totalUnitsInStock,
                productsWithStock,
                productsOutOfStock,
                productInventoryList);

        return summary;
    }

    /**
     * Obtener productos con stock bajo para un proveedor
     */
    @Transactional(readOnly = true)
    public List<ProductInventoryResponseDTO> getProductsWithLowStock(Long providerId, Integer minStock) {
        // Validar que el proveedor existe
        if (!providerRepository.existsById(providerId)) {
            throw new RuntimeException("Proveedor no encontrado con ID: " + providerId);
        }

        // Obtener productos con stock bajo
        List<Product> lowStockProducts = productRepository
                .findByProviderIdAndIsActiveTrueAndStockQuantityLessThanEqual(providerId, minStock);

        return lowStockProducts.stream()
                .map(this::mapToInventoryResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener productos sin stock de un proveedor
     */
    @Transactional(readOnly = true)
    public List<ProductInventoryResponseDTO> getProductsOutOfStock(Long providerId) {
        return getProductsWithLowStock(providerId, 0);
    }

    /**
     * Mapear Product a ProductInventoryResponseDTO
     */
    private ProductInventoryResponseDTO mapToInventoryResponseDTO(Product product) {
        return new ProductInventoryResponseDTO(
                product.getId(),
                product.getSku(),
                product.getAlbum().getTitle(),
                product.getAlbum().getArtist().getName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getProductType().toString(),
                product.getConditionType().toString(),
                product.getIsActive(),
                product.getFeatured(),
                product.getUpdatedAt());
    }

    /**
     * Crear un producto en el catálogo del proveedor
     */
    @Transactional
    public ProductCatalogResponseDTO createCatalogProduct(Long providerId, ProductCatalogCreateDTO createDTO) {
        // Validar que el proveedor existe y está verificado
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + providerId));

        if (!provider.getVerificationStatus().equals(VerificationStatus.VERIFIED)) {
            throw new RuntimeException("El proveedor debe estar verificado para crear productos");
        }

        // Validar que el álbum existe
        Album album = albumRepository.findById(createDTO.getAlbumId())
                .orElseThrow(() -> new RuntimeException("Álbum no encontrado con ID: " + createDTO.getAlbumId()));

        // Validar que la categoría existe
        Category category = categoryRepository.findById(createDTO.getCategoryId())
                .orElseThrow(
                        () -> new RuntimeException("Categoría no encontrada con ID: " + createDTO.getCategoryId()));

        // Validar SKU único
        if (productRepository.existsBySku(createDTO.getSku())) {
            throw new RuntimeException("Ya existe un producto con SKU: " + createDTO.getSku());
        }

        // Crear el producto
        Product product = new Product();
        product.setAlbum(album);
        product.setProvider(provider);
        product.setCategory(category);
        product.setSku(createDTO.getSku());
        product.setProductType(createDTO.getProductType());
        product.setConditionType(createDTO.getConditionType());
        product.setPrice(createDTO.getPrice());
        product.setStockQuantity(createDTO.getStockQuantity());
        product.setFeatured(createDTO.getFeatured() != null ? createDTO.getFeatured() : false);
        product.setIsActive(true);

        // Campos específicos según el tipo de producto
        if (createDTO.getProductType() == ProductType.PHYSICAL) {
            product.setVinylSize(createDTO.getVinylSize());
            product.setVinylSpeed(createDTO.getVinylSpeed());
            product.setWeightGrams(createDTO.getWeightGrams());
        } else if (createDTO.getProductType() == ProductType.DIGITAL) {
            product.setFileFormat(createDTO.getFileFormat());
            product.setFileSizeMb(createDTO.getFileSizeMb());
        }

        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        // Guardar el producto
        Product savedProduct = productRepository.save(product);

        logger.info("Producto creado en catálogo - ID: {}, SKU: {}, Proveedor: {}",
                savedProduct.getId(), savedProduct.getSku(), providerId);

        return mapToCatalogResponseDTO(savedProduct);
    }

    /**
     * Actualizar un producto del catálogo
     */
    @Transactional
    public ProductCatalogResponseDTO updateCatalogProduct(Long providerId, Long productId,
            ProductCatalogUpdateDTO updateDTO) {
        // Validar que el producto existe
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

        // Validar que el producto pertenece al proveedor
        if (!product.getProvider().getId().equals(providerId)) {
            throw new RuntimeException("No tienes permisos para actualizar este producto");
        }

        // Actualizar campos si están presentes en el DTO
        if (updateDTO.getSku() != null && !updateDTO.getSku().equals(product.getSku())) {
            // Validar SKU único si se está cambiando
            if (productRepository.existsBySku(updateDTO.getSku())) {
                throw new RuntimeException("Ya existe un producto con SKU: " + updateDTO.getSku());
            }
            product.setSku(updateDTO.getSku());
        }

        if (updateDTO.getConditionType() != null) {
            product.setConditionType(updateDTO.getConditionType());
        }

        if (updateDTO.getPrice() != null) {
            product.setPrice(updateDTO.getPrice());
        }

        if (updateDTO.getStockQuantity() != null) {
            product.setStockQuantity(updateDTO.getStockQuantity());
        }

        if (updateDTO.getFeatured() != null) {
            product.setFeatured(updateDTO.getFeatured());
        }

        if (updateDTO.getDescription() != null) {
            // Asumiendo que agregamos campo description a Product entity
            // product.setDescription(updateDTO.getDescription());
        }

        // Actualizar campos específicos por tipo
        if (product.getProductType() == ProductType.PHYSICAL) {
            if (updateDTO.getVinylSize() != null) {
                product.setVinylSize(updateDTO.getVinylSize());
            }
            if (updateDTO.getVinylSpeed() != null) {
                product.setVinylSpeed(updateDTO.getVinylSpeed());
            }
            if (updateDTO.getWeightGrams() != null) {
                product.setWeightGrams(updateDTO.getWeightGrams());
            }
        } else if (product.getProductType() == ProductType.DIGITAL) {
            if (updateDTO.getFileFormat() != null) {
                product.setFileFormat(updateDTO.getFileFormat());
            }
            if (updateDTO.getFileSizeMb() != null) {
                product.setFileSizeMb(updateDTO.getFileSizeMb());
            }
        }

        product.setUpdatedAt(LocalDateTime.now());

        // Guardar cambios
        Product savedProduct = productRepository.save(product);

        logger.info("Producto actualizado - ID: {}, Proveedor: {}, Razón: {}",
                productId, providerId, updateDTO.getUpdateReason());

        return mapToCatalogResponseDTO(savedProduct);
    }

    /**
     * Obtener catálogo completo del proveedor
     */
    @Transactional(readOnly = true)
    public ProviderCatalogSummaryDTO getProviderCatalog(Long providerId) {
        // Validar que el proveedor existe
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + providerId));

        // Obtener todos los productos del proveedor
        List<Product> allProducts = productRepository.findByProviderId(providerId);

        // Calcular estadísticas
        ProviderCatalogSummaryDTO summary = new ProviderCatalogSummaryDTO(providerId, provider.getBusinessName());

        summary.setTotalProducts(allProducts.size());
        summary.setActiveProducts((int) allProducts.stream().filter(Product::getIsActive).count());
        summary.setInactiveProducts(summary.getTotalProducts() - summary.getActiveProducts());
        summary.setFeaturedProducts((int) allProducts.stream().filter(Product::getFeatured).count());
        summary.setProductsInStock((int) allProducts.stream().filter(p -> p.getStockQuantity() > 0).count());
        summary.setProductsOutOfStock(summary.getTotalProducts() - summary.getProductsInStock());

        summary.setPhysicalProducts((int) allProducts.stream()
                .filter(p -> p.getProductType() == ProductType.PHYSICAL).count());
        summary.setDigitalProducts((int) allProducts.stream()
                .filter(p -> p.getProductType() == ProductType.DIGITAL).count());

        summary.setNewProducts((int) allProducts.stream()
                .filter(p -> p.getConditionType() == ConditionType.NEW).count());
        summary.setUsedProducts(summary.getTotalProducts() - summary.getNewProducts());

        // Calcular precio promedio
        BigDecimal averagePrice = BigDecimal.ZERO;
        if (!allProducts.isEmpty()) {
            BigDecimal totalPrice = allProducts.stream()
                    .map(Product::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            averagePrice = totalPrice.divide(BigDecimal.valueOf(allProducts.size()), 2, RoundingMode.HALF_UP);
        }
        summary.setAveragePrice(averagePrice);

        // Calcular valor total del catálogo
        BigDecimal totalValue = allProducts.stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStockQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotalCatalogValue(totalValue);

        // Mapear productos a DTOs
        List<ProductCatalogResponseDTO> productDTOs = allProducts.stream()
                .map(this::mapToCatalogResponseDTO)
                .collect(Collectors.toList());
        summary.setProducts(productDTOs);

        return summary;
    }

    /**
     * Buscar productos en el catálogo con filtros
     */
    @Transactional(readOnly = true)
    public List<ProductCatalogResponseDTO> searchCatalogProducts(CatalogFilterDTO filterDTO) {
        // Aquí implementarías la lógica de búsqueda con criterios dinámicos
        // Por simplicidad, implemento una versión básica

        List<Product> products = productRepository.findAll();

        // Filtro por búsqueda de texto
        if (filterDTO.getSearchQuery() != null && !filterDTO.getSearchQuery().isEmpty()) {
            String query = filterDTO.getSearchQuery().toLowerCase();
            products = products.stream()
                    .filter(p -> p.getAlbum().getTitle().toLowerCase().contains(query) ||
                            p.getAlbum().getArtist().getName().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

        // Filtro por categoría
        if (filterDTO.getCategoryId() != null) {
            products = products.stream()
                    .filter(p -> p.getCategory().getId().equals(filterDTO.getCategoryId()))
                    .collect(Collectors.toList());
        }

        // Filtro por tipo de producto
        if (filterDTO.getProductType() != null) {
            products = products.stream()
                    .filter(p -> p.getProductType().equals(filterDTO.getProductType()))
                    .collect(Collectors.toList());
        }

        // Filtro por rango de precios
        if (filterDTO.getMinPrice() != null) {
            products = products.stream()
                    .filter(p -> p.getPrice().compareTo(filterDTO.getMinPrice()) >= 0)
                    .collect(Collectors.toList());
        }

        if (filterDTO.getMaxPrice() != null) {
            products = products.stream()
                    .filter(p -> p.getPrice().compareTo(filterDTO.getMaxPrice()) <= 0)
                    .collect(Collectors.toList());
        }

        // Filtro solo con stock
        if (filterDTO.getInStockOnly() != null && filterDTO.getInStockOnly()) {
            products = products.stream()
                    .filter(p -> p.getStockQuantity() > 0)
                    .collect(Collectors.toList());
        }

        // Filtro solo destacados
        if (filterDTO.getFeaturedOnly() != null && filterDTO.getFeaturedOnly()) {
            products = products.stream()
                    .filter(Product::getFeatured)
                    .collect(Collectors.toList());
        }

        // Filtro solo activos
        if (filterDTO.getActiveOnly() != null && filterDTO.getActiveOnly()) {
            products = products.stream()
                    .filter(Product::getIsActive)
                    .collect(Collectors.toList());
        }

        // Ordenamiento básico
        if ("price".equals(filterDTO.getSortBy())) {
            products.sort((p1, p2) -> "asc".equals(filterDTO.getSortDirection())
                    ? p1.getPrice().compareTo(p2.getPrice())
                    : p2.getPrice().compareTo(p1.getPrice()));
        } else if ("createdAt".equals(filterDTO.getSortBy())) {
            products.sort((p1, p2) -> "asc".equals(filterDTO.getSortDirection())
                    ? p1.getCreatedAt().compareTo(p2.getCreatedAt())
                    : p2.getCreatedAt().compareTo(p1.getCreatedAt()));
        }

        return products.stream()
                .map(this::mapToCatalogResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Activar/Desactivar producto del catálogo
     */
    @Transactional
    public ProductCatalogResponseDTO toggleProductStatus(Long providerId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

        // Validar ownership
        if (!product.getProvider().getId().equals(providerId)) {
            throw new RuntimeException("No tienes permisos para modificar este producto");
        }

        product.setIsActive(!product.getIsActive());
        product.setUpdatedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);

        logger.info("Producto {} - ID: {}, Proveedor: {}",
                savedProduct.getIsActive() ? "activado" : "desactivado", productId, providerId);

        return mapToCatalogResponseDTO(savedProduct);
    }

    /**
     * Eliminar producto del catálogo (soft delete)
     */
    @Transactional
    public void deleteCatalogProduct(Long providerId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

        // Validar ownership
        if (!product.getProvider().getId().equals(providerId)) {
            throw new RuntimeException("No tienes permisos para eliminar este producto");
        }

        product.setIsActive(false);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        logger.info("Producto eliminado (soft delete) - ID: {}, Proveedor: {}", productId, providerId);
    }

    /**
     * Mapear Product a ProductCatalogResponseDTO
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

    // ================= MÉTODOS ESPECÍFICOS PARA HISTORIA DE USUARIO
    // =================

    /**
     * MÉTODO PRINCIPAL: Obtener formatos alternativos con información completa del
     * álbum
     */
    @Transactional(readOnly = true)
    public AlbumFormatsResponseDTO getProductAlternativeFormats(Long productId) {
        logger.info("Obteniendo formatos alternativos para producto ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productId));

        Album album = product.getAlbum();

        // Obtener todos los productos del álbum
        List<Product> allAlbumProducts = productRepository.findAllFormatsByAlbumId(album.getId());

        // Separar por tipo de formato
        List<AlbumFormatsResponseDTO.FormatAvailabilityDTO> availableFormats = allAlbumProducts.stream()
                .map(this::mapToFormatAvailabilityDTO)
                .collect(Collectors.toList());

        AlbumFormatsResponseDTO response = new AlbumFormatsResponseDTO(
                album.getId(),
                album.getTitle(),
                album.getArtist().getName(),
                album.getReleaseYear(),
                availableFormats);

        logger.info("Encontrados {} formatos para álbum '{}'", availableFormats.size(), album.getTitle());
        return response;
    }

    /**
     * Obtener información completa de todos los formatos de un álbum
     */
    @Transactional(readOnly = true)
    public AlbumFormatsDTO getAlbumAllFormats(Long albumId) {
        logger.info("Obteniendo todos los formatos para álbum ID: {}", albumId);

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado con ID: " + albumId));

        List<Product> allProducts = productRepository.findAllFormatsByAlbumId(albumId);

        // Separar productos por tipo
        List<ProductDTO> digitalVersions = allProducts.stream()
                .filter(p -> p.getProductType() == ProductType.DIGITAL)
                .map(productMapper::toDTO)
                .collect(Collectors.toList());

        List<ProductDTO> vinylVersions = allProducts.stream()
                .filter(p -> p.getProductType() == ProductType.PHYSICAL)
                .map(productMapper::toDTO)
                .collect(Collectors.toList());

        AlbumFormatsDTO albumFormats = new AlbumFormatsDTO(albumId, album.getTitle(), album.getArtist().getName());
        albumFormats.setGenreName(album.getGenre() != null ? album.getGenre().getName() : null);
        albumFormats.setReleaseYear(album.getReleaseYear());
        albumFormats.setDigitalVersions(digitalVersions);
        albumFormats.setVinylVersions(vinylVersions);

        // Establecer recomendaciones (versión más económica de cada tipo)
        if (!digitalVersions.isEmpty()) {
            ProductDTO cheapestDigital = digitalVersions.stream()
                    .min(Comparator.comparing(ProductDTO::getPrice))
                    .orElse(null);
            albumFormats.setRecommendedDigital(cheapestDigital);
        }

        if (!vinylVersions.isEmpty()) {
            ProductDTO cheapestVinyl = vinylVersions.stream()
                    .min(Comparator.comparing(ProductDTO::getPrice))
                    .orElse(null);
            albumFormats.setRecommendedVinyl(cheapestVinyl);
        }

        logger.info("Álbum '{}' tiene {} versiones digitales y {} versiones en vinilo",
                album.getTitle(), digitalVersions.size(), vinylVersions.size());

        return albumFormats;
    }

    /**
     * Obtener álbumes digitales que tienen versión en vinilo
     */
    @Transactional(readOnly = true)
    public List<AlbumFormatsDTO> getDigitalAlbumsWithVinylVersion() {
        logger.info("Obteniendo álbumes digitales que tienen versión en vinilo");

        List<Product> digitalProducts = productRepository.findByProductTypeAndIsActiveTrue(ProductType.DIGITAL);

        // Agrupar por álbum y filtrar solo los que tienen versión en vinilo
        Map<Long, Album> albumsWithBothFormats = digitalProducts.stream()
                .filter(product -> productRepository.hasVinylVersion(product.getAlbum().getId()))
                .collect(Collectors.toMap(
                        product -> product.getAlbum().getId(),
                        Product::getAlbum,
                        (existing, replacement) -> existing));

        return albumsWithBothFormats.values().stream()
                .map(album -> getAlbumAllFormats(album.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Obtener álbumes de vinilo que tienen versión digital
     */
    @Transactional(readOnly = true)
    public List<AlbumFormatsDTO> getVinylAlbumsWithDigitalVersion() {
        logger.info("Obteniendo álbumes de vinilo que tienen versión digital");

        List<Product> vinylProducts = productRepository.findByProductTypeAndIsActiveTrue(ProductType.PHYSICAL);

        // Agrupar por álbum y filtrar solo los que tienen versión digital
        Map<Long, Album> albumsWithBothFormats = vinylProducts.stream()
                .filter(product -> productRepository.hasDigitalVersion(product.getAlbum().getId()))
                .collect(Collectors.toMap(
                        product -> product.getAlbum().getId(),
                        Product::getAlbum,
                        (existing, replacement) -> existing));

        return albumsWithBothFormats.values().stream()
                .map(album -> getAlbumAllFormats(album.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Comparar formatos de un álbum con análisis de precios y disponibilidad
     */
    @Transactional(readOnly = true)
    public Map<String, Object> compareAlbumFormats(Long albumId) {
        logger.info("Comparando formatos para álbum ID: {}", albumId);

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado con ID: " + albumId));

        List<Product> allProducts = productRepository.findAllFormatsByAlbumId(albumId);

        Map<String, Object> comparison = new HashMap<>();
        comparison.put("albumId", albumId);
        comparison.put("albumTitle", album.getTitle());
        comparison.put("artistName", album.getArtist().getName());

        // Análisis por formato
        List<Product> digitalProducts = allProducts.stream()
                .filter(p -> p.getProductType() == ProductType.DIGITAL)
                .collect(Collectors.toList());

        List<Product> vinylProducts = allProducts.stream()
                .filter(p -> p.getProductType() == ProductType.PHYSICAL)
                .collect(Collectors.toList());

        // Información de formatos digitales
        Map<String, Object> digitalInfo = new HashMap<>();
        digitalInfo.put("available", !digitalProducts.isEmpty());
        digitalInfo.put("count", digitalProducts.size());
        if (!digitalProducts.isEmpty()) {
            digitalInfo.put("priceRange", getPriceRange(digitalProducts));
            digitalInfo.put("cheapestPrice", digitalProducts.stream()
                    .map(Product::getPrice)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO));
            digitalInfo.put("inStockCount", digitalProducts.stream()
                    .mapToInt(p -> p.getStockQuantity() > 0 ? 1 : 0)
                    .sum());
        }

        // Información de formatos en vinilo
        Map<String, Object> vinylInfo = new HashMap<>();
        vinylInfo.put("available", !vinylProducts.isEmpty());
        vinylInfo.put("count", vinylProducts.size());
        if (!vinylProducts.isEmpty()) {
            vinylInfo.put("priceRange", getPriceRange(vinylProducts));
            vinylInfo.put("cheapestPrice", vinylProducts.stream()
                    .map(Product::getPrice)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO));
            vinylInfo.put("inStockCount", vinylProducts.stream()
                    .mapToInt(p -> p.getStockQuantity() > 0 ? 1 : 0)
                    .sum());

            // Información específica de vinilos
            Map<String, Long> sizeDistribution = vinylProducts.stream()
                    .filter(p -> p.getVinylSize() != null)
                    .collect(Collectors.groupingBy(
                            p -> p.getVinylSize().toString(),
                            Collectors.counting()));
            vinylInfo.put("sizeDistribution", sizeDistribution);
        }

        comparison.put("digitalFormat", digitalInfo);
        comparison.put("vinylFormat", vinylInfo);

        // Recomendaciones
        List<String> recommendations = new ArrayList<>();
        if (!digitalProducts.isEmpty() && !vinylProducts.isEmpty()) {
            BigDecimal digitalMinPrice = digitalProducts.stream()
                    .map(Product::getPrice)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

            BigDecimal vinylMinPrice = vinylProducts.stream()
                    .map(Product::getPrice)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

            if (digitalMinPrice.compareTo(vinylMinPrice) < 0) {
                recommendations.add("El formato digital es más económico");
            } else {
                recommendations.add("El formato vinilo ofrece mejor experiencia de colección");
            }

            long digitalInStock = digitalProducts.stream().filter(p -> p.getStockQuantity() > 0).count();
            long vinylInStock = vinylProducts.stream().filter(p -> p.getStockQuantity() > 0).count();

            if (digitalInStock > vinylInStock) {
                recommendations.add("Mejor disponibilidad en formato digital");
            } else if (vinylInStock > digitalInStock) {
                recommendations.add("Mejor disponibilidad en formato vinilo");
            }
        } else if (!digitalProducts.isEmpty()) {
            recommendations.add("Solo disponible en formato digital");
        } else if (!vinylProducts.isEmpty()) {
            recommendations.add("Solo disponible en formato vinilo");
        } else {
            recommendations.add("No hay formatos disponibles actualmente");
        }

        comparison.put("recommendations", recommendations);
        comparison.put("hasBothFormats", !digitalProducts.isEmpty() && !vinylProducts.isEmpty());

        return comparison;
    }

    // ================= MÉTODOS AUXILIARES =================

    /**
     * Mapear Product a FormatAvailabilityDTO
     */
    private AlbumFormatsResponseDTO.FormatAvailabilityDTO mapToFormatAvailabilityDTO(Product product) {
        AlbumFormatsResponseDTO.FormatAvailabilityDTO dto = new AlbumFormatsResponseDTO.FormatAvailabilityDTO(
                product.getId(),
                product.getProductType(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getIsActive());

        // Campos específicos para vinilo
        if (product.getProductType() == ProductType.PHYSICAL) {
            if (product.getVinylSize() != null) {
                dto.setVinylSize(product.getVinylSize().toString());
            }
            if (product.getVinylSpeed() != null) {
                dto.setVinylSpeed(product.getVinylSpeed().toString());
            }
            if (product.getConditionType() != null) {
                dto.setConditionType(product.getConditionType().toString());
            }
        }

        // Campos específicos para digital
        if (product.getProductType() == ProductType.DIGITAL) {
            dto.setFileFormat(product.getFileFormat());
            dto.setAudioQuality(determineAudioQuality(product.getFileFormat()));
        }

        return dto;
    }

    /**
     * Determinar calidad de audio basado en el formato
     */
    private String determineAudioQuality(String fileFormat) {
        if (fileFormat == null)
            return "Unknown";

        return switch (fileFormat.toLowerCase()) {
            case "flac" -> "Lossless";
            case "wav" -> "Lossless";
            case "mp3" -> "Lossy";
            case "aac" -> "Lossy";
            case "ogg" -> "Lossy";
            default -> "Standard";
        };
    }

    /**
     * Obtener rango de precios de una lista de productos
     */
    private Map<String, BigDecimal> getPriceRange(List<Product> products) {
        if (products.isEmpty()) {
            return Map.of("min", BigDecimal.ZERO, "max", BigDecimal.ZERO);
        }

        BigDecimal minPrice = products.stream()
                .map(Product::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal maxPrice = products.stream()
                .map(Product::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        return Map.of("min", minPrice, "max", maxPrice);
    }

    // Agregar estos métodos al ProductService existente

    /**
     * Obtener productos del proveedor con paginación y filtros
     */
    @Transactional(readOnly = true)
    public Page<ProductCatalogResponseDTO> getProviderProductsWithPagination(
            Long providerId, Pageable pageable, String searchQuery, Boolean activeOnly) {

        logger.info("Obteniendo productos del proveedor {} con paginación", providerId);

        // Validar que el proveedor existe
        if (!providerRepository.existsById(providerId)) {
            throw new RuntimeException("Proveedor no encontrado con ID: " + providerId);
        }

        // Obtener productos con filtros
        Page<Product> productsPage;

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            // Búsqueda con filtro de texto
            if (activeOnly != null && activeOnly) {
                productsPage = productRepository
                        .findByProviderIdAndIsActiveTrueAndAlbumTitleContainingIgnoreCaseOrAlbumArtistNameContainingIgnoreCase(
                                providerId, searchQuery, pageable); // ← CORREGIDO: solo 3 parámetros
            } else {
                productsPage = productRepository
                        .findByProviderIdAndAlbumTitleContainingIgnoreCaseOrAlbumArtistNameContainingIgnoreCase(
                                providerId, searchQuery, pageable); // ← CORREGIDO: solo 3 parámetros
            }
        } else {
            // Sin filtro de texto
            if (activeOnly != null && activeOnly) {
                productsPage = productRepository.findByProviderIdAndIsActiveTrue(providerId, pageable);
            } else {
                productsPage = productRepository.findByProviderId(providerId, pageable);
            }
        }

        // Mapear a DTOs
        return productsPage.map(this::mapToCatalogResponseDTO);
    }

    /**
     * Obtener estadísticas rápidas del proveedor
     */
    @Transactional(readOnly = true)
    public QuickMetricsDTO getProviderQuickStats(Long providerId) {
        logger.info("Obteniendo estadísticas rápidas para proveedor ID: {}", providerId);

        // Validar que el proveedor existe
        if (!providerRepository.existsById(providerId)) {
            throw new RuntimeException("Proveedor no encontrado con ID: " + providerId);
        }

        // Obtener todos los productos del proveedor
        List<Product> allProducts = productRepository.findByProviderId(providerId);

        if (allProducts.isEmpty()) {
            return new QuickMetricsDTO(0, 0, 0, 0, 0, 0,
                    BigDecimal.ZERO, BigDecimal.ZERO, 0, 0);
        }

        // Calcular métricas
        Integer totalProducts = allProducts.size();
        Integer activeProducts = (int) allProducts.stream().filter(Product::getIsActive).count();
        Integer inactiveProducts = totalProducts - activeProducts;
        Integer productsInStock = (int) allProducts.stream().filter(p -> p.getStockQuantity() > 0).count();
        Integer productsOutOfStock = totalProducts - productsInStock;
        Integer featuredProducts = (int) allProducts.stream().filter(Product::getFeatured).count();

        Integer physicalProducts = (int) allProducts.stream()
                .filter(p -> p.getProductType() == ProductType.PHYSICAL).count();
        Integer digitalProducts = (int) allProducts.stream()
                .filter(p -> p.getProductType() == ProductType.DIGITAL).count();

        // Calcular valor total del catálogo
        BigDecimal totalCatalogValue = allProducts.stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStockQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular precio promedio
        BigDecimal averagePrice = BigDecimal.ZERO;
        if (!allProducts.isEmpty()) {
            BigDecimal totalPrice = allProducts.stream()
                    .map(Product::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            averagePrice = totalPrice.divide(BigDecimal.valueOf(allProducts.size()), 2, RoundingMode.HALF_UP);
        }

        return new QuickMetricsDTO(totalProducts, activeProducts, inactiveProducts,
                productsInStock, productsOutOfStock, featuredProducts,
                totalCatalogValue, averagePrice, physicalProducts, digitalProducts);
    }

    /**
     * Actualización masiva de precios
     */
    @Transactional
    public List<ProductCatalogResponseDTO> bulkUpdatePrices(Long providerId, ProductBulkUpdateDTO bulkUpdateDTO) {
        logger.info("Realizando actualización masiva para proveedor ID: {}, productos: {}",
                providerId, bulkUpdateDTO.getProductIds().size());

        // Validar que el proveedor existe
        if (!providerRepository.existsById(providerId)) {
            throw new RuntimeException("Proveedor no encontrado con ID: " + providerId);
        }

        // Obtener todos los productos a actualizar
        List<Product> productsToUpdate = productRepository.findAllById(bulkUpdateDTO.getProductIds());

        // Validar que todos los productos pertenecen al proveedor
        List<Product> invalidProducts = productsToUpdate.stream()
                .filter(p -> !p.getProvider().getId().equals(providerId))
                .collect(Collectors.toList());

        if (!invalidProducts.isEmpty()) {
            throw new RuntimeException("Algunos productos no pertenecen al proveedor autenticado");
        }

        // Guardar todos los productos actualizados
        List<Product> updatedProducts = productsToUpdate;
        List<Product> savedProducts = productRepository.saveAll(updatedProducts);

        logger.info("Actualización masiva completada - {} productos actualizados, Razón: {}",
                savedProducts.size(), bulkUpdateDTO.getReason());

        // Mapear a DTOs de respuesta
        return savedProducts.stream()
                .map(this::mapToCatalogResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Duplicar un producto existente
     */
    @Transactional
    public ProductCatalogResponseDTO duplicateProduct(Long providerId, Long productId, String newSku) {
        logger.info("Duplicando producto ID: {} para proveedor ID: {} con nuevo SKU: {}",
                productId, providerId, newSku);

        // Validar que el producto existe
        Product originalProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

        // Validar que el producto pertenece al proveedor
        if (!originalProduct.getProvider().getId().equals(providerId)) {
            throw new RuntimeException("No tienes permisos para duplicar este producto");
        }

        // Validar que el nuevo SKU no existe
        if (productRepository.existsBySku(newSku)) {
            throw new RuntimeException("Ya existe un producto con SKU: " + newSku);
        }

        // Crear nuevo producto duplicando el original
        Product duplicatedProduct = new Product();
        duplicatedProduct.setAlbum(originalProduct.getAlbum());
        duplicatedProduct.setProvider(originalProduct.getProvider());
        duplicatedProduct.setCategory(originalProduct.getCategory());
        duplicatedProduct.setSku(newSku);
        duplicatedProduct.setProductType(originalProduct.getProductType());
        duplicatedProduct.setConditionType(originalProduct.getConditionType());
        duplicatedProduct.setPrice(originalProduct.getPrice());
        duplicatedProduct.setStockQuantity(0); // Nuevo producto empieza sin stock

        // Copiar campos específicos según el tipo
        if (originalProduct.getProductType() == ProductType.PHYSICAL) {
            duplicatedProduct.setVinylSize(originalProduct.getVinylSize());
            duplicatedProduct.setVinylSpeed(originalProduct.getVinylSpeed());
            duplicatedProduct.setWeightGrams(originalProduct.getWeightGrams());
        } else if (originalProduct.getProductType() == ProductType.DIGITAL) {
            duplicatedProduct.setFileFormat(originalProduct.getFileFormat());
            duplicatedProduct.setFileSizeMb(originalProduct.getFileSizeMb());
        }

        duplicatedProduct.setFeatured(false); // No destacado por defecto
        duplicatedProduct.setIsActive(true);
        duplicatedProduct.setCreatedAt(LocalDateTime.now());
        duplicatedProduct.setUpdatedAt(LocalDateTime.now());

        // Guardar el producto duplicado
        Product savedProduct = productRepository.save(duplicatedProduct);

        logger.info("Producto duplicado exitosamente - ID original: {}, ID nuevo: {}, SKU nuevo: {}",
                productId, savedProduct.getId(), newSku);

        return mapToCatalogResponseDTO(savedProduct);
    }

    // Métodos auxiliares para repositorio (agregar a ProductRepository.java)

    // En ProductRepository.java, agregar estos métodos:
    /*
     * @Query("SELECT p FROM Product p WHERE p.provider.id = :providerId AND p.isActive = true "
     * +
     * "AND (LOWER(p.album.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) " +
     * "OR LOWER(p.album.artist.name) LIKE LOWER(CONCAT('%', :searchQuery, '%')))")
     * Page<Product>
     * findByProviderIdAndIsActiveTrueAndAlbumTitleContainingIgnoreCaseOrAlbumArtistNameContainingIgnoreCase(
     * 
     * @Param("providerId") Long providerId,
     * 
     * @Param("searchQuery") String searchQuery1,
     * 
     * @Param("searchQuery") String searchQuery2,
     * Pageable pageable);
     * 
     * @Query("SELECT p FROM Product p WHERE p.provider.id = :providerId " +
     * "AND (LOWER(p.album.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) " +
     * "OR LOWER(p.album.artist.name) LIKE LOWER(CONCAT('%', :searchQuery, '%')))")
     * Page<Product>
     * findByProviderIdAndAlbumTitleContainingIgnoreCaseOrAlbumArtistNameContainingIgnoreCase(
     * 
     * @Param("providerId") Long providerId,
     * 
     * @Param("searchQuery") String searchQuery1,
     * 
     * @Param("searchQuery") String searchQuery2,
     * Pageable pageable);
     * 
     * Page<Product> findByProviderIdAndIsActiveTrue(Long providerId, Pageable
     * pageable);
     * Page<Product> findByProviderId(Long providerId, Pageable pageable);
     */

    /**
     * Actualizar múltiples productos de forma masiva
     */
    @Transactional
    public List<ProductInventoryResponseDTO> bulkUpdateProducts(Long providerId, ProductBulkUpdateDTO bulkUpdateDTO) {
        logger.info("Iniciando actualización masiva de {} productos para proveedor: {}",
                bulkUpdateDTO.getProductIds().size(), providerId);

        // Validar que el proveedor existe
        if (!providerRepository.existsById(providerId)) {
            throw new RuntimeException("Proveedor no encontrado con ID: " + providerId);
        }

        // Obtener productos y validar ownership
        List<Product> productsToUpdate = new ArrayList<>();
        for (Long productId : bulkUpdateDTO.getProductIds()) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

            // Validar que el producto pertenece al proveedor
            if (!product.getProvider().getId().equals(providerId)) {
                throw new RuntimeException("No tienes permisos para actualizar el producto ID: " + productId);
            }

            productsToUpdate.add(product);
        }

        // Aplicar actualizaciones según el tipo
        List<Product> updatedProducts = new ArrayList<>();

        for (Product product : productsToUpdate) {
            switch (bulkUpdateDTO.getUpdateType()) {
                case PRICE_INCREASE_PERCENTAGE:
                    if (bulkUpdateDTO.getValue() != null) {
                        BigDecimal increaseAmount = product.getPrice()
                                .multiply(bulkUpdateDTO.getValue())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                        product.setPrice(product.getPrice().add(increaseAmount));
                    }
                    break;

                case PRICE_DECREASE_PERCENTAGE:
                    if (bulkUpdateDTO.getValue() != null) {
                        BigDecimal decreaseAmount = product.getPrice()
                                .multiply(bulkUpdateDTO.getValue())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                        BigDecimal newPrice = product.getPrice().subtract(decreaseAmount);
                        // Asegurar que el precio no sea negativo
                        product.setPrice(newPrice.max(BigDecimal.ZERO));
                    }
                    break;

                case PRICE_SET_FIXED:
                    if (bulkUpdateDTO.getValue() != null) {
                        product.setPrice(bulkUpdateDTO.getValue());
                    }
                    break;

                case STOCK_SET:
                    if (bulkUpdateDTO.getStockQuantity() != null) {
                        product.setStockQuantity(bulkUpdateDTO.getStockQuantity());
                    }
                    break;

                case STOCK_INCREMENT:
                    if (bulkUpdateDTO.getStockQuantity() != null) {
                        int newStock = product.getStockQuantity() + bulkUpdateDTO.getStockQuantity();
                        product.setStockQuantity(Math.max(0, newStock)); // No permitir stock negativo
                    }
                    break;

                case STOCK_DECREMENT:
                    if (bulkUpdateDTO.getStockQuantity() != null) {
                        int newStock = product.getStockQuantity() - bulkUpdateDTO.getStockQuantity();
                        product.setStockQuantity(Math.max(0, newStock)); // No permitir stock negativo
                    }
                    break;

                case TOGGLE_FEATURED:
                    product.setFeatured(bulkUpdateDTO.getBooleanValue() != null ? bulkUpdateDTO.getBooleanValue()
                            : !product.getFeatured());
                    break;

                case TOGGLE_ACTIVE:
                    product.setIsActive(bulkUpdateDTO.getBooleanValue() != null ? bulkUpdateDTO.getBooleanValue()
                            : !product.getIsActive());
                    break;

                default:
                    throw new RuntimeException("Tipo de actualización no soportado: " + bulkUpdateDTO.getUpdateType());
            }

            product.setUpdatedAt(LocalDateTime.now());
            updatedProducts.add(product);
        }

        // Guardar todos los productos actualizados
        List<Product> savedProducts = productRepository.saveAll(updatedProducts);

        // Log de la operación
        logger.info("Actualización masiva completada - Tipo: {}, Productos: {}, Proveedor: {}, Razón: {}",
                bulkUpdateDTO.getUpdateType(), savedProducts.size(), providerId, bulkUpdateDTO.getReason());

        // Convertir a DTOs de respuesta
        return savedProducts.stream()
                .map(this::mapToInventoryResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Método auxiliar para validar actualizaciones masivas
     */
    private void validateBulkUpdate(ProductBulkUpdateDTO bulkUpdateDTO) {
        // Validar que hay productos para actualizar
        if (bulkUpdateDTO.getProductIds() == null || bulkUpdateDTO.getProductIds().isEmpty()) {
            throw new RuntimeException("Debe seleccionar al menos un producto para actualizar");
        }

        // Validar parámetros según el tipo de actualización
        switch (bulkUpdateDTO.getUpdateType()) {
            case PRICE_INCREASE_PERCENTAGE:
            case PRICE_DECREASE_PERCENTAGE:
                if (bulkUpdateDTO.getValue() == null || bulkUpdateDTO.getValue().compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("El porcentaje debe ser un valor positivo");
                }
                if (bulkUpdateDTO.getValue().compareTo(BigDecimal.valueOf(100)) > 0) {
                    throw new RuntimeException("El porcentaje no puede ser mayor al 100%");
                }
                break;

            case PRICE_SET_FIXED:
                if (bulkUpdateDTO.getValue() == null || bulkUpdateDTO.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RuntimeException("El precio debe ser mayor a cero");
                }
                break;

            case STOCK_SET:
            case STOCK_INCREMENT:
            case STOCK_DECREMENT:
                if (bulkUpdateDTO.getStockQuantity() == null || bulkUpdateDTO.getStockQuantity() < 0) {
                    throw new RuntimeException("La cantidad de stock debe ser un valor positivo");
                }
                break;

            case TOGGLE_FEATURED:
            case TOGGLE_ACTIVE:
                // No se requieren validaciones adicionales para operaciones booleanas
                break;

            default:
                throw new RuntimeException("Tipo de actualización no válido: " + bulkUpdateDTO.getUpdateType());
        }
    }

    /**
     * Endpoint para actualizaciones masivas (agregar al ProductController si no
     * existe)
     */
    // En ProductController.java:
    /*
     * @PatchMapping("/bulk-update")
     * 
     * @PreAuthorize("hasRole('PROVIDER')")
     * 
     * @Operation(summary = "Actualización masiva de productos",
     * description = "Actualizar múltiples productos del catálogo de forma masiva")
     * public ResponseEntity<ApiResponse<List<ProductInventoryResponseDTO>>>
     * bulkUpdateProducts(
     * 
     * @Valid @RequestBody ProductBulkUpdateDTO bulkUpdateDTO,
     * Authentication authentication) {
     * try {
     * // Obtener el proveedor autenticado
     * Long providerId = getProviderIdFromAuthentication(authentication);
     * 
     * // Validar la actualización masiva
     * validateBulkUpdate(bulkUpdateDTO);
     * 
     * // Realizar la actualización
     * List<ProductInventoryResponseDTO> response =
     * productService.bulkUpdateProducts(providerId, bulkUpdateDTO);
     * 
     * String message =
     * String.format("Actualización masiva completada: %d productos actualizados",
     * response.size());
     * 
     * ApiResponse<List<ProductInventoryResponseDTO>> apiResponse = new
     * ApiResponse<>(
     * true,
     * message,
     * response);
     * 
     * return ResponseEntity.ok(apiResponse);
     * 
     * } catch (RuntimeException e) {
     * ApiResponse<List<ProductInventoryResponseDTO>> errorResponse = new
     * ApiResponse<>(
     * false,
     * e.getMessage(),
     * null);
     * return ResponseEntity.badRequest().body(errorResponse);
     * } catch (Exception e) {
     * ApiResponse<List<ProductInventoryResponseDTO>> errorResponse = new
     * ApiResponse<>(
     * false,
     * "Error interno del servidor",
     * null);
     * return
     * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
     * }
     * }
     */
}
