package com.hufsglobalion.glupshroom.domain.resell.repository;

import com.hufsglobalion.glupshroom.domain.resell.entity.Resell;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResellRepository extends JpaRepository<Resell, Long> {

    Page<Resell> findByPostStatus(String postStatus, Pageable pageable);

    Page<Resell> findByPostStatusAndSellerId(String postStatus, Long sellerId, Pageable pageable);

    Page<Resell> findByPostStatusAndBuyerId(String postStatus, Long buyerId, Pageable pageable);
}
