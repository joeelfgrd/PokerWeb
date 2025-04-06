package edu.badpals.pokerweb.domain.exceptions;

public class PartidaFinalizadaException extends RuntimeException {
  public PartidaFinalizadaException(String idPartida) {
    super("La partida con ID " + idPartida + " ha terminado. No hay suficientes jugadores con fichas.");
  }
}
