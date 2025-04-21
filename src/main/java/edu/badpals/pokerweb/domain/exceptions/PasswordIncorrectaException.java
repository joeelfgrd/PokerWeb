package edu.badpals.pokerweb.domain.exceptions;

public class PasswordIncorrectaException extends RuntimeException {
    /**
     * Declara un mensaje de error específico para la contraseña incorrecta.
     */
    public PasswordIncorrectaException() {
        super("La contraseña proporcionada es incorrecta.");
    }
}
