package com.example.proyectosemestralds6.api.response;

public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;

    // Getters y Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}