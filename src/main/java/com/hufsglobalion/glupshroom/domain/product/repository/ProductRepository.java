package com.hufsglobalion.glupshroom.domain.product.repository;

import com.hufsglobalion.glupshroom.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySerialNo(String serialNo);
}
