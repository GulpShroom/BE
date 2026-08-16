package com.hufsglobalion.glupshroom.domain.ownership.repository;

import com.hufsglobalion.glupshroom.domain.ownership.entity.OwnershipHistory;
import com.hufsglobalion.glupshroom.domain.ownership.entity.OwnershipStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnershipHistoryRepository extends JpaRepository<OwnershipHistory, Long> {

    List<OwnershipHistory> findByProductIdOrderByGenerationAsc(Long productId);

    List<OwnershipHistory> findByOwnerId(Long ownerId);

    List<OwnershipHistory> findByOwnerIdAndOwnershipStatus(
            Long ownerId,
            OwnershipStatus ownershipStatus
    );

    Optional<OwnershipHistory> findFirstByProductIdAndOwnerIdAndOwnershipStatusOrderByGenerationDesc(
            Long productId,
            Long ownerId,
            OwnershipStatus ownershipStatus
    );
}
