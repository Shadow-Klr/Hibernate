package com.example;

public class NegativeInt extends Exception {

    public NegativeInt(String tipoDato, double valor) {
        super("No puedes introducir un " + tipoDato + " negativo: " + valor);
    }

    public static void verificar(double valor, String tipoDato) throws NegativeInt {
        if (valor < 0) {
            throw new NegativeInt(tipoDato, valor);
        }
    }
}
