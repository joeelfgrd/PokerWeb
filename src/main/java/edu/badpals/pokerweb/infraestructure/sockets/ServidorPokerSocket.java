package edu.badpals.pokerweb.infraestructure.sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.badpals.pokerweb.infraestructure.sockets.PartidaRunnable;

public class ServidorPokerSocket {

    private static final int PUERTO = 10000;

    private static final Map<String, PartidaRunnable> partidasActivas = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.println("Servidor de Póker iniciado en el puerto " + PUERTO);

        try (ServerSocket servidor = new ServerSocket(PUERTO)) {

            while (true) {
                Socket socketCliente = servidor.accept();
                /*Acordarme tener esto en cuenta para la seguridad vs DoS*/
                System.out.println("Cliente conectado desde " + socketCliente.getInetAddress());

                HiloPoker hiloJugador = new HiloPoker(socketCliente, partidasActivas);
                hiloJugador.start();
            }

        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }
}
