package com.example.pandora.data.entity;

public class UserDTO {

    private Long id;

    public UserDTO() {}

    public UserDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
