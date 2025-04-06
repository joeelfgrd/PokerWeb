package edu.badpals.pokerweb.domain.exceptions;

public class UsuarioBloqueadoException extends RuntimeException {
    public UsuarioBloqueadoException(String email) {
        super("El usuario con email " + email + " está bloqueado.");
    }
}
