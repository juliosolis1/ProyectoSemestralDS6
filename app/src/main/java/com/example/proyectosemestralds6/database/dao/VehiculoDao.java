package com.example.proyectosemestralds6.database.dao;

import androidx.room.*;
import com.example.proyectosemestralds6.database.entities.Vehiculo;
import java.util.List;

@Dao
public interface VehiculoDao {
    @Insert
    void insert(Vehiculo vehiculo);

    @Query("SELECT * FROM vehiculos WHERE idUsuario = :idUsuario")
    List<Vehiculo> getByUsuario(int idUsuario);

    @Delete
    void delete(Vehiculo vehiculo);
}
