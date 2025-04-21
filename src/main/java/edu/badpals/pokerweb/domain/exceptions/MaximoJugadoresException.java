package edu.badpals.pokerweb.domain.exceptions;

public class MaximoJugadoresException extends RuntimeException {
    /**
     * Declara un mensaje de error específico para el máximo de jugadores alcanzado.
     *
     * @param idPartida El ID de la partida.
     */
    public MaximoJugadoresException(String idPartida) {
        super("La partida con ID " + idPartida + " ya tiene el máximo de 10 jugadores.");
    }
}
