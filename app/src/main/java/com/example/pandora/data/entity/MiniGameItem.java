package com.example.pandora.data.entity;

public class MiniGameItem {
    public String tag;        // MINI GAME #1
    public String title;      // Feed Jerry
    public String desc;       // ...
    public String actionText; // Start Game →
    public int leftBgRes;     // background gradient
    public int iconRes;       // icon/illustration
    public int actionColor;   // color resource
    public Class<?> targetActivity;

    public MiniGameItem(String tag, String title, String desc, String actionText,
                        int leftBgRes, int iconRes, int actionColor,
                        Class<?> targetActivity) {
        this.tag = tag;
        this.title = title;
        this.desc = desc;
        this.actionText = actionText;
        this.leftBgRes = leftBgRes;
        this.iconRes = iconRes;
        this.actionColor = actionColor;
        this.targetActivity = targetActivity;
    }
}
