package edu.badpals.pokerweb.domain.exceptions;

public class MaximoJugadoresException extends RuntimeException {
    public MaximoJugadoresException(String idPartida) {
        super("La partida con ID " + idPartida + " ya tiene el máximo de 10 jugadores.");
    }
}
