# Trabajo de Calidad de Software: calculadora de enteros en Java (v1.1.0)

Aplicación de **consola** (`Scanner` / `System.out`) que recibe dos números enteros y calcula suma, resta, multiplicación, división y potencia, con validación de datos y manejo explícito de la división por cero. Incluye además una ventana Swing opcional (`--gui`). Código, mensajes y documentación en español.

## Estructura

```
├── pom.xml                                    # proyecto Maven: lo abren IntelliJ IDEA y VS Code
├── .vscode/                                   # configuraciones de ejecución para VS Code
├── build.sh                                   # compila, prueba, empaqueta (Linux/macOS/Git Bash/WSL)
├── README.md
├── ANALISIS_ISO25010_Y_UML.md                 # resumen: ISO 25010 y casos de uso (consola)
├── src/main/java/com/calculadora/
│   ├── Principal.java                         # punto de entrada: consola (por defecto) o --gui
│   ├── Calculadora.java                       # sumar, restar, multiplicar, dividir, potencia (ciclo for)
│   ├── Operacion.java                         # enum de operaciones (símbolo y etiqueta)
│   ├── Resultado.java                         # resultado/mensaje, compartido por consola y GUI
│   ├── InterfazConsola.java                   # menú de consola (Scanner)
│   ├── VentanaCalculadora.java                # ventana; usa FlatLaf si está disponible, si no Nimbus
│   └── PanelCalculadora.java                  # contenido de la GUI (campos, selector, resultado)
├── src/test/java/com/calculadora/             # pruebas unitarias JUnit 5 (39)
│   ├── CalculadoraTest.java                   # las cinco operaciones y sus casos límite
│   └── InterfazConsolaTest.java               # consola con entrada simulada
├── src/hoja-pruebas/java/com/calculadora/     # hoja de pruebas ejecutable, sin dependencias (41 casos)
│   ├── HojaDePruebas.java                     # ejecuta los casos y genera la hoja de resultados
│   └── DiagnosticoFallos.java                 # diagnóstico de fallos: ubicación, causa raíz, corrección
└── docs/
    ├── 01_UML_Y_ACTORES.md                    # actores, casos de uso, clases y secuencia (Mermaid)
    ├── UML CASOS DE USO.docx                  # diagrama de casos de uso
    ├── 02_PLAN_DE_PRUEBAS.md                  # tabla estilo Excel con resultados reales
    ├── pruebas_unitarias.xlsx                 # hoja de Excel (la columna Estado es una fórmula)
    ├── 03_CALIDAD_ISO_25010.md                # qué evaluar y qué no
    ├── 04_GESTION_DE_CAMBIOS.md               # comunicado formal al cliente (CR-001)
    ├── 05_DIAGNOSTICO_DE_FALLOS.md            # cómo funciona el diagnóstico + demo con fallos reales
    ├── diagnostico_fallos.md                  # informe de la última ejecución (lo genera HojaDePruebas)
    └── resultados_pruebas.csv / .md           # los genera HojaDePruebas
```

## Requisitos

JDK 17 o superior (`java -version`, `javac -version`). No hace falta instalar Maven: los IDE traen el suyo y, sin IDE, se puede compilar con `javac`. Solo las pruebas JUnit y la apariencia FlatLaf (opcional) requieren descargar un `.jar`.

## Abrir en un IDE

El proyecto es un proyecto **Maven** (`pom.xml`), así que el IDE descarga JUnit y reconoce las carpetas de código por sí solo.

- **IntelliJ IDEA:** *File > Open* y elegir la carpeta del proyecto (o el `pom.xml`). Para ejecutar, abrir `Principal.java` y pulsar el triángulo verde junto a `main`; las pruebas se ejecutan igual desde `src/test`.
- **Visual Studio Code:** instalar el *Extension Pack for Java* (VS Code lo sugiere al abrir la carpeta) y usar *File > Open Folder*. En *Run and Debug* hay tres configuraciones listas: `Calculadora (consola)`, `Calculadora (ventana)` y `Hoja de pruebas (41 casos)`. Las pruebas JUnit aparecen en el panel *Testing*.
- **Con Maven instalado:** `mvn test` ejecuta las 39 pruebas JUnit y `mvn package` crea `target/calculadora-1.1.0.jar`.

## Compilar y ejecutar sin IDE (PowerShell, desde la raíz del proyecto)

```powershell
javac --release 17 -encoding UTF-8 -d build\classes (Get-ChildItem -Recurse src\main -Filter *.java).FullName
java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build\classes com.calculadora.Principal          # consola
java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build\classes com.calculadora.Principal --gui    # ventana
```

