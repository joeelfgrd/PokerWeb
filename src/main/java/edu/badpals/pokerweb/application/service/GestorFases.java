package edu.badpals.pokerweb.application.service;

import edu.badpals.pokerweb.domain.enums.FaseJuego;
import edu.badpals.pokerweb.domain.exceptions.PartidaNoEncontradaException;
import edu.badpals.pokerweb.domain.model.Baraja;
import edu.badpals.pokerweb.domain.model.Carta;
import edu.badpals.pokerweb.domain.model.Jugador;
import edu.badpals.pokerweb.domain.model.Partida;
import edu.badpals.pokerweb.domain.services.GameSessionManager;
import edu.badpals.pokerweb.infraestructure.persistence.repository.PartidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class GestorFases {

    @Autowired
    private PartidaRepository partidaRepository;

    /**
     * Verifica si la ronda de apuestas ha finalizado.
     *
     * @param partida La partida actual.
     * @return true si la ronda ha terminado.
     */
    public boolean rondaDeApuestasFinalizada(Partida partida) {
        Map<String, Integer> apuestas = partida.getApuestasActuales();
        int apuestaMaxima = apuestas.values().stream().mapToInt(Integer::intValue).max().orElse(0);

        for (Jugador jugador : partida.getJugadores()) {
            if (jugador.isActivo() && !jugador.isAllIn()) {
                int apuestaJugador = apuestas.getOrDefault(jugador.getId(), 0);

                // Si no ha actuado y no ha igualado la máxima, la ronda no ha terminado
                if (!partida.getJugadoresQueHanActuado().contains(jugador.getId())) {
                    if (apuestaMaxima == 0) {
                        return false;
                    } else if (apuestaJugador < apuestaMaxima) {
                        return false;
                    }
                }

            }
        }
        System.out.println("✅ Apuestas: " + apuestas);
        System.out.println("✅ Han actuado: " + partida.getJugadoresQueHanActuado());
        System.out.println("✅ Apuesta máxima: " + apuestaMaxima);
        return true;
    }


    /**
     * Avanza la fase del juego si corresponde.
     *
     * @param idPartida ID de la partida.
     * @return La partida actualizada tras avanzar de fase.
     */
    @Transactional
    public Partida avanzarFaseSiCorresponde(String idPartida) {
        Partida partida = obtenerPartida(idPartida);

        if (!rondaDeApuestasFinalizada(partida)) {
            return partida;
        }

        partida.getApuestasActuales().clear();

        boolean todosAllIn = partida.getJugadores().stream()
                .filter(Jugador::isActivo)
                .allMatch(Jugador::isAllIn);

        if (todosAllIn) {
            FaseJuego fase = GameSessionManager.getFase(idPartida);
            Baraja baraja = GameSessionManager.getBaraja(idPartida);

            if (fase == FaseJuego.PREFLOP) {
                baraja.repartirCarta();
                for (int i = 0; i < 3; i++) {
                    partida.getCartasComunitarias().add(baraja.repartirCarta());
                }
            }
            if (fase == FaseJuego.PREFLOP || fase == FaseJuego.FLOP) {
                baraja.repartirCarta();
                partida.getCartasComunitarias().add(baraja.repartirCarta());
            }
            if (fase == FaseJuego.PREFLOP || fase == FaseJuego.FLOP || fase == FaseJuego.TURN) {
                baraja.repartirCarta();
                partida.getCartasComunitarias().add(baraja.repartirCarta());
            }

            GameSessionManager.forzarFase(idPartida, FaseJuego.SHOWDOWN);
        } else {
            GameSessionManager.avanzarFase(idPartida);
            FaseJuego nuevaFase = GameSessionManager.getFase(idPartida);
            Baraja baraja = GameSessionManager.getBaraja(idPartida);

            switch (nuevaFase) {
                case FLOP -> {
                    baraja.repartirCarta();
                    for (int i = 0; i < 3; i++) {
                        partida.getCartasComunitarias().add(baraja.repartirCarta());
                    }
                }
                case TURN -> {
                    baraja.repartirCarta();
                    partida.getCartasComunitarias().add(baraja.repartirCarta());
                }
                case RIVER -> {
                    baraja.repartirCarta();
                    partida.getCartasComunitarias().add(baraja.repartirCarta());
                }
                case SHOWDOWN -> {
                    // Lo gestiona otro servicio
                }
            }
        }

        partida.getJugadoresQueHanActuado().clear();
        partidaRepository.save(partida);
        return partida;
    }





    private Partida obtenerPartida(String idPartida) {
        return partidaRepository.findById(idPartida)
                .orElseThrow(() -> new PartidaNoEncontradaException(idPartida));
    }
}
