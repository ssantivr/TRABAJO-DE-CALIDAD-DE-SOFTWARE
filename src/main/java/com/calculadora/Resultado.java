package com.calculadora;

/** Resultado de ejecutar una operación, ya redactado para mostrarlo al usuario en la consola. */
public record Resultado(boolean exito, String mensaje) {

    public static final String ENTRADA_INVALIDA = "Entrada inválida: ingrese un número entero.";

    /** Ejecuta la operación y convierte cada fallo esperado en un mensaje de error. */
    public static Resultado de(Calculadora calculadora, Operacion operacion, int a, int b) {
        try {
            int valor = operacion.aplicar(calculadora, a, b);
            return new Resultado(true, "Resultado: " + a + " " + operacion.getSimbolo() + " " + b + " = " + valor);
        } catch (ArithmeticException e) {
            return new Resultado(false, Calculadora.DIVISION_POR_CERO.equals(e.getMessage())
                    ? "Error: no se puede dividir entre cero."
                    : "Error: el resultado excede el rango de los enteros (int).");
        }
    }
}
