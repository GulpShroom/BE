package com.hufsglobalion.glupshroom.domain.care.dto.request;

public record CareTipCreateRequest(
        Long authorId,
        String content
) {
}
