package com.example.pandora.data.entity;

import java.util.List;

public class CreateStoryRequest {
    public String topic;
    public String style;
    public int userId;
    public List<CharacterRequest> characters;
}