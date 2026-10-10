package com.calculadora;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Explica una prueba fallida en tres partes: (1) ubicación exacta (archivo, clase, método,
 * línea o componente de la GUI), (2) causa raíz, (3) código corregido. La ubicación sale de
 * la traza real cuando hubo una excepción; si no, de los fuentes actuales.
 */
final class DiagnosticoFallos {

    private static final Path FUENTES = Path.of("src/main/java/com/calculadora");
    private static final Pattern NUMERO_RESULTADO = Pattern.compile("= (-?\\d+)$");

    private static final String CORRECCION_SUMAR = """
            public int sumar(int a, int b) {
                return Math.addExact(a, b);          // detecta el desbordamiento de int
            }""";
    private static final String CORRECCION_RESTAR = """
            public int restar(int a, int b) {
                return Math.subtractExact(a, b);
            }""";
    private static final String CORRECCION_MULTIPLICAR = """
            public int multiplicar(int a, int b) {
                return Math.multiplyExact(a, b);
            }""";
    private static final String CORRECCION_DIVIDIR = """
            public int dividir(int a, int b) {
                if (b == 0) {                                      // validar ANTES de dividir
                    throw new ArithmeticException(DIVISION_POR_CERO);
                }
                if (a == Integer.MIN_VALUE && b == -1) {
                    throw new ArithmeticException("Desbordamiento de entero");
                }
                return a / b;
            }""";
    private static final String CORRECCION_POTENCIA = """
            public int potencia(int base, int exponente) {
                if (exponente < 0) {
                    throw new IllegalArgumentException("No se admite exponente negativo para resultados enteros");
                }
                int resultado = 1;                                    // elemento neutro de la multiplicación
                for (int i = 0; i < exponente; i++) {                 // exactamente 'exponente' iteraciones
                    resultado = Math.multiplyExact(resultado, base);  // acumulador y desbordamiento
                }
                return resultado;
            }""";
    private static final String CORRECCION_RESULTADO = """
            // Resultado.de: toda excepción esperada debe convertirse en mensaje, nunca llegar a la interfaz
            try {
                int valor = operacion.aplicar(calculadora, a, b);
                return new Resultado(true, "Resultado: " + a + " " + operacion.getSimbolo() + " " + b + " = " + valor);
            } catch (ArithmeticException e) {
                return new Resultado(false, Calculadora.DIVISION_POR_CERO.equals(e.getMessage())
                        ? "Error: no se puede dividir entre cero."
                        : "Error: el resultado excede el rango de los enteros (int).");
            } catch (IllegalArgumentException e) {
                return new Resultado(false, "Error: el exponente no puede ser negativo (el resultado no es un entero).");
            }""";
    private static final String CORRECCION_OYENTE = """
            // PanelCalculadora(): conectar el botón con la acción
            botonCalcular.addActionListener(e -> calcular());""";

    private DiagnosticoFallos() {
    }

    static String diagnosticar(HojaDePruebas.CasoPrueba caso, String obtenido, Throwable error) {
        String op = caso.operacion().split(" ")[0];
        String canal = caso.operacion().contains("(GUI)") ? "GUI"
                : caso.operacion().contains("(consola)") ? "CONSOLA" : "UNITARIA";
        Long a = null;
        Long b = null;
        String[] entradas = caso.entradas().split(", ");
        if (entradas.length == 2 && esNumero(entradas[0]) && esNumero(entradas[1])) {
            a = Long.valueOf(entradas[0]);
            b = Long.valueOf(entradas[1]);
        }
        String[] causa = causaRaiz(caso, obtenido, error, op, canal, a, b);
        return "### FALLO " + caso.id() + ": " + caso.descripcion() + "\n\n"
                + "- **Entradas (a, b):** `" + caso.entradas() + "` · **Operación:** " + caso.operacion() + "\n"
                + "- **Esperado:** `" + caso.esperado() + "` · **Obtenido:** `" + obtenido + "`\n"
                + "- **1. Ubicación exacta:** " + ubicacion(error, op, canal) + "\n"
                + "- **2. Causa raíz:** " + causa[0] + "\n"
                + "- **3. Corrección sugerida:**\n\n```java\n" + causa[1] + "\n```\n";
    }

    // --------------------------------------------------------------- ubicación

