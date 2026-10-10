package com.calculadora;

/**
 * Resultado de ejecutar una operación, ya redactado para el usuario.
 * Lo comparten la consola y la GUI para que ambas muestren exactamente los mismos mensajes.
 */
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
        } catch (IllegalArgumentException e) {
            return new Resultado(false, "Error: el exponente no puede ser negativo (el resultado no es un entero).");
        }
    }

    /** Igual que {@link #de}, pero a partir del texto que escribió el usuario. */
    public static Resultado deTexto(Calculadora calculadora, Operacion operacion, String a, String b) {
        int primero;
        int segundo;
        try {
            primero = Integer.parseInt(a.trim());
            segundo = Integer.parseInt(b.trim());
        } catch (NumberFormatException e) {
            return new Resultado(false, ENTRADA_INVALIDA);
        }
        return de(calculadora, operacion, primero, segundo);
    }
}
