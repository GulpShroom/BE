package com.hufsglobalion.glupshroom.domain.transfer.controller;

import com.hufsglobalion.glupshroom.domain.transfer.dto.request.TransferCreateRequest;
import com.hufsglobalion.glupshroom.domain.transfer.dto.response.TransferCreateResponse;
import com.hufsglobalion.glupshroom.domain.transfer.service.TransferService;
import com.hufsglobalion.glupshroom.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Transfer", description = "계승(소유권 이전) API")
@RestController
@RequestMapping("/api/v1/mcarry/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @Operation(summary = "계승 시작", description = "리셀글 구매를 통해 소유권 이전(계승)을 시작합니다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<TransferCreateResponse> createTransfer(@RequestBody @Valid TransferCreateRequest request) {
        TransferCreateResponse response = transferService.createResellTransfer(request.resellId(), request.buyerId());
        return ApiResponse.of("S201", "계승이 시작되었습니다", response);
    }
}
