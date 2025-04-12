package edu.badpals.pokerweb.infraestructure.sockets;

import edu.badpals.pokerweb.domain.enums.FaseJuego;
import edu.badpals.pokerweb.domain.model.*;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PartidaRunnable implements Runnable {

    private final String codigoInvitacion;
    private final Partida partida;
    private final Baraja baraja;
    private FaseJuego faseActual = FaseJuego.PREFLOP;
    private final Map<String, PrintWriter> jugadoresConectados = new ConcurrentHashMap<>();

    private boolean enCurso = true;

    public PartidaRunnable(String codigoInvitacion) {
        this.codigoInvitacion = codigoInvitacion;
        this.partida = new Partida();
        this.partida.setCodigoInvitacion(codigoInvitacion);
        this.baraja = new Baraja();
    }

    public void agregarJugador(String idUsuario, String nombreJugador, PrintWriter salida) {
        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);
        usuario.setNombreCompleto(nombreJugador);
        usuario.setDinero(1000);

        Jugador jugador = new Jugador();
        jugador.setId(idUsuario);
        jugador.setUsuario(usuario);
        jugador.setActivo(true);
        jugador.setFichas(usuario.getDinero());
        jugador.setPartida(partida);

        partida.getJugadores().add(jugador);
        jugadoresConectados.put(idUsuario, salida);

        notificarATodos("🟢 " + nombreJugador + " se ha unido a la partida.");
        enviarEstadoAParticipante(idUsuario);
    }

    public void enviarEstadoAParticipante(String idJugador) {
        PrintWriter salida = jugadoresConectados.get(idJugador);
        if (salida == null) return;

        salida.println("📋 Estado actual de la partida:");
        salida.println("Jugadores:");

        for (Jugador j : partida.getJugadores()) {
            salida.println("- " + j.getNombre() + " (" + j.getFichas() + " fichas)");
        }

        salida.println("Fase actual: " + faseActual);
        salida.println("Bote actual: " + partida.getBote());
    }


    public void recibirComando(String idJugador, String mensaje) {
        notificarATodos("📨 " + idJugador + ": " + mensaje);
        // Aquí irá la lógica real del juego
    }

    public void notificarATodos(String mensaje) {
        jugadoresConectados.values().forEach(pw -> pw.println(mensaje));
    }

    @Override
    public void run() {
        notificarATodos("🎮 La partida '" + codigoInvitacion + "' ha comenzado.");
        while (enCurso) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void finalizarPartida() {
        enCurso = false;
        notificarATodos("🚪 La partida '" + codigoInvitacion + "' ha finalizado.");
    }

    public String getCodigoInvitacion() {
        return codigoInvitacion;
    }
}
