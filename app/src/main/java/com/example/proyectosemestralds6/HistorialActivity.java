package com.example.proyectosemestralds6;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private Button btnTodos, btnCasa, btnEstacion, btnCargaLenta;
    private RecyclerView recyclerViewHistorial;
    private CargaAdapter cargaAdapter;
    private List<Carga> cargasHistorial;
    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fabAgregarCarga;

    private DatabaseHelper dbHelper;
    private String filtroActual = "todos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        initializeViews();
        setupDatabase();
        setupBottomNavigation();
        setupFloatingActionButton();
        setupFilterButtons();
        loadHistorialData();
        setupRecyclerView();
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

    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
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
            updateFilterButtons();
            loadHistorialData();
        });

        btnCasa.setOnClickListener(v -> {
            filtroActual = "casa";
            updateFilterButtons();
            loadHistorialData();
        });

        btnEstacion.setOnClickListener(v -> {
            filtroActual = "estacion";
            updateFilterButtons();
            loadHistorialData();
        });

        btnCargaLenta.setOnClickListener(v -> {
            filtroActual = "carga_lenta";
            updateFilterButtons();
            loadHistorialData();
        });

        updateFilterButtons();
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
        switch (filtroActual) {
            case "todos":
                cargasHistorial = dbHelper.getAllCargas();
                break;
            case "casa":
                cargasHistorial = dbHelper.getCargasByUbicacion("casa");
                break;
            case "estacion":
                cargasHistorial = dbHelper.getCargasByUbicacion("estacion");
                break;
            case "carga_lenta":
                cargasHistorial = dbHelper.getCargasByTipo("lenta");
                break;
            default:
                cargasHistorial = dbHelper.getAllCargas();
                break;
        }

        if (cargaAdapter != null) {
            cargaAdapter.updateCargas(cargasHistorial);
        }
    }

    private void setupRecyclerView() {
        cargasHistorial = dbHelper.getAllCargas();
        cargaAdapter = new CargaAdapter(cargasHistorial, true);
        recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHistorial.setAdapter(cargaAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistorialData();
        bottomNavigation.setSelectedItemId(R.id.nav_historial);
    }
}