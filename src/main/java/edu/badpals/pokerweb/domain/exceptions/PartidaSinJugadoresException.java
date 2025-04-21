package edu.badpals.pokerweb.domain.exceptions;

public class PartidaSinJugadoresException extends RuntimeException {
    /**
     * Declara un mensaje de error específico para la partida sin jugadores.
     *
     * @param idPartida El ID de la partida.
     */
    public PartidaSinJugadoresException(String idPartida) {
        super("La partida con ID " + idPartida + " no tiene jugadores.");
    }
}
