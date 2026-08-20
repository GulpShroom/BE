package com.hufsglobalion.glupshroom.domain.care.dto.response;

import java.time.LocalDate;
import java.util.List;

public record CareDiagnosisHistoryResponse(
        List<DiagnosisSummary> diagnoses
) {
    public record DiagnosisSummary(
            Long diagnosisId,
            Integer generation,
            LocalDate diagnosedAt,
            Integer conditionGrade,
            String resultText,
            String solutionText
    ) {
    }
}
