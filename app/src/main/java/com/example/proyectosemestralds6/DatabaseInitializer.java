package com.example.proyectosemestralds6;

import com.example.proyectosemestralds6.database.AppDatabase;
import com.example.proyectosemestralds6.database.entities.Usuario;
import com.example.proyectosemestralds6.database.entities.Vehiculo;
import com.example.proyectosemestralds6.database.entities.Carga;

public class DatabaseInitializer {
    
    public static void initializeDatabase(AppDatabase db) {
        // Check if database is already initialized
        if (db.usuarioDao().getAll().size() > 0) {
            return; // Database already has data
        }
        
        // Create default user
        Usuario defaultUser = new Usuario();
        defaultUser.nombre = "Juan Díaz";
        defaultUser.email = "juan.diaz@email.com";
        defaultUser.contrasena = "password123";
        
        long userId = db.usuarioDao().insert(defaultUser);
        
        // Create default vehicle
        Vehiculo defaultVehicle = new Vehiculo();
        defaultVehicle.idUsuario = (int) userId;
        defaultVehicle.marca = "Tesla";
        defaultVehicle.modelo = "Model 3";
        defaultVehicle.anio = 2023;
        defaultVehicle.capacidad_kwh = 75.0f;
        defaultVehicle.placa = "ABC-123";
        
        long vehicleId = db.vehiculoDao().insert(defaultVehicle);
        
        // Create sample charging records
        createSampleCharges(db, (int) vehicleId);
    }
    
    private static void createSampleCharges(AppDatabase db, int vehicleId) {
        Carga[] sampleCharges = {
            createCarga(vehicleId, "Casa - Carga lenta", "15/12/2024", 25.5f, 180, 12.75f),
            createCarga(vehicleId, "Mall Plaza - Carga rápida", "14/12/2024", 45.2f, 45, 22.60f),
            createCarga(vehicleId, "Casa - Carga lenta", "13/12/2024", 30.1f, 210, 15.05f),
            createCarga(vehicleId, "Supermercado - Carga rápida", "12/12/2024", 38.7f, 35, 19.35f),
            createCarga(vehicleId, "Casa - Carga lenta", "11/12/2024", 28.3f, 195, 14.15f)
        };
        
        db.cargaDao().insertAll(sampleCharges);
    }
    
    private static Carga createCarga(int vehicleId, String lugar, String fecha, 
                                   float energia_kwh, int duracion_min, float costo) {
        Carga carga = new Carga();
        carga.idVehiculo = vehicleId;
        carga.lugar = lugar;
        carga.fecha = fecha;
        carga.energia_kwh = energia_kwh;
        carga.duracion_min = duracion_min;
        carga.costo = costo;
        return carga;
    }
}