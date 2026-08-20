package com.hufsglobalion.glupshroom.domain.resell.repository;

import com.hufsglobalion.glupshroom.domain.resell.entity.ResellSelectedTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResellSelectedTagRepository extends JpaRepository<ResellSelectedTag, Long> {

    List<ResellSelectedTag> findByResellId(Long resellId);

    boolean existsByResellId(Long resellId);
}
