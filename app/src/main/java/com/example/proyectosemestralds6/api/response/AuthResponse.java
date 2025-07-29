package com.example.proyectosemestralds6.api.response;

public class AuthResponse {
    private String token;
    private int userId;
    private String message;

    // Getters y Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}