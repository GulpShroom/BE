package com.hufsglobalion.glupshroom.journey;

import com.hufsglobalion.glupshroom.journey.dto.JourneySaveRequest;
import com.hufsglobalion.glupshroom.journey.dto.JourneySaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JourneyService {

    private final JourneyRepository journeyRepository;

    @Transactional
    public JourneySaveResponse saveJourney(Long userId, JourneySaveRequest request) {
        Journey journey = Journey.builder()
                .productId(request.productId())
                .userId(userId)
                .title(request.title())
                .content(request.content())
                .build();

        Journey saved = journeyRepository.save(journey);

        return new JourneySaveResponse(
                saved.getId(),
                saved.getProductId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getCreatedAt()
        );
    }
}