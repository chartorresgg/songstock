package com.songstock.repository;

import com.songstock.entity.Provider;
import com.songstock.entity.User;
import com.songstock.entity.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad {@link Provider}.
 * 
 * Extiende de {@link JpaRepository} para heredar operaciones CRUD básicas
 * y define consultas específicas para gestionar proveedores.
 */
@Repository // Indica que esta interfaz es un repositorio de Spring Data JPA.
public interface ProviderRepository extends JpaRepository<Provider, Long> {

        /**
         * Busca un proveedor asociado a un usuario específico.
         *
         * @param userId ID del usuario asociado.
         * @return Un {@link Optional} con el proveedor si existe.
         */
        Optional<Provider> findByUserId(Long userId);

        /**
         * Buscar proveedor por usuario
         */
        Optional<Provider> findByUser(User user);

        /**
         * Verificar si existe un Tax ID
         */
        boolean existsByTaxId(String taxId);

        /**
         * Buscar proveedor por Tax ID (NIT)
         */
        Optional<Provider> findByTaxId(String taxId);

        /**
         * Verificar si un usuario tiene un proveedor asociado
         */
        boolean existsByUserId(Long userId);

        /**
         * Contadores por estado de verificación
         */
        Long countByVerificationStatus(VerificationStatus verificationStatus);

        /**
         * Contadores por estado de verificación usando String (para compatibilidad)
         */
        @Query("SELECT COUNT(p) FROM Provider p WHERE p.verificationStatus = :status")
        Long countByVerificationStatus(@Param("status") String status);

        /**
         * Encontrar proveedores por estado con límite
         */
        @Query("SELECT p FROM Provider p WHERE p.verificationStatus = :status ORDER BY p.createdAt DESC")
        List<Provider> findByVerificationStatusWithLimit(@Param("status") String status);

        /**
         * Buscar proveedores por estado de verificación
         */
        @Query("SELECT p FROM Provider p WHERE p.verificationStatus = :status")
        java.util.List<Provider> findByVerificationStatus(@Param("status") String status);

        /**
         * Buscar proveedores activos
         */
        @Query("SELECT p FROM Provider p WHERE p.user.isActive = true")
        java.util.List<Provider> findAllActive();

        /**
         * Obtiene una lista de proveedores por su estado de verificación.
         *
         * @param verificationStatus Estado de verificación (ej. PENDING, VERIFIED,
         *                           REJECTED).
         * @return Lista de proveedores con ese estado.
         */
        List<Provider> findByVerificationStatus(VerificationStatus verificationStatus);

        /**
         * Busca proveedores cuyo nombre comercial contenga una cadena (sin importar
         * mayúsculas/minúsculas).
         *
         * @param businessName Parte del nombre comercial.
         * @return Lista de proveedores que coinciden.
         */
        List<Provider> findByBusinessNameContainingIgnoreCase(String businessName);

        /**
         * Obtiene proveedores filtrados por ciudad.
         *
         * @param city Ciudad de los proveedores.
         * @return Lista de proveedores en esa ciudad.
         */
        List<Provider> findByCity(String city);

        /**
         * Obtiene proveedores filtrados por país.
         *
         * @param country País de los proveedores.
         * @return Lista de proveedores en ese país.
         */
        List<Provider> findByCountry(String country);

        /**
         * Busca proveedores cuyo nombre comercial contenga un texto y que tengan un
         * estado de verificación específico.
         *
         * @param name   Parte del nombre comercial.
         * @param status Estado de verificación.
         * @return Lista de proveedores que cumplen ambas condiciones.
         */
        @Query("SELECT p FROM Provider p WHERE p.businessName LIKE %:name% AND p.verificationStatus = :status")
        List<Provider> findByBusinessNameAndStatus(@Param("name") String name,
                        @Param("status") VerificationStatus status);

        /**
         * Obtiene proveedores filtrados por el estado de actividad de su usuario
         * asociado.
         *
         * @param isActive Estado de actividad del usuario (true = activo, false =
         *                 inactivo).
         * @return Lista de proveedores cuyos usuarios cumplen la condición.
         */
        @Query("SELECT p FROM Provider p JOIN p.user u WHERE u.isActive = :isActive")
        List<Provider> findByUserIsActive(@Param("isActive") Boolean isActive);

        /**
         * Cuenta la cantidad de proveedores que tienen el estado de verificación
         * "VERIFIED".
         *
         * @return Número de proveedores verificados.
         */
        @Query("SELECT COUNT(p) FROM Provider p WHERE p.verificationStatus = 'VERIFIED'")
        Long countVerifiedProviders();

        /**
         * Top proveedores (por ejemplo, por número de productos - necesitarás ajustar
         * según tu modelo)
         */
        @Query("SELECT p FROM Provider p " +
                        "LEFT JOIN User u ON p.user.id = u.id " +
                        "WHERE u.isActive = true AND p.verificationStatus = 'VERIFIED' " +
                        "ORDER BY p.createdAt DESC")
        List<Provider> findTopActiveProviders();

        /**
         * Proveedores pendientes más antiguos
         */
        @Query("SELECT p FROM Provider p " +
                        "WHERE p.verificationStatus = 'PENDING' " +
                        "ORDER BY p.createdAt ASC")
        List<Provider> findOldestPendingProviders();

        /**
         * Estadísticas adicionales por estado
         */
        @Query("SELECT p.verificationStatus as status, COUNT(p) as count " +
                        "FROM Provider p " +
                        "GROUP BY p.verificationStatus")
        List<Object[]> getProviderStatusStatistics();
}
