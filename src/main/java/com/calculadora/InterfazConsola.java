package com.calculadora;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Interfaz de consola (Scanner). {@link #ejecutar} es visible en el paquete y recibe
 * los flujos para poder probarla con entrada simulada.
 */
final class InterfazConsola {

    private static final Operacion[] OPERACIONES = Operacion.values();

    private InterfazConsola() {
    }

    static void ejecutar(Scanner entrada, PrintStream salida) {
        Calculadora calculadora = new Calculadora();
        salida.println("=== Calculadora de números enteros ===");
        while (true) {
            imprimirMenu(salida);
            Integer opcion = leerEntero(entrada, salida, "Seleccione una opción: ");
            if (opcion == null || opcion == 0) {
                salida.println("¡Hasta pronto!");
                return;
            }
            if (opcion < 1 || opcion > OPERACIONES.length) {
                salida.println("Opción no válida. Elija un número entre 0 y " + OPERACIONES.length + ".");
                continue;
            }
            Integer a = leerEntero(entrada, salida, "Ingrese el primer número (a): ");
            Integer b = (a == null) ? null : leerEntero(entrada, salida, "Ingrese el segundo número (b): ");
            if (a == null || b == null) {
                salida.println("¡Hasta pronto!");
                return;
            }
            salida.println(Resultado.de(calculadora, OPERACIONES[opcion - 1], a, b).mensaje());
        }
    }

    private static void imprimirMenu(PrintStream salida) {
        salida.println();
        for (int i = 0; i < OPERACIONES.length; i++) {
            salida.println((i + 1) + ". " + OPERACIONES[i].getEtiqueta());
        }
        salida.println("0. Salir");
    }

    /** Lee un entero de una línea; vuelve a pedirlo si es inválido; devuelve null al terminar la entrada. */
    private static Integer leerEntero(Scanner entrada, PrintStream salida, String mensaje) {
        while (true) {
            salida.print(mensaje);
            if (!entrada.hasNextLine()) {
                salida.println();
                return null;
            }
            try {
                return Integer.valueOf(entrada.nextLine().trim());
            } catch (NumberFormatException e) {
                salida.println(Resultado.ENTRADA_INVALIDA);
            }
        }
    }
}
