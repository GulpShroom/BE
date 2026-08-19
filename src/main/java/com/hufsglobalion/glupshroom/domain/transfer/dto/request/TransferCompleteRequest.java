package com.hufsglobalion.glupshroom.domain.transfer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record TransferCompleteRequest(
        @Schema(description = "새 주인 ID", example = "3")
        @NotNull Long newOwnerId
) {
}
