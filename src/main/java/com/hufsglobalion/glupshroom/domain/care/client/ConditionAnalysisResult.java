package com.hufsglobalion.glupshroom.domain.care.client;

public record ConditionAnalysisResult(
        Integer conditionGrade,
        String resultText,
        String solutionText
) {
}
