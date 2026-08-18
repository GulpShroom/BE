package com.hufsglobalion.glupshroom.domain.transfer.controller;

import com.hufsglobalion.glupshroom.domain.transfer.dto.request.LetterCreateRequest;
import com.hufsglobalion.glupshroom.domain.transfer.dto.request.LetterDraftRequest;
import com.hufsglobalion.glupshroom.domain.transfer.dto.request.TransferCompleteRequest;
import com.hufsglobalion.glupshroom.domain.transfer.dto.request.TransferCreateRequest;
import com.hufsglobalion.glupshroom.domain.transfer.dto.response.LetterCreateResponse;
import com.hufsglobalion.glupshroom.domain.transfer.dto.response.LetterDraftResponse;
import com.hufsglobalion.glupshroom.domain.transfer.dto.response.TransferCompleteResponse;
import com.hufsglobalion.glupshroom.domain.transfer.dto.response.TransferCreateResponse;
import com.hufsglobalion.glupshroom.domain.transfer.service.TransferService;
import com.hufsglobalion.glupshroom.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Operation(summary = "계승 편지 작성", description = "전 주인이 다음 주인에게 남길 봉인 편지를 작성합니다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{transferId}/letter")
    public ApiResponse<LetterCreateResponse> writeLetter(
            @PathVariable Long transferId,
            @RequestBody @Valid LetterCreateRequest request
    ) {
        LetterCreateResponse response = transferService.writeLetter(transferId, request);
        return ApiResponse.of("S201", "편지가 봉인되었습니다", response);
    }

    @Operation(summary = "계승 편지 LLM 초안 생성", description = "여정 데이터를 기반으로 편지 초안을 생성합니다.")
    @PostMapping("/{transferId}/letter/draft")
    public ApiResponse<LetterDraftResponse> generateLetterDraft(
            @PathVariable Long transferId,
            @RequestBody @Valid LetterDraftRequest request
    ) {
        LetterDraftResponse response = transferService.generateLetterDraft(transferId, request.authorId());
        return ApiResponse.success("편지 초안이 생성되었습니다", response);
    }

    @Operation(summary = "소유권 이전 완료", description = "새 주인 등록으로 소유권 이전(계승)을 최종 완료합니다.")
    @PostMapping("/{transferId}/complete")
    public ApiResponse<TransferCompleteResponse> completeTransfer(
            @PathVariable Long transferId,
            @RequestBody @Valid TransferCompleteRequest request
    ) {
        TransferCompleteResponse response = transferService.completeTransfer(transferId, request.newOwnerId());
        return ApiResponse.success("대물림이 이어졌습니다", response);
    }
}
