package com.calculadora;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * Contenido de la GUI: dos campos de enteros, un selector de operación (botones de
 * alternancia), botones Calcular/Limpiar y un área de resultado.
 * Está separado de {@link VentanaCalculadora} para poder probarlo sin pantalla.
 */
@SuppressWarnings("serial")
final class PanelCalculadora extends JPanel {

    private static final Color FONDO = new Color(0xF3F5F9);
    private static final Color ACENTO = new Color(0x2D6CDF);
    private static final Color INACTIVO = new Color(0xE8ECF3);
    private static final Color TEXTO = new Color(0x1F2937);
    private static final Color ATENUADO = new Color(0x5F6B7A);
    private static final Color CORRECTO = new Color(0x1B7F4B);
    private static final Color ERROR = new Color(0xC62828);

    private final Calculadora calculadora = new Calculadora();
    private Operacion seleccionada = Operacion.SUMAR;

    final JTextField campoPrimero = new JTextField(12);
    final JTextField campoSegundo = new JTextField(12);
    final Map<Operacion, JToggleButton> botonesOperacion = new EnumMap<>(Operacion.class);
    final JTextArea areaResultado = new JTextArea(3, 30);
    final JButton botonCalcular = new JButton("Calcular");
    final JButton botonLimpiar = new JButton("Limpiar");

    PanelCalculadora() {
        super(new BorderLayout());
        setBackground(FONDO);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDDE2EA)), new EmptyBorder(24, 28, 24, 28)));

        tarjeta.add(aLaIzquierda(conEstilo(new JLabel("Calculadora de enteros"), Font.BOLD, 22f, TEXTO)));
        tarjeta.add(aLaIzquierda(conEstilo(new JLabel("Ingrese dos números enteros y elija una operación"),
                Font.PLAIN, 13f, ATENUADO)));
        tarjeta.add(Box.createVerticalStrut(18));
        tarjeta.add(aLaIzquierda(construirEntradas()));
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(aLaIzquierda(conEstilo(new JLabel("Operación"), Font.BOLD, 13f, TEXTO)));
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(aLaIzquierda(construirOperaciones()));
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(aLaIzquierda(construirBotones()));
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(aLaIzquierda(construirResultado()));
        add(tarjeta, BorderLayout.CENTER);

        campoPrimero.addActionListener(e -> calcular());
        campoSegundo.addActionListener(e -> calcular());
        botonCalcular.addActionListener(e -> calcular());
        botonLimpiar.addActionListener(e -> limpiar());
        actualizarBotonesOperacion();
    }

    /** Ejecuta la operación seleccionada con los valores escritos y muestra el resultado. */
    void calcular() {
        Resultado resultado = Resultado.deTexto(calculadora, seleccionada, campoPrimero.getText(), campoSegundo.getText());
        areaResultado.setText(resultado.mensaje());
        areaResultado.setForeground(resultado.exito() ? CORRECTO : ERROR);
    }

    void limpiar() {
        campoPrimero.setText("");
        campoSegundo.setText("");
        areaResultado.setText("");
        campoPrimero.requestFocusInWindow();
    }

    private JPanel construirEntradas() {
        JPanel fila = new JPanel(new GridLayout(1, 2, 16, 0));
        fila.setOpaque(false);
        fila.add(conEtiqueta("Primer número (a)", campoPrimero));
        fila.add(conEtiqueta("Segundo número (b)", campoSegundo));
        return fila;
    }

    private JPanel conEtiqueta(String texto, JTextField campo) {
        JPanel caja = new JPanel(new BorderLayout(0, 4));
        caja.setOpaque(false);
        caja.add(conEstilo(new JLabel(texto), Font.PLAIN, 12f, ATENUADO), BorderLayout.NORTH);
        campo.setFont(campo.getFont().deriveFont(15f));
        campo.putClientProperty("JTextField.placeholderText", "Ej. 12");
        caja.add(campo, BorderLayout.CENTER);
        return caja;
    }

    private JPanel construirOperaciones() {
        JPanel fila = new JPanel(new GridLayout(1, Operacion.values().length, 8, 0));
        fila.setOpaque(false);
        ButtonGroup grupo = new ButtonGroup();
        for (Operacion op : Operacion.values()) {
            JToggleButton boton = new JToggleButton("<html><div style='text-align:center'>"
                    + "<span style='font-size:16px'><b>" + op.getSimbolo() + "</b></span><br>"
                    + op.getEtiqueta() + "</div></html>");
            boton.setFocusPainted(false);
            boton.setToolTipText(op.getEtiqueta());
            boton.putClientProperty("JButton.buttonType", "roundRect");
            boton.addActionListener(e -> {
                seleccionada = op;
                actualizarBotonesOperacion();
            });
            grupo.add(boton);
            botonesOperacion.put(op, boton);
            fila.add(boton);
        }
        return fila;
    }

    private void actualizarBotonesOperacion() {
        botonesOperacion.forEach((op, boton) -> {
            boolean activo = op == seleccionada;
            boton.setSelected(activo);
            boton.setBackground(activo ? ACENTO : INACTIVO);
            boton.setForeground(activo ? Color.WHITE : TEXTO);
        });
    }

    private JPanel construirBotones() {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        fila.setOpaque(false);
        if (UIManager.getLookAndFeel().getClass().getName().startsWith("com.formdev")) {
            botonCalcular.setBackground(ACENTO);
            botonCalcular.setForeground(Color.WHITE);
        }
        botonCalcular.setFont(botonCalcular.getFont().deriveFont(Font.BOLD));
        botonCalcular.setFocusPainted(false);
        botonCalcular.putClientProperty("JButton.buttonType", "roundRect");
        botonLimpiar.setFocusPainted(false);
        botonLimpiar.putClientProperty("JButton.buttonType", "roundRect");
        fila.add(botonLimpiar);
        fila.add(botonCalcular);
        return fila;
    }

    private JPanel construirResultado() {
        JPanel caja = new JPanel(new BorderLayout());
        caja.setBackground(FONDO);
        caja.setBorder(new EmptyBorder(12, 14, 12, 14));
        areaResultado.setBorder(null);
        areaResultado.setEditable(false);
        areaResultado.setFocusable(false);
        areaResultado.setLineWrap(true);
        areaResultado.setWrapStyleWord(true);
        areaResultado.setBackground(FONDO);
        areaResultado.setOpaque(true);
        areaResultado.setFont(areaResultado.getFont().deriveFont(Font.BOLD, 15f));
        caja.add(areaResultado, BorderLayout.CENTER);
        return caja;
    }

    private static JLabel conEstilo(JLabel etiqueta, int estilo, float tamano, Color color) {
        etiqueta.setFont(etiqueta.getFont().deriveFont(estilo, tamano));
        etiqueta.setForeground(color);
        return etiqueta;
    }

    private static <T extends JComponent> T aLaIzquierda(T componente) {
        componente.setAlignmentX(LEFT_ALIGNMENT);
        return componente;
    }
}
