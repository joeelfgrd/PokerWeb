package edu.badpals.pokerweb.domain.services;

import edu.badpals.pokerweb.domain.model.Carta;
import edu.badpals.pokerweb.domain.model.Jugador;
import edu.badpals.pokerweb.domain.model.Partida;

import java.util.*;

public class EvaluadorManos {

    public static Jugador determinarGanador(Partida partida) {
        Jugador ganador = null;
        ManoEvaluada mejorMano = null;

        for (Jugador jugador : partida.getJugadores()) {
            if (jugador.isActivo() && jugador.getMano() != null) {
                List<Carta> cartasDelJugador = obtenerCartasDelJugador(jugador, partida);
                ManoEvaluada manoEvaluada = evaluar(cartasDelJugador);

                if (mejorMano == null || manoEvaluada.compareTo(mejorMano) > 0) {
                    mejorMano = manoEvaluada;
                    ganador = jugador;
                }
            }
        }

        return ganador;
    }

    public static List<Jugador> determinarGanadoresEntre(List<Jugador> jugadores, List<Carta> cartasComunitarias) {
        if (jugadores == null || jugadores.isEmpty()) return new ArrayList<>();

        Map<Jugador, ManoEvaluada> evaluaciones = new HashMap<>();
        for (Jugador jugador : jugadores) {
            if (jugador.getMano() == null) continue;
            List<Carta> cartasDelJugador = obtenerCartasDelJugador(jugador, cartasComunitarias);
            evaluaciones.put(jugador, evaluar(cartasDelJugador));
        }

        ManoEvaluada mejorMano = obtenerMejorMano(evaluaciones);
        return obtenerGanadores(evaluaciones, mejorMano);
    }

    private static List<Carta> obtenerCartasDelJugador(Jugador jugador, List<Carta> cartasComunitarias) {
        List<Carta> todasLasCartas = new ArrayList<>(jugador.getMano().getCartas());
        todasLasCartas.addAll(cartasComunitarias);
        return todasLasCartas;
    }

    private static List<Carta> obtenerCartasDelJugador(Jugador jugador, Partida partida) {
        List<Carta> todasLasCartas = new ArrayList<>(jugador.getMano().getCartas());
        todasLasCartas.addAll(partida.getCartasComunitarias());
        return todasLasCartas;
    }

    private static ManoEvaluada obtenerMejorMano(Map<Jugador, ManoEvaluada> evaluaciones) {
        ManoEvaluada mejorMano = null;
        for (ManoEvaluada manoEvaluada : evaluaciones.values()) {
            if (mejorMano == null || manoEvaluada.compareTo(mejorMano) > 0) {
                mejorMano = manoEvaluada;
            }
        }
        return mejorMano;
    }

    private static List<Jugador> obtenerGanadores(Map<Jugador, ManoEvaluada> evaluaciones, ManoEvaluada mejorMano) {
        List<Jugador> ganadores = new ArrayList<>();
        for (Map.Entry<Jugador, ManoEvaluada> entry : evaluaciones.entrySet()) {
            if (entry.getValue().compareTo(mejorMano) == 0) {
                ganadores.add(entry.getKey());
            }
        }

        // Si no se encontró un ganador, usamos la carta más alta como desempate
        if (ganadores.isEmpty()) {
            int cartaAlta = obtenerCartaAltaDeManos(evaluaciones.keySet());
            for (Jugador jugador : evaluaciones.keySet()) {
                int cartaAltaJugador = obtenerCartaAltaJugador(jugador);
                if (cartaAltaJugador == cartaAlta) {
                    ganadores.add(jugador);
                }
            }
        }

        return ganadores;
    }

    private static int obtenerCartaAltaDeManos(Set<Jugador> jugadores) {
        int maxCarta = 0;
        for (Jugador jugador : jugadores) {
            int cartaAlta = obtenerCartaAltaJugador(jugador);
            if (cartaAlta > maxCarta) {
                maxCarta = cartaAlta;
            }
        }
        return maxCarta;
    }

    private static int obtenerCartaAltaJugador(Jugador jugador) {
        return jugador.getMano().getCartas().stream()
                .mapToInt(Carta::getNumero)
                .max()
                .orElse(0);
    }

