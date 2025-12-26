# Test Gemini Story API with Cloudinary

Write-Host "=== Testing Gemini + Cloudinary Story API ===" -ForegroundColor Green
Write-Host ""

# 1. Test connection
Write-Host "1. Testing API connection..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/stories/topics" -Method Get
    Write-Host "✅ Connected! Topics:" -ForegroundColor Green
    $response.data | ForEach-Object { Write-Host "   - $_" }
} catch {
    Write-Host "❌ Connection failed. Is server running?" -ForegroundColor Red
    Write-Host "Run: mvn spring-boot:run" -ForegroundColor Yellow
    exit
}

Write-Host ""

# 2. Create a test story
Write-Host "2. Creating test story with Gemini + Cloudinary..." -ForegroundColor Yellow
Write-Host "   - Gemini will generate story content" -ForegroundColor Cyan
Write-Host "   - Placeholder images will be uploaded to Cloudinary" -ForegroundColor Cyan

$storyRequest = @{
    topic = "Animals"
    style = "Funny"
    userId = 1
    characters = @(
        @{
            name = "Leo"
            age = "5"
            gender = "Boy"
            role = "Hero"
        },
        @{
            name = "Mia"
            age = "6"
            gender = "Girl"
            role = "Friend"
        }
    )
} | ConvertTo-Json

try {
    Write-Host "Sending request (this may take 15-25 seconds)..." -ForegroundColor Cyan
    Write-Host "  - Generating story with Gemini AI..." -ForegroundColor Gray
    Write-Host "  - Creating character images..." -ForegroundColor Gray
    Write-Host "  - Uploading to Cloudinary..." -ForegroundColor Gray
    
    $story = Invoke-RestMethod `
        -Uri "http://localhost:8080/api/stories/create" `
        -Method Post `
        -ContentType "application/json" `
        -Body $storyRequest `
        -TimeoutSec 60
    
    Write-Host ""
    Write-Host "✅ Story created successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
    Write-Host "Story ID: $($story.data.id)" -ForegroundColor Cyan
    Write-Host "Title (EN): $($story.data.titleEn)" -ForegroundColor Yellow
    Write-Host "Title (VI): $($story.data.titleVi)" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Content Preview:" -ForegroundColor Magenta
    $preview = $story.data.contentEn.Substring(0, [Math]::Min(250, $story.data.contentEn.Length))
    Write-Host $preview + "..." -ForegroundColor White
    Write-Host ""
    Write-Host "Thumbnail (Cloudinary): " -ForegroundColor Cyan -NoNewline
    Write-Host $story.data.thumbnailUrl -ForegroundColor Green
    Write-Host "Topic: $($story.data.topic) | Style: $($story.data.style)" -ForegroundColor Gray
    Write-Host ""
    
    if ($story.data.characters -and $story.data.characters.Count -gt 0) {
        Write-Host "Characters:" -ForegroundColor Yellow
        $story.data.characters | ForEach-Object {
            Write-Host "  🎭 $($_.name) ($($_.age), $($_.gender))" -ForegroundColor Cyan
            Write-Host "     Role: $($_.role)" -ForegroundColor Gray
            Write-Host "     Image: $($_.imageUrl)" -ForegroundColor Green
            Write-Host ""
        }
    }
    
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
    Write-Host ""
    Write-Host "🎉 Success! All images stored on Cloudinary!" -ForegroundColor Green
    Write-Host "   Cloud: dqytpmnnk" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "💡 Check your Cloudinary dashboard:" -ForegroundColor Yellow
    Write-Host "   https://console.cloudinary.com/console/media_library" -ForegroundColor Blue
    
} catch {
    Write-Host ""
    Write-Host "❌ Error creating story:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
    Write-Host "Troubleshooting:" -ForegroundColor Yellow
    Write-Host "1. Check if server is running (mvn spring-boot:run)" -ForegroundColor Gray
    Write-Host "2. Check Gemini API key in application.properties" -ForegroundColor Gray
    Write-Host "3. Check Cloudinary credentials" -ForegroundColor Gray
    Write-Host "4. Check logs in server terminal" -ForegroundColor Gray
}

Write-Host ""
Write-Host "=== Test Complete ===" -ForegroundColor Green
