package com.hufsglobalion.glupshroom.domain.auth.dto.response;

import com.hufsglobalion.glupshroom.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProfileSelectResponse(
        @Schema(description = "선택된 사용자 ID", example = "1")
        Long userId,

        @Schema(description = "닉네임", example = "민지")
        String nickname,

        @Schema(description = "선택된 프로필 타입", example = "first_keeper")
        String profileType
) {

    public static ProfileSelectResponse from(User user) {
        return new ProfileSelectResponse(user.getId(), user.getNickname(), user.getProfileType().getValue());
    }
}
