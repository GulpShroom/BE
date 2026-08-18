package com.hufsglobalion.glupshroom.domain.journey.service;

import com.hufsglobalion.glupshroom.domain.journey.dto.response.OnThisDayResponse;
import com.hufsglobalion.glupshroom.domain.journey.dto.response.OnThisDayResponse.OnThisDayJourney;
import com.hufsglobalion.glupshroom.domain.journey.entity.Journey;
import com.hufsglobalion.glupshroom.domain.ownership.service.OwnershipService;
import com.hufsglobalion.glupshroom.domain.product.service.ProductService;
import com.hufsglobalion.glupshroom.global.exception.CustomException;
import com.hufsglobalion.glupshroom.global.exception.ErrorCode;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OnThisDayService {

    private static final ZoneId KST_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final ProductService productService;
    private final OwnershipService ownershipService;
    private final JourneyService journeyService;

    public OnThisDayResponse getOnThisDay(Long productId, Long userId) {
        try {
            productService.getProduct(productId);

            if (userId == null) {
                throw new CustomException(ErrorCode.ON_THIS_DAY_USER_ID_REQUIRED);
            }

            LocalDate baseDate = LocalDate.now(KST_ZONE_ID);
            if (!ownershipService.isLineageParticipant(productId, userId)) {
                return noJourney(baseDate);
            }

            Journey journey = journeyService.findJourneysWithExif(productId, userId).stream()
                    .filter(candidate -> isSameMonthAndDayInPast(candidate, baseDate))
                    .findFirst()
                    .orElse(null);

            if (journey == null) {
                return noJourney(baseDate);
            }

            return new OnThisDayResponse(
                    null,
                    new OnThisDayJourney(
                            journey.getId(),
                            baseDate.getYear() - journey.getExifTakenAt().getYear(),
                            journey.getCountry(),
                            journey.getCity(),
                            journey.getRecallText(),
                            journey.getPhotoUrl()
                    )
            );
        } catch (DataAccessException e) {
            log.error("On this day journey lookup failed. productId={}, userId={}", productId, userId, e);
            throw new CustomException(ErrorCode.ON_THIS_DAY_RETRIEVAL_FAILED);
        }
    }

    private boolean isSameMonthAndDayInPast(Journey journey, LocalDate baseDate) {
        LocalDate takenDate = journey.getExifTakenAt().toLocalDate();
        return takenDate.isBefore(baseDate)
                && takenDate.getMonthValue() == baseDate.getMonthValue()
                && takenDate.getDayOfMonth() == baseDate.getDayOfMonth();
    }

    private OnThisDayResponse noJourney(LocalDate baseDate) {
        return new OnThisDayResponse(baseDate, null);
    }
}
