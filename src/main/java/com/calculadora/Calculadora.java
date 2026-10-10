package com.calculadora;

/**
 * Calculadora de números enteros (sin estado).
 *
 * Política de errores: los métodos nunca devuelven un valor "mágico" al fallar; lanzan
 * {@link ArithmeticException} (división por cero o desbordamiento de int) o
 * {@link IllegalArgumentException} (exponente negativo en {@link #potencia}) para que la
 * consola, mediante {@link Resultado}, muestre un mensaje controlado.
 */
public final class Calculadora {

    /** Mensaje de la excepción que se lanza al dividir por cero. */
    public static final String DIVISION_POR_CERO = "División por cero";

    /** @return a + b. @throws ArithmeticException si hay desbordamiento de int. */
    public int sumar(int a, int b) {
        return Math.addExact(a, b);
    }

    /** @return a - b. @throws ArithmeticException si hay desbordamiento de int. */
    public int restar(int a, int b) {
        return Math.subtractExact(a, b);
    }

    /** @return a * b. @throws ArithmeticException si hay desbordamiento de int. */
    public int multiplicar(int a, int b) {
        return Math.multiplyExact(a, b);
    }

    /**
     * División entera (trunca hacia cero, p. ej. 7 / 2 = 3, -7 / 2 = -3).
     *
     * @return a / b.
     * @throws ArithmeticException si b es 0 ({@link #DIVISION_POR_CERO}) o si hay
     *                             desbordamiento (Integer.MIN_VALUE / -1).
     */
    public int dividir(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException(DIVISION_POR_CERO);
        }
        if (a == Integer.MIN_VALUE && b == -1) {
            throw new ArithmeticException("Desbordamiento de entero");
        }
        return a / b;
    }

    /**
     * base elevada a exponente, calculada con un ciclo {@code for} (sin Math.pow).
     * Convención: cualquier base elevada a 0 es 1 (incluido 0^0).
     *
     * @return base^exponente.
     * @throws IllegalArgumentException si el exponente es negativo (el resultado no es un int).
     * @throws ArithmeticException      si hay desbordamiento de int.
     */
    public int potencia(int base, int exponente) {
        if (exponente < 0) {
            throw new IllegalArgumentException("No se admite exponente negativo para resultados enteros");
        }
        int resultado = 1;
        for (int i = 0; i < exponente; i++) {
            resultado = Math.multiplyExact(resultado, base);
        }
        return resultado;
    }
}
