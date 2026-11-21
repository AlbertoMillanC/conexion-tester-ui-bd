/**
 * PanelInventario.java
 */
package edu.iudigital.ea3.ui;

import edu.iudigital.ea3.datos.ConfiguracionConexion;
import edu.iudigital.ea3.datos.ErrorDeDatosException;
import edu.iudigital.ea3.datos.ProbadorConexion;
import edu.iudigital.ea3.datos.ProductoDao;
import edu.iudigital.ea3.modelo.Producto;
import edu.iudigital.ea3.util.ExportadorCsv;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PanelInventario extends JPanel {

    private final ModeloTablaProductos modelo = new ModeloTablaProductos();
    private final JTable tabla = new JTable(modelo);
    private final JTextField campoBusqueda = new JTextField(18);
    private final JLabel etiquetaEstado = new JLabel("Sin conexion activa", SwingConstants.LEFT);
    private final JButton botonCrear = new JButton("Crear");
    private final JButton botonEditar = new JButton("Editar");
    private final JButton botonEliminar = new JButton("Eliminar");
    private final JButton botonRecargar = new JButton("Recargar");
    private final JButton botonExportar = new JButton("Exportar csv");

    private final ProbadorConexion probador = new ProbadorConexion();
    private final ExportadorCsv exportador = new ExportadorCsv();

    private ProductoDao dao;

    public PanelInventario() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        add(construirBarraSuperior(), BorderLayout.NORTH);
        add(construirTabla(), BorderLayout.CENTER);
        add(construirBarraInferior(), BorderLayout.SOUTH);
        conectarEventos();
        habilitarAcciones(false);
    }

    public void activar(ConfiguracionConexion configuracion) {
        this.dao = new ProductoDao(probador, configuracion);
        habilitarAcciones(true);
        recargar();
    }

    private JPanel construirBarraSuperior() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        barra.add(new JLabel("Buscar por nombre o categoria"));
        barra.add(campoBusqueda);
        barra.add(botonRecargar);
        barra.add(botonCrear);
        barra.add(botonEditar);
        barra.add(botonEliminar);
        barra.add(botonExportar);
        return barra;
    }

    private JScrollPane construirTabla() {
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.setAutoCreateRowSorter(false);
        tabla.setRowSorter(new TableRowSorter<>(modelo));
        tabla.getTableHeader().setFont(tabla.getTableHeader().getFont().deriveFont(Font.BOLD));
        JScrollPane panel = new JScrollPane(tabla);
        panel.setBorder(BorderFactory.createTitledBorder("Tabla productos"));
        return panel;
    }

    private JPanel construirBarraInferior() {
        etiquetaEstado.setFont(etiquetaEstado.getFont().deriveFont(Font.BOLD, 14f));
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBorder(BorderFactory.createEmptyBorder(6, 4, 0, 4));
        barra.add(etiquetaEstado, BorderLayout.WEST);
        return barra;
    }

    private void conectarEventos() {
        botonRecargar.addActionListener(e -> recargar());
        botonCrear.addActionListener(e -> crear());
        botonEditar.addActionListener(e -> editar());
        botonEliminar.addActionListener(e -> eliminar());
        botonExportar.addActionListener(e -> exportar());
        campoBusqueda.addActionListener(e -> recargar());
    }

    private void habilitarAcciones(boolean estado) {
        botonCrear.setEnabled(estado);
        botonEditar.setEnabled(estado);
        botonEliminar.setEnabled(estado);
        botonRecargar.setEnabled(estado);
        botonExportar.setEnabled(estado);
        campoBusqueda.setEnabled(estado);
    }

    private void recargar() {
        if (dao == null) {
            return;
        }
        try {
            List<Producto> productos = dao.buscar(campoBusqueda.getText().trim());
            modelo.reemplazar(productos);
            actualizarEstado();
        } catch (ErrorDeDatosException e) {
            informarError(e);
        }
    }

    private void crear() {
        DialogoProducto dialogo = new DialogoProducto(ventana(), null);
        dialogo.setVisible(true);
        Producto nuevo = dialogo.getResultado();
        if (nuevo == null) {
            return;
        }
        try {
            Producto guardado = dao.insertar(nuevo);
            recargar();
            etiquetaEstado.setText("Producto creado con id " + guardado.id()
                    + ". " + resumen());
        } catch (ErrorDeDatosException e) {
            informarError(e);
        }
    }

    private void editar() {
        Producto seleccionado = seleccionActual();
        if (seleccionado == null) {
            return;
        }
        DialogoProducto dialogo = new DialogoProducto(ventana(), seleccionado);
        dialogo.setVisible(true);
        Producto modificado = dialogo.getResultado();
        if (modificado == null) {
            return;
        }
        try {
            int filas = dao.actualizar(modificado);
            recargar();
            etiquetaEstado.setText("Se actualizaron " + filas + " filas. " + resumen());
        } catch (ErrorDeDatosException e) {
            informarError(e);
        }
    }

    private void eliminar() {
        Producto seleccionado = seleccionActual();
        if (seleccionado == null) {
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "Confirma eliminar el producto " + seleccionado.nombre() + " con id " + seleccionado.id(),
                "Eliminar producto", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            int filas = dao.eliminar(seleccionado.id());
            recargar();
            etiquetaEstado.setText("Se eliminaron " + filas + " filas. " + resumen());
        } catch (ErrorDeDatosException e) {
            informarError(e);
        }
    }

    private void exportar() {
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new File("inventario.csv"));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path destino = selector.getSelectedFile().toPath();
        try {
            int filas = exportador.exportar(modelo.obtenerTodos(), destino);
            JOptionPane.showMessageDialog(this,
                    "Se exportaron " + filas + " registros a " + destino,
                    "Exportacion completa", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "No fue posible escribir el archivo. " + e.getMessage(),
                    "Error de escritura", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Producto seleccionActual() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione primero una fila de la tabla.",
                    "Sin seleccion", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return modelo.obtener(tabla.convertRowIndexToModel(fila));
    }

    private void actualizarEstado() {
        etiquetaEstado.setText(resumen());
    }

    private String resumen() {
        NumberFormat formato = NumberFormat.getCurrencyInstance(Locale.of("es", "CO"));
        return "Registros visibles " + modelo.getRowCount()
                + ". Valor total en bodega " + formato.format(modelo.valorTotal());
    }

    private void informarError(ErrorDeDatosException e) {
        etiquetaEstado.setText("Ocurrio un error con la base de datos.");
        JOptionPane.showMessageDialog(this, e.getMessage(),
                "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }

    private java.awt.Frame ventana() {
        return (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this);
    }
}
