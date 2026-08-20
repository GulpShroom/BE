package com.hufsglobalion.glupshroom.domain.journey.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record RecallRegenerateResponse(
        @Schema(description = "재생성된 회고 문장", example = "벚꽃 흩날리던 도쿄, 첫 출장 성공!")
        String recallText,

        @Schema(description = "적용된 톤", example = "lively", allowableValues = {"emotional", "plain", "lively"})
        String recallTone
) {
}
