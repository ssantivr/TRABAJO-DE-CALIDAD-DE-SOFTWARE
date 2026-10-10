package com.calculadora;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ejecutor de pruebas sin dependencias (no necesita JUnit, así que compila sin internet).
 * Ejecuta todos los casos (Calculadora, consola y GUI sin pantalla), imprime un resumen
 * y escribe la hoja de pruebas en CSV (se abre en Excel) y Markdown en el directorio
 * de salida (por defecto "docs"). Código de salida 1 si algún caso falla.
 */
public final class HojaDePruebas {

    record CasoPrueba(String id, String descripcion, String entradas,
                      String operacion, String esperado, Supplier<String> obtenido) {
    }

    private static final Calculadora CALCULADORA = new Calculadora();
    private static final Pattern LINEA_INTERFAZ = Pattern.compile("(Resultado:|Error:|Entrada inválida:).*");
    private static final List<CasoPrueba> CASOS = new ArrayList<>();
    private static Throwable ultimoError;

    private static void unitaria(String id, String descripcion, int a, int b, String operacion, String esperado,
                                 IntSupplier calculo) {
        CASOS.add(new CasoPrueba(id, descripcion, a + ", " + b, operacion, esperado, () -> {
            try {
                return String.valueOf(calculo.getAsInt());
            } catch (RuntimeException e) {
                ultimoError = e;
                String nombre = e.getClass().getSimpleName();
                return esperado.contains(":") ? nombre + ": " + e.getMessage() : nombre;
            }
        }));
    }

    private static void consola(String id, String descripcion, String entradas, String operacion, String tecleado,
                                String esperado) {
        CASOS.add(new CasoPrueba(id, descripcion, entradas, operacion + " (consola)", esperado, () -> {
            ByteArrayOutputStream bufer = new ByteArrayOutputStream();
            PrintStream salida = new PrintStream(bufer, true, StandardCharsets.UTF_8);
            InterfazConsola.ejecutar(new Scanner(new ByteArrayInputStream(tecleado.getBytes(StandardCharsets.UTF_8)),
                    StandardCharsets.UTF_8), salida);
            List<String> encontradas = new ArrayList<>();
            for (String linea : bufer.toString(StandardCharsets.UTF_8).split("\\R")) {
                Matcher m = LINEA_INTERFAZ.matcher(linea);
                if (m.find()) {
                    encontradas.add(m.group());
                }
            }
            return encontradas.isEmpty() ? "(sin salida esperada)" : String.join(" → ", encontradas);
        }));
    }

    private static void gui(String id, String descripcion, String a, String b, Operacion operacion, String esperado) {
        CASOS.add(new CasoPrueba(id, descripcion, a + ", " + b, operacion.name().toLowerCase() + " (GUI)", esperado, () -> {
            PanelCalculadora panel = new PanelCalculadora();
            panel.campoPrimero.setText(a);
            panel.campoSegundo.setText(b);
            panel.botonesOperacion.get(operacion).doClick(0);
            panel.botonCalcular.doClick(0);
            return panel.areaResultado.getText();
        }));
    }

