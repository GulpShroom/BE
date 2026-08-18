package com.hufsglobalion.glupshroom.domain.store.repository;

import com.hufsglobalion.glupshroom.domain.store.entity.Store;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {

    List<Store> findByCountry(String country);

    List<Store> findByCountryAndCity(String country, String city);
}
