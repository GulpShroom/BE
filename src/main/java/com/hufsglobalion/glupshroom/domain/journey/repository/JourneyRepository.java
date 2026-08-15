package com.hufsglobalion.glupshroom.domain.journey.repository;

import com.hufsglobalion.glupshroom.domain.journey.entity.Journey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JourneyRepository extends JpaRepository<Journey, Long> {
    long countByProductId(Long productId);
}