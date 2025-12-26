# Story Generation Backend - AI-Powered Children's Story App

Backend API cho ứng dụng tạo truyện cho trẻ em sử dụng AI với các tính năng:
- ✨ Tạo truyện tự động bằng GPT-4
- 🎨 Sinh hình ảnh nhân vật và thumbnail bằng DALL-E 3
- 🎧 Chuyển văn bản thành giọng nói (Text-to-Speech)
- 🌐 Hỗ trợ song ngữ (Tiếng Anh & Tiếng Việt)

## 📋 Các API Endpoints

### Story Management

#### 1. Tạo truyện mới (với AI)
```http
POST /api/stories/create
Content-Type: application/json

{
  "topic": "Animals",
  "style": "Funny",
  "userId": 1,
  "characters": [
    {
      "name": "Leo",
      "age": "5",
      "gender": "Boy",
      "role": "Hero"
    },
    {
      "name": "Mia",
      "age": "6",
      "gender": "Girl",
      "role": "Friend"
    }
  ]
}
```

**Response:**
```json
{
  "success": true,
  "message": "Story created successfully! ✨",
  "data": {
    "id": 1,
    "titleEn": "Leo and Mia's Jungle Adventure",
    "titleVi": "Cuộc Phiêu Lưu Trong Rừng Của Leo và Mia",
    "contentEn": "Once upon a time...",
    "contentVi": "Ngày xửa ngày xưa...",
    "thumbnailUrl": "https://res.cloudinary.com/...",
    "genres": ["Adventure", "Friendship"],
    "topic": "Animals",
    "style": "Funny",
    "audioUrlEn": "https://res.cloudinary.com/audio_en.mp3",
    "audioUrlVi": "https://res.cloudinary.com/audio_vi.mp3",
    "characters": [
      {
        "id": 1,
        "name": "Leo",
        "age": "5",
        "gender": "Boy",
        "role": "Hero",
        "imageUrl": "https://res.cloudinary.com/leo.png",
        "description": "A brave 5-year-old boy with curly brown hair..."
      }
    ]
  }
}
```

#### 2. Lấy truyện theo ID
```http
GET /api/stories/{id}
```

#### 3. Lấy tất cả truyện của user
```http
GET /api/stories/user/{userId}
```

#### 4. Lấy truyện theo chủ đề
```http
GET /api/stories/topic/{topic}
```

#### 5. Xóa truyện
```http
DELETE /api/stories/{id}
```

#### 6. Tạo audio cho truyện
```http
POST /api/stories/audio
Content-Type: application/json

{
  "storyId": 1,
  "language": "en"
}
```

#### 7. Lấy danh sách chủ đề có sẵn
```http
GET /api/stories/topics
```

#### 8. Lấy danh sách phong cách có sẵn
```http
GET /api/stories/styles
```

## 🛠️ Cài đặt & Cấu hình

### 1. Cài đặt Dependencies

Thêm các dependencies sau vào `pom.xml`:

```xml
<!-- OpenAI API (Spring AI hoặc custom RestTemplate) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Google Cloud Text-to-Speech -->
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>google-cloud-texttospeech</artifactId>
    <version>2.30.0</version>
</dependency>

<!-- Cloudinary SDK -->
<dependency>
    <groupId>com.cloudinary</groupId>
    <artifactId>cloudinary-http44</artifactId>
    <version>1.36.0</version>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

### 2. Cấu hình API Keys

Cập nhật file `application.properties`:

```properties
# OpenAI API
openai.api.key=sk-YOUR_OPENAI_API_KEY
openai.model=gpt-4

# Google Cloud Text-to-Speech
google.cloud.project-id=your-gcp-project-id

# Cloudinary (đã có sẵn)
cloudinary.cloud_name=dnbxsm1mx
cloudinary.api_key=329513356252861
cloudinary.api_secret=PC_sIT6yaw-3fWy9jFKsMMbKTHA
```

### 3. Google Cloud Credentials

Tạo Service Account và tải file JSON credentials:

```bash
# Linux/Mac
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/your-credentials.json"

# Windows PowerShell
$env:GOOGLE_APPLICATION_CREDENTIALS="C:\path\to\your-credentials.json"
```

### 4. Tạo Database Tables

Chạy application để tự động tạo tables (đã cấu hình `ddl-auto=create-drop`):

```sql
-- stories
CREATE TABLE stories (
    id BIGSERIAL PRIMARY KEY,
    title_en VARCHAR(255) NOT NULL,
    content_en TEXT NOT NULL,
    title_vi VARCHAR(255) NOT NULL,
    content_vi TEXT NOT NULL,
    thumbnail_url VARCHAR(500),
    topic VARCHAR(100),
    style VARCHAR(100),
    audio_url_en VARCHAR(500),
    audio_url_vi VARCHAR(500),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    user_id BIGINT
);

