package com.hufsglobalion.glupshroom.domain.journey.service;

import com.hufsglobalion.glupshroom.domain.journey.Journey;
import com.hufsglobalion.glupshroom.domain.journey.JourneyRepository;
import com.hufsglobalion.glupshroom.domain.journey.dto.request.JourneySaveRequest;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneySaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JourneyService {

    private final JourneyRepository journeyRepository;

    @Transactional
    public JourneySaveResponse saveJourney(JourneySaveRequest request) {
        boolean isFirstJourney = journeyRepository.countByProductId(request.productId()) == 0;

        JourneySaveRequest.Tags tags = request.tags();
        JourneySaveRequest.TagSources tagSources = request.tagSources();

        Journey journey = Journey.builder()
                .userId(request.userId())
                .productId(request.productId())
                .photoUrl(request.photoUrl())
                .country(request.country())
                .city(request.city())
                .journeyYear(request.journeyYear())
                .journeyMonth(request.journeyMonth())
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