package com.example.pandora.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Upload image from URL to Cloudinary
     */
    public String uploadImageFromUrl(String imageUrl, String folder) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                imageUrl,
                ObjectUtils.asMap(
                    "folder", "stories/" + folder,
                    "resource_type", "image"
                )
            );
            
            String secureUrl = (String) uploadResult.get("secure_url");
            log.info("Image uploaded to Cloudinary: {}", secureUrl);
            return secureUrl;
            
        } catch (IOException e) {
            log.error("Error uploading image to Cloudinary: ", e);
            return imageUrl; // Return original URL if upload fails
        }
    }

    /**
     * Upload audio file to Cloudinary
     */
    public String uploadAudioFromUrl(String audioUrl, String folder) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                audioUrl,
                ObjectUtils.asMap(
                    "folder", "stories/" + folder,
                    "resource_type", "video" // Cloudinary uses 'video' for audio files
                )
            );
            
            String secureUrl = (String) uploadResult.get("secure_url");
            log.info("Audio uploaded to Cloudinary: {}", secureUrl);
            return secureUrl;
            
        } catch (IOException e) {
            log.error("Error uploading audio to Cloudinary: ", e);
            return audioUrl;
        }
    }

    /**
     * Upload audio from byte array
     */
    public String uploadAudio(byte[] audioData, String publicId) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                audioData,
                ObjectUtils.asMap(
                    "folder", "stories/audio",
                    "resource_type", "video",
                    "public_id", publicId,
                    "format", "mp3"
                )
            );
            
            return (String) uploadResult.get("secure_url");
            
        } catch (IOException e) {
            log.error("Error uploading audio: ", e);
            throw new RuntimeException("Failed to upload audio file");
        }
    }

    /**
     * Delete file from Cloudinary
     */
    public void deleteFile(String publicId, String resourceType) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", resourceType));
            log.info("Deleted file from Cloudinary: {}", publicId);
        } catch (IOException e) {
            log.error("Error deleting file from Cloudinary: ", e);
        }
    }
}
