package com.hufsglobalion.glupshroom.domain.journey.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record JourneyAnalyzeResponse(
        @Schema(description = "국가", example = "일본")
        String country,

        @Schema(description = "도시", example = "도쿄")
        String city,

        @Schema(description = "위도", example = "35.6895")
        Double latitude,

        @Schema(description = "경도", example = "139.6917")
        Double longitude,

        @Schema(description = "연도", example = "2026")
        Integer year,

        @Schema(description = "월", example = "4")
        Integer month,

        @Schema(description = "계절", example = "spring")
        String season,

        @Schema(description = "EXIF 촬영일", example = "2026-04-10T09:30:00")
        LocalDateTime exifTakenAt,

        @Schema(description = "활동 태그", example = "여행")
        String activityTag,

        @Schema(description = "상황 태그", example = "출장")
        String situationTag,

        @Schema(description = "스타일 태그", example = "캐주얼")
        String styleTag,

        @Schema(description = "AI 회고 문장", example = "도쿄의 벚꽃, 첫 출장을 함께한 날")
        String recallText,

        @Schema(description = "적용된 톤", example = "emotional", allowableValues = {"emotional", "plain", "lively"})
        String recallTone,

        @Schema(description = "검증 상태 (저장 전 예상 상태)", example = "verified", allowableValues = {"verified", "unverified", "need_check"})
        String verifyStatus,

        @Schema(description = "검증 확률(%)", example = "96")
        Integer verifyConfidence
) {
}
