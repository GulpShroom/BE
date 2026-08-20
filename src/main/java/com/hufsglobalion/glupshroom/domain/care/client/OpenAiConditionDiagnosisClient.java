package com.hufsglobalion.glupshroom.domain.care.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hufsglobalion.glupshroom.global.exception.CustomException;
import com.hufsglobalion.glupshroom.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class OpenAiConditionDiagnosisClient {

    private static final String CHAT_COMPLETIONS_URL = "https://api.openai.com/v1/chat/completions";
    private static final String SYSTEM_PROMPT = """
            당신은 가죽 제품의 마모·손상 상태를 사진으로 진단하는 전문가입니다.
            여러 장의 사진을 보고 제품의 상태를 1~5단계 등급(1=매우 나쁨, 5=매우 좋음)으로 평가하고,
            발견된 문제점(resultText)과 케어 솔루션(solutionText)을 각각 1~2문장으로 작성하세요.
            conditionGrade는 1~5 사이의 정수여야 합니다.
            resultText, solutionText는 반드시 한국어로 작성하세요. 영어를 섞지 마세요.
            사실을 지어내지 말고, 사진에서 실제로 보이는 마모·손상 흔적에 근거하세요.
            반드시 아래 JSON 형식으로만, 다른 텍스트 없이 응답하세요:
            {"conditionGrade": 1~5 사이 정수, "resultText": "...", "solutionText": "..."}
            """;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public OpenAiConditionDiagnosisClient(
            @Value("${openai.api-key:}") String apiKey,
            @Value("${openai.model:gpt-4o-mini}") String model
    ) {
        this.apiKey = apiKey;
        this.model = model;
        this.objectMapper = new ObjectMapper();

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(30000);
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    public ConditionAnalysisResult analyze(List<PhotoPayload> photos) {
        try {
            List<Map<String, Object>> content = new ArrayList<>();
            content.add(Map.of("type", "text", "text", "제품 상태를 진단해주세요."));
            for (PhotoPayload photo : photos) {
                String dataUrl = "data:" + photo.contentType() + ";base64,"
                        + Base64.getEncoder().encodeToString(photo.bytes());
                content.add(Map.of("type", "image_url", "image_url", Map.of("url", dataUrl)));
            }

            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "response_format", Map.of("type", "json_object"),
                    "messages", List.of(
                            Map.of("role", "system", "content", SYSTEM_PROMPT),
                            Map.of("role", "user", "content", content)
                    ),
                    "temperature", 0.3
            );

            Map<String, Object> response = restClient.post()
                    .uri(CHAT_COMPLETIONS_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            return parseResult(response);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI 상태 진단 실패", e);
            throw new CustomException(ErrorCode.DIAGNOSIS_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    private ConditionAnalysisResult parseResult(Map<String, Object> response) {
        if (response == null) {
            throw new CustomException(ErrorCode.DIAGNOSIS_FAILED);
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new CustomException(ErrorCode.DIAGNOSIS_FAILED);
        }

        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        Object content = message == null ? null : message.get("content");
        if (content == null) {
            throw new CustomException(ErrorCode.DIAGNOSIS_FAILED);
        }

        try {
            Map<String, Object> parsed = objectMapper.readValue(content.toString(), Map.class);
            Integer conditionGrade = parsed.get("conditionGrade") == null
                    ? null
                    : ((Number) parsed.get("conditionGrade")).intValue();

            return new ConditionAnalysisResult(
                    conditionGrade,
                    stringOrNull(parsed.get("resultText")),
                    stringOrNull(parsed.get("solutionText"))
            );
        } catch (Exception e) {
            log.error("AI 응답 파싱 실패: {}", content, e);
            throw new CustomException(ErrorCode.DIAGNOSIS_FAILED);
        }
    }

    private String stringOrNull(Object value) {
        return value == null ? null : value.toString();
    }
}
