package com.hufsglobalion.glupshroom.domain.resell.service;

import com.hufsglobalion.glupshroom.domain.product.entity.Product;
import com.hufsglobalion.glupshroom.domain.product.repository.ProductRepository;
import com.hufsglobalion.glupshroom.domain.resell.dto.request.ResellSaveRequest;
import com.hufsglobalion.glupshroom.domain.resell.dto.response.ResellListResponse;
import com.hufsglobalion.glupshroom.domain.resell.dto.response.ResellSaveResponse;
import com.hufsglobalion.glupshroom.domain.resell.entity.Resell;
import com.hufsglobalion.glupshroom.domain.resell.entity.ResellPhoto;
import com.hufsglobalion.glupshroom.domain.resell.repository.ResellPhotoRepository;
import com.hufsglobalion.glupshroom.domain.resell.repository.ResellRepository;
import com.hufsglobalion.glupshroom.global.exception.CustomException;
import com.hufsglobalion.glupshroom.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ResellService {

    private final ResellRepository resellRepository;
    private final ResellPhotoRepository resellPhotoRepository;
    private final ProductRepository productRepository;

    private static final Set<String> VALID_STATUSES = Set.of("active", "completed");
    private static final Set<String> VALID_ROLES = Set.of("seller", "buyer");

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

    @Transactional(readOnly = true)
    public ResellListResponse getResellList(String status, String role, Long userId, int page, int size) {
        if (!VALID_STATUSES.contains(status)) {
            throw new CustomException(ErrorCode.INVALID_RESELL_FILTER);
        }
        if (role != null && !VALID_ROLES.contains(role)) {
            throw new CustomException(ErrorCode.INVALID_RESELL_FILTER);
        }
        if (role != null && userId == null) {
            throw new CustomException(ErrorCode.INVALID_RESELL_FILTER);
        }
        if (role == null && userId != null) {
            throw new CustomException(ErrorCode.INVALID_RESELL_FILTER);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Resell> resellPage;
        if (role == null) {
            resellPage = resellRepository.findByPostStatus(status, pageable);
        } else if ("seller".equals(role)) {
            resellPage = resellRepository.findByPostStatusAndSellerId(status, userId, pageable);
        } else {
            resellPage = resellRepository.findByPostStatusAndBuyerId(status, userId, pageable);
        }

        List<ResellListResponse.ResellSummary> summaries = resellPage.getContent().stream()
                .map(resell -> {
                    Product product = productRepository.findById(resell.getProductId()).orElse(null);
                    String nickname = product != null ? product.getOfficialName() : null;
                    Integer provenanceScore = product != null ? product.getProvenanceScore() : null;
                    return new ResellListResponse.ResellSummary(
                            resell.getId(),
                            nickname,
                            resell.getPrice(),
                            resell.getPostStatus(),
                            provenanceScore,
                            resell.getConditionGrade()
                    );
                })
                .toList();

        return new ResellListResponse(resellPage.getTotalElements(), summaries);
    }

    @Transactional
    public void deleteResell(Long resellId, Long sellerId) {
        if (sellerId == null) {
            throw new CustomException(ErrorCode.RESELL_INVALID_REQUESTER);
        }

        Resell resell = resellRepository.findById(resellId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESELL_NOT_FOUND));

        if (!resell.getSellerId().equals(sellerId)) {
            throw new CustomException(ErrorCode.RESELL_DELETE_FORBIDDEN);
        }

        if (resell.getBuyerId() != null && !"completed".equals(resell.getPostStatus())) {
            throw new CustomException(ErrorCode.RESELL_TRADE_IN_PROGRESS);
        }

        resellPhotoRepository.deleteByResellId(resellId);
        resellRepository.delete(resell);
    }
}
