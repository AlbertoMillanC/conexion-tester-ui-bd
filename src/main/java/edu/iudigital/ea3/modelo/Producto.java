package edu.iudigital.ea3.modelo;

public record Producto(int id, String nombre, String categoria, int cantidad, double precio) {

    public static Producto nuevo(String nombre, String categoria, int cantidad, double precio) {
        return new Producto(0, nombre, categoria, cantidad, precio);
    }

    public double valorEnBodega() {
        return cantidad * precio;
    }

    public boolean esNuevo() {
        return id == 0;
    }

}
