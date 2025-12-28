# Test Story API với cURL

## 1. Test tạo truyện mới

```bash
curl -X POST http://localhost:8080/api/stories/create \
  -H "Content-Type: application/json" \
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
      },
      {
        "name": "Mia",
        "age": "6",
        "gender": "Girl",
        "role": "Friend"
      }
    ]
  }'
```

## 2. Test lấy truyện theo ID

```bash
curl -X GET http://localhost:8080/api/stories/1
```

## 3. Test lấy truyện của user

```bash
curl -X GET http://localhost:8080/api/stories/user/1
```

## 4. Test lấy truyện theo topic

```bash
curl -X GET http://localhost:8080/api/stories/topic/Animals
```

## 5. Test tạo audio

```bash
curl -X POST http://localhost:8080/api/stories/audio \
  -H "Content-Type: application/json" \
  -d '{
    "storyId": 1,
    "language": "en"
  }'
```

## 6. Test lấy topics

```bash
curl -X GET http://localhost:8080/api/stories/topics
```

## 7. Test lấy styles

```bash
curl -X GET http://localhost:8080/api/stories/styles
```

## 8. Test xóa truyện

```bash
curl -X DELETE http://localhost:8080/api/stories/1
```

---

## Test với Postman

Import collection sau vào Postman:

```json
{
  "info": {
    "name": "Story API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Create Story",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"topic\": \"Animals\",\n  \"style\": \"Funny\",\n  \"userId\": 1,\n  \"characters\": [\n    {\n      \"name\": \"Leo\",\n      \"age\": \"5\",\n      \"gender\": \"Boy\",\n      \"role\": \"Hero\"\n    }\n  ]\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/stories/create",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "stories", "create"]
        }
      }
    },
    {
      "name": "Get Story by ID",
      "request": {
        "method": "GET",
        "url": {
          "raw": "http://localhost:8080/api/stories/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "stories", "1"]
        }
      }
    }
  ]
}
```

---

## Test Checklist

### Trước khi test:
- [ ] PostgreSQL đang chạy
- [ ] OpenAI API key đã được cấu hình
- [ ] Google Cloud credentials đã được set
- [ ] Cloudinary đã được cấu hình
- [ ] Application đang chạy (port 8080)

### Các test cases:
- [ ] Create story với 1 character
- [ ] Create story với nhiều characters
- [ ] Create story không có characters
- [ ] Get story by ID (tồn tại)
- [ ] Get story by ID (không tồn tại)
- [ ] Get stories by user
- [ ] Get stories by topic
- [ ] Generate audio cho story
- [ ] Delete story
- [ ] Get available topics
- [ ] Get available styles

### Kiểm tra kết quả:
- [ ] Story content được generate (EN + VI)
- [ ] Character images được tạo
- [ ] Thumbnail được tạo (async)
- [ ] Audio files được tạo (async)
- [ ] Tất cả URLs hợp lệ
- [ ] Database được cập nhật đúng

---

## Troubleshooting

### Nếu API trả về lỗi 500:
1. Kiểm tra logs trong console
2. Kiểm tra OpenAI API key
3. Kiểm tra database connection
4. Kiểm tra Google Cloud credentials

### Nếu images không được tạo:
1. Kiểm tra OpenAI credit balance
2. Kiểm tra Cloudinary quotas
3. Kiểm tra network connection

### Nếu audio không được tạo:
1. Kiểm tra Google Cloud TTS API đã được enable
2. Kiểm tra credentials path
3. Kiểm tra quotas
