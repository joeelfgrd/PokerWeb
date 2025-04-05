package edu.badpals.pokerweb;

import edu.badpals.pokerweb.domain.model.Baraja;
import edu.badpals.pokerweb.domain.model.Carta;
import org.junit.jupiter.api.RepeatedTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BarajaTest {

    @RepeatedTest(1000)
    public void testNoHayCartasRepetidasEnPartida() {
        Baraja baraja = new Baraja();
        Set<Carta> todasLasCartas = new HashSet<>();

        for (int i = 0; i < 4; i++) {
            Carta carta1 = baraja.repartirCarta();
            Carta carta2 = baraja.repartirCarta();
            assertTrue(todasLasCartas.add(carta1), "Carta repetida en manos privadas");
            assertTrue(todasLasCartas.add(carta2), "Carta repetida en manos privadas");
        }

        for (int i = 0; i < 3; i++) {
            Carta flopCard = baraja.repartirCarta();
            assertTrue(todasLasCartas.add(flopCard), "Carta repetida en el flop");
        }

        Carta turn = baraja.repartirCarta();
        assertTrue(todasLasCartas.add(turn), "Carta repetida en el turn");

        Carta river = baraja.repartirCarta();
        assertTrue(todasLasCartas.add(river), "Carta repetida en el river");

        assertEquals(13, todasLasCartas.size(), "No hay 13 cartas únicas");
    }
}
