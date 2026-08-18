package com.hufsglobalion.glupshroom.domain.care.dto.response;

import java.time.OffsetDateTime;

public record CareTipCreateResponse(
        Long careTipId,
        boolean isInheritSelected,
        OffsetDateTime createdAt
) {
}
