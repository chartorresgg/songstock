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

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findBySessionToken(String sessionToken);

    Optional<UserSession> findByRefreshToken(String refreshToken);

    List<UserSession> findByUser(User user);

    List<UserSession> findByUserAndIsActive(User user, Boolean isActive);

    @Query("SELECT us FROM UserSession us WHERE us.expiresAt < :now")
    List<UserSession> findExpiredSessions(@Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("UPDATE UserSession us SET us.isActive = false WHERE us.user.id = :userId")
    void deactivateAllUserSessions(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserSession us SET us.isActive = false WHERE us.sessionToken = :token")
    void deactivateSession(@Param("token") String sessionToken);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserSession us WHERE us.expiresAt < :now")
    void deleteExpiredSessions(@Param("now") LocalDateTime now);
}
