package com.hufsglobalion.glupshroom.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;

public record MyProductListResponse(
        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "적용된 소유 상태 조회 조건", example = "owning")
        String status,

        @Schema(description = "제품 목록")
        List<ProductItem> products
) {

    public record ProductItem(
            @Schema(description = "제품 ID", example = "1")
            Long productId,

            @Schema(description = "디지털 여권 ID", example = "DP-20260823")
            String passportId,

            @Schema(description = "제품 별칭", example = "출근백")
            String nickname,

            @Schema(description = "제품 공식명", example = "MCM 스타크 백팩")
            String officialName,

            @Schema(description = "제품 공식 이미지 URL", example = "https://cdn.mcarry/products/stark.jpg")
            String officialImageUrl,

            @Schema(description = "사용자가 해당 제품을 소유한 세대", example = "1")
            Integer generation,

            @Schema(description = "해당 제품에 기록된 본인 여정 수", example = "12")
            Integer journeyCount,

            @Schema(description = "소유 상태", example = "owning")
            String ownershipStatus,

            @Schema(description = "조회 가능한 계승 편지 프리뷰")
            InheritanceLetterPreview inheritanceLetter,

            @Schema(description = "제품 등록 시각", example = "2026-08-24T10:30:00+09:00")
            OffsetDateTime registeredAt
    ) {
    }

    public record InheritanceLetterPreview(
            @Schema(description = "계승 편지 ID", example = "5")
            Long letterId,

            @Schema(description = "편지를 남긴 Keeper 라벨", example = "1st Keeper")
            String fromKeeperLabel,

            @Schema(description = "계승 편지 내용", example = "이 백과 함께한 첫 출장이 아직도 기억나요.")
            String content,

            @Schema(description = "편지 개봉 시각", example = "2026-08-24T14:00:00+09:00")
            OffsetDateTime openedAt
    ) {
    }
}
