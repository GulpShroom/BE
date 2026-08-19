package com.hufsglobalion.glupshroom.domain.product.controller;

import com.hufsglobalion.glupshroom.domain.product.dto.request.ProductScanRequest;
import com.hufsglobalion.glupshroom.domain.product.dto.request.ProductRegistrationRequest;
import com.hufsglobalion.glupshroom.domain.product.dto.response.MyProductListResponse;
import com.hufsglobalion.glupshroom.domain.product.dto.response.GenerationLetterResponse;
import com.hufsglobalion.glupshroom.domain.product.dto.response.DigitalPassportResponse;
import com.hufsglobalion.glupshroom.domain.product.dto.response.JourneyMapResponse;
import com.hufsglobalion.glupshroom.domain.product.dto.response.ProductLineageResponse;
import com.hufsglobalion.glupshroom.domain.product.dto.response.ProductScanResponse;
import com.hufsglobalion.glupshroom.domain.product.dto.response.ProductSummaryResponse;
import com.hufsglobalion.glupshroom.domain.product.dto.response.ProductRegistrationResponse;
import com.hufsglobalion.glupshroom.domain.product.service.ProductService;
import com.hufsglobalion.glupshroom.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Product", description = "제품 API")
@RestController
@RequestMapping("/api/v1/mcarry")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "디지털 여권 조회", description = "제품의 디지털 여권, 정품 인증, 사양, 구매 정보를 조회합니다.")
    @GetMapping("/products/{productId}")
    public ApiResponse<DigitalPassportResponse> getDigitalPassport(@PathVariable Long productId) {
        DigitalPassportResponse response = productService.getDigitalPassport(productId);
        return ApiResponse.success("디지털 여권을 조회했습니다", response);
    }

    @Operation(summary = "디지털 여권 발급", description = "정품 인증된 제품을 등록하고 디지털 여권, 1대 Keeper 소유 이력, 첫 여정을 생성합니다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/products")
    public ApiResponse<ProductRegistrationResponse> registerProduct(
            @RequestBody ProductRegistrationRequest request
    ) {
        ProductRegistrationResponse response = productService.registerProduct(request);
        return ApiResponse.of("S201", "디지털 여권이 발급되었습니다", response);
    }

    @Operation(summary = "내 제품 리스트 조회", description = "사용자의 소유 상태별 제품 목록을 조회합니다.")
    @GetMapping("/users/{userId}/products")
    public ApiResponse<MyProductListResponse> getMyProductList(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "owning") String status
    ) {
        MyProductListResponse response = productService.getMyProductList(userId, status);
        return ApiResponse.success("내 제품 리스트를 조회했습니다", response);
    }

    @Operation(summary = "제품 요약바 조회", description = "메인 제품 요약바에 표시할 정보를 조회합니다.")
    @GetMapping("/products/{productId}/summary")
    public ApiResponse<ProductSummaryResponse> getProductSummary(@PathVariable Long productId) {
        ProductSummaryResponse response = productService.getProductSummary(productId);
        return ApiResponse.success("제품 요약 정보를 조회했습니다", response);
    }

    @Operation(summary = "제품 스캔·정품 확인", description = "시리얼 넘버 또는 QR 코드로 제품 정품 여부를 확인합니다.")
    @PostMapping("/products/scan")
    public ApiResponse<ProductScanResponse> scanProduct(@RequestBody ProductScanRequest request) {
        ProductScanResponse response = productService.scanProduct(request);
        return ApiResponse.success("MCM 정품 인증이 완료되었습니다", response);
    }

    @Operation(summary = "계보 타임라인 조회", description = "제품의 세대별 소유 이력을 계보 타임라인으로 조회합니다.")
    @GetMapping("/products/{productId}/lineage")
    public ApiResponse<ProductLineageResponse> getProductLineage(@PathVariable Long productId) {
        ProductLineageResponse response = productService.getProductLineage(productId);
        return ApiResponse.success("계보 타임라인을 조회했습니다", response);
    }

    @Operation(summary = "대별 편지 조회", description = "제품의 특정 Keeper 세대가 남긴 개봉 편지를 조회합니다.")
    @GetMapping("/products/{productId}/lineage/{generation}/letter")
    public ApiResponse<GenerationLetterResponse> getGenerationLetter(
            @PathVariable Long productId,
            @PathVariable Integer generation
    ) {
        GenerationLetterResponse response = productService.getGenerationLetter(productId, generation);
        return ApiResponse.success("대별 편지를 조회했습니다", response);
    }

    @Operation(summary = "여정 지도 조회", description = "제품의 여정을 지도 마커/집계로 조회합니다. 요청자 권한에 따라 노출 필드가 달라집니다.")
    @GetMapping("/products/{productId}/map")
    public ApiResponse<JourneyMapResponse> getJourneyMap(
            @PathVariable Long productId,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "world") String zoom,
            @RequestParam(required = false) Integer generation
    ) {
        JourneyMapResponse response = productService.getJourneyMap(productId, userId, zoom, generation);
        return ApiResponse.success("여정 지도를 조회했습니다", response);
    }
}
