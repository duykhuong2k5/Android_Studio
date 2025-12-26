package com.example.pandora.controller;

import com.example.pandora.api.ApiResponse;
import com.example.pandora.dto.request.CreateStoryRequest;
import com.example.pandora.dto.request.TextToSpeechRequest;
import com.example.pandora.dto.response.StoryResponse;
import com.example.pandora.service.StoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class StoryController {

    private final StoryService storyService;

    /**
     * Create a new story with AI generation
     * POST /api/stories/create
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<StoryResponse>> createStory(@RequestBody CreateStoryRequest request) {
        try {
            log.info("Received request to create story: topic={}, style={}", request.getTopic(), request.getStyle());
            
            StoryResponse story = storyService.createStory(request);
            
            return ResponseEntity.ok(
                ApiResponse.<StoryResponse>builder()
                    .success(true)
                    .message("Story created successfully! ✨")
                    .data(story)
                    .build()
            );
        } catch (Exception e) {
            log.error("Error creating story: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<StoryResponse>builder()
                    .success(false)
                    .message("Failed to create story: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get story by ID
     * GET /api/stories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StoryResponse>> getStoryById(@PathVariable Long id) {
        try {
            StoryResponse story = storyService.getStoryById(id);
            
            return ResponseEntity.ok(
                ApiResponse.<StoryResponse>builder()
                    .success(true)
                    .message("Story retrieved successfully")
                    .data(story)
                    .build()
            );
        } catch (Exception e) {
            log.error("Error getting story: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<StoryResponse>builder()
                    .success(false)
                    .message("Story not found")
                    .build()
                );
        }
    }

    /**
     * Get all stories for a user
     * GET /api/stories/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<StoryResponse>>> getUserStories(@PathVariable Long userId) {
        try {
            List<StoryResponse> stories = storyService.getAllStoriesByUser(userId);
            
            return ResponseEntity.ok(
                ApiResponse.<List<StoryResponse>>builder()
                    .success(true)
                    .message("Stories retrieved successfully")
                    .data(stories)
                    .build()
            );
        } catch (Exception e) {
            log.error("Error getting user stories: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<List<StoryResponse>>builder()
                    .success(false)
                    .message("Failed to retrieve stories")
                    .build()
                );
        }
    }

    /**
     * Get stories by topic
     * GET /api/stories/topic/{topic}
     */
    @GetMapping("/topic/{topic}")
    public ResponseEntity<ApiResponse<List<StoryResponse>>> getStoriesByTopic(@PathVariable String topic) {
        try {
            List<StoryResponse> stories = storyService.getStoriesByTopic(topic);
            
            return ResponseEntity.ok(
                ApiResponse.<List<StoryResponse>>builder()
                    .success(true)
                    .message("Stories retrieved successfully")
                    .data(stories)
                    .build()
            );
        } catch (Exception e) {
            log.error("Error getting stories by topic: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<List<StoryResponse>>builder()
                    .success(false)
                    .message("Failed to retrieve stories")
                    .build()
                );
        }
    }

    /**
     * Delete a story
     * DELETE /api/stories/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStory(@PathVariable Long id) {
        try {
            storyService.deleteStory(id);
            
            return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                    .success(true)
                    .message("Story deleted successfully")
                    .build()
            );
        } catch (Exception e) {
            log.error("Error deleting story: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to delete story")
                    .build()
                );
        }
    }

    /**
     * Generate or regenerate audio for a story
     * POST /api/stories/audio
     */
    @PostMapping("/audio")
    public ResponseEntity<ApiResponse<String>> generateAudio(@RequestBody TextToSpeechRequest request) {
        try {
            String audioUrl = storyService.generateAudioForStory(request.getStoryId(), request.getLanguage());
            
            if (audioUrl != null) {
                return ResponseEntity.ok(
                    ApiResponse.<String>builder()
                        .success(true)
                        .message("Audio generated successfully 🎧")
                        .data(audioUrl)
                        .build()
                );
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<String>builder()
                        .success(false)
                        .message("Failed to generate audio")
                        .build()
                    );
            }
        } catch (Exception e) {
            log.error("Error generating audio: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to generate audio: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get available topics
     * GET /api/stories/topics
     */
    @GetMapping("/topics")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableTopics() {
        List<String> topics = List.of(
            "Animals", "School", "Space", "Fantasy", "Adventure",
            "Nature", "Family", "Friendship", "Magic", "Ocean"
        );
        
        return ResponseEntity.ok(
            ApiResponse.<List<String>>builder()
                .success(true)
                .message("Topics retrieved successfully")
                .data(topics)
                .build()
        );
    }

    /**
     * Get available styles
     * GET /api/stories/styles
     */
    @GetMapping("/styles")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableStyles() {
        List<String> styles = List.of(
            "Funny", "Brave", "Adventure", "Educational",
            "Magical", "Mysterious", "Heroic", "Heartwarming"
        );
        
        return ResponseEntity.ok(
            ApiResponse.<List<String>>builder()
                .success(true)
                .message("Styles retrieved successfully")
                .data(styles)
                .build()
        );
    }
}
