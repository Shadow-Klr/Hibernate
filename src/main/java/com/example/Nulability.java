package com.example;

public class Nulability extends Exception {

    public Nulability(String tipoDato, String valor) {
        super("No puedes introducir un " + tipoDato + " nulo");
    }

    public static void verifyNulability(String valor, String tipoDato) throws Nulability {
        if (valor == null || valor.trim().isEmpty()) {
            throw new Nulability(tipoDato, valor);
        }
    }
}
