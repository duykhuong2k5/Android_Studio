package com.example.pandora.data.entity;

import com.example.pandora.R;
import com.example.pandora.data.entity.enums.VocabularyTopic;

public class VocabularyWordResponse {

    public Long id;                 // backend: Long
    public String word;             // backend: String
    public String phonetic;         // backend: String
    public VocabularyTopic topic;   // backend: enum string
    public int level;               // backend: int
    public String imageUrl;         // backend: String
    public String audioUrl;         // backend: String

    // ===== Helpers dùng cho MiniGame =====

    public String safeWord() {
        return word == null ? "" : word.trim();
    }

    public String safeWordUpper() {
        return safeWord().toUpperCase();
    }

    public String safeImageUrl() {
        return imageUrl == null ? "" : imageUrl.trim();
    }

    public String safeAudioUrl() {
        return audioUrl == null ? "" : audioUrl.trim();
    }

    /**
     * Mini Game #1: nếu bạn đang dùng icon LOCAL (drawable),
     * map theo word hoặc imageUrl.
     */
    public int getLocalFoodDrawableRes() {
        String w = safeWordUpper();
        switch (w) {
            case "APPLE":
                return R.drawable.ic_food_apple;
            case "BANANA":
                return R.drawable.ic_food_banana;
            case "CHEESE":
                return R.drawable.ic_food_cheese;
            default:
                return R.drawable.ic_food_default; // bạn tạo icon default
        }
    }
}
