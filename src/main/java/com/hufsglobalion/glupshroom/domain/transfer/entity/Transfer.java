package com.hufsglobalion.glupshroom.domain.transfer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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

@Entity
@Table(name = "transfer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_id")
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "generation", nullable = false)
    private Integer generation;

    @Column(name = "from_user_id", nullable = false)
    private Long fromUserId;

    @Column(name = "to_user_id")
    private Long toUserId;

    @Column(name = "transfer_type", nullable = false, length = 20)
    private String transferType;

    @Column(name = "is_official", nullable = false)
    private boolean official;

    @Convert(converter = TransferStatusConverter.class)
    @Column(name = "transfer_status", nullable = false, length = 20)
    private TransferStatus transferStatus;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder
    private Transfer(Long productId, Integer generation, Long fromUserId, Long toUserId, String transferType,
                      boolean official, TransferStatus transferStatus) {
        this.productId = productId;
        this.generation = generation;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.transferType = transferType;
        this.official = official;
        this.transferStatus = transferStatus;
    }

    @PrePersist
    public void prePersist() {
        this.requestedAt = LocalDateTime.now();
    }
}
