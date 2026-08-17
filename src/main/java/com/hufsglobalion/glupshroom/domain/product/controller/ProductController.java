package com.hufsglobalion.glupshroom.domain.product.controller;

import com.hufsglobalion.glupshroom.domain.product.dto.response.MyProductListResponse;
import com.hufsglobalion.glupshroom.domain.product.dto.response.GenerationLetterResponse;
import com.hufsglobalion.glupshroom.domain.product.dto.response.ProductLineageResponse;
import com.hufsglobalion.glupshroom.domain.product.dto.response.ProductSummaryResponse;
import com.hufsglobalion.glupshroom.domain.product.service.ProductService;
import com.hufsglobalion.glupshroom.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Product", description = "제품 API")
@RestController
@RequestMapping("/api/v1/mcarry")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

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
}
