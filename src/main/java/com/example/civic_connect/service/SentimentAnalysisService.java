package com.example.civic_connect.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class SentimentAnalysisService {

    private final WebClient webClient;

    @Value("${huggingface.api.token}")
    private String apiToken;

    // We inject the WebClient.Builder and configure it once
    public SentimentAnalysisService(WebClient.Builder webClientBuilder, 
                                    @Value("${huggingface.api.url}") String apiUrl) {
        this.webClient = webClientBuilder
            .baseUrl(apiUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    public String analyzeSentiment(String text) {
        // We use a simple Map to build the JSON: {"inputs": "some text"}
        Map<String, String> requestBody = Map.of("inputs", text);

        try {
            // Call the API
            JsonNode response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block(); // .block() makes this synchronous, which is fine for our app

            // The response is a nested JSON array: [[{'label': 'POSITIVE', 'score': 0.99...}]]
            // We just want to get the "label"
            if (response != null && response.isArray() && response.get(0).isArray()) {
                String label = response.get(0).get(0).get("label").asText();
                return label.toUpperCase(); // Returns "POSITIVE" or "NEGATIVE"
            }
        } catch (Exception e) {
            // If the API fails, just log it and return a neutral sentiment
            System.err.println("Error calling Hugging Face API: " + e.getMessage());
            return "NEUTRAL";
        }
        return "NEUTRAL";
    }
}