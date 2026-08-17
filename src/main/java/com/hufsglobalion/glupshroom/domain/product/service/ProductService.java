package com.hufsglobalion.glupshroom.domain.product.service;

import com.hufsglobalion.glupshroom.domain.journey.service.JourneyService;
import com.hufsglobalion.glupshroom.domain.ownership.entity.OwnershipHistory;
import com.hufsglobalion.glupshroom.domain.ownership.entity.OwnershipStatus;
import com.hufsglobalion.glupshroom.domain.ownership.service.OwnershipService;
import com.hufsglobalion.glupshroom.domain.product.dto.request.ProductListStatus;
import com.hufsglobalion.glupshroom.domain.product.dto.response.GenerationLetterResponse;
import com.hufsglobalion.glupshroom.domain.product.dto.response.MyProductListResponse;
import com.hufsglobalion.glupshroom.domain.product.dto.response.MyProductListResponse.InheritanceLetterPreview;
import com.hufsglobalion.glupshroom.domain.product.dto.response.MyProductListResponse.ProductItem;
import com.hufsglobalion.glupshroom.domain.product.dto.response.ProductLineageResponse;
import com.hufsglobalion.glupshroom.domain.product.dto.response.ProductLineageResponse.Generation;
import com.hufsglobalion.glupshroom.domain.product.dto.response.ProductSummaryResponse;
import com.hufsglobalion.glupshroom.domain.product.entity.Product;
import com.hufsglobalion.glupshroom.domain.product.repository.ProductRepository;
import com.hufsglobalion.glupshroom.domain.transfer.entity.TransferLetter;
import com.hufsglobalion.glupshroom.domain.transfer.service.TransferService;
import com.hufsglobalion.glupshroom.domain.user.service.UserService;
import com.hufsglobalion.glupshroom.global.exception.CustomException;
import com.hufsglobalion.glupshroom.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private static final ZoneOffset KST_OFFSET = ZoneOffset.ofHours(9);
    private static final long MINIMUM_JOURNEY_COUNT_FOR_PROVENANCE = 3L;

    private final ProductRepository productRepository;
    private final UserService userService;
    private final OwnershipService ownershipService;
    private final JourneyService journeyService;
    private final TransferService transferService;

    public MyProductListResponse getMyProductList(Long userId, String statusValue) {
        ProductListStatus status = ProductListStatus.from(statusValue)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_PRODUCT_LIST_STATUS));

        userService.getUser(userId);

        List<ProductItem> products = findOwnershipHistories(userId, status).stream()
                .map(ownershipHistory -> toProductItem(userId, ownershipHistory))
                .sorted(Comparator.comparing(ProductItem::registeredAt).reversed())
                .toList();

        return new MyProductListResponse(userId, status.getValue(), products);
    }

    public ProductSummaryResponse getProductSummary(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        long journeyCount = journeyService.countJourneys(productId);

        return new ProductSummaryResponse(
                product.getId(),
                product.getNickname(),
                product.getOfficialName(),
                product.isAuthenticated(),
                Math.toIntExact(journeyCount),
                product.getProvenanceScore(),
                getProvenanceStatus(product.getProvenanceScore(), journeyCount),
                Math.toIntExact(ownershipService.countKeepers(productId))
        );
    }

    public ProductLineageResponse getProductLineage(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        List<Generation> generations = ownershipService.findProductOwnershipHistories(productId).stream()
                .map(ownershipHistory -> toGeneration(productId, ownershipHistory))
                .toList();

        return new ProductLineageResponse(product.getId(), generations);
    }

    public GenerationLetterResponse getGenerationLetter(Long productId, Integer generation) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        OwnershipHistory ownershipHistory = ownershipService.findProductOwnershipHistory(productId, generation)
                .orElseThrow(() -> new CustomException(ErrorCode.GENERATION_LETTER_NOT_FOUND));

        TransferLetter letter = transferService.findOpenedLetter(productId, generation)
                .orElseThrow(() -> new CustomException(ErrorCode.GENERATION_LETTER_NOT_FOUND));

        return new GenerationLetterResponse(
                product.getId(),
                ownershipHistory.getGeneration(),
                letter.getTransferId(),
                letter.getId(),
                letter.getContent(),
                toKst(letter.getOpenedAt())
        );
    }

    private String getProvenanceStatus(Integer provenanceScore, long journeyCount) {
        if (provenanceScore != null) {
            return "calculated";
        }

        if (journeyCount < MINIMUM_JOURNEY_COUNT_FOR_PROVENANCE) {
            return "insufficient";
        }

        return "calculating";
    }

    private Generation toGeneration(Long productId, OwnershipHistory ownershipHistory) {
        return new Generation(
                ownershipHistory.getId(),
                ownershipHistory.getGeneration(),
                toKeeperLabel(ownershipHistory.getGeneration()),
                ownershipHistory.getOwnershipStatus() == OwnershipStatus.OWNING,
                ownershipHistory.getOwnedFrom(),
                ownershipHistory.getOwnedTo(),
                toDurationText(ownershipHistory),
                transferService.hasOpenedLetter(productId, ownershipHistory.getGeneration())
        );
    }

    private String toDurationText(OwnershipHistory ownershipHistory) {
        String endYear = ownershipHistory.getOwnedTo() == null
                ? "현재"
                : String.valueOf(ownershipHistory.getOwnedTo().getYear());

        return ownershipHistory.getOwnedFrom().getYear() + " ~ " + endYear;
    }

    private List<OwnershipHistory> findOwnershipHistories(Long userId, ProductListStatus status) {
        return switch (status) {
            case OWNING -> ownershipService.findOwnershipHistories(userId, OwnershipStatus.OWNING);
            case TRANSFERRED -> ownershipService.findOwnershipHistories(userId, OwnershipStatus.TRANSFERRED);
            case ALL -> ownershipService.findOwnershipHistories(userId);
        };
    }

    private ProductItem toProductItem(Long userId, OwnershipHistory ownershipHistory) {
        Product product = productRepository.findById(ownershipHistory.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_LIST_RETRIEVAL_FAILED));

        InheritanceLetterPreview inheritanceLetter = transferService
                .findLatestOpenedLetter(product.getId(), userId)
                .map(letter -> toInheritanceLetterPreview(product.getId(), letter))
                .orElse(null);

        return new ProductItem(
                product.getId(),
                product.getPassportId(),
                product.getNickname(),
                product.getOfficialName(),
                product.getOfficialImageUrl(),
                ownershipHistory.getGeneration(),
                Math.toIntExact(journeyService.countJourneys(product.getId(), userId)),
                ownershipHistory.getOwnershipStatus().getValue(),
                inheritanceLetter,
                toKst(product.getCreatedAt())
        );
    }

    private InheritanceLetterPreview toInheritanceLetterPreview(Long productId, TransferLetter letter) {
        return ownershipService.findLatestTransferredOwnership(productId, letter.getAuthorId())
                .map(ownershipHistory -> new InheritanceLetterPreview(
                        letter.getId(),
                        toKeeperLabel(ownershipHistory.getGeneration()),
                        letter.getContent(),
                        toKst(letter.getOpenedAt())
                ))
                .orElse(null);
    }

    private String toKeeperLabel(Integer generation) {
        int lastTwoDigits = generation % 100;
        String suffix = switch (lastTwoDigits) {
            case 11, 12, 13 -> "th";
            default -> switch (generation % 10) {
                case 1 -> "st";
                case 2 -> "nd";
                case 3 -> "rd";
                default -> "th";
            };
        };
        return generation + suffix + " Keeper";
    }

    private OffsetDateTime toKst(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atOffset(KST_OFFSET);
    }
}
