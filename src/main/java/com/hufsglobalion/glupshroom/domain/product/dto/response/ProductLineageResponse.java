package com.hufsglobalion.glupshroom.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

public record ProductLineageResponse(
        @Schema(description = "제품 ID", example = "1")
        Long productId,

        @Schema(description = "세대별 소유 이력")
        List<Generation> generations
) {

    public record Generation(
            @Schema(description = "소유 이력 ID", example = "10")
            Long ownershipId,

            @Schema(description = "세대 번호", example = "1")
            Integer generation,

            @Schema(description = "화면 표시용 Keeper 라벨", example = "1st Keeper")
            String keeperLabel,

            @Schema(description = "현재 소유 세대 여부", example = "false")
            boolean isCurrentOwner,

            @Schema(description = "소유 시작일", example = "2019-04-12")
            LocalDate ownedFrom,

            @Schema(description = "소유 종료일", example = "2022-08-24", nullable = true)
            LocalDate ownedTo,

            @Schema(description = "화면 표시용 소유 기간", example = "2022 ~ 2026")
            String durationText,

            @Schema(description = "개봉된 계승 편지 존재 여부", example = "true")
            boolean hasOpenedLetter
    ) {
    }
}
