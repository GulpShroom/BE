package com.hufsglobalion.glupshroom.domain.transfer.dto.response;

import com.hufsglobalion.glupshroom.domain.transfer.entity.Transfer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public record TransferCompleteResponse(
        @Schema(description = "소유권이 이전된 제품 ID", example = "1")
        Long productId,

        @Schema(description = "이전 후 새 세대 번호", example = "2")
        Integer newGeneration,

        @Schema(description = "봉인 편지 개봉 여부", example = "true")
        boolean letterOpened,

        @Schema(description = "이전 상태", example = "completed")
        String transferStatus,

        @Schema(description = "이전 완료 시각", example = "2026-08-10T11:15:00+09:00")
        OffsetDateTime completedAt
) {

    private static final ZoneOffset KST_OFFSET = ZoneOffset.ofHours(9);

    public static TransferCompleteResponse of(Long productId, Integer newGeneration, boolean letterOpened, Transfer transfer) {
        return new TransferCompleteResponse(
                productId,
                newGeneration,
                letterOpened,
                transfer.getTransferStatus().getValue(),
                transfer.getCompletedAt().atOffset(KST_OFFSET)
        );
    }
}
