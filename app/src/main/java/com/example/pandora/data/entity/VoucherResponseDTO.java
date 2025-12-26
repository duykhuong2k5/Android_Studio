package com.example.pandora.data.entity;

public class VoucherResponseDTO {

    private boolean success;
    private String code;
    private String description;
    private double discount;

    private String message;

    public VoucherResponseDTO() {}

    public boolean isSuccess() {
        return success;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    // Android sẽ dùng hàm này để lấy tiền giảm
    public double getDiscountAmount() {
        return discount;
    }

    public String getMessage() {
        return message;
    }

    public void setSuccess(boolean success) { this.success = success; }
    public void setCode(String code) { this.code = code; }
    public void setDescription(String description) { this.description = description; }
    public void setDiscount(double discount) { this.discount = discount; }
    public void setMessage(String message) { this.message = message; }
}
