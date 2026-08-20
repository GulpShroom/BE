package com.hufsglobalion.glupshroom.domain.care.dto.response;

import java.time.LocalDate;

public record CareDiagnosisResponse(
        Long diagnosisId,
        Integer generation,
        LocalDate diagnosedAt,
        Integer conditionGrade,
        String resultText,
        String solutionText,
        String repairLinkUrl
) {
}
