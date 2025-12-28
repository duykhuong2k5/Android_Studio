# 🏗️ System Architecture - Story Generation Backend

## 📐 Kiến Trúc Tổng Quan

```
┌─────────────────────────────────────────────────────────────────────┐
│                         ANDROID APP                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐             │
│  │   Create     │  │   Reading    │  │    Story     │             │
│  │   Story      │  │   Activity   │  │    List      │             │
│  │   Activity   │  │              │  │              │             │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘             │
│         │                 │                 │                      │
│         └─────────────────┼─────────────────┘                      │
│                           │                                        │
│                  ┌────────▼─────────┐                              │
│                  │  Retrofit Client │                              │
│                  │  (HTTP Client)   │                              │
│                  └────────┬─────────┘                              │
└───────────────────────────┼────────────────────────────────────────┘
                            │
                            │ REST API Calls
                            │
┌───────────────────────────▼────────────────────────────────────────┐
│                    SPRING BOOT BACKEND                              │
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                     Controller Layer                          │  │
│  │  ┌────────────────────────────────────────────────────────┐  │  │
│  │  │         StoryController                                │  │  │
│  │  │  - POST /create    - GET /{id}                         │  │  │
│  │  │  - GET /user/{id}  - DELETE /{id}                      │  │  │
│  │  │  - POST /audio     - GET /topics                       │  │  │
│  │  └────────────────────────────────────────────────────────┘  │  │
│  └──────────────────────┬───────────────────────────────────────┘  │
│                         │                                           │
│  ┌──────────────────────▼───────────────────────────────────────┐  │
│  │                     Service Layer                             │  │
│  │  ┌───────────────┐  ┌──────────────┐  ┌─────────────────┐   │  │
│  │  │ StoryService  │  │ OpenAIService│  │ ImageGeneration │   │  │
│  │  │   Impl        │  │              │  │    Service      │   │  │
│  │  └───────┬───────┘  └──────┬───────┘  └────────┬────────┘   │  │
│  │          │                 │                   │             │  │
│  │  ┌───────▼─────────────────▼───────────────────▼────────┐   │  │
│  │  │     TextToSpeechService    CloudinaryService         │   │  │
│  │  └──────────────────────────────────────────────────────┘   │  │
│  └──────────────────────┬───────────────────────────────────────┘  │
│                         │                                           │
│  ┌──────────────────────▼───────────────────────────────────────┐  │
│  │                   Repository Layer                            │  │
│  │  ┌────────────────────┐  ┌──────────────────────────────┐    │  │
│  │  │  StoryRepository   │  │  StoryCharacterRepository    │    │  │
│  │  └────────┬───────────┘  └──────────────┬───────────────┘    │  │
│  └───────────┼──────────────────────────────┼────────────────────┘  │
│              │                              │                       │
│  ┌───────────▼──────────────────────────────▼────────────────────┐ │
│  │                    Database Layer                              │ │
│  │  ┌──────────┐  ┌──────────────────┐  ┌────────────────────┐  │ │
│  │  │ stories  │  │ story_characters │  │   story_genres     │  │ │
│  │  │   table  │  │      table       │  │      table         │  │ │
│  │  └──────────┘  └──────────────────┘  └────────────────────┘  │ │
│  └───────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
                            │
                            │ External API Calls
                            │
┌───────────────────────────▼────────────────────────────────────────┐
│                      EXTERNAL SERVICES                              │
│                                                                     │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐   │
│  │   OpenAI API    │  │  Google Cloud   │  │   Cloudinary    │   │
│  │                 │  │   Text-to-      │  │                 │   │
│  │  - GPT-4        │  │    Speech       │  │ - Image Storage │   │
│  │  - DALL-E 3     │  │                 │  │ - Audio Storage │   │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Luồng Xử Lý: Tạo Story

```
┌─────────────┐
│   Android   │
│     App     │
└──────┬──────┘
       │
       │ 1. POST /api/stories/create
       │    {topic, style, characters}
       │
       ▼
┌──────────────────────────────────────────────────────────────┐
│  StoryController.createStory()                               │
└──────┬───────────────────────────────────────────────────────┘
       │
       │ 2. Call Service
       ▼
