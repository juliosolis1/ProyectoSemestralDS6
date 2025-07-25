package com.example.proyectosemestralds6;

import java.util.Date;

public class Carga {
    private long id;
    private String ubicacion;
    private Date fecha;
    private double kwhCargados;
    private int duracionMinutos;
    private double costo;

    // Constructor completo
    public Carga(String ubicacion, Date fecha, double kwhCargados, int duracionMinutos, double costo) {
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.kwhCargados = kwhCargados;
        this.duracionMinutos = duracionMinutos;
        this.costo = costo;
    }

    // Constructor con ID
    public Carga(long id, String ubicacion, Date fecha, double kwhCargados, int duracionMinutos, double costo) {
        this.id = id;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.kwhCargados = kwhCargados;
        this.duracionMinutos = duracionMinutos;
        this.costo = costo;
    }

    // Getters
    public long getId() { return id; }
    public String getUbicacion() { return ubicacion; }
    public Date getFecha() { return fecha; }
    public double getKwhCargados() { return kwhCargados; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public double getCosto() { return costo; }

    // Setters
    public void setId(long id) { this.id = id; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public void setKwhCargados(double kwhCargados) { this.kwhCargados = kwhCargados; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    public void setCosto(double costo) { this.costo = costo; }

    // Métodos utilitarios
    public String getDuracionFormateada() {
        int horas = duracionMinutos / 60;
        int minutos = duracionMinutos % 60;
        if (horas > 0) {
            return horas + "h " + minutos + "m";
        } else {
            return minutos + "m";
        }
    }

    public boolean esCargaEnCasa() {
        return ubicacion != null && ubicacion.toLowerCase().contains("casa");
    }

    public boolean esCargaLenta() {
        return duracionMinutos > 60; // Más de 1 hora se considera carga lenta
    }

    public String getTipoCarga() {
        return esCargaLenta() ? "Carga lenta" : "Carga rápida";
    }
}