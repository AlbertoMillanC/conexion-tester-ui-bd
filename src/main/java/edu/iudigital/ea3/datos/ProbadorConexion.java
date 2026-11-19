/**
 * ProbadorConexion.java
 */
package edu.iudigital.ea3.datos;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ProbadorConexion {

    public ResultadoPrueba probar(ConfiguracionConexion configuracion) {
        if (configuracion == null || !configuracion.esValida()) {
            return ResultadoPrueba.fallo("DATOS INCOMPLETOS",
                    "La URL de conexion no puede quedar vacia. Escriba algo como jdbc:sqlite:inventario.db");
        }

        String driver = configuracion.nombreDelDriver();
        if (driver.isEmpty()) {
            return ResultadoPrueba.fallo("URL NO RECONOCIDA",
                    "La URL " + configuracion.url() + " no corresponde a ningun motor soportado por esta aplicacion. "
                            + "Motores disponibles: SQLite, MySQL, PostgreSQL y H2.");
        }

        try {
            Class.forName(driver); // busca y carga la clase del conector por su nombre, en tiempo de ejecucion.
        } catch (ClassNotFoundException e) {
            return ResultadoPrueba.fallo("DRIVER NO ENCONTRADO",
                    "No se encontro la clase " + driver + " en el classpath del proyecto. "
                            + "Agregue el conector del motor " + configuracion.motor()
                            + " en File, Project Structure, Libraries."
                            + System.lineSeparator()
                            + "Mensaje original: " + e.getMessage());
        }

        long inicio = System.currentTimeMillis();
        try (Connection conexion = abrir(configuracion)) {
            long duracion = System.currentTimeMillis() - inicio;
            DatabaseMetaData metadatos = conexion.getMetaData();
            String detalle = "Motor: " + metadatos.getDatabaseProductName()
                    + " " + metadatos.getDatabaseProductVersion()
                    + System.lineSeparator()
                    + "Driver: " + metadatos.getDriverName()
                    + " " + metadatos.getDriverVersion()
                    + System.lineSeparator()
                    + "URL: " + configuracion.url()
                    + System.lineSeparator()
                    + "Usuario: " + (configuracion.usuario().isBlank() ? "sin usuario" : configuracion.usuario())
                    + System.lineSeparator()
                    + "Tiempo de respuesta: " + duracion + " ms";
            return ResultadoPrueba.exito(detalle);
        } catch (SQLException e) {
            return ResultadoPrueba.fallo("ERROR DE CONEXION",
                    "No fue posible conectar con la base de datos."
                            + System.lineSeparator()
                            + "Mensaje: " + e.getMessage()
                            + System.lineSeparator()
                            + "Codigo de error: " + e.getErrorCode()
                            + System.lineSeparator()
                            + "Estado SQL: " + e.getSQLState());
        }
    }

    public Connection abrir(ConfiguracionConexion configuracion) throws SQLException {
        if (configuracion.usuario() == null || configuracion.usuario().isBlank()) {
            return DriverManager.getConnection(configuracion.url());
        }
        return DriverManager.getConnection(configuracion.url(), configuracion.usuario(), configuracion.contrasena());
    }


}
