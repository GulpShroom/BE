package com.hufsglobalion.glupshroom.domain.journey.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "journey")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Journey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "journey_id")
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "generation", nullable = false)
    private Integer generation;

    @Column(name = "is_first_journey", nullable = false)
    private boolean firstJourney;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "journey_year")
    private Integer journeyYear;

    @Column(name = "journey_month")
    private Integer journeyMonth;

    @Column(name = "season", length = 10)
    private String season;

    @Column(name = "exif_taken_at")
    private LocalDateTime exifTakenAt;

    @Column(name = "activity_tag", length = 100)
    private String activityTag;

    @Column(name = "activity_source", length = 20)
    private String activitySource;

    @Column(name = "situation_tag", length = 100)
    private String situationTag;

    @Column(name = "situation_source", length = 20)
    private String situationSource;

    @Column(name = "style_tag", length = 100)
    private String styleTag;

    @Column(name = "style_source", length = 20)
    private String styleSource;

    @Column(name = "recall_text", columnDefinition = "TEXT")
    private String recallText;

    @Column(name = "recall_tone", length = 20)
    private String recallTone;

    @Column(name = "user_memo", columnDefinition = "TEXT")
    private String userMemo;

    @Column(name = "verify_status", nullable = false, length = 20)
    private String verifyStatus;

    @Column(name = "verify_confidence")
    private Integer verifyConfidence;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Journey(Long authorId, Long productId, String photoUrl, String country, String city,
                   Integer journeyYear, Integer journeyMonth, String season,
                   String activityTag, String activitySource,
                   String situationTag, String situationSource,
                   String styleTag, String styleSource,
                   String recallText, String recallTone, String userMemo,
                   String verifyStatus, Integer verifyConfidence,
                   Integer generation, boolean firstJourney) {
        this.authorId = authorId;
        this.productId = productId;
        this.photoUrl = photoUrl;
        this.country = country;
        this.city = city;
        this.journeyYear = journeyYear;
        this.journeyMonth = journeyMonth;
        this.season = season;
        this.activityTag = activityTag;
        this.activitySource = activitySource;
        this.situationTag = situationTag;
        this.situationSource = situationSource;
        this.styleTag = styleTag;
        this.styleSource = styleSource;
        this.recallText = recallText;
        this.recallTone = recallTone;
        this.userMemo = userMemo;
        this.verifyStatus = verifyStatus;
        this.verifyConfidence = verifyConfidence;
        this.generation = generation;
        this.firstJourney = firstJourney;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}