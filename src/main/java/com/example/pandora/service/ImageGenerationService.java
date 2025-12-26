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
     * Generate story thumbnail with characters using collage
     * Upload to Cloudinary for permanent storage
     */
    public String generateStoryThumbnail(String storyTitle, String topic, String style, List<String> characterNames) {
        try {
            log.info("Generating story thumbnail with characters: {}", characterNames);
            
            // Create a colorful thumbnail with full-body character in scene
            String mainCharacter = characterNames.isEmpty() ? "story" : characterNames.get(0);
            String seed = mainCharacter.replaceAll("[^a-zA-Z0-9]", "") + topic.replaceAll("[^a-zA-Z0-9]", "");
            
            // Use bottts-neutral for full-body robot/character in scene
            // This style creates complete character with background elements
            String thumbnailUrl = String.format(
                "https://api.dicebear.com/7.x/bottts-neutral/svg?seed=%s&backgroundColor=ffdfbf,ffd5dc,c0aede,d1d4f9,b6e3f4&size=1024&scale=100",
                seed
            );
            
            log.info("Generated full-body thumbnail URL: {}", thumbnailUrl);
            
            // Upload to Cloudinary
            String cloudinaryUrl = cloudinaryService.uploadImageFromUrl(
                thumbnailUrl,
                "thumbnails/story_" + System.currentTimeMillis()
            );
            
            log.info("Story thumbnail uploaded: {}", cloudinaryUrl);
            return cloudinaryUrl;

        } catch (Exception e) {
            log.error("Error generating story thumbnail: ", e);
            // Fallback to bottts-neutral
            String mainCharacter = characterNames.isEmpty() ? "story" : characterNames.get(0);
            String seed = mainCharacter.replaceAll("[^a-zA-Z0-9]", "");
            return String.format(
                "https://api.dicebear.com/7.x/bottts-neutral/svg?seed=%s&backgroundColor=ffd5dc&size=1024",
                seed
            );
        }
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
