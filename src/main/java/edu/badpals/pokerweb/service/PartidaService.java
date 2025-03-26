package edu.badpals.pokerweb.service;

import edu.badpals.pokerweb.model.Jugador;
import edu.badpals.pokerweb.model.Mesa;
import edu.badpals.pokerweb.model.Partida;
import edu.badpals.pokerweb.model.Usuario;
import edu.badpals.pokerweb.repository.MesaRepository;
import edu.badpals.pokerweb.repository.PartidaRepository;
import edu.badpals.pokerweb.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

        return partida;
    }
}
