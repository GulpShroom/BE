package com.hufsglobalion.glupshroom.domain.transfer.service;

import com.hufsglobalion.glupshroom.domain.product.entity.Product;
import com.hufsglobalion.glupshroom.domain.product.repository.ProductRepository;
import com.hufsglobalion.glupshroom.domain.resell.entity.Resell;
import com.hufsglobalion.glupshroom.domain.resell.repository.ResellRepository;
import com.hufsglobalion.glupshroom.domain.transfer.dto.response.TransferCreateResponse;
import com.hufsglobalion.glupshroom.domain.transfer.entity.Transfer;
import com.hufsglobalion.glupshroom.domain.transfer.entity.TransferLetter;
import com.hufsglobalion.glupshroom.domain.transfer.entity.TransferStatus;
import com.hufsglobalion.glupshroom.domain.transfer.repository.TransferLetterRepository;
import com.hufsglobalion.glupshroom.domain.transfer.repository.TransferRepository;
import com.hufsglobalion.glupshroom.global.exception.CustomException;
import com.hufsglobalion.glupshroom.global.exception.ErrorCode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
// TODO: 계승 완료(complete) API 구현 시 ownership_history row 생성/갱신 로직 추가 필요.
// 지금은 이게 없어서 journey-list(BE-#8)의 ownershipStatus, product-lineage(BE-#12)의
// generations가 항상 기본값/빈 값으로만 나옴.
public class TransferService {

    private static final String TRANSFER_TYPE_RESELL = "resell";
    private static final List<TransferStatus> ACTIVE_TRANSFER_STATUSES =
            List.of(TransferStatus.PENDING, TransferStatus.APPROVED);

    private final TransferRepository transferRepository;
    private final TransferLetterRepository transferLetterRepository;
    private final ResellRepository resellRepository;
    private final ProductRepository productRepository;

    public Optional<TransferLetter> findLatestOpenedLetter(Long productId, Long userId) {
        return transferRepository
                .findFirstByProductIdAndToUserIdAndTransferStatusAndCompletedAtIsNotNullOrderByCompletedAtDesc(
                        productId,
                        userId,
                        TransferStatus.COMPLETED
                )
                .flatMap(transfer -> transferLetterRepository.findByTransferIdAndSealedFalse(transfer.getId()));
    }

    @Transactional
    public TransferCreateResponse createResellTransfer(Long resellId, Long buyerId) {
        Resell resell = resellRepository.findById(resellId)
                .filter(Resell::isSelling)
                .orElseThrow(() -> new CustomException(ErrorCode.RESELL_NOT_FOUND));

        Product product = productRepository.findById(resell.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.TRANSFER_CREATION_FAILED));

        Long sellerId = product.getCurrentOwnerId();
        if (sellerId.equals(buyerId)) {
            throw new CustomException(ErrorCode.CANNOT_BUY_OWN_PRODUCT);
        }

        if (transferRepository.existsByProductIdAndTransferStatusIn(product.getId(), ACTIVE_TRANSFER_STATUSES)) {
            throw new CustomException(ErrorCode.TRANSFER_ALREADY_IN_PROGRESS);
        }

        Transfer transfer = Transfer.builder()
                .productId(product.getId())
                .fromUserId(sellerId)
                .toUserId(buyerId)
                .transferType(TRANSFER_TYPE_RESELL)
                .official(true)
                .transferStatus(TransferStatus.PENDING)
                .build();

        Transfer saved = transferRepository.save(transfer);
        return TransferCreateResponse.from(saved);
    }
}
