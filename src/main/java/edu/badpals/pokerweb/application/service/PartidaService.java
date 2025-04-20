package edu.badpals.pokerweb.application.service;

import edu.badpals.pokerweb.domain.enums.FaseJuego;
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

    /**
     * Permite a un usuario unirse a una partida existente.
     *
     * @param idPartida ID de la partida.
     * @param idUsuario ID del usuario que se unirá.
     * @return La partida actualizada.
     */
    public Partida unirseAPartida(String idPartida, String idUsuario) {
        return gestorPartidas.unirseAPartida(idPartida, idUsuario);
    }

    /**
     * Inicia una nueva mano en la partida.
     *
     * @param idPartida ID de la partida.
     * @return La partida con la nueva mano iniciada.
     */
    @Transactional
    public Partida iniciarNuevaMano(String idPartida) {
        return gestorManos.iniciarNuevaMano(idPartida);
    }

    /**
     * Reparte las cartas privadas a los jugadores en la partida.
     *
     * @param idPartida ID de la partida.
     * @return Un mapa con el ID del jugador como clave y sus cartas privadas como valor.
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
        avanzarFaseSiCorresponde(idPartida);
        resolverSiTodosAllIn(idPartida);
        return partida;
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
        avanzarFaseSiCorresponde(idPartida);
        resolverSiTodosAllIn(idPartida);
        return partida;
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
        avanzarFaseSiCorresponde(idPartida);
        resolverSiTodosAllIn(idPartida);
        return partida;
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
        avanzarFaseSiCorresponde(idPartida);
        resolverSiTodosAllIn(idPartida);
        return partida;
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
        avanzarFaseSiCorresponde(idPartida);
        resolverSiTodosAllIn(idPartida);
        return partida;
    }


    /**
     * Avanza la fase de la partida si corresponde.
     *
     * @param idPartida ID de la partida.
     */
    @Transactional
    public void avanzarFaseSiCorresponde(String idPartida) {
        gestorFases.avanzarFaseSiCorresponde(idPartida);
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
        List<EstadoJugadorDTO> estadoJugadores = obtenerEstadoJugadores(partida);
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
     * Verifica si todos los jugadores están all-in y avanza la fase si es necesario.
     *
     * @param idPartida ID de la partida.
     */
    private void resolverSiTodosAllIn(String idPartida) {
        Partida partida = obtenerPartida(idPartida);

        boolean todosAllIn = partida.getJugadores().stream()
                .filter(Jugador::isActivo)
                .allMatch(Jugador::isAllIn);
        // Aqui treparto flop/turn/river automáticamente
        if (todosAllIn && GameSessionManager.getFase(idPartida) != FaseJuego.SHOWDOWN) {
            while (GameSessionManager.getFase(idPartida) != FaseJuego.SHOWDOWN) {
                avanzarFaseSiCorresponde(idPartida);
            }
        }

        if (GameSessionManager.getFase(idPartida) == FaseJuego.SHOWDOWN) {
            ResultadoShowdownDTO resultado = resolverShowdown(idPartida);
            // Aquí puedo emitir el mensaje o dejar que lo haga WebSocketHandler
        }
    }

    /**
     * Obtiene la lista de jugadores y sus respectivos estados.
     *
     * @param partida La partida en curso.
     * @return Lista de los estados de los jugadores.
     */
    private List<EstadoJugadorDTO> obtenerEstadoJugadores(Partida partida) {
        List<EstadoJugadorDTO> estadoJugadores = new ArrayList<>();

        for (Jugador jugador : partida.getJugadores()) {
            List<Carta> mano = jugador.getMano() != null ? jugador.getMano().getCartas() : null;

            EstadoJugadorDTO estadoJugador = new EstadoJugadorDTO(
                    jugador.getUsuario().getNombre(),
                    jugador.getFichas(),
                    jugador.isActivo(),
                    jugador.isAllIn(),
                    mano
            );

            estadoJugadores.add(estadoJugador);
        }

        return estadoJugadores;
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
