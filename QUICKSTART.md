# ⚡ Quick Start - Gemini Story API

## 🚀 Chạy trong 3 bước

### Bước 1: Start PostgreSQL
```powershell
# Kiểm tra PostgreSQL đang chạy
Get-Service -Name postgresql*

# Nếu stopped, start nó
Start-Service postgresql-x64-13
```

### Bước 2: Start Backend
```powershell
cd "d:\HCMUTE\HKI nam 3\LTDD\demo"
mvn spring-boot:run
```

Đợi đến khi thấy:
```
Started DemoApplication in X.XXX seconds
```

### Bước 3: Test API
Mở terminal mới:
```powershell
cd "d:\HCMUTE\HKI nam 3\LTDD\demo"
.\test-gemini.ps1
```

---

## 🎉 Xong!

Backend đã chạy với:
- ✅ Gemini AI (MIỄN PHÍ)
- ✅ Placeholder images (MIỄN PHÍ)
- ✅ Story generation song ngữ

---

## 📱 Tích Hợp Android

### 1. Base URL
```kotlin
// Emulator
private const val BASE_URL = "http://10.0.2.2:8080/api/"

// Real device (thay YOUR_IP)
private const val BASE_URL = "http://192.168.1.x:8080/api/"
```

### 2. Tạo story
```kotlin
val request = CreateStoryRequest(
    topic = "Animals",
    style = "Funny",
    userId = 1,
    characters = listOf(
        CharacterRequest("Leo", "5", "Boy", "Hero")
    )
)

RetrofitClient.storyService.createStory(request)
    .enqueue(object : Callback<ApiResponse<StoryResponse>> {
        override fun onResponse(...) {
            val story = response.body()?.data
            // Load story và hiển thị
        }
        override fun onFailure(...) {
            // Handle error
        }
    })
```

---

## 💡 Tips

### Check IP máy (cho real device):
```powershell
ipconfig
# Tìm IPv4 Address của WiFi
```

### Xem logs:
- Backend logs: Terminal đang chạy `mvn spring-boot:run`
- Gemini response: Check console output

### Nếu lỗi:
1. Check PostgreSQL: `Get-Service postgresql*`
2. Check port 8080: `netstat -ano | findstr :8080`
3. Check API key trong application.properties

---

## 📊 Monitoring

### Gemini API Usage:
- Check tại: https://makersuite.google.com/app/apikey
- Xem quota & requests

### Database:
```sql
-- Kết nối PostgreSQL
psql -U postgres -d pandora

-- Xem stories
SELECT id, title_en, topic, style, created_at FROM stories;

-- Xem characters
SELECT * FROM story_characters;
```

---

## 🔥 Ready to Develop!

Everything is set up với Gemini AI - MIỄN PHÍ!

Chỉ cần:
1. `mvn spring-boot:run`
2. `.\test-gemini.ps1`
3. Integrate vào Android

Happy Coding! 🚀
