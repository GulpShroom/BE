package com.hufsglobalion.glupshroom.domain.transfer.dto.response;

import com.hufsglobalion.glupshroom.domain.transfer.entity.TransferLetter;
import io.swagger.v3.oas.annotations.media.Schema;

public record LetterCreateResponse(
        @Schema(description = "생성된 편지 ID", example = "5")
        Long letterId,

        @Schema(description = "봉인 여부", example = "true")
        boolean isSealed,

        @Schema(description = "LLM 초안 사용 여부", example = "false")
        boolean isAiDraft
) {

    public static LetterCreateResponse from(TransferLetter letter) {
        return new LetterCreateResponse(letter.getId(), letter.isSealed(), letter.isAiDraft());
    }
}
