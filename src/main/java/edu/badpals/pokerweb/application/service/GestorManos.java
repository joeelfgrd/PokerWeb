package edu.badpals.pokerweb.application.service;

import edu.badpals.pokerweb.domain.exceptions.PartidaFinalizadaException;
import edu.badpals.pokerweb.domain.exceptions.PartidaNoActivaException;
import edu.badpals.pokerweb.domain.exceptions.PartidaNoEncontradaException;
import edu.badpals.pokerweb.domain.exceptions.PartidaSinJugadoresException;
import edu.badpals.pokerweb.domain.model.*;
import edu.badpals.pokerweb.domain.services.GameSessionManager;
import edu.badpals.pokerweb.infraestructure.persistence.repository.PartidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GestorManos {

    @Autowired
    PartidaRepository partidaRepository;

    @Transactional
    public Map<String, List<Carta>> repartirManosPrivadas(String idPartida) {
        Partida partida = obtenerPartida(idPartida);

        Baraja baraja = GameSessionManager.getBaraja(idPartida);
        if (baraja == null) {
            throw new PartidaNoActivaException(idPartida);
        }

        if (partida.getJugadores() == null || partida.getJugadores().isEmpty()) {
            throw new PartidaSinJugadoresException(idPartida);
        }

        Map<String, List<Carta>> manosRepartidas = new HashMap<>();

        for (Jugador jugador : partida.getJugadores()) {
            List<Carta> manoJugador = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                manoJugador.add(baraja.repartirCarta());
            }

            Mano mano = new Mano(manoJugador);
            jugador.setMano(mano);
            if (jugador.getUsuario() != null) {
                String idUsuario = jugador.getUsuario().getId();
                manosRepartidas.put(idUsuario, manoJugador);
            }
        }

        partidaRepository.save(partida);
        return manosRepartidas;
    }

    @Transactional
    public Partida iniciarNuevaMano(String idPartida) {
        Partida partida = obtenerPartida(idPartida);

        int jugadoresConFichas = 0;
        for (Jugador jugador : partida.getJugadores()) {
            if (jugador.getFichas() > 0) {
                jugadoresConFichas++;
            }
        }

        if (jugadoresConFichas < 2) {
            throw new PartidaFinalizadaException(idPartida);
        }

        // Reiniciar baraja, fase y avanzar dealer
        GameSessionManager.reiniciarFaseYBaraja(idPartida, partida.getJugadores());
        Baraja baraja = GameSessionManager.getBaraja(idPartida);

        // Limpiar estado de la partida
        partida.getApuestasActuales().clear();
        partida.getCartasComunitarias().clear();
        partida.setBote(0);
        partida.getJugadoresQueHanActuado().clear();

        // Repartir nuevas manos y reactivar jugadores con fichas
        for (Jugador jugador : partida.getJugadores()) {
            if (jugador.getFichas() > 0) {
                jugador.setActivo(true);
                jugador.setAllIn(false);
                List<Carta> nuevasCartas = new ArrayList<>();
                nuevasCartas.add(baraja.repartirCarta());
                nuevasCartas.add(baraja.repartirCarta());
                jugador.setMano(new Mano(nuevasCartas));
            } else {
                jugador.setActivo(false);
                jugador.setAllIn(false);
                jugador.setMano(null);
            }
        }

        // Aplicar ciegas
        List<Jugador> jugadores = partida.getJugadores();
        int dealer = GameSessionManager.getDealerIndex(idPartida);
        int sbIndex = (dealer + 1) % jugadores.size();
        int bbIndex = (dealer + 2) % jugadores.size();

        Jugador smallBlind = jugadores.get(sbIndex);
        Jugador bigBlind = jugadores.get(bbIndex);

        smallBlind.setFichas(smallBlind.getFichas() - 10);
        bigBlind.setFichas(bigBlind.getFichas() - 20);

        partida.getApuestasActuales().put(smallBlind.getId(), 10);
        partida.getApuestasActuales().put(bigBlind.getId(), 20);
        partida.setBote(30);

        // Guardar y devolver
        partidaRepository.save(partida);
        return partida;
    }


    private Partida obtenerPartida(String idPartida) {
        return partidaRepository.findById(idPartida)
                .orElseThrow(() -> new PartidaNoEncontradaException(idPartida));
    }
}
