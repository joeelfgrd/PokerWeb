package edu.badpals.pokerweb.domain.exceptions;

public class UsuarioYaExisteException extends RuntimeException {
    public UsuarioYaExisteException(String email) {
        super("El usuario con email " + email + " ya está registrado.");
    }
}

