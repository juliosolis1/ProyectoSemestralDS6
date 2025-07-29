package com.example.proyectosemestralds6.database.dao;

import androidx.room.*;
import com.example.proyectosemestralds6.database.entities.Usuario;
import java.util.List;

@Dao
public interface UsuarioDao {
    @Insert
    long insert(Usuario usuario);

    @Insert
    void insertAll(Usuario... usuarios);

    @Query("SELECT * FROM usuarios")
    List<Usuario> getAll();

    @Query("SELECT * FROM usuarios WHERE id = :id")
    Usuario getById(int id);

    @Delete
    void delete(Usuario usuario);
    
    @Query("DELETE FROM usuarios")
    void deleteAll();
}