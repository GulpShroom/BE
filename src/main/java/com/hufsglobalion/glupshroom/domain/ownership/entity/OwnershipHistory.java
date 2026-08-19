package com.hufsglobalion.glupshroom.domain.ownership.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ownership_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OwnershipHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ownership_id")
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "generation", nullable = false)
    private Integer generation;

    @Column(name = "owned_from", nullable = false)
    private LocalDate ownedFrom;

    @Column(name = "owned_to")
    private LocalDate ownedTo;

    @Convert(converter = OwnershipStatusConverter.class)
    @Column(name = "ownership_status", nullable = false, length = 20)
    private OwnershipStatus ownershipStatus;

    @Builder
    private OwnershipHistory(Long productId, Long ownerId, Integer generation, LocalDate ownedFrom,LocalDate ownedTo,
                              OwnershipStatus ownershipStatus) {
        this.productId = productId;
        this.ownerId = ownerId;
        this.generation = generation;
        this.ownedFrom = ownedFrom;
        this.ownedTo = ownedTo;
        this.ownershipStatus = ownershipStatus;
    }

    public void close(LocalDate ownedTo) {
        this.ownedTo = ownedTo;
        this.ownershipStatus = OwnershipStatus.TRANSFERRED;
    }
}
