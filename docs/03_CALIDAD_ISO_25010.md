# 3. Calidad según ISO/IEC 25010

Contexto: aplicación de escritorio (consola y ventana Swing), un solo proceso, sin red, sin base de datos, sin autenticación y sin datos persistentes. Se usan los nombres del modelo 2011 (la edición 2023 renombra Usabilidad → *Interaction Capability* y Portabilidad → *Flexibility*).

## 3.1 Dimensiones que SÍ vale la pena evaluar

| Característica | Por qué vale la pena aquí | Métrica / prueba concreta | Meta |
|---|---|---|---|
| **Adecuación funcional** (corrección y completitud) | Es una calculadora: el valor del producto es que los resultados sean correctos y estén todas las operaciones pedidas (5 con el CR-001). | Completitud = operaciones implementadas / requeridas. Corrección = casos `Pasa` / casos totales de la hoja de pruebas (partición de equivalencia + valores límite). | 5/5 · 100 % Pasa |
| **Fiabilidad** (tolerancia a fallos, madurez) | El riesgo real: división por cero, desbordamiento de `int`, exponente negativo y texto en vez de número. | Entradas inválidas manejadas sin que el programa termine por excepción no controlada (casos CP-04, 08, 13, 18-20, 28-31, IT-01/04/05, GUI-02/03/05). Una corrida con 1000 entradas aleatorias (*fuzz*) no debe producir ningún *stack trace*. | 0 excepciones no controladas |
| **Usabilidad** (operabilidad, protección contra errores del usuario) | Toda la interacción es por consola o ventana, en español; un mensaje confuso es el principal defecto posible. | Prueba con 3-5 usuarios: tasa de tareas completadas sin ayuda y tiempo por tarea; cuestionario SUS; revisión de que cada error indica qué hacer (IT-01, IT-04, IT-05). En la GUI: contraste y tamaño de letra legibles, Enter ejecuta *Calcular*, el error se ve en rojo y no en un cuadro modal (GUI-02, GUI-03, GUI-05). | ≥ 90 % tareas · SUS ≥ 68 |
| **Mantenibilidad** (modularidad, analizabilidad, modificabilidad, capacidad de prueba) | El proyecto ya sufre un cambio de alcance (potencia): mide qué tan barato es cambiar. | Complejidad ciclomática por método (≤ 10); longitud de método (≤ 20 líneas); duplicación de código (0 bloques); Javadoc en 100 % de los métodos públicos; cobertura de líneas con JaCoCo (≥ 90 %); impacto del cambio = archivos tocados para añadir `potencia` (esperado: `Calculadora`, `Operacion` y las pruebas; `InterfazConsola` y la GUI no cambian porque se construyen a partir de `Operacion`). Análisis estático con Checkstyle/PMD/SpotBugs. | Cumplir los umbrales |

## 3.2 Dimensiones que NO vale la pena evaluar (y por qué)

| Característica | Justificación |
|---|---|
| **Seguridad** (confidencialidad, autenticidad…) | No hay red, usuarios, sesiones, secretos ni datos persistentes. Lo único relevante, validar entradas, ya se cubre en Fiabilidad. Un *pentest* no aportaría información. |
| **Portabilidad** (adaptabilidad, instalabilidad) | Java corre en cualquier JVM 17+. Basta compilar con `--release 17` y ejecutar en un segundo sistema operativo como verificación puntual; no justifica un plan de pruebas por hardware o SO. |
| **Compatibilidad** (coexistencia, interoperabilidad) | No hay otros sistemas con los que intercambiar información ni recursos compartidos que contender. Única salvedad: FlatLaf es opcional; basta una verificación puntual de arranque con y sin su `.jar` (sin él la GUI usa Nimbus). |
| **Eficiencia de desempeño** | Cada operación es O(1), excepto `potencia`, que es O(b) y termina en ≤ 31 iteraciones si el valor absoluto de la base es ≥ 2 porque se detiene al desbordar. Solo `a ∈ {-1, 0, 1}` con `b` enorme recorre el ciclo completo (b = 2 000 000 000 tarda del orden de segundos). No justifica pruebas de carga; basta una prueba puntual si el cliente lo pide. |

## 3.3 Qué queda evidenciado hoy

- Adecuación funcional: **41/41 casos pasan** (`docs/resultados_pruebas.csv`), con las 5 operaciones implementadas.
- Fiabilidad: los casos de desbordamiento, división por cero, exponente negativo y entrada no numérica pasan.
- Usabilidad, cobertura JaCoCo, Checkstyle/PMD y fuzzing: **definidos pero no ejecutados todavía**; son las siguientes verificaciones a realizar antes de la entrega formal.
