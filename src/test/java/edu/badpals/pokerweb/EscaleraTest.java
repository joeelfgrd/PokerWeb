package edu.badpals.pokerweb;

import edu.badpals.pokerweb.auxiliar.EvaluadorManos;
import edu.badpals.pokerweb.model.*;
import edu.badpals.pokerweb.model.enums.Palo;
import edu.badpals.pokerweb.model.enums.ValorCarta;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EscaleraTest {

    @Test
    void testGanadorConEscaleraMedia() {
        // Jugador 1 - escalera: 5-6-7-8-9
        Jugador jugador1 = new Jugador();
        jugador1.setMano(new Mano(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.CINCO),
                new Carta(Palo.TREBOLES, ValorCarta.SEIS)
        )));
        jugador1.setActivo(true);

        Jugador jugador2 = new Jugador();
        jugador2.setMano(new Mano(List.of(
                new Carta(Palo.PICAS, ValorCarta.K),
                new Carta(Palo.DIAMANTES, ValorCarta.A)
        )));
        jugador2.setActivo(true);

        List<Carta> comunitarias = List.of(
                new Carta(Palo.PICAS, ValorCarta.SIETE),
                new Carta(Palo.DIAMANTES, ValorCarta.OCHO),
                new Carta(Palo.CORAZONES, ValorCarta.NUEVE),
                new Carta(Palo.TREBOLES, ValorCarta.CUATRO),
                new Carta(Palo.PICAS, ValorCarta.DOS)
        );

        Partida partida = new Partida();
        partida.setJugadores(List.of(jugador1, jugador2));
        partida.setCartasComunitarias(comunitarias);

        Jugador ganador = EvaluadorManos.determinarGanador(partida);
        assertEquals(jugador1, ganador, "Jugador 1 debe ganar con escalera 5-9");
    }

    @Test
    void testGanadorConEscaleraAltaConAs() {
        // Jugador 1 - escalera: 10-J-Q-K-A
        Jugador jugador1 = new Jugador();
        jugador1.setMano(new Mano(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.A),
                new Carta(Palo.TREBOLES, ValorCarta.K)
        )));
        jugador1.setActivo(true);

        Jugador jugador2 = new Jugador();
        jugador2.setMano(new Mano(List.of(
                new Carta(Palo.PICAS, ValorCarta.NUEVE),
                new Carta(Palo.DIAMANTES, ValorCarta.NUEVE)
        )));
        jugador2.setActivo(true);

        List<Carta> comunitarias = List.of(
                new Carta(Palo.PICAS, ValorCarta.DIEZ),
                new Carta(Palo.DIAMANTES, ValorCarta.J),
                new Carta(Palo.CORAZONES, ValorCarta.Q),
                new Carta(Palo.TREBOLES, ValorCarta.CUATRO),
                new Carta(Palo.PICAS, ValorCarta.DOS)
        );

        Partida partida = new Partida();
        partida.setJugadores(List.of(jugador1, jugador2));
        partida.setCartasComunitarias(comunitarias);

        Jugador ganador = EvaluadorManos.determinarGanador(partida);
        assertEquals(jugador1, ganador, "Jugador 1 debe ganar con escalera alta A-K-Q-J-10");
    }

    @Test
    void testGanadorConEscaleraBajaConAs() {
        // Jugador 1 - escalera: A-2-3-4-5
        Jugador jugador1 = new Jugador();
        jugador1.setMano(new Mano(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.A),
                new Carta(Palo.TREBOLES, ValorCarta.DOS)
        )));
        jugador1.setActivo(true);

        Jugador jugador2 = new Jugador();
        jugador2.setMano(new Mano(List.of(
                new Carta(Palo.PICAS, ValorCarta.K),
                new Carta(Palo.DIAMANTES, ValorCarta.K)
        )));
        jugador2.setActivo(true);

        List<Carta> comunitarias = List.of(
                new Carta(Palo.PICAS, ValorCarta.TRES),
                new Carta(Palo.DIAMANTES, ValorCarta.CUATRO),
                new Carta(Palo.CORAZONES, ValorCarta.CINCO),
                new Carta(Palo.TREBOLES, ValorCarta.SIETE),
                new Carta(Palo.PICAS, ValorCarta.OCHO)
        );

        Partida partida = new Partida();
        partida.setJugadores(List.of(jugador1, jugador2));
        partida.setCartasComunitarias(comunitarias);

        Jugador ganador = EvaluadorManos.determinarGanador(partida);
        assertEquals(jugador1, ganador, "Jugador 1 debe ganar con escalera baja A-2-3-4-5");
    }
}
