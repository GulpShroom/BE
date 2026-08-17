package com.hufsglobalion.glupshroom.domain.ownership.service;

import com.hufsglobalion.glupshroom.domain.ownership.entity.OwnershipHistory;
import com.hufsglobalion.glupshroom.domain.ownership.entity.OwnershipStatus;
import com.hufsglobalion.glupshroom.domain.ownership.repository.OwnershipHistoryRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OwnershipService {

    private final OwnershipHistoryRepository ownershipHistoryRepository;

    public List<OwnershipHistory> findProductOwnershipHistories(Long productId) {
        return ownershipHistoryRepository.findByProductIdOrderByGenerationAsc(productId);
    }

    public Optional<OwnershipHistory> findProductOwnershipHistory(Long productId, Integer generation) {
        return ownershipHistoryRepository.findByProductIdAndGeneration(productId, generation);
    }

    public long countKeepers(Long productId) {
        return ownershipHistoryRepository.countByProductId(productId);
    }

    public List<OwnershipHistory> findOwnershipHistories(Long ownerId) {
        return ownershipHistoryRepository.findByOwnerId(ownerId);
    }

    public List<OwnershipHistory> findOwnershipHistories(Long ownerId, OwnershipStatus ownershipStatus) {
        return ownershipHistoryRepository.findByOwnerIdAndOwnershipStatus(ownerId, ownershipStatus);
    }

    public Optional<OwnershipHistory> findLatestTransferredOwnership(Long productId, Long ownerId) {
        return ownershipHistoryRepository
                .findFirstByProductIdAndOwnerIdAndOwnershipStatusOrderByGenerationDesc(
                        productId,
                        ownerId,
                        OwnershipStatus.TRANSFERRED
                );
    }
}
