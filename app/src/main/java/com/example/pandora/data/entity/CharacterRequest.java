package com.example.pandora.data.entity;

public class CharacterRequest {
    public String name;
    public String age;
    public String gender;
    public String role;

    public CharacterRequest(String name, String age, String gender, String role) {
        this.name = name; this.age = age; this.gender = gender; this.role = role;
    }
}