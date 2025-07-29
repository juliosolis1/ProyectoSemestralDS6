package com.example.proyectosemestralds6.database.dao;

import androidx.room.*;
import com.example.proyectosemestralds6.database.entities.Carga;
import java.util.List;

@Dao
public interface CargaDao {
    @Insert
    long insert(Carga carga);

    @Query("SELECT * FROM cargas")
    List<Carga> getAllCargas();

    @Query("SELECT * FROM cargas WHERE lugar LIKE :ubicacion")
    List<Carga> getCargasByUbicacion(String ubicacion);

    @Query("SELECT * FROM cargas WHERE lugar LIKE :tipo")
    List<Carga> getCargasByTipo(String tipo);

    @Query("SELECT * FROM cargas ORDER BY fecha DESC LIMIT :limit")
    List<Carga> getCargasRecientes(int limit);

    @Delete
    void delete(Carga carga);
}

