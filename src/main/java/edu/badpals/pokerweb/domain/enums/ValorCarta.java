package edu.badpals.pokerweb.domain.enums;

public enum ValorCarta {
    DOS("2", 2),
    TRES("3", 3),
    CUATRO("4", 4),
    CINCO("5", 5),
    SEIS("6", 6),
    SIETE("7", 7),
    OCHO("8", 8),
    NUEVE("9", 9),
    DIEZ("10", 10),
    J("J", 11),
    Q("Q", 12),
    K("K", 13),
    A("A", 14);

    private final String valor;
    private final int numero;

    ValorCarta(String valor, int numero) {
        this.valor = valor;
        this.numero = numero;
    }

    public String getValor() {
        return valor;
    }

    public int getNumero() {
        return numero;
    }

    public String getValorString() {
        return valor;
    }
}
