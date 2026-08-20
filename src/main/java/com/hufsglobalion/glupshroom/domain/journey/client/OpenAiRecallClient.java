package com.hufsglobalion.glupshroom.domain.journey.client;

import com.hufsglobalion.glupshroom.global.exception.CustomException;
import com.hufsglobalion.glupshroom.global.exception.ErrorCode;
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
public class OpenAiRecallClient {

    private static final String CHAT_COMPLETIONS_URL = "https://api.openai.com/v1/chat/completions";
    private static final String SYSTEM_PROMPT = """
            당신은 사용자가 아끼는 물건과 함께한 여정의 회고 문장을 다시 써주는 도우미입니다.
            주어진 태그(활동/상황/스타일)와 장소·시점을 재료로, 요청한 톤에 맞춰 1~2문장의 한국어 회고 문장을 새로 생성하세요.
            같은 재료로 매번 다른 표현을 만들어내세요.
            사실을 지어내지 말고, 주어진 정보에서만 근거하세요.
            특정 인물(이름), 사적인 사건, 개인정보는 언급하지 마세요.
            문장만 출력하고, 따옴표나 다른 설명은 붙이지 마세요.
            """;

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public OpenAiRecallClient(
            @Value("${openai.api-key:}") String apiKey,
            @Value("${openai.model:gpt-4o-mini}") String model
    ) {
        this.apiKey = apiKey;
        this.model = model;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(15000);
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    public String regenerate(String journeySummary, String tone) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content", SYSTEM_PROMPT),
                            Map.of("role", "user", "content", "톤: " + tone + "\n" + journeySummary)
                    ),
                    "temperature", 0.9
            );

            Map<String, Object> response = restClient.post()
                    .uri(CHAT_COMPLETIONS_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            return extractContent(response);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("회고 문장 재생성 실패", e);
            throw new CustomException(ErrorCode.RECALL_REGENERATION_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    private String extractContent(Map<String, Object> response) {
        if (response == null) {
            throw new CustomException(ErrorCode.RECALL_REGENERATION_FAILED);
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new CustomException(ErrorCode.RECALL_REGENERATION_FAILED);
        }

        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        Object content = message == null ? null : message.get("content");
        if (content == null) {
            throw new CustomException(ErrorCode.RECALL_REGENERATION_FAILED);
        }

        return content.toString().trim();
    }
}
