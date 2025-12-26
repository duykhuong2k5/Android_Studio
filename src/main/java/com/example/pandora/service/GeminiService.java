package com.example.pandora.service;

import com.example.pandora.dto.request.CharacterRequest;
import com.example.pandora.dto.request.CreateStoryRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.model:gemini-pro}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Generate story content (both English and Vietnamese) using Google Gemini
     */
    public Map<String, String> generateStoryContent(CreateStoryRequest request) {
        try {
            String prompt = buildStoryPrompt(request);
            
            // Gemini API request format
            Map<String, Object> requestBody = new HashMap<>();
            
            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(Map.of("text", prompt)));
            
            requestBody.put("contents", List.of(content));
            
            // Generation config
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.8);
            generationConfig.put("maxOutputTokens", 2000);
            requestBody.put("generationConfig", generationConfig);

            String urlWithKey = geminiApiUrl + "?key=" + geminiApiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
            headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            log.info("Calling Gemini API to generate story...");
            ResponseEntity<String> response = restTemplate.exchange(
                urlWithKey,
                HttpMethod.POST,
                entity,
                String.class
            );

            return parseStoryResponse(response.getBody());

        } catch (Exception e) {
            log.error("Error generating story with Gemini: ", e);
            return getDefaultStory(request);
        }
    }

    /**
     * Generate character description for image generation
     */
    public String generateCharacterDescription(CharacterRequest character, String storyContext) {
        try {
            String prompt = String.format(
                "Create a detailed visual description for a children's story character named %s, " +
                "age %s, gender %s, role %s. The character is part of a %s story. " +
                "Describe their appearance, clothing, and distinctive features in a child-friendly way. " +
                "Keep it suitable for generating a cartoon-style illustration. Maximum 100 words.",
                character.getName(),
                character.getAge(),
                character.getGender(),
                character.getRole(),
                storyContext
            );

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(Map.of("text", prompt)));
            requestBody.put("contents", List.of(content));

            String urlWithKey = geminiApiUrl + "?key=" + geminiApiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
            headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                urlWithKey,
                HttpMethod.POST,
                entity,
                String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("candidates").get(0)
                      .path("content").path("parts").get(0)
                      .path("text").asText();

        } catch (Exception e) {
            log.error("Error generating character description: ", e);
            return String.format("A %s year old %s character named %s", 
                character.getAge(), character.getGender(), character.getName());
        }
    }

    private String buildStoryPrompt(CreateStoryRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Create a children's story with the following requirements:\n\n");
        prompt.append("Topic: ").append(request.getTopic()).append("\n");
        prompt.append("Style: ").append(request.getStyle()).append("\n");
        
        if (request.getCharacters() != null && !request.getCharacters().isEmpty()) {
            prompt.append("Characters:\n");
            for (CharacterRequest character : request.getCharacters()) {
                prompt.append("- ").append(character.getName())
                      .append(" (Age: ").append(character.getAge())
                      .append(", Gender: ").append(character.getGender())
                      .append(", Role: ").append(character.getRole()).append(")\n");
            }
        }

        prompt.append("\nRequirements:\n");
        prompt.append("1. Create a story suitable for children aged 4-8 years\n");
        prompt.append("2. Include educational elements and positive values\n");
        prompt.append("3. Keep the story between 300-500 words\n");
        prompt.append("4. Use simple, engaging language\n");
        prompt.append("5. Provide both English and Vietnamese versions\n\n");
        
        prompt.append("Please respond in this EXACT JSON format:\n");
        prompt.append("{\n");
        prompt.append("  \"titleEn\": \"English title here\",\n");
        prompt.append("  \"titleVi\": \"Tiêu đề tiếng Việt\",\n");
        prompt.append("  \"contentEn\": \"Full English story content...\",\n");
        prompt.append("  \"contentVi\": \"Nội dung truyện tiếng Việt...\",\n");
        prompt.append("  \"genres\": [\"genre1\", \"genre2\"]\n");
        prompt.append("}");

        return prompt.toString();
    }

    private Map<String, String> parseStoryResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("candidates").get(0)
                               .path("content").path("parts").get(0)
                               .path("text").asText();
            
            // Try to parse JSON from content
            content = content.trim();
            if (content.startsWith("```json")) {
                content = content.substring(7);
            }
            if (content.startsWith("```")) {
                content = content.substring(3);
            }
            if (content.endsWith("```")) {
                content = content.substring(0, content.length() - 3);
            }
            content = content.trim();

            JsonNode storyData = objectMapper.readTree(content);
            
            Map<String, String> result = new HashMap<>();
            result.put("titleEn", storyData.path("titleEn").asText());
            result.put("titleVi", storyData.path("titleVi").asText());
            result.put("contentEn", storyData.path("contentEn").asText());
            result.put("contentVi", storyData.path("contentVi").asText());
            
            // Parse genres array
            JsonNode genresNode = storyData.path("genres");
            if (genresNode.isArray()) {
                List<String> genres = new java.util.ArrayList<>();
                genresNode.forEach(node -> genres.add(node.asText()));
                result.put("genres", String.join(",", genres));
            }
            
            return result;
        } catch (Exception e) {
            log.error("Error parsing Gemini response: ", e);
            throw new RuntimeException("Failed to parse story content from AI response");
        }
    }

    private Map<String, String> getDefaultStory(CreateStoryRequest request) {
        Map<String, String> result = new HashMap<>();
        result.put("titleEn", "The Magic Adventure");
        result.put("titleVi", "Cuộc Phiêu Lưu Kỳ Diệu");
        result.put("contentEn", "Once upon a time, there was a magical adventure...");
        result.put("contentVi", "Ngày xửa ngày xưa, có một cuộc phiêu lưu kỳ diệu...");
        result.put("genres", "Adventure,Fantasy");
        return result;
    }
}
