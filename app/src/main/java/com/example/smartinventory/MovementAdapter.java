package com.example.smartinventory;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class MovementAdapter extends RecyclerView.Adapter<MovementAdapter.MovementViewHolder> {

    private Context context;
    private List<Movement> movementList;

    public MovementAdapter(Context context, List<Movement> movementList) {
        this.context = context;
        this.movementList = movementList;
    }

    @NonNull
    @Override
    public MovementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_movement, parent, false);
        return new MovementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovementViewHolder holder, int position) {
        Movement movement = movementList.get(position);
        
        String tipo = movement.getTipo().toLowerCase();
        holder.tvMovementTitle.setText(tipo.toUpperCase());
        holder.tvMovementDetail.setText(movement.getProductoNombre() + " (" + movement.getMotivo() + ")");
        
        // Configurar cantidad con color y signo
        if (tipo.equals("entrada")) {
            holder.tvTime.setText("+" + movement.getCantidad() + " un.");
            holder.tvTime.setTextColor(context.getColor(R.color.stock_ok));
            holder.imgType.setImageResource(android.R.drawable.ic_input_add);
            holder.imgType.setColorFilter(context.getColor(R.color.stock_ok));
            holder.cardIcon.setCardBackgroundColor(Color.parseColor("#F0FDF4")); // Verde muy claro
        } else {
            holder.tvTime.setText("-" + movement.getCantidad() + " un.");
            holder.tvTime.setTextColor(context.getColor(R.color.stock_critical));
            holder.imgType.setImageResource(android.R.drawable.ic_delete);
            holder.imgType.setColorFilter(context.getColor(R.color.stock_critical));
            holder.cardIcon.setCardBackgroundColor(Color.parseColor("#FEF2F2")); // Rojo muy claro
        }
        
        // Añadir fecha debajo del detalle o en un campo pequeño si lo deseas
        // Por ahora usamos tvTime para la cantidad destacada por visibilidad en reportes
    }

    @Override
    public int getItemCount() {
        return movementList.size();
    }

    public static class MovementViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovementTitle, tvMovementDetail, tvTime;
        ImageView imgType;
        MaterialCardView cardIcon;

        public MovementViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovementTitle = itemView.findViewById(R.id.tvMovementTitle);
            tvMovementDetail = itemView.findViewById(R.id.tvMovementDetail);
            tvTime = itemView.findViewById(R.id.tvTime);
            imgType = itemView.findViewById(R.id.imgType);
            cardIcon = itemView.findViewById(R.id.cardIcon);
        }
    }
}