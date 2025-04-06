package edu.badpals.pokerweb.domain.exceptions;

public class PartidaNoActivaException extends RuntimeException {
    public PartidaNoActivaException(String idPartida) {
        super("La partida con ID " + idPartida + " no está activa o no se ha inicializado correctamente.");
    }
}
