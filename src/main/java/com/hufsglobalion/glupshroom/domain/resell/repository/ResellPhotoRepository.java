package com.hufsglobalion.glupshroom.domain.resell.repository;

import com.hufsglobalion.glupshroom.domain.resell.entity.ResellPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResellPhotoRepository extends JpaRepository<ResellPhoto, Long> {

    List<ResellPhoto> findByResellIdOrderBySortOrder(Long resellId);

    void deleteByResellId(Long resellId);
}
