package com.hufsglobalion.glupshroom.global.common;

import com.hufsglobalion.glupshroom.global.exception.ErrorCode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record ApiResponse<T>(
        String code,
        String message,
        T data,
        List<?> errors,
        String timestamp
) {

    private static final String SUCCESS_CODE = "S200";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(SUCCESS_CODE, message, data, null, now());
    }

    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), null, null, now());
    }

    private static String now() {
        return OffsetDateTime.now(ZoneOffset.ofHours(9))
                .withNano(0)
                .format(TIMESTAMP_FORMATTER);
    }
}