    public static ManoEvaluada evaluar(List<Carta> cartas) {
        if (tienePoker(cartas)) {
            return evaluarPoker(cartas);
        }
        if (tieneFull(cartas)) {
            return evaluarFull(cartas);
        }
        if (tieneColor(cartas)) {
            return evaluarColor(cartas);
        }
        if (tieneEscalera(cartas)) {
            return evaluarEscalera(cartas);
        }
        if (tieneTrio(cartas)) {
            return evaluarTrio(cartas);
        }
        if (tieneDoblePareja(cartas)) {
            return evaluarDoblePareja(cartas);
        }
        if (tienePareja(cartas)) {
            return evaluarPareja(cartas);
        }

        return evaluarCartaAlta(cartas);
    }

    private static ManoEvaluada evaluarPoker(List<Carta> cartas) {
        int valor = obtenerValorPorRepeticion(cartas, 4);
        List<Integer> kickers = obtenerKickers(cartas, List.of(valor));
        return new ManoEvaluada(700, valor, kickers);
    }

    private static ManoEvaluada evaluarFull(List<Carta> cartas) {
        int trio = obtenerValorPorRepeticion(cartas, 3);
        int pareja = obtenerValorPorRepeticionExcluyendo(cartas, 2, List.of(trio));
        return new ManoEvaluada(600, trio, List.of(pareja));
    }

    private static ManoEvaluada evaluarColor(List<Carta> cartas) {
        List<Carta> color = obtenerCartasMismoPalo(cartas);
        List<Integer> valores = obtenerValoresCartas(color);
        return new ManoEvaluada(500, valores.get(0), valores.subList(1, Math.min(5, valores.size())));
    }

    private static ManoEvaluada evaluarEscalera(List<Carta> cartas) {
        int alta = obtenerCartaAltaEscalera(cartas);
        return new ManoEvaluada(400, alta, List.of());
    }

    private static ManoEvaluada evaluarTrio(List<Carta> cartas) {
        int valor = obtenerValorPorRepeticion(cartas, 3);
        List<Integer> kickers = obtenerKickers(cartas, List.of(valor));
        return new ManoEvaluada(300, valor, kickers);
    }

    private static ManoEvaluada evaluarDoblePareja(List<Carta> cartas) {
        List<Integer> parejas = obtenerParejas(cartas);
        parejas.sort(Comparator.reverseOrder());
        List<Integer> kickers = obtenerKickers(cartas, parejas);
        return new ManoEvaluada(200, parejas.get(0), List.of(parejas.get(1), kickers.get(0)));
    }

    private static ManoEvaluada evaluarPareja(List<Carta> cartas) {
        int valor = obtenerValorPorRepeticion(cartas, 2);
        List<Integer> kickers = obtenerKickers(cartas, List.of(valor));
        return new ManoEvaluada(100, valor, kickers);
    }

    private static ManoEvaluada evaluarCartaAlta(List<Carta> cartas) {
        List<Integer> valoresOrdenados = obtenerValoresCartas(cartas);
        return new ManoEvaluada(0, valoresOrdenados.get(0), valoresOrdenados.subList(1, Math.min(4, valoresOrdenados.size())));
    }

    private static List<Integer> obtenerValoresCartas(List<Carta> cartas) {
        List<Integer> valores = new ArrayList<>();
        for (Carta carta : cartas) {
            valores.add(carta.getNumero());
        }
        Collections.sort(valores, Collections.reverseOrder());
        return valores;
    }

    private static boolean tienePoker(List<Carta> cartas) {
        return contarRepeticiones(cartas, 4);
    }

    private static boolean tieneFull(List<Carta> cartas) {
        return obtenerValorPorRepeticion(cartas, 3) != 0 && obtenerValorPorRepeticionExcluyendo(cartas, 2, List.of(obtenerValorPorRepeticion(cartas, 3))) != 0;
    }

    private static boolean tieneColor(List<Carta> cartas) {
        return obtenerCartasMismoPalo(cartas).size() >= 5;
    }

    private static boolean tieneEscalera(List<Carta> cartas) {
        return obtenerCartaAltaEscalera(cartas) != 0;
    }

