# 5. Módulo de diagnóstico de fallos

Cada vez que un caso termina en `Falla` (resultado distinto del esperado) o lanza una excepción inesperada, `HojaDePruebas` llama a `DiagnosticoFallos` y entrega **tres cosas**:

1. **Ubicación exacta:** archivo, clase, método y línea. Si hubo excepción, sale de la **pila de llamadas real** (con la cadena `Calculadora.dividir ← Operacion.aplicar ← Resultado.de ← InterfazConsola.ejecutar`); si no la hubo, se busca en el código fuente actual (para la GUI se indica el componente: `areaResultado`, `botonCalcular`).
2. **Causa raíz:** explicación técnica y lógica (falta de validación `b == 0`, desbordamiento de `int`, acumulador mal inicializado o iteración de más en el ciclo `for` de la potencia, excepción sin capturar, botón sin `ActionListener`…).
3. **Corrección sugerida:** el fragmento de código corregido.

El informe se imprime en la consola y se guarda en `docs/diagnostico_fallos.md` en cada ejecución (con el código sano dice que no hay fallos).

## Cómo se usa

```bash
./build.sh probar        # ejecuta las pruebas; si algo falla, imprime el diagnóstico y sale con código 1
cat docs/diagnostico_fallos.md
```

## Qué patrones reconoce

| Síntoma | Causa raíz que reporta |
|---|---|
| `ArithmeticException("/ by zero")` en `dividir`, o mensaje de desbordamiento en vez del de división por cero | Falta la validación `b == 0` antes de `a / b` |
| Excepción que llega hasta la consola o la GUI | `Resultado.de` no la captura |
| GUI sin salida tras pulsar *Calcular* | `botonCalcular` sin `ActionListener` |
| Se esperaba error y se devolvió un valor | Operación sin `Math.xxxExact` (desborda en silencio) o `potencia` sin validar exponente negativo |
| `potencia` devuelve 0 | Acumulador inicializado en 0 en lugar de 1 |
| `potencia` devuelve `esperado * base` | Una iteración de más (`i <= exponente`) |
| `potencia` devuelve `esperado / base` | Una iteración de menos |
| Otro desajuste | Diagnóstico genérico con el método y el código correcto de la operación |

Límite: es un clasificador por patrones conocidos. Un fallo de otro tipo recibe una explicación genérica más la ubicación y el código correcto, pero no una causa específica.

## Demostración con fallos reales

Para comprobar el módulo se introdujo **un defecto a la vez** en copias del proyecto (el original no se modificó), se ejecutaron las pruebas y luego se aplicó literalmente la corrección sugerida: en los seis casos las pruebas volvieron a pasar **41/41**. Los números de línea corresponden a la copia modificada.

| # | Defecto introducido | Fallos | Casos |
|---|---|---|---|
| M1 | Se eliminó el bloque `if (b == 0) { throw ... }` de `Calculadora.dividir`. | 4 | CP-18, CP-19, IT-01, GUI-02 |
| M2 | En `Calculadora.potencia`: `int resultado = 1;` → `int resultado = 0;`. | 10 | CP-21, CP-22, CP-23, CP-24, CP-26, CP-27, CP-30, CP-31, IT-03, GUI-01 |
| M3 | En `Calculadora.potencia`: `i < exponente` → `i <= exponente`. | 9 | CP-21, CP-22, CP-23, CP-24, CP-26, CP-27, CP-30, IT-03, GUI-01 |
| M4 | En `Resultado.de`: `catch (ArithmeticException e)` → `catch (IllegalStateException e)` (la excepción deja de capturarse). | 2 | IT-01, GUI-02 |
| M5 | En `PanelCalculadora`: se quitó `botonCalcular.addActionListener(e -> calcular());`. | 5 | GUI-01, GUI-02, GUI-03, GUI-04, GUI-05 |
| M6 | En `Calculadora.sumar`: `Math.addExact(a, b)` → `a + b`. | 1 | CP-04 |

## Detalle de cada diagnóstico

### Mutante M1

**Defecto introducido:** Se eliminó el bloque `if (b == 0) { throw ... }` de `Calculadora.dividir`.

