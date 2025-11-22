package com.songstock.repository;

import com.songstock.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByProviderId(Long providerId);

    @Query("SELECT SUM(oi.subtotal) FROM OrderItem oi WHERE oi.provider.id = :providerId")
    BigDecimal getTotalSalesByProvider(@Param("providerId") Long providerId);

    @Query("SELECT SUM(oi.subtotal) FROM OrderItem oi WHERE oi.provider.id = :providerId AND oi.status IN ('SHIPPED', 'DELIVERED')")
    BigDecimal getTotalRevenueByProvider(@Param("providerId") Long providerId);

    @Query("SELECT COUNT(DISTINCT oi.order.id) FROM OrderItem oi WHERE oi.provider.id = :providerId")
    Long countOrdersByProvider(@Param("providerId") Long providerId);

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.provider.id = :providerId AND oi.status IN ('SHIPPED', 'DELIVERED')")
    Long countCompletedItemsByProvider(@Param("providerId") Long providerId);

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.provider.id = :providerId AND oi.status = 'PENDING'")
    Long countPendingItemsByProvider(@Param("providerId") Long providerId);
}