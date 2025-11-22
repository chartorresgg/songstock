package com.songstock.service;

import com.songstock.dto.*;
import com.songstock.entity.*;
import com.songstock.exception.*;
import com.songstock.repository.*;
import com.songstock.mapper.UserManagementMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.songstock.service.OrderService;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión administrativa de usuarios, proveedores
 * y estadísticas relacionadas con la plataforma.
 *
 * Proporciona funcionalidades de:
 * - Administración de usuarios (CRUD, activación/desactivación, filtros).
 * - Gestión de proveedores (verificación, estadísticas, top ranking).
 * - Generación de estadísticas para dashboards administrativos.
 */
@Service
@Transactional
public class AdminUserService {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserManagementMapper userManagementMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ============================================================
    // =============== GESTIÓN DE USUARIOS ====================
    // ============================================================

    /**
     * Obtiene todos los usuarios aplicando filtros, búsqueda por texto,
     * rol, estado y paginación.
     *
     * @param filterDTO Objeto con filtros de búsqueda y paginación.
     * @return Página de {@link UserManagementResponseDTO} con los resultados.
     */
    @Transactional(readOnly = true)
    public Page<UserManagementResponseDTO> getAllUsers(UserSearchFilterDTO filterDTO) {
        logger.info("Obteniendo usuarios con filtros: query={}, role={}, isActive={}",
                filterDTO.getSearchQuery(), filterDTO.getRole(), filterDTO.getIsActive());

        // Filtrar solo usuarios activos por defecto si no se especifica
        if (filterDTO.getIsActive() == null) {
            filterDTO.setIsActive(true);
        }

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(filterDTO.getSortDirection())
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                filterDTO.getSortBy());

        Pageable pageable = PageRequest.of(filterDTO.getPage(), filterDTO.getSize(), sort);

        Page<User> userPage = userRepository.findUsersWithFilters(
                filterDTO.getSearchQuery(),
                filterDTO.getRole(),
                filterDTO.getIsActive(),
                filterDTO.getVerificationStatus(),
                filterDTO.getCreatedAfter(),
                filterDTO.getCreatedBefore(),
                pageable);

