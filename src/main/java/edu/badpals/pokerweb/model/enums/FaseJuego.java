package edu.badpals.pokerweb.model.enums;

public enum FaseJuego {
    PREFLOP,
    FLOP,
    TURN,
    RIVER,
    SHOWDOWN;

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
