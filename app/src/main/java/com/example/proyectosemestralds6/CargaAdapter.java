package com.example.proyectosemestralds6;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CargaAdapter extends RecyclerView.Adapter<CargaAdapter.CargaViewHolder> {

    private List<com.example.proyectosemestralds6.Carga> cargas;
    private boolean mostrarFechaCompleta;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public CargaAdapter(List<com.example.proyectosemestralds6.Carga> cargas, boolean mostrarFechaCompleta) {
        this.cargas = cargas;
        this.mostrarFechaCompleta = mostrarFechaCompleta;
    }

    @NonNull
    @Override
    public CargaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_carga, parent, false);
        return new CargaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CargaViewHolder holder, int position) {
        com.example.proyectosemestralds6.Carga carga = cargas.get(position);

        // Configurar icono según ubicación
        if (carga.esCargaEnCasa()) {
            holder.iconoCarga.setImageResource(R.drawable.ic_home);
        } else {
            holder.iconoCarga.setImageResource(R.drawable.ic_charging_station);
        }

        // Título (ubicación y tipo)
        holder.txtTitulo.setText(carga.getUbicacion());

        // Fecha y hora
        if (mostrarFechaCompleta) {
            holder.txtFecha.setText(dateFormat.format(carga.getFecha()) + " • " +
                    timeFormat.format(carga.getFecha()));
        } else {
            holder.txtFecha.setText(dateFormat.format(carga.getFecha()));
        }

        // Información de carga
        String infoCarga = decimalFormat.format(carga.getKwhCargados()) + " kWh • " +
                carga.getDuracionFormateada();
        holder.txtInfoCarga.setText(infoCarga);

        // Costo
        holder.txtCosto.setText("$" + decimalFormat.format(carga.getCosto()));

        // Color del fondo según tipo de carga
        if (carga.esCargaLenta()) {
            holder.itemView.setBackgroundResource(R.drawable.background_carga_lenta);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.background_carga_rapida);
        }
    }

    @Override
    public int getItemCount() {
        return cargas.size();
    }

    public void updateCargas(List<com.example.proyectosemestralds6.Carga> nuevasCargas) {
        this.cargas = nuevasCargas;
        notifyDataSetChanged();
    }

    static class CargaViewHolder extends RecyclerView.ViewHolder {
        ImageView iconoCarga;
        TextView txtTitulo;
        TextView txtFecha;
        TextView txtInfoCarga;
        TextView txtCosto;

        public CargaViewHolder(@NonNull View itemView) {
            super(itemView);
            iconoCarga = itemView.findViewById(R.id.icono_carga);
            txtTitulo = itemView.findViewById(R.id.txt_titulo_carga);
            txtFecha = itemView.findViewById(R.id.txt_fecha_carga);
            txtInfoCarga = itemView.findViewById(R.id.txt_info_carga);
            txtCosto = itemView.findViewById(R.id.txt_costo_carga);
        }
    }
}