# Trabajo de Calidad de Software: calculadora de enteros en Java

Aplicación de consola que recibe dos números enteros y calcula operaciones aritméticas básicas, con validación de datos y manejo explícito de la división por cero.

## Requisitos

JDK 17 o superior (`java -version`, `javac -version`).

## Compilar y ejecutar (PowerShell, desde la raíz del proyecto)

```powershell
javac --release 17 -encoding UTF-8 -d build\classes (Get-ChildItem -Recurse src\main -Filter *.java).FullName
java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp build\classes com.calculadora.Principal
```