        return userPage.map(user -> {
            UserManagementResponseDTO dto = userManagementMapper.toManagementResponseDTO(user);
            enrichUserDTO(dto, user);
            return dto;
        });
    }

    /**
     * Obtiene la información detallada de un usuario por su ID,
     * incluyendo datos del proveedor si aplica.
     *
     * @param userId ID del usuario.
     * @return DTO con información completa del usuario.
     * @throws ResourceNotFoundException Si no existe el usuario.
     */
    @Transactional(readOnly = true)
    public UserManagementResponseDTO getUserById(Long userId) {
        logger.info("Obteniendo usuario por ID: {}", userId);

        Optional<Object[]> result = userRepository.findUserWithProviderInfo(userId);
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + userId);
        }

        Object[] data = result.get();
        User user = (User) data[0];
        Provider provider = (Provider) data[1]; // Puede ser null

        UserManagementResponseDTO dto = userManagementMapper.toManagementResponseDTO(user);

        if (provider != null) {
            dto.setProviderId(provider.getId());
            dto.setBusinessName(provider.getBusinessName());
            dto.setVerificationStatus(provider.getVerificationStatus());
            dto.setVerificationDate(provider.getVerificationDate());
            dto.setTotalProducts(productRepository.countActiveProductsByProvider(provider.getId()).intValue());
        }

        return dto;
    }

    /**
     * Edita los datos de un usuario existente.
     *
     * @param userId  ID del usuario.
     * @param editDTO DTO con nuevos valores.
     * @return Usuario actualizado como {@link UserManagementResponseDTO}.
     * @throws ResourceNotFoundException  Si no se encuentra el usuario.
     * @throws DuplicateResourceException Si el username o email ya están en uso.
     */
    public UserManagementResponseDTO updateUser(Long userId, UserEditDTO editDTO) {
        logger.info("Actualizando usuario ID: {} con datos: {}", userId, editDTO.getUsername());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        // Validaciones de unicidad
        if (!user.getUsername().equals(editDTO.getUsername())
                && !userRepository.isUsernameAvailableForUpdate(editDTO.getUsername(), userId)) {
            throw new DuplicateResourceException("El username ya está en uso: " + editDTO.getUsername());
        }
        if (!user.getEmail().equals(editDTO.getEmail())
                && !userRepository.isEmailAvailableForUpdate(editDTO.getEmail(), userId)) {
            throw new DuplicateResourceException("El email ya está en uso: " + editDTO.getEmail());
        }

        // Actualización de campos
        user.setFirstName(editDTO.getFirstName());
        user.setLastName(editDTO.getLastName());
        user.setUsername(editDTO.getUsername());
        user.setEmail(editDTO.getEmail());
        user.setPhone(editDTO.getPhone());
        user.setRole(editDTO.getRole());
        user.setIsActive(editDTO.getIsActive());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        logger.info("Usuario actualizado exitosamente - ID: {}, Razón: {}", userId, editDTO.getUpdateReason());

        UserManagementResponseDTO dto = userManagementMapper.toManagementResponseDTO(savedUser);
        enrichUserDTO(dto, savedUser);
        return dto;
    }

    /**
     * Activa o desactiva un usuario según su estado actual.
     *
     * @param userId ID del usuario.
     * @param reason Razón de la operación.
     * @return Usuario actualizado como {@link UserManagementResponseDTO}.
     * @throws ResourceNotFoundException Si no se encuentra el usuario.
     */
    public UserManagementResponseDTO toggleUserStatus(Long userId, String reason) {
        logger.info("Cambiando estado del usuario ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        boolean previousStatus = user.getIsActive();
        user.setIsActive(!previousStatus);
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        logger.info("Estado de usuario cambiado - ID: {}, Estado anterior: {}, Nuevo estado: {}, Razón: {}",
                userId, previousStatus, savedUser.getIsActive(), reason);

        UserManagementResponseDTO dto = userManagementMapper.toManagementResponseDTO(savedUser);
        enrichUserDTO(dto, savedUser);
        return dto;
    }

    /**
     * Realiza un "soft delete" de un usuario, es decir, lo desactiva en vez de
     * eliminarlo físicamente.
     *
     * @param userId ID del usuario.
     * @param reason Razón de la eliminación.
     * @throws ResourceNotFoundException Si no se encuentra el usuario.
     * @throws BusinessException         Si el usuario tiene productos asociados.
     */
    public void deleteUser(Long userId, String reason) {
        logger.info("Eliminando usuario ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        logger.info("Usuario eliminado (soft delete) - ID: {}, Razón: {}", userId, reason);
    }

    // ============================================================
    // ============= GESTIÓN DE PROVEEDORES ===================
    // ============================================================

    /**
     * Obtiene la lista de proveedores pendientes de verificación.
     *
     * @return Lista de proveedores en espera de verificación.
     */
    @Transactional(readOnly = true)
    public List<UserManagementResponseDTO> getPendingProviders() {
        logger.info("Obteniendo proveedores pendientes de verificación");

        return userRepository.findPendingProviders().stream()
                .map(user -> {
                    UserManagementResponseDTO dto = userManagementMapper.toManagementResponseDTO(user);
                    enrichUserDTO(dto, user);
                    return dto;
                })
                .toList();
    }

    /**
     * Actualiza el estado de verificación de un proveedor.
     *
     * @param userId        ID del usuario proveedor.
     * @param managementDTO DTO con la información de verificación.
     * @return Usuario proveedor actualizado.
     * @throws ResourceNotFoundException Si el usuario o proveedor no existen.
     * @throws BusinessException         Si el usuario no tiene rol de proveedor.
     */
    public UserManagementResponseDTO updateProviderVerification(Long userId, ProviderManagementDTO managementDTO) {
        logger.info("Actualizando verificación del proveedor - Usuario ID: {}, Estado: {}",
                userId, managementDTO.getVerificationStatus());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        if (user.getRole() != UserRole.PROVIDER) {
            throw new BusinessException("El usuario no es un proveedor");
        }

        Provider provider = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Información de proveedor no encontrada"));

        VerificationStatus previousStatus = provider.getVerificationStatus();
        provider.setVerificationStatus(managementDTO.getVerificationStatus());

        if (managementDTO.getVerificationStatus() == VerificationStatus.VERIFIED
                || managementDTO.getVerificationStatus() == VerificationStatus.REJECTED) {
            provider.setVerificationDate(LocalDateTime.now());
        }

        if (managementDTO.getBusinessName() != null) {
            provider.setBusinessName(managementDTO.getBusinessName());
        }
        if (managementDTO.getCommissionRate() != null) {
            provider.setCommissionRate(managementDTO.getCommissionRate());
        }

        provider.setUpdatedAt(LocalDateTime.now());
        providerRepository.save(provider);

        logger.info("Verificación actualizada - ID: {}, Estado anterior: {}, Nuevo estado: {}, Razón: {}",
                userId, previousStatus, managementDTO.getVerificationStatus(), managementDTO.getChangeReason());

        UserManagementResponseDTO dto = userManagementMapper.toManagementResponseDTO(user);
        enrichUserDTO(dto, user);
        return dto;
    }

    // ============================================================
    // ============ ESTADÍSTICAS Y DASHBOARD ==================
    // ============================================================

    /**
     * Genera las estadísticas principales para el dashboard administrativo.
     *
     * @return DTO con métricas de usuarios, proveedores y productos.
     */
    @Transactional(readOnly = true)
    public AdminDashboardDTO getDashboardStatistics() {
        logger.info("Generando estadísticas del dashboard administrativo");

        AdminDashboardDTO dashboard = new AdminDashboardDTO();

        // Usuarios
        Object[] userStats = userRepository.getUserStatistics();
        if (userStats != null && userStats.length > 0) {
            dashboard.setTotalUsers((Long) userStats[0]);
            dashboard.setTotalAdmins((Long) userStats[1]);
            dashboard.setTotalProviders((Long) userStats[2]);
            dashboard.setTotalCustomers((Long) userStats[3]);
            dashboard.setActiveUsers((Long) userStats[4]);
            dashboard.setInactiveUsers((Long) userStats[5]);
        }

        // Proveedores
        Object[] providerStats = userRepository.getProviderStatistics();
        if (providerStats != null && providerStats.length > 0) {
            dashboard.setVerifiedProviders((Long) providerStats[0]);
            dashboard.setPendingProviders((Long) providerStats[1]);
            dashboard.setRejectedProviders((Long) providerStats[2]);
        }

        // Productos
        dashboard.setTotalProducts(productRepository.count());
        dashboard.setActiveProducts((long) productRepository.findByIsActiveTrue().size());
        dashboard.setDigitalProducts(productRepository.countByProductType(ProductType.DIGITAL));
        dashboard.setPhysicalProducts(productRepository.countByProductType(ProductType.PHYSICAL));

        // Estadísticas mensuales
        LocalDateTime startOfMonth = YearMonth.now().atDay(1).atStartOfDay();
        dashboard.setUsersRegisteredThisMonth(userRepository.countUsersRegisteredThisMonth(startOfMonth));
        dashboard.setProvidersVerifiedThisMonth(userRepository.countProvidersVerifiedThisMonth(startOfMonth));

        logger.info("Estadísticas generadas - Total usuarios: {}, Proveedores verificados: {}",
                dashboard.getTotalUsers(), dashboard.getVerifiedProviders());

        return dashboard;
    }

    // ========== MODERACIÓN DE VALORACIONES ==========

    public List<OrderReviewDTO> getPendingReviews() {
        return orderService.getPendingReviews();
    }

    public OrderReviewDTO approveReview(Long reviewId, Long adminUserId) {
        return orderService.approveReview(reviewId, adminUserId);
    }

    public OrderReviewDTO rejectReview(Long reviewId, Long adminUserId) {
        return orderService.rejectReview(reviewId, adminUserId);
    }

    /**
     * Busca usuarios por texto libre en username, email o nombres.
     *
     * @param query Texto de búsqueda.
     * @return Lista de usuarios coincidentes.
     */
    @Transactional(readOnly = true)
    public List<UserManagementResponseDTO> searchUsers(String query) {
        logger.info("Buscando usuarios con query: {}", query);

        return userRepository.searchUsers(query).stream()
                .map(user -> {
                    UserManagementResponseDTO dto = userManagementMapper.toManagementResponseDTO(user);
                    enrichUserDTO(dto, user);
                    return dto;
                })
                .toList();
    }

    /**
     * Obtiene los usuarios registrados recientemente dentro de un rango de días.
     *
     * @param days Número de días atrás desde la fecha actual.
     * @return Lista de usuarios recientes.
     */
    @Transactional(readOnly = true)
    public List<UserManagementResponseDTO> getRecentUsers(int days) {
        logger.info("Obteniendo usuarios registrados en los últimos {} días", days);

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return userRepository.findRecentUsers(cutoffDate).stream()
                .map(user -> {
                    UserManagementResponseDTO dto = userManagementMapper.toManagementResponseDTO(user);
                    enrichUserDTO(dto, user);
                    return dto;
                })
                .toList();
    }

    /**
     * Obtiene el ranking de proveedores con más productos.
     *
     * @param limit Número máximo de resultados.
     * @return Lista de proveedores top con su número de productos.
     */
    @Transactional(readOnly = true)
    public List<UserManagementResponseDTO> getTopProviders(int limit) {
        logger.info("Obteniendo top {} proveedores por número de productos", limit);

        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = userRepository.getTopProvidersByProductCount(pageable);

        return results.stream()
                .map(result -> {
                    User user = (User) result[0];
                    Long productCount = (Long) result[1];

                    UserManagementResponseDTO dto = userManagementMapper.toManagementResponseDTO(user);
                    enrichUserDTO(dto, user);
                    dto.setTotalProducts(productCount.intValue());
                    return dto;
                })
                .toList();
    }

    // ============================================================
    // =============== MÉTODOS AUXILIARES =====================
    // ============================================================

    /**
     * Enriquecer DTO de usuario con información de proveedor
     * o estadísticas adicionales según el rol.
     *
     * @param dto  DTO a enriquecer.
     * @param user Entidad usuario base.
     */
    private void enrichUserDTO(UserManagementResponseDTO dto, User user) {
        if (user.getRole() == UserRole.PROVIDER) {
            providerRepository.findByUserId(user.getId()).ifPresent(provider -> {
                dto.setProviderId(provider.getId());
                dto.setBusinessName(provider.getBusinessName());
                dto.setVerificationStatus(provider.getVerificationStatus());
                dto.setVerificationDate(provider.getVerificationDate());
                dto.setTotalProducts(productRepository.countActiveProductsByProvider(provider.getId()).intValue());
            });
        }

        // Futuro: estadísticas de clientes (pedidos, etc.)
        if (user.getRole() == UserRole.CUSTOMER) {
            // dto.setTotalOrders(orderRepository.countByCustomerId(user.getId()));
        }
    }

    /**
     * Valida permisos y reglas de seguridad para operaciones administrativas.
     *
     * @param operation Tipo de operación (ej: deactivate).
     * @param userId    ID del usuario afectado.
     * @throws BusinessException Si se intenta desactivar al último admin activo.
     */
    private void validateAdminOperation(String operation, Long userId) {
        logger.debug("Validando operación administrativa: {} para usuario: {}", operation, userId);

        if ("deactivate".equals(operation)) {
            Long activeAdmins = userRepository.countByRole(UserRole.ADMIN);
            if (activeAdmins <= 1) {
                throw new BusinessException("No se puede desactivar el último administrador activo");
            }
        }
    }
}
