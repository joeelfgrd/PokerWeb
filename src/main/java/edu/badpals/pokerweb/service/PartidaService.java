package edu.badpals.pokerweb.service;

import edu.badpals.pokerweb.auxiliar.GameSessionManager;
import edu.badpals.pokerweb.model.*;
import edu.badpals.pokerweb.repository.MesaRepository;
import edu.badpals.pokerweb.repository.PartidaRepository;
import edu.badpals.pokerweb.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
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


    public Partida crearPartida(String idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Mesa mesa = new Mesa();
        mesaRepository.save(mesa);

        Partida partida = new Partida();
        partida.setMesa(mesa);

        Jugador jugador = new Jugador(usuario, mesa, partida);
        partida.setJugadores(List.of(jugador));

        partidaRepository.save(partida);
        GameSessionManager.iniciarPartida(partida);
        return partida;
    }

    @Transactional
    public List<Carta> repartirFlop(String idPartida) {
        Partida partida = partidaRepository.findById(idPartida)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));

        Baraja baraja = GameSessionManager.getBaraja(idPartida);
        List<Carta> flop = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Carta carta = baraja.repartirCarta();
            partida.getCartasComunitarias().add(carta);
            flop.add(carta);
        }

        partidaRepository.save(partida);
        return flop;
    }

    @Transactional
    public Carta repartirTurn(String idPartida) {
        Partida partida = partidaRepository.findById(idPartida)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));

        if (partida.getCartasComunitarias().size() < 3) {
            throw new RuntimeException("Primero debes repartir el flop");
        }

        Baraja baraja = GameSessionManager.getBaraja(idPartida);
        Carta turn = baraja.repartirCarta();
        partida.getCartasComunitarias().add(turn);
        partidaRepository.save(partida);

        return turn;
    }

    @Transactional
    public Carta repartirRiver(String idPartida) {
        Partida partida = partidaRepository.findById(idPartida)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));

        if (partida.getCartasComunitarias().size() < 4) {
            throw new RuntimeException("Primero debes repartir el turn");
        }

        Baraja baraja = GameSessionManager.getBaraja(idPartida);
        Carta river = baraja.repartirCarta();
        partida.getCartasComunitarias().add(river);
        partidaRepository.save(partida);

        return river;
    }

    @Transactional
    public Map<String, List<Carta>> repartirManosPrivadas(String idPartida) {
        Partida partida = partidaRepository.findById(idPartida)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));

        Baraja baraja = GameSessionManager.getBaraja(idPartida);

        if (baraja == null) {
            throw new RuntimeException("La partida no está activa o no se ha inicializado la baraja");
        }

        Map<String, List<Carta>> manosRepartidas = new HashMap<>();

        for (Jugador jugador : partida.getJugadores()) {
            List<Carta> manoJugador = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                manoJugador.add(baraja.repartirCarta());
            }

            Mano mano = new Mano(manoJugador);
            jugador.setMano(mano);
            manosRepartidas.put(jugador.getId(), manoJugador);
        }

        partidaRepository.save(partida);
        return manosRepartidas;
    }




}
