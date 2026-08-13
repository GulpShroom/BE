package com.hufsglobalion.glupshroom.domain.user.entity;

import com.hufsglobalion.glupshroom.global.exception.CustomException;
import com.hufsglobalion.glupshroom.global.exception.ErrorCode;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum ProfileType {

    FIRST_KEEPER("first_keeper"),
    NEXT_KEEPER("next_keeper");

    private final String value;

    ProfileType(String value) {
        this.value = value;
    }

    public static ProfileType from(String value) {
        return Arrays.stream(values())
                .filter(profileType -> profileType.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.UNSUPPORTED_PROFILE_TYPE));
    }
}
