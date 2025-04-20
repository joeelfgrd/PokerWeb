package edu.badpals.pokerweb.config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManagerWebSocket {

    // Map<idPartida, Map<idUsuario, WebSocketSession>>
    private final Map<String, Map<String, WebSocketSession>> sesionesPorPartida = new ConcurrentHashMap<>();

    public void registrarSesion(String idPartida, String idJugador, WebSocketSession session) {
        sesionesPorPartida.computeIfAbsent(idPartida, k -> new ConcurrentHashMap<>()).put(idJugador, session);
    }

    public void eliminarSesion(WebSocketSession session) {
        for (Map<String, WebSocketSession> mapa : sesionesPorPartida.values()) {
            mapa.values().removeIf(s -> s.getId().equals(session.getId()));
        }
    }

    public void eliminarSesion(String idPartida, String idJugador) {
        if (sesionesPorPartida.containsKey(idPartida)) {
            sesionesPorPartida.get(idPartida).remove(idJugador);
            if (sesionesPorPartida.get(idPartida).isEmpty()) {
                sesionesPorPartida.remove(idPartida);
            }
        }
    }

    public List<WebSocketSession> obtenerSesionesDePartida(String idPartida) {
        if (!sesionesPorPartida.containsKey(idPartida)) return List.of();
        return new ArrayList<>(sesionesPorPartida.get(idPartida).values());
    }

    public Optional<WebSocketSession> obtenerSesion(String idPartida, String idJugador) {
        if (!sesionesPorPartida.containsKey(idPartida)) return Optional.empty();
        return Optional.ofNullable(sesionesPorPartida.get(idPartida).get(idJugador));
    }

    public void enviarMensajeATodos(String idPartida, String mensaje) throws IOException {
        for (WebSocketSession session : obtenerSesionesDePartida(idPartida)) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(mensaje));
            }
        }
    }

    public void enviarMensajeATodosMenos(String idPartida, String idJugadorExcluido, String mensaje) throws IOException {
        Map<String, WebSocketSession> SesionesJugadores = sesionesPorPartida.get(idPartida);
        if (SesionesJugadores != null) {
            for (Map.Entry<String, WebSocketSession> entry : SesionesJugadores.entrySet()) {
                if (!entry.getKey().equals(idJugadorExcluido)) {
                    WebSocketSession session = entry.getValue();
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(mensaje));
                    }
                }
            }
        }
    }

    public void enviarSoloASesion(String idPartida, String idJugador, String mensaje) throws IOException {
        Optional<WebSocketSession> sessionOpt = obtenerSesion(idPartida, idJugador);
        if (sessionOpt.isPresent() && sessionOpt.get().isOpen()) {
            sessionOpt.get().sendMessage(new TextMessage(mensaje));
        }
    }

}