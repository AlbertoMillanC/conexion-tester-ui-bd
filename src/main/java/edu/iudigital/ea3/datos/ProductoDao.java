/**
 * ProductoDao.java
 */
package edu.iudigital.ea3.datos;

import edu.iudigital.ea3.modelo.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDao {

    private static final String LISTAR =
            "SELECT id, nombre, categoria, cantidad, precio FROM productos ORDER BY id";

    private static final String BUSCAR =
            "SELECT id, nombre, categoria, cantidad, precio FROM productos "
                    + "WHERE LOWER(nombre) LIKE ? OR LOWER(categoria) LIKE ? ORDER BY id";

    private static final String INSERTAR =
            "INSERT INTO productos (nombre, categoria, cantidad, precio) VALUES (?, ?, ?, ?)";

    private static final String ACTUALIZAR =
            "UPDATE productos SET nombre = ?, categoria = ?, cantidad = ?, precio = ? WHERE id = ?";

    private static final String ELIMINAR = "DELETE FROM productos WHERE id = ?";

    private final ProbadorConexion probador;
    private final ConfiguracionConexion configuracion;

    public ProductoDao(ProbadorConexion probador, ConfiguracionConexion configuracion) {
        this.probador = probador;
        this.configuracion = configuracion;
    }

    public List<Producto> listar() throws ErrorDeDatosException {
        List<Producto> productos = new ArrayList<>();
        try (Connection conexion = probador.abrir(configuracion);
             PreparedStatement sentencia = conexion.prepareStatement(LISTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                productos.add(construir(resultado));
            }
            return productos;

        } catch (SQLException e) {
            throw new ErrorDeDatosException("No fue posible listar los productos. " + e.getMessage(), e);
        }
    }

    public List<Producto> buscar(String texto) throws ErrorDeDatosException {
        if (texto == null || texto.isBlank()) {
            return listar();
        }
        String patron = "%" + texto.toLowerCase() + "%";
        List<Producto> productos = new ArrayList<>();
        try (Connection conexion = probador.abrir(configuracion);
             PreparedStatement sentencia = conexion.prepareStatement(BUSCAR)) {

            sentencia.setString(1, patron);
            sentencia.setString(2, patron);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    productos.add(construir(resultado));
                }
            }
            return productos;

        } catch (SQLException e) {
            throw new ErrorDeDatosException("No fue posible buscar productos. " + e.getMessage(), e);
        }
    }

    public Producto insertar(Producto producto) throws ErrorDeDatosException {
        try (Connection conexion = probador.abrir(configuracion);
             PreparedStatement sentencia = conexion.prepareStatement(INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            asignarCampos(sentencia, producto);
            sentencia.executeUpdate();

            try (ResultSet llaves = sentencia.getGeneratedKeys()) {
                int idGenerado = llaves.next() ? llaves.getInt(1) : 0;
                return new Producto(idGenerado, producto.nombre(), producto.categoria(),
                        producto.cantidad(), producto.precio());
            }

        } catch (SQLException e) {
            throw new ErrorDeDatosException("No fue posible insertar el producto. " + e.getMessage(), e);
        }
    }

    public int actualizar(Producto producto) throws ErrorDeDatosException {
        try (Connection conexion = probador.abrir(configuracion);
             PreparedStatement sentencia = conexion.prepareStatement(ACTUALIZAR)) {

            asignarCampos(sentencia, producto);
            sentencia.setInt(5, producto.id());
            return sentencia.executeUpdate();

        } catch (SQLException e) {
            throw new ErrorDeDatosException("No fue posible actualizar el producto. " + e.getMessage(), e);
        }
    }

    public int eliminar(int id) throws ErrorDeDatosException {
        try (Connection conexion = probador.abrir(configuracion);
             PreparedStatement sentencia = conexion.prepareStatement(ELIMINAR)) {

            sentencia.setInt(1, id);
            return sentencia.executeUpdate();

        } catch (SQLException e) {
            throw new ErrorDeDatosException("No fue posible eliminar el producto. " + e.getMessage(), e);
        }
    }

    private void asignarCampos(PreparedStatement sentencia, Producto producto) throws SQLException {
        sentencia.setString(1, producto.nombre());
        sentencia.setString(2, producto.categoria());
        sentencia.setInt(3, producto.cantidad());
        sentencia.setDouble(4, producto.precio());
    }

    private Producto construir(ResultSet resultado) throws SQLException {
        return new Producto(
                resultado.getInt("id"),
                resultado.getString("nombre"),
                resultado.getString("categoria"),
                resultado.getInt("cantidad"),
                resultado.getDouble("precio"));
    }
}
