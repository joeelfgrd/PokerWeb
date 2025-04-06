package edu.badpals.pokerweb.domain.exceptions;

public class PartidaNoEncontradaException extends RuntimeException {
    public PartidaNoEncontradaException(String idPartida) {
        super("No se encontró ningún usuario con el email: " + idPartida);
    }
}
