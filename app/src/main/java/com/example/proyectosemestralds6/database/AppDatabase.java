package com.example.proyectosemestralds6.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.proyectosemestralds6.database.entities.*;
import com.example.proyectosemestralds6.database.dao.*;

@Database(entities = {Usuario.class, Vehiculo.class, Carga.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UsuarioDao usuarioDao();
    public abstract VehiculoDao vehiculoDao();
    public abstract CargaDao cargaDao();
    
    private static volatile AppDatabase INSTANCE;
    
    public static AppDatabase getDatabase(android.content.Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "mi_base_de_datos")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries() // Only for development
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
