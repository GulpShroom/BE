package com.hufsglobalion.glupshroom.domain.store.service;

import com.hufsglobalion.glupshroom.domain.store.dto.response.StoreListResponse;
import com.hufsglobalion.glupshroom.domain.store.entity.Store;
import com.hufsglobalion.glupshroom.domain.store.repository.StoreRepository;
import com.hufsglobalion.glupshroom.global.exception.CustomException;
import com.hufsglobalion.glupshroom.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreListResponse getStores(String country, String city) {
        String normalizedCountry = normalize(country);
        String normalizedCity = normalize(city);

        try {
            if (normalizedCountry == null) {
                return new StoreListResponse(
                        storeRepository.findAll().stream()
                                .map(Store::getCountry)
                                .distinct()
                                .toList(),
                        List.of(),
                        List.of()
                );
            }

            List<Store> stores = normalizedCity == null
                    ? storeRepository.findByCountry(normalizedCountry)
                    : storeRepository.findByCountryAndCity(normalizedCountry, normalizedCity);

            return new StoreListResponse(
                    List.of(),
                    normalizedCity == null
                            ? stores.stream()
                            .map(Store::getCity)
                            .distinct()
                            .map(StoreListResponse.City::new)
                            .toList()
                            : List.of(),
                    stores.stream()
                            .map(store -> new StoreListResponse.Store(
                                    store.getId(),
                                    store.getStoreName()
                            ))
                            .toList()
            );
        } catch (DataAccessException e) {
            log.error("Store list database lookup failed", e);
            throw new CustomException(ErrorCode.STORE_LIST_RETRIEVAL_FAILED);
        }
    }

    public Store getStore(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
