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

    @Autowired
    private GestorPartidas gestorPartidas;

    @Autowired
    private GestorManos gestorManos;

    @Transactional
    public ResultadoShowdownDTO resolver(String idPartida) {
        Partida partida = obtenerPartida(idPartida);

        if (GameSessionManager.getFase(idPartida) != FaseJuego.SHOWDOWN) {
            throw new FaseIncorrectaException(GameSessionManager.getFase(idPartida).name());
        }

        List<Jugador> jugadoresActivos = partida.getJugadores().stream()
                .filter(Jugador::isActivo)
                .toList();

        List<SidePot> sidePots = partida.getSidePots();
        int totalGanado = 0;
        Jugador ganadorPrincipal = null;
        ManoEvaluada mejorManoPrincipal = null;

        for (SidePot sidePot : sidePots) {
            List<Jugador> candidatos = jugadoresActivos.stream()
                    .filter(j -> sidePot.getParticipantes().contains(j.getId()))
                    .toList();

            List<Jugador> ganadores = determinarGanadoresEntre(candidatos, partida.getCartasComunitarias());
            totalGanado += repartirGanancias(ganadores, sidePot.getCantidad());

            for (Jugador g : ganadores) {
                ManoEvaluada mano = EvaluadorManos.evaluar(combinarCartas(g, partida));
                if (mejorManoPrincipal == null || mano.compareTo(mejorManoPrincipal) > 0) {
                    mejorManoPrincipal = mano;
                    ganadorPrincipal = g;
                }
            }
        }

        // Bote principal
        if (partida.getBote() > 0) {
            List<Jugador> ganadores = determinarGanadoresEntre(jugadoresActivos, partida.getCartasComunitarias());
            totalGanado += repartirGanancias(ganadores, partida.getBote());

            for (Jugador g : ganadores) {
                ManoEvaluada mano = EvaluadorManos.evaluar(combinarCartas(g, partida));
                if (mejorManoPrincipal == null || mano.compareTo(mejorManoPrincipal) > 0) {
                    mejorManoPrincipal = mano;
                    ganadorPrincipal = g;
                }
            }

            partida.setIdGanador(ganadores.size() == 1 ? ganadores.get(0).getId() : null);
        } else if (ganadorPrincipal != null) {
            partida.setIdGanador(ganadorPrincipal.getId());
        } else {
            throw new GanadorIndeterminadoException(idPartida);
        }

        partida.setBote(0);
        partida.getSidePots().clear();
        partidaRepository.save(partida);

        ResultadoShowdownDTO resultado = new ResultadoShowdownDTO(
                ganadorPrincipal != null ? ganadorPrincipal.getUsuario().getNombre() : "Empate",
                ganadorPrincipal != null ? ganadorPrincipal.getMano().getCartas() : new ArrayList<>(),
                ganadorPrincipal != null ? ganadorPrincipal.getFichas() : 0,
                totalGanado,
                new ArrayList<>(partida.getCartasComunitarias())
        );

        gestorManos.iniciarNuevaMano(idPartida);
        return resultado;
    }

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

    private List<Carta> combinarCartas(Jugador jugador, Partida partida) {
        List<Carta> total = new ArrayList<>(jugador.getMano().getCartas());
        total.addAll(partida.getCartasComunitarias());
        return total;
    }

    private Partida obtenerPartida(String id) {
        return partidaRepository.findById(id).orElseThrow(() -> new PartidaNoEncontradaException(id));
    }
}
