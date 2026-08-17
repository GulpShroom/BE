package com.hufsglobalion.glupshroom.domain.journey.dto.response;

public record JourneyDetailResponse(
        Long journeyId,
        boolean isAuthor,
        boolean isFirstJourney,
        String photoUrl,
        String country,
        String city,
        Integer journeyYear,
        Integer journeyMonth,
        Tags tags,
        String recallText,
        String recallTone,
        String userMemo,
        String createdAt
) {
    public record Tags(
            TagDetail activity,
            TagDetail situation,
            TagDetail style
    ) {}

    public record TagDetail(
            String tag,
            String source
    ) {}
}