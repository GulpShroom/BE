package com.hufsglobalion.glupshroom.domain.journey.dto.response;

public record JourneyUpdateResponse(
        Long journeyId,
        String country,
        String city,
        Integer journeyYear,
        Integer journeyMonth,
        Tags tags,
        String recallText,
        String recallTone,
        String userMemo
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