    private static String ubicacion(Throwable error, String op, String canal) {
        if (error != null) {
            List<StackTraceElement> marcos = new ArrayList<>();
            for (StackTraceElement e : error.getStackTrace()) {
                String clase = e.getClassName();
                if (clase.startsWith("com.calculadora.") && !clase.contains("HojaDePruebas")
                        && !clase.contains("DiagnosticoFallos")) {
                    marcos.add(e);
                }
            }
            if (!marcos.isEmpty()) {
                StackTraceElement primero = marcos.get(0);
                StringBuilder cadena = new StringBuilder();
                for (StackTraceElement e : marcos) {
                    cadena.append(cadena.length() == 0 ? "" : " ← ")
                            .append(nombreSimple(e.getClassName())).append('.').append(e.getMethodName())
                            .append("() (").append(e.getFileName()).append(':').append(e.getLineNumber()).append(')');
                }
                return "archivo `" + primero.getFileName() + "`, clase `" + nombreSimple(primero.getClassName())
                        + "`, método `" + primero.getMethodName() + "()`, **línea " + primero.getLineNumber()
                        + "**. Excepción: `" + error + "`. Cadena de llamadas: " + cadena + ".";
            }
        }
        return switch (canal) {
            case "UNITARIA" -> donde("Calculadora.java", "Calculadora", op + "()",
                    lineaDe("Calculadora.java", "public int " + op + "("))
                    + (op.equals("potencia")
                    ? "; acumulador `resultado` en la línea " + lineaDe("Calculadora.java", "int resultado =")
                    + " y ciclo `for` en la línea " + lineaDe("Calculadora.java", "for (int i")
                    : "");
            case "CONSOLA" -> donde("Resultado.java", "Resultado", "de()",
                    lineaDe("Resultado.java", "public static Resultado de("))
                    + " (mensajes) y `InterfazConsola.java`, método `ejecutar()`, línea "
                    + lineaDe("InterfazConsola.java", "static void ejecutar(") + " (menú y lectura de datos)";
            default -> "componente visual `areaResultado` (JTextArea) y botón `botonCalcular` (JButton) de "
                    + "`PanelCalculadora.java`, método `calcular()`, línea "
                    + lineaDe("PanelCalculadora.java", "void calcular()")
                    + "; el texto lo arma `Resultado.java`, método `de()`, línea "
                    + lineaDe("Resultado.java", "public static Resultado de(");
        };
    }

    private static String donde(String archivo, String clase, String metodo, int linea) {
        return "archivo `" + archivo + "`, clase `" + clase + "`, método `" + metodo + "`, **línea "
                + (linea > 0 ? String.valueOf(linea) : "no localizada") + "**";
    }

    private static int lineaDe(String archivo, String fragmento) {
        try {
            List<String> lineas = Files.readAllLines(FUENTES.resolve(archivo));
            for (int i = 0; i < lineas.size(); i++) {
                if (lineas.get(i).contains(fragmento)) {
                    return i + 1;
                }
            }
        } catch (IOException e) {
            // fuente no disponible (pruebas ejecutadas desde otro directorio): la línea queda sin localizar
        }
        return -1;
    }

    // -------------------------------------------------------------- causa raíz

