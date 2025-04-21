package edu.badpals.pokerweb.domain.exceptions;

public class UsuarioNoEncontradoException extends RuntimeException {
    /**
     * Declara un mensaje de error específico para el usuario no encontrado.
     *
     * @param email El email del usuario que no se encontró.
     */
    public UsuarioNoEncontradoException(String email) {
        super("No se encontró ningún usuario con el email: " + email);
    }
}