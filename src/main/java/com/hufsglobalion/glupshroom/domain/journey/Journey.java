package com.hufsglobalion.glupshroom.domain.journey;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "journey")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Journey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String photoUrl;

    @Column(nullable = false)
    private String country;

    private String city;

    @Column(nullable = false)
    private Integer journeyYear;

    private Integer journeyMonth;

    private String activityTag;
    private String activityTagSource;

    private String situationTag;
    private String situationTagSource;

    private String styleTag;
    private String styleTagSource;

    @Lob
    private String recallText;

    private String recallTone;

    @Lob
    private String userMemo;

    private String verifyStatus;      // TODO: EXIF 검증 붙이기 전까진 기본값(PENDING 등)
    private Double verifyConfidence;  // TODO: 위와 동일

    private Integer generation;       // TODO: Product의 current_generation 연동 필요
    private boolean isFirstJourney;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Journey(Long userId, Long productId, String photoUrl, String country, String city,
                   Integer journeyYear, Integer journeyMonth,
                   String activityTag, String activityTagSource,
                   String situationTag, String situationTagSource,
                   String styleTag, String styleTagSource,
                   String recallText, String recallTone, String userMemo,
                   String verifyStatus, Double verifyConfidence,
                   Integer generation, boolean isFirstJourney) {
        this.userId = userId;
        this.productId = productId;
        this.photoUrl = photoUrl;
        this.country = country;
        this.city = city;
        this.journeyYear = journeyYear;
        this.journeyMonth = journeyMonth;
        this.activityTag = activityTag;
        this.activityTagSource = activityTagSource;
        this.situationTag = situationTag;
        this.situationTagSource = situationTagSource;
        this.styleTag = styleTag;
        this.styleTagSource = styleTagSource;
        this.recallText = recallText;
        this.recallTone = recallTone;
        this.userMemo = userMemo;
        this.verifyStatus = verifyStatus;
        this.verifyConfidence = verifyConfidence;
        this.generation = generation;
        this.isFirstJourney = isFirstJourney;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}