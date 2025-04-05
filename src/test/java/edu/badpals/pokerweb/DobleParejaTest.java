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

public class DobleParejaTest {

    @Test
    void testGanadorConDoblePareja() {
        // Jugador 1 - doble pareja (8 y 5)
        Jugador jugador1 = new Jugador();
        jugador1.setMano(new Mano(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.OCHO),
                new Carta(Palo.TREBOLES, ValorCarta.CINCO)
        )));
        jugador1.setActivo(true);

        // Jugador 2 - carta alta K
        Jugador jugador2 = new Jugador();
        jugador2.setMano(new Mano(List.of(
                new Carta(Palo.PICAS, ValorCarta.K),
                new Carta(Palo.DIAMANTES, ValorCarta.TRES)
        )));
        jugador2.setActivo(true);

        // Jugador 3 - carta alta Q
        Jugador jugador3 = new Jugador();
        jugador3.setMano(new Mano(List.of(
                new Carta(Palo.TREBOLES, ValorCarta.Q),
                new Carta(Palo.CORAZONES, ValorCarta.DOS)
        )));
        jugador3.setActivo(true);

        // Cartas comunitarias: completan la doble pareja del jugador1,
        // y evitan formar escalera o color para los demás.
        List<Carta> comunitarias = List.of(
                new Carta(Palo.DIAMANTES, ValorCarta.CINCO),   // pareja para jugador1
                new Carta(Palo.PICAS, ValorCarta.OCHO),        // pareja para jugador1
                new Carta(Palo.TREBOLES, ValorCarta.SIETE),
                new Carta(Palo.CORAZONES, ValorCarta.J),       // no ayuda a nadie
                new Carta(Palo.DIAMANTES, ValorCarta.DOS)      // no ayuda a nadie
        );

        Partida partida = new Partida();
        partida.setJugadores(List.of(jugador1, jugador2, jugador3));
        partida.setCartasComunitarias(comunitarias);

        Jugador ganador = EvaluadorManos.determinarGanador(partida);

        assertNotNull(ganador, "Debe haber un ganador válido");
        assertEquals(jugador1, ganador, "El jugador con doble pareja (8 y 5) debería ganar");
    }
}
