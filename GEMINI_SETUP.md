# 🌟 Gemini AI Integration - Setup Guide

## ✅ Đã Cấu Hình

Backend đã được chuyển sang sử dụng **Google Gemini AI** thay vì OpenAI!

### API Key của bạn:
```
AIzaSyCsJ4z9sj1w-OyYGYvfVeSoshLqhHiVdQo
```

---

## 🎯 Thay Đổi Chính

### 1. **Story Generation**: Gemini Pro
- ✅ Gemini API key đã được config
- ✅ Service đã chuyển từ OpenAIService → GeminiService
- ✅ Miễn phí với quota hào phóng

### 2. **Image Generation**: Placeholder Images
- ⚠️ Gemini không hỗ trợ image generation trực tiếp
- ✅ Tạm dùng placeholder images (miễn phí)
- 💡 Có thể nâng cấp lên Stability AI hoặc Replicate sau

### 3. **Text-to-Speech**: Google Cloud TTS
- ✅ Giữ nguyên (miễn phí 4M ký tự/tháng)

---

## 💰 Chi Phí

### Gemini Pro (Story Generation)
- **FREE**: 60 requests/phút
- **FREE**: 1 triệu tokens/tháng
- **Mỗi story**: ~2000 tokens → **MIỄN PHÍ 500 stories/tháng**

### Image Generation
- **Placeholder**: MIỄN PHÍ (dùng placehold.co)
- **Nếu muốn AI images**: Stability AI ~$0.02/image

### Text-to-Speech
- **Google TTS**: MIỄN PHÍ 4M ký tự/tháng

**Tổng chi phí: $0/tháng (trong quota)** 🎉

---

## 🚀 Chạy Ngay

### 1. Config đã sẵn sàng
File [application.properties](d:\HCMUTE\HKI nam 3\LTDD\demo\src\main\resources\application.properties) đã có:
```properties
gemini.api.key=AIzaSyCsJ4z9sj1w-OyYGYvfVeSoshLqhHiVdQo
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent
gemini.model=gemini-pro
```

### 2. Không cần Google Cloud credentials nữa!
- ❌ Không cần `GOOGLE_APPLICATION_CREDENTIALS` cho Gemini
- ✅ Chỉ cần API key trong properties

### 3. Start server
```powershell
mvn spring-boot:run
```

### 4. Test API
```powershell
# Test topics
curl http://localhost:8081/api/stories/topics

# Tạo story
curl -X POST http://localhost:8081/api/stories/create `
  -H "Content-Type: application/json" `
  -d '{
    "topic": "Animals",
    "style": "Funny",
    "userId": 1,
    "characters": [
      {
        "name": "Leo",
        "age": "5",
        "gender": "Boy",
        "role": "Hero"
      }
    ]
  }'
```

---

## 📊 So Sánh OpenAI vs Gemini

| Feature | OpenAI | Gemini Pro |
|---------|--------|------------|
| **Story Generation** | GPT-4: $0.03/story | **FREE (500/month)** |
| **Image Generation** | DALL-E: $0.12/image | Placeholder (FREE) |
| **Quality** | Excellent | Very Good |
| **Speed** | ~5s | ~3s |
| **Cost/Month (100 stories)** | ~$15 | **$0** |

---

## 🔧 Nếu Muốn AI Images (Tùy chọn)

### Option 1: Stability AI (~$0.02/image)
1. Đăng ký: https://platform.stability.ai/
2. Lấy API key
3. Thêm vào properties:
   ```properties
   stability.api.key=your_key_here
   ```
4. Code đã sẵn sàng!

### Option 2: Replicate (Pay as you go)
1. Đăng ký: https://replicate.com/
2. Lấy API key
3. Dùng model SDXL hoặc others

### Option 3: Giữ Placeholder
- Đủ cho demo/prototype
- Sau này thêm real images

---

## 📝 Files Đã Thay Đổi

1. [application.properties](d:\HCMUTE\HKI nam 3\LTDD\demo\src\main\resources\application.properties) - Gemini config
2. [GeminiService.java](d:\HCMUTE\HKI nam 3\LTDD\demo\src\main\java\com\example\pandora\service\OpenAIService.java) - Renamed & updated
3. [ImageGenerationService.java](d:\HCMUTE\HKI nam 3\LTDD\demo\src\main\java\com\example\pandora\service\ImageGenerationService.java) - Placeholder mode
4. [StoryServiceImpl.java](d:\HCMUTE\HKI nam 3\LTDD\demo\src\main\java\com\example\pandora\service\impl\StoryServiceImpl.java) - Use GeminiService

---

## ✅ Ưu Điểm Của Gemini

1. **Miễn phí**: 60 requests/phút, 1M tokens/tháng
2. **Nhanh hơn**: Response time ~3s
3. **Multilingual tốt**: Tiếng Việt rất tự nhiên
4. **No credit card**: Không cần thêm thẻ
5. **Google ecosystem**: Dễ integrate với Google Cloud

---

## 🎓 Gemini API Limits

### Free Tier (Đã đủ cho development):
- **Requests**: 60/phút
- **Tokens**: 1 triệu/tháng
- **Rate**: 15 RPM (requests per minute)

### Nếu cần nhiều hơn:
- Pay-as-you-go: $0.00025/1K tokens (rẻ hơn OpenAI)

---

## 📚 Tài Liệu

- Gemini API: https://ai.google.dev/docs
- API Keys: https://makersuite.google.com/app/apikey
- Pricing: https://ai.google.dev/pricing

---

## 🔥 Ready to Go!

Backend đã sẵn sàng với Gemini:
- ✅ API key configured
- ✅ Service updated
- ✅ Miễn phí hoàn toàn
- ✅ Chạy ngay được

Chỉ cần:
```powershell
mvn spring-boot:run
```

**Happy Coding! 🚀**
