package edu.badpals.pokerweb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.badpals.pokerweb.application.dtos.EstadoPartidaDTO;
import edu.badpals.pokerweb.application.dtos.MensajeWebSocketDTO;
import edu.badpals.pokerweb.application.dtos.ResultadoShowdownDTO;
import edu.badpals.pokerweb.application.service.PartidaService;
import edu.badpals.pokerweb.domain.enums.FaseJuego;
import edu.badpals.pokerweb.domain.exceptions.GanadorIndeterminadoException;
import edu.badpals.pokerweb.domain.exceptions.PartidaFinalizadaException;
import edu.badpals.pokerweb.domain.model.Carta;
import edu.badpals.pokerweb.domain.model.Partida;
import edu.badpals.pokerweb.domain.services.GameSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class PokerWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private PartidaService partidaService;

    @Autowired
    private SessionManagerWebSocket sessionManager;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("✅ Conexión establecida: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionManager.eliminarSesion(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        MensajeWebSocketDTO dto = mapper.readValue(message.getPayload(), MensajeWebSocketDTO.class);

        switch (dto.getAccion()) {
            case "CREAR_PARTIDA" -> {
                Partida partida = partidaService.crearPartida(dto.getIdUsuario());
                sessionManager.registrarSesion(partida.getId(), dto.getIdUsuario(), session);
                session.sendMessage(new TextMessage("✅ Código: " + partida.getCodigoInvitacion() + " | ID: " + partida.getId()));
            }

            case "UNIRSE_PARTIDA" -> {
                Partida partida = partidaService.unirseAPartida(dto.getCodigoInvitacion(), dto.getIdUsuario());
                sessionManager.registrarSesion(partida.getId(), dto.getIdUsuario(), session);
                session.sendMessage(new TextMessage("✅ Te uniste a " + partida.getCodigoInvitacion()));
            }

            case "ESTADO_PARTIDA" -> {
                EstadoPartidaDTO estado = partidaService.obtenerEstadoPartida(dto.getIdPartida());
                session.sendMessage(new TextMessage(mapper.writeValueAsString(estado)));
            }

            case "APOSTAR" -> {
                partidaService.apostar(dto.getIdPartida(), dto.getIdUsuario(), dto.getCantidad());
                sessionManager.enviarMensajeATodos(dto.getIdPartida(), "💰 Apuesta de " + dto.getCantidad() + " por " + dto.getIdUsuario());
                checkAndEnviarShowdown(dto.getIdPartida());
            }

            case "IGUALAR" -> {
                partidaService.igualar(dto.getIdPartida(), dto.getIdUsuario());
                sessionManager.enviarMensajeATodos(dto.getIdPartida(), "🤝 Igualó la apuesta: " + dto.getIdUsuario());
                checkAndEnviarShowdown(dto.getIdPartida());
            }

            case "PASAR" -> {
                partidaService.pasar(dto.getIdPartida(), dto.getIdUsuario());
                sessionManager.enviarMensajeATodos(dto.getIdPartida(), "👉 Pasó turno: " + dto.getIdUsuario());
                checkAndEnviarShowdown(dto.getIdPartida());
            }

            case "RETIRARSE" -> {
                partidaService.retirarse(dto.getIdPartida(), dto.getIdUsuario());
                sessionManager.enviarMensajeATodos(dto.getIdPartida(), "❌ Se retiró: " + dto.getIdUsuario());
                checkAndEnviarShowdown(dto.getIdPartida());
            }

            case "ALL_IN" -> {
                partidaService.allIn(dto.getIdPartida(), dto.getIdUsuario());
                sessionManager.enviarMensajeATodos(dto.getIdPartida(), "🧨 All-in de " + dto.getIdUsuario());
                checkAndEnviarShowdown(dto.getIdPartida());
            }

            case "SHOWDOWN" -> {
                ResultadoShowdownDTO resultado = partidaService.resolverShowdown(dto.getIdPartida());
                sessionManager.enviarMensajeATodos(dto.getIdPartida(), "🃏 Showdown! Ganador: " + resultado.getNombreGanador());
                partidaService.iniciarNuevaMano(dto.getIdPartida());
            }

            case "REPARTIR_MANOS" -> {
                try {
                    partidaService.iniciarNuevaMano(dto.getIdPartida());
                    Map<String, List<Carta>> manos = partidaService.repartirManosPrivadas(dto.getIdPartida());

                    for (Map.Entry<String, List<Carta>> entry : manos.entrySet()) {
                        String idJugador = entry.getKey();
                        List<Carta> mano = entry.getValue();

                        sessionManager.enviarSoloASesion(dto.getIdPartida(), idJugador,
                                "🃏 Tus cartas: " + mano.get(0) + ", " + mano.get(1));
                    }

                    sessionManager.enviarMensajeATodosMenos(dto.getIdPartida(), dto.getIdUsuario(),
                            "✅ Todos han recibido sus cartas.");
                } catch (PartidaFinalizadaException e) {
                    sessionManager.enviarMensajeATodos(dto.getIdPartida(),
                            "🏁 La partida ha finalizado. ¡Un jugador se ha llevado todas las fichas!");
                }
            }

            default -> session.sendMessage(new TextMessage("❌ Acción no reconocida: " + dto.getAccion()));
        }
    }

    private void checkAndEnviarShowdown(String idPartida) throws IOException {
        if (GameSessionManager.getFase(idPartida) == FaseJuego.SHOWDOWN) {
            ResultadoShowdownDTO resultado = partidaService.resolverShowdown(idPartida);
            sessionManager.enviarMensajeATodos(idPartida, "🃏 Showdown! Ganador: " + resultado.getNombreGanador());

            new Thread(() -> {
                try {
                    Thread.sleep(10000);
                    partidaService.iniciarNuevaMano(idPartida);
                    Map<String, List<Carta>> manos = partidaService.repartirManosPrivadas(idPartida);

                    for (Map.Entry<String, List<Carta>> entry : manos.entrySet()) {
                        String idJugador = entry.getKey();
                        List<Carta> mano = entry.getValue();
                        sessionManager.enviarSoloASesion(idPartida, idJugador,
                                "🃏 Tus cartas: " + mano.get(0) + ", " + mano.get(1));
                    }

                    sessionManager.enviarMensajeATodos(idPartida, "🌀 Comienza una nueva mano.");

                } catch (Exception e) {
                    System.err.println("❌ Error al iniciar nueva mano: " + e.getMessage());
                }
            }).start();
        }
    }




}
