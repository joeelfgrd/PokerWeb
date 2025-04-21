package edu.badpals.pokerweb.application.service;

import edu.badpals.pokerweb.domain.enums.FaseJuego;
import edu.badpals.pokerweb.domain.exceptions.PartidaNoEncontradaException;
import edu.badpals.pokerweb.domain.model.Baraja;
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
        int apuestaMaxima = obtenerApuestaMaxima(apuestas);

        for (Jugador jugador : partida.getJugadores()) {
            if (jugador.isActivo() && !jugador.isAllIn()) {
                int apuestaJugador = apuestas.getOrDefault(jugador.getId(), 0);

                if (!jugadorHaActuado(partida, jugador) && apuestaJugador < apuestaMaxima) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
        * Obtiene la apuesta máxima de los jugadores.
        *
        * @param apuestas Mapa de apuestas de los jugadores.
        * @return La apuesta máxima.
        */
    private int obtenerApuestaMaxima(Map<String, Integer> apuestas) {
        int apuestaMaxima = 0;
        for (int apuesta : apuestas.values()) {
            if (apuesta > apuestaMaxima) {
                apuestaMaxima = apuesta;
            }
        }
        return apuestaMaxima;
    }

    /**
     * Verifica si un jugador ha actuado en la ronda actual.
     *
     * @param partida La partida actual.
     * @param jugador El jugador a verificar.
     * @return true si el jugador ha actuado.
     */
    private boolean jugadorHaActuado(Partida partida, Jugador jugador) {
        return partida.getJugadoresQueHanActuado().contains(jugador.getId());
    }

    /**
     * Avanza la fase del juego si corresponde.
     *
     * @param idPartida ID de la partida.
     */
    @Transactional
    public void avanzarFaseSiCorresponde(String idPartida) {
        Partida partida = obtenerPartida(idPartida);

        if (!rondaDeApuestasFinalizada(partida)) {
            return;
        }

        limpiarApuestas(partida);

        if (todosJugadoresEstanAllIn(partida)) {
            manejarFaseShowdown(idPartida, partida);
        } else {
            avanzarFaseNormal(idPartida, partida);
        }

        partida.getJugadoresQueHanActuado().clear();
        partidaRepository.save(partida);
    }

    /**
     * Resuelve la situación cuando todos los jugadores están all-in.
     *
     * @param partida La partida actual.
     */
    private void limpiarApuestas(Partida partida) {
        partida.getApuestasActuales().clear();
    }


    /**
     * Verifica si todos los jugadores están all-in.
     *
     * @param partida La partida actual.
     * @return true si todos los jugadores están all-in.
     */
    private boolean todosJugadoresEstanAllIn(Partida partida) {
        for (Jugador jugador : partida.getJugadores()) {
            if (jugador.isActivo() && !jugador.isAllIn()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Maneja la fase de showdown.
     *
     * @param idPartida ID de la partida.
     * @param partida   La partida actual.
     */
    private void manejarFaseShowdown(String idPartida, Partida partida) {
        FaseJuego fase = GameSessionManager.getFase(idPartida);
        Baraja baraja = GameSessionManager.getBaraja(idPartida);

        if (fase == FaseJuego.PREFLOP) {
            repartirCartas(baraja, partida, 3);
        }
        if (fase == FaseJuego.PREFLOP || fase == FaseJuego.FLOP) {
            repartirCartas(baraja, partida, 1);
        }
        if (fase == FaseJuego.PREFLOP || fase == FaseJuego.FLOP || fase == FaseJuego.TURN) {
            repartirCartas(baraja, partida, 1);
        }

        GameSessionManager.forzarFase(idPartida, FaseJuego.SHOWDOWN);
    }

    /**
     * Reparte cartas a la partida.
     *
     * @param baraja   La baraja de cartas.
     * @param partida  La partida actual.
     * @param cantidad La cantidad de cartas a repartir.
     */
    private void repartirCartas(Baraja baraja, Partida partida, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            partida.getCartasComunitarias().add(baraja.repartirCarta());
        }
    }

    /**
     * Avanza la fase normal del juego.
     *
     * @param idPartida ID de la partida.
     * @param partida   La partida actual.
     */
    private void avanzarFaseNormal(String idPartida, Partida partida) {
        GameSessionManager.avanzarFase(idPartida);
        FaseJuego nuevaFase = GameSessionManager.getFase(idPartida);
        Baraja baraja = GameSessionManager.getBaraja(idPartida);

        switch (nuevaFase) {
            case FLOP:
                repartirCartas(baraja, partida, 3);
                break;
            case TURN:
            case RIVER:
                repartirCartas(baraja, partida, 1);
                break;
        }
    }
    /**
     * Obtiene la partida por su ID.
     *
     * @param idPartida ID de la partida.
     * @return La partida correspondiente.
     * @throws PartidaNoEncontradaException Si no se encuentra la partida.
     */
    private Partida obtenerPartida(String idPartida) {
        return partidaRepository.findById(idPartida)
                .orElseThrow(() -> new PartidaNoEncontradaException(idPartida));
    }
}

