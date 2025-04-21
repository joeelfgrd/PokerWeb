package edu.badpals.pokerweb.domain.exceptions;

public class GanadorIndeterminadoException extends RuntimeException {
    /**
     * Declara un mensaje de error específico para el ganador indeterminado.
     *
     * @param idPartida El ID de la partida.
     */
    public GanadorIndeterminadoException(String idPartida) {
        super("No se pudo determinar un ganador para la partida con ID " + idPartida + ".");
    }
}
