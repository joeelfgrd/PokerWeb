package edu.badpals.pokerweb.domain.exceptions;

public class PartidaNoEncontradaException extends RuntimeException {
    public PartidaNoEncontradaException(String idPartida) {
        super("No se encontró ninguna partida con el id: " + idPartida);
    }
}
