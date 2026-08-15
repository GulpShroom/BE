package com.hufsglobalion.glupshroom.domain.journey;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JourneyRepository extends JpaRepository<Journey, Long> {
    long countByProductId(Long productId);
}