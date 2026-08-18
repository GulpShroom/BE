package com.hufsglobalion.glupshroom.domain.resell.dto.response;

public record ResellUpdateResponse(
        Long resellId,
        Long price,
        String conditionGrade,
        Boolean letterShared,
        Boolean caretipShared
) {}
