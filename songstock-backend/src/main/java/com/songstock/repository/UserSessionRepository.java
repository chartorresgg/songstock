package com.songstock.repository;

import com.songstock.entity.User;
import com.songstock.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad {@link UserSession}.
 *
 * Gestiona las sesiones de usuario, incluyendo tokens, estado de actividad,
 * y limpieza de sesiones expiradas.
 */
@Repository // Marca la interfaz como un repositorio de Spring.
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    /**
     * Busca una sesión por su token de sesión.
     *
     * @param sessionToken Token único de sesión.
     * @return Un {@link Optional} con la sesión si existe.
     */
    Optional<UserSession> findBySessionToken(String sessionToken);

    /**
     * Busca una sesión por su token de refresco.
     *
     * @param refreshToken Token de refresco único.
     * @return Un {@link Optional} con la sesión si existe.
     */
    Optional<UserSession> findByRefreshToken(String refreshToken);

    /**
     * Obtiene todas las sesiones asociadas a un usuario.
     *
     * @param user Usuario propietario de las sesiones.
     * @return Lista de sesiones del usuario.
     */
    List<UserSession> findByUser(User user);

    /**
     * Obtiene todas las sesiones activas o inactivas de un usuario específico.
     *
     * @param user     Usuario propietario.
     * @param isActive Estado de las sesiones (true = activas, false = inactivas).
     * @return Lista de sesiones que cumplen la condición.
     */
    List<UserSession> findByUserAndIsActive(User user, Boolean isActive);

    /**
     * Encuentra todas las sesiones que han expirado antes de una fecha y hora
     * específica.
     *
     * @param now Fecha y hora actual.
     * @return Lista de sesiones expiradas.
     */
    @Query("SELECT us FROM UserSession us WHERE us.expiresAt < :now")
    List<UserSession> findExpiredSessions(@Param("now") LocalDateTime now);

    /**
     * Desactiva todas las sesiones de un usuario, marcándolas como inactivas.
     *
     * @param userId ID del usuario.
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserSession us SET us.isActive = false WHERE us.user.id = :userId")
    void deactivateAllUserSessions(@Param("userId") Long userId);

    /**
     * Desactiva una sesión específica por su token.
     *
     * @param sessionToken Token de la sesión a desactivar.
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserSession us SET us.isActive = false WHERE us.sessionToken = :token")
    void deactivateSession(@Param("token") String sessionToken);

    /**
     * Elimina de la base de datos todas las sesiones que hayan expirado.
     *
     * @param now Fecha y hora actual para comparar.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM UserSession us WHERE us.expiresAt < :now")
    void deleteExpiredSessions(@Param("now") LocalDateTime now);
}
