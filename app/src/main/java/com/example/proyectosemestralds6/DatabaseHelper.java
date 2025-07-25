package com.example.proyectosemestralds6;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "EVChargeTracker.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla cargas
    private static final String TABLE_CARGAS = "cargas";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_UBICACION = "ubicacion";
    private static final String COLUMN_FECHA = "fecha";
    private static final String COLUMN_KWH = "kwh_cargados";
    private static final String COLUMN_DURACION = "duracion_minutos";
    private static final String COLUMN_COSTO = "costo";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_CARGAS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_UBICACION + " TEXT NOT NULL, " +
                COLUMN_FECHA + " TEXT NOT NULL, " +
                COLUMN_KWH + " REAL NOT NULL, " +
                COLUMN_DURACION + " INTEGER NOT NULL, " +
                COLUMN_COSTO + " REAL NOT NULL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARGAS);
        onCreate(db);
    }

    // Insertar nueva carga
    public long insertCarga(Carga carga) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_UBICACION, carga.getUbicacion());
        values.put(COLUMN_FECHA, dateFormat.format(carga.getFecha()));
        values.put(COLUMN_KWH, carga.getKwhCargados());
        values.put(COLUMN_DURACION, carga.getDuracionMinutos());
        values.put(COLUMN_COSTO, carga.getCosto());

        long id = db.insert(TABLE_CARGAS, null, values);
        db.close();
        return id;
    }

    // Obtener todas las cargas
    public List<Carga> getAllCargas() {
        List<Carga> cargas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_CARGAS + " ORDER BY " + COLUMN_FECHA + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Carga carga = cursorToCarga(cursor);
                cargas.add(carga);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cargas;
    }

    // Obtener cargas recientes (límite especificado)
    public List<Carga> getCargasRecientes(int limite) {
        List<Carga> cargas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_CARGAS +
                " ORDER BY " + COLUMN_FECHA + " DESC LIMIT " + limite;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Carga carga = cursorToCarga(cursor);
                cargas.add(carga);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cargas;
    }

    // Obtener cargas por ubicación
    public List<Carga> getCargasByUbicacion(String tipoUbicacion) {
        List<Carga> cargas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_CARGAS +
                " WHERE LOWER(" + COLUMN_UBICACION + ") LIKE ? " +
                " ORDER BY " + COLUMN_FECHA + " DESC";
        String[] selectionArgs = {"%" + tipoUbicacion.toLowerCase() + "%"};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                Carga carga = cursorToCarga(cursor);
                cargas.add(carga);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cargas;
    }

    // Obtener cargas por tipo (lenta/rápida)
    public List<Carga> getCargasByTipo(String tipoCarga) {
        List<Carga> cargas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_CARGAS;
        if (tipoCarga.equals("lenta")) {
            query += " WHERE " + COLUMN_DURACION + " > 60";
        } else {
            query += " WHERE " + COLUMN_DURACION + " <= 60";
        }
        query += " ORDER BY " + COLUMN_FECHA + " DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Carga carga = cursorToCarga(cursor);
                cargas.add(carga);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cargas;
    }

    // Eliminar carga
    public boolean deleteCarga(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_CARGAS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // Actualizar carga
    public boolean updateCarga(Carga carga) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_UBICACION, carga.getUbicacion());
        values.put(COLUMN_FECHA, dateFormat.format(carga.getFecha()));
        values.put(COLUMN_KWH, carga.getKwhCargados());
        values.put(COLUMN_DURACION, carga.getDuracionMinutos());
        values.put(COLUMN_COSTO, carga.getCosto());

        int result = db.update(TABLE_CARGAS, values, COLUMN_ID + "=?",
                new String[]{String.valueOf(carga.getId())});
        db.close();
        return result > 0;
    }

    // Convertir cursor a objeto Carga
    private Carga cursorToCarga(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String ubicacion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UBICACION));
        String fechaStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA));
        double kwh = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_KWH));
        int duracion = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURACION));
        double costo = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_COSTO));

        Date fecha = new Date();
        try {
            fecha = dateFormat.parse(fechaStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Carga(id, ubicacion, fecha, kwh, duracion, costo);
    }

    // Obtener estadísticas del mes actual
    public EstadisticasMes getEstadisticasMesActual() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Obtener primer y último día del mes actual
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        Date inicioMes = cal.getTime();

        cal.add(java.util.Calendar.MONTH, 1);
        cal.add(java.util.Calendar.DAY_OF_MONTH, -1);
        Date finMes = cal.getTime();

        String query = "SELECT COUNT(*) as total_cargas, " +
                "SUM(" + COLUMN_KWH + ") as total_kwh, " +
                "SUM(" + COLUMN_COSTO + ") as total_costo, " +
                "AVG(" + COLUMN_COSTO + ") as promedio_costo " +
                "FROM " + TABLE_CARGAS +
                " WHERE " + COLUMN_FECHA + " BETWEEN ? AND ?";

        String[] args = {dateFormat.format(inicioMes), dateFormat.format(finMes)};
        Cursor cursor = db.rawQuery(query, args);

        EstadisticasMes stats = new EstadisticasMes();
        if (cursor.moveToFirst()) {
            stats.totalCargas = cursor.getInt(0);
            stats.totalKwh = cursor.getDouble(1);
            stats.totalCosto = cursor.getDouble(2);
            stats.promedioCosto = cursor.getDouble(3);
        }

        cursor.close();
        db.close();
        return stats;
    }

    // Clase para estadísticas
    public static class EstadisticasMes {
        public int totalCargas = 0;
        public double totalKwh = 0.0;
        public double totalCosto = 0.0;
        public double promedioCosto = 0.0;
    }
}