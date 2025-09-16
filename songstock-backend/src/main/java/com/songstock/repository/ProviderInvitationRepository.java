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

@Repository
public interface ProviderInvitationRepository extends JpaRepository<ProviderInvitation, Long> {

    Optional<ProviderInvitation> findByInvitationToken(String invitationToken);

    Optional<ProviderInvitation> findByEmail(String email);

    List<ProviderInvitation> findByStatus(InvitationStatus status);

    List<ProviderInvitation> findByInvitedBy(Long invitedBy);

    @Query("SELECT pi FROM ProviderInvitation pi WHERE pi.expiresAt < :now AND pi.status = 'PENDING'")
    List<ProviderInvitation> findExpiredInvitations(@Param("now") LocalDateTime now);

    boolean existsByEmail(String email);

    boolean existsByInvitationToken(String token);
}