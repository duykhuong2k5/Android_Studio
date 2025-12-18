package com.example.pandora.model.response;

public class ResponseDTO {

    private boolean success;     // trạng thái
    private String message;      // mô tả ngắn
    private Object data;         // dữ liệu
    private String error;        // lỗi chi tiết (nếu có)

    // Constructor rỗng (bắt buộc cho JSON)
    public ResponseDTO() {}

    // Constructor đầy đủ
    public ResponseDTO(boolean success, String message, Object data, String error) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    // ======= GETTERS =======

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    // ======= SETTERS =======

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setError(String error) {
        this.error = error;
    }

    // ======= Helper methods =======

    public static ResponseDTO ok(Object data) {
        return new ResponseDTO(true, "success", data, null);
    }

    public static ResponseDTO fail(String message, String error) {
        return new ResponseDTO(false, message, null, error);
    }

    @Override
    public String toString() {
        return "ResponseDTO{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", error='" + error + '\'' +
                '}';
    }
}
