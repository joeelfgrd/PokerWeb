package edu.badpals.pokerweb.domain.services;

import edu.badpals.pokerweb.domain.model.Baraja;
import edu.badpals.pokerweb.domain.model.Jugador;
import edu.badpals.pokerweb.domain.model.Partida;
import edu.badpals.pokerweb.domain.enums.FaseJuego;

import java.util.*;

public class GameSessionManager {

    private static final Map<String, Baraja> barajaPartida = new HashMap<>();
    private static final Map<String, Integer> turnoJugadoresPartida = new HashMap<>();
    private static final Map<String, FaseJuego> fasePartida = new HashMap<>();
    private static final Map<String, Integer> dealerIndexPorPartida = new HashMap<>();

    public static void iniciarPartida(Partida partida) {
        barajaPartida.put(partida.getId(), new Baraja());
        turnoJugadoresPartida.put(partida.getId(), 0);
        fasePartida.put(partida.getId(), FaseJuego.PREFLOP);
        dealerIndexPorPartida.put(partida.getId(), 0);
    }

    public static void avanzarDealer(String partidaId, List<Jugador> jugadores) {
        int actual = dealerIndexPorPartida.getOrDefault(partidaId, 0);
        int siguiente = (actual + 1) % jugadores.size();
        dealerIndexPorPartida.put(partidaId, siguiente);
    }

    public static int getDealerIndex(String partidaId) {
        return dealerIndexPorPartida.getOrDefault(partidaId, 0);
    }


    public static Baraja getBaraja(String partidaId) {
        return barajaPartida.get(partidaId);
    }

    public static boolean existePartida(String partidaId) {
        return barajaPartida.containsKey(partidaId);
    }

    public static void finalizarPartida(String partidaId) {
        barajaPartida.remove(partidaId);
        turnoJugadoresPartida.remove(partidaId);
        fasePartida.remove(partidaId);
    }

    public static int getTurnoActual(String partidaId) {
        return turnoJugadoresPartida.getOrDefault(partidaId, 0);
    }

    public static void avanzarTurno(String partidaId, List<Jugador> jugadores) {
        if (jugadores.isEmpty()) return;
        int turno = turnoJugadoresPartida.getOrDefault(partidaId, 0);
        int total = jugadores.size();

        for (int i = 0; i < total; i++) {
            turno = (turno + 1) % total;
            Jugador cand = jugadores.get(turno);
            if (cand.isActivo() && !cand.isAllIn()) {
                turnoJugadoresPartida.put(partidaId, turno);
                return;
            }
        }
        // si todos inactivos o allin no cambia nada
    }







    public static FaseJuego getFase(String partidaId) {
        return fasePartida.getOrDefault(partidaId, FaseJuego.PREFLOP);
    }

    public static void avanzarFase(String partidaId) {
        FaseJuego actual = getFase(partidaId);
        fasePartida.put(partidaId, actual.siguiente());
        turnoJugadoresPartida.put(partidaId, 0);
    }

    public static void reiniciarFaseYBaraja(String partidaId, List<Jugador> jugadores) {
        barajaPartida.put(partidaId, new Baraja());
        fasePartida.put(partidaId, FaseJuego.PREFLOP);
        avanzarDealer(partidaId, jugadores);
        int nuevoTurno = (getDealerIndex(partidaId) + 1) % jugadores.size();
        turnoJugadoresPartida.put(partidaId, nuevoTurno);
    }
    public static void setFase(String partidaId, FaseJuego nuevaFase) {
        fasePartida.put(partidaId, nuevaFase);
        turnoJugadoresPartida.put(partidaId, 0); // Reinicia turno
    }


    public static String getJugadorEnTurno(String partidaId, List<Jugador> jugadores) {
        if (jugadores.isEmpty()) throw new IllegalStateException("No hay jugadores");

        int turno = turnoJugadoresPartida.getOrDefault(partidaId, 0);
        int total = jugadores.size();

        for (int i = 0; i < total; i++) {
            Jugador cand = jugadores.get((turno + i) % total);
            if (cand.isActivo() && !cand.isAllIn()) {
                int idxReal = (turno + i) % total;
                turnoJugadoresPartida.put(partidaId, idxReal);
                return cand.getId();
            }
        }
        throw new IllegalStateException("Todos inactivos o all-in");
    }

    public static void forzarFase(String partidaId, FaseJuego nuevaFase) {
        fasePartida.put(partidaId, nuevaFase);
        turnoJugadoresPartida.put(partidaId, 0);
    }


    public static void forzarTurnoPorJugador(String partidaId, String idJugador, List<Jugador> jugadores) {
        for (int i = 0; i < jugadores.size(); i++) {
            if (jugadores.get(i).getId().equals(idJugador)) {
                turnoJugadoresPartida.put(partidaId, i);
                return;
            }
        }
        throw new IllegalArgumentException("Jugador no encontrado en la lista");
    }

}