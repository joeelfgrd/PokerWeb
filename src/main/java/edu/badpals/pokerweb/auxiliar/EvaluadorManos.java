package edu.badpals.pokerweb.auxiliar;

import edu.badpals.pokerweb.model.Carta;
import edu.badpals.pokerweb.model.Jugador;
import edu.badpals.pokerweb.model.Partida;

import java.util.*;

public class EvaluadorManos {

    private static boolean contarRepeticiones(List<Carta> cartas, int repeticiones) {
        Map<Integer, Integer> contadorCartas = new HashMap<>();
        for (Carta carta : cartas) {
            int valor = carta.getNumero();
            contadorCartas.putIfAbsent(valor, 0);
            contadorCartas.put(valor, contadorCartas.get(valor) + 1);
        }

        for (int count : contadorCartas.values()) {
            if (count >= repeticiones) return true;
        }

        return false;
    }

    public static Jugador determinarGanador(Partida partida) {
        Jugador ganador = null;
        int mejorPuntuacion = -1;
        int mejorCartaAlta = -1;

        for (Jugador jugador : partida.getJugadores()) {
            if (jugador.isActivo() && jugador.getMano() != null) {
                List<Carta> todasCartas = new ArrayList<>();
                todasCartas.addAll(jugador.getMano().getCartas());
                todasCartas.addAll(partida.getCartasComunitarias());

                int puntuacion = evaluarMano(todasCartas);
                int cartaAlta = obtenerCartaMasAlta(todasCartas);

                if (ganador == null ||
                        puntuacion > mejorPuntuacion ||
                        (puntuacion == mejorPuntuacion && cartaAlta > mejorCartaAlta)) {

                    ganador = jugador;
                    mejorPuntuacion = puntuacion;
                    mejorCartaAlta = cartaAlta;
                }
            }
        }

        return ganador;
    }


    public static int evaluarMano(List<Carta> cartas) {
        if (tienePoker(cartas)) {
            return 700;
        }
        if (tieneFull(cartas)) {
            return 600;
        }
        if (tieneColor(cartas)) {
            return 500;
        }
        if (tieneEscalera(cartas)) {
            return 400;
        }
        if (tieneTrio(cartas)) {
            return 300;
        }
        if (tieneDoblePareja(cartas)) {
            return 200;
        }
        if (tienePareja(cartas)) {
            return 100;
        }

        return obtenerCartaMasAlta(cartas);
    }

    private static boolean tienePoker(List<Carta> cartas) {
        return contarRepeticiones(cartas, 4);
    }

    private static boolean tieneFull(List<Carta> cartas) {
        return contarRepeticiones(cartas, 3) && contarRepeticiones(cartas, 2);
    }

    private static boolean tieneColor(List<Carta> cartas) {
        int[] palos = new int[4];
        for (Carta carta : cartas) {
            palos[carta.getIdPalo()]++;
        }
        for (int count : palos) {
            if (count >= 5) return true;
        }
        return false;
    }

    private static boolean tieneEscalera(List<Carta> cartas) {
        Set<Integer> valores = new HashSet<>();
        for (Carta carta : cartas) {
            valores.add(carta.getNumero());
        }

        List<Integer> lista = new ArrayList<>(valores);
        Collections.sort(lista);

        int contador = 1;
        for (int i = 1; i < lista.size(); i++) {
            if (lista.get(i) == lista.get(i - 1) + 1) {
                contador++;
                if (contador >= 5) return true;
            } else {
                contador = 1;
            }
        }

        if (valores.contains(14) && valores.contains(2) && valores.contains(3) && valores.contains(4) && valores.contains(5)) {
            return true;
        }

        return false;
    }

    private static boolean tieneTrio(List<Carta> cartas) {
        return contarRepeticiones(cartas, 3);
    }

    private static boolean tieneDoblePareja(List<Carta> cartas) {
        Map<Integer, Integer> contadorCartas = new HashMap<>();
        for (Carta carta : cartas) {
            int valor = carta.getNumero();
            contadorCartas.put(valor, contadorCartas.getOrDefault(valor, 0) + 1);
        }

        int parejas = 0;
        for (int count : contadorCartas.values()) {
            if (count >= 2) {
                parejas++;
            }
        }

        return parejas >= 2;
    }


    private static boolean tienePareja(List<Carta> cartas) {
        return contarRepeticiones(cartas, 2);
    }


    private static int obtenerCartaMasAlta(List<Carta> cartas) {
        int max = 0;
        for (Carta carta : cartas) {
            if (carta.getNumero() > max) {
                max = carta.getNumero();
            }
        }
        return max;
    }
}
