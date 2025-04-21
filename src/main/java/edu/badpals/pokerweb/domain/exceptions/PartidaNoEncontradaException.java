package edu.badpals.pokerweb.domain.exceptions;

public class PartidaNoEncontradaException extends RuntimeException {
    /**
     * Declara un mensaje de error específico para la partida no encontrada.
     *
     * @param idPartida El ID de la partida.
     */
    public PartidaNoEncontradaException(String idPartida) {
        super("No se encontró ninguna partida con el id: " + idPartida);
    }
}
