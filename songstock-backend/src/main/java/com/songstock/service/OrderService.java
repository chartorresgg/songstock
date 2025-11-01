package com.songstock.service;

import com.songstock.dto.*;
import com.songstock.entity.*;
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

@Service
@Transactional
public class OrderService {

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
}