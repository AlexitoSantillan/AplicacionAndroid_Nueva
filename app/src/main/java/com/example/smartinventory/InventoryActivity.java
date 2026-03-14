package com.example.smartinventory;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {
    private RecyclerView rvInventory;
    private ProductAdapter adapter;
    private List<Product> fullProductList;
    private List<Product> filteredList;
    private TextInputEditText txtSearch;
    private ChipGroup chipGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        rvInventory = findViewById(R.id.rvInventory);
        txtSearch = findViewById(R.id.txtSearch);
        chipGroup = findViewById(R.id.chipGroup);

        fullProductList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new ProductAdapter(this, filteredList);
        
        rvInventory.setLayoutManager(new LinearLayoutManager(this));
        rvInventory.setAdapter(adapter);

        loadProducts();
        setupSearch();
        setupFilters();
        setupNavigation();
    }

    private void loadProducts() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Config.GET_PRODUCTS_URL, null,
                response -> {
                    fullProductList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            fullProductList.add(new Product(
                                    obj.getInt("id"),
                                    obj.getString("nombre"),
                                    obj.getString("sku"),
                                    obj.getInt("stock"),
                                    obj.getString("categoria"),
                                    obj.getString("ubicacion"),
                                    obj.getDouble("precio"),
                                    obj.optString("imagenUrl", "")
                            ));
                        } catch (JSONException e) { e.printStackTrace(); }
                    }
                    filter(""); // Mostrar todos al inicio
                },
                error -> Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void setupSearch() {
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilters() {
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            filter(txtSearch.getText().toString());
        });
    }

    private void filter(String query) {
        filteredList.clear();
        String lowerQuery = query.toLowerCase();
        
        boolean filterLowStock = chipGroup.getCheckedChipId() == R.id.chipStockLow;

        for (Product p : fullProductList) {
            boolean matchesQuery = p.getNombre().toLowerCase().contains(lowerQuery) || 
                                 p.getSku().toLowerCase().contains(lowerQuery);
            
            boolean matchesStock = !filterLowStock || p.getStock() < 5;

            if (matchesQuery && matchesStock) {
                filteredList.add(p);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void setupNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_inventory);
            bottomNavigation.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_inventory) {
                    return true;
                } else if (id == R.id.nav_reports) {
                    startActivity(new Intent(this, ReportsActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class));
                    finish();
                    return true;
                }
                return false;
            });
        }

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> {
                startActivity(new Intent(this, NewProductActivity.class));
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts(); // Refresh list when returning from NewProductActivity
    }
}