package edu.badpals.pokerweb.domain.exceptions;

public class TurnoIncorrectoException extends RuntimeException {
    public TurnoIncorrectoException(String idJugador) {
        super("No es el turno del jugador con ID: " + idJugador);
    }
}
