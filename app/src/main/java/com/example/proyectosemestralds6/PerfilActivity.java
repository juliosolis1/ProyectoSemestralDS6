package com.example.proyectosemestralds6;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.DecimalFormat;

public class PerfilActivity extends AppCompatActivity {

    private TextView txtNombreUsuario, txtEmailUsuario;
    private EditText editTarifaElectrica, editPresupuestoMensual;
    private Button btnGuardarConfiguracion;
    private BottomNavigationView bottomNavigation;

    private SharedPreferences preferences;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        initializeViews();
        setupSharedPreferences();
        setupBottomNavigation();
        loadUserData();
        setupSaveButton();
    }

    private void initializeViews() {
        txtNombreUsuario = findViewById(R.id.txt_nombre_usuario);
        txtEmailUsuario = findViewById(R.id.txt_email_usuario);
        editTarifaElectrica = findViewById(R.id.edit_tarifa_electrica);
        editPresupuestoMensual = findViewById(R.id.edit_presupuesto_mensual);
        btnGuardarConfiguracion = findViewById(R.id.btn_guardar_configuracion);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupSharedPreferences() {
        preferences = getSharedPreferences("EVChargeTracker", MODE_PRIVATE);

        // Establecer valores por defecto si no existen
        if (!preferences.contains("nombre_usuario")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("nombre_usuario", "Juan Díaz");
            editor.putString("email_usuario", "juan.diaz@email.com");
            editor.putFloat("tarifa_electrica", 0.50f);
            editor.putFloat("presupuesto_mensual", 200.00f);
            editor.apply();
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_perfil);
        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_dashboard) {
                    startActivity(new Intent(PerfilActivity.this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.nav_historial) {
                    startActivity(new Intent(PerfilActivity.this, HistorialActivity.class));
                    return true;
                } else if (itemId == R.id.nav_perfil) {
                    return true;
                }
                return false;
            }
        });
    }

    private void loadUserData() {
        String nombre = preferences.getString("nombre_usuario", "Juan Díaz");
        String email = preferences.getString("email_usuario", "juan.diaz@email.com");
        float tarifaElectrica = preferences.getFloat("tarifa_electrica", 0.50f);
        float presupuestoMensual = preferences.getFloat("presupuesto_mensual", 200.00f);

        txtNombreUsuario.setText(nombre);
        txtEmailUsuario.setText(email);
        editTarifaElectrica.setText(decimalFormat.format(tarifaElectrica));
        editPresupuestoMensual.setText(decimalFormat.format(presupuestoMensual));
    }

    private void setupSaveButton() {
        btnGuardarConfiguracion.setOnClickListener(v -> {
            saveUserConfiguration();
        });
    }

    private void saveUserConfiguration() {
        try {
            String tarifaStr = editTarifaElectrica.getText().toString();
            String presupuestoStr = editPresupuestoMensual.getText().toString();

            float tarifa = Float.parseFloat(tarifaStr);
            float presupuesto = Float.parseFloat(presupuestoStr);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putFloat("tarifa_electrica", tarifa);
            editor.putFloat("presupuesto_mensual", presupuesto);
            editor.apply();

            // Mostrar mensaje de confirmación
            showToast("Configuración guardada exitosamente");

        } catch (NumberFormatException e) {
            showToast("Por favor ingrese valores numéricos válidos");
        }
    }

    private void showToast(String mensaje) {
        android.widget.Toast.makeText(this, mensaje, android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigation.setSelectedItemId(R.id.nav_perfil);
    }
}