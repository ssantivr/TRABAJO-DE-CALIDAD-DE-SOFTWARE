package com.calculadora;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Pruebas de la consola con entrada simulada: resultados, validación de datos y mensajes de error. */
class InterfazConsolaTest {

    /** Ejecuta la consola con las líneas tecleadas (separadas por espacios) y devuelve todo lo impreso. */
    private static String ejecutar(String tecleado) {
        ByteArrayOutputStream bufer = new ByteArrayOutputStream();
        String entrada = tecleado.replace(' ', '\n') + "\n";
        InterfazConsola.ejecutar(
                new Scanner(new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8),
                new PrintStream(bufer, true, StandardCharsets.UTF_8));
        return bufer.toString(StandardCharsets.UTF_8);
    }

    @ParameterizedTest(name = "teclear [{0}] muestra: {1}")
    @CsvSource({
            "1 2 3 0,     Resultado: 2 + 3 = 5",
            "2 7 9 0,     Resultado: 7 - 9 = -2",
            "3 4 6 0,     Resultado: 4 * 6 = 24",
            "4 20 4 0,    Resultado: 20 / 4 = 5",
            "5 2 10 0,    Resultado: 2 ^ 10 = 1024",
            "4 10 0 0,    Error: no se puede dividir entre cero.",
            "5 2 -1 0,    Error: el exponente no puede ser negativo (el resultado no es un entero).",
            "5 2 31 0,    Error: el resultado excede el rango de los enteros (int).",
            "3 abc 4 6 0, Entrada inválida: ingrese un número entero.",
            "3 2.5 4 6 0, Entrada inválida: ingrese un número entero.",
            "9 0,         Opción no válida. Elija un número entre 0 y 5."
    })
    void muestraElMensajeEsperado(String tecleado, String esperado) {
        String salida = ejecutar(tecleado);
        assertTrue(salida.contains(esperado), () -> "Salida obtenida:\n" + salida);
    }
}
