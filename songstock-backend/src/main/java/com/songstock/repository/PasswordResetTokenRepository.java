package com.songstock.repository;

import com.songstock.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByEmailAndUsedFalseAndExpiresAtAfter(String email, LocalDateTime now);

    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiresAt < :now OR p.used = true")
    void deleteExpiredAndUsedTokens(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE PasswordResetToken p SET p.used = true WHERE p.email = :email")
    void markAllTokensAsUsedForEmail(@Param("email") String email);
}