package com.hufsglobalion.glupshroom.domain.journey.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hufsglobalion.glupshroom.global.exception.CustomException;
import com.hufsglobalion.glupshroom.global.exception.ErrorCode;
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
public class OpenAiVisionClient {

    private static final String CHAT_COMPLETIONS_URL = "https://api.openai.com/v1/chat/completions";
    private static final String SYSTEM_PROMPT = """
            당신은 사용자가 업로드한 사진을 보고, 아끼는 물건과 함께한 여정을 분석하는 도우미입니다.
            사진 속 장면을 보고 활동(activityTag)·상황(situationTag)·스타일(styleTag) 태그를 각각 한 단어~짧은 구로 뽑고,
            그 장면을 바탕으로 감성적인 회고 문장(recallText)을 1~2문장으로 작성하세요.
            activityTag, situationTag, styleTag, recallText 값은 전부 반드시 한국어로 작성하세요. 영어를 섞지 마세요.
            사실을 지어내지 말고, 사진에서 실제로 보이는 것에 근거하세요.
            특정 인물(이름), 사적인 사건, 개인정보는 언급하지 마세요.
            반드시 아래 JSON 형식으로만, 다른 텍스트 없이 응답하세요:
            {"activityTag": "...", "situationTag": "...", "styleTag": "...", "recallText": "..."}
            """;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public OpenAiVisionClient(
            @Value("${openai.api-key:}") String apiKey,
            @Value("${openai.model:gpt-4o-mini}") String model
    ) {
        this.apiKey = apiKey;
        this.model = model;
        this.objectMapper = new ObjectMapper();

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(20000);
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    public VisionAnalysisResult analyze(byte[] imageBytes, String contentType, String tone) {
        try {
            String dataUrl = "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(imageBytes);

            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "response_format", Map.of("type", "json_object"),
                    "messages", List.of(
                            Map.of("role", "system", "content", SYSTEM_PROMPT),
                            Map.of("role", "user", "content", List.of(
                                    Map.of("type", "text", "text", "회고 톤: " + tone),
                                    Map.of("type", "image_url", "image_url", Map.of("url", dataUrl))
                            ))
                    ),
                    "temperature", 0.7
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
            log.error("AI 여정 분석 실패", e);
            throw new CustomException(ErrorCode.JOURNEY_ANALYSIS_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    private VisionAnalysisResult parseResult(Map<String, Object> response) {
        if (response == null) {
            throw new CustomException(ErrorCode.JOURNEY_ANALYSIS_FAILED);
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new CustomException(ErrorCode.JOURNEY_ANALYSIS_FAILED);
        }

        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        Object content = message == null ? null : message.get("content");
        if (content == null) {
            throw new CustomException(ErrorCode.JOURNEY_ANALYSIS_FAILED);
        }

        try {
            Map<String, Object> parsed = objectMapper.readValue(content.toString(), Map.class);
            return new VisionAnalysisResult(
                    stringOrNull(parsed.get("activityTag")),
                    stringOrNull(parsed.get("situationTag")),
                    stringOrNull(parsed.get("styleTag")),
                    stringOrNull(parsed.get("recallText"))
            );
        } catch (Exception e) {
            log.error("AI 응답 파싱 실패: {}", content, e);
            throw new CustomException(ErrorCode.JOURNEY_ANALYSIS_FAILED);
        }
    }

    private String stringOrNull(Object value) {
        return value == null ? null : value.toString();
    }
}
