package com.hufsglobalion.glupshroom.domain.transfer.dto.response;

import com.hufsglobalion.glupshroom.domain.transfer.entity.Transfer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public record TransferCreateResponse(
        @Schema(description = "생성된 계승 ID", example = "10")
        Long transferId,

        @Schema(description = "대상 제품 ID", example = "1")
        Long productId,

        @Schema(description = "이전 소유자(판매자) ID", example = "1")
        Long fromUserId,

        @Schema(description = "신규 소유자(구매자) ID", example = "3")
        Long toUserId,

        @Schema(description = "이전 유형", example = "resell")
        String transferType,

        @Schema(description = "공식 채널 여부", example = "true")
        boolean isOfficial,

        @Schema(description = "진행 상태", example = "pending", allowableValues = {"pending", "approved", "completed", "expired"})
        String transferStatus,

        @Schema(description = "계승 시작 시각", example = "2026-08-10T11:00:00+09:00")
        OffsetDateTime requestedAt
) {

    private static final ZoneOffset KST_OFFSET = ZoneOffset.ofHours(9);

    public static TransferCreateResponse from(Transfer transfer) {
        return new TransferCreateResponse(
                transfer.getId(),
                transfer.getProductId(),
                transfer.getFromUserId(),
                transfer.getToUserId(),
                transfer.getTransferType(),
                transfer.isOfficial(),
                transfer.getTransferStatus().getValue(),
                transfer.getRequestedAt().atOffset(KST_OFFSET)
        );
    }
}
