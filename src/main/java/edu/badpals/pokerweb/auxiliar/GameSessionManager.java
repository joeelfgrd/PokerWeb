package edu.badpals.pokerweb.auxiliar;

import edu.badpals.pokerweb.model.Baraja;
import edu.badpals.pokerweb.model.Partida;

import java.util.HashMap;
import java.util.Map;

public class GameSessionManager {

    private static final Map<String, Baraja> barajasPorPartida = new HashMap<>();

    public static void iniciarPartida(Partida partida) {
        barajasPorPartida.put(partida.getId(), new Baraja());
    }

    public static Baraja getBaraja(String partidaId) {
        return barajasPorPartida.get(partidaId);
    }

    public static void finalizarPartida(String partidaId) {
        barajasPorPartida.remove(partidaId);
    }

    public static boolean existePartida(String partidaId) {
        return barajasPorPartida.containsKey(partidaId);
    }
}
