package edu.badpals.pokerweb.domain.exceptions;

public class UsuarioYaExisteException extends RuntimeException {
    /**
     * Declara un mensaje de error específico para el usuario ya existente.
     *
     * @param email El email del usuario que ya existe.
     */
    public UsuarioYaExisteException(String email) {
        super("El usuario con email " + email + " ya está registrado.");
    }
}

