package com.hufsglobalion.glupshroom.domain.resell.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "resell_post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Resell {

    private static final String STATUS_ACTIVE = "active";
    private static final String STATUS_COMPLETED = "completed";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resell_id")
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "buyer_id")
    private Long buyerId;

    @Column(nullable = false)
    private Long price;

    @Column(name = "condition_grade", length = 10)
    private String conditionGrade;

    @Column(name = "post_status", nullable = false, length = 20)
    private String postStatus;

    @Column(name = "letter_shared", nullable = false)
    private boolean letterShared;

    @Column(name = "caretip_shared", nullable = false)
    private boolean caretipShared;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Resell(Long productId, Long sellerId, Long price, String conditionGrade,
                  boolean letterShared, boolean caretipShared) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.price = price;
        this.conditionGrade = conditionGrade;
        this.letterShared = letterShared;
        this.caretipShared = caretipShared;
        this.postStatus = STATUS_ACTIVE;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isSelling() {
        return STATUS_ACTIVE.equals(postStatus);
    }

    public void updateDetails(Long price, String conditionGrade, Boolean letterShared, Boolean caretipShared) {
        if (price != null) this.price = price;
        if (conditionGrade != null) this.conditionGrade = conditionGrade;
        if (letterShared != null) this.letterShared = letterShared;
        if (caretipShared != null) this.caretipShared = caretipShared;
    }

    public void completePurchase(Long buyerId) {
        this.buyerId = buyerId;
        this.postStatus = STATUS_COMPLETED;
    }
}
