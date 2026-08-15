package com.hufsglobalion.glupshroom.domain.journey.service;

import com.hufsglobalion.glupshroom.domain.journey.repository.JourneyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JourneyService {

    private final JourneyRepository journeyRepository;

    public long countJourneys(Long productId, Long authorId) {
        return journeyRepository.countByProductIdAndAuthorId(productId, authorId);
    }
}
