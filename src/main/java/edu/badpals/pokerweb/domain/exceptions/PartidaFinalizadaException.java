package edu.badpals.pokerweb.domain.exceptions;

public class PartidaFinalizadaException extends RuntimeException {
    /**
     * Declara un mensaje de error específico para la partida finalizada.
     *
     * @param idPartida El ID de la partida.
     */
  public PartidaFinalizadaException(String idPartida) {
    super("La partida con ID " + idPartida + " ha terminado. No hay suficientes jugadores con fichas.");
  }
}
