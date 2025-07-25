package com.example.proyectosemestralds6;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AgregarCargaActivity extends AppCompatActivity {

    private EditText editFecha, editHora, editKwh, editDuracionHoras, editDuracionMinutos;
    private Spinner spinnerUbicacion;
    private Button btnGuardarCarga;
    private Toolbar toolbar;

    private DatabaseHelper dbHelper;
    private SharedPreferences preferences;
    private Calendar selectedDateTime;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_carga);

        initializeViews();
        setupToolbar();
        setupDatabase();
        setupSpinner();
        setupDateTimePickers();
        setupSaveButton();
        setDefaultValues();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        editFecha = findViewById(R.id.edit_fecha);
        editHora = findViewById(R.id.edit_hora);
        editKwh = findViewById(R.id.edit_kwh);
        editDuracionHoras = findViewById(R.id.edit_duracion_horas);
        editDuracionMinutos = findViewById(R.id.edit_duracion_minutos);
        spinnerUbicacion = findViewById(R.id.spinner_ubicacion);
        btnGuardarCarga = findViewById(R.id.btn_guardar_carga);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Nueva Carga");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
        preferences = getSharedPreferences("EVChargeTracker", MODE_PRIVATE);
    }

    private void setupSpinner() {
        String[] ubicaciones = {
                "Casa - Carga lenta",
                "Casa - Carga rápida",
                "Mall Plaza - Carga rápida",
                "Supermercado - Carga rápida",
                "Estación de servicio - Carga rápida",
                "Centro comercial - Carga rápida",
                "Oficina - Carga lenta",
                "Otro"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, ubicaciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUbicacion.setAdapter(adapter);
    }

    private void setupDateTimePickers() {
        selectedDateTime = Calendar.getInstance();

        editFecha.setOnClickListener(v -> showDatePicker());
        editHora.setOnClickListener(v -> showTimePicker());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    editFecha.setText(dateFormat.format(selectedDateTime.getTime()));
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    editHora.setText(timeFormat.format(selectedDateTime.getTime()));
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void setupSaveButton() {
        btnGuardarCarga.setOnClickListener(v -> guardarCarga());
    }

    private void setDefaultValues() {
        // Establecer fecha y hora actuales
        Date now = new Date();
        selectedDateTime.setTime(now);
        editFecha.setText(dateFormat.format(now));
        editHora.setText(timeFormat.format(now));

        // Valores por defecto
        editDuracionHoras.setText("0");
        editDuracionMinutos.setText("30");
    }

    private void guardarCarga() {
        try {
            // Validar campos
            if (!validarCampos()) {
                return;
            }

            // Obtener valores
            String ubicacion = spinnerUbicacion.getSelectedItem().toString();
            double kwh = Double.parseDouble(editKwh.getText().toString());
            int horas = Integer.parseInt(editDuracionHoras.getText().toString());
            int minutos = Integer.parseInt(editDuracionMinutos.getText().toString());
            int duracionTotal = (horas * 60) + minutos;

            // Calcular costo basado en tarifa eléctrica
            float tarifaElectrica = preferences.getFloat("tarifa_electrica", 0.50f);
            double costo = kwh * tarifaElectrica;

            // Crear objeto Carga
            Carga nuevaCarga = new Carga(
                    ubicacion,
                    selectedDateTime.getTime(),
                    kwh,
                    duracionTotal,
                    costo
            );

            // Guardar en base de datos
            long resultado = dbHelper.insertCarga(nuevaCarga);

            if (resultado != -1) {
                Toast.makeText(this, "Carga guardada exitosamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al guardar la carga", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Por favor ingrese valores numéricos válidos", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error inesperado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validarCampos() {
        // Validar kWh
        String kwhStr = editKwh.getText().toString().trim();
        if (kwhStr.isEmpty()) {
            editKwh.setError("Ingrese los kWh cargados");
            return false;
        }

        try {
            double kwh = Double.parseDouble(kwhStr);
            if (kwh <= 0) {
                editKwh.setError("Los kWh deben ser mayor a 0");
                return false;
            }
        } catch (NumberFormatException e) {
            editKwh.setError("Valor inválido");
            return false;
        }

        // Validar duración
        String horasStr = editDuracionHoras.getText().toString().trim();
        String minutosStr = editDuracionMinutos.getText().toString().trim();

        if (horasStr.isEmpty()) horasStr = "0";
        if (minutosStr.isEmpty()) minutosStr = "0";

        try {
            int horas = Integer.parseInt(horasStr);
            int minutos = Integer.parseInt(minutosStr);

            if (horas < 0 || minutos < 0) {
                Toast.makeText(this, "La duración no puede ser negativa", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (horas == 0 && minutos == 0) {
                Toast.makeText(this, "La duración debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (minutos >= 60) {
                Toast.makeText(this, "Los minutos deben ser menores a 60", Toast.LENGTH_SHORT).show();
                return false;
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Duración inválida", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar fecha
        if (editFecha.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Seleccione una fecha", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (editHora.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Seleccione una hora", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}