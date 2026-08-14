package com.hufsglobalion.glupshroom.journey.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JourneySaveRequest(
        @NotNull Long productId,
        @NotBlank String title,
        String content
) {}