┌──────────────────────────────────────────────────────────────┐
│  StoryServiceImpl.createStory()                              │
│  ┌────────────────────────────────────────────────────────┐  │
│  │ Step 1: Generate Story Content                         │  │
│  │  ┌──────────────────────────────────────────────────┐  │  │
│  │  │ OpenAIService.generateStoryContent()             │  │  │
│  │  │  - Build prompt with topic, style, characters    │  │  │
│  │  │  - Call GPT-4 API                                │  │  │
│  │  │  - Parse JSON response                           │  │  │
│  │  │  → Returns: titleEn, titleVi, contentEn,         │  │  │
│  │  │             contentVi, genres                    │  │  │
│  │  └──────────────────────────────────────────────────┘  │  │
│  └────────────────────────────────────────────────────────┘  │
│                                                               │
│  ┌────────────────────────────────────────────────────────┐  │
│  │ Step 2: Save Story to Database                         │  │
│  │  - Create Story entity                                 │  │
│  │  - Set all fields from AI response                     │  │
│  │  - StoryRepository.save()                              │  │
│  │  → Returns: Story with ID                              │  │
│  └────────────────────────────────────────────────────────┘  │
│                                                               │
│  ┌────────────────────────────────────────────────────────┐  │
│  │ Step 3: Process Characters (for each character)        │  │
│  │  ┌──────────────────────────────────────────────────┐  │  │
│  │  │ A. Generate Character Description                │  │  │
│  │  │   OpenAIService.generateCharacterDescription()   │  │  │
│  │  │    → Returns: Visual description text            │  │  │
│  │  └──────────────────────────────────────────────────┘  │  │
│  │  ┌──────────────────────────────────────────────────┐  │  │
│  │  │ B. Generate Character Image                      │  │  │
│  │  │   ImageGenerationService.generateCharacterImage()│  │  │
│  │  │    - Call DALL-E 3 API with description         │  │  │
│  │  │    - Get temp image URL                          │  │  │
│  │  │    - Upload to Cloudinary                        │  │  │
│  │  │    → Returns: Permanent image URL               │  │  │
│  │  └──────────────────────────────────────────────────┘  │  │
│  │  ┌──────────────────────────────────────────────────┐  │  │
│  │  │ C. Save Character                                │  │  │
│  │  │   - Create StoryCharacter entity                 │  │  │
│  │  │   - Link to Story                                │  │  │
│  │  │   - StoryCharacterRepository.save()              │  │  │
│  │  └──────────────────────────────────────────────────┘  │  │
│  └────────────────────────────────────────────────────────┘  │
│                                                               │
│  ┌────────────────────────────────────────────────────────┐  │
│  │ Step 4: Generate Thumbnail (ASYNC)                     │  │
│  │  CompletableFuture.runAsync(() -> {                    │  │
│  │    ImageGenerationService.generateStoryThumbnail()     │  │
│  │      - Build prompt from story info                    │  │
│  │      - Call DALL-E 3 API                               │  │
│  │      - Upload to Cloudinary                            │  │
│  │      - Update Story.thumbnailUrl                       │  │
│  │  })                                                     │  │
│  └────────────────────────────────────────────────────────┘  │
│                                                               │
│  ┌────────────────────────────────────────────────────────┐  │
│  │ Step 5: Generate Audio Files (ASYNC)                   │  │
│  │  CompletableFuture.runAsync(() -> {                    │  │
│  │    ┌────────────────────────────────────────────────┐  │  │
│  │    │ A. English Audio                               │  │  │
│  │    │   TextToSpeechService.generateSpeech()         │  │  │
│  │    │    - Text: contentEn                           │  │  │
│  │    │    - Language: "en"                            │  │  │
│  │    │    - Call Google Cloud TTS                     │  │  │
│  │    │    - Get audio bytes                           │  │  │
│  │    │    - Upload to Cloudinary                      │  │  │
│  │    │    → audioUrlEn                                │  │  │
│  │    └────────────────────────────────────────────────┘  │  │
│  │    ┌────────────────────────────────────────────────┐  │  │
│  │    │ B. Vietnamese Audio                            │  │  │
│  │    │   TextToSpeechService.generateSpeech()         │  │  │
│  │    │    - Text: contentVi                           │  │  │
│  │    │    - Language: "vi"                            │  │  │
│  │    │    - Same process as English                   │  │  │
│  │    │    → audioUrlVi                                │  │  │
│  │    └────────────────────────────────────────────────┘  │  │
│  │    - Update Story with audio URLs                      │  │
│  │  })                                                     │  │
│  └────────────────────────────────────────────────────────┘  │
│                                                               │
│  ┌────────────────────────────────────────────────────────┐  │
│  │ Step 6: Return Response                                │  │
│  │  - Map Story to StoryResponse DTO                      │  │
│  │  - Include all characters                              │  │
│  │  - Return immediately (don't wait for async tasks)     │  │
│  └────────────────────────────────────────────────────────┘  │
└───────────────────────────┬───────────────────────────────────┘
                            │
                            │ 3. Return StoryResponse
                            ▼
┌──────────────────────────────────────────────────────────────┐
│  ApiResponse<StoryResponse>                                  │
│  {                                                            │
│    "success": true,                                           │
│    "message": "Story created successfully! ✨",               │
│    "data": {                                                  │
│      "id": 1,                                                 │
│      "titleEn": "Leo's Big Adventure",                        │
│      "contentEn": "...",                                      │
│      "thumbnailUrl": null,  ← Sẽ có sau vài giây             │
│      "characters": [...],                                     │
│      "audioUrlEn": null,    ← Sẽ có sau vài giây             │
│      "audioUrlVi": null     ← Sẽ có sau vài giây             │
│    }                                                          │
│  }                                                            │
└──────┬───────────────────────────────────────────────────────┘
       │
       │ 4. Display Story
       ▼
┌─────────────┐
│   Android   │
│  Shows the  │
│    Story    │
└─────────────┘

Note: Thumbnail và audio sẽ có sẵn khi user refresh hoặc GET story lại
```

---

## ⏱️ Timeline Ước Tính

| Step | Action | Time | Why |
|------|--------|------|-----|
| 1 | Generate story content (GPT-4) | ~5-10s | Large model, complex prompt |
| 2 | Save to database | <1s | Simple insert |
| 3A | Generate character description (GPT-4) | ~3-5s/char | Per character |
| 3B | Generate character image (DALL-E) | ~10-15s/char | Image generation |
| 3C | Upload to Cloudinary | ~1-2s/char | Network upload |
| 4 | Generate thumbnail (ASYNC) | ~10-15s | Runs in background |
| 5 | Generate audio (ASYNC) | ~5-10s/file | Runs in background |

**Total response time to user:** ~20-40s (depends on number of characters)
**Background tasks:** ~15-25s more for thumbnail + audio

---

## 🎯 Optimization Strategies

### 1. Caching
```java
// Cache story templates
@Cacheable("story-templates")
public Map<String, String> getStoryTemplate(String topic, String style)
```

### 2. Parallel Processing
```java
// Generate all character images in parallel
List<CompletableFuture<String>> futures = characters.stream()
    .map(char -> CompletableFuture.supplyAsync(() -> 
        generateCharacterImage(char)
    ))
    .collect(Collectors.toList());
```

### 3. Pre-generated Assets
- Cache common character types
- Pre-generate popular topics
- Store template stories

### 4. Batch Processing
- Queue multiple story requests
- Process in batches to save costs
- Implement rate limiting

---

## 📊 Database Schema

```sql
┌─────────────────────────────────────────────┐
│              stories                        │
├─────────────────┬──────────────┬────────────┤
│ id              │ BIGSERIAL    │ PK         │
│ title_en        │ VARCHAR(255) │ NOT NULL   │
│ content_en      │ TEXT         │ NOT NULL   │
│ title_vi        │ VARCHAR(255) │ NOT NULL   │
│ content_vi      │ TEXT         │ NOT NULL   │
│ thumbnail_url   │ VARCHAR(500) │            │
│ topic           │ VARCHAR(100) │            │
│ style           │ VARCHAR(100) │            │
│ audio_url_en    │ VARCHAR(500) │            │
│ audio_url_vi    │ VARCHAR(500) │            │
│ user_id         │ BIGINT       │ FK         │
│ created_at      │ TIMESTAMP    │            │
│ updated_at      │ TIMESTAMP    │            │
└─────────────────┴──────────────┴────────────┘
         │
         │ 1:N
         ▼
┌─────────────────────────────────────────────┐
│        story_characters                     │
├─────────────────┬──────────────┬────────────┤
│ id              │ BIGSERIAL    │ PK         │
│ name            │ VARCHAR(255) │ NOT NULL   │
│ age             │ VARCHAR(10)  │            │
│ gender          │ VARCHAR(20)  │            │
│ role            │ VARCHAR(100) │            │
│ image_url       │ VARCHAR(500) │            │
│ description     │ TEXT         │            │
│ story_id        │ BIGINT       │ FK         │
└─────────────────┴──────────────┴────────────┘

┌─────────────────────────────────────────────┐
│          story_genres                       │
├─────────────────┬──────────────┬────────────┤
│ story_id        │ BIGINT       │ FK         │
│ genre           │ VARCHAR(100) │            │
└─────────────────┴──────────────┴────────────┘
```

---

## 🔐 Security Considerations

1. **API Keys**: Never commit to Git
2. **Rate Limiting**: Implement per-user limits
3. **Input Validation**: Sanitize all user inputs
4. **CORS**: Configure properly for production
5. **Authentication**: Add JWT/OAuth for production

---

Made with 🎨 for educational storytelling
