package com.calculadora;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Pruebas unitarias (JUnit 5) de las cinco operaciones de {@link Calculadora}. */
class CalculadoraTest {

    private final Calculadora calculadora = new Calculadora();

    @ParameterizedTest(name = "{0} + {1} = {2}")
    @CsvSource({"5, 3, 8", "-5, -3, -8", "7, 0, 7"})
    void sumar(int a, int b, int esperado) {
        assertEquals(esperado, calculadora.sumar(a, b));
    }

    @ParameterizedTest(name = "{0} - {1} = {2}")
    @CsvSource({"10, 4, 6", "3, 8, -5", "9, 9, 0"})
    void restar(int a, int b, int esperado) {
        assertEquals(esperado, calculadora.restar(a, b));
    }

    @ParameterizedTest(name = "{0} * {1} = {2}")
    @CsvSource({"6, 7, 42", "123, 0, 0", "-4, 5, -20", "-4, -5, 20"})
    void multiplicar(int a, int b, int esperado) {
        assertEquals(esperado, calculadora.multiplicar(a, b));
    }

    @ParameterizedTest(name = "{0} / {1} = {2}")
    @CsvSource({"20, 4, 5", "7, 2, 3", "-7, 2, -3", "0, 5, 0"})
    void dividir(int a, int b, int esperado) {
        assertEquals(esperado, calculadora.dividir(a, b));
    }

    @ParameterizedTest(name = "{0} / 0 lanza ArithmeticException")
    @CsvSource({"10", "0", "-10"})
    void dividirPorCero(int a) {
        ArithmeticException e = assertThrows(ArithmeticException.class, () -> calculadora.dividir(a, 0));
        assertEquals(Calculadora.DIVISION_POR_CERO, e.getMessage());
    }

    @ParameterizedTest(name = "{0} ^ {1} = {2}")
    @CsvSource({"2, 10, 1024", "5, 1, 5", "7, 0, 1", "0, 0, 1", "0, 5, 0", "-2, 3, -8", "-2, 4, 16",
            "2, 30, 1073741824"})
    void potencia(int base, int exponente, int esperado) {
        assertEquals(esperado, calculadora.potencia(base, exponente));
    }

    @ParameterizedTest(name = "{0} ^ {1} lanza IllegalArgumentException")
    @CsvSource({"2, -1", "0, -1"})
    void potenciaConExponenteNegativo(int base, int exponente) {
        assertThrows(IllegalArgumentException.class, () -> calculadora.potencia(base, exponente));
    }

    @Test
    void desbordamiento() {
        assertThrows(ArithmeticException.class, () -> calculadora.sumar(Integer.MAX_VALUE, 1));
        assertThrows(ArithmeticException.class, () -> calculadora.restar(Integer.MIN_VALUE, 1));
        assertThrows(ArithmeticException.class, () -> calculadora.multiplicar(100000, 100000));
        assertThrows(ArithmeticException.class, () -> calculadora.dividir(Integer.MIN_VALUE, -1));
        assertThrows(ArithmeticException.class, () -> calculadora.potencia(2, 31));
    }
}
