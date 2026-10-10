<div align="center">

# Calculadora de enteros en Java

**Trabajo de Calidad de Software**

Aplicación de consola con pruebas unitarias, análisis de calidad ISO/IEC 25010 y gestión formal de cambios.

![Java](https://img.shields.io/badge/Java-17%2B-orange)
![Maven](https://img.shields.io/badge/build-Maven-C71A36)
![JUnit](https://img.shields.io/badge/pruebas-JUnit%205-25A162)
![Casos](https://img.shields.io/badge/casos%20de%20prueba-80%20aprobados-brightgreen)

</div>

---

## Tabla de contenido

1. [Descripción](#descripción)
2. [Características](#características)
3. [Requisitos](#requisitos)
4. [Inicio rápido](#inicio-rápido)
5. [Ejemplo de uso](#ejemplo-de-uso)
6. [Pruebas](#pruebas)
7. [Estructura del proyecto](#estructura-del-proyecto)
8. [Documentación](#documentación)
9. [Decisiones de diseño](#decisiones-de-diseño)
10. [Autor](#autor)

---

## Descripción

Calculadora que recibe dos números enteros por consola y ejecuta cinco operaciones aritméticas: suma, resta, multiplicación, división y potencia. Toda la lectura y escritura se realiza con `Scanner` y `System.out`.

El proyecto cubre el ciclo completo que exige la asignatura: implementación, validación de datos, pruebas unitarias, especificación UML, evaluación de calidad según la norma ISO/IEC 25010 y la gestión de un cambio de alcance (la operación de potencia, solicitud **CR-001**).

El código, los mensajes y la documentación están en español.

## Características

| Característica | Detalle |
|---|---|
| **Cinco operaciones** | `sumar`, `restar`, `multiplicar`, `dividir` y `potencia`, cada una en un método independiente de la clase `Calculadora`. |
| **Validación de entradas** | Solo se aceptan números enteros; ante un dato inválido se informa el error y se vuelve a solicitar. |
| **División por cero controlada** | Se muestra `Error: no se puede dividir entre cero.` y el programa continúa en ejecución. |
| **Potencia con ciclo `for`** | Implementada sin `Math.pow`, con control de exponente negativo. |
| **Detección de desbordamiento** | Si el resultado no cabe en un `int`, se informa un error en lugar de devolver un valor incorrecto. |
| **Interfaz gráfica opcional** | Ventana Swing disponible con el argumento `--gui`; comparte la lógica y los mensajes de la consola. |

## Requisitos

| Componente | Versión | Observación |
|---|---|---|
| JDK | 17 o superior | Verificar con `java -version` y `javac -version`. |
| IDE (opcional) | IntelliJ IDEA o Visual Studio Code | Ambos incluyen Maven; no es necesario instalarlo por separado. |
| Conexión a internet | Solo la primera vez | Para que el IDE o Maven descarguen JUnit 5. |

## Inicio rápido

### IntelliJ IDEA

1. *File > Open* y seleccionar la carpeta del proyecto (o el archivo `pom.xml`).
2. Abrir `src/main/java/com/calculadora/Principal.java`.
3. Pulsar el triángulo verde junto al método `main`.

### Visual Studio Code

1. Instalar el *Extension Pack for Java* (VS Code lo sugiere al abrir la carpeta).
2. *File > Open Folder* y seleccionar la carpeta del proyecto.
3. En *Run and Debug*, elegir una de las configuraciones incluidas:

| Configuración | Acción |
|---|---|
| `Calculadora (consola)` | Ejecuta la aplicación en la terminal integrada. |
| `Calculadora (ventana)` | Abre la interfaz gráfica. |
| `Hoja de pruebas (41 casos)` | Ejecuta la hoja de pruebas y actualiza los resultados en `docs/`. |

### Línea de comandos

Con Maven instalado:

```bash
mvn test       # ejecuta las pruebas JUnit
mvn package    # genera target/calculadora-1.1.0.jar
java -jar target/calculadora-1.1.0.jar
```

Sin Maven, en PowerShell y desde la raíz del proyecto:

```powershell
javac --release 17 -encoding UTF-8 -d build\classes (Get-ChildItem -Recurse src\main -Filter *.java).FullName
java -cp build\classes com.calculadora.Principal          # consola
java -cp build\classes com.calculadora.Principal --gui    # ventana
```

## Ejemplo de uso

```text
=== Calculadora de números enteros ===

1. Sumar
2. Restar
3. Multiplicar
4. Dividir
5. Potencia
0. Salir
Seleccione una opción: 5
Ingrese el primer número (a): 2
Ingrese el segundo número (b): 10
Resultado: 2 ^ 10 = 1024
```

Manejo de errores:

```text
Seleccione una opción: 4
Ingrese el primer número (a): 10
Ingrese el segundo número (b): 0
Error: no se puede dividir entre cero.

Seleccione una opción: 3
Ingrese el primer número (a): abc
Entrada inválida: ingrese un número entero.
Ingrese el primer número (a):
```

## Pruebas

El proyecto incluye dos conjuntos de pruebas complementarios.

| Conjunto | Ubicación | Casos | Alcance |
|---|---|---|---|
| **Pruebas unitarias JUnit 5** | `src/test` | 39 | Las cinco operaciones con sus casos límite y la consola con entrada simulada. |
| **Hoja de pruebas ejecutable** | `src/hoja-pruebas` | 41 | Los casos de la hoja de Excel: 31 unitarios, 5 de consola y 5 de interfaz gráfica. |

### Pruebas unitarias (JUnit 5)

Desde el IDE, ejecutar las clases de `src/test`. Con Maven, `mvn test`. Sin Maven, en PowerShell:

```powershell
# Una sola vez: descargar JUnit 5
Invoke-WebRequest "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.11.4/junit-platform-console-standalone-1.11.4.jar" -OutFile junit.jar

javac --release 17 -encoding UTF-8 -cp "build\classes;junit.jar" -d build\test-classes (Get-ChildItem -Recurse src\test -Filter *.java).FullName
java "-Dfile.encoding=UTF-8" -jar junit.jar execute -cp "build\classes;build\test-classes" --scan-classpath build\test-classes --details=tree
```

### Hoja de pruebas y diagnóstico de fallos

`HojaDePruebas` no requiere JUnit. Ejecuta los 41 casos y actualiza `docs/resultados_pruebas.md` y `docs/diagnostico_fallos.md`:

```powershell
javac --release 17 -encoding UTF-8 -cp build\classes -d build\hoja-classes (Get-ChildItem -Recurse src\hoja-pruebas -Filter *.java).FullName
java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp "build\classes;build\hoja-classes" com.calculadora.HojaDePruebas docs
```

Cuando un caso falla, el informe indica:

1. **Ubicación exacta:** archivo, clase, método y línea, o el componente de la interfaz gráfica.
2. **Causa raíz:** explicación técnica del defecto.
3. **Corrección sugerida:** el fragmento de código corregido.

### Script de construcción (bash)

Para Linux, macOS, Git Bash o WSL:

| Comando | Acción |
|---|---|
| `./build.sh` | Compila, ejecuta la hoja de pruebas y genera `build/calculadora-1.1.0.jar`. |
| `./build.sh probar` | Compila y ejecuta únicamente la hoja de pruebas. |
| `./build.sh ejecutar` | Abre la consola. |
| `./build.sh gui` | Abre la interfaz gráfica. |
| `./build.sh dependencias` | Descarga la apariencia FlatLaf (opcional) en `lib/`. |
| `./build.sh distribucion` | Genera además `dist/calculadora-v1.1.0.zip`. |
| `./build.sh limpiar` | Elimina `build/` y `dist/`. |

> **Nota:** `build.sh` no pudo ejecutarse en el equipo donde se preparó esta versión (Windows sin bash). Los comandos de PowerShell y de Maven de este documento sí fueron ejecutados.

## Estructura del proyecto

```text
├── pom.xml                                    Proyecto Maven (IntelliJ IDEA y VS Code)
├── build.sh                                   Script de construcción para bash
├── README.md
├── ANALISIS_ISO25010_Y_UML.md                 Resumen de calidad ISO 25010 y casos de uso
├── .vscode/                                   Configuraciones de ejecución para VS Code
├── src/
│   ├── main/java/com/calculadora/
│   │   ├── Principal.java                     Punto de entrada: consola (por defecto) o --gui
│   │   ├── Calculadora.java                   Las cinco operaciones aritméticas
│   │   ├── Operacion.java                     Enumeración de operaciones del menú
│   │   ├── Resultado.java                     Mensajes de resultado y de error
│   │   ├── InterfazConsola.java               Menú de consola (Scanner)
│   │   ├── VentanaCalculadora.java            Ventana de la interfaz gráfica
│   │   └── PanelCalculadora.java              Contenido de la interfaz gráfica
│   ├── test/java/com/calculadora/
│   │   ├── CalculadoraTest.java               Pruebas JUnit de las operaciones
│   │   └── InterfazConsolaTest.java           Pruebas JUnit de la consola
│   └── hoja-pruebas/java/com/calculadora/
│       ├── HojaDePruebas.java                 Ejecutor de los 41 casos de la hoja de pruebas
│       └── DiagnosticoFallos.java             Diagnóstico automático de fallos
└── docs/                                      Documentación del trabajo
```

## Documentación

| Documento | Contenido |
|---|---|
| [Análisis ISO 25010 y casos de uso](ANALISIS_ISO25010_Y_UML.md) | Resumen de las dimensiones de calidad que aplican y especificación de los casos de uso. |
| [01. UML y actores](docs/01_UML_Y_ACTORES.md) | Actores, casos de uso, diagrama de clases y diagrama de secuencia. |
| [Diagrama UML de casos de uso](docs/UML%20CASOS%20DE%20USO.docx) | Diagrama de casos de uso en formato Word. |
| [02. Plan de pruebas](docs/02_PLAN_DE_PRUEBAS.md) | Convenciones y tabla de los 41 casos con sus resultados. |
| [Hoja de pruebas unitarias](docs/pruebas_unitarias.xlsx) | Hoja de Excel con el resultado esperado, el obtenido y el estado de cada caso. |
| [03. Calidad ISO/IEC 25010](docs/03_CALIDAD_ISO_25010.md) | Características de calidad evaluadas, métricas y justificación de las descartadas. |
| [04. Gestión de cambios](docs/04_GESTION_DE_CAMBIOS.md) | Comunicado formal al cliente sobre la solicitud de cambio CR-001. |
| [05. Diagnóstico de fallos](docs/05_DIAGNOSTICO_DE_FALLOS.md) | Funcionamiento del módulo de diagnóstico y demostración con seis defectos. |
| [Resultados de la última ejecución](docs/resultados_pruebas.md) | Tabla generada por `HojaDePruebas`. |

## Decisiones de diseño

| Tema | Decisión |
|---|---|
| **Idioma del código** | Paquete, clases, métodos, variables y comentarios en español. Los identificadores no llevan tildes ni ñ (`Operacion`, `DIVISION_POR_CERO`) para evitar problemas de codificación. Se conservan en inglés los nombres propios de Java y sus bibliotecas (`main`, `ArithmeticException`, Swing, JUnit). |
| **Consola por defecto** | El enunciado exige entrada y salida por consola: sin argumentos se inicia la consola; la ventana solo se abre con `--gui`. |
| **División por cero** | `dividir` lanza `ArithmeticException` y `Resultado` la convierte en un mensaje claro. El programa no se interrumpe. |
| **División entera** | Trunca hacia cero: `7 / 2 = 3` y `-7 / 2 = -3`. |
| **Desbordamiento** | Se emplean `Math.addExact`, `subtractExact` y `multiplyExact` para detectar resultados fuera del rango de `int`. |
| **Potencia (CR-001)** | Ciclo `for` sin `Math.pow`. Por convención, `x^0 = 1`, incluido `0^0 = 1`. El exponente negativo no se admite porque el resultado no es entero. |
| **Lógica compartida** | La consola y la ventana usan la misma enumeración `Operacion` y los mismos mensajes de `Resultado`, sin duplicar código. |
| **Apariencia FlatLaf** | Opcional y cargada en tiempo de ejecución; sin su `.jar` la ventana usa la apariencia Nimbus. |

## Autor

**Santiago Rosero** · Trabajo de Calidad de Software
