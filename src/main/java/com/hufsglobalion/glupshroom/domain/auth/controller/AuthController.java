package com.hufsglobalion.glupshroom.domain.auth.controller;

import com.hufsglobalion.glupshroom.domain.auth.dto.request.ProfileSelectRequest;
import com.hufsglobalion.glupshroom.domain.auth.dto.response.ProfileSelectResponse;
import com.hufsglobalion.glupshroom.domain.auth.service.AuthService;
import com.hufsglobalion.glupshroom.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "프로필 선택 API")
@RestController
@RequestMapping("/api/v1/mcarry/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "프로필 선택", description = "First Keeper / Next Keeper 프로필을 선택하고 사용자 정보를 반환합니다.")
    @PostMapping("/profile")
    public ApiResponse<ProfileSelectResponse> selectProfile(@RequestBody ProfileSelectRequest request) {
        ProfileSelectResponse response = authService.selectProfile(request.profileType());
        return ApiResponse.success("프로필이 선택되었습니다", response);
    }
}
