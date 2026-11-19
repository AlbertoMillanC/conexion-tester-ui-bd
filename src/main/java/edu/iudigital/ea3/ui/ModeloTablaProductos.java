/**
 * ModeloTablaProductos.java
 */
package edu.iudigital.ea3.ui;

import edu.iudigital.ea3.modelo.Producto;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ModeloTablaProductos extends AbstractTableModel {

    private static final String[] COLUMNAS = {"ID", "Nombre", "Categoria", "Cantidad", "Precio", "Valor en bodega"};

    private final List<Producto> productos = new ArrayList<>();

    public void reemplazar(List<Producto> nuevos) {
        productos.clear();
        productos.addAll(nuevos);
        fireTableDataChanged();
    }

    public Producto obtener(int fila) {
        return productos.get(fila);
    }

    public List<Producto> obtenerTodos() {
        return List.copyOf(productos);
    }

    public double valorTotal() {
        double total = 0;
        for (Producto producto : productos) {
            total += producto.valorEnBodega();
        }
        return total;
    }

    @Override
    public int getRowCount() {
        return productos.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNAS.length;
    }

    @Override
    public String getColumnName(int columna) {
        return COLUMNAS[columna];
    }

    @Override
    public Class<?> getColumnClass(int columna) {
        return switch (columna) {
            case 0, 3 -> Integer.class;
            case 4, 5 -> Double.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        Producto producto = productos.get(fila);
        return switch (columna) {
            case 0 -> producto.id();
            case 1 -> producto.nombre();
            case 2 -> producto.categoria();
            case 3 -> producto.cantidad();
            case 4 -> producto.precio();
            case 5 -> producto.valorEnBodega();
            default -> "";
        };
    }
}
