package com.hufsglobalion.glupshroom.domain.resell.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ResellInheritPreviewResponse(
        @Schema(description = "자동 계승분 (AI 원본·드롭다운값, 항상 넘어감)")
        AutoInherit autoInherit,

        @Schema(description = "선택 계승분 (자유텍스트 수정분, 체크 시 넘어감)")
        List<SelectableItem> selectableItems
) {

    public record AutoInherit(
            @Schema(description = "계승될 여정 총 개수", example = "32")
            Integer journeyCount,

            @Schema(description = "대표 회고 1개(안내용)", example = "도쿄의 벚꽃, 첫 출장을 함께한 날")
            String sampleRecall,

            @Schema(description = "대표 도시 목록(distinct)", example = "[\"파리\", \"도쿄\"]")
            List<String> cities,

            @Schema(description = "대표 외 나머지 도시 수", example = "6")
            Integer cityMoreCount,

            @Schema(description = "검증 비율(%)", example = "88")
            Integer verifiedRatio
    ) {
    }

    public record SelectableItem(
            @Schema(description = "여정 ID", example = "2")
            Long journeyId,

            @Schema(description = "도시(맥락 표시용)", example = "도쿄")
            String city,

            @Schema(description = "연도(맥락 표시용)", example = "2026")
            Integer year,

            @Schema(description = "회고(어느 여정인지 식별용)", example = "도쿄의 벚꽃, 첫 출장을 함께한 날")
            String recallText,

            @Schema(description = "이 여정에서 자유텍스트로 수정한 태그들")
            List<ModifiedTag> modifiedTags
    ) {
    }

    public record ModifiedTag(
            @Schema(description = "태그 종류", example = "style", allowableValues = {"activity", "situation", "style"})
            String type,

            @Schema(description = "수정된 값", example = "빈티지룩")
            String value
    ) {
    }
}
