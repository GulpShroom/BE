package com.hufsglobalion.glupshroom.domain.resell.dto.response;

public record ResellSaveResponse(
        Long productId,
        Long price,
        String postStatus,
        Long resellId
) {}
