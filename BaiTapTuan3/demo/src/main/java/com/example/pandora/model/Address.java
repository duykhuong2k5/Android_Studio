package com.example.pandora.model;

import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // liên kết với User (user_id trong DB)
    @Column(nullable = false)
    private Long userId;

    // thông tin người nhận
    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 20)
    private String phone;

    // Địa chỉ chi tiết chia nhỏ
    @Column(nullable = false, length = 255)
    private String house;      // số nhà / xóm / ấp

    @Column(nullable = false, length = 255)
    private String street;     // đường / thôn

    @Column(nullable = false, length = 100)
    private String ward;       // phường / xã

    @Column(nullable = false, length = 100)
    private String district;   // quận / huyện

    @Column(nullable = false, length = 100)
    private String province;   // tỉnh / thành phố

    // có thể có ghi chú thêm (không bắt buộc)
    private String note;

    @Column(name = "is_default")
    private boolean isDefault = false;

    // ===== Constructors =====
    public Address() {}

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getHouse() { return house; }
    public void setHouse(String house) { this.house = house; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
}
