/**
 * EsquemaInicializador.java
 */
package edu.iudigital.ea3.datos;

import java.sql.*;

public class EsquemaInicializador {

    private static final String CREAR_TABLA = """
            CREATE TABLE IF NOT EXISTS productos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                categoria TEXT NOT NULL,
                cantidad INTEGER NOT NULL,
                precio REAL NOT NULL
            )
            """;

    private static final String CONTAR = "SELECT COUNT(*) FROM productos";

    private static final String INSERTAR =
            "INSERT INTO productos (nombre, categoria, cantidad, precio) VALUES (?, ?, ?, ?)";

    private static final String[][] SEMILLA = {
            {"Teclado mecanico", "Perifericos", "14", "189000"},
            {"Mouse inalambrico", "Perifericos", "32", "72000"},
            {"Monitor 24 pulgadas", "Pantallas", "8", "640000"},
            {"Diadema con microfono", "Audio", "21", "135000"},
            {"Disco solido 1 TB", "Almacenamiento", "11", "395000"},
            {"Memoria RAM 16 GB", "Componentes", "17", "280000"},
            {"Camara web HD", "Video", "6", "158000"},
            {"Base refrigerante", "Accesorios", "25", "89000"}
    };

    private final ProbadorConexion probador;

    public EsquemaInicializador(ProbadorConexion probador) {
        this.probador = probador;
    }

    public String preparar(ConfiguracionConexion configuracion) throws ErrorDeDatosException {
        try (Connection conexion = probador.abrir(configuracion);
             Statement sentencia = conexion.createStatement()) {

            sentencia.execute(CREAR_TABLA);

            int existentes = contarFilas(conexion);
            if (existentes > 0) {
                return "La tabla productos ya existia con " + existentes + " registros.";
            }

            insertarSemilla(conexion);
            return "Se creo la tabla productos y se cargaron " + SEMILLA.length + " registros de ejemplo.";

        } catch (SQLException e) {
            throw new ErrorDeDatosException("No fue posible preparar el esquema. " + e.getMessage(), e);
        }
    }

    private int contarFilas(Connection conexion) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(CONTAR);
             ResultSet resultado = sentencia.executeQuery()) {
            return resultado.next() ? resultado.getInt(1) : 0;
        }
    }

    private void insertarSemilla(Connection conexion) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(INSERTAR)) {
            for (String[] fila : SEMILLA) {
                sentencia.setString(1, fila[0]);
                sentencia.setString(2, fila[1]);
                sentencia.setInt(3, Integer.parseInt(fila[2]));
                sentencia.setDouble(4, Double.parseDouble(fila[3]));
                sentencia.addBatch();
            }
            sentencia.executeBatch();
        }
    }
}
