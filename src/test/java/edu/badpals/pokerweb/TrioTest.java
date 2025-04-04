package edu.badpals.pokerweb;

import edu.badpals.pokerweb.auxiliar.EvaluadorManos;
import edu.badpals.pokerweb.model.*;
import edu.badpals.pokerweb.model.enums.Palo;
import edu.badpals.pokerweb.model.enums.ValorCarta;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TrioTest {

    @Test
    void testGanadorConTrio() {
        // Jugador 1 - tiene un trío de 7
        Jugador jugador1 = new Jugador();
        jugador1.setMano(new Mano(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.SIETE),
                new Carta(Palo.TREBOLES, ValorCarta.SIETE)
        )));
        jugador1.setActivo(true);

        // Jugador 2 - solo carta alta (A)
        Jugador jugador2 = new Jugador();
        jugador2.setMano(new Mano(List.of(
                new Carta(Palo.PICAS, ValorCarta.A),
                new Carta(Palo.DIAMANTES, ValorCarta.DOS)
        )));
        jugador2.setActivo(true);

        // Jugador 3 - solo carta alta (K)
        Jugador jugador3 = new Jugador();
        jugador3.setMano(new Mano(List.of(
                new Carta(Palo.TREBOLES, ValorCarta.K),
                new Carta(Palo.CORAZONES, ValorCarta.TRES)
        )));
        jugador3.setActivo(true);

        // Cartas comunitarias: una tercera carta para el trío de 7
        // No deben ayudar a los otros jugadores
        List<Carta> comunitarias = List.of(
                new Carta(Palo.DIAMANTES, ValorCarta.SIETE), // tercera carta para el trío de jugador1
                new Carta(Palo.PICAS, ValorCarta.CINCO),
                new Carta(Palo.TREBOLES, ValorCarta.NUEVE),
                new Carta(Palo.CORAZONES, ValorCarta.CUATRO),
                new Carta(Palo.PICAS, ValorCarta.DIEZ)
        );

        Partida partida = new Partida();
        partida.setJugadores(List.of(jugador1, jugador2, jugador3));
        partida.setCartasComunitarias(comunitarias);

        Jugador ganador = EvaluadorManos.determinarGanador(partida);

        assertNotNull(ganador, "Debe haber un ganador válido");
        assertEquals(jugador1, ganador, "El jugador con trío de 7 debería ganar");
    }
}
