package com.hufsglobalion.glupshroom.domain.resell.dto.response;

import java.util.List;

public record ResellListResponse(
        long totalCount,
        List<ResellSummary> resells
) {
    public record ResellSummary(
            Long resellId,
            String nickname,
            Long price,
            String postStatus,
            Integer provenanceScore,
            String conditionGrade
    ) {}
}
