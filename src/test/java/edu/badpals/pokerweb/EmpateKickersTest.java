package edu.badpals.pokerweb;

import edu.badpals.pokerweb.domain.services.EvaluadorManos;
import edu.badpals.pokerweb.domain.services.ManoEvaluada;
import edu.badpals.pokerweb.domain.model.*;
import edu.badpals.pokerweb.domain.enums.Palo;
import edu.badpals.pokerweb.domain.enums.ValorCarta;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EmpateKickersTest {

    private Partida crearPartidaConJugadores(Jugador j1, Jugador j2, List<Carta> comunitarias) {
        Partida partida = new Partida();
        j1.setActivo(true);
        j2.setActivo(true);
        partida.setJugadores(List.of(j1, j2));
        partida.setCartasComunitarias(comunitarias);
        return partida;
    }

    @Test
    void cartaAltaConKickerDesempate() {
        Jugador j1 = new Jugador();
        j1.setMano(new Mano(List.of(
                new Carta(Palo.PICAS, ValorCarta.K),
                new Carta(Palo.TREBOLES, ValorCarta.CUATRO))));

        Jugador j2 = new Jugador();
        j2.setMano(new Mano(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.K),
                new Carta(Palo.DIAMANTES, ValorCarta.TRES))));

        List<Carta> comunitarias = List.of(
                new Carta(Palo.TREBOLES, ValorCarta.SIETE),
                new Carta(Palo.CORAZONES, ValorCarta.DOS),
                new Carta(Palo.PICAS, ValorCarta.CINCO),
                new Carta(Palo.DIAMANTES, ValorCarta.SEIS),
                new Carta(Palo.CORAZONES, ValorCarta.NUEVE));

        Partida partida = crearPartidaConJugadores(j1, j2, comunitarias);
        Jugador ganador = EvaluadorManos.determinarGanador(partida);
        assertEquals(j1, ganador);
    }

    @Test
    void parejaConKickerDesempate() {
        Jugador j1 = new Jugador();
        j1.setMano(new Mano(List.of(
                new Carta(Palo.PICAS, ValorCarta.CUATRO),
                new Carta(Palo.TREBOLES, ValorCarta.DIEZ))));

        Jugador j2 = new Jugador();
        j2.setMano(new Mano(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.CUATRO),
                new Carta(Palo.DIAMANTES, ValorCarta.NUEVE))));

        List<Carta> comunitarias = List.of(
                new Carta(Palo.TREBOLES, ValorCarta.CUATRO),
                new Carta(Palo.CORAZONES, ValorCarta.SIETE),
                new Carta(Palo.PICAS, ValorCarta.TRES),
                new Carta(Palo.DIAMANTES, ValorCarta.SEIS),
                new Carta(Palo.CORAZONES, ValorCarta.DOS));

        Partida partida = crearPartidaConJugadores(j1, j2, comunitarias);
        Jugador ganador = EvaluadorManos.determinarGanador(partida);
        assertEquals(j1, ganador);
    }

    @Test
    void dobleParejaConKickerDesempate() {
        // j1 tiene doble pareja Q y J con kicker 10, j2 tiene misma doble pareja pero kicker 9
        Jugador j1 = new Jugador();
        j1.setMano(new Mano(List.of(
                new Carta(Palo.PICAS, ValorCarta.Q),
                new Carta(Palo.TREBOLES, ValorCarta.DIEZ))));

        Jugador j2 = new Jugador();
        j2.setMano(new Mano(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.Q),
                new Carta(Palo.DIAMANTES, ValorCarta.NUEVE))));

        List<Carta> comunitarias = List.of(
                new Carta(Palo.TREBOLES, ValorCarta.J),
                new Carta(Palo.CORAZONES, ValorCarta.J),
                new Carta(Palo.PICAS, ValorCarta.SIETE),
                new Carta(Palo.DIAMANTES, ValorCarta.CINCO),
                new Carta(Palo.CORAZONES, ValorCarta.DOS));

        Partida partida = crearPartidaConJugadores(j1, j2, comunitarias);
        Jugador ganador = EvaluadorManos.determinarGanador(partida);
        assertEquals(j1, ganador);
    }

    @Test
    void trioConKickerDesempate() {
        Jugador j1 = new Jugador();
        j1.setMano(new Mano(List.of(
                new Carta(Palo.PICAS, ValorCarta.OCHO),
                new Carta(Palo.TREBOLES, ValorCarta.DIEZ))));

        Jugador j2 = new Jugador();
        j2.setMano(new Mano(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.OCHO),
                new Carta(Palo.DIAMANTES, ValorCarta.NUEVE))));

        List<Carta> comunitarias = List.of(
                new Carta(Palo.TREBOLES, ValorCarta.OCHO),
                new Carta(Palo.CORAZONES, ValorCarta.CINCO),
                new Carta(Palo.PICAS, ValorCarta.TRES),
                new Carta(Palo.DIAMANTES, ValorCarta.SIETE),
                new Carta(Palo.CORAZONES, ValorCarta.DOS));

        Partida partida = crearPartidaConJugadores(j1, j2, comunitarias);
        Jugador ganador = EvaluadorManos.determinarGanador(partida);
        assertEquals(j1, ganador);
    }



    @Test
    void colorConCartaAltaDesempate() {
        Jugador j1 = new Jugador();
        j1.setMano(new Mano(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.K),
                new Carta(Palo.CORAZONES, ValorCarta.OCHO))));

        Jugador j2 = new Jugador();
        j2.setMano(new Mano(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.Q),
                new Carta(Palo.CORAZONES, ValorCarta.NUEVE))));

        List<Carta> comunitarias = List.of(
                new Carta(Palo.CORAZONES, ValorCarta.TRES),
                new Carta(Palo.CORAZONES, ValorCarta.CINCO),
                new Carta(Palo.CORAZONES, ValorCarta.SEIS),
                new Carta(Palo.PICAS, ValorCarta.DOS),
                new Carta(Palo.DIAMANTES, ValorCarta.CUATRO));

        Partida partida = crearPartidaConJugadores(j1, j2, comunitarias);
        Jugador ganador = EvaluadorManos.determinarGanador(partida);
        assertEquals(j1, ganador);
    }

    @Test
    void escaleraConCartaAltaDesempate() {
        Jugador j1 = new Jugador();
        j1.setMano(new Mano(List.of(
                new Carta(Palo.PICAS, ValorCarta.NUEVE),
                new Carta(Palo.TREBOLES, ValorCarta.DIEZ))));

        Jugador j2 = new Jugador();
        j2.setMano(new Mano(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.CINCO),
                new Carta(Palo.DIAMANTES, ValorCarta.CUATRO))));

        List<Carta> comunitarias = List.of(
                new Carta(Palo.TREBOLES, ValorCarta.SEIS),
                new Carta(Palo.CORAZONES, ValorCarta.SIETE),
                new Carta(Palo.PICAS, ValorCarta.OCHO),
                new Carta(Palo.DIAMANTES, ValorCarta.DOS),
                new Carta(Palo.CORAZONES, ValorCarta.K));

        Partida partida = crearPartidaConJugadores(j1, j2, comunitarias);
        Jugador ganador = EvaluadorManos.determinarGanador(partida);
        assertEquals(j1, ganador);
    }


    @Test
    void pokerConKickerDesempate() {
        Jugador j1 = new Jugador();
        j1.setMano(new Mano(List.of(
                new Carta(Palo.PICAS, ValorCarta.CUATRO),
                new Carta(Palo.TREBOLES, ValorCarta.CINCO))));

        Jugador j2 = new Jugador();
        j2.setMano(new Mano(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.CUATRO),
                new Carta(Palo.DIAMANTES, ValorCarta.TRES))));

        List<Carta> comunitarias = List.of(
                new Carta(Palo.TREBOLES, ValorCarta.CUATRO),
                new Carta(Palo.CORAZONES, ValorCarta.CUATRO),
                new Carta(Palo.PICAS, ValorCarta.DOS),
                new Carta(Palo.DIAMANTES, ValorCarta.CINCO),
                new Carta(Palo.CORAZONES, ValorCarta.SIETE));

        Partida partida = crearPartidaConJugadores(j1, j2, comunitarias);
        Jugador ganador = EvaluadorManos.determinarGanador(partida);
        assertEquals(j1, ganador);
    }



    @Test
    void evaluarVariasManosAleatorias() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("\n--- Partida " + i + " ---");
            Baraja baraja = new Baraja();
            baraja.barajar();

            Partida partida = new Partida();
            List<Jugador> jugadores = new ArrayList<>();

            for (int j = 0; j < 4; j++) {
                Jugador jugador = new Jugador();
                List<Carta> mano = new ArrayList<>();
                mano.add(baraja.repartirCarta());
                mano.add(baraja.repartirCarta());
                jugador.setMano(new Mano(mano));
                jugador.setActivo(true);
                jugadores.add(jugador);
            }

            List<Carta> comunitarias = new ArrayList<>();
            for (int c = 0; c < 5; c++) {
                comunitarias.add(baraja.repartirCarta());
            }

            partida.setJugadores(jugadores);
            partida.setCartasComunitarias(comunitarias);

            Jugador ganador = EvaluadorManos.determinarGanador(partida);
            assertNotNull(ganador);

            ManoEvaluada manoGanadora = EvaluadorManos.evaluar(new ArrayList<>() {{
                addAll(ganador.getMano().getCartas());
                addAll(partida.getCartasComunitarias());
            }});

            for (Jugador j : jugadores) {
                System.out.println("Jugador con mano: " + j.getMano().getCartas());
            }
            System.out.println("Cartas comunitarias: " + comunitarias);
            System.out.println("Ganador: " + ganador.getMano().getCartas());
            System.out.println("Tipo de jugada ganadora: " + manoGanadora);
        }
    }

}
