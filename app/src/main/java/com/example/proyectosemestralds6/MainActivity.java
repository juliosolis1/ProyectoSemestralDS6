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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView gastoTotalText;
    private TextView cargasTotalesText;
    private TextView kwhCargadosText;
    private TextView promedioCargaText;
    private TextView cargasEnCasaText;
    private RecyclerView recyclerViewRecientes;
    private CargaAdapter cargaAdapter;
    private List<Carga> cargasRecientes;
    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fabAgregarCarga;

    private DatabaseHelper dbHelper;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupDatabase();
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

    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
        // Insertar datos de ejemplo si la base está vacía
        if (dbHelper.getAllCargas().isEmpty()) {
            insertSampleData();
        }
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
        List<Carga> todasLasCargas = dbHelper.getAllCargas();

        // Calcular estadísticas
        double gastoTotal = 0;
        double kwhTotal = 0;
        int cargasEnCasa = 0;

        Calendar cal = Calendar.getInstance();
        int mesActual = cal.get(Calendar.MONTH);
        int añoActual = cal.get(Calendar.YEAR);

        for (Carga carga : todasLasCargas) {
            Calendar cargaCal = Calendar.getInstance();
            cargaCal.setTime(carga.getFecha());

            // Solo contar cargas del mes actual
            if (cargaCal.get(Calendar.MONTH) == mesActual &&
                    cargaCal.get(Calendar.YEAR) == añoActual) {
                gastoTotal += carga.getCosto();
                kwhTotal += carga.getKwhCargados();
                if (carga.getUbicacion().toLowerCase().contains("casa")) {
                    cargasEnCasa++;
                }
            }
        }

        // Actualizar UI
        gastoTotalText.setText("$" + decimalFormat.format(gastoTotal));
        cargasTotalesText.setText(String.valueOf(todasLasCargas.size()));
        kwhCargadosText.setText(decimalFormat.format(kwhTotal));

        if (todasLasCargas.size() > 0) {
            double promedio = gastoTotal / todasLasCargas.size();
            promedioCargaText.setText("$" + decimalFormat.format(promedio));
        } else {
            promedioCargaText.setText("$0.00");
        }

        // Calcular porcentaje de cargas en casa
        int porcentajeCasa = todasLasCargas.size() > 0 ?
                (int) ((double) cargasEnCasa / todasLasCargas.size() * 100) : 0;
        cargasEnCasaText.setText(porcentajeCasa + "%");
    }

    private void setupRecyclerView() {
        cargasRecientes = dbHelper.getCargasRecientes(5);
        cargaAdapter = new CargaAdapter(cargasRecientes, false);
        recyclerViewRecientes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRecientes.setAdapter(cargaAdapter);
    }

    private void insertSampleData() {
        Calendar cal = Calendar.getInstance();

        // Carga 1 - Diciembre
        cal.set(2025, Calendar.DECEMBER, 1, 8, 30);
        dbHelper.insertCarga(new Carga(
                "Casa - Carga lenta",
                cal.getTime(),
                25.9,
                150, // minutos
                12.45
        ));

        // Carga 2 - Noviembre
        cal.set(2025, Calendar.NOVEMBER, 30, 14, 15);
        dbHelper.insertCarga(new Carga(
                "Mall Plaza - Carga rápida",
                cal.getTime(),
                12.1,
                30,
                5.36
        ));

        // Carga 3 - Noviembre
        cal.set(2025, Calendar.NOVEMBER, 25, 19, 45);
        dbHelper.insertCarga(new Carga(
                "Casa - Carga lenta",
                cal.getTime(),
                32.1,
                190,
                16.05
        ));

        // Carga 4 - Noviembre
        cal.set(2025, Calendar.NOVEMBER, 20, 10, 30);
        dbHelper.insertCarga(new Carga(
                "Supermercado - Carga rápida",
                cal.getTime(),
                20.5,
                45,
                8.20
        ));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
        cargasRecientes = dbHelper.getCargasRecientes(5);
        cargaAdapter.updateCargas(cargasRecientes);
        bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
    }
}