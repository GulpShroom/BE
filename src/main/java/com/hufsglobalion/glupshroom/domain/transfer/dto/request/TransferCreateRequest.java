package com.hufsglobalion.glupshroom.domain.transfer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record TransferCreateRequest(
        @Schema(description = "리셀글 ID", example = "1")
        @NotNull Long resellId,

        @Schema(description = "구매자 ID", example = "3")
        @NotNull Long buyerId
) {
}
