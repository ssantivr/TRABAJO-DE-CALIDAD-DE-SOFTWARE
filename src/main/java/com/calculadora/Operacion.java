package com.calculadora;

/** Operaciones que ofrecen ambas interfaces, con la etiqueta y el símbolo que ve el usuario. */
public enum Operacion {
    SUMAR("Sumar", "+"),
    RESTAR("Restar", "-"),
    MULTIPLICAR("Multiplicar", "*"),
    DIVIDIR("Dividir", "/"),
    POTENCIA("Potencia", "^");

    private final String etiqueta;
    private final String simbolo;

    Operacion(String etiqueta, String simbolo) {
        this.etiqueta = etiqueta;
        this.simbolo = simbolo;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getSimbolo() {
        return simbolo;
    }

    /** Ejecuta esta operación; puede lanzar lo mismo que el método correspondiente de {@link Calculadora}. */
    public int aplicar(Calculadora calculadora, int a, int b) {
        return switch (this) {
            case SUMAR -> calculadora.sumar(a, b);
            case RESTAR -> calculadora.restar(a, b);
            case MULTIPLICAR -> calculadora.multiplicar(a, b);
            case DIVIDIR -> calculadora.dividir(a, b);
            case POTENCIA -> calculadora.potencia(a, b);
        };
    }
}
