package edu.badpals.pokerweb.application.service;

import edu.badpals.pokerweb.domain.exceptions.*;
import edu.badpals.pokerweb.domain.services.GameSessionManager;
import edu.badpals.pokerweb.domain.services.GestorApuestas;
import edu.badpals.pokerweb.application.dtos.EstadoJugadorDTO;
import edu.badpals.pokerweb.application.dtos.EstadoPartidaDTO;
import edu.badpals.pokerweb.application.dtos.ResultadoShowdownDTO;
import edu.badpals.pokerweb.domain.model.*;
import edu.badpals.pokerweb.infraestructure.persistence.repository.MesaRepository;
import edu.badpals.pokerweb.infraestructure.persistence.repository.PartidaRepository;
import edu.badpals.pokerweb.infraestructure.persistence.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PartidaService {

    @Autowired
    private PartidaRepository partidaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private GestorApuestas gestorApuestas;

    @Autowired
    private GestorShowdown gestorShowdown;

    @Autowired
    private GestorPartidas gestorPartidas;

    @Autowired
    private GestorManos gestorManos;

    @Autowired
    private GestorFases gestorFases;


    /**
     * Crea una nueva partida y la asocia a un usuario.
     *
     * @param idUsuario ID del usuario que crea la partida.
     * @return La partida creada.
     */
    public Partida crearPartida(String idUsuario) {
        return gestorPartidas.crearPartida(idUsuario);
    }

    public Partida unirseAPartida(String idPartida, String idUsuario) {
        return gestorPartidas.unirseAPartida(idPartida, idUsuario);
    }

    @Transactional
    public Partida iniciarNuevaMano(String idPartida) {
        return gestorManos.iniciarNuevaMano(idPartida);
    }

    /**
     * Reparte cartas a los jugadores de la partida.
     *
     * @param idPartida ID de la partida.
     * @return Un mapa con el ID del jugador como clave y su mano como valor.
     */
    @Transactional
    public Map<String, List<Carta>> repartirManosPrivadas(String idPartida) {
        return gestorManos.repartirManosPrivadas(idPartida);
    }

    /**
     * Realiza una apuesta en la partida.
     *
     * @param idPartida ID de la partida.
     * @param idJugador ID del jugador que realiza la apuesta.
     * @param cantidad  Cantidad a apostar.
     * @return La partida actualizada.
     */
    @Transactional
    public Partida apostar(String idPartida, String idJugador, int cantidad) {
        Partida partida = obtenerPartida(idPartida);
        gestorApuestas.apostar(partida, idJugador, cantidad);
        partidaRepository.save(partida);
        return avanzarFaseSiCorresponde(idPartida);
    }

    /**
     * Realiza una apuesta de igualar en la partida.
     *
     * @param idPartida ID de la partida.
     * @param idJugador ID del jugador que iguala.
     * @return La partida actualizada.
     */
    @Transactional
    public Partida igualar(String idPartida, String idJugador) {
        Partida partida = obtenerPartida(idPartida);
        gestorApuestas.igualar(partida, idJugador);
        partidaRepository.save(partida);
        return avanzarFaseSiCorresponde(idPartida);
    }


    /**
     * Realiza una acción de pasar en la partida.
     *
     * @param idPartida ID de la partida.
     * @param idJugador ID del jugador que pasa.
     * @return La partida actualizada.
     */
    @Transactional
    public Partida pasar(String idPartida, String idJugador) {
        Partida partida = obtenerPartida(idPartida);
        gestorApuestas.pasar(partida, idJugador);
        partidaRepository.save(partida);
        return avanzarFaseSiCorresponde(idPartida);
    }

    /**
     * Realiza una acción de retirarse en la partida.
     *
     * @param idPartida ID de la partida.
     * @param idJugador ID del jugador que se retira.
     * @return La partida actualizada.
     */
    @Transactional
    public Partida retirarse(String idPartida, String idJugador) {
        Partida partida = obtenerPartida(idPartida);
        gestorApuestas.retirarse(partida, idJugador);
        partidaRepository.save(partida);
        return avanzarFaseSiCorresponde(idPartida);
    }


    /**
     * Realiza una acción de all-in en la partida.
     *
     * @param idPartida ID de la partida.
     * @param idJugador ID del jugador que hace all-in.
     * @return La partida actualizada.
     */
    @Transactional
    public Partida allIn(String idPartida, String idJugador) {
        Partida partida = obtenerPartida(idPartida);
        gestorApuestas.allIn(partida, idJugador);
        partidaRepository.save(partida);
        return avanzarFaseSiCorresponde(idPartida);
    }


    /**
     * Avanza la fase de la partida si corresponde.
     *
     * @param idPartida ID de la partida.
     * @return La partida actualizada.
     */
    @Transactional
    public Partida avanzarFaseSiCorresponde(String idPartida) {
        return gestorFases.avanzarFaseSiCorresponde(idPartida);
    }

    /**
     * Realiza el showdown de la partida.
     *
     * @param idPartida ID de la partida.
     * @return El resultado del showdown.
     */
    @Transactional
    public ResultadoShowdownDTO resolverShowdown(String idPartida) {
        return gestorShowdown.resolver(idPartida);
    }

    /**
     * Obtiene el estado de la partida.
     *
     * @param idPartida ID de la partida.
     * @return El estado de la partida.
     */
    @Transactional
    public EstadoPartidaDTO obtenerEstadoPartida(String idPartida) {
        Partida partida = obtenerPartida(idPartida);

        List<EstadoJugadorDTO> estadoJugadores = new ArrayList<>();

        for (Jugador jugador : partida.getJugadores()) {
            List<Carta> mano = null;
            if (jugador.getMano() != null) {
                mano = jugador.getMano().getCartas();
            }

            EstadoJugadorDTO estadoJugador = new EstadoJugadorDTO(
                    jugador.getUsuario().getNombre(),
                    jugador.getFichas(),
                    jugador.isActivo(),
                    jugador.isAllIn(),
                    mano
            );

            estadoJugadores.add(estadoJugador);
        }
        String idJugadorTurno = GameSessionManager.getJugadorEnTurno(idPartida, partida.getJugadores());

        return new EstadoPartidaDTO(
                GameSessionManager.getFase(idPartida),
                partida.getBote(),
                partida.getCartasComunitarias(),
                estadoJugadores,
                idJugadorTurno
        );
    }
    /**
     * Obtiene la partida por su ID.
     *
     * @param idPartida ID de la partida.
     * @return La partida correspondiente.
     */
    private Partida obtenerPartida(String idPartida) {
        return partidaRepository.findById(idPartida)
                .orElseThrow(() -> new PartidaNoEncontradaException(idPartida));
    }

}
