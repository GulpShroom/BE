package com.hufsglobalion.glupshroom.domain.resell.dto.request;

import java.util.List;

public record ResellUpdateRequest(
        Long sellerId,
        Long price,
        String conditionGrade,
        Boolean letterShared,
        Boolean caretipShared,
        List<String> photoUrls
) {}
