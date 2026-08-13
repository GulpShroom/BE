package com.hufsglobalion.glupshroom.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProfileSelectRequest(
        @Schema(description = "선택한 프로필 타입", example = "first_keeper", allowableValues = {"first_keeper", "next_keeper"})
        String profileType
) {
}
