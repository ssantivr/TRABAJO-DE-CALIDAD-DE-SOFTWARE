package com.calculadora;

/**
 * Calculadora de números enteros (sin estado).
 *
 * Política de errores: los métodos nunca devuelven un valor "mágico" al fallar; lanzan
 * {@link ArithmeticException} (división por cero o desbordamiento de int) para que la
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
}
