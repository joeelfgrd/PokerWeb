package edu.badpals.pokerweb.auxiliar;

import edu.badpals.pokerweb.model.Carta;
import edu.badpals.pokerweb.model.Jugador;
import edu.badpals.pokerweb.model.Partida;

import java.util.*;

public class EvaluadorManosV2 {

    public static Jugador determinarGanador(Partida partida) {
        Jugador ganador = null;
        ManoEvaluada mejorMano = null;

        for (Jugador jugador : partida.getJugadores()) {
            if (jugador.isActivo() && jugador.getMano() != null) {
                List<Carta> todasCartas = new ArrayList<>();
                todasCartas.addAll(jugador.getMano().getCartas());
                todasCartas.addAll(partida.getCartasComunitarias());

                ManoEvaluada manoEvaluada = EvaluadorManosV2.evaluar(todasCartas);

                if (mejorMano == null || manoEvaluada.compareTo(mejorMano) > 0) {
                    mejorMano = manoEvaluada;
                    ganador = jugador;
                }
            }
        }

        return ganador;
    }

    public static ManoEvaluada evaluar(List<Carta> cartas) {
        if (tienePoker(cartas)) {
            int valor = obtenerValorPorRepeticion(cartas, 4);
            List<Integer> kickers = obtenerKickers(cartas, List.of(valor));
            return new ManoEvaluada(700, valor, kickers);
        }
        if (tieneFull(cartas)) {
            int trio = obtenerValorPorRepeticion(cartas, 3);
            int pareja = obtenerValorPorRepeticionExcluyendo(cartas, 2, List.of(trio));
            return new ManoEvaluada(600, trio, List.of(pareja));
        }
        if (tieneColor(cartas)) {
            List<Carta> color = obtenerCartasMismoPalo(cartas);
            List<Integer> valores = color.stream().map(Carta::getNumero).sorted(Comparator.reverseOrder()).toList();
            return new ManoEvaluada(500, valores.get(0), valores.subList(1, Math.min(5, valores.size())));
        }
        if (tieneEscalera(cartas)) {
            int alta = obtenerCartaAltaEscalera(cartas);
            return new ManoEvaluada(400, alta, List.of());
        }
        if (tieneTrio(cartas)) {
            int valor = obtenerValorPorRepeticion(cartas, 3);
            List<Integer> kickers = obtenerKickers(cartas, List.of(valor));
            return new ManoEvaluada(300, valor, kickers);
        }
        if (tieneDoblePareja(cartas)) {
            List<Integer> parejas = obtenerParejas(cartas);
            parejas.sort(Comparator.reverseOrder());
            List<Integer> kickers = obtenerKickers(cartas, parejas);
            return new ManoEvaluada(200, parejas.get(0), List.of(parejas.get(1), kickers.get(0)));
        }
        if (tienePareja(cartas)) {
            int valor = obtenerValorPorRepeticion(cartas, 2);
            List<Integer> kickers = obtenerKickers(cartas, List.of(valor));
            return new ManoEvaluada(100, valor, kickers);
        }

        List<Integer> ordenadas = cartas.stream()
                .map(Carta::getNumero)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();

        return new ManoEvaluada(0, ordenadas.get(0), ordenadas.subList(1, Math.min(4, ordenadas.size())));
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
        return contador.entrySet().stream()
                .filter(e -> e.getValue() == repeticiones)
                .map(Map.Entry::getKey)
                .max(Integer::compareTo)
                .orElse(0);
    }

    private static int obtenerValorPorRepeticionExcluyendo(List<Carta> cartas, int repeticiones, List<Integer> excluidos) {
        Map<Integer, Integer> contador = new HashMap<>();
        for (Carta carta : cartas) {
            contador.put(carta.getNumero(), contador.getOrDefault(carta.getNumero(), 0) + 1);
        }
        return contador.entrySet().stream()
                .filter(e -> e.getValue() >= repeticiones && !excluidos.contains(e.getKey()))
                .map(Map.Entry::getKey)
                .max(Integer::compareTo)
                .orElse(0);
    }

    private static List<Integer> obtenerKickers(List<Carta> cartas, List<Integer> excluidos) {
        return cartas.stream()
                .map(Carta::getNumero)
                .filter(n -> !excluidos.contains(n))
                .distinct()
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .toList();
    }

    private static List<Carta> obtenerCartasMismoPalo(List<Carta> cartas) {
        Map<Integer, List<Carta>> porPalo = new HashMap<>();
        for (Carta carta : cartas) {
            porPalo.computeIfAbsent(carta.getIdPalo(), k -> new ArrayList<>()).add(carta);
        }
        return porPalo.values().stream()
                .filter(lista -> lista.size() >= 5)
                .map(lista -> lista.stream()
                        .sorted(Comparator.comparingInt(Carta::getNumero).reversed())
                        .limit(5)
                        .toList())
                .findFirst()
                .orElse(new ArrayList<>());
    }

    private static List<Integer> obtenerParejas(List<Carta> cartas) {
        Map<Integer, Integer> contador = new HashMap<>();
        for (Carta carta : cartas) {
            contador.put(carta.getNumero(), contador.getOrDefault(carta.getNumero(), 0) + 1);
        }
        List<Integer> parejas = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : contador.entrySet()) {
            if (entry.getValue() >= 2) parejas.add(entry.getKey());
        }
        return parejas;
    }

    private static int obtenerCartaAltaEscalera(List<Carta> cartas) {
        Set<Integer> valoresUnicos = new HashSet<>();
        for (Carta carta : cartas) {
            valoresUnicos.add(carta.getNumero());
        }

        List<Integer> valores = new ArrayList<>(valoresUnicos);
        valores.sort(Collections.reverseOrder()); // de mayor a menor

        for (int i = 0; i <= valores.size() - 5; i++) {
            int inicio = valores.get(i);
            boolean escalera = true;
            for (int j = 1; j < 5; j++) {
                if (!valoresUnicos.contains(inicio - j)) {
                    escalera = false;
                    break;
                }
            }
            if (escalera) return inicio;
        }

        // Escalera baja A-2-3-4-5 (donde 14 es el As)
        if (valoresUnicos.containsAll(List.of(14, 2, 3, 4, 5))) return 5;

        return 0;
    }


}