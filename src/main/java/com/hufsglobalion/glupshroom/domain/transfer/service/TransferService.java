package com.hufsglobalion.glupshroom.domain.transfer.service;

import com.hufsglobalion.glupshroom.domain.journey.entity.Journey;
import com.hufsglobalion.glupshroom.domain.journey.repository.JourneyRepository;
import com.hufsglobalion.glupshroom.domain.ownership.entity.OwnershipHistory;
import com.hufsglobalion.glupshroom.domain.ownership.entity.OwnershipStatus;
import com.hufsglobalion.glupshroom.domain.ownership.repository.OwnershipHistoryRepository;
import com.hufsglobalion.glupshroom.domain.product.entity.Product;
import com.hufsglobalion.glupshroom.domain.product.repository.ProductRepository;
import com.hufsglobalion.glupshroom.domain.resell.entity.Resell;
import com.hufsglobalion.glupshroom.domain.resell.repository.ResellRepository;
import com.hufsglobalion.glupshroom.domain.transfer.client.OpenAiLetterDraftClient;
import com.hufsglobalion.glupshroom.domain.transfer.dto.request.LetterCreateRequest;
import com.hufsglobalion.glupshroom.domain.transfer.dto.response.LetterCreateResponse;
import com.hufsglobalion.glupshroom.domain.transfer.dto.response.LetterDraftResponse;
import com.hufsglobalion.glupshroom.domain.transfer.dto.response.TransferCompleteResponse;
import com.hufsglobalion.glupshroom.domain.transfer.dto.response.TransferCreateResponse;
import com.hufsglobalion.glupshroom.domain.transfer.entity.Transfer;
import com.hufsglobalion.glupshroom.domain.transfer.entity.TransferLetter;
import com.hufsglobalion.glupshroom.domain.transfer.entity.TransferStatus;
import com.hufsglobalion.glupshroom.domain.transfer.repository.TransferLetterRepository;
import com.hufsglobalion.glupshroom.domain.transfer.repository.TransferRepository;
import com.hufsglobalion.glupshroom.global.exception.CustomException;
import com.hufsglobalion.glupshroom.global.exception.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransferService {

    private static final String TRANSFER_TYPE_RESELL = "resell";
    private static final String TAG_SOURCE_AI_ORIGIN = "ai_origin";
    private static final List<TransferStatus> ACTIVE_TRANSFER_STATUSES =
            List.of(TransferStatus.PENDING, TransferStatus.APPROVED);

    private final TransferRepository transferRepository;
    private final TransferLetterRepository transferLetterRepository;
    private final ResellRepository resellRepository;
    private final ProductRepository productRepository;
    private final JourneyRepository journeyRepository;
    private final OwnershipHistoryRepository ownershipHistoryRepository;
    private final OpenAiLetterDraftClient openAiLetterDraftClient;

    public boolean hasOpenedLetter(Long productId, Integer generation) {
        return findOpenedLetter(productId, generation).isPresent();
    }

    public Optional<TransferLetter> findOpenedLetter(Long productId, Integer generation) {
        return transferRepository
                .findFirstByProductIdAndGenerationAndTransferStatusAndCompletedAtIsNotNullOrderByCompletedAtDesc(
                        productId,
                        generation,
                        TransferStatus.COMPLETED
                )
                .flatMap(transfer -> transferLetterRepository.findByTransferIdAndSealedFalse(transfer.getId()));
    }

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
                .generation(product.getCurrentGeneration())
                .fromUserId(sellerId)
                .toUserId(buyerId)
                .transferType(TRANSFER_TYPE_RESELL)
                .official(true)
                .transferStatus(TransferStatus.PENDING)
                .build();

        Transfer saved = transferRepository.save(transfer);
        return TransferCreateResponse.from(saved);
    }

    @Transactional
    public LetterCreateResponse writeLetter(Long transferId, LetterCreateRequest request) {
        Transfer transfer = getTransfer(transferId);

        if (!transfer.getFromUserId().equals(request.authorId())) {
            throw new CustomException(ErrorCode.LETTER_WRITE_FORBIDDEN);
        }

        if (request.content() == null || request.content().isBlank()) {
            throw new CustomException(ErrorCode.LETTER_CONTENT_REQUIRED);
        }

        if (transferLetterRepository.existsByTransferId(transfer.getId())) {
            throw new CustomException(ErrorCode.LETTER_ALREADY_WRITTEN);
        }

        TransferLetter letter = TransferLetter.builder()
                .transferId(transfer.getId())
                .authorId(request.authorId())
                .content(request.content())
                .aiDraft(request.isAiDraftOrDefault())
                .sealed(true)
                .build();

        TransferLetter saved;
        try {
            saved = transferLetterRepository.save(letter);
        } catch (DataAccessException e) {
            throw new CustomException(ErrorCode.LETTER_SAVE_FAILED);
        }

        return LetterCreateResponse.from(saved);
    }

    public LetterDraftResponse generateLetterDraft(Long transferId, Long authorId) {
        Transfer transfer = getTransfer(transferId);

        if (!transfer.getFromUserId().equals(authorId)) {
            throw new CustomException(ErrorCode.LETTER_WRITE_FORBIDDEN);
        }

        Product product = productRepository.findById(transfer.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.LETTER_DRAFT_GENERATION_FAILED));

        List<Journey> journeys = journeyRepository
                .findByProductIdAndAuthorIdOrderByJourneyYearAscJourneyMonthAsc(
                        transfer.getProductId(),
                        transfer.getFromUserId()
                );

        String draft = openAiLetterDraftClient.generateDraft(buildJourneySummary(product, journeys));
        return new LetterDraftResponse(draft);
    }

    @Transactional
    public TransferCompleteResponse completeTransfer(Long transferId, Long newOwnerId) {
        Transfer transfer = getTransfer(transferId);

        if (!newOwnerId.equals(transfer.getToUserId())) {
            throw new CustomException(ErrorCode.TRANSFER_NEW_OWNER_MISMATCH);
        }

        if (!isCompletable(transfer)) {
            throw new CustomException(ErrorCode.TRANSFER_NOT_COMPLETABLE);
        }

        try {
            boolean letterOpened = openLetterIfExists(transfer.getId());

            Product product = productRepository.findById(transfer.getProductId())
                    .orElseThrow(() -> new CustomException(ErrorCode.TRANSFER_COMPLETION_FAILED));

            Integer previousGeneration = product.getCurrentGeneration();
            closeCurrentOwnership(product.getId(), previousGeneration);

            product.completeTransfer(newOwnerId);
            openNewOwnership(product.getId(), newOwnerId, product.getCurrentGeneration());

            completeResellIfApplicable(transfer, newOwnerId);

            transfer.complete();

            return TransferCompleteResponse.of(product.getId(), product.getCurrentGeneration(), letterOpened, transfer);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.TRANSFER_COMPLETION_FAILED);
        }
    }

    private boolean isCompletable(Transfer transfer) {
        if (transfer.getTransferStatus() == TransferStatus.APPROVED) {
            return true;
        }
        return transfer.getTransferStatus() == TransferStatus.PENDING
                && TRANSFER_TYPE_RESELL.equals(transfer.getTransferType());
    }

    private boolean openLetterIfExists(Long transferId) {
        return transferLetterRepository.findFirstByTransferIdOrderByIdDesc(transferId)
                .map(letter -> {
                    letter.open();
                    return true;
                })
                .orElse(false);
    }

    private void closeCurrentOwnership(Long productId, Integer currentGeneration) {
        OwnershipHistory current = ownershipHistoryRepository
                .findByProductIdAndGeneration(productId, currentGeneration)
                .orElseThrow(() -> new CustomException(ErrorCode.TRANSFER_COMPLETION_FAILED));
        current.close(LocalDate.now());
    }

    private void completeResellIfApplicable(Transfer transfer, Long newOwnerId) {
        if (!TRANSFER_TYPE_RESELL.equals(transfer.getTransferType())) {
            return;
        }

        Resell resell = resellRepository
                .findByProductIdAndSellerIdAndPostStatus(transfer.getProductId(), transfer.getFromUserId(), "active")
                .orElseThrow(() -> new CustomException(ErrorCode.TRANSFER_COMPLETION_FAILED));
        resell.completePurchase(newOwnerId);
    }

    private void openNewOwnership(Long productId, Long newOwnerId, Integer newGeneration) {
        OwnershipHistory newOwnership = OwnershipHistory.builder()
                .productId(productId)
                .ownerId(newOwnerId)
                .generation(newGeneration)
                .ownedFrom(LocalDate.now())
                .ownershipStatus(OwnershipStatus.OWNING)
                .build();
        ownershipHistoryRepository.save(newOwnership);
    }

    private Transfer getTransfer(Long transferId) {
        return transferRepository.findById(transferId)
                .orElseThrow(() -> new CustomException(ErrorCode.TRANSFER_NOT_FOUND));
    }

    private String buildJourneySummary(Product product, List<Journey> journeys) {
        StringBuilder summary = new StringBuilder();
        summary.append("제품명: ").append(product.getOfficialName());
        if (product.getNickname() != null) {
            summary.append(" (별칭: ").append(product.getNickname()).append(")");
        }
        summary.append("\n여정 기록:\n");

        if (journeys.isEmpty()) {
            summary.append("- 기록된 여정이 없습니다.\n");
            return summary.toString();
        }

        for (Journey journey : journeys) {
            summary.append("- ");
            if (journey.getJourneyYear() != null) {
                summary.append(journey.getJourneyYear()).append("년 ");
            }
            if (journey.getJourneyMonth() != null) {
                summary.append(journey.getJourneyMonth()).append("월, ");
            }
            if (journey.getCountry() != null) {
                summary.append(journey.getCountry());
            }
            if (journey.getCity() != null) {
                summary.append(" ").append(journey.getCity());
            }
            if (isInheritableTag(journey.getActivityTag(), journey.getActivitySource())) {
                summary.append(" / ").append(journey.getActivityTag());
            }
            if (isInheritableTag(journey.getSituationTag(), journey.getSituationSource())) {
                summary.append(" / ").append(journey.getSituationTag());
            }
            if (isInheritableTag(journey.getStyleTag(), journey.getStyleSource())) {
                summary.append(" / ").append(journey.getStyleTag());
            }
            if (journey.getRecallText() != null) {
                summary.append(" - ").append(journey.getRecallText());
            }
            summary.append("\n");
        }

        return summary.toString();
    }

    private boolean isInheritableTag(String tag, String source) {
        return tag != null && TAG_SOURCE_AI_ORIGIN.equals(source);
    }
}
