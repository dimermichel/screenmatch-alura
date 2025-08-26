package com.michelmaia.screenmach.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;

@Service
public class ChatGPTGet {

    public static String getTranslation(String text) {
        // Check if an API key is available
        final String OPENAI_KEY = System.getenv("OPENAI_API_KEY");
        if (OPENAI_KEY == null || OPENAI_KEY.trim().isEmpty()) {
            // Return mock translation if no API key is configured
            return "[Mock Translation] " + text;
        }

        try {
            OpenAiService service = new OpenAiService(OPENAI_KEY);

            CompletionRequest request = CompletionRequest.builder()
                    .model("gpt-3.5-turbo-instruct")
                    .prompt("traduza para o português o texto: " + text)
                    .maxTokens(1000)
                    .temperature(0.7)
                    .build();

            var response = service.createCompletion(request);
            return response.getChoices().get(0).getText();
        } catch (Exception e) {
            // Fallback to mock translation if the API call fails
            return "[Translation Error] " + text;
        }
    }
}
