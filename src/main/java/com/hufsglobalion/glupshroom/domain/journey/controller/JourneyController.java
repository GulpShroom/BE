package com.hufsglobalion.glupshroom.domain.journey.controller;

import com.hufsglobalion.glupshroom.domain.journey.dto.request.JourneySaveRequest;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneySaveResponse;
import com.hufsglobalion.glupshroom.domain.journey.service.JourneyService;
import com.hufsglobalion.glupshroom.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Journey", description = "여정 저장/조회 관련 API")
@RestController
@RequestMapping("/api/v1/mcarry/journeys")
@RequiredArgsConstructor
public class JourneyController {

    private final JourneyService journeyService;

    @Operation(summary = "여정 저장", description = "AI 큐레이터 결과와 사용자 수정값을 받아 여정을 최종 저장합니다.")
    @PostMapping
    public ApiResponse<JourneySaveResponse> saveJourney(@RequestBody @Valid JourneySaveRequest request) {
        JourneySaveResponse response = journeyService.saveJourney(request);
        return ApiResponse.of("S201", "여정 저장에 성공했습니다", response);
    }
}