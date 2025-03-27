package edu.badpals.pokerweb.application.service;

import edu.badpals.pokerweb.infraestructure.auxiliar.GameSessionManager;
import edu.badpals.pokerweb.domain.model.*;
import edu.badpals.pokerweb.domain.model.enums.FaseJuego;
import edu.badpals.pokerweb.domain.repository.MesaRepository;
import edu.badpals.pokerweb.domain.repository.PartidaRepository;
import edu.badpals.pokerweb.domain.repository.UsuarioRepository;
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

        baraja.repartirCarta();

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

        baraja.repartirCarta();

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

        baraja.repartirCarta();

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

    @Transactional
    public Partida apostar(String idPartida, String idJugador, int cantidad) {
        Partida partida = partidaRepository.findById(idPartida)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));

        Jugador jugador = partida.getJugadores().stream()
                .filter(j -> j.getId().equals(idJugador))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        if (!jugador.isActivo() || jugador.getFichas() < cantidad) {
            throw new RuntimeException("El jugador no puede apostar esa cantidad");
        }

        jugador.setFichas(jugador.getFichas() - cantidad);

        partida.setBote(partida.getBote() + cantidad);

        Map<String, Integer> apuestas = partida.getApuestasActuales();
        apuestas.put(jugador.getId(), apuestas.getOrDefault(jugador.getId(), 0) + cantidad);

        partidaRepository.save(partida);
        return avanzarFaseSiCorresponde(idPartida);
    }

    @Transactional
    public Partida igualar(String idPartida, String idJugador) {
        Partida partida = partidaRepository.findById(idPartida)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));

        Jugador jugador = partida.getJugadores().stream()
                .filter(j -> j.getId().equals(idJugador))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        if (!jugador.isActivo()) {
            throw new RuntimeException("Jugador no está activo");
        }

        Map<String, Integer> apuestas = partida.getApuestasActuales();
        int apuestaActualJugador = apuestas.getOrDefault(jugador.getId(), 0);

        int apuestaMaxima = 0;
        for (Integer apuesta : apuestas.values()) {
            if (apuesta > apuestaMaxima) {
                apuestaMaxima = apuesta;
            }
        }

        int diferencia = apuestaMaxima - apuestaActualJugador;

        if (diferencia <= 0) {
            return partida;
        }

        if (jugador.getFichas() < diferencia) {
            throw new RuntimeException("No tienes suficientes fichas para igualar. Debes hacer all-in");
        }

        jugador.setFichas(jugador.getFichas() - diferencia);
        partida.setBote(partida.getBote() + diferencia);
        apuestas.put(jugador.getId(), apuestaActualJugador + diferencia);

        partidaRepository.save(partida);
        return avanzarFaseSiCorresponde(idPartida);
    }


    @Transactional
    public Partida pasar(String idPartida, String idJugador) {
        Partida partida = partidaRepository.findById(idPartida)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));

        Jugador jugador = partida.getJugadores().stream()
                .filter(j -> j.getId().equals(idJugador))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        if (!jugador.isActivo()) {
            throw new RuntimeException("Jugador no está activo");
        }

        Map<String, Integer> apuestas = partida.getApuestasActuales();
        int apuestaJugador = apuestas.getOrDefault(jugador.getId(), 0);

        int apuestaMaxima = 0;
        for (Integer apuesta : apuestas.values()) {
            if (apuesta > apuestaMaxima) {
                apuestaMaxima = apuesta;
            }
        }

        if (apuestaJugador < apuestaMaxima) {
            throw new RuntimeException("No puedes pasar, hay una apuesta que debes igualar.");
        }

        return avanzarFaseSiCorresponde(idPartida);
    }

    @Transactional
    public Partida retirarse(String idPartida, String idJugador) {
        Partida partida = partidaRepository.findById(idPartida)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));

        Jugador jugador = partida.getJugadores().stream()
                .filter(j -> j.getId().equals(idJugador))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        if (!jugador.isActivo()) {
            throw new RuntimeException("Jugador ya está fuera de la mano");
        }

        Map<String, Integer> apuestas = partida.getApuestasActuales();
        int apuestaAcumulada = apuestas.getOrDefault(jugador.getId(), 0);

        partida.setBote(partida.getBote() + apuestaAcumulada);

        apuestas.remove(jugador.getId());

        jugador.setActivo(false);
        jugador.setMano(null);

        partidaRepository.save(partida);
        return avanzarFaseSiCorresponde(idPartida);
    }


    @Transactional
    public Partida allIn(String idPartida, String idJugador) {
        Partida partida = partidaRepository.findById(idPartida)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));

        Jugador jugador = partida.getJugadores().stream()
                .filter(j -> j.getId().equals(idJugador))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        if (!jugador.isActivo()) {
            throw new RuntimeException("Jugador no está activo");
        }

        int fichas = jugador.getFichas();
        if (fichas <= 0) {
            throw new RuntimeException("El jugador no tiene fichas suficientes");
        }

        jugador.setFichas(0);
        jugador.setAllIn(true);
        partida.setBote(partida.getBote() + fichas);

        Map<String, Integer> apuestas = partida.getApuestasActuales();
        int apuestaActual = apuestas.getOrDefault(jugador.getId(), 0);
        apuestas.put(jugador.getId(), apuestaActual + fichas);

        partidaRepository.save(partida);
        return avanzarFaseSiCorresponde(idPartida);
    }

    public boolean rondaDeApuestasFinalizada(Partida partida) {
        Map<String, Integer> apuestas = partida.getApuestasActuales();
        int apuestaMaxima = 0;

        for (Integer cantidad : apuestas.values()) {
            if (cantidad > apuestaMaxima) {
                apuestaMaxima = cantidad;
            }
        }

        int jugadoresActivos = 0;

        for (Jugador jugador : partida.getJugadores()) {
            if (jugador.isActivo()) {
                if (!jugador.isAllIn()) {
                    jugadoresActivos++;

                    int apuestaJugador = 0;
                    if (apuestas.containsKey(jugador.getId())) {
                        apuestaJugador = apuestas.get(jugador.getId());
                    }

                    if (apuestaJugador < apuestaMaxima) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Transactional
    public Partida avanzarFaseSiCorresponde(String idPartida) {
        Partida partida = partidaRepository.findById(idPartida)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));

        if (!rondaDeApuestasFinalizada(partida)) {
            return partida;
        }

        partida.getApuestasActuales().clear();

        GameSessionManager.avanzarFase(idPartida);

        FaseJuego nuevaFase = GameSessionManager.getFase(idPartida);
        Baraja baraja = GameSessionManager.getBaraja(idPartida);

        if (nuevaFase == FaseJuego.FLOP) {
            baraja.repartirCarta();
            for (int i = 0; i < 3; i++) {
                Carta carta = baraja.repartirCarta();
                partida.getCartasComunitarias().add(carta);
            }
        } else if (nuevaFase == FaseJuego.TURN) {
            baraja.repartirCarta();
            Carta turn = baraja.repartirCarta();
            partida.getCartasComunitarias().add(turn);
        } else if (nuevaFase == FaseJuego.RIVER) {
            baraja.repartirCarta();
            Carta river = baraja.repartirCarta();
            partida.getCartasComunitarias().add(river);
        } else if (nuevaFase == FaseJuego.SHOWDOWN) {
            // En esta fase se debería calcular el ganador y repartir el bote (aún por implementar)
        }

        partidaRepository.save(partida);
        return partida;
    }

    @Transactional
    public Partida iniciarNuevaMano(String idPartida) {
        Partida partida = partidaRepository.findById(idPartida)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));

        int jugadoresConFichas = 0;
        for (Jugador jugador : partida.getJugadores()) {
            if (jugador.getFichas() > 0) {
                jugadoresConFichas++;
            }
        }

        if (jugadoresConFichas < 2) {
            throw new RuntimeException("La partida ha terminado. No hay suficientes jugadores con fichas.");
        }

        GameSessionManager.iniciarNuevaMano(partida);

        partida.getApuestasActuales().clear();
        partida.getCartasComunitarias().clear();
        partida.setBote(0);

        for (Jugador jugador : partida.getJugadores()) {
            if (jugador.getFichas() > 0) {
                jugador.setActivo(true);
                jugador.setAllIn(false);
            } else {
                jugador.setActivo(false);
                jugador.setAllIn(false);
                jugador.setMano(null);
            }
        }

        partidaRepository.save(partida);
        return partida;
    }

    @Transactional
    public Partida unirseAPartida(String idPartida, String idUsuario) {
        Partida partida = partidaRepository.findById(idPartida)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        for (Jugador jugador : partida.getJugadores()) {
            if (jugador.getUsuario().getId().equals(idUsuario)) {
                throw new RuntimeException("El usuario ya está unido a esta partida");
            }
        }

        if (partida.getJugadores().size() >= 10) {
            throw new RuntimeException("La partida ya tiene el máximo de 10 jugadores");
        }

        Mesa mesa = partida.getMesa();
        Jugador nuevoJugador = new Jugador(usuario, mesa, partida);
        partida.getJugadores().add(nuevoJugador);

        partidaRepository.save(partida);
        return partida;
    }



}
