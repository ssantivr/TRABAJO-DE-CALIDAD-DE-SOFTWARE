# 2. Plan de pruebas unitarias (formato hoja de Excel)

La misma tabla está en `docs/pruebas_unitarias.xlsx` (Excel; la columna Estado es una fórmula) y en `docs/resultados_pruebas.md`.
**"Resultado Obtenido" y "Estado" salen de ejecutar `HojaDePruebas`** (`./build.sh probar`); no se llenaron a mano.

**Convenciones**

- Resultado esperado `ArithmeticException` / `IllegalArgumentException`: el método debe lanzar esa excepción (la consola y la GUI la convierten en un mensaje). En la división por cero se exige además el mensaje controlado `División por cero` (CP-18, CP-19).
- Si una prueba falla, `HojaDePruebas` genera el diagnóstico (ubicación, causa raíz y corrección): ver `05_DIAGNOSTICO_DE_FALLOS.md`.
- `CP-xx`: pruebas unitarias de `Calculadora`. `IT-xx`: pruebas de la consola con entrada simulada. `GUI-xx`: pruebas de la interfaz gráfica (`PanelCalculadora`) con clics simulados, sin necesidad de pantalla.
- Decisiones de diseño probadas: la división es entera (trunca hacia cero); `0^0 = 1` por convención; el exponente negativo no se soporta porque el resultado no sería un `int`; todo resultado fuera del rango de `int` se rechaza en lugar de desbordarse en silencio.

**Casos límite pedidos:** división por cero (CP-18, CP-19, IT-01, GUI-02), exponente positivo (CP-21, CP-22, CP-27, CP-30), exponente cero (CP-23, CP-24), exponente negativo (CP-28, CP-29, IT-04, GUI-05).

## Tabla de pruebas

