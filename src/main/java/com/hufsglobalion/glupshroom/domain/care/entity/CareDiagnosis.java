package com.hufsglobalion.glupshroom.domain.care.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "care_diagnosis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareDiagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diagnosis_id")
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "generation", nullable = false)
    private Integer generation;

    @Column(name = "condition_grade")
    private Integer conditionGrade;

    @Column(name = "result_text", columnDefinition = "TEXT")
    private String resultText;

    @Column(name = "solution_text", columnDefinition = "TEXT")
    private String solutionText;

    @Column(name = "diagnosis_photo_url", length = 500)
    private String diagnosisPhotoUrl;

    @Column(name = "is_ai_generated", nullable = false)
    private boolean aiGenerated;

    @Column(name = "diagnosed_at", nullable = false)
    private LocalDate diagnosedAt;

    @Builder
    private CareDiagnosis(Long productId, Integer generation, Integer conditionGrade,
                           String resultText, String solutionText, String diagnosisPhotoUrl,
                           boolean aiGenerated) {
        this.productId = productId;
        this.generation = generation;
        this.conditionGrade = conditionGrade;
        this.resultText = resultText;
        this.solutionText = solutionText;
        this.diagnosisPhotoUrl = diagnosisPhotoUrl;
        this.aiGenerated = aiGenerated;
    }

    @PrePersist
    private void prePersist() {
        this.diagnosedAt = LocalDate.now();
    }
}
