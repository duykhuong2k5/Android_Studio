package com.example.pandora.service.impl;

import com.example.pandora.dto.request.CharacterRequest;
import com.example.pandora.dto.request.CreateStoryRequest;
import com.example.pandora.dto.response.CharacterResponse;
import com.example.pandora.dto.response.StoryResponse;
import com.example.pandora.model.Story;
import com.example.pandora.model.StoryCharacter;
import com.example.pandora.model.User;
import com.example.pandora.repository.StoryCharacterRepository;
import com.example.pandora.repository.StoryRepository;
import com.example.pandora.repository.UserRepository;
import com.example.pandora.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoryServiceImpl implements StoryService {

    private final StoryRepository storyRepository;
    private final StoryCharacterRepository characterRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;
    private final ImageGenerationService imageGenerationService;
    private final TextToSpeechService textToSpeechService;

    @Override
    @Transactional
    public StoryResponse createStory(CreateStoryRequest request) {
        log.info("Creating new story with topic: {} and style: {}", request.getTopic(), request.getStyle());

        // 1. Generate story content using AI
        log.info("Calling Gemini service to generate story content...");
        Map<String, String> storyContent = geminiService.generateStoryContent(request);
        log.info("Story content generated - Title EN: {}, Content length: {}", 
            storyContent.get("titleEn"), 
            storyContent.get("contentEn") != null ? storyContent.get("contentEn").length() : 0);

        // 2. Create Story entity
        Story story = new Story();
        story.setTitleEn(storyContent.get("titleEn"));
        story.setTitleVi(storyContent.get("titleVi"));
        story.setContentEn(storyContent.get("contentEn"));
        story.setContentVi(storyContent.get("contentVi"));
        story.setTopic(request.getTopic());
        story.setStyle(request.getStyle());
        
        // Parse genres
        String genresStr = storyContent.get("genres");
        if (genresStr != null && !genresStr.isEmpty()) {
            story.setGenres(Arrays.asList(genresStr.split(",")));
        }

        // Set user if provided
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            story.setUser(user);
        }

        // Save story first to get ID
        story = storyRepository.save(story);
        log.info("Story saved with ID: {}", story.getId());

        // 3. Process characters (generate images and descriptions)
        List<StoryCharacter> characters = new ArrayList<>();
        if (request.getCharacters() != null && !request.getCharacters().isEmpty()) {
            characters = processCharacters(request.getCharacters(), story);
        }
        story.setCharacters(characters);

        // 4. Generate story thumbnail (synchronous for immediate response)
        try {
            List<String> characterNames = characters.stream()
                .map(StoryCharacter::getName)
                .collect(Collectors.toList());
            
            String thumbnailUrl = imageGenerationService.generateStoryThumbnail(
                story.getTitleEn(), story.getTopic(), story.getStyle(), characterNames
            );
            
            if (thumbnailUrl != null) {
                story.setThumbnailUrl(thumbnailUrl);
                // No need to save again - entity is already managed by JPA
                log.info("Thumbnail generated for story {}: {}", story.getId(), thumbnailUrl);
            }
        } catch (Exception e) {
            log.warn("Failed to generate thumbnail, will continue without it: {}", e.getMessage());
        }

        // 5. Generate audio files (async - will update story after completion)
        generateAudioFiles(story);

        StoryResponse response = mapToResponse(story);
        log.info("✓ Story created successfully - ID: {}, Thumbnail: {}", 
            response.getId(), 
            response.getThumbnailUrl() != null ? "Present" : "NULL");

        return response;
    }

    @Override
    public StoryResponse getStoryById(Long id) {
        Story story = storyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Story not found"));
        return mapToResponse(story);
    }

    @Override
    public List<StoryResponse> getAllStoriesByUser(Long userId) {
        List<Story> stories = storyRepository.findByUserId(userId);
        return stories.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<StoryResponse> getStoriesByTopic(String topic) {
        return storyRepository.findByTopic(topic).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteStory(Long id) {
        Story story = storyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Story not found"));
        storyRepository.delete(story);
        log.info("Story deleted: {}", id);
    }

    @Override
    public String generateAudioForStory(Long storyId, String language) {
        Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new RuntimeException("Story not found"));

        String text = "en".equalsIgnoreCase(language) ? story.getContentEn() : story.getContentVi();
        String audioUrl = textToSpeechService.generateSpeech(text, language, storyId.toString());

        if (audioUrl != null) {
            if ("en".equalsIgnoreCase(language)) {
                story.setAudioUrlEn(audioUrl);
            } else {
                story.setAudioUrlVi(audioUrl);
            }
            storyRepository.save(story);
        }

        return audioUrl;
    }

    private List<StoryCharacter> processCharacters(List<CharacterRequest> characterRequests, Story story) {
        List<StoryCharacter> characters = new ArrayList<>();
        
        for (CharacterRequest request : characterRequests) {
            // Generate character description
            String description = geminiService.generateCharacterDescription(
                request, 
                story.getTopic() + " " + story.getStyle()
            );

            // Generate character image
            String imageUrl = imageGenerationService.generateCharacterImage(
                description,
                request.getName()
            );

            StoryCharacter character = new StoryCharacter();
            character.setName(request.getName());
            character.setAge(request.getAge());
            character.setGender(request.getGender());
            character.setRole(request.getRole());
            character.setDescription(description);
            character.setImageUrl(imageUrl);
            character.setStory(story);

            characters.add(characterRepository.save(character));
            log.info("Character created: {} with image: {}", request.getName(), imageUrl);
        }

        return characters;
    }

    private void generateAudioFiles(Story story) {
        // Capture content at this point to avoid stale data
        final String contentEn = story.getContentEn();
        final String contentVi = story.getContentVi();
        final Long storyId = story.getId();
        
        log.info("Scheduling audio generation for story {} - EN length: {}, VI length: {}", 
            storyId, 
            contentEn != null ? contentEn.length() : 0,
            contentVi != null ? contentVi.length() : 0);
        
        CompletableFuture.runAsync(() -> {
            try {
                // Generate English audio
                String audioEnUrl = textToSpeechService.generateSpeech(
                    contentEn, 
                    "en", 
                    storyId.toString()
                );
                
                // Generate Vietnamese audio
                String audioViUrl = textToSpeechService.generateSpeech(
                    contentVi, 
                    "vi", 
                    storyId.toString()
                );

                // Update story with audio URLs
                Story s = storyRepository.findById(storyId).orElse(null);
                if (s != null) {
                    s.setAudioUrlEn(audioEnUrl);
                    s.setAudioUrlVi(audioViUrl);
                    storyRepository.save(s);
                    log.info("Audio files generated for story {}", storyId);
                }
            } catch (Exception e) {
                log.error("Error generating audio: ", e);
            }
        });
    }

    private StoryResponse mapToResponse(Story story) {
        StoryResponse response = new StoryResponse();
        response.setId(story.getId());
        response.setTitleEn(story.getTitleEn());
        response.setTitleVi(story.getTitleVi());
        response.setContentEn(story.getContentEn());
        response.setContentVi(story.getContentVi());
        response.setThumbnailUrl(story.getThumbnailUrl());
        response.setGenres(story.getGenres());
        response.setTopic(story.getTopic());
        response.setStyle(story.getStyle());
        response.setAudioUrlEn(story.getAudioUrlEn());
        response.setAudioUrlVi(story.getAudioUrlVi());

        List<CharacterResponse> characters = story.getCharacters().stream()
            .map(this::mapCharacterToResponse)
            .collect(Collectors.toList());
        response.setCharacters(characters);

        return response;
    }

    private CharacterResponse mapCharacterToResponse(StoryCharacter character) {
        CharacterResponse response = new CharacterResponse();
        response.setId(character.getId());
        response.setName(character.getName());
        response.setAge(character.getAge());
        response.setGender(character.getGender());
        response.setRole(character.getRole());
        response.setImageUrl(character.getImageUrl());
        response.setDescription(character.getDescription());
        return response;
    }
}
