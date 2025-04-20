package edu.badpals.pokerweb.application.service;

import edu.badpals.pokerweb.application.dtos.ResultadoShowdownDTO;
import edu.badpals.pokerweb.domain.enums.FaseJuego;
import edu.badpals.pokerweb.domain.exceptions.*;
import edu.badpals.pokerweb.domain.model.*;
import edu.badpals.pokerweb.domain.services.EvaluadorManos;
import edu.badpals.pokerweb.domain.services.GameSessionManager;
import edu.badpals.pokerweb.domain.services.ManoEvaluada;
import edu.badpals.pokerweb.infraestructure.persistence.repository.PartidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static edu.badpals.pokerweb.domain.services.EvaluadorManos.determinarGanadoresEntre;

@Service
public class GestorShowdown {

    @Autowired
    private PartidaRepository partidaRepository;

    /**
     * Resuelve el showdown de la partida y determina al ganador.
     *
     * @param idPartida ID de la partida.
     * @return El resultado del showdown.
     */
    @Transactional
    public ResultadoShowdownDTO resolver(String idPartida) {

        Partida partida = obtenerPartida(idPartida);

        verificarFase(idPartida);

        List<Jugador> activos = obtenerJugadoresActivos(partida);

        int boteTotalEntregado = 0;
        Jugador ganadorPrincipal = null;
        ManoEvaluada mejorMano = null;

        boteTotalEntregado += procesarSidePots(partida, activos, ganadorPrincipal, mejorMano);

        boteTotalEntregado += procesarBotePrincipal(partida, activos, ganadorPrincipal, mejorMano);

        ganadorPrincipal = obtenerGanadorPrincipalSeguridad(activos, ganadorPrincipal);

        limpiarPartida(partida);

        return crearResultadoShowdownDTO(ganadorPrincipal, boteTotalEntregado, partida);
    }

    /**
     * Verifica que la fase de la partida sea SHOWDOWN.
     *
     * @param idPartida ID de la partida.
     */
    private void verificarFase(String idPartida) {
        if (GameSessionManager.getFase(idPartida) != FaseJuego.SHOWDOWN) {
            throw new FaseIncorrectaException(GameSessionManager.getFase(idPartida).name());
        }
    }

    /**
     * Obtiene la lista de jugadores activos en la partida.
     *
     * @param partida La partida en curso.
     * @return Lista de jugadores activos.
     */
    private List<Jugador> obtenerJugadoresActivos(Partida partida) {
        List<Jugador> activos = new ArrayList<>();
        for (Jugador jugador : partida.getJugadores()) {
            if (jugador.isActivo()) {
                activos.add(jugador);
            }
        }
        return activos;
    }

    /**
     * Procesa los side pots de la partida y determina al ganador de cada uno.
     *
     * @param partida        La partida en curso.
     * @param activos        Lista de jugadores activos.
     * @param ganadorPrincipal El ganador principal.
     * @param mejorMano      La mejor mano hasta el momento.
     * @return El total entregado en los side pots.
     */
    private int procesarSidePots(Partida partida, List<Jugador> activos, Jugador ganadorPrincipal, ManoEvaluada mejorMano) {
        int boteTotalEntregado = 0;
        for (SidePot pot : partida.getSidePots()) {
            List<Jugador> cand = obtenerJugadoresEnSidePot(activos, pot);

            if (cand.isEmpty()) continue;

            List<Jugador> winners = determinarGanadoresEntre(cand, partida.getCartasComunitarias());
            boteTotalEntregado += repartirGanancias(winners, pot.getCantidad());

            for (Jugador w : winners) {
                ManoEvaluada eval = EvaluadorManos.evaluar(combinarCartas(w, partida));
                if (mejorMano == null || eval.compareTo(mejorMano) > 0) {
                    mejorMano = eval;
                    ganadorPrincipal = w;
                }
            }
        }
        return boteTotalEntregado;
    }

    /**
     * Obtiene la lista de jugadores que participan en un side pot.
     *
     * @param activos La lista de jugadores activos.
     * @param pot     El side pot en curso.
     * @return Lista de jugadores participantes en el side pot.
     */
    private List<Jugador> obtenerJugadoresEnSidePot(List<Jugador> activos, SidePot pot) {
        List<Jugador> participantes = new ArrayList<>();
        for (Jugador jugador : activos) {
            if (pot.getParticipantes().contains(jugador.getId())) {
                participantes.add(jugador);
            }
        }
        return participantes;
    }

