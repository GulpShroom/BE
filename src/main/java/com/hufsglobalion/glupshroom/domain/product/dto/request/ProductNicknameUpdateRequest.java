package com.hufsglobalion.glupshroom.domain.product.dto.request;

public record ProductNicknameUpdateRequest(
        Long userId,
        String nickname
) {
}
