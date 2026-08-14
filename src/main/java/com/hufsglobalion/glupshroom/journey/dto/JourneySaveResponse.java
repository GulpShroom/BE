package com.hufsglobalion.glupshroom.journey.dto;

import java.time.LocalDateTime;

public record JourneySaveResponse(
        Long journeyId,
        Long productId,
        String title,
        String content,
        LocalDateTime createdAt
) {}