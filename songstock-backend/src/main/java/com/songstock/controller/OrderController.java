package com.songstock.controller;

import com.songstock.dto.CreateOrderDTO;
import com.songstock.dto.OrderDTO;
import com.songstock.entity.User;
import com.songstock.repository.UserRepository;
import com.songstock.service.OrderService;
import com.songstock.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders") // ✅ SIN /api/v1 (ya está en context-path)
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class OrderController {

        @Autowired
        private OrderService orderService;
        @Autowired
        private UserRepository userRepository;

        @PostMapping
        @PreAuthorize("hasRole('CUSTOMER')")
        public ResponseEntity<ApiResponse<OrderDTO>> createOrder(
                        @RequestBody CreateOrderDTO createDTO,
                        Authentication authentication) {

                try {
                        User user = userRepository.findByUsername(authentication.getName())
                                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                        OrderDTO order = orderService.createOrder(user.getId(), createDTO);

                        return ResponseEntity.status(HttpStatus.CREATED)
                                        .body(ApiResponse.success("Orden creada exitosamente", order));

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(false, "Error al crear la orden", null));
                }
        }

        @GetMapping("/provider/pending")
        @PreAuthorize("hasRole('PROVIDER')")
        public ResponseEntity<ApiResponse<List<OrderDTO>>> getPendingOrders(
                        Authentication authentication) {

                try {
                        User user = userRepository.findByUsername(authentication.getName())
                                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                        Long providerId = user.getProvider().getId();
                        List<OrderDTO> orders = orderService.getProviderPendingOrders(providerId);

                        return ResponseEntity.ok(
                                        ApiResponse.success("Órdenes pendientes obtenidas", orders));

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(false, "Error al obtener órdenes", null));
                }
        }

        @GetMapping("/my-orders")
        @PreAuthorize("hasRole('CUSTOMER')")
        public ResponseEntity<ApiResponse<List<OrderDTO>>> getMyOrders(
                        Authentication authentication) {

                try {
                        User user = userRepository.findByUsername(authentication.getName())
                                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                        List<OrderDTO> orders = orderService.getUserOrders(user.getId());

                        return ResponseEntity.ok(
                                        ApiResponse.success("Órdenes obtenidas exitosamente", orders));

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(false, "Error al obtener órdenes", null));
                }
        }

        @GetMapping("/{orderId}")
        @PreAuthorize("hasRole('CUSTOMER') or hasRole('PROVIDER')")
        public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(@PathVariable Long orderId) {

                try {
                        OrderDTO order = orderService.getOrderById(orderId);
                        return ResponseEntity.ok(
                                        ApiResponse.success("Orden obtenida exitosamente", order));

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new ApiResponse<>(false, "Orden no encontrada", null));
                }
        }

        @PutMapping("/items/{itemId}/accept")
        @PreAuthorize("hasRole('PROVIDER')")
        public ResponseEntity<ApiResponse<Void>> acceptOrderItem(
                        @PathVariable Long itemId,
                        Authentication authentication) {

                try {
                        orderService.acceptOrderItem(itemId);
                        return ResponseEntity.ok(ApiResponse.success("Item aceptado", null));

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(false, "Error al aceptar el item", null));
                }
        }

        @PutMapping("/items/{itemId}/reject")
        @PreAuthorize("hasRole('PROVIDER')")
        public ResponseEntity<ApiResponse<Void>> rejectOrderItem(
                        @PathVariable Long itemId,
                        @RequestParam String reason,
                        Authentication authentication) {

                try {
                        orderService.rejectOrderItem(itemId, reason);
                        return ResponseEntity.ok(ApiResponse.success("Item rechazado", null));

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(false, "Error al rechazar el item", null));
                }
        }
}