package com.example.pandora.dto;

public class AddressRequest {

    private Long userId;
    private String fullName;
    private String phone;
    private String house;
    private String street;
    private String ward;
    private String district;
    private String province;
    private String note;
    private boolean isDefault;

    // getters & setters
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
