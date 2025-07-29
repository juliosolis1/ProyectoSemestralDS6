package com.example.proyectosemestralds6.api.dto;

public class RegisterRequest {
    private String nombre;
    private String email;
    private String contrasena;
    private String confirmarContrasena;

    public RegisterRequest(String nombre, String email, String contrasena, String confirmarContrasena) {
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.confirmarContrasena = confirmarContrasena;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public String getConfirmarContrasena() { return confirmarContrasena; }
    public void setConfirmarContrasena(String confirmarContrasena) { this.confirmarContrasena = confirmarContrasena; }
}