    private static String[] causaRaiz(HojaDePruebas.CasoPrueba caso, String obtenido, Throwable error,
                                      String op, String canal, Long a, Long b) {
        String esperado = caso.esperado();
        String mensaje = error == null ? "" : String.valueOf(error.getMessage());
        boolean interfaz = !canal.equals("UNITARIA");
        boolean obtuvoExcepcion = obtenido.endsWith("Exception");
        boolean esperaError = esperado.endsWith("Exception") || esperado.startsWith("Error:");
        Long valorEsperado = numeroEn(esperado);
        Long valorObtenido = numeroEn(obtenido);

        // División por cero sin la validación `b == 0`
        if (op.equals("dividir") && mensaje.contains("/ by zero")) {
            return new String[]{"En `Calculadora.dividir` **falta la validación condicional `b == 0`** antes de ejecutar `a / b`. "
                    + "Sin ella la JVM lanza un `ArithmeticException(\"/ by zero\")` genérico, distinto de `DIVISION_POR_CERO`"
                    + (interfaz && obtuvoExcepcion ? ", y como nadie lo captura llega sin controlar hasta la interfaz."
                    : ", así que la interfaz no puede mostrar el mensaje controlado."), CORRECCION_DIVIDIR};
        }
        if (op.equals("dividir") && esperado.contains("dividir entre cero") && obtenido.contains("excede el rango")) {
            return new String[]{"`Calculadora.dividir` no lanza `ArithmeticException(DIVISION_POR_CERO)` para `b == 0` (falta la "
                    + "validación o el mensaje es otro), por eso `Resultado.de` clasifica el error como desbordamiento de `int`.",
                    CORRECCION_DIVIDIR};
        }
        // Excepción que escapó hasta la capa de interfaz
        if (interfaz && obtuvoExcepcion && error != null) {
            return new String[]{"La excepción `" + error.getClass().getSimpleName() + "` (\"" + mensaje + "\") **no se captura**: "
                    + "viaja desde la capa de cálculo hasta la interfaz. `Resultado.de` debe convertir cada excepción esperada "
                    + "en un mensaje de error controlado.", CORRECCION_RESULTADO};
        }
        // La GUI o la consola no produjeron nada
        if (canal.equals("GUI") && obtenido.isEmpty()) {
            return new String[]{"El clic en *Calcular* no produjo salida: `botonCalcular` no tiene un `ActionListener` que llame a "
                    + "`calcular()` (o `calcular()` no escribe en `areaResultado`).", CORRECCION_OYENTE};
        }
        if (canal.equals("CONSOLA") && obtenido.equals("(sin salida esperada)")) {
            return new String[]{"`InterfazConsola.ejecutar` no imprimió ningún resultado ni error: la opción del menú no llegó a "
                    + "ejecutar la operación (índice de `OPERACIONES` mal calculado, lectura de datos o salida del bucle "
                    + "anticipada).", correccionPara(op)};
        }
        // La operación devolvió un valor aunque se esperaba un error
        if (esperaError && valorObtenido != null) {
            if (op.equals("potencia") && b != null && b < 0) {
                return new String[]{"`Calculadora.potencia` **no valida el exponente negativo**: el ciclo `for` no se ejecuta y se "
                        + "devuelve el acumulador inicial, un `int` incorrecto para `base^(-n)`.", CORRECCION_POTENCIA};
            }
            if (op.equals("potencia") && a != null && b != null && b >= 0 && a != 0 && valorObtenido == 0) {
                return new String[]{"El **acumulador del ciclo `for` se inicializa en 0** (`0 * base` es siempre 0), por eso nunca "
                        + "llega a desbordar ni a calcular la potencia. Debe iniciar en 1 (elemento neutro de la multiplicación).",
                        CORRECCION_POTENCIA};
            }
            return new String[]{"`" + op + "` **no detecta el desbordamiento de `int`**: usa el operador normal, que envuelve el valor "
                    + "en silencio (p. ej. `2147483647 + 1 = -2147483648`), en lugar de "
                    + "`Math.addExact/subtractExact/multiplyExact`.", correccionPara(op)};
        }
        // Valor numérico incorrecto
        if (valorEsperado != null && valorObtenido != null && !valorEsperado.equals(valorObtenido)) {
            if (op.equals("potencia") && a != null && b != null) {
                long base = a;
                long exponente = b;
                String motivo;
                if (exponente == 0) {
                    motivo = "Con exponente 0 el resultado debe ser 1: el **acumulador no vale 1** cuando el ciclo no se ejecuta.";
                } else if (valorObtenido == 0 && valorEsperado != 0) {
                    motivo = "El **acumulador del ciclo `for` se inicializa en 0**: `0 * base` es siempre 0. Debe iniciar en 1 "
                            + "(elemento neutro de la multiplicación).";
                } else if (base != 0 && valorObtenido == valorEsperado * base) {
                    motivo = "El ciclo `for` **ejecuta una iteración de más** (condición `i <= exponente`): multiplica "
                            + "`exponente + 1` veces. Debe ser `i < exponente`.";
                } else if (base != 0 && valorEsperado % base == 0 && valorObtenido == valorEsperado / base) {
                    motivo = "El ciclo `for` **ejecuta una iteración de menos** (inicia en 1 o usa `i < exponente - 1`): "
                            + "multiplica `exponente - 1` veces.";
                } else {
                    motivo = "El acumulador no coincide con `base^exponente`: revise la inicialización (1), la condición del `for` "
                            + "(`i < exponente`) y la operación acumulativa (`resultado *= base`).";
                }
                return new String[]{motivo, CORRECCION_POTENCIA};
            }
            return new String[]{"El valor obtenido (" + valorObtenido + ") difiere del esperado (" + valorEsperado
                    + "): la fórmula u operador de `" + op + "` no corresponde a la operación definida.", correccionPara(op)};
        }
        // Se esperaba un valor pero salió un error
        if (valorEsperado != null && (obtuvoExcepcion || obtenido.startsWith("Error:"))) {
            return new String[]{"Se esperaba un resultado válido pero la operación `" + op + "` lanzó un error: el resultado sí cabe "
                    + "en `int`, así que el acumulador o los operandos se están calculando mal (p. ej. iteraciones de más en el "
                    + "`for`).", correccionPara(op)};
        }
        return new String[]{"El resultado obtenido no coincide con el esperado y no encaja en un patrón conocido: compare ambos "
                + "textos y revise `Resultado.java` (mensajes) y el método `" + op + "`.", correccionPara(op)};
    }

    private static String correccionPara(String op) {
        return switch (op) {
            case "sumar" -> CORRECCION_SUMAR;
            case "restar" -> CORRECCION_RESTAR;
            case "multiplicar" -> CORRECCION_MULTIPLICAR;
            case "dividir" -> CORRECCION_DIVIDIR;
            default -> CORRECCION_POTENCIA;
        };
    }

    private static boolean esNumero(String s) {
        return s.matches("-?\\d{1,18}");
    }

    private static Long numeroEn(String s) {
        if (esNumero(s)) {
            return Long.valueOf(s);
        }
        Matcher m = NUMERO_RESULTADO.matcher(s);
        return m.find() ? Long.valueOf(m.group(1)) : null;
    }

    private static String nombreSimple(String nombreClase) {
        return nombreClase.substring(nombreClase.lastIndexOf('.') + 1);
    }
}
