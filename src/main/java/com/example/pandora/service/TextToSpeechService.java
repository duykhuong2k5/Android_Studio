package com.example.pandora.service;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class TextToSpeechService {

    private final CloudinaryService cloudinaryService;

    @Value("${google.cloud.project-id:#{null}}")
    private String projectId;

    /**
     * Convert text to speech using Google Cloud Text-to-Speech
     */
    public String generateSpeech(String text, String language, String storyId) {
        try {
            // Initialize client
            try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
                
                // Build the synthesis input
                SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(text)
                    .build();

                // Build the voice request
                VoiceSelectionParams voice = buildVoiceParams(language);

                // Select the audio config
                AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .setSpeakingRate(0.9) // Slower for children
                    .setPitch(2.0) // Higher pitch for child-friendly voice
                    .build();

                // Perform the text-to-speech request
                log.info("Generating speech for language: {}", language);
                SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(
                    input, voice, audioConfig
                );

                // Get the audio contents from the response
                ByteString audioContents = response.getAudioContent();
                byte[] audioData = audioContents.toByteArray();

                // Upload to Cloudinary
                String publicId = String.format("story_%s_%s", storyId, language);
                String audioUrl = cloudinaryService.uploadAudio(audioData, publicId);
                
                log.info("Speech generated and uploaded successfully: {}", audioUrl);
                return audioUrl;
            }

        } catch (Exception e) {
            log.error("Error generating speech: ", e);
            return null;
        }
    }

    /**
     * Alternative: Use OpenAI Text-to-Speech
     */
    public String generateSpeechWithOpenAI(String text, String language, String storyId) {
        // Implementation using OpenAI TTS API
        // https://platform.openai.com/docs/guides/text-to-speech
        log.warn("OpenAI TTS not implemented yet");
        return null;
    }

    private VoiceSelectionParams buildVoiceParams(String language) {
        String languageCode;
        String voiceName;
        
        if ("vi".equalsIgnoreCase(language)) {
            languageCode = "vi-VN";
            voiceName = "vi-VN-Wavenet-A"; // Female voice
        } else {
            languageCode = "en-US";
            voiceName = "en-US-Wavenet-H"; // Female child-friendly voice
        }

        return VoiceSelectionParams.newBuilder()
            .setLanguageCode(languageCode)
            .setName(voiceName)
            .setSsmlGender(SsmlVoiceGender.FEMALE)
            .build();
    }

    /**
     * Save audio file locally (optional, for testing)
     */
    private String saveAudioLocally(byte[] audioData, String fileName) throws IOException {
        Path filePath = Paths.get("uploads/audio/" + fileName + ".mp3");
        Files.createDirectories(filePath.getParent());
        
        try (FileOutputStream out = new FileOutputStream(filePath.toFile())) {
            out.write(audioData);
        }
        
        return filePath.toString();
    }
}
