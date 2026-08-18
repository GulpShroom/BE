package com.hufsglobalion.glupshroom.domain.product.dto.response;

import java.time.OffsetDateTime;

public record ProductRegistrationResponse(
        Long productId,
        String passportId,
        String serialNo,
        String nickname,
        String officialName,
        Integer currentGeneration,
        String purchaseInfoStatus,
        Long firstJourneyId,
        OffsetDateTime createdAt
) {
}
