package edu.badpals.pokerweb.application.service;

import edu.badpals.pokerweb.domain.enums.FaseJuego;
import edu.badpals.pokerweb.domain.exceptions.*;
import edu.badpals.pokerweb.domain.model.*;
import edu.badpals.pokerweb.domain.services.GameSessionManager;
import edu.badpals.pokerweb.infraestructure.persistence.repository.MesaRepository;
import edu.badpals.pokerweb.infraestructure.persistence.repository.PartidaRepository;
import edu.badpals.pokerweb.infraestructure.persistence.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GestorPartidas {

    @Autowired
    PartidaRepository partidaRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    MesaRepository mesaRepository;

    public Partida crearPartida(String idUsuario) {
        Usuario usuario = getUsuario(idUsuario);

        Mesa mesa = new Mesa();
        mesaRepository.save(mesa);

        Partida partida = new Partida();
        partida.setMesa(mesa);

        String codigo = generarCodigoInvitacion();
        partida.setCodigoInvitacion(codigo);

        Jugador jugador = new Jugador(usuario, mesa, partida);
        partida.setJugadores(List.of(jugador));

        partidaRepository.save(partida);
        GameSessionManager.iniciarPartida(partida);
        return partida;
    }

    @Transactional
    public Partida unirseAPartida(String codigoInvitacion, String idUsuario) {
        Partida partida = partidaRepository.findByCodigoInvitacion(codigoInvitacion)
                .orElseThrow(() -> new PartidaNoEncontradaException(
                        "No existe partida con código: " + codigoInvitacion));

        Usuario usuario = getUsuario(idUsuario);

        for (Jugador jugador : partida.getJugadores()) {
            if (jugador.getUsuario().getId().equals(idUsuario)) {
                throw new JugadorYaUnidoException(idUsuario, partida.getId());
            }
        }

        if (partida.getJugadores().size() >= 10) {
            throw new MaximoJugadoresException(partida.getId());
        }

        Mesa mesa = partida.getMesa();
        Jugador nuevoJugador = new Jugador(usuario, mesa, partida);
        partida.getJugadores().add(nuevoJugador);

        partidaRepository.save(partida);
        return partida;
    }

    private Usuario getUsuario(String idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException(idUsuario));
    }

    private Partida obtenerPartida(String idPartida) {
        return partidaRepository.findById(idPartida)
                .orElseThrow(() -> new PartidaNoEncontradaException(idPartida));
    }

    private String generarCodigoInvitacion() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb;
        do {
            sb = new StringBuilder();
            for (int i = 0; i < 6; i++) { // 6 o 8
                sb.append(chars.charAt(new Random().nextInt(chars.length())));
            }
            // hasta asegurarte que no duplique
        } while (partidaRepository.existsByCodigoInvitacion(sb.toString()));

        return sb.toString();
    }
}
