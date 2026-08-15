package com.hufsglobalion.glupshroom.domain.product.dto.request;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;

@Getter
public enum ProductListStatus {

    OWNING("owning"),
    TRANSFERRED("transferred"),
    ALL("all");

    private final String value;

    ProductListStatus(String value) {
        this.value = value;
    }

    public static Optional<ProductListStatus> from(String value) {
        return Arrays.stream(values())
                .filter(productListStatus -> productListStatus.value.equals(value))
                .findFirst();
    }
}
