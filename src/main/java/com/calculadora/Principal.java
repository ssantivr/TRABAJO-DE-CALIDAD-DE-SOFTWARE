package com.calculadora;

import java.util.Scanner;

/** Punto de entrada: toda la lectura y escritura se hace por consola (Scanner / System.out). */
public final class Principal {

    private Principal() {
    }

    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in)) {
            InterfazConsola.ejecutar(entrada, System.out);
        }
    }
}
