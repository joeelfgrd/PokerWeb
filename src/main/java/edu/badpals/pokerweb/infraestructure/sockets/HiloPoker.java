package edu.badpals.pokerweb.infraestructure.sockets;

import edu.badpals.pokerweb.domain.model.Jugador;
import edu.badpals.pokerweb.domain.model.Usuario;
import edu.badpals.pokerweb.infraestructure.sockets.PartidaRunnable;
import java.io.*;
import java.net.Socket;
import java.util.Map;

public class HiloPoker extends Thread {

    private final Socket socket;
    private final Map<String, PartidaRunnable> partidasActivas;
    private BufferedReader entrada;
    private PrintWriter salida;

    private String idJugador;
    private String nombreJugador;
    private PartidaRunnable partida;

    public HiloPoker(Socket socket, Map<String, PartidaRunnable> partidasActivas) {
        this.socket = socket;
        this.partidasActivas = partidasActivas;
    }

    @Override
    public void run() {
        try {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            salida.println("Conectado al servidor de póker. Usa: CREAR <partida> <nombre> <id> o UNIRSE <partida> <nombre> <id>");

            String comandoInicial = entrada.readLine();
            if (comandoInicial == null || (!comandoInicial.startsWith("CREAR") && !comandoInicial.startsWith("UNIRSE"))) {
                salida.println("Comando inválido. Desconectando.");
                socket.close();
                return;
            }

            String[] partes = comandoInicial.trim().split("\\s+");
            if (partes.length != 4) {
                salida.println("Formato incorrecto. Usa: CREAR/UNIRSE <partida> <nombre> <id>");
                socket.close();
                return;
            }

            String accion = partes[0];
            String idPartida = partes[1];
            this.nombreJugador = partes[2];
            this.idJugador = partes[3];

            Usuario usuario = new Usuario();
            usuario.setId(idJugador);
            usuario.setNombreCompleto(nombreJugador);
            usuario.setDinero(1000); // o el dinero que tengas por defecto

            Jugador jugador = new Jugador();
            jugador.setId(idJugador);
            jugador.setUsuario(usuario); // 👈 esta línea es esencial


            // Crear o unirse
            if (accion.equalsIgnoreCase("CREAR")) {
                if (partidasActivas.containsKey(idPartida)) {
                    salida.println("❌ La partida ya existe.");
                    socket.close();
                    return;
                }
                PartidaRunnable nueva = new PartidaRunnable(idPartida);
                nueva.agregarJugador(idJugador, nombreJugador, salida);

                partidasActivas.put(idPartida, nueva);
                new Thread(nueva).start();
                this.partida = nueva;
                salida.println("✅ Partida '" + idPartida + "' creada y unida.");
            } else if (accion.equalsIgnoreCase("UNIRSE")) {
                if (!partidasActivas.containsKey(idPartida)) {
                    salida.println("❌ La partida no existe.");
                    socket.close();
                    return;
                }
                this.partida = partidasActivas.get(idPartida);
                partida.agregarJugador(idJugador, nombreJugador, salida);

                salida.println("✅ Te has unido a la partida '" + idPartida + "'");
            }

            // Loop para escuchar mensajes posteriores (acciones del juego)
            String mensaje;
            while ((mensaje = entrada.readLine()) != null) {
                partida.recibirComando(idJugador, mensaje);
            }

        } catch (IOException e) {
            System.err.println("Error en HiloPoker: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                System.err.println("Error cerrando socket: " + ex.getMessage());
            }
        }
    }
}
