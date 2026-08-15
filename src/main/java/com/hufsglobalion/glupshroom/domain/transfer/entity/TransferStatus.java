package com.hufsglobalion.glupshroom.domain.transfer.entity;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum TransferStatus {

    PENDING("pending"),
    APPROVED("approved"),
    COMPLETED("completed"),
    EXPIRED("expired");

    private final String value;

    TransferStatus(String value) {
        this.value = value;
    }

    public static TransferStatus from(String value) {
        return Arrays.stream(values())
                .filter(transferStatus -> transferStatus.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 계승 상태입니다"));
    }
}
