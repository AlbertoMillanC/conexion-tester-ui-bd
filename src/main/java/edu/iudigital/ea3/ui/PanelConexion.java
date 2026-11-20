/**
 * PanelConexion.java
 */
package edu.iudigital.ea3.ui;

import edu.iudigital.ea3.datos.*;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class PanelConexion extends JPanel {

    private static final Color VERDE = new Color(0, 110, 60);
    private static final Color ROJO = new Color(170, 30, 30);

    private final JComboBox<String> comboMotor = new JComboBox<>(new String[]{
            "SQLite archivo local",
            "MySQL",
            "PostgreSQL",
            "H2 en memoria"
    });
    private final JTextField campoUrl = new JTextField("jdbc:sqlite:inventario.db");
    private final JTextField campoUsuario = new JTextField();
    private final JPasswordField campoContrasena = new JPasswordField();
    private final JButton botonProbar = new JButton("Probar conexion");
    private final JButton botonLimpiar = new JButton("Limpiar");
    private final JTextArea areaResultado = new JTextArea();

    private final ProbadorConexion probador = new ProbadorConexion();
    private final EsquemaInicializador inicializador = new EsquemaInicializador(probador);
    private final Consumer<ConfiguracionConexion> alConectar;

    public PanelConexion(Consumer<ConfiguracionConexion> alConectar) {
        this.alConectar = alConectar;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        add(construirFormulario(), BorderLayout.NORTH);
        add(construirResultado(), BorderLayout.CENTER);
        conectarEventos();
    }

    private JPanel construirFormulario() {
        JPanel formulario = new JPanel(new GridLayout(4, 2, 10, 10));
        formulario.setBorder(BorderFactory.createTitledBorder("Configuracion de la conexion"));
        formulario.add(new JLabel("Motor de base de datos"));
        formulario.add(comboMotor);
        formulario.add(new JLabel("URL de conexion JDBC"));
        formulario.add(campoUrl);
        formulario.add(new JLabel("Usuario"));
        formulario.add(campoUsuario);
        formulario.add(new JLabel("Contrasena"));
        formulario.add(campoContrasena);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        botones.add(botonProbar);
        botones.add(botonLimpiar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(formulario, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);
        return norte;
    }

    private JScrollPane construirResultado() {
        areaResultado.setEditable(false);
        areaResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        areaResultado.setText("Escriba la URL de conexion y presione Probar conexion.");
        JScrollPane panel = new JScrollPane(areaResultado);
        panel.setBorder(BorderFactory.createTitledBorder("Resultado de la prueba"));
        return panel;
    }

    private void conectarEventos() {
        comboMotor.addActionListener(e -> sugerirUrl());
        botonProbar.addActionListener(e -> probarConexion());
        botonLimpiar.addActionListener(e -> {
            campoUsuario.setText("");
            campoContrasena.setText("");
            areaResultado.setForeground(Color.DARK_GRAY);
            areaResultado.setText("Escriba la URL de conexion y presione Probar conexion.");
        });
    }

    private void sugerirUrl() {
        String seleccion = (String) comboMotor.getSelectedItem();
        String sugerida = switch (seleccion == null ? "" : seleccion) {
            case "MySQL" -> "jdbc:mysql://localhost:3306/inventario";
            case "PostgreSQL" -> "jdbc:postgresql://localhost:5432/inventario";
            case "H2 en memoria" -> "jdbc:h2:mem:inventario";
            default -> "jdbc:sqlite:inventario.db";
        };
        campoUrl.setText(sugerida);
    }

    private void probarConexion() {
        ConfiguracionConexion configuracion = new ConfiguracionConexion(
                campoUrl.getText().trim(),
                campoUsuario.getText().trim(),
                new String(campoContrasena.getPassword()));

        ResultadoPrueba resultado = probador.probar(configuracion);

        StringBuilder mensaje = new StringBuilder();
        mensaje.append(resultado.titulo())
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append(resultado.detalle());

        if (resultado.exitosa()) {
            areaResultado.setForeground(VERDE);
            try {
                String aviso = inicializador.preparar(configuracion);
                mensaje.append(System.lineSeparator())
                        .append(System.lineSeparator())
                        .append(aviso)
                        .append(System.lineSeparator())
                        .append("La pestana Inventario ya esta habilitada.");
                alConectar.accept(configuracion);
            } catch (ErrorDeDatosException e) {
                areaResultado.setForeground(ROJO);
                mensaje.append(System.lineSeparator())
                        .append(System.lineSeparator())
                        .append("La conexion funciona pero el esquema no pudo prepararse.")
                        .append(System.lineSeparator())
                        .append(e.getMessage());
            }
        } else {
            areaResultado.setForeground(ROJO);
        }

        areaResultado.setText(mensaje.toString());
        areaResultado.setCaretPosition(0);
    }
}
