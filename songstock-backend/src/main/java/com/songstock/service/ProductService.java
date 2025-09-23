package com.songstock.service;

import com.songstock.dto.ProductDTO;
import com.songstock.entity.*;
import com.songstock.exception.ResourceNotFoundException;
import com.songstock.exception.DuplicateResourceException;
import com.songstock.exception.BusinessException;
import com.songstock.mapper.ProductMapper;
import com.songstock.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado con ID: " + productDTO.getAlbumId()));
        
        Provider provider = providerRepository.findById(productDTO.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + productDTO.getProviderId()));
        
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + productDTO.getCategoryId()));
        
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
                    .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado con ID: " + productDTO.getAlbumId()));
            existingProduct.setAlbum(newAlbum);
        }
        
        if (!existingProduct.getProvider().getId().equals(productDTO.getProviderId())) {
            Provider newProvider = providerRepository.findById(productDTO.getProviderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + productDTO.getProviderId()));
            existingProduct.setProvider(newProvider);
        }
        
        if (!existingProduct.getCategory().getId().equals(productDTO.getCategoryId())) {
            Category newCategory = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + productDTO.getCategoryId()));
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
     * Obtener formatos alternativos de un producto (si es digital, mostrar vinilo y viceversa)
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getAlternativeFormats(Long productId) {
        logger.info("Obteniendo formatos alternativos para producto ID: {}", productId);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productId));
        
        List<Product> alternativeProducts = productRepository.findAlternativeFormats(
                product.getAlbum().getId(), 
                product.getProductType()
        );
        
        List<ProductDTO> alternatives = productMapper.toDTOList(alternativeProducts);
        logger.info("Encontrados {} formatos alternativos", alternatives.size());
        
        return alternatives;
    }
    
    /**
     * Verificar si un producto tiene versión alternativa (vinilo para digital o viceversa)
     */
    @Transactional(readOnly = true)
    public boolean hasAlternativeFormat(Long productId) {
        logger.info("Verificando si producto ID: {} tiene formato alternativo", productId);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productId));
        
        List<Product> alternatives = productRepository.findAlternativeFormats(
                product.getAlbum().getId(), 
                product.getProductType()
        );
        
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
        public Long getTotalProducts() { return totalProducts; }
        public void setTotalProducts(Long totalProducts) { this.totalProducts = totalProducts; }
        
        public Long getDigitalProducts() { return digitalProducts; }
        public void setDigitalProducts(Long digitalProducts) { this.digitalProducts = digitalProducts; }
        
        public Long getPhysicalProducts() { return physicalProducts; }
        public void setPhysicalProducts(Long physicalProducts) { this.physicalProducts = physicalProducts; }
        
        public Long getInStockProducts() { return inStockProducts; }
        public void setInStockProducts(Long inStockProducts) { this.inStockProducts = inStockProducts; }
        
        public Long getOutOfStockProducts() { return outOfStockProducts; }
        public void setOutOfStockProducts(Long outOfStockProducts) { this.outOfStockProducts = outOfStockProducts; }
    }
}
        