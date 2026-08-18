package com.hufsglobalion.glupshroom.domain.journey.controller;

import com.hufsglobalion.glupshroom.domain.journey.dto.request.JourneySaveRequest;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneyDetailResponse;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneyListResponse;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneySaveResponse;
import com.hufsglobalion.glupshroom.domain.journey.service.JourneyService;
import com.hufsglobalion.glupshroom.global.common.ApiResponse;
import com.hufsglobalion.glupshroom.domain.journey.dto.request.JourneyUpdateRequest;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneyUpdateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Journey", description = "여정 저장/조회 관련 API")
@RestController
@RequestMapping("/api/v1/mcarry")
@RequiredArgsConstructor
public class JourneyController {

    private final JourneyService journeyService;

    @Operation(summary = "여정 저장", description = "AI 큐레이터 결과와 사용자 수정값을 받아 여정을 최종 저장합니다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/journeys")
    public ApiResponse<JourneySaveResponse> saveJourney(@RequestBody @Valid JourneySaveRequest request) {
        JourneySaveResponse response = journeyService.saveJourney(request);
        return ApiResponse.of("S201", "여정 저장에 성공했습니다", response);
    }

    @Operation(summary = "여정 상세 조회", description = "여정 1건의 상세 정보를 조회합니다. 본인이 작성한 여정인지 여부(isAuthor)와 전체 필드를 반환합니다.")
    @GetMapping("/journeys/{journeyId}")
    public ApiResponse<JourneyDetailResponse> getJourneyDetail(
            @PathVariable Long journeyId,
            @RequestParam Long userId
    ) {
        JourneyDetailResponse response = journeyService.getJourneyDetail(journeyId, userId);
        return ApiResponse.success("여정 상세 조회에 성공했습니다", response);
    }

    @Operation(summary = "여정 목록 조회", description = "제품의 여정 목록을 조회합니다. 본인이 작성한 기록만 반환하며 날짜/국가별 정렬을 지원합니다.")
    @GetMapping("/products/{productId}/journeys")
    public ApiResponse<JourneyListResponse> getJourneyList(
            @PathVariable Long productId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        JourneyListResponse response = journeyService.getJourneyList(productId, userId, sort, page, size);
        return ApiResponse.success("여정 목록 조회에 성공했습니다", response);
    }

    @Operation(summary = "여정 수정", description = "저장된 여정을 수정합니다. 본인이 작성한 여정만 수정 가능하며, EXIF로 촬영 날짜가 확인된 경우 시점은 수정할 수 없습니다.")
    @PatchMapping("/journeys/{journeyId}")
    public ApiResponse<JourneyUpdateResponse> updateJourney(
            @PathVariable Long journeyId,
            @RequestBody JourneyUpdateRequest request
    ) {
        JourneyUpdateResponse response = journeyService.updateJourney(journeyId, request);
        return ApiResponse.success("여정 수정에 성공했습니다", response);
    }
}