package com.hufsglobalion.glupshroom.domain.care.dto.response;

import java.util.List;

public record CareTipListResponse(
        List<CareTipSummary> careTips
) {
    public record CareTipSummary(
            Long careTipId,
            Integer generation,
            String content
    ) {
    }
}