## Pruebas unitarias (JUnit 5)

`src/test` contiene las pruebas de `Calculadora` (las cinco operaciones, división por cero, exponente negativo y desbordamiento) y de la consola con entrada simulada. Después de compilar:

```powershell
# Una sola vez: JUnit 5 standalone
Invoke-WebRequest "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.11.4/junit-platform-console-standalone-1.11.4.jar" -OutFile junit.jar

javac --release 17 -encoding UTF-8 -cp "build\classes;junit.jar" -d build\test-classes (Get-ChildItem -Recurse src\test -Filter *.java).FullName
java "-Dfile.encoding=UTF-8" -jar junit.jar execute -cp "build\classes;build\test-classes" --scan-classpath build\test-classes --details=tree
```

## Hoja de pruebas y diagnóstico de fallos

`HojaDePruebas` ejecuta los 41 casos de la hoja de Excel (unitarios, consola y GUI sin pantalla) sin necesidad de JUnit y vuelve a generar `docs/resultados_pruebas.csv`, `docs/resultados_pruebas.md` y `docs/diagnostico_fallos.md`:

```powershell
javac --release 17 -encoding UTF-8 -cp build\classes -d build\hoja-classes (Get-ChildItem -Recurse src\hoja-pruebas -Filter *.java).FullName
java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp "build\classes;build\hoja-classes" com.calculadora.HojaDePruebas docs
```

Si un caso falla, imprime **(1)** la ubicación exacta (archivo, clase, método, línea o componente de la GUI), **(2)** la causa raíz y **(3)** el código corregido. Detalles y demostración con seis defectos reales en `docs/05_DIAGNOSTICO_DE_FALLOS.md`.

## Uso con bash (Linux / macOS / Git Bash / WSL)

```bash
./build.sh                # compila + ejecuta la hoja de pruebas + crea build/calculadora-1.1.0.jar
./build.sh probar         # compila y ejecuta solo la hoja de pruebas
./build.sh ejecutar       # abre la consola
./build.sh gui            # abre la ventana
./build.sh dependencias   # (opcional) descarga FlatLaf a lib/ para el look moderno; luego ./build.sh jar
./build.sh distribucion   # además crea dist/calculadora-v1.1.0.zip (o .tar.gz)
./build.sh limpiar        # borra build/ y dist/
```

`build.sh` no se pudo ejecutar en el equipo donde se preparó esta versión (Windows sin bash); los comandos de PowerShell de este README sí se ejecutaron.

## Decisiones de diseño

| Tema | Decisión |
|---|---|
| Nombres en el código | Todo el código está en **español**: `sumar`, `restar`, `multiplicar`, `dividir`, `potencia(int base, int exponente)`. Los identificadores no llevan tildes ni ñ (`Operacion`, `DIVISION_POR_CERO`) para evitar problemas de codificación. Solo quedan en inglés los nombres que imponen Java y sus bibliotecas (`main`, `ArithmeticException`, Swing, JUnit). |
| Consola por defecto | El enunciado pide lectura y escritura por consola: sin argumentos se abre la consola; la ventana solo con `--gui`. |
| División por cero | `dividir` lanza `ArithmeticException`; `Resultado` la convierte en `Error: no se puede dividir entre cero.`, que se muestra en la consola o en rojo en la GUI. El programa sigue funcionando. |
| División | Entera: trunca hacia cero (`7 / 2 = 3`, `-7 / 2 = -3`). |
| Desbordamiento | `Math.addExact`, `subtractExact` y `multiplyExact`: si el resultado no cabe en `int` se informa un error en vez de devolver un valor incorrecto. |
| Potencia (CR-001) | `potencia` usa un ciclo `for` (sin `Math.pow`). `x^0 = 1`, incluido `0^0 = 1`. Exponente negativo: no soportado (el resultado no es entero). |
| Entrada | Solo enteros. En consola se vuelve a pedir el dato; en la GUI se muestra el error y se conservan los campos. |
| Sin duplicar lógica | La consola y la GUI usan la misma enumeración `Operacion` y los mismos mensajes de `Resultado`. |
| FlatLaf opcional | Se carga por nombre en tiempo de ejecución: no hay dependencia de compilación; sin el `.jar` se usa Nimbus. |

## Control de versiones del cambio

- **v1.0.0**: línea base (cuatro operaciones), commit `feat: agregar métodos aritméticos básicos y manejo de errores`.
- **v1.1.0**: añade `potencia` (solicitud de cambio **CR-001**, ver `docs/04_GESTION_DE_CAMBIOS.md`), commit `feat: agregar operación de potencia usando ciclo for (gestión de cambio)`.
