package com.hufsglobalion.glupshroom.domain.journey;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JourneyRepository extends JpaRepository<Journey, Long> {
    long countByProductId(Long productId);
    Page<Journey> findByProductIdAndUserId(Long productId, Long userId, Pageable pageable);
}