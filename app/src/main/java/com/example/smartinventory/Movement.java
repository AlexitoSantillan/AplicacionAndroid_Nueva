package com.example.smartinventory;

public class Movement {
    private int id;
    private String productoNombre;
    private String tipo;
    private int cantidad;
    private String motivo;
    private String fecha;

    public Movement(int id, String productoNombre, String tipo, int cantidad, String motivo, String fecha) {
        this.id = id;
        this.productoNombre = productoNombre;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.fecha = fecha;
    }

    public int getId() { return id; }
    public String getProductoNombre() { return productoNombre; }
    public String getTipo() { return tipo; }
    public int getCantidad() { return cantidad; }
    public String getMotivo() { return motivo; }
    public String getFecha() { return fecha; }
}