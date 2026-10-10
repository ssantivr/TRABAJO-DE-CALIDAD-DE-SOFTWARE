package com.calculadora;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ventana de la aplicación. Usa FlatLaf cuando su jar está en el classpath (apariencia
 * plana moderna) y, si no, Nimbus; no hay dependencia en tiempo de compilación.
 */
@SuppressWarnings("serial")
public final class VentanaCalculadora extends JFrame {

    public VentanaCalculadora() {
        super("Calculadora de enteros");
        PanelCalculadora panel = new PanelCalculadora();
        setContentPane(panel);
        getRootPane().setDefaultButton(panel.botonCalcular);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setMinimumSize(new Dimension(getWidth(), getHeight()));
        setLocationRelativeTo(null);
    }

    /** Instala la apariencia visual y muestra la ventana en el hilo de Swing. */
    public static void iniciar() {
        SwingUtilities.invokeLater(() -> {
            instalarApariencia();
            new VentanaCalculadora().setVisible(true);
        });
    }

    private static void instalarApariencia() {
        String[] candidatas = {"com.formdev.flatlaf.FlatLightLaf", "javax.swing.plaf.nimbus.NimbusLookAndFeel"};
        for (String nombre : candidatas) {
            try {
                UIManager.setLookAndFeel(nombre);
                UIManager.put("Button.arc", 14);
                UIManager.put("Component.arc", 14);
                UIManager.put("TextComponent.arc", 14);
                return;
            } catch (Exception e) {
                // apariencia no disponible: se prueba la siguiente candidata
            }
        }
    }
}
