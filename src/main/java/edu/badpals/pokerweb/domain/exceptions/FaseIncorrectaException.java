package edu.badpals.pokerweb.domain.exceptions;

public class FaseIncorrectaException extends RuntimeException {
    /**
     * Declara un mensaje de error específico para la fase incorrecta.
     *
     * @param faseActual La fase actual del juego.
     */
    public FaseIncorrectaException(String faseActual) {
        super("No se puede resolver el showdown si no se está en la fase SHOWDOWN (fase actual: " + faseActual + ").");
    }
}
