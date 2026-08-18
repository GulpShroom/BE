package com.hufsglobalion.glupshroom.domain.transfer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LetterDraftResponse(
        @Schema(description = "LLM이 생성한 편지 초안", example = "이 가방과 함께한 4년의 시간들이 떠오르네요...")
        String draftContent
) {
}
