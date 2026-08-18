package com.hufsglobalion.glupshroom.domain.transfer.client;

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
public class OpenAiLetterDraftClient {

    private static final String CHAT_COMPLETIONS_URL = "https://api.openai.com/v1/chat/completions";
    private static final String SYSTEM_PROMPT = """
            당신은 사용자가 오랫동안 사용한 물건을 다음 주인에게 넘기며 남기는 '계승 편지'의 초안을 작성하는 도우미입니다.
            주어진 여정 기록을 참고해서, 따뜻하고 진심 어린 톤의 한국어 편지를 3~5문장으로 작성하세요.
            사실을 과장하지 말고, 여정 기록에 없는 내용은 지어내지 마세요.
            특정 인물(이름), 사적인 사건, 개인정보는 언급하지 마세요.
            도시·기간·거쳐온 여정처럼 이 물건이 함께한 여정에 집중하세요.
            """;

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public OpenAiLetterDraftClient(
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

    public String generateDraft(String journeySummary) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content", SYSTEM_PROMPT),
                            Map.of("role", "user", "content", journeySummary)
                    ),
                    "temperature", 0.8
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
            log.error("OpenAI 편지 초안 생성 실패", e);
            throw new CustomException(ErrorCode.LETTER_DRAFT_GENERATION_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    private String extractContent(Map<String, Object> response) {
        if (response == null) {
            throw new CustomException(ErrorCode.LETTER_DRAFT_GENERATION_FAILED);
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new CustomException(ErrorCode.LETTER_DRAFT_GENERATION_FAILED);
        }

        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        Object content = message == null ? null : message.get("content");
        if (content == null) {
            throw new CustomException(ErrorCode.LETTER_DRAFT_GENERATION_FAILED);
        }

        return content.toString().trim();
    }
}
