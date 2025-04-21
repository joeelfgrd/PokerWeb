package edu.badpals.pokerweb.domain.enums;
/**
 * Enum que representa las diferentes fases del juego de poker.
 * Las fases son: PREFLOP, FLOP, TURN, RIVER y SHOWDOWN.
 */
public enum FaseJuego {

    PREFLOP,
    FLOP,
    TURN,
    RIVER,
    SHOWDOWN;
    /**
     * Devuelve la siguiente fase del juego.
     *
     * @return La siguiente fase del juego.
     */
    public FaseJuego siguiente() {
        FaseJuego[] fases = FaseJuego.values();
        for (int i = 0; i < fases.length - 1; i++) {
            if (fases[i] == this) {
                return fases[i + 1];
            }
        }
        return this;
    }
}