    private static boolean tieneTrio(List<Carta> cartas) {
        return contarRepeticiones(cartas, 3);
    }

    private static boolean tieneDoblePareja(List<Carta> cartas) {
        return obtenerParejas(cartas).size() >= 2;
    }

    private static boolean tienePareja(List<Carta> cartas) {
        return contarRepeticiones(cartas, 2);
    }

    private static boolean contarRepeticiones(List<Carta> cartas, int repeticiones) {
        Map<Integer, Integer> contador = new HashMap<>();
        for (Carta carta : cartas) {
            contador.put(carta.getNumero(), contador.getOrDefault(carta.getNumero(), 0) + 1);
        }
        return contador.containsValue(repeticiones);
    }

    private static int obtenerValorPorRepeticion(List<Carta> cartas, int repeticiones) {
        Map<Integer, Integer> contador = new HashMap<>();
        for (Carta carta : cartas) {
            contador.put(carta.getNumero(), contador.getOrDefault(carta.getNumero(), 0) + 1);
        }
        for (Map.Entry<Integer, Integer> entry : contador.entrySet()) {
            if (entry.getValue() == repeticiones) {
                return entry.getKey();
            }
        }
        return 0;
    }

    private static int obtenerValorPorRepeticionExcluyendo(List<Carta> cartas, int repeticiones, List<Integer> excluidos) {
        Map<Integer, Integer> contador = new HashMap<>();
        for (Carta carta : cartas) {
            contador.put(carta.getNumero(), contador.getOrDefault(carta.getNumero(), 0) + 1);
        }
        for (Map.Entry<Integer, Integer> entry : contador.entrySet()) {
            if (entry.getValue() == repeticiones && !excluidos.contains(entry.getKey())) {
                return entry.getKey();
            }
        }
        return 0;
    }

    private static List<Integer> obtenerKickers(List<Carta> cartas, List<Integer> excluidos) {
        List<Integer> kickers = new ArrayList<>();
        for (Carta carta : cartas) {
            if (!excluidos.contains(carta.getNumero())) {
                kickers.add(carta.getNumero());
            }
        }
        Collections.sort(kickers, Collections.reverseOrder());
        return kickers.size() > 3 ? kickers.subList(0, 3) : kickers;
    }

    private static List<Carta> obtenerCartasMismoPalo(List<Carta> cartas) {
        Map<Integer, List<Carta>> cartasPorPalo = new HashMap<>();
        for (Carta carta : cartas) {
            cartasPorPalo.computeIfAbsent(carta.getIdPalo(), k -> new ArrayList<>()).add(carta);
        }

        for (List<Carta> cartasDePalo : cartasPorPalo.values()) {
            if (cartasDePalo.size() >= 5) {
                Collections.sort(cartasDePalo, Comparator.comparingInt(Carta::getNumero).reversed());
                return cartasDePalo.subList(0, 5);
            }
        }

        return new ArrayList<>();
    }

    private static List<Integer> obtenerParejas(List<Carta> cartas) {
        Map<Integer, Integer> contador = new HashMap<>();
        for (Carta carta : cartas) {
            contador.put(carta.getNumero(), contador.getOrDefault(carta.getNumero(), 0) + 1);
        }

        List<Integer> parejas = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : contador.entrySet()) {
            if (entry.getValue() >= 2) {
                parejas.add(entry.getKey());
            }
        }
        return parejas;
    }

    private static int obtenerCartaAltaEscalera(List<Carta> cartas) {
        Set<Integer> valoresUnicos = new HashSet<>();
        for (Carta carta : cartas) {
            valoresUnicos.add(carta.getNumero());
        }

        List<Integer> valores = new ArrayList<>(valoresUnicos);
        Collections.sort(valores, Collections.reverseOrder());

        for (int i = 0; i <= valores.size() - 5; i++) {
            int inicio = valores.get(i);
            boolean esEscalera = true;
            for (int j = 1; j < 5; j++) {
                if (!valoresUnicos.contains(inicio - j)) {
                    esEscalera = false;
                    break;
                }
            }
            if (esEscalera) return inicio;
        }

        if (valoresUnicos.containsAll(Arrays.asList(14, 2, 3, 4, 5))) return 5;

        return 0;
    }
}
