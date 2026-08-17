package com.hufsglobalion.glupshroom.domain.journey.dto.response;

import java.util.List;

public record JourneyListResponse(
        long totalCount,
        List<JourneySummary> journeys
) {
    public record JourneySummary(
            Long journeyId,
            String thumbnailUrl,
            String recallText,
            String country,
            String city,
            Integer journeyYear,
            Integer journeyMonth,
            String ownershipStatus
    ) {}
}