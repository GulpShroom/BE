package com.hufsglobalion.glupshroom.domain.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductScanRequest(
        @Schema(description = "제품 시리얼 번호", example = "MCM-2026-001", nullable = true)
        String serialNo,

        @Schema(description = "QR 코드 원문 또는 QR에서 추출한 코드", example = "MCM-2026-001", nullable = true)
        String qrCode
) {
}
