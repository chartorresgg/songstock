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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

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
    private PasswordEncoder passwordEncoder;

    // ========== GESTIÓN DE USUARIOS ==========

    /**
     * Obtener todos los usuarios con filtros y paginación
     */
    @Transactional(readOnly = true)
    public Page<UserManagementResponseDTO> getAllUsers(UserSearchFilterDTO filterDTO) {
        logger.info("Obteniendo usuarios con filtros: query={}, role={}, isActive={}",
                filterDTO.getSearchQuery(), filterDTO.getRole(), filterDTO.getIsActive());

        // Crear Pageable con ordenamiento
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(filterDTO.getSortDirection())
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                filterDTO.getSortBy());

        Pageable pageable = PageRequest.of(
                filterDTO.getPage(),
                filterDTO.getSize(),
                sort);

        // Buscar usuarios con filtros
        Page<User> userPage = userRepository.findUsersWithFilters(
                filterDTO.getSearchQuery(),
                filterDTO.getRole(),
                filterDTO.getIsActive(),
                filterDTO.getVerificationStatus(),
                filterDTO.getCreatedAfter(),
                filterDTO.getCreatedBefore(),
                pageable);

        // Convertir a DTO con información adicional
        return userPage.map(user -> {
            UserManagementResponseDTO dto = userManagementMapper.toManagementResponseDTO(user);
            enrichUserDTO(dto, user);
            return dto;
        });
    }

    /**
     * Obtener usuario específico por ID con información completa
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
        Provider provider = (Provider) data[1]; // Puede ser null si no es proveedor

        UserManagementResponseDTO dto = userManagementMapper.toManagementResponseDTO(user);

        if (provider != null) {
            dto.setProviderId(provider.getId());
            dto.setBusinessName(provider.getBusinessName());
            dto.setVerificationStatus(provider.getVerificationStatus());
            dto.setVerificationDate(provider.getVerificationDate());

            // Obtener estadísticas de productos
            Long productCount = productRepository.countActiveProductsByProvider(provider.getId());
            dto.setTotalProducts(productCount.intValue());
        }

        return dto;
    }

    /**
     * Editar usuario existente
     */
    public UserManagementResponseDTO updateUser(Long userId, UserEditDTO editDTO) {
        logger.info("Actualizando usuario ID: {} con datos: {}", userId, editDTO.getUsername());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        // Validar username único (si cambió)
        if (!user.getUsername().equals(editDTO.getUsername())) {
            if (!userRepository.isUsernameAvailableForUpdate(editDTO.getUsername(), userId)) {
                throw new DuplicateResourceException("El username ya está en uso: " + editDTO.getUsername());
            }
        }

        // Validar email único (si cambió)
        if (!user.getEmail().equals(editDTO.getEmail())) {
            if (!userRepository.isEmailAvailableForUpdate(editDTO.getEmail(), userId)) {
                throw new DuplicateResourceException("El email ya está en uso: " + editDTO.getEmail());
            }
        }

        // Actualizar campos
        user.setFirstName(editDTO.getFirstName());
        user.setLastName(editDTO.getLastName());
        user.setUsername(editDTO.getUsername());
        user.setEmail(editDTO.getEmail());
        user.setPhone(editDTO.getPhone());
        user.setRole(editDTO.getRole());
        user.setIsActive(editDTO.getIsActive());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        logger.info("Usuario actualizado exitosamente - ID: {}, Razón: {}",
                userId, editDTO.getUpdateReason());

        UserManagementResponseDTO dto = userManagementMapper.toManagementResponseDTO(savedUser);
        enrichUserDTO(dto, savedUser);
        return dto;
    }

    /**
     * Activar/Desactivar usuario
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
     * Eliminar usuario (soft delete)
     */
    public void deleteUser(Long userId, String reason) {
        logger.info("Eliminando usuario ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        // Verificar si el usuario puede ser eliminado
        if (!userRepository.canUserBeDeleted(userId)) {
            throw new BusinessException("El usuario no puede ser eliminado porque tiene productos asociados");
        }

        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        logger.info("Usuario eliminado (soft delete) - ID: {}, Razón: {}", userId, reason);
    }

    // ========== GESTIÓN ESPECÍFICA DE PROVEEDORES ==========

    /**
     * Obtener proveedores pendientes de verificación
     */
    @Transactional(readOnly = true)
    public List<UserManagementResponseDTO> getPendingProviders() {
        logger.info("Obteniendo proveedores pendientes de verificación");

        List<User> pendingUsers = userRepository.findPendingProviders();

        return pendingUsers.stream()
                .map(user -> {
                    UserManagementResponseDTO dto = userManagementMapper.toManagementResponseDTO(user);
                    enrichUserDTO(dto, user);
                    return dto;
                })
                .toList();
    }

    /**
     * Actualizar estado de verificación de proveedor
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

        // Actualizar estado de verificación
        VerificationStatus previousStatus = provider.getVerificationStatus();
        provider.setVerificationStatus(managementDTO.getVerificationStatus());

        if (managementDTO.getVerificationStatus() == VerificationStatus.VERIFIED ||
                managementDTO.getVerificationStatus() == VerificationStatus.REJECTED) {
            provider.setVerificationDate(LocalDateTime.now());
        }

        // Actualizar información adicional si se proporciona
        if (managementDTO.getBusinessName() != null) {
            provider.setBusinessName(managementDTO.getBusinessName());
        }
        if (managementDTO.getCommissionRate() != null) {
            provider.setCommissionRate(managementDTO.getCommissionRate());
        }

        provider.setUpdatedAt(LocalDateTime.now());
        providerRepository.save(provider);

        logger.info("Verificación de proveedor actualizada - ID: {}, Estado anterior: {}, Nuevo estado: {}, Razón: {}",
                userId, previousStatus, managementDTO.getVerificationStatus(), managementDTO.getChangeReason());

        UserManagementResponseDTO dto = userManagementMapper.toManagementResponseDTO(user);
        enrichUserDTO(dto, user);
        return dto;
    }

    // ========== ESTADÍSTICAS Y DASHBOARD ==========

    /**
     * Obtener estadísticas para dashboard administrativo
     */
    @Transactional(readOnly = true)
    public AdminDashboardDTO getDashboardStatistics() {
        logger.info("Generando estadísticas del dashboard administrativo");

        AdminDashboardDTO dashboard = new AdminDashboardDTO();

        // Estadísticas de usuarios
        Object[] userStats = userRepository.getUserStatistics();
        if (userStats != null && userStats.length > 0) {
            dashboard.setTotalUsers((Long) userStats[0]);
            dashboard.setTotalAdmins((Long) userStats[1]);
            dashboard.setTotalProviders((Long) userStats[2]);
            dashboard.setTotalCustomers((Long) userStats[3]);
            dashboard.setActiveUsers((Long) userStats[4]);
            dashboard.setInactiveUsers((Long) userStats[5]);
        }

        // Estadísticas de proveedores
        Object[] providerStats = userRepository.getProviderStatistics();
        if (providerStats != null && providerStats.length > 0) {
            dashboard.setVerifiedProviders((Long) providerStats[0]);
            dashboard.setPendingProviders((Long) providerStats[1]);
            dashboard.setRejectedProviders((Long) providerStats[2]);
        }

        // Estadísticas de productos
        dashboard.setTotalProducts(productRepository.count());
        dashboard.setActiveProducts((long) productRepository.findByIsActiveTrue().size());
        dashboard.setDigitalProducts(productRepository.countByProductType(ProductType.DIGITAL));
        dashboard.setPhysicalProducts(productRepository.countByProductType(ProductType.PHYSICAL));

        // Estadísticas del mes actual
        LocalDateTime startOfMonth = YearMonth.now().atDay(1).atStartOfDay();
        dashboard.setUsersRegisteredThisMonth(userRepository.countUsersRegisteredThisMonth(startOfMonth));
        dashboard.setProvidersVerifiedThisMonth(userRepository.countProvidersVerifiedThisMonth(startOfMonth));

        // Obtener productos creados este mes (assumiendo que existe el método)
        // dashboard.setProductsCreatedThisMonth(productRepository.countProductsCreatedThisMonth(startOfMonth));

        logger.info("Estadísticas generadas - Total usuarios: {}, Proveedores verificados: {}",
                dashboard.getTotalUsers(), dashboard.getVerifiedProviders());

        return dashboard;
    }

    /**
     * Buscar usuarios con texto libre
     */
    @Transactional(readOnly = true)
    public List<UserManagementResponseDTO> searchUsers(String query) {
        logger.info("Buscando usuarios con query: {}", query);

        List<User> users = userRepository.searchUsers(query);

        return users.stream()
                .map(user -> {
                    UserManagementResponseDTO dto = userManagementMapper.toManagementResponseDTO(user);
                    enrichUserDTO(dto, user);
                    return dto;
                })
                .toList();
    }

    /**
     * Obtener usuarios registrados recientemente
     */
    @Transactional(readOnly = true)
    public List<UserManagementResponseDTO> getRecentUsers(int days) {
        logger.info("Obteniendo usuarios registrados en los últimos {} días", days);

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        List<User> users = userRepository.findRecentUsers(cutoffDate);

        return users.stream()
                .map(user -> {
                    UserManagementResponseDTO dto = userManagementMapper.toManagementResponseDTO(user);
                    enrichUserDTO(dto, user);
                    return dto;
                })
                .toList();
    }

    /**
     * Obtener top proveedores por número de productos
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

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Enriquecer DTO con información adicional
     */
    private void enrichUserDTO(UserManagementResponseDTO dto, User user) {
        if (user.getRole() == UserRole.PROVIDER) {
            Optional<Provider> providerOpt = providerRepository.findByUserId(user.getId());
            if (providerOpt.isPresent()) {
                Provider provider = providerOpt.get();
                dto.setProviderId(provider.getId());
                dto.setBusinessName(provider.getBusinessName());
                dto.setVerificationStatus(provider.getVerificationStatus());
                dto.setVerificationDate(provider.getVerificationDate());

                // Contar productos del proveedor
                Long productCount = productRepository.countActiveProductsByProvider(provider.getId());
                dto.setTotalProducts(productCount.intValue());
            }
        }

        // Para compradores, podrían agregarse estadísticas de pedidos en el futuro
        if (user.getRole() == UserRole.CUSTOMER) {
            // dto.setTotalOrders(orderRepository.countByCustomerId(user.getId()));
        }
    }

    /**
     * Validar permisos para operación administrativa
     */
    private void validateAdminOperation(String operation, Long userId) {
        logger.debug("Validando operación administrativa: {} para usuario: {}", operation, userId);

        // Aquí podrían agregarse validaciones específicas
        // Por ejemplo, evitar que un admin se desactive a sí mismo

        if ("deactivate".equals(operation)) {
            // Verificar que no sea el último admin activo
            Long activeAdmins = userRepository.countByRole(UserRole.ADMIN);
            if (activeAdmins <= 1) {
                throw new BusinessException("No se puede desactivar el último administrador activo");
            }
        }
    }
}