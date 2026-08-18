package com.hufsglobalion.glupshroom.domain.care.controller;

import com.hufsglobalion.glupshroom.domain.care.dto.request.CareTipCreateRequest;
import com.hufsglobalion.glupshroom.domain.care.dto.response.CareTipCreateResponse;
import com.hufsglobalion.glupshroom.domain.care.service.CareTipService;
import com.hufsglobalion.glupshroom.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
