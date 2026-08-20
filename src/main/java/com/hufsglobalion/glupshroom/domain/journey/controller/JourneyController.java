package com.hufsglobalion.glupshroom.domain.journey.controller;

import com.hufsglobalion.glupshroom.domain.journey.dto.request.JourneySaveRequest;
import com.hufsglobalion.glupshroom.domain.journey.dto.request.RecallRegenerateRequest;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneyAnalyzeResponse;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneyDetailResponse;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneyListResponse;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneySaveResponse;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.OnThisDayResponse;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.RecallRegenerateResponse;
import com.hufsglobalion.glupshroom.domain.journey.service.JourneyService;
import com.hufsglobalion.glupshroom.domain.journey.service.OnThisDayService;
import com.hufsglobalion.glupshroom.global.common.ApiResponse;
import com.hufsglobalion.glupshroom.domain.journey.dto.request.JourneyUpdateRequest;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.JourneyUpdateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Journey", description = "여정 저장/조회 관련 API")
@RestController
@RequestMapping("/api/v1/mcarry")
@RequiredArgsConstructor
public class JourneyController {

    private final JourneyService journeyService;
    private final OnThisDayService onThisDayService;

    @Operation(summary = "여정 저장", description = "AI 큐레이터 결과와 사용자 수정값을 받아 여정을 최종 저장합니다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/journeys")
    public ApiResponse<JourneySaveResponse> saveJourney(@RequestBody @Valid JourneySaveRequest request) {
        JourneySaveResponse response = journeyService.saveJourney(request);
        return ApiResponse.of("S201", "여정 저장에 성공했습니다", response);
    }

    @Operation(summary = "AI 여정 큐레이터", description = "업로드한 사진을 분석해 여정 태그와 회고 문장 초안을 생성합니다. 저장은 하지 않습니다.")
    @PostMapping(value = "/journeys/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<JourneyAnalyzeResponse> analyzeJourney(
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam Long productId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "emotional") String tone
    ) {
        JourneyAnalyzeResponse response = journeyService.analyzeJourney(productId, userId, tone, photo);
        return ApiResponse.success("AI 분석이 완료되었습니다", response);
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

    @Operation(summary = "온 디스 데이 조회", description = "오늘과 같은 월·일에 작성한 본인 과거 여정 1건을 조회합니다.")
    @GetMapping("/products/{productId}/on-this-day")
    public ApiResponse<OnThisDayResponse> getOnThisDay(
            @PathVariable Long productId,
            @RequestParam(required = false) Long userId
    ) {
        OnThisDayResponse response = onThisDayService.getOnThisDay(productId, userId);
        String message = response.journey() == null
                ? "오늘 회상할 여정이 없습니다"
                : "온 디스 데이 여정을 조회했습니다";
        return ApiResponse.success(message, response);
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

    @Operation(summary = "여정 삭제", description = "저장된 여정을 삭제합니다. 본인이 작성한 여정만 삭제 가능합니다.")
    @DeleteMapping("/journeys/{journeyId}")
    public ApiResponse<Void> deleteJourney(
            @PathVariable Long journeyId,
            @RequestParam(required = false) Long userId
    ) {
        journeyService.deleteJourney(journeyId, userId);
        return ApiResponse.success("여정 삭제에 성공했습니다", null);
    }

    @Operation(summary = "회고 문장 재생성", description = "저장된 여정의 태그를 재료로 AI가 회고 문장을 다시 생성해 반영합니다.")
    @PostMapping("/journeys/{journeyId}/recall")
    public ApiResponse<RecallRegenerateResponse> regenerateRecall(
            @PathVariable Long journeyId,
            @RequestBody @Valid RecallRegenerateRequest request
    ) {
        RecallRegenerateResponse response = journeyService.regenerateRecall(journeyId, request.userId(), request.tone());
        return ApiResponse.success("회고 문장이 재생성되었습니다", response);
    }
}
