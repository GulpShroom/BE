package com.hufsglobalion.glupshroom.domain.product.dto.request;

import java.time.LocalDate;

public record ProductRegistrationRequest(
        String serialNo,
        String nickname,
        LocalDate purchaseDate,
        Long storeId,
        Long ownerId,
        String firstJourneyMemo
) {
}
