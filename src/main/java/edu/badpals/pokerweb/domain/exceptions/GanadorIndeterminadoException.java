package edu.badpals.pokerweb.domain.exceptions;

public class GanadorIndeterminadoException extends RuntimeException {
    public GanadorIndeterminadoException(String idPartida) {
        super("No se pudo determinar un ganador para la partida con ID " + idPartida + ".");
    }
}
