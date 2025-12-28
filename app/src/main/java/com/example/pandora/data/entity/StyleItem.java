package com.example.pandora.data.entity;

public class StyleItem {
    private String name;
    private int backgroundRes;

    public StyleItem(String name, int backgroundRes) {
        this.name = name;
        this.backgroundRes = backgroundRes;
    }

    public String getName() { return name; }
    public int getBackgroundRes() { return backgroundRes; }
}