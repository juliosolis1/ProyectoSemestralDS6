package com.example.proyectosemestralds6.database.dao;

import androidx.room.*;
import com.example.proyectosemestralds6.database.entities.Usuario;
import java.util.List;

@Dao
public interface UsuarioDao {
    @Insert
    void insert(Usuario usuario);

    @Query("SELECT * FROM usuarios")
    List<Usuario> getAll();

    @Delete
    void delete(Usuario usuario);
}