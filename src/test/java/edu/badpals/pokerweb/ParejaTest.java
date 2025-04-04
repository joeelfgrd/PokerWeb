package edu.badpals.pokerweb;

import edu.badpals.pokerweb.auxiliar.EvaluadorManos;
import edu.badpals.pokerweb.model.*;
import edu.badpals.pokerweb.model.enums.Palo;
import edu.badpals.pokerweb.model.enums.ValorCarta;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParejaTest {

    @Test
    void testGanadorConPareja() {
        // Jugador 1 - tiene pareja de 9
        Jugador jugador1 = new Jugador();
        jugador1.setMano(new Mano(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.NUEVE),
                new Carta(Palo.TREBOLES, ValorCarta.NUEVE)
        )));
        jugador1.setActivo(true);

        // Jugador 2 - sin combinación, carta más alta: Q
        Jugador jugador2 = new Jugador();
        jugador2.setMano(new Mano(List.of(
                new Carta(Palo.PICAS, ValorCarta.Q),
                new Carta(Palo.DIAMANTES, ValorCarta.A)
        )));
        jugador2.setActivo(true);

        // Jugador 3 - sin combinación, carta más alta: J
        Jugador jugador3 = new Jugador();
        jugador3.setMano(new Mano(List.of(
                new Carta(Palo.TREBOLES, ValorCarta.J),
                new Carta(Palo.CORAZONES, ValorCarta.SIETE)
        )));
        jugador3.setActivo(true);

        // Cartas comunitarias sin relevancia para combinaciones
        List<Carta> comunitarias = List.of(
                new Carta(Palo.DIAMANTES, ValorCarta.DOS),
                new Carta(Palo.PICAS, ValorCarta.CUATRO),
                new Carta(Palo.TREBOLES, ValorCarta.SEIS),
                new Carta(Palo.CORAZONES, ValorCarta.OCHO),
                new Carta(Palo.PICAS, ValorCarta.TRES)
        );

        // Crear partida con los jugadores y las comunitarias
        Partida partida = new Partida();
        partida.setJugadores(List.of(jugador1, jugador2, jugador3));
        partida.setCartasComunitarias(comunitarias);

        // Evaluar el ganador
        Jugador ganador = EvaluadorManos.determinarGanador(partida);

        // Validar que el ganador sea jugador1 (tiene pareja)
        assertNotNull(ganador, "Debe determinarse un ganador válido");
        assertEquals(jugador1, ganador, "El jugador con pareja de 9 debería ganar");
    }
}
