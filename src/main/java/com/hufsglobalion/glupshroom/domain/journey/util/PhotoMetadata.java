package com.hufsglobalion.glupshroom.domain.journey.util;

public record PhotoMetadata(
        Integer year,
        Integer month,
        String season,
        String country,
        String city
) {}