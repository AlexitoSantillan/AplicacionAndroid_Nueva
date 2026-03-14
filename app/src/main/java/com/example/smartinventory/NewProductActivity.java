package com.example.smartinventory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NewProductActivity extends AppCompatActivity {

    private TextInputEditText txtName, txtSku, txtPrice, txtStock, txtLocation;
    private AutoCompleteTextView txtCategory;
    private ImageView imgProductPreview;
    private MaterialCardView cardPhoto;
    private MaterialButton btnSave, btnLocalGallery, btnServerGallery;
    private Bitmap bitmap;
    private String selectedServerImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        txtName = findViewById(R.id.txtName);
        txtSku = findViewById(R.id.txtSku);
        txtPrice = findViewById(R.id.txtPrice);
        txtStock = findViewById(R.id.txtStock);
        txtLocation = findViewById(R.id.txtLocation);
        txtCategory = findViewById(R.id.txtCategory);
        imgProductPreview = findViewById(R.id.imgProductPreview);
        cardPhoto = findViewById(R.id.cardPhoto);
        btnSave = findViewById(R.id.btnSave);
        btnLocalGallery = findViewById(R.id.btnLocalGallery);
        btnServerGallery = findViewById(R.id.btnServerGallery);

        String[] categories = {"Electrónica", "Accesorios", "Herramientas", "Oficina", "Otros"};
        txtCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories));

        ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            imgProductPreview.setImageBitmap(bitmap);
                            imgProductPreview.setPadding(0, 0, 0, 0);
                            selectedServerImage = ""; // Reset server selection
                        } catch (IOException e) { e.printStackTrace(); }
                    }
                }
        );

        if (btnLocalGallery != null) {
            btnLocalGallery.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryLauncher.launch(intent);
            });
        }

        if (btnServerGallery != null) {
            btnServerGallery.setOnClickListener(v -> openServerGallery());
        }

        // Mantener compatibilidad con el click en el cuadro de foto
        cardPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void openServerGallery() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Config.BASE_URL + "list_images.php", null,
                response -> {
                    String[] images = new String[response.length()];
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            images[i] = response.getString(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    new AlertDialog.Builder(this)
                            .setTitle("Seleccionar foto del servidor")
                            .setItems(images, (dialog, which) -> {
                                selectedServerImage = images[which];
                                bitmap = null; // Reset local selection
                                Glide.with(this)
                                        .load(Config.BASE_URL + "uploads/" + selectedServerImage)
                                        .into(imgProductPreview);
                                imgProductPreview.setPadding(0, 0, 0, 0);
                            }).show();
                },
                error -> Toast.makeText(this, "Error al conectar con la carpeta uploads", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void saveProduct() {
        String name = txtName.getText().toString().trim();
        String sku = txtSku.getText().toString().trim();
        String price = txtPrice.getText().toString().trim();
        String stock = txtStock.getText().toString().trim();
        String category = txtCategory.getText().toString().trim();
        String location = txtLocation.getText().toString().trim();

        if (name.isEmpty() || sku.isEmpty()) {
            Toast.makeText(this, "Nombre y SKU son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.BASE_URL + "add_product.php",
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            Toast.makeText(this, "Producto guardado con éxito", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Error: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) { e.printStackTrace(); }
                },
                error -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", name);
                params.put("sku", sku);
                params.put("precio", price);
                params.put("stock", stock);
                params.put("categoria", category);
                params.put("ubicacion", location);
                
                if (bitmap != null) {
                    params.put("imagen", getStringImage(bitmap));
                } else if (!selectedServerImage.isEmpty()) {
                    params.put("imagen_servidor", selectedServerImage);
                }
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}