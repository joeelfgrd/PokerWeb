package edu.badpals.pokerweb;

import edu.badpals.pokerweb.auxiliar.EvaluadorManos;
import edu.badpals.pokerweb.model.*;
import edu.badpals.pokerweb.model.enums.Palo;
import edu.badpals.pokerweb.model.enums.ValorCarta;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ColorTest {

    @Test
    void testGanadorConColor() {
        // Jugador 1 - va a tener color de tréboles
        Jugador jugador1 = new Jugador();
        jugador1.setMano(new Mano(List.of(
                new Carta(Palo.TREBOLES, ValorCarta.CUATRO),
                new Carta(Palo.TREBOLES, ValorCarta.SIETE)
        )));
        jugador1.setActivo(true);

        // Jugador 2 - solo carta alta
        Jugador jugador2 = new Jugador();
        jugador2.setMano(new Mano(List.of(
                new Carta(Palo.PICAS, ValorCarta.A),
                new Carta(Palo.CORAZONES, ValorCarta.NUEVE)
        )));
        jugador2.setActivo(true);

        // Cartas comunitarias: completan el color para jugador 1
        List<Carta> comunitarias = List.of(
                new Carta(Palo.TREBOLES, ValorCarta.DOS),
                new Carta(Palo.TREBOLES, ValorCarta.CINCO),
                new Carta(Palo.TREBOLES, ValorCarta.K),
                new Carta(Palo.PICAS, ValorCarta.TRES),
                new Carta(Palo.DIAMANTES, ValorCarta.OCHO)
        );

        Partida partida = new Partida();
        partida.setJugadores(List.of(jugador1, jugador2));
        partida.setCartasComunitarias(comunitarias);

        Jugador ganador = EvaluadorManos.determinarGanador(partida);

        assertNotNull(ganador, "Debe haber un ganador válido");
        assertEquals(jugador1, ganador, "El jugador 1 debe ganar con un color de tréboles");
    }
}
