package com.hufsglobalion.glupshroom.domain.resell.service;

import com.hufsglobalion.glupshroom.domain.product.entity.Product;
import com.hufsglobalion.glupshroom.domain.product.repository.ProductRepository;
import com.hufsglobalion.glupshroom.domain.resell.dto.request.ResellSaveRequest;
import com.hufsglobalion.glupshroom.domain.resell.dto.response.ResellSaveResponse;
import com.hufsglobalion.glupshroom.domain.resell.entity.Resell;
import com.hufsglobalion.glupshroom.domain.resell.entity.ResellPhoto;
import com.hufsglobalion.glupshroom.domain.resell.repository.ResellPhotoRepository;
import com.hufsglobalion.glupshroom.domain.resell.repository.ResellRepository;
import com.hufsglobalion.glupshroom.global.exception.CustomException;
import com.hufsglobalion.glupshroom.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResellService {

    private final ResellRepository resellRepository;
    private final ResellPhotoRepository resellPhotoRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ResellSaveResponse saveResell(ResellSaveRequest request) {
        if (request.sellerId() == null || request.productId() == null || request.price() == null
                || request.photoUrls() == null || request.photoUrls().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getCurrentOwnerId().equals(request.sellerId())) {
            throw new CustomException(ErrorCode.RESELL_SELLER_MISMATCH);
        }

        Resell resell = Resell.builder()
                .productId(request.productId())
                .sellerId(request.sellerId())
                .price(request.price())
                .conditionGrade(request.conditionGrade())
                .letterShared(request.letterShared() != null ? request.letterShared() : false)
                .caretipShared(request.caretipShared() != null ? request.caretipShared() : false)
                .build();

        Resell saved = resellRepository.save(resell);

        if (request.photoUrls() != null && !request.photoUrls().isEmpty()) {
            List<ResellPhoto> photos = new java.util.ArrayList<>();
            for (int i = 0; i < request.photoUrls().size(); i++) {
                photos.add(ResellPhoto.builder()
                        .resellId(saved.getId())
                        .photoUrl(request.photoUrls().get(i))
                        .sortOrder(i)
                        .build());
            }
            resellPhotoRepository.saveAll(photos);
        }

        return new ResellSaveResponse(
                saved.getProductId(),
                saved.getPrice(),
                "active",
                saved.getId()
        );
    }
}
