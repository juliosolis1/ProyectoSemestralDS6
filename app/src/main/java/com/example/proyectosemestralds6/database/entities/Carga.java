package com.example.proyectosemestralds6.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "cargas",
        foreignKeys = @ForeignKey(entity = Vehiculo.class,
                parentColumns = "id",
                childColumns = "idVehiculo",
                onDelete = ForeignKey.CASCADE))
public class Carga {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int idVehiculo;
    public String fecha;
    public float energia_kwh;
    public int duracion_min;
    public float costo;
    public String lugar;
}
