package edu.badpals.pokerweb.domain.exceptions;

public class JugadorYaUnidoException extends RuntimeException {
    public JugadorYaUnidoException(String idUsuario, String idPartida) {
        super("El usuario con ID " + idUsuario + " ya está unido a la partida con ID " + idPartida + ".");
    }
}
