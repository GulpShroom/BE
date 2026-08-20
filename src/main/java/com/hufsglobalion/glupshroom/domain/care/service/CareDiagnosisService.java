package com.hufsglobalion.glupshroom.domain.care.service;

import com.hufsglobalion.glupshroom.domain.care.client.ConditionAnalysisResult;
import com.hufsglobalion.glupshroom.domain.care.client.OpenAiConditionDiagnosisClient;
import com.hufsglobalion.glupshroom.domain.care.client.PhotoPayload;
import com.hufsglobalion.glupshroom.domain.care.dto.response.CareDiagnosisHistoryResponse;
import com.hufsglobalion.glupshroom.domain.care.dto.response.CareDiagnosisResponse;
import com.hufsglobalion.glupshroom.domain.care.entity.CareDiagnosis;
import com.hufsglobalion.glupshroom.domain.care.repository.CareDiagnosisRepository;
import com.hufsglobalion.glupshroom.domain.product.entity.Product;
import com.hufsglobalion.glupshroom.domain.product.service.ProductService;
import com.hufsglobalion.glupshroom.global.exception.CustomException;
import com.hufsglobalion.glupshroom.global.exception.ErrorCode;
import com.hufsglobalion.glupshroom.global.storage.FileStorageService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CareDiagnosisService {

    private final CareDiagnosisRepository careDiagnosisRepository;
    private final ProductService productService;
    private final OpenAiConditionDiagnosisClient conditionDiagnosisClient;
    private final FileStorageService fileStorageService;

    @Value("${app.repair.link-url}")
    private String repairLinkUrl;

    public CareDiagnosisResponse diagnose(Long productId, Long userId, List<MultipartFile> photos) {
        if (photos == null || photos.isEmpty() || photos.stream().anyMatch(MultipartFile::isEmpty)) {
            throw new CustomException(ErrorCode.DIAGNOSIS_PHOTO_REQUIRED);
        }

        Product product = productService.getProduct(productId);

        if (!product.getCurrentOwnerId().equals(userId)) {
            throw new CustomException(ErrorCode.JOURNEY_ANALYSIS_FORBIDDEN);
        }

        List<PhotoPayload> payloads = photos.stream()
                .map(this::toPayload)
                .toList();

        ConditionAnalysisResult result = conditionDiagnosisClient.analyze(payloads);
        String diagnosisPhotoUrl = fileStorageService.store(photos.get(0));

        try {
            CareDiagnosis diagnosis = careDiagnosisRepository.save(CareDiagnosis.builder()
                    .productId(product.getId())
                    .generation(product.getCurrentGeneration())
                    .conditionGrade(result.conditionGrade())
                    .resultText(result.resultText())
                    .solutionText(result.solutionText())
                    .diagnosisPhotoUrl(diagnosisPhotoUrl)
                    .aiGenerated(true)
                    .build());

            return new CareDiagnosisResponse(
                    diagnosis.getId(),
                    diagnosis.getGeneration(),
                    diagnosis.getDiagnosedAt(),
                    diagnosis.getConditionGrade(),
                    diagnosis.getResultText(),
                    diagnosis.getSolutionText(),
                    repairLinkUrl
            );
        } catch (DataAccessException e) {
            log.error("Care diagnosis save failed. productId={}", productId, e);
            throw new CustomException(ErrorCode.DIAGNOSIS_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public CareDiagnosisHistoryResponse getDiagnosisHistory(Long productId) {
        productService.getProduct(productId);

        try {
            List<CareDiagnosisHistoryResponse.DiagnosisSummary> diagnoses = careDiagnosisRepository
                    .findByProductIdOrderByGenerationAsc(productId).stream()
                    .map(diagnosis -> new CareDiagnosisHistoryResponse.DiagnosisSummary(
                            diagnosis.getId(),
                            diagnosis.getGeneration(),
                            diagnosis.getDiagnosedAt(),
                            diagnosis.getConditionGrade(),
                            diagnosis.getResultText(),
                            diagnosis.getSolutionText()
                    ))
                    .toList();

            return new CareDiagnosisHistoryResponse(diagnoses);
        } catch (DataAccessException e) {
            log.error("Care diagnosis history retrieval failed. productId={}", productId, e);
            throw new CustomException(ErrorCode.DIAGNOSIS_HISTORY_RETRIEVAL_FAILED);
        }
    }

    private PhotoPayload toPayload(MultipartFile photo) {
        try {
            return new PhotoPayload(photo.getBytes(), photo.getContentType());
        } catch (IOException e) {
            throw new CustomException(ErrorCode.DIAGNOSIS_FAILED);
        }
    }
}
