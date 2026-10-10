# Trabajo de Calidad de Software: calculadora de enteros en Java

Aplicación de consola que recibe dos números enteros y calcula operaciones aritméticas básicas, con validación de datos y manejo explícito de la división por cero.

## Requisitos

JDK 17 o superior (`java -version`, `javac -version`).

## Compilar y ejecutar (PowerShell, desde la raíz del proyecto)

```powershell
javac --release 17 -encoding UTF-8 -d build\classes (Get-ChildItem -Recurse src\main -Filter *.java).FullName
java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build\classes com.calculadora.Principal
```

## Pruebas unitarias (JUnit 5)

`src/test` contiene las pruebas de `Calculadora` (las cinco operaciones, división por cero, exponente negativo y desbordamiento) y de la consola con entrada simulada. Después de compilar:

```powershell
# Una sola vez: JUnit 5 standalone
Invoke-WebRequest "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.11.4/junit-platform-console-standalone-1.11.4.jar" -OutFile junit.jar

javac --release 17 -encoding UTF-8 -cp "build\classes;junit.jar" -d build\test-classes (Get-ChildItem -Recurse src\test -Filter *.java).FullName
java "-Dfile.encoding=UTF-8" -jar junit.jar execute -cp "build\classes;build\test-classes" --scan-classpath build\test-classes --details=tree
```
