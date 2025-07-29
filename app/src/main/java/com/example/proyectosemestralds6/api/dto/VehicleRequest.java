package com.example.proyectosemestralds6.api.dto;

public class VehicleRequest {
    private int usuarioId;
    private String marca;
    private String modelo;
    private int anio;
    private float capacidad_kwh;
    private String placa;

    public VehicleRequest(int usuarioId, String marca, String modelo,
                          int anio, float capacidad_kwh, String placa) {
        this.usuarioId = usuarioId;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.capacidad_kwh = capacidad_kwh;
        this.placa = placa;
    }

    // Getters y Setters
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }
    public float getCapacidad_kwh() { return capacidad_kwh; }
    public void setCapacidad_kwh(float capacidad_kwh) { this.capacidad_kwh = capacidad_kwh; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
}