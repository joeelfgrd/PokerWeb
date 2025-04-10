package edu.badpals.pokerweb.domain.services;

import edu.badpals.pokerweb.domain.exceptions.TurnoIncorrectoException;
import edu.badpals.pokerweb.domain.model.Jugador;
import edu.badpals.pokerweb.domain.model.Partida;
import edu.badpals.pokerweb.domain.model.SidePot;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GestorApuestas {

    public void apostar(Partida partida, String idJugador, int cantidad) {
        validarTurno(partida, idJugador);
        Jugador jugador = getJugadorActivo(partida, idJugador);

        if (cantidad <= 0) {
            throw new RuntimeException("La apuesta debe ser mayor que 0.");
        }
        if (jugador.getFichas() < cantidad) {
            throw new RuntimeException("El jugador no tiene suficientes fichas para apostar.");
        }

        // Actualizar apuesta y fichas
        jugador.setFichas(jugador.getFichas() - cantidad);
        partida.setBote(partida.getBote() + cantidad);
        partida.getJugadoresQueHanActuado().add(idJugador);

        Map<String, Integer> apuestas = partida.getApuestasActuales();
        int anterior = apuestas.getOrDefault(idJugador, 0);
        apuestas.put(idJugador, anterior + cantidad);

        // Recalcular side pots con la nueva situación
        recalcularSidePots(partida);

        // Turno
        GameSessionManager.avanzarTurno(partida.getId(), partida.getJugadores());
    }

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

        // Actualizar
        jugador.setFichas(jugador.getFichas() - diferencia);
        partida.setBote(partida.getBote() + diferencia);
        partida.getJugadoresQueHanActuado().add(idJugador);

        int nuevaApuesta = apuestaJugador + diferencia;
        apuestas.put(idJugador, nuevaApuesta);

        // Recalcular side pots
        recalcularSidePots(partida);

        // Turno
        GameSessionManager.avanzarTurno(partida.getId(), partida.getJugadores());
    }

    public void pasar(Partida partida, String idJugador) {
        validarTurno(partida, idJugador);
        getJugadorActivo(partida, idJugador); // Validación

        Map<String, Integer> apuestas = partida.getApuestasActuales();
        int maxApuesta = apuestas.values().stream().max(Integer::compareTo).orElse(0);
        int apuestaJugador = apuestas.getOrDefault(idJugador, 0);

        if (apuestaJugador < maxApuesta) {
            throw new RuntimeException("No puedes pasar, hay una apuesta activa.");
        }

        partida.getJugadoresQueHanActuado().add(idJugador);
        // Sin cambio en side pots (nadie subió)
        GameSessionManager.avanzarTurno(partida.getId(), partida.getJugadores());
    }

    public void retirarse(Partida partida, String idJugador) {
        // Opcional: permitir retirarse fuera de turno
        // o forzar que sea en turno con validarTurno(partida, idJugador);

        Jugador jugador = getJugadorActivo(partida, idJugador);

        int apuesta = partida.getApuestasActuales().getOrDefault(idJugador, 0);
        partida.setBote(partida.getBote() + apuesta);
        partida.getApuestasActuales().remove(idJugador);

        jugador.setActivo(false);
        jugador.setMano(null);

        partida.getJugadoresQueHanActuado().add(idJugador);

        // Recalcular side pots, por si su retirada reduce participantes
        recalcularSidePots(partida);

        GameSessionManager.avanzarTurno(partida.getId(), partida.getJugadores());
    }

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

        partida.setBote(partida.getBote() + fichas);

        // Recalcular side pots con la nueva aportación
        recalcularSidePots(partida);

        GameSessionManager.avanzarTurno(partida.getId(), partida.getJugadores());
    }

    // ============================================================
    // Métodos auxiliares
    // ============================================================

    private void validarTurno(Partida partida, String idJugador) {
        String idEnTurno = GameSessionManager.getJugadorEnTurno(partida.getId(), partida.getJugadores());
        if (!idEnTurno.equals(idJugador)) {
            throw new TurnoIncorrectoException(idJugador);
        }
    }

    private Jugador getJugadorActivo(Partida partida, String idJugador) {
        return partida.getJugadores().stream()
                .filter(j -> j.getId().equals(idJugador) && j.isActivo())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado o no activo."));
    }

    /**
     * Cálculo real de side pots según las aportaciones finales de cada jugador.
     * Se basa en la lógica típica de "ordenar por aportación ascendente" y crear pots parciales.
     */
    private void recalcularSidePots(Partida partida) {
        // Limpiar side pots actuales
        partida.getSidePots().clear();

        // Tomamos solo jugadores que han apostado algo
        // y que no se han retirado (aunque allIn cuenta).
        Map<String, Integer> aportes = new HashMap<>();
        for (Map.Entry<String, Integer> e : partida.getApuestasActuales().entrySet()) {
            if (e.getValue() > 0) {
                aportes.put(e.getKey(), e.getValue());
            }
        }
        if (aportes.isEmpty()) return;

        // Ordenar asc por contribución
        List<Map.Entry<String, Integer>> ordenados = new ArrayList<>(aportes.entrySet());
        ordenados.sort(Comparator.comparingInt(Map.Entry::getValue));

        int aportePrevio = 0;
        // Recorremos las distintas contribuciones
        for (int i = 0; i < ordenados.size(); i++) {
            int aporteActual = ordenados.get(i).getValue();
            if (aporteActual <= aportePrevio) continue; // ya cubierto

            // ¿Quién participa en este pot?
            // Todos los que tienen una aportación >= aporteActual
            List<String> participantes = new ArrayList<>();
            for (Map.Entry<String, Integer> x : ordenados) {
                if (x.getValue() >= aporteActual) {
                    participantes.add(x.getKey());
                }
            }

            // El importe parcial es la diferencia * #participantes
            int diff = aporteActual - aportePrevio;
            int potSize = diff * participantes.size();

            SidePot pot = new SidePot(potSize);
            for (String pid : participantes) {
                pot.añadirParticipante(pid);
            }
            partida.getSidePots().add(pot);

            aportePrevio = aporteActual;
        }
    }

    // Solo para testing
    public void avanzarTurnoManual(Partida partida) {
        GameSessionManager.avanzarTurno(partida.getId(), partida.getJugadores());
    }
}
