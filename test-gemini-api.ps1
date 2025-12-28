# Test Gemini API to find valid model names
$apiKey = "AIzaSyCsJ4z9sj1w-OyYGYvfVeSoshLqhHiVdQo"

Write-Host "`n=== Testing Gemini API Models ===" -ForegroundColor Cyan

# Test 1: List available models
Write-Host "`n1. Listing available models..." -ForegroundColor Yellow
try {
    $listUrl = "https://generativelanguage.googleapis.com/v1beta/models?key=$apiKey"
    $response = Invoke-RestMethod -Uri $listUrl -Method Get -ErrorAction Stop
    Write-Host "Available models:" -ForegroundColor Green
    $response.models | ForEach-Object {
        Write-Host "  - $($_.name) (supports: $($_.supportedGenerationMethods -join ', '))" -ForegroundColor White
    }
} catch {
    Write-Host "Error listing models: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Try gemini-pro
Write-Host "`n2. Testing gemini-pro model..." -ForegroundColor Yellow
$models = @("gemini-pro", "gemini-1.5-pro-latest", "gemini-1.0-pro")

foreach ($model in $models) {
    Write-Host "`nTrying model: $model" -ForegroundColor Cyan
    try {
        $testUrl = "https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent?key=$apiKey"
        
        $body = @{
            contents = @(
                @{
                    parts = @(
                        @{
                            text = "Say hello in 5 words"
                        }
                    )
                }
            )
        } | ConvertTo-Json -Depth 10
        
        $response = Invoke-RestMethod -Uri $testUrl -Method Post -Body $body -ContentType "application/json" -ErrorAction Stop
        $text = $response.candidates[0].content.parts[0].text
        
        Write-Host "  ✓ SUCCESS! Response: $text" -ForegroundColor Green
        Write-Host "  Use this model in application.properties:" -ForegroundColor Green
        Write-Host "  gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent" -ForegroundColor White
        break
    } catch {
        Write-Host "  ✗ Failed: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n=== Test Complete ===" -ForegroundColor Cyan
