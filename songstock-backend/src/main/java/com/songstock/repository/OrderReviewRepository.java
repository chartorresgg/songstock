package com.songstock.repository;

import com.songstock.entity.OrderReview;
import com.songstock.entity.ReviewStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OrderReviewRepository extends JpaRepository<OrderReview, Long> {
    Optional<OrderReview> findByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);

    List<OrderReview> findByStatus(ReviewStatus status);

    List<OrderReview> findByStatusOrderByCreatedAtDesc(ReviewStatus status);
}