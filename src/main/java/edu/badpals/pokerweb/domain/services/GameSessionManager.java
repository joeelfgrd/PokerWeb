package edu.badpals.pokerweb.domain.services;

import edu.badpals.pokerweb.domain.model.Baraja;
import edu.badpals.pokerweb.domain.model.Partida;
import edu.badpals.pokerweb.domain.enums.FaseJuego;

import java.util.*;

public class GameSessionManager {

    private static final Map<String, Baraja> barajaPartida = new HashMap<>();
    private static final Map<String, Integer> turnoJugadoresPartida = new HashMap<>();
    private static final Map<String, FaseJuego> fasePartida = new HashMap<>();

    public static void iniciarPartida(Partida partida) {
        barajaPartida.put(partida.getId(), new Baraja());
        turnoJugadoresPartida.put(partida.getId(), 0);
        fasePartida.put(partida.getId(), FaseJuego.PREFLOP);
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

    public static void avanzarTurno(String partidaId, int totalJugadores) {
        int turno = turnoJugadoresPartida.getOrDefault(partidaId, 0);
        turno = turno + 1;
        if (turno >= totalJugadores) {
            turno = 0;
        }
        turnoJugadoresPartida.put(partidaId, turno);
    }

    public static FaseJuego getFase(String partidaId) {
        return fasePartida.getOrDefault(partidaId, FaseJuego.PREFLOP);
    }

    public static void avanzarFase(String partidaId) {
        FaseJuego actual = getFase(partidaId);
        fasePartida.put(partidaId, actual.siguiente());
        turnoJugadoresPartida.put(partidaId, 0);
    }

    public static void reiniciarFaseYBaraja(String partidaId) {
        barajaPartida.put(partidaId, new Baraja());
        fasePartida.put(partidaId, FaseJuego.PREFLOP);
        turnoJugadoresPartida.put(partidaId, 0);
    }
}