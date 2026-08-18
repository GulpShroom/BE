package com.hufsglobalion.glupshroom.domain.store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record StoreListResponse(
        @Schema(description = "선택 가능한 국가 목록")
        List<String> countries,

        @Schema(description = "선택한 국가에 속한 도시 목록")
        List<City> cities,

        @Schema(description = "선택 조건에 맞는 매장 목록")
        List<Store> stores
) {

    public record City(
            @Schema(description = "도시명", example = "서울")
            String city
    ) {
    }

    public record Store(
            @Schema(description = "매장 ID", example = "1")
            Long storeId,

            @Schema(description = "매장명", example = "MCM 신세계 본점")
            String storeName
    ) {
    }
}
