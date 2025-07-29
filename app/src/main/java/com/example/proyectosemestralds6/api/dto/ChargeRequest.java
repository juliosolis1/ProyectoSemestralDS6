package com.example.proyectosemestralds6.api.dto;

import java.util.Date;

public class ChargeRequest {
    private int vehicleId;
    private Date fecha;
    private float energia_kwh;
    private int duracion_min;
    private float costo;
    private String lugar;

    public ChargeRequest(int vehicleId, Date fecha, float energia_kwh,
                         int duracion_min, float costo, String lugar) {
        this.vehicleId = vehicleId;
        this.fecha = fecha;
        this.energia_kwh = energia_kwh;
        this.duracion_min = duracion_min;
        this.costo = costo;
        this.lugar = lugar;
    }

    // Getters y Setters
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public float getEnergia_kwh() { return energia_kwh; }
    public void setEnergia_kwh(float energia_kwh) { this.energia_kwh = energia_kwh; }
    public int getDuracion_min() { return duracion_min; }
    public void setDuracion_min(int duracion_min) { this.duracion_min = duracion_min; }
    public float getCosto() { return costo; }
    public void setCosto(float costo) { this.costo = costo; }
    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }
}