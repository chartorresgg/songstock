package com.songstock.controller;

import com.songstock.dto.NotificationDTO;
import com.songstock.entity.User;
import com.songstock.repository.UserRepository;
import com.songstock.service.NotificationService;
import com.songstock.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getNotifications(
            Authentication authentication) {
        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<NotificationDTO> notifications = notificationService.getUserNotifications(user.getId());
            return ResponseEntity.ok(ApiResponse.success("Notificaciones obtenidas", notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error al obtener notificaciones", null));
        }
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long count = notificationService.getUnreadCount(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Contador obtenido", count));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok(ApiResponse.success("Notificación marcada como leída", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Error", null));
        }
    }
}