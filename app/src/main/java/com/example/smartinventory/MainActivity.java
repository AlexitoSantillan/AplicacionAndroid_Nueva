package com.example.smartinventory;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalProducts, tvCriticalStock;
    private RecyclerView rvRecentMovements, rvQuickInventory;

    private ProductAdapter productAdapter;
    private MovementAdapter movementAdapter;

    private List<Product> quickProductList;
    private List<Movement> recentMovementList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvCriticalStock = findViewById(R.id.tvCriticalStock);
        rvRecentMovements = findViewById(R.id.rvRecentMovements);
        rvQuickInventory = findViewById(R.id.rvQuickInventory);

        setupRecyclerViews();
        loadDashboardData();
        setupNavigation();
    }

    private void setupRecyclerViews() {

        quickProductList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, quickProductList);
        rvQuickInventory.setLayoutManager(new LinearLayoutManager(this));
        rvQuickInventory.setAdapter(productAdapter);

        recentMovementList = new ArrayList<>();
        movementAdapter = new MovementAdapter(this, recentMovementList);
        rvRecentMovements.setLayoutManager(new LinearLayoutManager(this));
        rvRecentMovements.setAdapter(movementAdapter);
    }

    private void loadDashboardData() {

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Config.BASE_URL + "get_dashboard_full.php",
                null,

                response -> {
                    try {

                        // Estadísticas
                        JSONObject stats = response.getJSONObject("stats");

                        tvTotalProducts.setText(stats.getString("total"));
                        tvCriticalStock.setText(stats.getString("critical"));

                        // Movimientos recientes
                        recentMovementList.clear();
                        JSONArray movements = response.getJSONArray("movements");

                        for (int i = 0; i < movements.length(); i++) {

                            JSONObject m = movements.getJSONObject(i);

                            recentMovementList.add(new Movement(
                                    m.getInt("id"),
                                    m.getString("nombre"),
                                    m.getString("tipo"),
                                    m.getInt("cantidad"),
                                    m.getString("motivo"),
                                    m.getString("fecha")
                            ));
                        }

                        movementAdapter.notifyDataSetChanged();

                        // Inventario rápido
                        quickProductList.clear();
                        JSONArray inventory = response.getJSONArray("inventory");

                        for (int i = 0; i < inventory.length(); i++) {

                            JSONObject p = inventory.getJSONObject(i);

                            quickProductList.add(new Product(
                                    p.getInt("id"),
                                    p.getString("nombre"),
                                    p.getString("sku"),
                                    p.getInt("stock"),
                                    p.getString("categoria"),
                                    p.getString("ubicacion"),
                                    p.getDouble("precio"),
                                    p.optString("imagenUrl", "")
                            ));
                        }

                        productAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },

                error -> Toast.makeText(
                        this,
                        "Error al cargar dashboard",
                        Toast.LENGTH_SHORT
                ).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void setupNavigation() {

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        if (bottomNavigation != null) {

            bottomNavigation.setSelectedItemId(R.id.nav_home);

            bottomNavigation.setOnItemSelectedListener(item -> {

                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    return true;
                }

                else if (id == R.id.nav_inventory) {
                    startActivity(new Intent(this, InventoryActivity.class));
                    return true;
                }

                else if (id == R.id.nav_reports) {
                    startActivity(new Intent(this, ReportsActivity.class));
                    return true;
                }

                else if (id == R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class));
                    return true;
                }

                return false;
            });
        }

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        if (fabAdd != null) {

            fabAdd.setOnClickListener(v -> {

                startActivity(
                        new Intent(this, MovementRegistrationActivity.class)
                );

            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }
}