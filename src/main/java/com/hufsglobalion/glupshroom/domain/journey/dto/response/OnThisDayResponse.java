package com.hufsglobalion.glupshroom.domain.journey.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;

public record OnThisDayResponse(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDate baseDate,
        OnThisDayJourney journey
) {

    public record OnThisDayJourney(
            Long journeyId,
            int yearsAgo,
            String country,
            String city,
            String recallText,
            String thumbnailUrl
    ) {
    }
}
