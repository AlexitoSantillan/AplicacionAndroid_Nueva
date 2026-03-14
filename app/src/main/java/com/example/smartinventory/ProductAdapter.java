package com.example.smartinventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_product, parent, false);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        Product product = productList.get(position);

        holder.tvProductName.setText(product.getNombre());
        holder.tvProductCode.setText("SKU: " + product.getSku());
        holder.tvStockCount.setText(String.valueOf(product.getStock()));

        // Cargar imagen con Glide
        if (product.getImagenUrl() != null && !product.getImagenUrl().isEmpty()) {

            String fullImageUrl = Config.BASE_URL + "uploads/" + product.getImagenUrl();

            Glide.with(context)
                    .load(fullImageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.imgProduct);

        } else {

            holder.imgProduct.setImageResource(android.R.drawable.ic_menu_gallery);

        }

        // Estado del stock
        int statusColor;
        String statusLabel;

        if (product.getStock() <= 0) {

            statusColor = R.color.stock_critical;
            statusLabel = "Sin Stock";

        } else if (product.getStock() <= 10) {

            statusColor = R.color.stock_low;
            statusLabel = "Stock Bajo";

        } else {

            statusColor = R.color.stock_ok;
            statusLabel = "En Stock";

        }

        holder.statusIndicator.setCardBackgroundColor(
                ContextCompat.getColor(context, statusColor)
        );

        holder.tvStatusLabel.setText(statusLabel);
    }

    @Override
    public int getItemCount() {

        return productList.size();

    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductName;
        TextView tvProductCode;
        TextView tvStockCount;
        TextView tvStatusLabel;
        ImageView imgProduct;
        MaterialCardView statusIndicator;

        public ProductViewHolder(@NonNull View itemView) {

            super(itemView);

            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductCode = itemView.findViewById(R.id.tvProductCode);
            tvStockCount = itemView.findViewById(R.id.tvStockCount);
            tvStatusLabel = itemView.findViewById(R.id.tvStatusLabel);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);

        }
    }
}