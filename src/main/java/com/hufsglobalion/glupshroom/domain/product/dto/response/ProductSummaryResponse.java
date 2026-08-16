package com.hufsglobalion.glupshroom.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductSummaryResponse(
        @Schema(description = "제품 ID", example = "1")
        Long productId,

        @Schema(description = "제품 별칭", example = "출근백")
        String nickname,

        @Schema(description = "제품 공식명", example = "MCM 스타크 백팩")
        String officialName,

        @Schema(description = "정품 인증 여부", example = "true")
        boolean isAuthenticated,

        @Schema(description = "제품에 등록된 전체 여정 기록 수", example = "12")
        Integer journeyCount,

        @Schema(description = "프로비넌스 스코어", example = "87", nullable = true)
        Integer provenanceScore,

        @Schema(description = "프로비넌스 표시 상태", example = "calculated", allowableValues = {"calculated", "calculating", "insufficient"})
        String provenanceStatus,

        @Schema(description = "계보에 포함된 전체 Keeper 세대 수", example = "3")
        Integer keeperCount
) {
}
