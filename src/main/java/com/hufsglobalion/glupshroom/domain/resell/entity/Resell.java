package com.hufsglobalion.glupshroom.domain.resell.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="resell_post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Resell {

    private static final String STATUS_ACTIVE = "active";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="resell_id")
    private Long id;

    @Column(name="product_id", nullable = false)
    private Long productId;

    @Column(name="seller_id", nullable = false)
    private Long sellerId;

    @Column(name="buyer_id", nullable = true)
    private Long buyerId;

    @Column(nullable = false)
    private Long price;

    @Column(name="condition_grade", nullable = false, length = 10)
    private String conditionGrade;

    @Column(name="post_status", nullable = false)
    private String postStatus;

    @Column(name="letter_shared", nullable = false)
    private boolean letterShared;

    @Column(name="caretip_shared", nullable = false)
    private boolean caretipShared;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    public boolean isSelling() {
        return STATUS_ACTIVE.equals(postStatus);
    }
}
