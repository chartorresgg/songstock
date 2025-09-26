package com.songstock.repository;

import com.songstock.entity.User;
import com.songstock.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.songstock.entity.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

/**
 * Repositorio para la entidad {@link User}.
 * 
 * Extiende {@link JpaRepository} para proporcionar operaciones CRUD básicas
 * y define consultas personalizadas relacionadas con usuarios, roles,
 * estados de verificación y estadísticas administrativas.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

        /**
         * Buscar usuario por username.
         */
        Optional<User> findByUsername(String username);

        /**
         * Buscar usuario por email.
         */
        Optional<User> findByEmail(String email);

        /**
         * Buscar usuario por username o email.
         */
        Optional<User> findByUsernameOrEmail(String username, String email);

        /**
         * Verificar si existe un usuario con un username específico.
         */
        boolean existsByUsername(String username);

        /**
         * Verificar si existe un usuario con un email específico.
         */
        boolean existsByEmail(String email);

        /**
         * Buscar usuarios por rol.
         */
        List<User> findByRole(UserRole role);

        /**
         * Buscar usuarios por estado activo/inactivo.
         */
        List<User> findByIsActive(Boolean isActive);

        /**
         * Buscar usuarios por rol y estado activo.
         */
        @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = :isActive")
        List<User> findByRoleAndIsActive(@Param("role") UserRole role, @Param("isActive") Boolean isActive);

        /**
         * Buscar usuarios por coincidencia parcial en nombre o apellido.
         */
        @Query("SELECT u FROM User u WHERE u.firstName LIKE %:name% OR u.lastName LIKE %:name%")
        List<User> findByNameContaining(@Param("name") String name);

        /**
         * Buscar usuarios con filtros múltiples para administración:
         * - Consulta de texto (nombre, apellido, username, email, proveedor)
         * - Rol
         * - Estado activo
         * - Estado de verificación de proveedor
         * - Fechas de creación
         */
        @Query("SELECT DISTINCT u FROM User u " +
                        "LEFT JOIN Provider p ON u.id = p.user.id " +
                        "WHERE (:searchQuery IS NULL OR :searchQuery = '' OR " +
                        "       LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
                        "       LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
                        "       LOWER(u.username) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
                        "       LOWER(u.email) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
                        "       LOWER(p.businessName) LIKE LOWER(CONCAT('%', :searchQuery, '%'))) " +
                        "AND (:role IS NULL OR u.role = :role) " +
                        "AND (:isActive IS NULL OR u.isActive = :isActive) " +
                        "AND (:verificationStatus IS NULL OR p.verificationStatus = :verificationStatus) " +
                        "AND (:createdAfter IS NULL OR u.createdAt >= :createdAfter) " +
                        "AND (:createdBefore IS NULL OR u.createdAt <= :createdBefore)")
        Page<User> findUsersWithFilters(@Param("searchQuery") String searchQuery,
                        @Param("role") UserRole role,
                        @Param("isActive") Boolean isActive,
                        @Param("verificationStatus") VerificationStatus verificationStatus,
                        @Param("createdAfter") LocalDateTime createdAfter,
                        @Param("createdBefore") LocalDateTime createdBefore,
                        Pageable pageable);

        /**
         * Obtener todos los usuarios con información de proveedor asociada (si aplica).
         */
        @Query("SELECT u, p FROM User u " +
                        "LEFT JOIN Provider p ON u.id = p.user.id " +
                        "WHERE u.isActive = true " +
                        "ORDER BY u.createdAt DESC")
        List<Object[]> findAllUsersWithProviderInfo();

        /**
         * Buscar usuarios activos por rol.
         */
        List<User> findByRoleAndIsActiveTrue(UserRole role);

        /**
         * Buscar usuarios por rol con paginación.
         */
        Page<User> findByRole(UserRole role, Pageable pageable);

        /**
         * Buscar usuarios por estado activo/inactivo con paginación.
         */
        Page<User> findByIsActive(Boolean isActive, Pageable pageable);

        /**
         * Buscar usuarios creados en un rango de fechas.
         */
        @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate ORDER BY u.createdAt DESC")
        List<User> findUsersByDateRange(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        /**
         * Buscar proveedores por estado de verificación.
         */
        @Query("SELECT u FROM User u " +
                        "JOIN Provider p ON u.id = p.user.id " +
                        "WHERE p.verificationStatus = :verificationStatus " +
                        "ORDER BY p.createdAt DESC")
        List<User> findProvidersByVerificationStatus(
                        @Param("verificationStatus") VerificationStatus verificationStatus);

        /**
         * Buscar proveedores pendientes de verificación.
         */
        @Query("SELECT u FROM User u " +
                        "JOIN Provider p ON u.id = p.user.id " +
                        "WHERE p.verificationStatus = 'PENDING' " +
                        "ORDER BY p.createdAt ASC")
        List<User> findPendingProviders();

        // ================== QUERIES DE ESTADÍSTICAS ==================

        /**
         * Contar usuarios por rol.
         */
        @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
        Long countByRole(@Param("role") UserRole role);

        /**
         * Contar usuarios activos/inactivos.
         */
        Long countByIsActive(Boolean isActive);

        /**
         * Contar usuarios registrados en el mes actual.
         */
        @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startOfMonth")
        Long countUsersRegisteredThisMonth(@Param("startOfMonth") LocalDateTime startOfMonth);

        /**
         * Contar proveedores por estado de verificación.
         */
        @Query("SELECT COUNT(p) FROM Provider p WHERE p.verificationStatus = :status")
        Long countProvidersByVerificationStatus(@Param("status") VerificationStatus status);

        /**
         * Contar proveedores verificados este mes.
         */
        @Query("SELECT COUNT(p) FROM Provider p " +
                        "WHERE p.verificationStatus = 'VERIFIED' " +
                        "AND p.verificationDate >= :startOfMonth")
        Long countProvidersVerifiedThisMonth(@Param("startOfMonth") LocalDateTime startOfMonth);

        /**
         * Obtener estadísticas agregadas de usuarios.
         */
        @Query("SELECT " +
                        "COUNT(u) as totalUsers, " +
                        "SUM(CASE WHEN u.role = 'ADMIN' THEN 1 ELSE 0 END) as totalAdmins, " +
                        "SUM(CASE WHEN u.role = 'PROVIDER' THEN 1 ELSE 0 END) as totalProviders, " +
                        "SUM(CASE WHEN u.role = 'CUSTOMER' THEN 1 ELSE 0 END) as totalCustomers, " +
                        "SUM(CASE WHEN u.isActive = true THEN 1 ELSE 0 END) as activeUsers, " +
                        "SUM(CASE WHEN u.isActive = false THEN 1 ELSE 0 END) as inactiveUsers " +
                        "FROM User u")
        Object[] getUserStatistics();

        /**
         * Obtener estadísticas agregadas de proveedores.
         */
        @Query("SELECT " +
                        "SUM(CASE WHEN p.verificationStatus = 'VERIFIED' THEN 1 ELSE 0 END) as verifiedProviders, " +
                        "SUM(CASE WHEN p.verificationStatus = 'PENDING' THEN 1 ELSE 0 END) as pendingProviders, " +
                        "SUM(CASE WHEN p.verificationStatus = 'REJECTED' THEN 1 ELSE 0 END) as rejectedProviders " +
                        "FROM Provider p")
        Object[] getProviderStatistics();

        // ================== QUERIES DE INFORMACIÓN DETALLADA ==================

        /**
         * Obtener usuario con información completa de proveedor asociada.
         */
        @Query("SELECT u, p FROM User u " +
                        "LEFT JOIN Provider p ON u.id = p.user.id " +
                        "WHERE u.id = :userId")
        Optional<Object[]> findUserWithProviderInfo(@Param("userId") Long userId);

        /**
         * Buscar proveedores con un número mínimo de productos.
         */
        @Query("SELECT u FROM User u " +
                        "JOIN Provider p ON u.id = p.user.id " +
                        "JOIN Product prod ON p.id = prod.provider.id " +
                        "GROUP BY u.id " +
                        "HAVING COUNT(prod.id) >= :minProducts")
        List<User> findProvidersWithMinProducts(@Param("minProducts") Long minProducts);

        /**
         * Buscar usuarios inactivos desde una fecha de corte.
         */
        @Query("SELECT u FROM User u " +
                        "WHERE u.isActive = true " +
                        "AND u.updatedAt < :cutoffDate")
        List<User> findInactiveUsersSince(@Param("cutoffDate") LocalDateTime cutoffDate);

        /**
         * Buscar usuarios por coincidencia en nombre completo.
         */
        @Query("SELECT u FROM User u " +
                        "WHERE LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :fullName, '%'))")
        List<User> findByFullNameContaining(@Param("fullName") String fullName);

        // ================== QUERIES DE AUDITORÍA ==================

        /**
         * Obtener usuarios modificados recientemente.
         */
        @Query("SELECT u FROM User u " +
                        "WHERE u.updatedAt > u.createdAt " +
                        "AND u.updatedAt >= :since " +
                        "ORDER BY u.updatedAt DESC")
        List<User> findRecentlyModifiedUsers(@Param("since") LocalDateTime since);

        /**
         * Buscar usuarios por dominio de correo electrónico.
         */
        @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%@', :domain))")
        List<User> findUsersByEmailDomain(@Param("domain") String domain);

        // ================== QUERIES DE VALIDACIÓN ==================

        /**
         * Verificar si un usuario puede ser eliminado (sin dependencias críticas).
         */
        @Query("SELECT CASE WHEN COUNT(p) > 0 THEN false ELSE true END " +
                        "FROM Product p " +
                        "JOIN Provider pr ON p.provider.id = pr.id " +
                        "WHERE pr.user.id = :userId")
        Boolean canUserBeDeleted(@Param("userId") Long userId);

        /**
         * Verificar disponibilidad de username al actualizar (excluyendo a un usuario
         * específico).
         */
        @Query("SELECT CASE WHEN COUNT(u) > 0 THEN false ELSE true END " +
                        "FROM User u " +
                        "WHERE LOWER(u.username) = LOWER(:username) " +
                        "AND u.id != :excludeUserId")
        Boolean isUsernameAvailableForUpdate(@Param("username") String username,
                        @Param("excludeUserId") Long excludeUserId);

        /**
         * Verificar disponibilidad de email al actualizar (excluyendo a un usuario
         * específico).
         */
        @Query("SELECT CASE WHEN COUNT(u) > 0 THEN false ELSE true END " +
                        "FROM User u " +
                        "WHERE LOWER(u.email) = LOWER(:email) " +
                        "AND u.id != :excludeUserId")
        Boolean isEmailAvailableForUpdate(@Param("email") String email,
                        @Param("excludeUserId") Long excludeUserId);

        // ================== QUERIES PARA REPORTES ==================

        /**
         * Obtener crecimiento de usuarios por mes desde una fecha.
         */
        @Query("SELECT YEAR(u.createdAt), MONTH(u.createdAt), COUNT(u) " +
                        "FROM User u " +
                        "WHERE u.createdAt >= :since " +
                        "GROUP BY YEAR(u.createdAt), MONTH(u.createdAt) " +
                        "ORDER BY YEAR(u.createdAt), MONTH(u.createdAt)")
        List<Object[]> getUserGrowthByMonth(@Param("since") LocalDateTime since);

        /**
         * Obtener top de proveedores por número de productos activos.
         */
        @Query("SELECT u, COUNT(p) as productCount " +
                        "FROM User u " +
                        "JOIN Provider pr ON u.id = pr.user.id " +
                        "JOIN Product p ON pr.id = p.provider.id " +
                        "WHERE p.isActive = true " +
                        "GROUP BY u.id " +
                        "ORDER BY productCount DESC")
        List<Object[]> getTopProvidersByProductCount(Pageable pageable);

        /**
         * Buscar usuarios registrados en los últimos N días.
         */
        @Query("SELECT u FROM User u " +
                        "WHERE u.createdAt >= :cutoffDate " +
                        "ORDER BY u.createdAt DESC")
        List<User> findRecentUsers(@Param("cutoffDate") LocalDateTime cutoffDate);

        // ================== MÉTODOS DE BÚSQUEDA AVANZADA ==================

        /**
         * Búsqueda de texto completo en usuarios y proveedores.
         */
        @Query("SELECT DISTINCT u FROM User u " +
                        "LEFT JOIN Provider p ON u.id = p.user.id " +
                        "WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "      LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "      LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "      LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "      LOWER(u.phone) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "      LOWER(p.businessName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "      LOWER(p.taxId) LIKE LOWER(CONCAT('%', :query, '%'))")
        List<User> searchUsers(@Param("query") String query);

        /**
         * Búsqueda de usuarios con información agregada (incluyendo cantidad de
         * productos de proveedor).
         */
        @Query("SELECT u, p, " +
                        "CASE WHEN p.id IS NOT NULL THEN " +
                        "  (SELECT COUNT(prod) FROM Product prod WHERE prod.provider.id = p.id) " +
                        "ELSE 0 END as productCount " +
                        "FROM User u " +
                        "LEFT JOIN Provider p ON u.id = p.user.id " +
                        "WHERE u.isActive = :isActive " +
                        "ORDER BY u.createdAt DESC")
        List<Object[]> findUsersWithAggregatedInfo(@Param("isActive") Boolean isActive);
}
