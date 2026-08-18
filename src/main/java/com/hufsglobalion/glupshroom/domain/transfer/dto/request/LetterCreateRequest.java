package com.hufsglobalion.glupshroom.domain.transfer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record LetterCreateRequest(
        @Schema(description = "작성자 ID", example = "1")
        @NotNull Long authorId,

        @Schema(description = "편지 내용", example = "이 백과 함께한 4년...")
        String content,

        @Schema(description = "LLM 초안 사용 여부", example = "false")
        Boolean isAiDraft
) {

    public boolean isAiDraftOrDefault() {
        return Boolean.TRUE.equals(isAiDraft);
    }
}
