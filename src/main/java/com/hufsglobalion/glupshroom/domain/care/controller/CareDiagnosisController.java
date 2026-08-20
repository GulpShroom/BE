package com.hufsglobalion.glupshroom.domain.care.controller;

import com.hufsglobalion.glupshroom.domain.care.dto.response.CareDiagnosisHistoryResponse;
import com.hufsglobalion.glupshroom.domain.care.dto.response.CareDiagnosisResponse;
import com.hufsglobalion.glupshroom.domain.care.service.CareDiagnosisService;
import com.hufsglobalion.glupshroom.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "CareDiagnosis", description = "AI 상태 진단 API")
@RestController
@RequestMapping("/api/v1/mcarry/products")
@RequiredArgsConstructor
public class CareDiagnosisController {

    private final CareDiagnosisService careDiagnosisService;

    @Operation(summary = "AI 상태 진단", description = "제품 사진 여러 장을 분석해 상태 등급·문제·솔루션을 생성하고 이력으로 저장합니다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{productId}/diagnosis", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CareDiagnosisResponse> diagnose(
            @PathVariable Long productId,
            @RequestParam Long userId,
            @RequestParam("photos") List<MultipartFile> photos
    ) {
        CareDiagnosisResponse response = careDiagnosisService.diagnose(productId, userId, photos);
        return ApiResponse.of("S201", "상태 진단이 완료되었습니다", response);
    }

    @Operation(summary = "AI 상태 진단 이력 조회", description = "제품의 세대별 AI 상태 진단 이력을 조회합니다.")
    @GetMapping("/{productId}/diagnosis")
    public ApiResponse<CareDiagnosisHistoryResponse> getDiagnosisHistory(@PathVariable Long productId) {
        CareDiagnosisHistoryResponse response = careDiagnosisService.getDiagnosisHistory(productId);
        return ApiResponse.success("케어 진단 이력을 조회했습니다", response);
    }
}
