package com.hufsglobalion.glupshroom.domain.journey.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecallRegenerateRequest(
        @Schema(description = "요청자 ID", example = "1")
        @NotNull Long userId,

        @Schema(description = "회고 톤", example = "lively", allowableValues = {"emotional", "plain", "lively"})
        @NotBlank String tone
) {
}
