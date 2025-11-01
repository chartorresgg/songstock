package com.songstock.repository;

import com.songstock.entity.Order;
import com.songstock.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

        /**
         * Buscar órdenes por usuario ordenadas por fecha
         */
        List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

        /**
         * Buscar orden por número de orden
         */
        Optional<Order> findByOrderNumber(String orderNumber);

        /**
         * Buscar órdenes que contengan items de un proveedor específico
         * (Query personalizada porque la relación es Order -> OrderItem -> Provider)
         */
        @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.provider.id = :providerId ORDER BY o.createdAt DESC")
        List<Order> findOrdersByProviderId(@Param("providerId") Long providerId);

        /**
         * Buscar órdenes que contengan items pendientes de un proveedor
         */
        @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.provider.id = :providerId AND i.status = 'PENDING' ORDER BY o.createdAt DESC")
        List<Order> findOrdersWithPendingItemsByProviderId(@Param("providerId") Long providerId);

        /**
         * Contar órdenes de un proveedor
         */
        @Query("SELECT COUNT(DISTINCT o) FROM Order o JOIN o.items i WHERE i.provider.id = :providerId")
        Long countOrdersByProviderId(@Param("providerId") Long providerId);
}