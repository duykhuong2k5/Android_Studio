package com.example.pandora.data.entity;

public class ProductDTO {

    private Long id;

    // Constructor rỗng (bắt buộc)
    public ProductDTO() {}

    // Constructor đầy đủ
    public ProductDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
