/**
 * DialogoProducto.java
 */
package edu.iudigital.ea3.ui;

import edu.iudigital.ea3.modelo.Producto;

import javax.swing.*;
import java.awt.*;

public class DialogoProducto extends JDialog {

    private final JTextField campoNombre = new JTextField();
    private final JTextField campoCategoria = new JTextField();
    private final JTextField campoCantidad = new JTextField();
    private final JTextField campoPrecio = new JTextField();

    private final int idOriginal;
    private Producto resultado;

    public DialogoProducto(Frame padre, Producto inicial) {
        super(padre, inicial == null ? "Nuevo producto" : "Editar producto", true);
        this.idOriginal = inicial == null ? 0 : inicial.id();

        if (inicial != null) {
            campoNombre.setText(inicial.nombre());
            campoCategoria.setText(inicial.categoria());
            campoCantidad.setText(String.valueOf(inicial.cantidad()));
            campoPrecio.setText(String.valueOf(inicial.precio()));
        }

        JPanel formulario = new JPanel(new GridLayout(4, 2, 10, 10));
        formulario.setBorder(BorderFactory.createEmptyBorder(16, 16, 10, 16));
        formulario.add(new JLabel("Nombre", SwingConstants.RIGHT));
        formulario.add(campoNombre);
        formulario.add(new JLabel("Categoria", SwingConstants.RIGHT));
        formulario.add(campoCategoria);
        formulario.add(new JLabel("Cantidad", SwingConstants.RIGHT));
        formulario.add(campoCantidad);
        formulario.add(new JLabel("Precio", SwingConstants.RIGHT));
        formulario.add(campoPrecio);

        JButton botonGuardar = new JButton("Guardar");
        JButton botonCancelar = new JButton("Cancelar");
        botonGuardar.addActionListener(e -> validarYCerrar());
        botonCancelar.addActionListener(e -> dispose());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        botones.add(botonCancelar);
        botones.add(botonGuardar);

        setLayout(new BorderLayout());
        add(formulario, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(botonGuardar);
        setSize(420, 240);
        setLocationRelativeTo(padre);
    }

    public Producto getResultado() {
        return resultado;
    }

    private void validarYCerrar() {
        String nombre = campoNombre.getText().trim();
        String categoria = campoCategoria.getText().trim();

        if (nombre.isEmpty() || categoria.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El nombre y la categoria son obligatorios.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cantidad;
        double precio;
        try {
            cantidad = Integer.parseInt(campoCantidad.getText().trim());
            precio = Double.parseDouble(campoPrecio.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "La cantidad debe ser un numero entero y el precio un numero decimal.",
                    "Formato invalido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cantidad < 0 || precio < 0) {
            JOptionPane.showMessageDialog(this,
                    "La cantidad y el precio no pueden ser negativos.",
                    "Valores invalidos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        resultado = new Producto(idOriginal, nombre, categoria, cantidad, precio);
        dispose();
    }
}
