package edu.badpals.pokerweb.domain.exceptions;

public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(String email) {
        super("No se encontró ningún usuario con el email: " + email);
    }
}