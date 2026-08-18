package com.hufsglobalion.glupshroom.domain.care.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "care_tip")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareTip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "care_tip_id")
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "generation", nullable = false)
    private Integer generation;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_inherit_selected", nullable = false)
    private boolean inheritSelected;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private CareTip(Long productId, Long authorId, Integer generation, String content,
                    boolean inheritSelected) {
        this.productId = productId;
        this.authorId = authorId;
        this.generation = generation;
        this.content = content;
        this.inheritSelected = inheritSelected;
    }

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
