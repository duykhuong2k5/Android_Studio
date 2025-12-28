package com.example.pandora.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageGenerationService {

    @Value("${stability.api.key:}")
    private String stabilityApiKey;

    @Value("${replicate.api.key:}")
    private String replicateApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CloudinaryService cloudinaryService;

    /**
     * Generate character image using DiceBear API (free avatars)
     * Upload to Cloudinary for permanent storage
     */
    public String generateCharacterImage(String characterDescription, String characterName) {
        try {
            log.info("Generating character image for: {}", characterName);
            
            // Use DiceBear API to generate cute character avatar
            // Styles: adventurer, avataaars, big-ears, bottts, croodles, fun-emoji, lorelei, etc.
            String seed = characterName.replaceAll("[^a-zA-Z0-9]", "");
            String avatarUrl = String.format(
                "https://api.dicebear.com/7.x/adventurer/svg?seed=%s&backgroundColor=b6e3f4,c0aede,d1d4f9&size=512",
                seed
            );
            
            log.info("Generated DiceBear avatar: {}", avatarUrl);
            
            // Upload to Cloudinary for permanent storage
            String cloudinaryUrl = cloudinaryService.uploadImageFromUrl(
                avatarUrl, 
                "characters/" + seed
            );
            
            log.info("Character image uploaded to Cloudinary: {}", cloudinaryUrl);
            return cloudinaryUrl;

        } catch (Exception e) {
            log.error("Error generating character image: ", e);
            // Return DiceBear URL as fallback
            String seed = characterName.replaceAll("[^a-zA-Z0-9]", "");
            return String.format(
                "https://api.dicebear.com/7.x/adventurer/svg?seed=%s&backgroundColor=b6e3f4&size=512",
                seed
            );
        }
    }

    /**
     * Generate story thumbnail scene based on story content
     * Uses AI to create contextual scene with characters
     * Upload to Cloudinary for permanent storage
     */
    public String generateStoryThumbnail(String storyTitle, String topic, String style, List<String> characterNames) {
        try {
            log.info("Generating contextual story thumbnail - Topic: {}, Style: {}, Characters: {}", 
                topic, style, characterNames);
            
            // Build detailed scene prompt for children's book illustration
            StringBuilder scenePrompt = new StringBuilder();
            scenePrompt.append("Children's book illustration, ");
            scenePrompt.append(topic.toLowerCase()).append(" theme, ");
            scenePrompt.append(style.toLowerCase()).append(" style, ");
            
            if (!characterNames.isEmpty()) {
                scenePrompt.append("featuring cute characters ");
                scenePrompt.append(String.join(" and ", characterNames));
                scenePrompt.append(" in ");
            }
            
            // Add scene context based on topic
            String sceneContext = getSceneContext(topic);
            scenePrompt.append(sceneContext);
            scenePrompt.append(", colorful, vibrant, kid-friendly, cartoon style, high quality");
            
            String prompt = scenePrompt.toString();
            log.info("Scene prompt: {}", prompt);
            
            // Use Pollinations.ai (free text-to-image API)
            String encodedPrompt = java.net.URLEncoder.encode(prompt, "UTF-8");
            String imageUrl = String.format(
                "https://image.pollinations.ai/prompt/%s?width=1024&height=1024&seed=%d&nologo=true",
                encodedPrompt,
                Math.abs(prompt.hashCode()) // Consistent seed for same prompt
            );
            
            log.info("Generated scene-based thumbnail URL");
            
            // Upload to Cloudinary for permanent storage
            String cloudinaryUrl = cloudinaryService.uploadImageFromUrl(
                imageUrl,
                "thumbnails/story_" + System.currentTimeMillis()
            );
            
            log.info("Story thumbnail uploaded to Cloudinary: {}", cloudinaryUrl);
            return cloudinaryUrl;

        } catch (Exception e) {
            log.error("Error generating story thumbnail, using fallback: ", e);
            // Fallback: Use simple scene illustration
            String fallbackPrompt = String.format(
                "children's book cover, %s, cute, colorful",
                topic
            );
            try {
                String encodedPrompt = java.net.URLEncoder.encode(fallbackPrompt, "UTF-8");
                return String.format(
                    "https://image.pollinations.ai/prompt/%s?width=1024&height=1024&nologo=true",
                    encodedPrompt
                );
            } catch (Exception ex) {
                log.error("Fallback also failed, using default", ex);
                return "https://via.placeholder.com/1024x1024/FFB6C1/000000?text=Story+Cover";
            }
        }
    }
    
    /**
     * Get scene context description based on story topic
     */
    private String getSceneContext(String topic) {
        return switch (topic.toLowerCase()) {
            case "animals" -> "a magical forest with animals playing together";
            case "school" -> "a colorful classroom with students learning and having fun";
            case "adventure" -> "an exciting adventure scene with exploration";
            case "family" -> "a warm family scene at home";
            case "friendship" -> "friends playing and laughing together";
            case "nature" -> "a beautiful natural landscape with wildlife";
            case "space" -> "a whimsical space scene with planets and stars";
            case "ocean" -> "an underwater scene with sea creatures";
            case "fantasy" -> "a magical fantasy world with enchanting elements";
            default -> "a charming scene that captures the story's essence";
        };
    }

    /**
     * Generate placeholder image using placehold.co (free service)
     * Creates colorful, child-friendly placeholders
     */
    private String generatePlaceholderImage(String text) {
        // Array of fun colors for children
        String[] colors = {"FFD700", "FF69B4", "87CEEB", "98FB98", "DDA0DD", "F0E68C"};
        int colorIndex = Math.abs(text.hashCode()) % colors.length;
        String bgColor = colors[colorIndex];
        
        String url = String.format(
            "https://placehold.co/512x512/%s/000000?text=%s&font=raleway",
            bgColor,
            text.replace(" ", "+").substring(0, Math.min(text.length(), 20))
        );
        return url;
    }

    /**
     * Generate image with Stability AI (if you have API key)
     */
    private String generateWithStabilityAI(String prompt, String fileName) {
        try {
            String apiUrl = "https://api.stability.ai/v1/generation/stable-diffusion-xl-1024-v1-0/text-to-image";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("text_prompts", List.of(
                Map.of("text", prompt + ", cartoon style, child-friendly, colorful", "weight", 1)
            ));
            requestBody.put("cfg_scale", 7);
            requestBody.put("height", 512);
            requestBody.put("width", 512);
            requestBody.put("samples", 1);
            requestBody.put("steps", 30);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + stabilityApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            String base64Image = root.path("artifacts").get(0).path("base64").asText();
            
            // Upload to Cloudinary
            return cloudinaryService.uploadImageFromUrl("data:image/png;base64," + base64Image, "characters/" + fileName);

        } catch (Exception e) {
            log.error("Error with Stability AI: ", e);
            return generatePlaceholderImage(fileName);
        }
    }

    /**
     * Alternative: Generate image using Replicate (if you want to use it)
     */
    public String generateImageWithReplicate(String prompt) {
        // Implementation for Replicate API
        // You can use models like: stability-ai/sdxl or other image models
        log.warn("Replicate generation not implemented yet");
        return null;
    }
}
