package com.example;

public class MaxLength extends Exception {

    public MaxLength(String tipoDato, String valor, int tamMax) {
        super("No puedes introducir un " + tipoDato + " de tamaño superior a " + tamMax + ". Valor actual: '" + valor + "'");
    }

    // Método estático para validar la longitud de una cadena
    public static void verificar(String valor, String tipoDato, int tamMax) throws MaxLength {
        if (valor != null && valor.length() > tamMax) {
            throw new MaxLength(tipoDato, valor, tamMax);
        }
    }
}
