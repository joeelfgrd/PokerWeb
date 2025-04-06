package edu.badpals.pokerweb.domain.exceptions;

public class PasswordIncorrectaException extends RuntimeException {
    public PasswordIncorrectaException() {
        super("La contraseña proporcionada es incorrecta.");
    }
}
