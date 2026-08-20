package com.hufsglobalion.glupshroom.domain.care.repository;

import com.hufsglobalion.glupshroom.domain.care.entity.CareTip;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CareTipRepository extends JpaRepository<CareTip, Long> {

    @Query("SELECT c FROM CareTip c "
            + "WHERE c.productId = :productId "
            + "AND (c.generation = :currentGeneration OR c.inheritSelected = true) "
            + "ORDER BY c.generation ASC")
    List<CareTip> findVisibleCareTips(
            @Param("productId") Long productId,
            @Param("currentGeneration") Integer currentGeneration
    );
}
