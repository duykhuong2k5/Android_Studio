package com.example.pandora.data.entity;

public class User {
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String role;

    public User(Long id, String fullName, String email, String password, String phone, String address, String role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.role = role != null ? role : "ROLE_CUSTOMER";
    }

    // Constructor không có id
    public User(String fullName, String email, String password, String phone, String address) {
        this(null, fullName, email, password, phone, address, "ROLE_CUSTOMER");
    }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