| Caso de Prueba | Descripción / Lo que teníamos planeado hacer | Entradas (a, b) | Operación | Resultado Esperado | Resultado Obtenido | Estado |
|---|---|---|---|---|---|---|
| CP-01 | Suma de dos positivos | `5, 3` | sumar | `8` | `8` | Pasa |
| CP-02 | Suma de dos negativos | `-5, -3` | sumar | `-8` | `-8` | Pasa |
| CP-03 | Suma con cero (elemento neutro) | `7, 0` | sumar | `7` | `7` | Pasa |
| CP-04 | Suma con desbordamiento de int | `2147483647, 1` | sumar | `ArithmeticException` | `ArithmeticException` | Pasa |
| CP-05 | Resta de positivos | `10, 4` | restar | `6` | `6` | Pasa |
| CP-06 | Resta con resultado negativo | `3, 8` | restar | `-5` | `-5` | Pasa |
| CP-07 | Resta de números iguales | `9, 9` | restar | `0` | `0` | Pasa |
| CP-08 | Resta con desbordamiento de int | `-2147483648, 1` | restar | `ArithmeticException` | `ArithmeticException` | Pasa |
| CP-09 | Multiplicación de positivos | `6, 7` | multiplicar | `42` | `42` | Pasa |
| CP-10 | Multiplicación por cero | `123, 0` | multiplicar | `0` | `0` | Pasa |
| CP-11 | Multiplicación positivo por negativo | `-4, 5` | multiplicar | `-20` | `-20` | Pasa |
| CP-12 | Multiplicación de dos negativos | `-4, -5` | multiplicar | `20` | `20` | Pasa |
| CP-13 | Multiplicación con desbordamiento de int | `100000, 100000` | multiplicar | `ArithmeticException` | `ArithmeticException` | Pasa |
| CP-14 | División exacta | `20, 4` | dividir | `5` | `5` | Pasa |
| CP-15 | División entera (trunca decimales) | `7, 2` | dividir | `3` | `3` | Pasa |
| CP-16 | División entera negativa (trunca hacia cero) | `-7, 2` | dividir | `-3` | `-3` | Pasa |
| CP-17 | Dividendo cero | `0, 5` | dividir | `0` | `0` | Pasa |
| CP-18 | División por cero (caso límite): debe lanzar la excepción controlada | `10, 0` | dividir | `ArithmeticException: División por cero` | `ArithmeticException: División por cero` | Pasa |
| CP-19 | Cero dividido entre cero: debe lanzar la excepción controlada | `0, 0` | dividir | `ArithmeticException: División por cero` | `ArithmeticException: División por cero` | Pasa |
| CP-20 | División con desbordamiento (MIN_VALUE / -1) | `-2147483648, -1` | dividir | `ArithmeticException` | `ArithmeticException` | Pasa |
| CP-21 | Potencia con exponente positivo | `2, 10` | potencia | `1024` | `1024` | Pasa |
| CP-22 | Potencia con exponente 1 | `5, 1` | potencia | `5` | `5` | Pasa |
| CP-23 | Potencia con exponente cero | `7, 0` | potencia | `1` | `1` | Pasa |
| CP-24 | Cero elevado a cero (convención) | `0, 0` | potencia | `1` | `1` | Pasa |
| CP-25 | Base cero, exponente positivo | `0, 5` | potencia | `0` | `0` | Pasa |
| CP-26 | Base negativa, exponente impar | `-2, 3` | potencia | `-8` | `-8` | Pasa |
| CP-27 | Base negativa, exponente par | `-2, 4` | potencia | `16` | `16` | Pasa |
| CP-28 | Exponente negativo (no representable en int) | `2, -1` | potencia | `IllegalArgumentException` | `IllegalArgumentException` | Pasa |
| CP-29 | Cero con exponente negativo | `0, -1` | potencia | `IllegalArgumentException` | `IllegalArgumentException` | Pasa |
| CP-30 | Mayor potencia de 2 que cabe en int | `2, 30` | potencia | `1073741824` | `1073741824` | Pasa |
| CP-31 | Potencia con desbordamiento de int | `2, 31` | potencia | `ArithmeticException` | `ArithmeticException` | Pasa |
| IT-01 | Consola: división por cero muestra mensaje controlado | `10, 0` | dividir (consola) | `Error: no se puede dividir entre cero.` | `Error: no se puede dividir entre cero.` | Pasa |
| IT-02 | Consola: suma muestra el resultado | `2, 3` | sumar (consola) | `Resultado: 2 + 3 = 5` | `Resultado: 2 + 3 = 5` | Pasa |
| IT-03 | Consola: potencia muestra el resultado | `2, 10` | potencia (consola) | `Resultado: 2 ^ 10 = 1024` | `Resultado: 2 ^ 10 = 1024` | Pasa |
| IT-04 | Consola: exponente negativo muestra mensaje controlado | `2, -1` | potencia (consola) | `Error: el exponente no puede ser negativo (el resultado no es un entero).` | `Error: el exponente no puede ser negativo (el resultado no es un entero).` | Pasa |
| IT-05 | Consola: entrada no numérica se rechaza y se vuelve a pedir | `abc → 4, 6` | multiplicar (consola) | `Entrada inválida: ingrese un número entero. → Resultado: 4 * 6 = 24` | `Entrada inválida: ingrese un número entero. → Resultado: 4 * 6 = 24` | Pasa |
| GUI-01 | GUI: potencia muestra el resultado | `2, 10` | potencia (GUI) | `Resultado: 2 ^ 10 = 1024` | `Resultado: 2 ^ 10 = 1024` | Pasa |
| GUI-02 | GUI: división por cero muestra error en pantalla | `10, 0` | dividir (GUI) | `Error: no se puede dividir entre cero.` | `Error: no se puede dividir entre cero.` | Pasa |
| GUI-03 | GUI: texto no numérico muestra error en pantalla | `abc, 5` | sumar (GUI) | `Entrada inválida: ingrese un número entero.` | `Entrada inválida: ingrese un número entero.` | Pasa |
| GUI-04 | GUI: resta con resultado negativo | `7, 9` | restar (GUI) | `Resultado: 7 - 9 = -2` | `Resultado: 7 - 9 = -2` | Pasa |
| GUI-05 | GUI: exponente negativo muestra error en pantalla | `3, -2` | potencia (GUI) | `Error: el exponente no puede ser negativo (el resultado no es un entero).` | `Error: el exponente no puede ser negativo (el resultado no es un entero).` | Pasa |
