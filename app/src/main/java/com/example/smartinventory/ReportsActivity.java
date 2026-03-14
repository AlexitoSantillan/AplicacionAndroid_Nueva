package com.example.smartinventory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class ReportsActivity extends AppCompatActivity {

    MaterialButton btnExportPDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        btnExportPDF = findViewById(R.id.btnExportPDF);
        btnExportPDF.setOnClickListener(v -> exportPDF());

        // Pedir permiso de almacenamiento
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);

        setupNavigation();
    }

    private void exportPDF() {
        try {
            File folder = new File(getExternalFilesDir(null), "SmartInventory");
            if (!folder.exists()) folder.mkdirs();

            File file = new File(folder, "reporte_semanal.pdf");

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));

            document.open();
            document.add(new Paragraph("REPORTE SEMANAL - SMART INVENTORY"));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Movimientos registrados:"));
            document.add(new Paragraph("Producto A - Entrada 10"));
            document.add(new Paragraph("Producto B - Salida 5"));
            document.add(new Paragraph("Producto C - Entrada 20"));
            document.close();

            Toast.makeText(this,
                    "PDF generado correctamente",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Error al generar PDF: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setupNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation != null) {
            // Selecciona el menú de reportes
            bottomNavigation.setSelectedItemId(R.id.nav_reports);

            bottomNavigation.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_inventory) {
                    startActivity(new Intent(this, InventoryActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_reports) {
                    return true; // Ya estamos en reportes
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class));
                    finish();
                    return true;
                }
                return false;
            });
        }
    }
}