package com.hufsglobalion.glupshroom.domain.transfer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record LetterDraftRequest(
        @Schema(description = "작성자(전 주인) ID", example = "1")
        @NotNull Long authorId
) {
}
