package com.hufsglobalion.glupshroom.domain.journey.repository;

import com.hufsglobalion.glupshroom.domain.journey.entity.Journey;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JourneyRepository extends JpaRepository<Journey, Long> {

    long countByProductId(Long productId);

    long countByProductIdAndAuthorId(Long productId, Long authorId);

    Page<Journey> findByProductIdAndAuthorId(Long productId, Long authorId, Pageable pageable);

    List<Journey> findByProductIdAndAuthorIdOrderByJourneyYearAscJourneyMonthAsc(Long productId, Long authorId);

    List<Journey> findByProductIdAndAuthorIdAndExifTakenAtIsNotNullOrderByExifTakenAtDesc(
            Long productId,
            Long authorId
    );

    List<Journey> findByProductId(Long productId);

    @Query("SELECT COUNT(DISTINCT j.generation) FROM Journey j WHERE j.productId = :productId")
    long countDistinctGenerationByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(DISTINCT j.country) FROM Journey j WHERE j.productId = :productId AND j.country IS NOT NULL")
    long countDistinctCountryByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(DISTINCT j.city) FROM Journey j WHERE j.productId = :productId AND j.city IS NOT NULL")
    long countDistinctCityByProductId(@Param("productId") Long productId);

    @Query("SELECT DISTINCT j.city FROM Journey j WHERE j.productId = :productId AND j.city IS NOT NULL")
    List<String> findDistinctCitiesByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(j) FROM Journey j WHERE j.productId = :productId AND j.verifyStatus = 'VERIFIED'")
    long countVerifiedByProductId(@Param("productId") Long productId);
}
