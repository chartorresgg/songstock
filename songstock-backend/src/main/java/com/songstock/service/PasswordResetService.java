package com.songstock.service;

import com.songstock.entity.PasswordResetToken;
import com.songstock.entity.User;
import com.songstock.repository.PasswordResetTokenRepository;
import com.songstock.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class PasswordResetService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void createPasswordResetToken(String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return;
        }

        // Invalidar tokens anteriores
        passwordResetTokenRepository.markAllTokensAsUsedForEmail(email);

        // Generar nuevo token
        String resetToken = generateSecureToken();

        // Usar el constructor con User
        PasswordResetToken passwordResetToken = new PasswordResetToken(resetToken, user);
        passwordResetTokenRepository.save(passwordResetToken);

        logger.info("Password reset token for {}: {}", email, resetToken);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (!resetToken.isValid()) {
            throw new RuntimeException("Token expirado o ya utilizado");
        }

        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar contraseña
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Marcar token como usado
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    @Transactional
    public void cleanupExpiredTokens() {
        passwordResetTokenRepository.deleteExpiredAndUsedTokens(LocalDateTime.now());
    }

    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}