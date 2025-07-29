package com.example.proyectosemestralds6;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import com.example.proyectosemestralds6.database.AppDatabase;
import com.example.proyectosemestralds6.database.entities.Carga;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView gastoTotalText, cargasTotalesText, kwhCargadosText,
            promedioCargaText, cargasEnCasaText;
    private RecyclerView recyclerViewRecientes;
    private CargaAdapter cargaAdapter;
    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fabAgregarCarga;

    private AppDatabase db;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getDatabase(this);
        
        // Initialize database with default data
        DatabaseInitializer.initializeDatabase(db);

        initializeViews();
        setupBottomNavigation();
        setupFloatingActionButton();
        loadDashboardData();
        setupRecyclerView();
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
        List<Carga> todas = db.cargaDao().getAllCargas();
        
        // Convert Room entities to your Carga model
        List<com.example.proyectosemestralds6.Carga> cargasModel = new ArrayList<>();
        for (com.example.proyectosemestralds6.database.entities.Carga c : todas) {
            try {
                // Parse date string to Date object
                String[] partes = c.fecha.split("/");
                if (partes.length == 3) {
                    int dia = Integer.parseInt(partes[0]);
                    int mes = Integer.parseInt(partes[1]) - 1; // Calendar months are 0-based
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
                // Skip invalid date entries
                continue;
            }
        }

        double gastoTotal = 0, kwhTotal = 0;
        int cargasEnCasa = 0;
        Calendar cal = Calendar.getInstance();
        int mes = cal.get(Calendar.MONTH), año = cal.get(Calendar.YEAR);

        for (com.example.proyectosemestralds6.database.entities.Carga c : todas) {
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
                // Skip invalid entries
                continue;
            }
        }

        gastoTotalText.setText("$" + decimalFormat.format(gastoTotal));
        cargasTotalesText.setText(String.valueOf(todas.size()));
        kwhCargadosText.setText(decimalFormat.format(kwhTotal));
        promedioCargaText.setText(
                todas.isEmpty() ? "$0.00" : "$" + decimalFormat.format(gastoTotal / todas.size())
        );
        int porcCasa = todas.isEmpty() ? 0 : (cargasEnCasa * 100 / todas.size());
        cargasEnCasaText.setText(porcCasa + "%");
        
        // Setup recent charges
        List<com.example.proyectosemestralds6.database.entities.Carga> recientes = 
            db.cargaDao().getCargasRecientes(5);
        List<com.example.proyectosemestralds6.Carga> recientesModel = new ArrayList<>();
        
        for (com.example.proyectosemestralds6.database.entities.Carga c : recientes) {
            try {
                String[] partes = c.fecha.split("/");
                if (partes.length == 3) {
                    int dia = Integer.parseInt(partes[0]);
                    int mesC = Integer.parseInt(partes[1]) - 1;
                    int añoC = Integer.parseInt(partes[2]);
                    
                    Calendar calTemp = Calendar.getInstance();
                    calTemp.set(añoC, mesC, dia);
                    
                    com.example.proyectosemestralds6.Carga cargaModel = 
                        new com.example.proyectosemestralds6.Carga(
                            c.lugar, 
                            calTemp.getTime(), 
                            c.energia_kwh, 
                            c.duracion_min, 
                            c.costo
                        );
                    cargaModel.setId(c.id);
                    recientesModel.add(cargaModel);
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        if (cargaAdapter == null) {
            cargaAdapter = new CargaAdapter(recientesModel, true);
        } else {
            cargaAdapter.updateCargas(recientesModel);
        }
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