    /**
     * Procesa el bote principal de la partida y determina al ganador.
     *
     * @param partida        La partida en curso.
     * @param activos        Lista de jugadores activos.
     * @param ganadorPrincipal El ganador principal.
     * @param mejorMano      La mejor mano hasta el momento.
     * @return El total entregado en el bote principal.
     */
    private int procesarBotePrincipal(Partida partida, List<Jugador> activos, Jugador ganadorPrincipal, ManoEvaluada mejorMano) {
        int boteTotalEntregado = 0;
        if (partida.getBote() > 0) {
            List<Jugador> winners = determinarGanadoresEntre(activos, partida.getCartasComunitarias());

            if (winners.isEmpty()) winners = new ArrayList<>(activos);

            boteTotalEntregado += repartirGanancias(winners, partida.getBote());

            for (Jugador w : winners) {
                ManoEvaluada eval = EvaluadorManos.evaluar(combinarCartas(w, partida));
                if (mejorMano == null || eval.compareTo(mejorMano) > 0) {
                    mejorMano = eval;
                    ganadorPrincipal = w;
                }
            }
            partida.setIdGanador(winners.size() == 1 ? winners.get(0).getId() : null);
        }
        return boteTotalEntregado;
    }

    /**
     * Asigna un ganador principal si aún no se ha asignado uno.
     *
     * @param activos        La lista de jugadores activos.
     * @param ganadorPrincipal El ganador principal.
     * @return El ganador principal.
     */
    private Jugador obtenerGanadorPrincipalSeguridad(List<Jugador> activos, Jugador ganadorPrincipal) {
        if (ganadorPrincipal == null && !activos.isEmpty()) {
            ganadorPrincipal = activos.get(0);
        }
        return ganadorPrincipal;
    }

    /**
     * Limpia los side pots y el bote de la partida y guarda los cambios en la base de datos.
     *
     * @param partida La partida en curso.
     */
    private void limpiarPartida(Partida partida) {
        partida.setBote(0);
        partida.getSidePots().clear();
        partidaRepository.save(partida);
    }

    /**
     * Crea un objeto ResultadoShowdownDTO con los detalles del ganador y el bote entregado.
     *
     * @param ganadorPrincipal El ganador principal.
     * @param boteTotalEntregado El total entregado en el bote.
     * @param partida La partida en curso.
     * @return El objeto ResultadoShowdownDTO.
     */
    private ResultadoShowdownDTO crearResultadoShowdownDTO(Jugador ganadorPrincipal, int boteTotalEntregado, Partida partida) {
        return new ResultadoShowdownDTO(
                ganadorPrincipal.getUsuario().getNombre(),
                ganadorPrincipal.getMano().getCartas(),
                ganadorPrincipal.getFichas(),
                boteTotalEntregado,
                new ArrayList<>(partida.getCartasComunitarias())
        );
    }

    /**
     * Reparte las ganancias entre los jugadores ganadores.
     *
     * @param ganadores Lista de jugadores ganadores.
     * @param cantidad  Cantidad a repartir.
     * @return El total entregado a los ganadores.
     */
    private int repartirGanancias(List<Jugador> ganadores, int cantidad) {
        if (ganadores.isEmpty()) return 0;

        int base = cantidad / ganadores.size();
        int resto = cantidad % ganadores.size();
        int total = 0;

        for (int i = 0; i < ganadores.size(); i++) {
            int ganancia = base + ((i == 0 && resto > 0) ? resto : 0);
            ganadores.get(i).setFichas(ganadores.get(i).getFichas() + ganancia);
            total += ganancia;
        }
        return total;
    }

    /**
     * Combina las cartas del jugador con las cartas comunitarias.
     *
     * @param jugador El jugador cuyas cartas se combinarán.
     * @param partida La partida que contiene las cartas comunitarias.
     * @return Una lista de cartas combinadas.
     */
    private List<Carta> combinarCartas(Jugador jugador, Partida partida) {
        List<Carta> total = new ArrayList<>();
        total.addAll(jugador.getMano().getCartas());
        total.addAll(partida.getCartasComunitarias());
        return total;
    }

    /**
     * Obtiene la partida por su ID.
     *
     * @param idPartida ID de la partida.
     * @return La partida correspondiente.
     */
    private Partida obtenerPartida(String idPartida) {
        return partidaRepository.findById(idPartida).orElseThrow(() -> new PartidaNoEncontradaException(idPartida));
    }
}
