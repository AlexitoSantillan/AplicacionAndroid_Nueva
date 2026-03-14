package com.example.smartinventory;

public class Product {
    private int id;
    private String nombre;
    private String sku;
    private int stock;
    private String categoria;
    private String ubicacion;
    private double precio;
    private String imagenUrl;

    public Product(int id, String nombre, String sku, int stock, String categoria, String ubicacion, double precio, String imagenUrl) {
        this.id = id;
        this.nombre = nombre;
        this.sku = sku;
        this.stock = stock;
        this.categoria = categoria;
        this.ubicacion = ubicacion;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getSku() { return sku; }
    public int getStock() { return stock; }
    public String getCategoria() { return categoria; }
    public String getUbicacion() { return ubicacion; }
    public double getPrecio() { return precio; }
    public String getImagenUrl() { return imagenUrl; }
}