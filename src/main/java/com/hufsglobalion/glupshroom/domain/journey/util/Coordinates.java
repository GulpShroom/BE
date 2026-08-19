package com.hufsglobalion.glupshroom.domain.journey.util;

import java.math.BigDecimal;

public record Coordinates(BigDecimal latitude, BigDecimal longitude) {

    public static final Coordinates EMPTY = new Coordinates(null, null);
}
