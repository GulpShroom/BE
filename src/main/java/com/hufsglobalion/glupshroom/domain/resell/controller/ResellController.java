package com.hufsglobalion.glupshroom.domain.resell.controller;

import com.hufsglobalion.glupshroom.domain.resell.dto.request.ResellSaveRequest;
import com.hufsglobalion.glupshroom.domain.resell.dto.response.ResellListResponse;
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

    @Operation(summary = "리셀글 목록 조회", description = "리셀 마켓에 등록된 판매글 목록을 조회합니다. role/userId로 판매자/구매자 필터링, status로 상태 필터링을 지원합니다.")
    @GetMapping("/resells")
    public ApiResponse<ResellListResponse> getResellList(
            @RequestParam(defaultValue = "active") String status,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ResellListResponse response = resellService.getResellList(status, role, userId, page, size);
        return ApiResponse.success("리셀글 목록 조회에 성공했습니다", response);
    }

    @Operation(summary = "리셀글 삭제", description = "본인이 작성한 리셀글을 삭제합니다. 진행 중인 거래가 있으면 삭제할 수 없습니다.")
    @DeleteMapping("/resells/{resellId}")
    public ApiResponse<Void> deleteResell(
            @PathVariable Long resellId,
            @RequestParam(required = false) Long sellerId
    ) {
        resellService.deleteResell(resellId, sellerId);
        return ApiResponse.success("리셀글 삭제에 성공했습니다", null);
    }
}
