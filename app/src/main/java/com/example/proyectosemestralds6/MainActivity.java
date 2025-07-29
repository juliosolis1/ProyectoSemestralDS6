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

        db = Room.databaseBuilder(
                        getApplicationContext(),
                        AppDatabase.class, "mi_base_de_datos")
                .allowMainThreadQueries()
                .build();

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

        double gastoTotal = 0, kwhTotal = 0;
        int cargasEnCasa = 0;
        Calendar cal = Calendar.getInstance();
        int mes = cal.get(Calendar.MONTH), año = cal.get(Calendar.YEAR);

        for (Carga c : todas) {
            // asumiendo que fecha está en formato "dd/MM/yyyy"
            String[] partes = c.fecha.split("/");
            int dia = Integer.parseInt(partes[0]) - 1;
            int mesC = Integer.parseInt(partes[1]) - 1;
            int añoC = Integer.parseInt(partes[2]);
            cal.set(añoC, mesC, dia);
            if (mesC == mes && añoC == año) {
                gastoTotal += c.costo;
                kwhTotal += c.energia_kwh;
                if (c.lugar.toLowerCase().contains("casa")) cargasEnCasa++;
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
    }

    private void setupRecyclerView() {
        recyclerViewRecientes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRecientes.setAdapter(cargaAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
        bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
    }
}