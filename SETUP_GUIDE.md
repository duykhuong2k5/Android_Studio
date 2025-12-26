# 🚀 Hướng Dẫn Cài Đặt Chi Tiết - Story Generation Backend

## 📋 Mục Lục
1. [Chuẩn bị](#1-chuẩn-bị)
2. [Cài đặt OpenAI API](#2-cài-đặt-openai-api)
3. [Cài đặt Google Cloud Text-to-Speech](#3-cài-đặt-google-cloud-text-to-speech)
4. [Cấu hình Cloudinary](#4-cấu-hình-cloudinary)
5. [Cấu hình Database](#5-cấu-hình-database)
6. [Chạy Backend](#6-chạy-backend)
7. [Tích hợp Android](#7-tích-hợp-android)
8. [Troubleshooting](#8-troubleshooting)

---

## 1. Chuẩn Bị

### Yêu cầu hệ thống:
- ✅ Java 17 trở lên
- ✅ Maven 3.6+
- ✅ PostgreSQL 13+
- ✅ IntelliJ IDEA hoặc VS Code
- ✅ Kết nối internet ổn định

### Kiểm tra version:
```powershell
java -version
mvn -version
psql --version
```

---

## 2. Cài Đặt OpenAI API

### Bước 1: Tạo tài khoản OpenAI
1. Truy cập: https://platform.openai.com/signup
2. Đăng ký tài khoản (cần email và số điện thoại)
3. Xác thực tài khoản

### Bước 2: Nạp credits
1. Vào: https://platform.openai.com/account/billing
2. Click "Add payment method"
3. Nạp tối thiểu **$5-10** (đủ cho 2-3 stories test)

### Bước 3: Tạo API Key
1. Vào: https://platform.openai.com/api-keys
2. Click "Create new secret key"
3. Đặt tên: `Story-Generator-API`
4. Copy key (chỉ hiện 1 lần!)

**Ví dụ API key:** `sk-proj-ABC123xyz...`

### Bước 4: Cập nhật application.properties
```properties
openai.api.key=sk-proj-YOUR_ACTUAL_API_KEY_HERE
openai.model=gpt-4
```

### Kiểm tra chi phí:
- **GPT-4**: ~$0.03/1K tokens input, ~$0.06/1K tokens output
- **DALL-E 3**: ~$0.04/image (standard), ~$0.08/image (HD)
- **Mỗi story**: ~$3-5 (bao gồm text + 3 images)

---

## 3. Cài Đặt Google Cloud Text-to-Speech

### Bước 1: Tạo Google Cloud Project
1. Truy cập: https://console.cloud.google.com/
2. Click "Select a project" → "NEW PROJECT"
3. Tên project: `story-tts-service`
4. Click "CREATE"

### Bước 2: Enable Text-to-Speech API
1. Vào: https://console.cloud.google.com/apis/library
2. Tìm: "Cloud Text-to-Speech API"
3. Click "ENABLE"

### Bước 3: Tạo Service Account
1. Vào: https://console.cloud.google.com/iam-admin/serviceaccounts
2. Click "CREATE SERVICE ACCOUNT"
3. Tên: `story-tts-service-account`
4. Role: `Cloud Text-to-Speech User`
5. Click "DONE"

### Bước 4: Tạo JSON Key
1. Click vào service account vừa tạo
2. Tab "KEYS" → "ADD KEY" → "Create new key"
3. Chọn "JSON" → "CREATE"
4. File JSON sẽ tự động download

### Bước 5: Cấu hình credentials
```powershell
# Windows PowerShell
$env:GOOGLE_APPLICATION_CREDENTIALS="C:\path\to\your-service-account-key.json"

# Linux/Mac
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/your-service-account-key.json"
```

**Lưu ý:** Set biến môi trường này **MỖI LẦN** mở terminal mới, hoặc thêm vào system environment variables.

### Bước 6: Cập nhật application.properties
```properties
google.cloud.project-id=story-tts-service
```

### Kiểm tra chi phí:
- **4 triệu ký tự/tháng**: MIỄN PHÍ
- Sau đó: $4/1 triệu ký tự
- 1 story (~500 từ × 2 ngôn ngữ): ~3000 ký tự → **MIỄN PHÍ**

---

## 4. Cấu Hình Cloudinary

### Bước 1: Tạo tài khoản (nếu chưa có)
1. Truy cập: https://cloudinary.com/users/register/free
2. Đăng ký (Free tier: 25 credits/tháng, đủ cho test)

### Bước 2: Lấy credentials
1. Login vào Dashboard
2. Copy các thông tin:
   - Cloud name
   - API Key
   - API Secret

### Bước 3: Cập nhật application.properties
```properties
cloudinary.cloud_name=your_cloud_name
cloudinary.api_key=your_api_key
cloudinary.api_secret=your_api_secret
```

**Lưu ý:** Bạn đã có sẵn credentials trong project:
```properties
cloudinary.cloud_name=dnbxsm1mx
cloudinary.api_key=329513356252861
cloudinary.api_secret=PC_sIT6yaw-3fWy9jFKsMMbKTHA
```

---

## 5. Cấu Hình Database

### Bước 1: Tạo database
```sql
-- Connect to PostgreSQL
psql -U postgres

-- Tạo database (nếu chưa có)
CREATE DATABASE pandora;

-- Kết nối vào database
\c pandora
```

### Bước 2: Kiểm tra connection
```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pandora
spring.datasource.username=postgres
spring.datasource.password=123456
```

### Bước 3: Auto-create tables
Spring Boot sẽ tự động tạo tables khi chạy lần đầu (do `ddl-auto=create-drop`).

**Lưu ý:** Đổi thành `update` khi production:
```properties
spring.jpa.hibernate.ddl-auto=update
```

---

## 6. Chạy Backend

### Bước 1: Build project
```powershell
cd "d:\HCMUTE\HKI nam 3\LTDD\demo"
mvn clean install
```

### Bước 2: Chạy application
```powershell
mvn spring-boot:run
```

Hoặc chạy từ IDE:
1. Open IntelliJ/VS Code
2. Mở file `DemoApplication.java`
3. Click Run

### Bước 3: Kiểm tra server
```powershell
# Test API
curl http://localhost:8080/api/stories/topics
```

**Expected response:**
```json
{
  "success": true,
  "message": "Topics retrieved successfully",
  "data": ["Animals", "School", "Space", ...]
}
```

---

## 7. Tích Hợp Android

### Bước 1: Thêm dependencies vào build.gradle
```kotlin
dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
}
```

### Bước 2: Cấu hình network security
File: `res/xml/network_security_config.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">192.168.1.x</domain>
    </domain-config>
</network-security-config>
```

AndroidManifest.xml:
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
```

### Bước 3: Cấu hình base URL
```kotlin
// RetrofitClient.kt
private const val BASE_URL = "http://10.0.2.2:8080/api/"  // Emulator
// hoặc
private const val BASE_URL = "http://192.168.1.x:8080/api/"  // Real Device
```

**Lưu ý:**
- `10.0.2.2`: Dùng cho Android Emulator
- `192.168.1.x`: IP máy thật (check bằng `ipconfig`)

### Bước 4: Test kết nối
```kotlin
// Trong Activity
RetrofitClient.storyService.getAvailableTopics().enqueue(
    object : Callback<ApiResponse<List<String>>> {
        override fun onResponse(call: Call<...>, response: Response<...>) {
            if (response.isSuccessful) {
                Log.d("TEST", "Connected! Topics: ${response.body()?.data}")
            }
        }
        override fun onFailure(call: Call<...>, t: Throwable) {
            Log.e("TEST", "Connection failed", t)
        }
    }
)
```

---

## 8. Troubleshooting

### ❌ Lỗi: "OpenAI API key not found"
**Giải pháp:**
1. Kiểm tra `application.properties`:
   ```properties
   openai.api.key=sk-proj-YOUR_KEY
   ```
2. Không có dấu cách trước/sau key
3. Restart server

### ❌ Lỗi: "Google Cloud credentials not found"
**Giải pháp:**
1. Kiểm tra biến môi trường:
   ```powershell
   echo $env:GOOGLE_APPLICATION_CREDENTIALS
   ```
2. File JSON phải tồn tại tại path đó
3. Restart terminal/IDE sau khi set

### ❌ Lỗi: "Connection refused" từ Android
**Giải pháp:**
1. Kiểm tra server đang chạy: `http://localhost:8080/api/stories/topics`
2. Kiểm tra IP máy: `ipconfig` (Windows) hoặc `ifconfig` (Mac/Linux)
3. Đổi BASE_URL trong Android:
   - Emulator: `http://10.0.2.2:8080/api/`
   - Real device: `http://192.168.x.x:8080/api/`
4. Tắt firewall tạm thời để test

### ❌ Lỗi: "Insufficient credits" từ OpenAI
**Giải pháp:**
1. Check balance: https://platform.openai.com/account/usage
2. Nạp thêm credits
3. Giảm số lượng characters trong test

### ❌ Lỗi: "Rate limit exceeded"
**Giải pháp:**
1. GPT-4 có limit: 500 requests/phút (tier 1)
2. DALL-E 3: 5 images/phút
3. Đợi 1 phút rồi thử lại
4. Hoặc upgrade tier

### ❌ Lỗi: Database connection failed
**Giải pháp:**
1. Kiểm tra PostgreSQL đang chạy:
   ```powershell
   Get-Service -Name postgresql*
   ```
2. Start nếu stopped:
   ```powershell
   Start-Service postgresql-x64-13
   ```
3. Kiểm tra credentials trong `application.properties`

---

## 📊 Checklist Hoàn Thành

### Backend Setup:
- [ ] Java 17+ đã cài
- [ ] Maven build thành công
- [ ] PostgreSQL đang chạy
- [ ] OpenAI API key đã set
- [ ] Google Cloud credentials đã set
- [ ] Cloudinary credentials đã có
- [ ] Server chạy thành công (port 8080)
- [ ] Test API topics thành công

### Android Setup:
- [ ] Dependencies đã thêm
- [ ] Network security config đã thêm
- [ ] BASE_URL đã đúng
- [ ] Retrofit client test thành công
- [ ] Internet permission trong manifest

### Test E2E:
- [ ] Tạo story từ Android thành công
- [ ] Nhận được story content (EN + VI)
- [ ] Character images hiển thị
- [ ] Thumbnail hiển thị
- [ ] Audio files có thể play

---

## 💰 Ước Tính Chi Phí (1 Tháng Development)

### Free Tier:
- Google TTS: 4M ký tự/tháng = **FREE**
- Cloudinary: 25 credits = **FREE**

### Phải trả:
- OpenAI GPT-4 + DALL-E: ~$50-100/tháng
  - 20-30 stories test
  - Mỗi story: ~$3-5

**Tổng: ~$50-100/tháng trong giai đoạn development**

**Lưu ý:** Production có thể cache stories để giảm chi phí!

---

## 🎯 Next Steps

1. ✅ Setup xong → Test với 1 story đơn giản
2. ✅ Thành công → Integrate vào Android app
3. ✅ Test E2E → Deploy to production server
4. ✅ Production → Implement caching & optimization

**Good luck! 🚀**
