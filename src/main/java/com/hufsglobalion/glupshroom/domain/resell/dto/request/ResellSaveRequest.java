package com.hufsglobalion.glupshroom.domain.resell.dto.request;

import java.util.List;

public record ResellSaveRequest(
        Long productId,
        Long sellerId,
        Long price,
        String conditionGrade,
        Boolean letterShared,
        Boolean caretipShared,
        List<String> photoUrls,
        List<SelectedTag> selectedTags
) {
    public record SelectedTag(
            Long journeyId,
            String type
    ) {}
}
