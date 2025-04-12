package edu.badpals.pokerweb.infraestructure.sockets;

import java.io.*;
import java.net.Socket;

public class ClientePokerConsola {

    public static void main(String[] args) {
        final String host = "localhost";
        final int puerto = 10000;

        try (Socket socket = new Socket(host, puerto);
             BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consola = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("🟢 Conectado al servidor de póker en " + host + ":" + puerto);

            Thread escuchaServidor = new Thread(() -> {
                String respuesta;
                try {
                    while ((respuesta = entrada.readLine()) != null) {
                        System.out.println("[Servidor] " + respuesta);
                    }
                } catch (IOException e) {
                    System.out.println("❌ Conexión cerrada por el servidor.");
                }
            });

            escuchaServidor.start();

            String linea;
            while ((linea = consola.readLine()) != null) {
                salida.println(linea);
                if (linea.equalsIgnoreCase("SALIR")) {
                    break;
                }
            }

            System.out.println("🔴 Cliente desconectado.");
            escuchaServidor.interrupt();

        } catch (IOException e) {
            System.err.println("❌ Error en cliente: " + e.getMessage());
        }
    }
}
