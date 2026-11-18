package edu.iudigital.ea3.datos;

public record ResultadoPrueba(boolean exitosa, String titulo, String detalle) {

    public static ResultadoPrueba exito(String detalle) {
        return new ResultadoPrueba(true, "CONEXION EXITOSA", detalle);
    }

    public static ResultadoPrueba fallo(String titulo, String detalle) {
        return new ResultadoPrueba(false, titulo, detalle);
    }
}
