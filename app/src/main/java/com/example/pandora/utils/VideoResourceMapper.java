package com.example.pandora.utils;

import com.example.pandora.R;

public class VideoResourceMapper {

    public static int getVideoRes(String title) {
        switch (title) {
            case "ABC Song":
                return R.raw.abc;
            case "Alphabet Song":
                return R.raw.alphabet;
            case "ABC Karaoke":
                return R.raw.kara_abc;
            case "See You":
                return R.raw.seeyou;
            case "Baby Shark":
                return R.raw.babyshark;
            case "Monkey Banana":
                return R.raw.monkeybanana;
            default:
                return -1;
        }
    }

    public static int getThumbRes(String title) {
        switch (title) {
            case "ABC Song":
            case "ABC Karaoke":
                return R.drawable.abc_thumb;
            case "Alphabet Song":
                return R.drawable.alphabet_thumb;
            case "See You":
                return R.drawable.seeyou_thumb;
            case "Baby Shark":
                return R.drawable.babyshark_thumb;
            case "Monkey Banana":
                return R.drawable.monkeybanana_thumb;
            default:
                return R.drawable.bg_default;
        }
    }
}
