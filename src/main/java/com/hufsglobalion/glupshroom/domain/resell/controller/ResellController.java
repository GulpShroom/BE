package com.hufsglobalion.glupshroom.domain.resell.controller;

import com.hufsglobalion.glupshroom.domain.resell.dto.request.ResellSaveRequest;
import com.hufsglobalion.glupshroom.domain.resell.dto.response.ResellDetailResponse;
import com.hufsglobalion.glupshroom.domain.resell.dto.response.ResellSaveResponse;
import com.hufsglobalion.glupshroom.domain.resell.service.ResellService;
import com.hufsglobalion.glupshroom.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Resell", description = "리셀 마켓 관련 API")
@RestController
@RequestMapping("/api/v1/mcarry")
@RequiredArgsConstructor
public class ResellController {

    private final ResellService resellService;

    @Operation(summary = "리셀글 작성", description = "소유 중인 제품을 리셀 마켓에 판매글로 등록합니다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/resells")
    public ApiResponse<ResellSaveResponse> saveResell(@RequestBody ResellSaveRequest request) {
        ResellSaveResponse response = resellService.saveResell(request);
        return ApiResponse.of("S201", "리셀글 작성에 성공했습니다", response);
    }

    @Operation(summary = "리셀 상세 조회", description = "구매자 관점에서 리셀글 상세를 조회합니다. 실물 정보는 공개하고, 여정 정보는 요약/티저만 제공합니다.")
    @GetMapping("/resells/{resellId}")
    public ApiResponse<ResellDetailResponse> getResellDetail(
            @PathVariable Long resellId,
            @RequestParam(required = false) Long userId
    ) {
        ResellDetailResponse response = resellService.getResellDetail(resellId);
        return ApiResponse.success("리셀 상세 조회에 성공했습니다", response);
    }
}
