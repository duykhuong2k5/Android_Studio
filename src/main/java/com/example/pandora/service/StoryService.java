package com.example.pandora.service;

import com.example.pandora.dto.request.CreateStoryRequest;
import com.example.pandora.dto.response.StoryResponse;

import java.util.List;

public interface StoryService {
    StoryResponse createStory(CreateStoryRequest request);
    StoryResponse getStoryById(Long id);
    List<StoryResponse> getAllStoriesByUser(Long userId);
    List<StoryResponse> getStoriesByTopic(String topic);
    void deleteStory(Long id);
    String generateAudioForStory(Long storyId, String language);
}
