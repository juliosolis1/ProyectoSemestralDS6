package com.example.proyectosemestralds6.api;

import com.example.proyectosemestralds6.api.dto.ChargeRequest;
import com.example.proyectosemestralds6.api.dto.LoginRequest;
import com.example.proyectosemestralds6.api.dto.RegisterRequest;
import com.example.proyectosemestralds6.api.dto.VehicleRequest;
import com.example.proyectosemestralds6.api.response.AuthResponse;
import com.example.proyectosemestralds6.database.entities.Carga;
import com.example.proyectosemestralds6.database.entities.Usuario;
import com.example.proyectosemestralds6.database.entities.Vehiculo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {
    // Autenticación
    @POST("auth/register")
    Call<AuthResponse> registerUser(@Body RegisterRequest request);

    @POST("auth/login")
    Call<AuthResponse> loginUser(@Body LoginRequest request);

    // Usuarios
    @PUT("users/{id}")
    Call<Usuario> updateUser(
            @Header("Authorization") String token,
            @Path("id") int userId,
            @Body Usuario user
    );

    // Vehículos
    @POST("vehicles")
    Call<Vehiculo> registerVehicle(
            @Header("Authorization") String token,
            @Body VehicleRequest request
    );

    @GET("users/{userId}/vehicles")
    Call<List<Vehiculo>> getUserVehicles(
            @Header("Authorization") String token,
            @Path("userId") int userId
    );

    @PUT("vehicles/{id}")
    Call<Vehiculo> updateVehicle(
            @Header("Authorization") String token,
            @Path("id") int vehicleId,
            @Body VehicleRequest request
    );

    @DELETE("vehicles/{id}")
    Call<Void> deleteVehicle(
            @Header("Authorization") String token,
            @Path("id") int vehicleId
    );

    // Cargas
    @POST("charges")
    Call<Carga> createCharge(
            @Header("Authorization") String token,
            @Body ChargeRequest request
    );

    @GET("users/{userId}/charges")
    Call<List<Carga>> getUserCharges(
            @Header("Authorization") String token,
            @Path("userId") int userId
    );

    @PUT("charges/{id}")
    Call<Carga> updateCharge(
            @Header("Authorization") String token,
            @Path("id") int chargeId,
            @Body ChargeRequest request
    );

    @DELETE("charges/{id}")
    Call<Void> deleteCharge(
            @Header("Authorization") String token,
            @Path("id") int chargeId
    );

    // Estadísticas
    @GET("stats/{userId}")
    Call<Object> getStats(
            @Header("Authorization") String token,
            @Path("userId") int userId
    );
}