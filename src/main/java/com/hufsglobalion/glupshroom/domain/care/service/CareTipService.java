package com.hufsglobalion.glupshroom.domain.care.service;

import com.hufsglobalion.glupshroom.domain.care.dto.request.CareTipCreateRequest;
import com.hufsglobalion.glupshroom.domain.care.dto.response.CareTipCreateResponse;
import com.hufsglobalion.glupshroom.domain.care.entity.CareTip;
import com.hufsglobalion.glupshroom.domain.care.repository.CareTipRepository;
import com.hufsglobalion.glupshroom.domain.product.entity.Product;
import com.hufsglobalion.glupshroom.domain.product.service.ProductService;
import com.hufsglobalion.glupshroom.domain.user.service.UserService;
import com.hufsglobalion.glupshroom.global.exception.CustomException;
import com.hufsglobalion.glupshroom.global.exception.ErrorCode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CareTipService {

    private static final ZoneOffset KST_OFFSET = ZoneOffset.ofHours(9);

    private final CareTipRepository careTipRepository;
    private final ProductService productService;
    private final UserService userService;

    public CareTipCreateResponse createCareTip(Long productId, CareTipCreateRequest request) {
        try {
            Product product = productService.getProduct(productId);

            if (request.authorId() == null) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            userService.getUser(request.authorId());

            if (!product.getCurrentOwnerId().equals(request.authorId())) {
                throw new CustomException(ErrorCode.CARE_TIP_WRITE_FORBIDDEN);
            }

            if (request.content() == null || request.content().isBlank()) {
                throw new CustomException(ErrorCode.CARE_TIP_CONTENT_REQUIRED);
            }

            CareTip careTip = careTipRepository.save(CareTip.builder()
                    .productId(product.getId())
                    .authorId(request.authorId())
                    .generation(product.getCurrentGeneration())
                    .content(request.content())
                    .inheritSelected(false)
                    .build());

            return new CareTipCreateResponse(
                    careTip.getId(),
                    careTip.isInheritSelected(),
                    toKst(careTip.getCreatedAt())
            );
        } catch (DataAccessException e) {
            log.error("Care tip creation failed. productId={}", productId, e);
            throw new CustomException(ErrorCode.CARE_TIP_SAVE_FAILED);
        }
    }

    private OffsetDateTime toKst(java.time.LocalDateTime createdAt) {
        return createdAt.atOffset(KST_OFFSET);
    }
}
