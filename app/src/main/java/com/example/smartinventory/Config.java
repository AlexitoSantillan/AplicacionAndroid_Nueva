package com.example.smartinventory;

public class Config {
    // Reemplaza con la IP de tu servidor local (ej. 192.168.1.XX) si usas un dispositivo físico
    // O usa 10.0.2.2 si usas el emulador de Android Studio para conectar a localhost
    public static final String BASE_URL = "http://10.0.2.2/smartInventory/";
    
    public static final String LOGIN_URL = BASE_URL + "login.php";
    public static final String GET_PRODUCTS_URL = BASE_URL + "get_products.php";
    public static final String ADD_MOVEMENT_URL = BASE_URL + "add_movement.php";
}