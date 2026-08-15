package com.hufsglobalion.glupshroom.domain.journey.service;

import com.hufsglobalion.glupshroom.domain.journey.dto.request.JourneySaveRequest;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneySaveResponse;
import com.hufsglobalion.glupshroom.domain.journey.entity.Journey;
import com.hufsglobalion.glupshroom.domain.journey.repository.JourneyRepository;
import com.hufsglobalion.glupshroom.domain.journey.util.PhotoMetadata;
import com.hufsglobalion.glupshroom.domain.journey.util.PhotoMetadataExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JourneyService {

    private final JourneyRepository journeyRepository;
    private final PhotoMetadataExtractor photoMetadataExtractor;

    @Transactional
    public JourneySaveResponse saveJourney(JourneySaveRequest request) {
        boolean isFirstJourney = journeyRepository.countByProductId(request.productId()) == 0;

        PhotoMetadata metadata = photoMetadataExtractor.extract(request.photoUrl());

        Integer journeyYear = metadata.year() != null ? metadata.year() : request.journeyYear();
        Integer journeyMonth = metadata.month() != null ? metadata.month() : request.journeyMonth();
        String country = metadata.country() != null ? metadata.country() : request.country();
        String city = metadata.city() != null ? metadata.city() : request.city();

        JourneySaveRequest.Tags tags = request.tags();
        JourneySaveRequest.TagSources tagSources = request.tagSources();

        Journey journey = Journey.builder()
                .userId(request.userId())
                .productId(request.productId())
                .photoUrl(request.photoUrl())
                .country(country)
                .city(city)
                .journeyYear(journeyYear)
                .journeyMonth(journeyMonth)
                .season(metadata.season())
                .activityTag(tags != null ? tags.activity() : null)
                .activityTagSource(tagSources.activity())
                .situationTag(tags != null ? tags.situation() : null)
                .situationTagSource(tagSources.situation())
                .styleTag(tags != null ? tags.style() : null)
                .styleTagSource(tagSources.style())
                .recallText(request.recallText())
                .recallTone(request.recallTone())
                .userMemo(request.userMemo())
                .verifyStatus("PENDING")
                .verifyConfidence(null)
                .generation(1)
                .isFirstJourney(isFirstJourney)
                .build();

        Journey saved = journeyRepository.save(journey);
        return new JourneySaveResponse(saved.getId());
    }
}