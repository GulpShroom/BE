package com.hufsglobalion.glupshroom.domain.journey.service;

import com.hufsglobalion.glupshroom.domain.journey.dto.request.JourneySaveRequest;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneySaveResponse;
import com.hufsglobalion.glupshroom.domain.journey.entity.Journey;
import com.hufsglobalion.glupshroom.domain.journey.repository.JourneyRepository;
import com.hufsglobalion.glupshroom.domain.journey.util.PhotoMetadata;
import com.hufsglobalion.glupshroom.domain.journey.util.PhotoMetadataExtractor;
import com.hufsglobalion.glupshroom.domain.product.entity.Product;
import com.hufsglobalion.glupshroom.domain.product.repository.ProductRepository;
import com.hufsglobalion.glupshroom.global.exception.CustomException;
import com.hufsglobalion.glupshroom.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JourneyService {

    private final JourneyRepository journeyRepository;
    private final ProductRepository productRepository;
    private final PhotoMetadataExtractor photoMetadataExtractor;

    public JourneySaveResponse saveJourney(JourneySaveRequest request) {
        PhotoMetadata metadata = photoMetadataExtractor.extract(request.photoUrl());
        return save(request, metadata);
    }

    @Transactional
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
    public long countJourneys(Long productId, Long authorId) {
        return journeyRepository.countByProductIdAndAuthorId(productId, authorId);
    }
}