    private static void definirCasos() {
        String aritmetica = "ArithmeticException";
        String argumento = "IllegalArgumentException";

        unitaria("CP-01", "Suma de dos positivos", 5, 3, "sumar", "8", () -> CALCULADORA.sumar(5, 3));
        unitaria("CP-02", "Suma de dos negativos", -5, -3, "sumar", "-8", () -> CALCULADORA.sumar(-5, -3));
        unitaria("CP-03", "Suma con cero (elemento neutro)", 7, 0, "sumar", "7", () -> CALCULADORA.sumar(7, 0));
        unitaria("CP-04", "Suma con desbordamiento de int", Integer.MAX_VALUE, 1, "sumar", aritmetica,
                () -> CALCULADORA.sumar(Integer.MAX_VALUE, 1));

        unitaria("CP-05", "Resta de positivos", 10, 4, "restar", "6", () -> CALCULADORA.restar(10, 4));
        unitaria("CP-06", "Resta con resultado negativo", 3, 8, "restar", "-5", () -> CALCULADORA.restar(3, 8));
        unitaria("CP-07", "Resta de números iguales", 9, 9, "restar", "0", () -> CALCULADORA.restar(9, 9));
        unitaria("CP-08", "Resta con desbordamiento de int", Integer.MIN_VALUE, 1, "restar", aritmetica,
                () -> CALCULADORA.restar(Integer.MIN_VALUE, 1));

        unitaria("CP-09", "Multiplicación de positivos", 6, 7, "multiplicar", "42", () -> CALCULADORA.multiplicar(6, 7));
        unitaria("CP-10", "Multiplicación por cero", 123, 0, "multiplicar", "0", () -> CALCULADORA.multiplicar(123, 0));
        unitaria("CP-11", "Multiplicación positivo por negativo", -4, 5, "multiplicar", "-20",
                () -> CALCULADORA.multiplicar(-4, 5));
        unitaria("CP-12", "Multiplicación de dos negativos", -4, -5, "multiplicar", "20",
                () -> CALCULADORA.multiplicar(-4, -5));
        unitaria("CP-13", "Multiplicación con desbordamiento de int", 100000, 100000, "multiplicar", aritmetica,
                () -> CALCULADORA.multiplicar(100000, 100000));

        unitaria("CP-14", "División exacta", 20, 4, "dividir", "5", () -> CALCULADORA.dividir(20, 4));
        unitaria("CP-15", "División entera (trunca decimales)", 7, 2, "dividir", "3", () -> CALCULADORA.dividir(7, 2));
        unitaria("CP-16", "División entera negativa (trunca hacia cero)", -7, 2, "dividir", "-3",
                () -> CALCULADORA.dividir(-7, 2));
        unitaria("CP-17", "Dividendo cero", 0, 5, "dividir", "0", () -> CALCULADORA.dividir(0, 5));
        String divisionPorCero = aritmetica + ": " + Calculadora.DIVISION_POR_CERO;
        unitaria("CP-18", "División por cero (caso límite): debe lanzar la excepción controlada", 10, 0, "dividir",
                divisionPorCero, () -> CALCULADORA.dividir(10, 0));
        unitaria("CP-19", "Cero dividido entre cero: debe lanzar la excepción controlada", 0, 0, "dividir",
                divisionPorCero, () -> CALCULADORA.dividir(0, 0));
        unitaria("CP-20", "División con desbordamiento (MIN_VALUE / -1)", Integer.MIN_VALUE, -1, "dividir", aritmetica,
                () -> CALCULADORA.dividir(Integer.MIN_VALUE, -1));

        unitaria("CP-21", "Potencia con exponente positivo", 2, 10, "potencia", "1024", () -> CALCULADORA.potencia(2, 10));
        unitaria("CP-22", "Potencia con exponente 1", 5, 1, "potencia", "5", () -> CALCULADORA.potencia(5, 1));
        unitaria("CP-23", "Potencia con exponente cero", 7, 0, "potencia", "1", () -> CALCULADORA.potencia(7, 0));
        unitaria("CP-24", "Cero elevado a cero (convención)", 0, 0, "potencia", "1", () -> CALCULADORA.potencia(0, 0));
        unitaria("CP-25", "Base cero, exponente positivo", 0, 5, "potencia", "0", () -> CALCULADORA.potencia(0, 5));
        unitaria("CP-26", "Base negativa, exponente impar", -2, 3, "potencia", "-8", () -> CALCULADORA.potencia(-2, 3));
        unitaria("CP-27", "Base negativa, exponente par", -2, 4, "potencia", "16", () -> CALCULADORA.potencia(-2, 4));
        unitaria("CP-28", "Exponente negativo (no representable en int)", 2, -1, "potencia", argumento,
                () -> CALCULADORA.potencia(2, -1));
        unitaria("CP-29", "Cero con exponente negativo", 0, -1, "potencia", argumento, () -> CALCULADORA.potencia(0, -1));
        unitaria("CP-30", "Mayor potencia de 2 que cabe en int", 2, 30, "potencia", "1073741824",
                () -> CALCULADORA.potencia(2, 30));
        unitaria("CP-31", "Potencia con desbordamiento de int", 2, 31, "potencia", aritmetica,
                () -> CALCULADORA.potencia(2, 31));

        consola("IT-01", "Consola: división por cero muestra mensaje controlado", "10, 0", "dividir",
                "4\n10\n0\n0\n", "Error: no se puede dividir entre cero.");
        consola("IT-02", "Consola: suma muestra el resultado", "2, 3", "sumar",
                "1\n2\n3\n0\n", "Resultado: 2 + 3 = 5");
        consola("IT-03", "Consola: potencia muestra el resultado", "2, 10", "potencia",
                "5\n2\n10\n0\n", "Resultado: 2 ^ 10 = 1024");
        consola("IT-04", "Consola: exponente negativo muestra mensaje controlado", "2, -1", "potencia",
                "5\n2\n-1\n0\n", "Error: el exponente no puede ser negativo (el resultado no es un entero).");
        consola("IT-05", "Consola: entrada no numérica se rechaza y se vuelve a pedir", "abc → 4, 6", "multiplicar",
                "3\nabc\n4\n6\n0\n", "Entrada inválida: ingrese un número entero. → Resultado: 4 * 6 = 24");

        gui("GUI-01", "GUI: potencia muestra el resultado", "2", "10", Operacion.POTENCIA,
                "Resultado: 2 ^ 10 = 1024");
        gui("GUI-02", "GUI: división por cero muestra error en pantalla", "10", "0", Operacion.DIVIDIR,
                "Error: no se puede dividir entre cero.");
        gui("GUI-03", "GUI: texto no numérico muestra error en pantalla", "abc", "5", Operacion.SUMAR,
                Resultado.ENTRADA_INVALIDA);
        gui("GUI-04", "GUI: resta con resultado negativo", "7", "9", Operacion.RESTAR,
                "Resultado: 7 - 9 = -2");
        gui("GUI-05", "GUI: exponente negativo muestra error en pantalla", "3", "-2", Operacion.POTENCIA,
                "Error: el exponente no puede ser negativo (el resultado no es un entero).");
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        Path directorioSalida = Path.of(args.length > 0 ? args[0] : "docs");
        definirCasos();

        StringBuilder csv = new StringBuilder("﻿");
        csv.append("Caso de Prueba,Descripción / Lo que teníamos planeado hacer,\"Entradas (a, b)\","
                + "Operación,Resultado Esperado,Resultado Obtenido,Estado\n");
        StringBuilder md = new StringBuilder();
        md.append("| Caso de Prueba | Descripción / Lo que teníamos planeado hacer | Entradas (a, b) | Operación "
                + "| Resultado Esperado | Resultado Obtenido | Estado |\n");
        md.append("|---|---|---|---|---|---|---|\n");

        int aprobados = 0;
        StringBuilder diagnostico = new StringBuilder();
        for (CasoPrueba caso : CASOS) {
            ultimoError = null;
            String obtenido;
            try {
                obtenido = caso.obtenido().get();
            } catch (Throwable e) {
                ultimoError = e;
                obtenido = e.getClass().getSimpleName();
            }
            boolean correcto = caso.esperado().equals(obtenido);
            String estado = correcto ? "Pasa" : "Falla";
            if (correcto) {
                aprobados++;
            } else {
                String informe = DiagnosticoFallos.diagnosticar(caso, obtenido, ultimoError);
                System.out.println(informe);
                diagnostico.append(informe).append('\n');
            }
            csv.append(String.join(",", comillas(caso.id()), comillas(caso.descripcion()), comillas(caso.entradas()),
                    comillas(caso.operacion()), comillas(caso.esperado()), comillas(obtenido), comillas(estado)))
                    .append('\n');
            md.append("| ").append(String.join(" | ", caso.id(), caso.descripcion(), "`" + caso.entradas() + "`",
                    caso.operacion(), "`" + caso.esperado() + "`", "`" + obtenido + "`", estado)).append(" |\n");
        }

        Files.createDirectories(directorioSalida);
        Files.writeString(directorioSalida.resolve("resultados_pruebas.csv"), csv, StandardCharsets.UTF_8);
        Files.writeString(directorioSalida.resolve("resultados_pruebas.md"), md, StandardCharsets.UTF_8);
        Files.writeString(directorioSalida.resolve("diagnostico_fallos.md"), "# Diagnóstico de fallos\n\n"
                + (diagnostico.length() == 0 ? "Última ejecución sin fallos: no hay nada que diagnosticar.\n" : diagnostico),
                StandardCharsets.UTF_8);

        int total = CASOS.size();
        System.out.printf("Pruebas: %d | Pasan: %d | Fallan: %d%n", total, aprobados, total - aprobados);
        System.exit(aprobados == total ? 0 : 1);
    }

    private static String comillas(String s) {
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
}
