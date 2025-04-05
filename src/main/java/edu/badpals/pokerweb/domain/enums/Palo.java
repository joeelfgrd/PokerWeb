package edu.badpals.pokerweb.domain.enums;

public enum Palo {
    CORAZONES(0),
    DIAMANTES(1),
    TREBOLES(2),
    PICAS(3);

    private final int id;

    Palo(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

