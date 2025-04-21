package edu.badpals.pokerweb.domain.exceptions;

public class UsuarioBloqueadoException extends RuntimeException {
    /**
     * Declara un mensaje de error específico para el usuario bloqueado.
     *
     * @param email El email del usuario bloqueado.
     */
    public UsuarioBloqueadoException(String email) {
        super("El usuario con email " + email + " está bloqueado.");
    }
}
