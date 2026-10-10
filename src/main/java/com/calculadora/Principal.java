package com.calculadora;

import java.awt.GraphicsEnvironment;
import java.util.Scanner;

/**
 * Punto de entrada de la aplicación.
 *
 * <pre>
 * java -jar calculadora.jar             consola (Scanner / System.out)
 * java -jar calculadora.jar --consola   consola
 * java -jar calculadora.jar --gui       ventana Swing (consola si no hay pantalla)
 * </pre>
 */
public final class Principal {

    private Principal() {
    }

    public static void main(String[] args) {
        String modo = args.length > 0 ? args[0] : "--consola";
        if (!modo.equals("--gui") && !modo.equals("--consola")) {
            System.out.println("Uso: java -jar calculadora.jar [--consola | --gui]");
            return;
        }
        boolean consola = modo.equals("--consola");
        if (!consola && GraphicsEnvironment.isHeadless()) {
            System.out.println("No hay pantalla disponible: se usará la consola.");
            consola = true;
        }
        if (consola) {
            try (Scanner entrada = new Scanner(System.in)) {
                InterfazConsola.ejecutar(entrada, System.out);
            }
        } else {
            VentanaCalculadora.iniciar();
        }
    }
}
