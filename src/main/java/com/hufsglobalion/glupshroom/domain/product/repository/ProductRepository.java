package com.hufsglobalion.glupshroom.domain.product.repository;

import com.hufsglobalion.glupshroom.domain.product.entity.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySerialNo(String serialNo);
}