**Resultado:** 41 pruebas, 37 pasan, **4 fallan** (CP-18, CP-19, IT-01, GUI-02). Diagnóstico del primer fallo, tal como lo imprime el ejecutor:

**FALLO CP-18**: División por cero (caso límite): debe lanzar la excepción controlada

- **Entradas (a, b):** `10, 0` · **Operación:** dividir
- **Esperado:** `ArithmeticException: División por cero` · **Obtenido:** `ArithmeticException: / by zero`
- **1. Ubicación exacta:** archivo `Calculadora.java`, clase `Calculadora`, método `dividir()`, **línea 60**. Excepción: `java.lang.ArithmeticException: / by zero`. Cadena de llamadas: Calculadora.dividir() (Calculadora.java:60).
- **2. Causa raíz:** En `Calculadora.dividir` **falta la validación condicional `b == 0`** antes de ejecutar `a / b`. Sin ella la JVM lanza un `ArithmeticException("/ by zero")` genérico, distinto de `DIVISION_POR_CERO`, así que la interfaz no puede mostrar el mensaje controlado.
- **3. Corrección sugerida:**

```java
public int dividir(int a, int b) {
    if (b == 0) {                                      // validar ANTES de dividir
        throw new ArithmeticException(DIVISION_POR_CERO);
    }
    if (a == Integer.MIN_VALUE && b == -1) {
        throw new ArithmeticException("Desbordamiento de entero");
    }
    return a / b;
}
```

### Mutante M2

**Defecto introducido:** En `Calculadora.potencia`: `int resultado = 1;` → `int resultado = 0;`.

**Resultado:** 41 pruebas, 31 pasan, **10 fallan** (CP-21, CP-22, CP-23, CP-24, CP-26, CP-27, CP-30, CP-31, IT-03, GUI-01). Diagnóstico del primer fallo, tal como lo imprime el ejecutor:

**FALLO CP-21**: Potencia con exponente positivo

- **Entradas (a, b):** `2, 10` · **Operación:** potencia
- **Esperado:** `1024` · **Obtenido:** `0`
- **1. Ubicación exacta:** archivo `Calculadora.java`, clase `Calculadora`, método `potencia()`, **línea 74**; acumulador `resultado` en la línea 78 y ciclo `for` en la línea 79
- **2. Causa raíz:** El **acumulador del ciclo `for` se inicializa en 0**: `0 * base` es siempre 0. Debe iniciar en 1 (elemento neutro de la multiplicación).
- **3. Corrección sugerida:**

```java
public int potencia(int base, int exponente) {
    if (exponente < 0) {
        throw new IllegalArgumentException("No se admite exponente negativo para resultados enteros");
    }
    int resultado = 1;                                    // elemento neutro de la multiplicación
    for (int i = 0; i < exponente; i++) {                 // exactamente 'exponente' iteraciones
        resultado = Math.multiplyExact(resultado, base);  // acumulador y desbordamiento
    }
    return resultado;
}
```

### Mutante M3

**Defecto introducido:** En `Calculadora.potencia`: `i < exponente` → `i <= exponente`.

**Resultado:** 41 pruebas, 32 pasan, **9 fallan** (CP-21, CP-22, CP-23, CP-24, CP-26, CP-27, CP-30, IT-03, GUI-01). Diagnóstico del primer fallo, tal como lo imprime el ejecutor:

**FALLO CP-21**: Potencia con exponente positivo

- **Entradas (a, b):** `2, 10` · **Operación:** potencia
- **Esperado:** `1024` · **Obtenido:** `2048`
- **1. Ubicación exacta:** archivo `Calculadora.java`, clase `Calculadora`, método `potencia()`, **línea 74**; acumulador `resultado` en la línea 78 y ciclo `for` en la línea 79
- **2. Causa raíz:** El ciclo `for` **ejecuta una iteración de más** (condición `i <= exponente`): multiplica `exponente + 1` veces. Debe ser `i < exponente`.
- **3. Corrección sugerida:**

