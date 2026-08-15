package com.hufsglobalion.glupshroom.domain.journey.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JourneySaveRequest(
        @NotNull Long userId,
        @NotNull Long productId,
        @NotBlank String photoUrl,
        @NotBlank String country,
        String city,
        @NotNull Integer journeyYear,
        Integer journeyMonth,
        Tags tags,
        @NotNull @Valid TagSources tagSources,
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
            @NotBlank String activity,
            @NotBlank String situation,
            @NotBlank String style
    ) {}
}