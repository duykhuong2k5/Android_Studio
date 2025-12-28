# ✅ FINAL SETUP - Gemini + Cloudinary

## 🎉 Hoàn Thành 100%

Backend đã được setup hoàn chỉnh với:

### ✨ Services Integrated:

1. **Gemini AI** (Story Generation)
   - ✅ API Key: `AIzaSyCsJ4z9sj1w-OyYGYvfVeSoshLqhHiVdQo`
   - ✅ Model: gemini-pro
   - ✅ Miễn phí: 1M tokens/tháng

2. **Cloudinary** (Image Storage)
   - ✅ Cloud Name: `dqytpmnnk`
   - ✅ API Key: `399265322181627`
   - ✅ API Secret: Configured
   - ✅ Miễn phí: 25 credits/tháng

3. **Placeholder Images**
   - ✅ Colorful character placeholders
   - ✅ Attractive story thumbnails
   - ✅ Auto-upload to Cloudinary

---

## 💰 Chi Phí: $0/tháng

- **Gemini Pro**: MIỄN PHÍ (~500 stories/tháng)
- **Cloudinary**: MIỄN PHÍ (storage + CDN)
- **Placeholders**: MIỄN PHÍ
- **Total**: $0 🎉

---

## 🚀 Chạy Backend

### Bước 1: Start PostgreSQL
```powershell
Get-Service postgresql* | Start-Service
```

### Bước 2: Start Backend
```powershell
cd "d:\HCMUTE\HKI nam 3\LTDD\demo"
mvn spring-boot:run
```

Đợi thấy:
```
Started DemoApplication in X.XXX seconds
```

### Bước 3: Test (Terminal mới)
```powershell
.\test-gemini.ps1
```

---

## 📋 Kết Quả Mong Đợi

Khi chạy test, bạn sẽ thấy:

```
✅ Story created successfully!

Story ID: 1
Title (EN): Leo and Mia's Jungle Adventure
Title (VI): Cuộc Phiêu Lưu Trong Rừng Của Leo và Mia

Content Preview:
Once upon a time, in a lush green jungle...

Thumbnail (Cloudinary): 
https://res.cloudinary.com/dqytpmnnk/image/upload/...

Characters:
  🎭 Leo (5, Boy)
     Role: Hero
     Image: https://res.cloudinary.com/dqytpmnnk/...
     
  🎭 Mia (6, Girl)
     Role: Friend
     Image: https://res.cloudinary.com/dqytpmnnk/...

🎉 Success! All images stored on Cloudinary!
```

---

## 🔍 Kiểm Tra Cloudinary

Xem images đã upload:
1. Vào: https://console.cloudinary.com/console/media_library
2. Folder structure:
   ```
   stories/
   ├── characters/
   │   ├── Leo.png
   │   └── Mia.png
   └── thumbnails/
       └── story_123456789.png
   ```

---

## 📱 Tích Hợp Android

### Retrofit Interface
```kotlin
interface StoryService {
    @POST("stories/create")
    suspend fun createStory(
        @Body request: CreateStoryRequest
    ): ApiResponse<StoryResponse>
    
    @GET("stories/{id}")
    suspend fun getStory(@Path("id") id: Long): ApiResponse<StoryResponse>
}
```

### Base URL
```kotlin
// Emulator
private const val BASE_URL = "http://10.0.2.2:8080/api/"

// Real Device (check your IP with ipconfig)
private const val BASE_URL = "http://192.168.1.x:8080/api/"
```

### Create Story
```kotlin
val request = CreateStoryRequest(
    topic = "Animals",
    style = "Funny",
    userId = currentUserId,
    characters = listOf(
        CharacterRequest("Leo", "5", "Boy", "Hero"),
        CharacterRequest("Mia", "6", "Girl", "Friend")
    )
)

lifecycleScope.launch {
    try {
        val response = storyService.createStory(request)
        if (response.success) {
            val story = response.data
            // Load story content
            tvTitle.text = story.titleEn
            tvContent.text = story.contentEn
            
            // Load thumbnail from Cloudinary
            Glide.with(this@Activity)
                .load(story.thumbnailUrl)
                .into(imgThumbnail)
            
            // Load character images
            story.characters.forEach { character ->
                Glide.with(this@Activity)
                    .load(character.imageUrl)
                    .into(characterImageView)
            }
        }
    } catch (e: Exception) {
        Log.e("Story", "Error: ${e.message}")
    }
}
```

---

## 🎯 API Endpoints

```
POST   /api/stories/create          - Tạo truyện mới
GET    /api/stories/{id}            - Lấy truyện theo ID  
GET    /api/stories/user/{userId}   - Truyện của user
GET    /api/stories/topic/{topic}   - Truyện theo chủ đề
DELETE /api/stories/{id}            - Xóa truyện
GET    /api/stories/topics          - Danh sách topics
GET    /api/stories/styles          - Danh sách styles
```

---

## 🐛 Troubleshooting

### Server không start?
```powershell
# Check port 8080
netstat -ano | findstr :8080

# Kill process nếu bị chiếm
taskkill /PID <PID> /F
```

### Database error?
```powershell
# Check PostgreSQL
Get-Service postgresql*

# Start nếu stopped
Start-Service postgresql-x64-13
```

### Cloudinary upload failed?
- Check internet connection
- Check credentials trong application.properties
- Check Cloudinary dashboard quota

### Gemini API error?
- Check API key
- Check quota: https://makersuite.google.com/app/apikey
- Free tier: 60 requests/minute

---

## 📊 Monitoring

### Check Database
```sql
psql -U postgres -d pandora

SELECT id, title_en, topic, created_at FROM stories;
SELECT * FROM story_characters;
```

### Check Logs
- Backend: Terminal running `mvn spring-boot:run`
- Cloudinary uploads: Look for "uploaded to Cloudinary" messages
- Gemini responses: Look for "Calling Gemini API" messages

---

## ✅ Checklist

Backend Setup:
- [x] Gemini API configured
- [x] Cloudinary credentials set
- [x] PostgreSQL running
- [x] Dependencies installed
- [x] Test script ready

Android Integration:
- [ ] Add Retrofit dependencies
- [ ] Create service interface
- [ ] Configure BASE_URL
- [ ] Add network permissions
- [ ] Test API calls
- [ ] Load images with Glide/Coil

---

## 🎨 Features

### Hiện Tại:
- ✅ AI story generation (EN + VI)
- ✅ Character images (placeholders)
- ✅ Story thumbnails
- ✅ Cloudinary storage
- ✅ RESTful API

### Tương Lai (Nếu Cần):
- [ ] Real AI-generated images (Stability AI)
- [ ] Text-to-speech (Google TTS)
- [ ] Story ratings
- [ ] Story sharing
- [ ] Parent dashboard
- [ ] Offline mode

---

## 🎓 Resources

- **Gemini API**: https://ai.google.dev/docs
- **Cloudinary**: https://cloudinary.com/documentation
- **Spring Boot**: https://spring.io/guides
- **Android Retrofit**: https://square.github.io/retrofit/

---

## 🎉 Summary

Setup hoàn chỉnh:
- ✅ Gemini AI for story generation
- ✅ Cloudinary for image storage
- ✅ PostgreSQL database
- ✅ RESTful API
- ✅ Miễn phí 100%
- ✅ Ready for production

**Chỉ cần chạy: `mvn spring-boot:run` và `.\test-gemini.ps1`**

Happy Coding! 🚀✨
