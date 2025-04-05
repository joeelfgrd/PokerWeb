package edu.badpals.pokerweb.application.service;

import edu.badpals.pokerweb.domain.services.EvaluadorManos;
import edu.badpals.pokerweb.domain.services.GameSessionManager;
import edu.badpals.pokerweb.domain.services.GestorApuestas;
import edu.badpals.pokerweb.domain.services.ManoEvaluada;
import edu.badpals.pokerweb.application.dtos.EstadoJugadorDTO;
import edu.badpals.pokerweb.application.dtos.EstadoPartidaDTO;
import edu.badpals.pokerweb.application.dtos.ResultadoShowdownDTO;
import edu.badpals.pokerweb.domain.model.*;
import edu.badpals.pokerweb.domain.enums.FaseJuego;
import edu.badpals.pokerweb.infraestructure.persistence.repository.MesaRepository;
import edu.badpals.pokerweb.infraestructure.persistence.repository.PartidaRepository;
import edu.badpals.pokerweb.infraestructure.persistence.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.badpals.pokerweb.domain.services.EvaluadorManos.determinarGanadoresEntre;

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
        Partida partida = obtenerPartida(idPartida);
        gestorApuestas.apostar(partida, idJugador, cantidad);
        partidaRepository.save(partida);
        return avanzarFaseSiCorresponde(idPartida);
    }


    @Transactional
    public Partida igualar(String idPartida, String idJugador) {
        Partida partida = obtenerPartida(idPartida);
        gestorApuestas.igualar(partida, idJugador);
        partidaRepository.save(partida);
        return avanzarFaseSiCorresponde(idPartida);
    }



    @Transactional
    public Partida pasar(String idPartida, String idJugador) {
        Partida partida = obtenerPartida(idPartida);
        gestorApuestas.pasar(partida, idJugador);
        partidaRepository.save(partida);
        return avanzarFaseSiCorresponde(idPartida);
    }


    @Transactional
    public Partida retirarse(String idPartida, String idJugador) {
        Partida partida = obtenerPartida(idPartida);
        gestorApuestas.retirarse(partida, idJugador);
        partidaRepository.save(partida);
        return avanzarFaseSiCorresponde(idPartida);
    }



    @Transactional
    public Partida allIn(String idPartida, String idJugador) {
        Partida partida = obtenerPartida(idPartida);
        gestorApuestas.allIn(partida, idJugador);
        partidaRepository.save(partida);
        return avanzarFaseSiCorresponde(idPartida);
    }

    private Partida obtenerPartida(String idPartida) {
        return partidaRepository.findById(idPartida)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));
    }


    public boolean rondaDeApuestasFinalizada(Partida partida) {
        Map<String, Integer> apuestas = partida.getApuestasActuales();
        int apuestaMaxima = 0;
        int jugadoresActivos = 0;

        for (Integer cantidad : apuestas.values()) {
            if (cantidad > apuestaMaxima) {
                apuestaMaxima = cantidad;
            }
        }

        for (Jugador jugador : partida.getJugadores()) {
            if (jugador.isActivo() && !jugador.isAllIn()) {
                jugadoresActivos++;

                int apuestaJugador = apuestas.getOrDefault(jugador.getId(), 0);
                if (apuestaJugador < apuestaMaxima) {
                    return false;
                }
            }
        }

        return partida.getJugadoresQueHanActuado().size() >= jugadoresActivos;
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

        }

        partida.getJugadoresQueHanActuado().clear();
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

        // Reiniciar baraja y fase
        GameSessionManager.reiniciarFaseYBaraja(idPartida);
        Baraja baraja = GameSessionManager.getBaraja(idPartida);

        // Limpiar estado de la partida
        partida.getApuestasActuales().clear();
        partida.getCartasComunitarias().clear();
        partida.setBote(0);
        partida.getJugadoresQueHanActuado().clear();

        // Repartir nuevas cartas
        for (Jugador jugador : partida.getJugadores()) {
            if (jugador.getFichas() > 0) {
                jugador.setActivo(true);
                jugador.setAllIn(false);
                List<Carta> nuevasCartas = new ArrayList<>();
                nuevasCartas.add(baraja.repartirCarta());
                nuevasCartas.add(baraja.repartirCarta());
                jugador.setMano(new Mano(nuevasCartas));
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

    @Transactional
    public ResultadoShowdownDTO resolverShowdown(String idPartida) {
        Partida partida = partidaRepository.findById(idPartida)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));

        if (GameSessionManager.getFase(idPartida) != FaseJuego.SHOWDOWN) {
            throw new RuntimeException("No se puede resolver el showdown si no se está en la fase SHOWDOWN");
        }

        // Filtramos solo jugadores activos
        List<Jugador> jugadoresActivos = partida.getJugadores().stream()
                .filter(Jugador::isActivo)
                .toList();

        List<SidePot> sidePots = partida.getSidePots();
        int totalGanado = 0;
        Jugador ganadorPrincipal = null;
        ManoEvaluada mejorManoPrincipal = null;

        // Repartimos cada side pot entre los ganadores correspondientes
        for (SidePot sidePot : sidePots) {
            // Candidatos = jugadores activos que participaron
            List<Jugador> candidatos = new ArrayList<>();
            for (Jugador j : jugadoresActivos) {
                if (sidePot.getParticipantes().contains(j.getId())) {
                    candidatos.add(j);
                }
            }

            // Podríamos tener 1, varios o ningún ganador
            List<Jugador> ganadoresSidePot = determinarGanadoresEntre(candidatos, partida.getCartasComunitarias());
            if (!ganadoresSidePot.isEmpty()) {
                int cantPorGanador = sidePot.getCantidad() / ganadoresSidePot.size();
                int resto = sidePot.getCantidad() % ganadoresSidePot.size();

                // Sumamos a cada ganador su parte
                for (int i = 0; i < ganadoresSidePot.size(); i++) {
                    Jugador g = ganadoresSidePot.get(i);
                    int monto = cantPorGanador;
                    // Si hay un sobrante, se lo damos al primer ganador, por ejemplo.
                    if (i == 0 && resto > 0) {
                        monto += resto;
                    }
                    g.setFichas(g.getFichas() + monto);
                    totalGanado += monto;

                    // Verificamos si tiene mejor mano principal
                    List<Carta> cartas = new ArrayList<>(g.getMano().getCartas());
                    cartas.addAll(partida.getCartasComunitarias());
                    ManoEvaluada manoEval = EvaluadorManos.evaluar(cartas);

                    if (mejorManoPrincipal == null || manoEval.compareTo(mejorManoPrincipal) > 0) {
                        mejorManoPrincipal = manoEval;
                        ganadorPrincipal = g;
                    }
                }
            }
        }

        // Repartimos el bote principal (partida.getBote()) entre los ganadores de la partida
        int botePrincipal = partida.getBote();
        if (botePrincipal > 0) {
            List<Jugador> ganadoresMain = determinarGanadoresEntre(jugadoresActivos, partida.getCartasComunitarias());
            if (!ganadoresMain.isEmpty()) {
                int cantPorGanador = botePrincipal / ganadoresMain.size();
                int resto = botePrincipal % ganadoresMain.size();

                for (int i = 0; i < ganadoresMain.size(); i++) {
                    Jugador g = ganadoresMain.get(i);
                    int monto = cantPorGanador;
                    // Si hay un sobrante, se lo sumamos al primero (política simple)
                    if (i == 0 && resto > 0) {
                        monto += resto;
                    }
                    g.setFichas(g.getFichas() + monto);
                    totalGanado += monto;

                    List<Carta> cartas = new ArrayList<>(g.getMano().getCartas());
                    cartas.addAll(partida.getCartasComunitarias());
                    ManoEvaluada manoEval = EvaluadorManos.evaluar(cartas);

                    if (mejorManoPrincipal == null || manoEval.compareTo(mejorManoPrincipal) > 0) {
                        mejorManoPrincipal = manoEval;
                        ganadorPrincipal = g;
                    }
                }

                // Si solo hay un ganador, lo establecemos como idGanador
                if (ganadoresMain.size() == 1) {
                    partida.setIdGanador(ganadoresMain.get(0).getId());
                } else {
                    // Podrías dejar en null para representar un empate,
                    // o seleccionar uno arbitrariamente.
                    partida.setIdGanador(null);
                }
            }
        } else if (ganadorPrincipal != null) {
            // Si no había bote principal pero sí side pots,
            // y tuvimos un "ganador principal"
            partida.setIdGanador(ganadorPrincipal.getId());
        } else {
            throw new RuntimeException("No se pudo determinar un ganador");
        }

        // Dejamos el bote y sidepots en cero para la siguiente mano
        partida.setBote(0);
        partida.getSidePots().clear();
        partidaRepository.save(partida);

        // Finalmente, creamos el DTO con la info del "ganador principal".
        // Para la UI, muchas veces interesa mostrar el que tenía la mejor mano,
        // aun si hubo empates.
        String nombreGanador = (ganadorPrincipal != null)
                ? ganadorPrincipal.getUsuario().getNombre()
                : "Empate";

        List<Carta> cartasGanador = (ganadorPrincipal != null && ganadorPrincipal.getMano() != null)
                ? ganadorPrincipal.getMano().getCartas()
                : new ArrayList<>();

        int fichasGanador = (ganadorPrincipal != null) ? ganadorPrincipal.getFichas() : 0;

        ResultadoShowdownDTO resultado = new ResultadoShowdownDTO(
                nombreGanador,
                cartasGanador,
                fichasGanador,
                totalGanado,
                new ArrayList<>(partida.getCartasComunitarias())
        );

        // Iniciar la siguiente mano
        iniciarNuevaMano(idPartida);
        return resultado;
    }

    public static List<Jugador> determinarGanadores(Partida partida) {
        List<Jugador> jugadoresActivos = new ArrayList<>();
        for (Jugador j : partida.getJugadores()) {
            if (j.isActivo() && j.getMano() != null) {
                jugadoresActivos.add(j);
            }
        }
        return determinarGanadoresEntre(jugadoresActivos, partida.getCartasComunitarias());
    }




    public EstadoPartidaDTO obtenerEstadoPartida(String idPartida) {
        Partida partida = partidaRepository.findById(idPartida)
                .orElseThrow(() -> new RuntimeException("Partida no encontrada"));

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

        return new EstadoPartidaDTO(
                GameSessionManager.getFase(idPartida),
                partida.getBote(),
                partida.getCartasComunitarias(),
                estadoJugadores
        );
    }




}
