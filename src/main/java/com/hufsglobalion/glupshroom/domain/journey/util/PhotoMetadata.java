package com.hufsglobalion.glupshroom.domain.journey.util;

import java.time.LocalDateTime;

public record PhotoMetadata(
        Integer year,
        Integer month,
        String season,
        String country,
        String city,
        LocalDateTime takenAt
) {}