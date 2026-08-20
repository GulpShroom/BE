package com.hufsglobalion.glupshroom.domain.care.client;

public record PhotoPayload(
        byte[] bytes,
        String contentType
) {
}
