package com.example.pandora.dto;

import com.example.pandora.model.Address;

public class AddressDTO {

    private Long id;
    private String fullName;
    private String phone;
    private String house;
    private String street;
    private String ward;
    private String district;
    private String province;
    private String note;

    public AddressDTO(Address a) {
        this.id = a.getId();
        this.fullName = a.getFullName();
        this.phone = a.getPhone();
        this.house = a.getHouse();
        this.street = a.getStreet();
        this.ward = a.getWard();
        this.district = a.getDistrict();
        this.province = a.getProvince();
        this.note = a.getNote();
    }

    // GETTERS
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getHouse() { return house; }
    public String getStreet() { return street; }
    public String getWard() { return ward; }
    public String getDistrict() { return district; }
    public String getProvince() { return province; }
    public String getNote() { return note; }
}
