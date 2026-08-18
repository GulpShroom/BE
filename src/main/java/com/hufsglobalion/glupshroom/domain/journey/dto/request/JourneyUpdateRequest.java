package com.hufsglobalion.glupshroom.domain.journey.dto.request;

public record JourneyUpdateRequest(
        Long userId,
        String country,
        String city,
        Integer journeyYear,
        Integer journeyMonth,
        Tags tags,
        TagSources tagSources,
        String recallText,
        String recallTone,
        String userMemo
) {
    public record Tags(
            String activity,
            String situation,
            String style
    ) {}

    public record TagSources(
            String activity,
            String situation,
            String style
    ) {}
}