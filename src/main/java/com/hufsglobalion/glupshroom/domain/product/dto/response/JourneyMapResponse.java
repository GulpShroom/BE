package com.hufsglobalion.glupshroom.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record JourneyMapResponse(
        @Schema(description = "요청자 권한", example = "owner", allowableValues = {"owner", "lineage", "visitor"})
        String viewerRole,

        @Schema(description = "적용된 줌 레벨", example = "district", allowableValues = {"world", "country", "district"})
        String zoom,

        @Schema(description = "개별 마커 목록 (zoom=district일 때만 채워짐)")
        List<Marker> markers,

        @Schema(description = "지역별 집계 (zoom=world/country일 때만 채워짐, district면 null)")
        List<Aggregate> aggregates
) {

    public record Marker(
            @Schema(description = "여정 ID", example = "2")
            Long journeyId,

            @Schema(description = "작성 세대", example = "1")
            Integer generation,

            @Schema(description = "본인 여정 여부", example = "true")
            boolean isOwn,

            @Schema(description = "도시 (null이면 국가 중앙)", example = "도쿄")
            String city,

            @Schema(description = "위도", example = "35.6895")
            Double latitude,

            @Schema(description = "경도", example = "139.6917")
            Double longitude,

            @Schema(description = "썸네일 (본인=원본 사진, 타 세대=제품 공식 사진)", example = "https://cdn.mcarry/user/p1.jpg")
            String thumbnailUrl,

            @Schema(description = "AI 회고 문장", example = "도쿄의 벚꽃, 첫 출장을 함께한 날")
            String recallText,

            @Schema(description = "검증 상태", example = "verified", allowableValues = {"verified", "unverified", "need_check"})
            String verifyStatus
    ) {
    }

    public record Aggregate(
            @Schema(description = "지역명 (world면 국가, country면 도시)", example = "일본")
            String region,

            @Schema(description = "해당 지역의 여정 수", example = "5")
            long count
    ) {
    }
}
