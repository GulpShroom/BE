package com.hufsglobalion.glupshroom.domain.transfer.service;

import com.hufsglobalion.glupshroom.domain.transfer.entity.TransferLetter;
import com.hufsglobalion.glupshroom.domain.transfer.entity.TransferStatus;
import com.hufsglobalion.glupshroom.domain.transfer.repository.TransferLetterRepository;
import com.hufsglobalion.glupshroom.domain.transfer.repository.TransferRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransferService {

    private final TransferRepository transferRepository;
    private final TransferLetterRepository transferLetterRepository;

    public Optional<TransferLetter> findLatestOpenedLetter(Long productId, Long userId) {
        return transferRepository
                .findFirstByProductIdAndToUserIdAndTransferStatusAndCompletedAtIsNotNullOrderByCompletedAtDesc(
                        productId,
                        userId,
                        TransferStatus.COMPLETED
                )
                .flatMap(transfer -> transferLetterRepository.findByTransferIdAndSealedFalse(transfer.getId()));
    }
}
