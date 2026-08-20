package com.hufsglobalion.glupshroom.domain.journey.service;

import com.hufsglobalion.glupshroom.domain.journey.client.OpenAiVisionClient;
import com.hufsglobalion.glupshroom.domain.journey.client.VisionAnalysisResult;
import com.hufsglobalion.glupshroom.domain.journey.dto.request.JourneySaveRequest;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneyAnalyzeResponse;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneyDetailResponse;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneyListResponse;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneySaveResponse;
import com.hufsglobalion.glupshroom.domain.journey.entity.Journey;
import com.hufsglobalion.glupshroom.domain.journey.repository.JourneyRepository;
import com.hufsglobalion.glupshroom.domain.journey.util.Coordinates;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JourneyService {

    private static final Set<String> ALLOWED_IMAGE_CONTENT_TYPES = Set.of("image/jpeg", "image/png");
    private static final Set<String> VALID_RECALL_TONES = Set.of("emotional", "plain", "lively");
    private static final String DEFAULT_RECALL_TONE = "emotional";

    private final JourneyRepository journeyRepository;
    private final ProductRepository productRepository;
    private final PhotoMetadataExtractor photoMetadataExtractor;
    private final OwnershipHistoryRepository ownershipHistoryRepository;
    private final OpenAiVisionClient openAiVisionClient;

    public JourneySaveResponse saveJourney(JourneySaveRequest request) {
        PhotoMetadata metadata = photoMetadataExtractor.extract(request.photoUrl());
        return save(request, metadata);
    }

    @Transactional
    public JourneySaveResponse createFirstJourney(Long productId, Long authorId, Integer generation,
                                                   LocalDate purchaseDate, String country, String city,
                                                   BigDecimal latitude, BigDecimal longitude, String userMemo) {
        Journey firstJourney = Journey.builder()
                .productId(productId)
                .authorId(authorId)
                .generation(generation)
                .firstJourney(true)
                .country(country)
                .city(city)
                .latitude(latitude)
                .longitude(longitude)
                .journeyYear(purchaseDate != null ? purchaseDate.getYear() : null)
                .journeyMonth(purchaseDate != null ? purchaseDate.getMonthValue() : null)
                .userMemo(userMemo)
                .verifyStatus("UNVERIFIED")
                .verifyConfidence(0)
                .build();

        firstJourney = journeyRepository.save(firstJourney);
        return new JourneySaveResponse(firstJourney.getId());
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

        Coordinates coordinates = photoMetadataExtractor.resolveCoordinates(city, country);

        JourneySaveRequest.Tags tags = request.tags();
        JourneySaveRequest.TagSources tagSources = request.tagSources();

        Journey journey = Journey.builder()
                .authorId(request.userId())
                .productId(request.productId())
                .photoUrl(request.photoUrl())
                .country(country)
                .city(city)
                .latitude(coordinates.latitude())
                .longitude(coordinates.longitude())
                .journeyYear(journeyYear)
                .journeyMonth(journeyMonth)
                .season(metadata.season())
                .exifTakenAt(metadata.takenAt())
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

    public JourneyAnalyzeResponse analyzeJourney(Long productId, Long userId, String tone, MultipartFile photo) {
        if (photo == null || photo.isEmpty() || !ALLOWED_IMAGE_CONTENT_TYPES.contains(photo.getContentType())) {
            throw new CustomException(ErrorCode.JOURNEY_ANALYSIS_IMAGE_REQUIRED);
        }

        productRepository.findById(productId)
                .filter(product -> product.getCurrentOwnerId().equals(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.JOURNEY_ANALYSIS_FORBIDDEN));

        String resolvedTone = VALID_RECALL_TONES.contains(tone) ? tone : DEFAULT_RECALL_TONE;

        byte[] photoBytes;
        try {
            photoBytes = photo.getBytes();
        } catch (IOException e) {
            throw new CustomException(ErrorCode.JOURNEY_ANALYSIS_FAILED);
        }

        PhotoMetadata metadata = photoMetadataExtractor.extractFromBytes(photoBytes);
        Coordinates coordinates = photoMetadataExtractor.resolveCoordinates(metadata.city(), metadata.country());

        boolean hasExif = metadata.year() != null;
        String verifyStatus = hasExif ? "verified" : "unverified";
        Integer verifyConfidence = hasExif ? 96 : null;

        VisionAnalysisResult vision = openAiVisionClient.analyze(photoBytes, photo.getContentType(), resolvedTone);

        return new JourneyAnalyzeResponse(
                metadata.country(),
                metadata.city(),
                coordinates.latitude() == null ? null : coordinates.latitude().doubleValue(),
                coordinates.longitude() == null ? null : coordinates.longitude().doubleValue(),
                metadata.year(),
                metadata.month(),
                metadata.season(),
                metadata.takenAt(),
                vision.activityTag(),
                vision.situationTag(),
                vision.styleTag(),
                vision.recallText(),
                resolvedTone,
                verifyStatus,
                verifyConfidence
        );
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
    public List<Journey> findJourneys(Long productId, Integer generation) {
        return generation != null
                ? journeyRepository.findByProductIdAndGeneration(productId, generation)
                : journeyRepository.findByProductId(productId);
    }

    @Transactional(readOnly = true)
    public List<Journey> findJourneysWithExif(Long productId, Long authorId) {
        return journeyRepository.findByProductIdAndAuthorIdAndExifTakenAtIsNotNullOrderByExifTakenAtDesc(
                productId,
                authorId
        );
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

        boolean hasExifDate = journey.getExifTakenAt() != null;
        if (hasExifDate && (request.journeyYear() != null || request.journeyMonth() != null)) {
            throw new CustomException(ErrorCode.JOURNEY_DATE_LOCKED);
        }

        JourneyUpdateRequest.Tags tags = request.tags();
        JourneyUpdateRequest.TagSources tagSources = request.tagSources();

        String activityTag = tags != null ? tags.activity() : null;
        String activitySource = activityTag != null
                ? (tagSources != null && tagSources.activity() != null ? tagSources.activity() : "free_text")
                : null;

        String situationTag = tags != null ? tags.situation() : null;
        String situationSource = situationTag != null
                ? (tagSources != null && tagSources.situation() != null ? tagSources.situation() : "free_text")
                : null;

        String styleTag = tags != null ? tags.style() : null;
        String styleSource = styleTag != null
                ? (tagSources != null && tagSources.style() != null ? tagSources.style() : "free_text")
                : null;

        journey.updateDetails(
                request.country(), request.city(),
                request.journeyYear(), request.journeyMonth(),
                activityTag, activitySource,
                situationTag, situationSource,
                styleTag, styleSource,
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

    @Transactional
    public void deleteJourney(Long journeyId, Long userId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.JOURNEY_INVALID_REQUESTER);
        }

        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOURNEY_NOT_FOUND));

        if (!journey.getAuthorId().equals(userId)) {
            throw new CustomException(ErrorCode.JOURNEY_DELETE_FORBIDDEN);
        }

        journeyRepository.delete(journey);
    }
}
