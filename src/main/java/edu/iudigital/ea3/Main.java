package edu.iudigital.ea3;

import edu.iudigital.ea3.ui.VentanaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Se usa la apariencia por defecto de un JSwing");
        }
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}