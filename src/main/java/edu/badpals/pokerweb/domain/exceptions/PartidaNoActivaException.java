package edu.badpals.pokerweb.domain.exceptions;

public class PartidaNoActivaException extends RuntimeException {
    /**
     * Declara un mensaje de error específico para la partida no activa.
     *
     * @param idPartida El ID de la partida.
     */
    public PartidaNoActivaException(String idPartida) {
        super("La partida con ID " + idPartida + " no está activa o no se ha inicializado correctamente.");
    }
}
