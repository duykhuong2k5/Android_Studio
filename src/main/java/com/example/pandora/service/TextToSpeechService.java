package com.example.pandora.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class TextToSpeechService {

    private final CloudinaryService cloudinaryService;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Convert text to speech using Google Translate TTS (free, no auth required)
     * Format: https://translate.google.com/translate_tts?ie=UTF-8&tl=LANG&client=tw-ob&q=TEXT
     */
    public String generateSpeech(String text, String language, String storyId) {
        if (text == null || text.trim().isEmpty()) {
            log.warn("Empty text provided for TTS");
            return null;
        }

        try {
            // Split text into chunks (Google TTS limit: ~200 chars per request)
            String[] chunks = splitTextIntoChunks(text, 200);
            byte[] combinedAudio = new byte[0];

            for (int i = 0; i < chunks.length; i++) {
                String chunk = chunks[i];
                log.info("Generating speech for chunk {}/{} (length: {})", i+1, chunks.length, chunk.length());
                
                // Language code: vi for Vietnamese, en for English
                String langCode = language.equalsIgnoreCase("vi") ? "vi" : "en";
                
                // Build URL with proper encoding using UriComponentsBuilder
                String ttsUrl = UriComponentsBuilder
                    .fromHttpUrl("https://translate.google.com/translate_tts")
                    .queryParam("ie", "UTF-8")
                    .queryParam("tl", langCode)
                    .queryParam("client", "tw-ob")
                    .queryParam("q", chunk)
                    .build()
                    .toUriString();

                // Download audio
                byte[] audioChunk = downloadAudio(ttsUrl);
                if (audioChunk != null && audioChunk.length > 0) {
                    combinedAudio = concatenateBytes(combinedAudio, audioChunk);
                    log.info("Successfully downloaded audio chunk {}/{} ({} bytes)", i+1, chunks.length, audioChunk.length);
                }

                // Add delay to avoid rate limiting
                if (i < chunks.length - 1) {
                    Thread.sleep(500);
                }
            }

            if (combinedAudio.length == 0) {
                log.error("No audio data generated");
                return null;
            }

            // Upload to Cloudinary
            String publicId = String.format("story_%s_%s", storyId, language);
            String audioUrl = cloudinaryService.uploadAudio(combinedAudio, publicId);
            
            log.info("Speech generated and uploaded successfully: {}", audioUrl);
            return audioUrl;

        } catch (Exception e) {
            log.error("Error generating speech: ", e);
            return null;
        }
    }

    /**
     * Split text into chunks suitable for TTS
     * Tries to break at sentence boundaries
     */
    private String[] splitTextIntoChunks(String text, int maxChunkSize) {
        if (text.length() <= maxChunkSize) {
            return new String[]{text};
        }

        // Split by sentences first
        String[] sentences = text.split("(?<=[.!?])\\s+");
        StringBuilder currentChunk = new StringBuilder();
        var chunks = new java.util.ArrayList<String>();

        for (String sentence : sentences) {
            if (sentence.length() > maxChunkSize) {
                // If single sentence is too long, split by words
                if (currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString());
                    currentChunk = new StringBuilder();
                }
                
                String[] words = sentence.split("\\s+");
                for (String word : words) {
                    if (currentChunk.length() + word.length() + 1 > maxChunkSize) {
                        chunks.add(currentChunk.toString());
                        currentChunk = new StringBuilder(word);
                    } else {
                        if (currentChunk.length() > 0) {
                            currentChunk.append(" ");
                        }
                        currentChunk.append(word);
                    }
                }
            } else if (currentChunk.length() + sentence.length() + 1 > maxChunkSize) {
                chunks.add(currentChunk.toString());
                currentChunk = new StringBuilder(sentence);
            } else {
                if (currentChunk.length() > 0) {
                    currentChunk.append(" ");
                }
                currentChunk.append(sentence);
            }
        }

        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString());
        }

        return chunks.toArray(new String[0]);
    }

    /**
     * Download audio from URL with proper headers
     */
    private byte[] downloadAudio(String url) {
        try {
            log.debug("Attempting to download audio from: {}", url);
            
            // Add headers to mimic browser request
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            headers.set("Referer", "https://translate.google.com/");
            headers.set("Accept", "audio/mpeg,*/*");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
            byte[] audioData = response.getBody();
            
            if (audioData == null || audioData.length == 0) {
                log.error("Downloaded audio is empty from URL: {}", url);
                return null;
            }
            
            log.debug("Successfully downloaded {} bytes", audioData.length);
            return audioData;
        } catch (Exception e) {
            log.error("Error downloading audio from URL: {} - Error: {}", url, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Concatenate byte arrays
     */
    private byte[] concatenateBytes(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