```java
public int potencia(int base, int exponente) {
    if (exponente < 0) {
        throw new IllegalArgumentException("No se admite exponente negativo para resultados enteros");
    }
    int resultado = 1;                                    // elemento neutro de la multiplicación
    for (int i = 0; i < exponente; i++) {                 // exactamente 'exponente' iteraciones
        resultado = Math.multiplyExact(resultado, base);  // acumulador y desbordamiento
    }
    return resultado;
}
```

### Mutante M4

**Defecto introducido:** En `Resultado.de`: `catch (ArithmeticException e)` → `catch (IllegalStateException e)` (la excepción deja de capturarse).

**Resultado:** 41 pruebas, 39 pasan, **2 fallan** (IT-01, GUI-02). Diagnóstico del primer fallo, tal como lo imprime el ejecutor:

**FALLO IT-01**: Consola: división por cero muestra mensaje controlado

- **Entradas (a, b):** `10, 0` · **Operación:** dividir (consola)
- **Esperado:** `Error: no se puede dividir entre cero.` · **Obtenido:** `ArithmeticException`
- **1. Ubicación exacta:** archivo `Calculadora.java`, clase `Calculadora`, método `dividir()`, **línea 58**. Excepción: `java.lang.ArithmeticException: División por cero`. Cadena de llamadas: Calculadora.dividir() (Calculadora.java:58) ← Operacion.aplicar() (Operacion.java:33) ← Resultado.de() (Resultado.java:14) ← InterfazConsola.ejecutar() (InterfazConsola.java:37).
- **2. Causa raíz:** La excepción `ArithmeticException` ("División por cero") **no se captura**: viaja desde la capa de cálculo hasta la interfaz. `Resultado.de` debe convertir cada excepción esperada en un mensaje de error controlado.
- **3. Corrección sugerida:**

```java
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
}
```

### Mutante M5

**Defecto introducido:** En `PanelCalculadora`: se quitó `botonCalcular.addActionListener(e -> calcular());`.

**Resultado:** 41 pruebas, 36 pasan, **5 fallan** (GUI-01, GUI-02, GUI-03, GUI-04, GUI-05). Diagnóstico del primer fallo, tal como lo imprime el ejecutor:

**FALLO GUI-01**: GUI: potencia muestra el resultado

- **Entradas (a, b):** `2, 10` · **Operación:** potencia (GUI)
- **Esperado:** `Resultado: 2 ^ 10 = 1024` · **Obtenido:** ``
- **1. Ubicación exacta:** componente visual `areaResultado` (JTextArea) y botón `botonCalcular` (JButton) de `PanelCalculadora.java`, método `calcular()`, línea 83; el texto lo arma `Resultado.java`, método `de()`, línea 12
- **2. Causa raíz:** El clic en *Calcular* no produjo salida: `botonCalcular` no tiene un `ActionListener` que llame a `calcular()` (o `calcular()` no escribe en `areaResultado`).
- **3. Corrección sugerida:**

```java
// PanelCalculadora(): conectar el botón con la acción
botonCalcular.addActionListener(e -> calcular());
```

### Mutante M6

**Defecto introducido:** En `Calculadora.sumar`: `Math.addExact(a, b)` → `a + b`.

**Resultado:** 41 pruebas, 40 pasan, **1 falla** (CP-04). Diagnóstico del primer fallo, tal como lo imprime el ejecutor:

**FALLO CP-04**: Suma con desbordamiento de int

- **Entradas (a, b):** `2147483647, 1` · **Operación:** sumar
- **Esperado:** `ArithmeticException` · **Obtenido:** `-2147483648`
- **1. Ubicación exacta:** archivo `Calculadora.java`, clase `Calculadora`, método `sumar()`, **línea 35**
- **2. Causa raíz:** `sumar` **no detecta el desbordamiento de `int`**: usa el operador normal, que envuelve el valor en silencio (p. ej. `2147483647 + 1 = -2147483648`), en lugar de `Math.addExact/subtractExact/multiplyExact`.
- **3. Corrección sugerida:**

```java
public int sumar(int a, int b) {
    return Math.addExact(a, b);          // detecta el desbordamiento de int
}
```
