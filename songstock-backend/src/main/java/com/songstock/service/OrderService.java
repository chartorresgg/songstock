package com.songstock.service;

import com.songstock.dto.*;
import com.songstock.entity.*;
import com.songstock.entity.OrderReview;
import com.songstock.repository.OrderReviewRepository;
import com.songstock.dto.CreateReviewDTO;
import com.songstock.dto.OrderReviewDTO;
import com.songstock.exception.ResourceNotFoundException;
import com.songstock.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import com.songstock.exception.BusinessException;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderReviewRepository orderReviewRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProviderRepository providerRepository;

    public OrderDTO createOrder(Long userId, CreateOrderDTO createDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setPaymentMethod(createDTO.getPaymentMethod());
        order.setShippingAddress(createDTO.getShippingAddress());
        order.setShippingCity(createDTO.getShippingCity());
        order.setShippingState(createDTO.getShippingState());
        order.setShippingPostalCode(createDTO.getShippingPostalCode());
        order.setShippingCountry(createDTO.getShippingCountry());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderDTO.OrderItemRequestDTO itemDTO : createDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

            Provider provider = product.getProvider();

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setProvider(provider);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(product.getPrice());
            item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
            item.setStatus(OrderItemStatus.PENDING);

            order.getItems().add(item);
            total = total.add(item.getSubtotal());
        }

        order.setTotal(total);
        Order savedOrder = orderRepository.save(order);

        // Crear notificación
        notificationService.createOrderNotification(userId, savedOrder.getId(), savedOrder.getOrderNumber());

        return mapToDTO(savedOrder);
    }

    /**
     * Obtener órdenes con items pendientes de un proveedor
     */
    public List<OrderDTO> getProviderPendingOrders(Long providerId) {
        List<Order> orders = orderRepository.findOrdersWithPendingItemsByProviderId(providerId);
        return orders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Aceptar un item de orden
     */
    public void acceptOrderItem(Long itemId) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));

        item.setStatus(OrderItemStatus.ACCEPTED);
        orderItemRepository.save(item);
    }

    /**
     * Rechazar un item de orden
     */
    public void rejectOrderItem(Long itemId, String reason) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));

        item.setStatus(OrderItemStatus.REJECTED);
        item.setRejectionReason(reason);
        orderItemRepository.save(item);
    }

    /**
     * Obtener todas las órdenes de un proveedor
     */
    public List<OrderDTO> getProviderOrders(Long providerId) {
        List<Order> orders = orderRepository.findOrdersByProviderId(providerId);
        return orders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener órdenes de un usuario
     */
    public List<OrderDTO> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener orden por ID
     */
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));
        return mapToDTO(order);
    }

    /**
     * Actualizar estado de orden (no usado actualmente)
     */
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus, String rejectionReason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        order.setStatus(newStatus);
        if (newStatus == OrderStatus.REJECTED && rejectionReason != null) {
            order.setRejectionReason(rejectionReason);
        }
        if (newStatus == OrderStatus.SHIPPED) {
            order.setShippedAt(LocalDateTime.now());
        }
        if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        return mapToDTO(orderRepository.save(order));
    }

    /**
     * Generar número de orden único
     */
    private String generateOrderNumber() {
        return "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }

    /**
     * Mapear entidad Order a DTO
     */
    private OrderDTO mapToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setUserId(order.getUser().getId());
        dto.setUserName(order.getUser().getFirstName() + " " + order.getUser().getLastName());
        dto.setUserEmail(order.getUser().getEmail());
        dto.setStatus(order.getStatus());
        dto.setTotal(order.getTotal());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setRejectionReason(order.getRejectionReason());
        dto.setEstimatedDeliveryDate(order.getEstimatedDeliveryDate());
        dto.setShippedAt(order.getShippedAt());
        dto.setDeliveredAt(order.getDeliveredAt());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        // Mapear dirección de envío
        OrderDTO.ShippingAddressDTO shippingDTO = new OrderDTO.ShippingAddressDTO();
        shippingDTO.setAddress(order.getShippingAddress());
        shippingDTO.setCity(order.getShippingCity());
        shippingDTO.setState(order.getShippingState());
        shippingDTO.setPostalCode(order.getShippingPostalCode());
        shippingDTO.setCountry(order.getShippingCountry());
        dto.setShippingAddress(shippingDTO);

        // Mapear items
        List<OrderItemDTO> itemDTOs = order.getItems().stream().map(item -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setUnitPrice(item.getUnitPrice());
            itemDTO.setSubtotal(item.getSubtotal());
            itemDTO.setProviderId(item.getProvider().getId());
            itemDTO.setProviderName(item.getProvider().getBusinessName());
            itemDTO.setStatus(item.getStatus());
            itemDTO.setRejectionReason(item.getRejectionReason());
            itemDTO.setShippedAt(item.getShippedAt());

            // Mapear producto
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(item.getProduct().getId());
            productDTO.setAlbumTitle(item.getProduct().getAlbum().getTitle());
            productDTO.setArtistName(item.getProduct().getAlbum().getArtist().getName());
            productDTO.setPrice(item.getProduct().getPrice());
            productDTO.setProductType(item.getProduct().getProductType());
            itemDTO.setProduct(productDTO);

            return itemDTO;
        }).collect(Collectors.toList());
        dto.setItems(itemDTOs);

        return dto;
    }

    @Transactional
    public void shipOrderItem(Long itemId, LocalDateTime shippedDate) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));

        // Usar la fecha proporcionada o la actual
        LocalDateTime shipDate = shippedDate != null ? shippedDate : LocalDateTime.now();

        item.setStatus(OrderItemStatus.SHIPPED);
        item.setShippedAt(shipDate); // ← ESTA LÍNEA ES CRÍTICA
        orderItemRepository.save(item);

        // Actualizar orden si todos los items están shipped
        updateOrderStatusIfNeeded(item.getOrder());
    }

    @Transactional
    public void deliverOrderItem(Long itemId) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));

        // El item ya debe estar en SHIPPED
        if (item.getStatus() != OrderItemStatus.SHIPPED) {
            throw new BusinessException("El item debe estar en estado SHIPPED para marcarlo como entregado");
        }

        // Actualizar la orden completa a DELIVERED
        item.setStatus(OrderItemStatus.DELIVERED);
        orderItemRepository.save(item);

        // Actualizar orden si todos los items están delivered
        Order order = item.getOrder();
        boolean allDelivered = order.getItems().stream()
                .allMatch(i -> i.getStatus() == OrderItemStatus.DELIVERED);

        if (allDelivered) {
            order.setStatus(OrderStatus.DELIVERED);
            order.setDeliveredAt(LocalDateTime.now());
            orderRepository.save(order);
        }
    }

    @Transactional
    public void confirmOrderReceived(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException("No tienes permiso para confirmar esta orden");
        }

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessException("Solo puedes confirmar órdenes en estado DELIVERED");
        }

        order.setStatus(OrderStatus.RECEIVED);
        orderRepository.save(order);
    }

    /**
     * Actualizar estado de la orden basado en el estado de todos sus items
     */
    private void updateOrderStatusIfNeeded(Order order) {
        List<OrderItem> items = order.getItems();

        // Si todos los items están SHIPPED y la orden aún no está SHIPPED
        boolean allShipped = items.stream()
                .allMatch(i -> i.getStatus() == OrderItemStatus.SHIPPED);

        if (allShipped && order.getStatus() != OrderStatus.SHIPPED && order.getStatus() != OrderStatus.DELIVERED) {
            order.setStatus(OrderStatus.SHIPPED);

            LocalDateTime mostRecentShipDate = items.stream()
                    .map(OrderItem::getShippedAt)
                    .filter(date -> date != null)
                    .max(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now());

            order.setShippedAt(mostRecentShipDate);
            orderRepository.save(order);
        }
    }

    @Transactional
    public OrderReviewDTO createReview(Long orderId, Long userId, CreateReviewDTO dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        // Validar que la orden pertenece al usuario
        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException("No tienes permiso para valorar esta orden");
        }

        // Validar que la orden está entregada
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessException("Solo puedes valorar órdenes entregadas");
        }

        // Validar que no existe una valoración previa
        if (orderReviewRepository.existsByOrderId(orderId)) {
            throw new BusinessException("Esta orden ya fue valorada");
        }

        OrderReview review = new OrderReview();
        review.setOrder(order);
        review.setUser(order.getUser());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        review = orderReviewRepository.save(review);
        return mapToReviewDTO(review);
    }

    public OrderReviewDTO getReview(Long orderId) {
        return orderReviewRepository.findByOrderId(orderId)
                .map(this::mapToReviewDTO)
                .orElse(null);
    }

    private OrderReviewDTO mapToReviewDTO(OrderReview review) {
        OrderReviewDTO dto = new OrderReviewDTO();
        dto.setId(review.getId());
        dto.setOrderId(review.getOrder().getId());
        dto.setUserId(review.getUser().getId());
        dto.setUserName(review.getUser().getFirstName() + " " + review.getUser().getLastName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }

}