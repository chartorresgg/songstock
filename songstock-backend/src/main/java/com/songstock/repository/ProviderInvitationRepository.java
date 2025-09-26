package com.songstock.repository;

import com.songstock.entity.InvitationStatus;
import com.songstock.entity.ProviderInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad {@link ProviderInvitation}.
 *
 * Extiende de {@link JpaRepository} para heredar operaciones CRUD básicas y
 * define consultas personalizadas para gestionar invitaciones de proveedores.
 */
@Repository // Marca esta interfaz como un repositorio de Spring, habilitando la inyección y
            // detección automática.
public interface ProviderInvitationRepository extends JpaRepository<ProviderInvitation, Long> {

    /**
     * Busca una invitación por su token único.
     *
     * @param invitationToken Token de invitación.
     * @return Un {@link Optional} con la invitación si existe, o vacío si no se
     *         encuentra.
     */
    Optional<ProviderInvitation> findByInvitationToken(String invitationToken);

    /**
     * Busca una invitación por el correo electrónico al que fue enviada.
     *
     * @param email Correo electrónico del destinatario.
     * @return Un {@link Optional} con la invitación si existe, o vacío si no se
     *         encuentra.
     */
    Optional<ProviderInvitation> findByEmail(String email);

    /**
     * Obtiene todas las invitaciones que coinciden con un estado específico.
     *
     * @param status Estado de la invitación (por ejemplo, PENDING, ACCEPTED,
     *               REJECTED).
     * @return Lista de invitaciones con el estado indicado.
     */
    List<ProviderInvitation> findByStatus(InvitationStatus status);

    /**
     * Obtiene todas las invitaciones enviadas por un usuario específico.
     *
     * @param invitedBy ID del usuario que envió las invitaciones.
     * @return Lista de invitaciones creadas por ese usuario.
     */
    List<ProviderInvitation> findByInvitedBy(Long invitedBy);

    /**
     * Encuentra las invitaciones que ya han expirado y aún están en estado PENDING.
     *
     * @param now Fecha y hora actual para comparar con la fecha de expiración.
     * @return Lista de invitaciones expiradas pendientes de acción.
     */
    @Query("SELECT pi FROM ProviderInvitation pi WHERE pi.expiresAt < :now AND pi.status = 'PENDING'")
    List<ProviderInvitation> findExpiredInvitations(@Param("now") LocalDateTime now);

    /**
     * Verifica si ya existe una invitación para un correo electrónico específico.
     *
     * @param email Correo electrónico a verificar.
     * @return true si ya existe una invitación para ese email, false en caso
     *         contrario.
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si ya existe una invitación asociada a un token específico.
     *
     * @param token Token de invitación a verificar.
     * @return true si ya existe una invitación con ese token, false en caso
     *         contrario.
     */
    boolean existsByInvitationToken(String token);
}
