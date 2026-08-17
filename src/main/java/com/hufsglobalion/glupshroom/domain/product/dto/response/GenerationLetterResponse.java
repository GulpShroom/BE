package com.hufsglobalion.glupshroom.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record GenerationLetterResponse(
        @Schema(description = "제품 ID", example = "1")
        Long productId,

        @Schema(description = "편지를 조회할 Keeper 세대 번호", example = "1")
        Integer generation,

        @Schema(description = "편지와 연결된 계승 ID", example = "7")
        Long transferId,

        @Schema(description = "편지 ID", example = "5")
        Long letterId,

        @Schema(description = "편지 내용", example = "이 가방과 함께한 첫 출장의 기억을 남겨요.")
        String content,

        @Schema(description = "편지 개봉 시각", example = "2026-08-24T14:00:00+09:00")
        OffsetDateTime openedAt
) {
}
