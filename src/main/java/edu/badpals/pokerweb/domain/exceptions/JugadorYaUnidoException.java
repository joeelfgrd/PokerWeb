package edu.badpals.pokerweb.domain.exceptions;

public class JugadorYaUnidoException extends RuntimeException {
    /**
     * Declara un mensaje de error específico para el jugador que ya está unido a la partida.
     *
     * @param idUsuario  El ID del usuario.
     * @param idPartida  El ID de la partida.
     */
    public JugadorYaUnidoException(String idUsuario, String idPartida) {
        super("El usuario con ID " + idUsuario + " ya está unido a la partida con ID " + idPartida + ".");
    }
}
