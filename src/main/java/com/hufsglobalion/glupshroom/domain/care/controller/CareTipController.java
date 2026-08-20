package com.hufsglobalion.glupshroom.domain.care.controller;

import com.hufsglobalion.glupshroom.domain.care.dto.request.CareTipCreateRequest;
import com.hufsglobalion.glupshroom.domain.care.dto.response.CareTipCreateResponse;
import com.hufsglobalion.glupshroom.domain.care.dto.response.CareTipListResponse;
import com.hufsglobalion.glupshroom.domain.care.service.CareTipService;
import com.hufsglobalion.glupshroom.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CareTip", description = "케어팁 API")
@RestController
@RequestMapping("/api/v1/mcarry")
@RequiredArgsConstructor
public class CareTipController {

    private final CareTipService careTipService;

    @Operation(summary = "사용자 케어팁 작성", description = "현재 소유자가 제품에 대한 케어팁을 작성합니다.")
    @PostMapping("/products/{productId}/care-tip")
    public ResponseEntity<ApiResponse<CareTipCreateResponse>> createCareTip(
            @PathVariable Long productId,
            @RequestBody CareTipCreateRequest request
    ) {
        CareTipCreateResponse response = careTipService.createCareTip(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("S201", "케어팁을 작성했습니다", response));
    }

    @Operation(summary = "케어팁 조회", description = "제품에 작성된 케어팁 목록을 조회합니다. 현재 소유자 작성분과 계승 선택된 이전 세대 작성분이 표시됩니다.")
    @GetMapping("/products/{productId}/care-tips")
    public ApiResponse<CareTipListResponse> getCareTips(@PathVariable Long productId) {
        CareTipListResponse response = careTipService.getCareTips(productId);
        return ApiResponse.success("케어 팁을 조회했습니다", response);
    }
}
