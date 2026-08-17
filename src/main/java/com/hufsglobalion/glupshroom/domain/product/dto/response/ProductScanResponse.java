package com.hufsglobalion.glupshroom.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record ProductScanResponse(
        @Schema(description = "디지털 여권으로 이미 등록된 제품 여부", example = "false")
        boolean isRegistered,

        @Schema(description = "제품 시리얼 번호", example = "MCM-2026-001")
        String serialNo,

        @Schema(description = "제품 공식명", example = "MCM 스타크 백팩")
        String officialName,

        @Schema(description = "제품 공식 이미지 URL", example = "https://cdn.mcarry/products/stark.jpg")
        String officialImageUrl,

        @Schema(description = "제조연도", example = "2024", nullable = true)
        Integer manufactureYear,

        @Schema(description = "제품 라인", example = "비세토스", nullable = true)
        String productLine,

        @Schema(description = "제품 색상", example = "코냑", nullable = true)
        String color,

        @Schema(description = "정품 인증일", example = "2026-08-11", nullable = true)
        LocalDate authenticatedAt
) {
}
