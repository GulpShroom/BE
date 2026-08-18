package com.hufsglobalion.glupshroom.domain.product.dto.response;

import java.time.LocalDate;

public record DigitalPassportResponse(
        Long productId,
        String passportId,
        String serialNo,
        String nickname,
        String officialName,
        String officialImageUrl,
        boolean isAuthenticated,
        LocalDate authenticatedAt,
        Integer currentGeneration,
        Specification specification,
        Purchase purchase
) {
    public record Specification(
            Integer manufactureYear,
            String productLine,
            String color
    ) {
    }

    public record Purchase(
            LocalDate purchaseDate,
            Long storeId,
            String storeName,
            String city,
            String country
    ) {
    }
}
