package com.example.proyectosemestralds6;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
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
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView gastoTotalText, cargasTotalesText, kwhCargadosText,
            promedioCargaText, cargasEnCasaText;
    private RecyclerView recyclerViewRecientes;
    private CargaAdapter cargaAdapter;
    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fabAgregarCarga;

    private AppDatabase db;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getDatabase(this);
        preferences = getSharedPreferences("EVChargeTracker", MODE_PRIVATE);
        DatabaseInitializer.initializeDatabase(db);

        initializeViews();
        setupBottomNavigation();
        setupFloatingActionButton();
        loadDashboardData();
    }

    private void initializeViews() {
        gastoTotalText = findViewById(R.id.txt_gasto_total);
        cargasTotalesText = findViewById(R.id.txt_cargas_totales);
        kwhCargadosText = findViewById(R.id.txt_kwh_cargados);
        promedioCargaText = findViewById(R.id.txt_promedio_carga);
        cargasEnCasaText = findViewById(R.id.txt_cargas_casa);
        recyclerViewRecientes = findViewById(R.id.recycler_cargas_recientes);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        fabAgregarCarga = findViewById(R.id.fab_agregar_carga);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_dashboard) {
                    return true;
                } else if (itemId == R.id.nav_historial) {
                    startActivity(new Intent(MainActivity.this, HistorialActivity.class));
                    return true;
                } else if (itemId == R.id.nav_perfil) {
                    startActivity(new Intent(MainActivity.this, PerfilActivity.class));
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
                Intent intent = new Intent(MainActivity.this, AgregarCargaActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadDashboardData() {
        String token = "Bearer " + obtenerTokenDeSharedPreferences();
        int userId = obtenerUserIdDeSharedPreferences();

        ApiInterface apiService = ApiClient.getApiService();
        Call<List<Carga>> call = apiService.getUserCharges(token, userId);

        call.enqueue(new Callback<List<Carga>>() {
            @Override
            public void onResponse(Call<List<Carga>> call, Response<List<Carga>> response) {
                if (response.isSuccessful()) {
                    List<Carga> cargasRemotas = response.body();
                    actualizarUI(cargasRemotas);
                    guardarCargasLocales(cargasRemotas);
                } else {
                    // Cargar datos locales si falla
                    List<Carga> cargasLocales = db.cargaDao().getAllCargas();
                    actualizarUI(cargasLocales);
                }
            }

            @Override
            public void onFailure(Call<List<Carga>> call, Throwable t) {
                List<Carga> cargasLocales = db.cargaDao().getAllCargas();
                actualizarUI(cargasLocales);
            }
        });
    }

    private void actualizarUI(List<Carga> cargas) {
        // Convertir a modelo para el dashboard
        List<com.example.proyectosemestralds6.Carga> cargasModel = convertirCargasRoomAModelo(cargas);

        double gastoTotal = 0, kwhTotal = 0;
        int cargasEnCasa = 0;
        Calendar cal = Calendar.getInstance();
        int mes = cal.get(Calendar.MONTH), año = cal.get(Calendar.YEAR);

        for (Carga c : cargas) {
            try {
                String[] partes = c.fecha.split("/");
                if (partes.length == 3) {
                    int dia = Integer.parseInt(partes[0]);
                    int mesC = Integer.parseInt(partes[1]) - 1;
                    int añoC = Integer.parseInt(partes[2]);

                    if (mesC == mes && añoC == año) {
                        gastoTotal += c.costo;
                        kwhTotal += c.energia_kwh;
                        if (c.lugar.toLowerCase().contains("casa")) cargasEnCasa++;
                    }
                }
            } catch (Exception e) {
                continue;
            }
        }

        gastoTotalText.setText("$" + decimalFormat.format(gastoTotal));
        cargasTotalesText.setText(String.valueOf(cargas.size()));
        kwhCargadosText.setText(decimalFormat.format(kwhTotal));
        promedioCargaText.setText(
                cargas.isEmpty() ? "$0.00" : "$" + decimalFormat.format(gastoTotal / cargas.size())
        );
        int porcCasa = cargas.isEmpty() ? 0 : (cargasEnCasa * 100 / cargas.size());
        cargasEnCasaText.setText(porcCasa + "%");

        // Obtener cargas recientes (últimas 5)
        List<Carga> recientes = cargas.subList(0, Math.min(5, cargas.size()));
        List<com.example.proyectosemestralds6.Carga> recientesModel = convertirCargasRoomAModelo(recientes);

        if (cargaAdapter == null) {
            cargaAdapter = new CargaAdapter(recientesModel, true);
            recyclerViewRecientes.setAdapter(cargaAdapter);
        } else {
            cargaAdapter.updateCargas(recientesModel);
        }
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

    private void guardarCargasLocales(List<Carga> cargas) {
        new Thread(() -> {
            db.cargaDao().deleteAll();
            db.cargaDao().insertAll(cargas.toArray(new Carga[0]));
        }).start();
    }

    private String obtenerTokenDeSharedPreferences() {
        return preferences.getString("token", "");
    }

    private int obtenerUserIdDeSharedPreferences() {
        return preferences.getInt("userId", 1);
    }

    private void setupRecyclerView() {
        recyclerViewRecientes.setLayoutManager(new LinearLayoutManager(this));
        if (cargaAdapter != null) {
            recyclerViewRecientes.setAdapter(cargaAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
        bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
    }
}