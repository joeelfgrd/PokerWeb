package edu.badpals.pokerweb.infraestructure.auxiliar;

import edu.badpals.pokerweb.domain.model.*;
import edu.badpals.pokerweb.domain.model.enums.FaseJuego;

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

    public static void iniciarNuevaMano(Partida partida) {
        String partidaId = partida.getId();
        Baraja baraja = new Baraja();
        barajaPartida.put(partidaId, baraja);

        partida.getCartasComunitarias().clear();

        for (Jugador jugador : partida.getJugadores()) {
            if (jugador.isActivo() && jugador.getFichas() > 0) {
                List<Carta> cartas = new ArrayList<>();
                cartas.add(baraja.repartirCarta());
                cartas.add(baraja.repartirCarta());
                jugador.setMano(new Mano(cartas));
            } else {
                jugador.setMano(null);
                jugador.setActivo(false);
            }
        }
        turnoJugadoresPartida.put(partidaId, 0);
        fasePartida.put(partidaId, FaseJuego.PREFLOP);
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
}
