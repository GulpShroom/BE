package com.hufsglobalion.glupshroom.domain.journey.service;

import com.hufsglobalion.glupshroom.domain.journey.Journey;
import com.hufsglobalion.glupshroom.domain.journey.JourneyRepository;
import com.hufsglobalion.glupshroom.domain.journey.dto.request.JourneySaveRequest;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneySaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneyListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;

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
    @Transactional(readOnly = true)
    public JourneyListResponse getJourneyList(Long productId, Long userId, String sort, int page, int size) {
        // TODO: Product 엔티티 생기면 productId 존재 확인(404) 추가

        Sort sortOption = "country".equalsIgnoreCase(sort)
                ? Sort.by(Sort.Direction.ASC, "country")
                : Sort.by(Sort.Direction.DESC, "journeyYear").and(Sort.by(Sort.Direction.DESC, "journeyMonth"));

        Pageable pageable = PageRequest.of(page, size, sortOption);
        Page<Journey> journeyPage = journeyRepository.findByProductIdAndUserId(productId, userId, pageable);

        List<JourneyListResponse.JourneySummary> summaries = journeyPage.getContent().stream()
                .map(j -> new JourneyListResponse.JourneySummary(
                        j.getId(),
                        j.getPhotoUrl(),
                        j.getRecallText(),
                        j.getCountry(),
                        j.getCity(),
                        j.getJourneyYear(),
                        j.getJourneyMonth(),
                        "owning" // TODO: ownership_history 연동 전까지 기본값
                ))
                .toList();

        return new JourneyListResponse(journeyPage.getTotalElements(), summaries);
    }
}