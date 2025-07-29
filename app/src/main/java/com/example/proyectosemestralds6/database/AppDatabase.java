package com.example.proyectosemestralds6.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.proyectosemestralds6.database.entities.*;
import com.example.proyectosemestralds6.database.dao.*;

@Database(entities = {Usuario.class, Vehiculo.class, Carga.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UsuarioDao usuarioDao();
    public abstract VehiculoDao vehiculoDao();
    public abstract CargaDao cargaDao();
}
