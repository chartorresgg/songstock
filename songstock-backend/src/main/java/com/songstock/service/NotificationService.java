package com.songstock.service;

import com.songstock.dto.NotificationDTO;
import com.songstock.entity.Notification;
import com.songstock.entity.User;
import com.songstock.entity.Product;
import com.songstock.repository.NotificationRepository;
import com.songstock.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public void createOrderNotification(Long userId, Long orderId, String orderNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(Notification.NotificationType.ORDER_CREATED);
        notification.setTitle("Pedido confirmado");
        notification.setMessage("Tu pedido #" + orderNumber + " ha sido creado exitosamente");
        notification.setOrderId(orderId);
        notification.setIsRead(false);

        notificationRepository.save(notification);
    }

    public void createProviderOrderNotification(Long providerId, Long orderId, String orderNumber, String productName) {
        User providerUser = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("Usuario proveedor no encontrado"));

        Notification notification = new Notification();
        notification.setUser(providerUser);
        notification.setType(Notification.NotificationType.PROVIDER_NEW_ORDER);
        notification.setTitle("Nuevo pedido recibido");
        notification.setMessage("Has recibido un pedido #" + orderNumber + " para: " + productName);
        notification.setOrderId(orderId);
        notification.setIsRead(false);

        notificationRepository.save(notification);
    }

    public List<NotificationDTO> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }

    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public void createLowStockAlert(Product product, Long providerId) {
        logger.info("📦 Creando alerta LOW_STOCK - Producto: {}, Provider: {}, Stock: {}",
                product.getId(), providerId, product.getStockQuantity());

        User providerUser = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("Usuario proveedor no encontrado"));

        Notification notification = new Notification();
        notification.setUser(providerUser);
        notification.setType(Notification.NotificationType.LOW_STOCK);
        notification.setTitle("Alerta: Stock bajo");
        notification.setMessage(
                String.format("El producto '%s' tiene stock bajo (%d unidades). Umbral: %d",
                        product.getAlbum().getTitle(),
                        product.getStockQuantity(),
                        product.getLowStockThreshold()));
        notification.setIsRead(false);
        Notification saved = notificationRepository.save(notification);

        logger.info("✅ Notificación LOW_STOCK creada - ID: {}, User: {}", saved.getId(), providerUser.getId());
    }
}