package edu.badpals.pokerweb.domain.exceptions;

public class TurnoIncorrectoException extends RuntimeException {
    /**
     * Declara un mensaje de error específico para el turno incorrecto.
     *
     * @param idJugador El ID del jugador que no tiene el turno.
     */
    public TurnoIncorrectoException(String idJugador) {
        super("No es el turno del jugador con ID: " + idJugador);
    }
}
