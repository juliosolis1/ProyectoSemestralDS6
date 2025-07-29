package com.example.proyectosemestralds6.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuarios")
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nombre;
    public String email;
    public String contrasena;
}
