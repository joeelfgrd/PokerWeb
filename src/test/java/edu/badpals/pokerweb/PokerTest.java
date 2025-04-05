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

public class PokerTest {

    @Test
    void testGanadorConPoker() {
        // Jugador 1 - tendrá póker de 9s
        Jugador jugador1 = new Jugador();
        jugador1.setMano(new Mano(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.NUEVE),
                new Carta(Palo.TREBOLES, ValorCarta.NUEVE)
        )));
        jugador1.setActivo(true);

        // Jugador 2 - solo carta alta
        Jugador jugador2 = new Jugador();
        jugador2.setMano(new Mano(List.of(
                new Carta(Palo.PICAS, ValorCarta.A),
                new Carta(Palo.DIAMANTES, ValorCarta.CUATRO)
        )));
        jugador2.setActivo(true);

        // Cartas comunitarias: completan el póker para jugador1
        List<Carta> comunitarias = List.of(
                new Carta(Palo.DIAMANTES, ValorCarta.NUEVE),
                new Carta(Palo.PICAS, ValorCarta.NUEVE),
                new Carta(Palo.TREBOLES, ValorCarta.CINCO),
                new Carta(Palo.CORAZONES, ValorCarta.DOS),
                new Carta(Palo.PICAS, ValorCarta.TRES)
        );

        Partida partida = new Partida();
        partida.setJugadores(List.of(jugador1, jugador2));
        partida.setCartasComunitarias(comunitarias);

        Jugador ganador = EvaluadorManos.determinarGanador(partida);

        assertNotNull(ganador, "Debe haber un ganador válido");
        assertEquals(jugador1, ganador, "El jugador 1 debe ganar con póker de 9s");
    }
}
