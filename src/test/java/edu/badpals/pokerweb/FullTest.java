package edu.badpals.pokerweb;

import edu.badpals.pokerweb.domain.services.EvaluadorManos;
import edu.badpals.pokerweb.domain.model.Carta;
import edu.badpals.pokerweb.domain.model.Jugador;
import edu.badpals.pokerweb.domain.model.Mano;
import edu.badpals.pokerweb.domain.model.Partida;
import edu.badpals.pokerweb.domain.enums.Palo;
import edu.badpals.pokerweb.domain.enums.ValorCarta;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FullTest {

    @Test
    void testGanadorConFull() {
        // Jugador 1 - tiene trío de 9 y pareja de 5 => Full
        Jugador jugador1 = new Jugador();
        jugador1.setMano(new Mano(List.of(
                new Carta(Palo.TREBOLES, ValorCarta.NUEVE),
                new Carta(Palo.CORAZONES, ValorCarta.CINCO)
        )));
        jugador1.setActivo(true);

        // Jugador 2 - solo una pareja (menos fuerte)
        Jugador jugador2 = new Jugador();
        jugador2.setMano(new Mano(List.of(
                new Carta(Palo.PICAS, ValorCarta.K),
                new Carta(Palo.DIAMANTES, ValorCarta.TRES)
        )));
        jugador2.setActivo(true);

        // Cartas comunitarias: completan el full para el jugador 1
        List<Carta> comunitarias = List.of(
                new Carta(Palo.DIAMANTES, ValorCarta.NUEVE),   // 2º nueve
                new Carta(Palo.PICAS, ValorCarta.NUEVE),       // 3º nueve (trío)
                new Carta(Palo.CORAZONES, ValorCarta.CINCO),   // 2º cinco (pareja)
                new Carta(Palo.TREBOLES, ValorCarta.SIETE),
                new Carta(Palo.PICAS, ValorCarta.CUATRO)
        );

        Partida partida = new Partida();
        partida.setJugadores(List.of(jugador1, jugador2));
        partida.setCartasComunitarias(comunitarias);

        Jugador ganador = EvaluadorManos.determinarGanador(partida);

        assertNotNull(ganador, "Debe haber un ganador válido");
        assertEquals(jugador1, ganador, "El jugador 1 debe ganar con un full de 9 y 5");
    }
}
