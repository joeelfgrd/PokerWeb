package edu.badpals.pokerweb.domain.enums;
/**
 * Enum que representa los diferentes palos de una baraja de cartas.
 * Los palos son: CORAZONES, DIAMANTES, TREBOLES y PICAS.
 */
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

