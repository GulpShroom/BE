package com.hufsglobalion.glupshroom.domain.journey.repository;

import com.hufsglobalion.glupshroom.domain.journey.entity.Journey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JourneyRepository extends JpaRepository<Journey, Long> {

    long countByProductId(Long productId);

    long countByProductIdAndAuthorId(Long productId, Long authorId);

    Page<Journey> findByProductIdAndAuthorId(Long productId, Long authorId, Pageable pageable);
}