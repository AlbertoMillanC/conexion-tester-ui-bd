/**
 * VentanaPrincipal.java
 */
package edu.iudigital.ea3.ui;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame{

    private final JTabbedPane pestanas = new JTabbedPane();
    private final PanelInventario panelInventario = new PanelInventario();

    public VentanaPrincipal() {
        super("Inventario IU Digital. Conexion a base de datos y gestion de productos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        PanelConexion panelConexion = new PanelConexion(configuracion -> {
            panelInventario.activar(configuracion);
            pestanas.setEnabledAt(1, true);
            pestanas.setSelectedIndex(1);
        });

        pestanas.addTab("Conexion", panelConexion);
        pestanas.addTab("Inventario", panelInventario);
        pestanas.setEnabledAt(1, false);

        add(pestanas, BorderLayout.CENTER);
        setSize(960, 620);
        setLocationRelativeTo(null);
    }
}
