package edu.badpals.pokerweb.domain.exceptions;

public class PartidaSinJugadoresException extends RuntimeException {
    public PartidaSinJugadoresException(String idPartida) {
        super("La partida con ID " + idPartida + " no tiene jugadores.");
    }
}
