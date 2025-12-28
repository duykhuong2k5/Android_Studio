package com.example.pandora.data.entity;

public class CharacterModel {
    public String name;
    public String role; // e.g. "Main Character"
    public String age;
    public String gender;
    public int imageResId;

    public CharacterModel(String name, String role, String age, String gender, int imageResId) {
        this.name = name;
        this.role = role;
        this.age = age;
        this.gender = gender;
        this.imageResId = imageResId;
    }
}