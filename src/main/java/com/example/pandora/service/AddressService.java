package com.example.pandora.service;

import com.example.pandora.dto.AddressRequest;
import com.example.pandora.model.Address;
import com.example.pandora.repository.AddressRepository;
import com.example.pandora.repository.UserRepository;
import com.example.pandora.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepo;

    @Autowired
    private UserRepository userRepo;

    // validate: yêu cầu đủ 4 phần
    private void validate(AddressRequest dto) {
        if (dto.getUserId() == null)
            throw new IllegalArgumentException("Thiếu userId");

        if (isBlank(dto.getFullName()))
            throw new IllegalArgumentException("Họ tên không được để trống");

        if (isBlank(dto.getPhone()))
            throw new IllegalArgumentException("Số điện thoại không được để trống");

        if (isBlank(dto.getHouse()) ||
                isBlank(dto.getStreet()) ||
                isBlank(dto.getWard()) ||
                isBlank(dto.getDistrict()) ||
                isBlank(dto.getProvince())) {
            throw new IllegalArgumentException(
                    "Địa chỉ phải có đủ: số nhà/xóm, đường/thôn, phường/xã, quận/huyện, tỉnh/thành phố"
            );
        }

        User u = userRepo.findById(dto.getUserId()).orElse(null);
        if (u == null)
            throw new IllegalArgumentException("User không tồn tại");
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public Address create(AddressRequest dto) {
        validate(dto);

        Address address = new Address();
        copy(dto, address);

        // nếu isDefault = true → bỏ default cũ
        if (dto.isDefault()) {
            clearDefaultForUser(dto.getUserId());
            address.setDefault(true);
        }

        return addressRepo.save(address);
    }

    public Address update(Long id, AddressRequest dto) {
        Address old = addressRepo.findById(id).orElse(null);
        if (old == null)
            throw new IllegalArgumentException("Không tìm thấy địa chỉ");

        // userId phải giữ nguyên / hoặc kiểm tra cho chắc
        dto.setUserId(old.getUserId());
        validate(dto);

        copy(dto, old);

        if (dto.isDefault()) {
            clearDefaultForUser(old.getUserId());
            old.setDefault(true);
        }

        return addressRepo.save(old);
    }

    public void delete(Long id) {
        addressRepo.deleteById(id);
    }

    public List<Address> listByUser(Long userId) {
        return addressRepo.findByUserIdOrderByIsDefaultDescIdDesc(userId);
    }

    public Address setDefault(Long id) {
        Address address = addressRepo.findById(id).orElse(null);
        if (address == null)
            throw new IllegalArgumentException("Không tìm thấy địa chỉ");

        clearDefaultForUser(address.getUserId());
        address.setDefault(true);
        return addressRepo.save(address);
    }

    private void clearDefaultForUser(Long userId) {
        List<Address> list = addressRepo.findByUserId(userId);
        for (Address a : list) {
            if (a.isDefault()) {
                a.setDefault(false);
                addressRepo.save(a);
            }
        }
    }

    private void copy(AddressRequest dto, Address a) {
        a.setUserId(dto.getUserId());
        a.setFullName(dto.getFullName());
        a.setPhone(dto.getPhone());
        a.setHouse(dto.getHouse());
        a.setStreet(dto.getStreet());
        a.setWard(dto.getWard());
        a.setDistrict(dto.getDistrict());
        a.setProvince(dto.getProvince());
        a.setNote(dto.getNote());
        a.setDefault(dto.isDefault());
    }
}