-- story_characters
CREATE TABLE story_characters (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    age VARCHAR(10),
    gender VARCHAR(20),
    role VARCHAR(100),
    image_url VARCHAR(500),
    description TEXT,
    story_id BIGINT REFERENCES stories(id)
);

-- story_genres
CREATE TABLE story_genres (
    story_id BIGINT REFERENCES stories(id),
    genre VARCHAR(100)
);
```

## 🚀 Chạy Application

```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run
```

Server sẽ chạy tại: `http://localhost:8080`

## 📝 Cách hoạt động

### Quy trình tạo truyện:

1. **Nhận request** từ frontend với topic, style, và characters
2. **Generate nội dung** truyện (EN + VI) bằng GPT-4
3. **Lưu story** vào database
4. **Generate hình ảnh nhân vật** cho từng character bằng DALL-E 3
5. **Generate thumbnail** cho truyện (async)
6. **Generate audio** (EN + VI) bằng Google Text-to-Speech (async)
7. **Upload** tất cả files lên Cloudinary
8. **Trả về response** với đầy đủ URLs

### Xử lý Async:
- Thumbnail generation và audio generation chạy async để không block response
- User nhận được story content ngay lập tức
- Hình ảnh và audio sẽ được cập nhật khi hoàn thành

## 💡 Ví dụ sử dụng trong Android

```kotlin
// API Call từ Android
val retrofit = Retrofit.Builder()
    .baseUrl("http://YOUR_SERVER_IP:8080/api/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val storyService = retrofit.create(StoryService::class.java)

val request = CreateStoryRequest(
    topic = "Animals",
    style = "Funny",
    userId = currentUserId,
    characters = listOf(
        CharacterRequest("Leo", "5", "Boy", "Hero"),
        CharacterRequest("Mia", "6", "Girl", "Friend")
    )
)

storyService.createStory(request).enqueue(object : Callback<ApiResponse<StoryResponse>> {
    override fun onResponse(call: Call<...>, response: Response<...>) {
        val story = response.body()?.data
        // Load story content, images, play audio
    }
    
    override fun onFailure(call: Call<...>, t: Throwable) {
        // Handle error
    }
})
```

## 🔧 Troubleshooting

### OpenAI API Errors
- Kiểm tra API key có đúng không
- Kiểm tra credit balance trên OpenAI
- Rate limit: GPT-4 có giới hạn requests/phút

### Image Generation Errors
- DALL-E 3: 1 USD ≈ 1 image (HD quality)
- Prompt phải child-appropriate
- Size: 1024x1024 recommended

### Text-to-Speech Errors
- Google Cloud TTS: 4 triệu ký tự/tháng miễn phí
- Cần enable Cloud Text-to-Speech API
- Voice phụ thuộc vào ngôn ngữ

### Cloudinary Upload Errors
- Kiểm tra network connection
- Kiểm tra Cloudinary quotas
- File size limits

## 💰 Chi phí ước tính

**Mỗi lần tạo truyện (~500 từ, 2 nhân vật):**
- GPT-4 (story generation): ~$0.03
- DALL-E 3 (3 images: 2 characters + 1 thumbnail): ~$3.00
- Google TTS (2 audio files): Miễn phí trong quota
- Cloudinary: Miễn phí trong quota

**Total: ~$3.03 per story**

## 📚 Tài liệu tham khảo

- [OpenAI API Documentation](https://platform.openai.com/docs)
- [Google Cloud Text-to-Speech](https://cloud.google.com/text-to-speech/docs)
- [Cloudinary Documentation](https://cloudinary.com/documentation)
- [Spring Boot REST API](https://spring.io/guides/gs/rest-service/)

## 🎯 Tính năng tương lai

- [ ] Cache stories để giảm chi phí AI
- [ ] Batch processing cho nhiều stories
- [ ] Story templates để generate nhanh hơn
- [ ] Story rating & feedback
- [ ] Multilingual support (thêm các ngôn ngữ khác)
- [ ] Story customization (thay đổi endings)
- [ ] Interactive stories với choices
- [ ] Parent dashboard để quản lý stories

## 📞 Support

Nếu gặp vấn đề, hãy kiểm tra:
1. Logs trong console
2. API responses/status codes
3. Database connections
4. External API credentials

---

Made with ❤️ for children's education
