package com.example.proyectosemestralds6;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyectosemestralds6.api.ApiClient;
import com.example.proyectosemestralds6.api.ApiInterface;
import com.example.proyectosemestralds6.database.AppDatabase;
import com.example.proyectosemestralds6.database.entities.Carga;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistorialActivity extends AppCompatActivity {

    private Button btnTodos, btnCasa, btnEstacion, btnCargaLenta;
    private RecyclerView recyclerViewHistorial;
    private CargaAdapter cargaAdapter;
    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fabAgregarCarga;

    private AppDatabase db;
    private String filtroActual = "todos";
    private List<com.example.proyectosemestralds6.Carga> cargasModel = new ArrayList<>();
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        db = AppDatabase.getDatabase(this);
        preferences = getSharedPreferences("EVChargeTracker", MODE_PRIVATE);

        initializeViews();
        setupBottomNavigation();
        setupFloatingActionButton();
        setupFilterButtons();
        setupRecyclerView();
        loadHistorialData();
    }

    private void initializeViews() {
        btnTodos = findViewById(R.id.btn_todos);
        btnCasa = findViewById(R.id.btn_casa);
        btnEstacion = findViewById(R.id.btn_estacion);
        btnCargaLenta = findViewById(R.id.btn_carga_lenta);
        recyclerViewHistorial = findViewById(R.id.recycler_historial);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        fabAgregarCarga = findViewById(R.id.fab_agregar_carga);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_historial);
        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_dashboard) {
                    startActivity(new Intent(HistorialActivity.this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.nav_historial) {
                    return true;
                } else if (itemId == R.id.nav_perfil) {
                    startActivity(new Intent(HistorialActivity.this, PerfilActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void setupFloatingActionButton() {
        fabAgregarCarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistorialActivity.this, AgregarCargaActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupFilterButtons() {
        btnTodos.setOnClickListener(v -> {
            filtroActual = "todos";
            loadHistorialData();
        });
        btnCasa.setOnClickListener(v -> {
            filtroActual = "Casa%";
            loadHistorialData();
        });
        btnEstacion.setOnClickListener(v -> {
            filtroActual = "Estación%";
            loadHistorialData();
        });
        btnCargaLenta.setOnClickListener(v -> {
            filtroActual = "lenta%";
            loadHistorialData();
        });
    }

    private void updateFilterButtons() {
        // Reset all buttons
        btnTodos.setBackgroundResource(R.drawable.button_filter_unselected);
        btnCasa.setBackgroundResource(R.drawable.button_filter_unselected);
        btnEstacion.setBackgroundResource(R.drawable.button_filter_unselected);
        btnCargaLenta.setBackgroundResource(R.drawable.button_filter_unselected);

        // Set selected button
        switch (filtroActual) {
            case "todos":
                btnTodos.setBackgroundResource(R.drawable.button_filter_selected);
                break;
            case "casa":
                btnCasa.setBackgroundResource(R.drawable.button_filter_selected);
                break;
            case "estacion":
                btnEstacion.setBackgroundResource(R.drawable.button_filter_unselected);
                break;
            case "carga_lenta":
                btnCargaLenta.setBackgroundResource(R.drawable.button_filter_unselected);
                break;
        }
    }

    private void loadHistorialData() {
        String token = "Bearer " + obtenerTokenDeSharedPreferences();
        int userId = obtenerUserIdDeSharedPreferences();

        ApiInterface apiService = ApiClient.getApiService();
        Call<List<Carga>> call = apiService.getUserCharges(token, userId);

        call.enqueue(new Callback<List<Carga>>() {
            @Override
            public void onResponse(Call<List<Carga>> call, Response<List<Carga>> response) {
                if (response.isSuccessful()) {
                    List<Carga> cargasRemotas = response.body();
                    cargasModel = convertirCargasRoomAModelo(cargasRemotas);
                    actualizarAdaptador();

                    // Guardar localmente
                    new Thread(() -> {
                        db.cargaDao().deleteAll();
                        db.cargaDao().insertAll(cargasRemotas.toArray(new Carga[0]));
                    }).start();
                } else {
                    cargasModel = cargarDatosLocales();
                    actualizarAdaptador();
                }
            }

            @Override
            public void onFailure(Call<List<Carga>> call, Throwable t) {
                cargasModel = cargarDatosLocales();
                actualizarAdaptador();
            }
        });
    }

    private List<com.example.proyectosemestralds6.Carga> cargarDatosLocales() {
        List<Carga> cargasLocales = db.cargaDao().getAllCargas();
        return convertirCargasRoomAModelo(cargasLocales);
    }

    private List<com.example.proyectosemestralds6.Carga> convertirCargasRoomAModelo(List<Carga> cargasRoom) {
        List<com.example.proyectosemestralds6.Carga> cargasModel = new ArrayList<>();
        for (Carga c : cargasRoom) {
            try {
                String[] partes = c.fecha.split("/");
                if (partes.length == 3) {
                    int dia = Integer.parseInt(partes[0]);
                    int mes = Integer.parseInt(partes[1]) - 1;
                    int año = Integer.parseInt(partes[2]);

                    Calendar cal = Calendar.getInstance();
                    cal.set(año, mes, dia);

                    com.example.proyectosemestralds6.Carga cargaModel =
                            new com.example.proyectosemestralds6.Carga(
                                    c.lugar,
                                    cal.getTime(),
                                    c.energia_kwh,
                                    c.duracion_min,
                                    c.costo
                            );
                    cargaModel.setId(c.id);
                    cargasModel.add(cargaModel);
                }
            } catch (Exception e) {
                continue;
            }
        }
        return cargasModel;
    }

    private void actualizarAdaptador() {
        if (cargaAdapter == null) {
            cargaAdapter = new CargaAdapter(cargasModel, true);
            recyclerViewHistorial.setAdapter(cargaAdapter);
        } else {
            cargaAdapter.updateCargas(cargasModel);
        }
    }

    private void setupRecyclerView() {
        recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(this));
        if (cargaAdapter == null) {
            cargaAdapter = new CargaAdapter(cargasModel, true);
        }
        recyclerViewHistorial.setAdapter(cargaAdapter);
    }

    private String obtenerTokenDeSharedPreferences() {
        return preferences.getString("token", "");
    }

    private int obtenerUserIdDeSharedPreferences() {
        return preferences.getInt("userId", 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistorialData();
        bottomNavigation.setSelectedItemId(R.id.nav_historial);
    }
}