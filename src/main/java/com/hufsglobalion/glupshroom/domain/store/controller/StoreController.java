package com.hufsglobalion.glupshroom.domain.store.controller;

import com.hufsglobalion.glupshroom.domain.store.dto.response.StoreListResponse;
import com.hufsglobalion.glupshroom.domain.store.service.StoreService;
import com.hufsglobalion.glupshroom.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Store", description = "매장 API")
@RestController
@RequestMapping("/api/v1/mcarry")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @Operation(summary = "매장 조회", description = "국가와 도시 조건에 따라 드롭다운용 매장 정보를 조회합니다")
    @GetMapping("/stores")
    public ApiResponse<StoreListResponse> getStores(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city
    ) {
        StoreListResponse response = storeService.getStores(country, city);
        return ApiResponse.success("매장 정보를 조회했습니다", response);
    }
}
