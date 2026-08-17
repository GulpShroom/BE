package com.hufsglobalion.glupshroom.domain.journey.service;

import com.hufsglobalion.glupshroom.domain.journey.dto.request.JourneySaveRequest;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneyDetailResponse;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneyListResponse;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneySaveResponse;
import com.hufsglobalion.glupshroom.domain.journey.entity.Journey;
import com.hufsglobalion.glupshroom.domain.journey.repository.JourneyRepository;
import com.hufsglobalion.glupshroom.domain.journey.util.PhotoMetadata;
import com.hufsglobalion.glupshroom.domain.journey.util.PhotoMetadataExtractor;
import com.hufsglobalion.glupshroom.domain.ownership.repository.OwnershipHistoryRepository;
import com.hufsglobalion.glupshroom.domain.ownership.entity.OwnershipStatus;
import com.hufsglobalion.glupshroom.domain.product.entity.Product;
import com.hufsglobalion.glupshroom.domain.product.repository.ProductRepository;
import com.hufsglobalion.glupshroom.global.exception.CustomException;
import com.hufsglobalion.glupshroom.global.exception.ErrorCode;
import com.hufsglobalion.glupshroom.domain.journey.dto.request.JourneyUpdateRequest;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneyUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JourneyService {

    private final JourneyRepository journeyRepository;
    private final ProductRepository productRepository;
    private final PhotoMetadataExtractor photoMetadataExtractor;
    private final OwnershipHistoryRepository ownershipHistoryRepository;

    public JourneySaveResponse saveJourney(JourneySaveRequest request) {
        PhotoMetadata metadata = photoMetadataExtractor.extract(request.photoUrl());
        return save(request, metadata);
    }

    protected JourneySaveResponse save(JourneySaveRequest request, PhotoMetadata metadata) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getCurrentOwnerId().equals(request.userId())) {
            throw new CustomException(ErrorCode.PRODUCT_OWNER_MISMATCH);
        }

        boolean isFirstJourney = journeyRepository.countByProductId(request.productId()) == 0;

        Integer journeyYear = metadata.year() != null ? metadata.year() : request.journeyYear();
        Integer journeyMonth = metadata.month() != null ? metadata.month() : request.journeyMonth();
        String country = metadata.country() != null ? metadata.country() : request.country();
        String city = metadata.city() != null ? metadata.city() : request.city();

        boolean hasExif = metadata.year() != null;
        String verifyStatus = hasExif ? "VERIFIED" : "UNVERIFIED";
        Integer verifyConfidence = hasExif ? 1 : 0;

        JourneySaveRequest.Tags tags = request.tags();
        JourneySaveRequest.TagSources tagSources = request.tagSources();

        Journey journey = Journey.builder()
                .authorId(request.userId())
                .productId(request.productId())
                .photoUrl(request.photoUrl())
                .country(country)
                .city(city)
                .journeyYear(journeyYear)
                .journeyMonth(journeyMonth)
                .season(metadata.season())
                .activityTag(tags != null ? tags.activity() : null)
                .activitySource(tagSources.activity())
                .situationTag(tags != null ? tags.situation() : null)
                .situationSource(tagSources.situation())
                .styleTag(tags != null ? tags.style() : null)
                .styleSource(tagSources.style())
                .recallText(request.recallText())
                .recallTone(request.recallTone())
                .userMemo(request.userMemo())
                .verifyStatus(verifyStatus)
                .verifyConfidence(verifyConfidence)
                .generation(product.getCurrentGeneration())
                .firstJourney(isFirstJourney)
                .build();

        Journey saved = journeyRepository.save(journey);
        return new JourneySaveResponse(saved.getId());
    }

    @Transactional(readOnly = true)
    public long countJourneys(Long productId) {
        return journeyRepository.countByProductId(productId);
    }

    @Transactional(readOnly = true)
    public long countJourneys(Long productId, Long authorId) {
        return journeyRepository.countByProductIdAndAuthorId(productId, authorId);
    }

    @Transactional(readOnly = true)
    public JourneyDetailResponse getJourneyDetail(Long journeyId, Long userId) {
        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOURNEY_NOT_FOUND));

        boolean isAuthor = journey.getAuthorId().equals(userId);
        if (!isAuthor) {
            throw new CustomException(ErrorCode.JOURNEY_NOT_FOUND);
        }

        JourneyDetailResponse.Tags tags = new JourneyDetailResponse.Tags(
                new JourneyDetailResponse.TagDetail(journey.getActivityTag(), journey.getActivitySource()),
                new JourneyDetailResponse.TagDetail(journey.getSituationTag(), journey.getSituationSource()),
                new JourneyDetailResponse.TagDetail(journey.getStyleTag(), journey.getStyleSource())
        );

        String createdAt = journey.getCreatedAt()
                .atOffset(ZoneOffset.ofHours(9))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        return new JourneyDetailResponse(
                journey.getId(),
                isAuthor,
                journey.isFirstJourney(),
                journey.getPhotoUrl(),
                journey.getCountry(),
                journey.getCity(),
                journey.getJourneyYear(),
                journey.getJourneyMonth(),
                tags,
                journey.getRecallText(),
                journey.getRecallTone(),
                journey.getUserMemo(),
                createdAt
        );
    }

    @Transactional(readOnly = true)
    public JourneyListResponse getJourneyList(Long productId, Long userId, String sort, int page, int size) {
        if (!productRepository.existsById(productId)) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        boolean isOwning = ownershipHistoryRepository
                .findFirstByProductIdAndOwnerIdAndOwnershipStatusOrderByGenerationDesc(
                        productId, userId, OwnershipStatus.OWNING)
                .isPresent();
        String ownershipStatus = isOwning ? OwnershipStatus.OWNING.getValue() : OwnershipStatus.TRANSFERRED.getValue();

        Sort sortOption = "country".equalsIgnoreCase(sort)
                ? Sort.by(Sort.Direction.ASC, "country")
                : Sort.by(Sort.Direction.DESC, "journeyYear").and(Sort.by(Sort.Direction.DESC, "journeyMonth"));

        Pageable pageable = PageRequest.of(page, size, sortOption);
        Page<Journey> journeyPage = journeyRepository.findByProductIdAndAuthorId(productId, userId, pageable);

        List<JourneyListResponse.JourneySummary> summaries = journeyPage.getContent().stream()
                .map(j -> new JourneyListResponse.JourneySummary(
                        j.getId(),
                        j.getPhotoUrl(),
                        j.getRecallText(),
                        j.getCountry(),
                        j.getCity(),
                        j.getJourneyYear(),
                        j.getJourneyMonth(),
                        ownershipStatus
                ))
                .toList();

        return new JourneyListResponse(journeyPage.getTotalElements(), summaries);
    }

    @Transactional
    public JourneyUpdateResponse updateJourney(Long journeyId, JourneyUpdateRequest request) {
        if (request.userId() == null) {
            throw new CustomException(ErrorCode.JOURNEY_INVALID_REQUESTER);
        }

        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOURNEY_NOT_FOUND));

        if (!journey.getAuthorId().equals(request.userId())) {
            throw new CustomException(ErrorCode.JOURNEY_ACCESS_DENIED);
        }

        boolean hasExifDate = "VERIFIED".equals(journey.getVerifyStatus());
        if (hasExifDate && (request.journeyYear() != null || request.journeyMonth() != null)) {
            throw new CustomException(ErrorCode.JOURNEY_DATE_LOCKED);
        }

        JourneyUpdateRequest.Tags tags = request.tags();
        JourneyUpdateRequest.TagSources tagSources = request.tagSources();

        journey.updateDetails(
                request.country(), request.city(),
                request.journeyYear(), request.journeyMonth(),
                tags != null ? tags.activity() : null, tagSources != null ? tagSources.activity() : null,
                tags != null ? tags.situation() : null, tagSources != null ? tagSources.situation() : null,
                tags != null ? tags.style() : null, tagSources != null ? tagSources.style() : null,
                request.recallText(), request.recallTone(), request.userMemo()
        );

        return new JourneyUpdateResponse(
                journey.getId(),
                journey.getCountry(),
                journey.getCity(),
                journey.getJourneyYear(),
                journey.getJourneyMonth(),
                new JourneyUpdateResponse.Tags(
                        new JourneyUpdateResponse.TagDetail(journey.getActivityTag(), journey.getActivitySource()),
                        new JourneyUpdateResponse.TagDetail(journey.getSituationTag(), journey.getSituationSource()),
                        new JourneyUpdateResponse.TagDetail(journey.getStyleTag(), journey.getStyleSource())
                ),
                journey.getRecallText(),
                journey.getRecallTone(),
                journey.getUserMemo()
        );
    }
}
