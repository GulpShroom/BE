package com.hufsglobalion.glupshroom.domain.ownership.entity;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum OwnershipStatus {

    OWNING("owning"),
    TRANSFERRED("transferred");

    private final String value;

    OwnershipStatus(String value) {
        this.value = value;
    }

    public static OwnershipStatus from(String value) {
        return Arrays.stream(values())
                .filter(ownershipStatus -> ownershipStatus.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 소유 상태입니다"));
    }
}
