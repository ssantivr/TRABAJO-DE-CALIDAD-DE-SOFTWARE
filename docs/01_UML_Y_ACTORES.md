# 1. Diagrama UML y actores

## 1.1 Actores

| Actor | Tipo | Rol |
|---|---|---|
| **Usuario** | Principal (humano) | Ingresa dos enteros `a` y `b`, elige la operación y lee el resultado o el mensaje de error, ya sea en la ventana (GUI) o en la consola. |
| **Sistema GUI (Swing)** | Secundario (sistema) | Ventana con campos, selector de operaciones y área de resultado (`VentanaCalculadora` / `PanelCalculadora`). Usa FlatLaf si está disponible. |
| **Sistema de consola** | Secundario (sistema) | Entrada estándar (`Scanner` sobre `System.in`) y salida estándar (`System.out`). |
| **Cliente / Patrocinador** | Interesado (no usa el sistema) | Aprueba los requisitos y los cambios de alcance (ver `04_GESTION_DE_CAMBIOS.md`). |

## 1.2 Casos de uso

```mermaid
flowchart LR
    U([Usuario])
    G([Sistema GUI])
    S([Sistema de consola])
    subgraph Calculadora
        UC1(Sumar)
        UC2(Restar)
        UC3(Multiplicar)
        UC4(Dividir)
        UC5(Potencia - CR-001)
        UC6(Mostrar error controlado)
    end
    U --> UC1 & UC2 & UC3 & UC4 & UC5
    UC4 -. include .-> UC6
    UC5 -. include .-> UC6
    UC1 & UC2 & UC3 & UC4 & UC5 --- G
    UC1 & UC2 & UC3 & UC4 & UC5 --- S
```

## 1.3 Diagrama de clases

`+` público · `-` privado · `~` visible en el paquete · `$` estático. `Calculadora` no tiene atributos de instancia (es **sin estado**).

```mermaid
classDiagram
    class Principal {
        -Principal()
        +main(String[] args)$ void
    }
    class Calculadora {
        +String DIVISION_POR_CERO$
        +sumar(int a, int b) int
        +restar(int a, int b) int
        +multiplicar(int a, int b) int
        +dividir(int a, int b) int
        +potencia(int base, int exponente) int
    }
    class Operacion {
        <<enumeration>>
        SUMAR
        RESTAR
        MULTIPLICAR
        DIVIDIR
        POTENCIA
        -String etiqueta
        -String simbolo
        +getEtiqueta() String
        +getSimbolo() String
        +aplicar(Calculadora calculadora, int a, int b) int
    }
    class Resultado {
        <<record>>
        +boolean exito
        +String mensaje
        +String ENTRADA_INVALIDA$
        +de(Calculadora calculadora, Operacion operacion, int a, int b)$ Resultado
        +deTexto(Calculadora calculadora, Operacion operacion, String a, String b)$ Resultado
    }
    class InterfazConsola {
        -Operacion[] OPERACIONES$
        ~ejecutar(Scanner entrada, PrintStream salida)$ void
        -imprimirMenu(PrintStream salida)$ void
        -leerEntero(Scanner entrada, PrintStream salida, String mensaje)$ Integer
    }
    class VentanaCalculadora {
        +VentanaCalculadora()
        +iniciar()$ void
        -instalarApariencia()$ void
    }
    class PanelCalculadora {
        -Calculadora calculadora
        -Operacion seleccionada
        ~JTextField campoPrimero
        ~JTextField campoSegundo
        ~Map~Operacion, JToggleButton~ botonesOperacion
        ~JTextArea areaResultado
        ~JButton botonCalcular
        ~JButton botonLimpiar
        ~calcular() void
        ~limpiar() void
        -construirEntradas() JPanel
        -construirOperaciones() JPanel
        -construirBotones() JPanel
        -construirResultado() JPanel
        -actualizarBotonesOperacion() void
    }
    class HojaDePruebas {
        -Calculadora CALCULADORA$
        -List~CasoPrueba~ CASOS$
        +main(String[] args)$ void
    }
    Principal ..> InterfazConsola : modo --consola
    Principal ..> VentanaCalculadora : modo --gui
    VentanaCalculadora *-- PanelCalculadora
    PanelCalculadora --> Calculadora
    PanelCalculadora ..> Resultado
    PanelCalculadora o-- Operacion
    InterfazConsola ..> Resultado
    InterfazConsola ..> Operacion
    Resultado ..> Operacion
    Resultado ..> Calculadora
    Operacion ..> Calculadora
    HojaDePruebas ..> Calculadora : prueba
    HojaDePruebas ..> InterfazConsola : prueba
    HojaDePruebas ..> PanelCalculadora : prueba sin pantalla
```

`Operacion` y `Resultado` evitan duplicar lógica: la consola y la GUI usan la misma enumeración de operaciones y los mismos mensajes (en español).

## 1.4 Secuencia: potencia desde la GUI

```mermaid
sequenceDiagram
    actor U as Usuario
    participant P as PanelCalculadora
    participant O as Resultado
    participant C as Calculadora
    U->>P: a=2, b=10, botón "Potencia", clic en "Calcular"
    P->>O: deTexto(calculadora, POTENCIA, "2", "10")
    O->>C: potencia(2, 10)
    loop for i = 0 .. exponente-1
        C->>C: resultado = multiplyExact(resultado, base)
    end
    C-->>O: 1024
    O-->>P: Resultado(true, "Resultado: 2 ^ 10 = 1024")
    P-->>U: muestra el mensaje en verde
```
