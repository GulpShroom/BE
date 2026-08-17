package com.hufsglobalion.glupshroom.domain.transfer.repository;

import com.hufsglobalion.glupshroom.domain.transfer.entity.Transfer;
import com.hufsglobalion.glupshroom.domain.transfer.entity.TransferStatus;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Optional<Transfer> findFirstByProductIdAndGenerationAndTransferStatusAndCompletedAtIsNotNullOrderByCompletedAtDesc(
            Long productId,
            Integer generation,
            TransferStatus transferStatus
    );

    Optional<Transfer> findFirstByProductIdAndToUserIdAndTransferStatusAndCompletedAtIsNotNullOrderByCompletedAtDesc(
            Long productId,
            Long toUserId,
            TransferStatus transferStatus
    );

    boolean existsByProductIdAndTransferStatusIn(Long productId, Collection<TransferStatus> transferStatuses);
}
