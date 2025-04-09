package edu.badpals.pokerweb.domain.services;

import edu.badpals.pokerweb.domain.exceptions.TurnoIncorrectoException;
import edu.badpals.pokerweb.domain.model.Jugador;
import edu.badpals.pokerweb.domain.model.Partida;
import edu.badpals.pokerweb.domain.model.SidePot;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GestorApuestas {
    /**
     * Realiza una apuesta en la partida.
     *
     * @param partida   La partida en la que se realiza la apuesta.
     * @param idJugador El ID del jugador que realiza la apuesta.
     * @param cantidad  La cantidad a apostar.
     */
    public void apostar(Partida partida, String idJugador, int cantidad) {
        validarTurno(partida, idJugador);
        Jugador jugador = getJugadorActivo(partida, idJugador);

        if (cantidad <= 0) {
            throw new RuntimeException("La apuesta debe ser mayor que 0.");
        }
        if (jugador.getFichas() < cantidad) {
            throw new RuntimeException("El jugador no tiene suficientes fichas para apostar.");
        }

        jugador.setFichas(jugador.getFichas() - cantidad);
        partida.setBote(partida.getBote() + cantidad);
        partida.getJugadoresQueHanActuado().add(idJugador);

        Map<String, Integer> apuestas = partida.getApuestasActuales();
        apuestas.put(idJugador, apuestas.getOrDefault(idJugador, 0) + cantidad);

        GameSessionManager.avanzarTurno(partida.getId(), partida.getJugadores());
    }
    /**
     * Igualar la apuesta actual.
     *
     * @param partida   La partida en la que se iguala la apuesta.
     * @param idJugador El ID del jugador que iguala.
     */
    public void igualar(Partida partida, String idJugador) {
        validarTurno(partida, idJugador);
        Jugador jugador = getJugadorActivo(partida, idJugador);

        Map<String, Integer> apuestas = partida.getApuestasActuales();
        int maxApuesta = apuestas.values().stream().max(Integer::compareTo).orElse(0);
        int apuestaJugador = apuestas.getOrDefault(idJugador, 0);
        int diferencia = maxApuesta - apuestaJugador;

        if (diferencia > jugador.getFichas()) {
            throw new RuntimeException("No puedes igualar, debes hacer all-in.");
        }

        jugador.setFichas(jugador.getFichas() - diferencia);
        partida.setBote(partida.getBote() + diferencia);
        partida.getJugadoresQueHanActuado().add(idJugador);
        apuestas.put(idJugador, apuestaJugador + diferencia);

        GameSessionManager.avanzarTurno(partida.getId(), partida.getJugadores());
    }
    /**
     * Pasar la acción.
     *
     * @param partida   La partida en la que se pasa.
     * @param idJugador El ID del jugador que pasa.
     */
    public void pasar(Partida partida, String idJugador) {
        validarTurno(partida, idJugador);
        getJugadorActivo(partida, idJugador); // validación

        Map<String, Integer> apuestas = partida.getApuestasActuales();
        int maxApuesta = apuestas.values().stream().max(Integer::compareTo).orElse(0);
        int apuestaJugador = apuestas.getOrDefault(idJugador, 0);

        if (apuestaJugador < maxApuesta) {
            throw new RuntimeException("No puedes pasar, hay una apuesta activa.");
        }

        partida.getJugadoresQueHanActuado().add(idJugador);
        GameSessionManager.avanzarTurno(partida.getId(), partida.getJugadores());
    }
    /**
     * Retirarse de la partida.
     *
     * @param partida   La partida en la que se retira el jugador.
     * @param idJugador El ID del jugador que se retira.
     */
    public void retirarse(Partida partida, String idJugador) {
        validarTurno(partida, idJugador);
        Jugador jugador = getJugadorActivo(partida, idJugador);

        int apuesta = partida.getApuestasActuales().getOrDefault(idJugador, 0);
        partida.setBote(partida.getBote() + apuesta);
        partida.getApuestasActuales().remove(idJugador);

        jugador.setActivo(false);
        jugador.setMano(null);

        partida.getJugadoresQueHanActuado().add(idJugador);
        GameSessionManager.avanzarTurno(partida.getId(), partida.getJugadores());
    }
    /**
     * Realiza una acción de all-in en la partida.
     *
     * @param partida   La partida en la que se realiza el all-in.
     * @param idJugador El ID del jugador que hace all-in.
     */
    public void allIn(Partida partida, String idJugador) {
        validarTurno(partida, idJugador);
        Jugador jugador = getJugadorActivo(partida, idJugador);
        int fichas = jugador.getFichas();

        if (fichas <= 0) {
            throw new RuntimeException("No tienes fichas para hacer all-in.");
        }

        jugador.setFichas(0);
        jugador.setAllIn(true);

        Map<String, Integer> apuestas = partida.getApuestasActuales();
        int totalApuesta = apuestas.getOrDefault(idJugador, 0) + fichas;
        apuestas.put(idJugador, totalApuesta);
        partida.getJugadoresQueHanActuado().add(idJugador);

        actualizarSidePots(partida, idJugador, totalApuesta);
        GameSessionManager.avanzarTurno(partida.getId(), partida.getJugadores());

    }
    /**
     * Valida que el jugador en turno sea el correcto.
     *
     * @param partida   La partida en la que se realiza la acción.
     * @param idJugador El ID del jugador que realiza la acción.
     */
    private void validarTurno(Partida partida, String idJugador) {
        String idEnTurno = GameSessionManager.getJugadorEnTurno(partida.getId(), partida.getJugadores());
        if (!idEnTurno.equals(idJugador)) {
            throw new TurnoIncorrectoException(idJugador);
        }
    }
    /**
     * Obtiene el jugador activo en la partida.
     *
     * @param partida   La partida en la que se busca el jugador.
     * @param idJugador El ID del jugador a buscar.
     * @return El jugador activo.
     */
    private Jugador getJugadorActivo(Partida partida, String idJugador) {
        return partida.getJugadores().stream()
                .filter(j -> j.getId().equals(idJugador) && j.isActivo())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado o no activo."));
    }

    /**
     * Actualiza los side pots de la partida.
     *
     * @param partida    La partida en la que se actualizan los side pots.
     * @param idJugador  El ID del jugador que realiza la acción.
     * @param totalApuesta La cantidad total apostada por el jugador.
     */
    private void actualizarSidePots(Partida partida, String idJugador, int totalApuesta) {
        List<SidePot> sidePots = partida.getSidePots();

        if (sidePots.isEmpty()) {
            SidePot nuevoPot = new SidePot(totalApuesta);
            nuevoPot.añadirParticipante(idJugador);
            partida.getSidePots().add(nuevoPot);
            return;
        }

        int acumulado = sidePots.stream().mapToInt(SidePot::getCantidad).sum();
        int diferencia = totalApuesta - acumulado;

        if (diferencia > 0) {
            SidePot nuevoPot = new SidePot(diferencia);
            nuevoPot.añadirParticipante(idJugador);
            partida.getSidePots().add(nuevoPot);
        }
    }
    // Solo para testing
    public void avanzarTurnoManual(Partida partida) {
        GameSessionManager.avanzarTurno(partida.getId(), partida.getJugadores());

    }

}
