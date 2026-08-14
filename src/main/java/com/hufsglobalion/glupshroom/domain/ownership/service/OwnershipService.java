package com.hufsglobalion.glupshroom.domain.ownership.service;

import com.hufsglobalion.glupshroom.domain.ownership.entity.OwnershipHistory;
import com.hufsglobalion.glupshroom.domain.ownership.entity.OwnershipStatus;
import com.hufsglobalion.glupshroom.domain.ownership.repository.OwnershipHistoryRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OwnershipService {

    private final OwnershipHistoryRepository ownershipHistoryRepository;

    public Optional<OwnershipHistory> findLatestTransferredOwnership(Long productId, Long ownerId) {
        return ownershipHistoryRepository
                .findFirstByProductIdAndOwnerIdAndOwnershipStatusOrderByGenerationDesc(
                        productId,
                        ownerId,
                        OwnershipStatus.TRANSFERRED
                );
    }
}
