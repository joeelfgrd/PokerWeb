package edu.badpals.pokerweb.domain.exceptions;

public class FaseIncorrectaException extends RuntimeException {
    public FaseIncorrectaException(String faseActual) {
        super("No se puede resolver el showdown si no se está en la fase SHOWDOWN (fase actual: " + faseActual + ").");
    }
}
