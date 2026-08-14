package com.hufsglobalion.glupshroom.journey;

import com.hufsglobalion.glupshroom.journey.dto.JourneySaveRequest;
import com.hufsglobalion.glupshroom.journey.dto.JourneySaveResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/journeys")
@RequiredArgsConstructor
public class JourneyController {

    private final JourneyService journeyService;

    @PostMapping
    public ResponseEntity<JourneySaveResponse> saveJourney(
            @RequestBody @Valid JourneySaveRequest request
    ) {
        Long tempUserId = 1L; // TODO: 인증(JWT) 연동되면 실제 로그인 유저 id로 교체
        JourneySaveResponse response = journeyService.saveJourney(tempUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}