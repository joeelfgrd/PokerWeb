package edu.badpals.pokerweb;

import edu.badpals.pokerweb.domain.services.EvaluadorManos;
import edu.badpals.pokerweb.domain.model.Carta;
import edu.badpals.pokerweb.domain.model.Jugador;
import edu.badpals.pokerweb.domain.model.Mano;
import edu.badpals.pokerweb.domain.model.Partida;
import edu.badpals.pokerweb.domain.enums.Palo;
import edu.badpals.pokerweb.domain.enums.ValorCarta;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CartaAltaTest {

    @Test
    void testGanadorPorCartaAlta() {
        // Jugador 1 - carta más alta: K
        Jugador jugador1 = new Jugador();
        jugador1.setMano(new Mano(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.K),
                new Carta(Palo.TREBOLES, ValorCarta.SIETE)
        )));
        jugador1.setActivo(true);

        // Jugador 2 - carta más alta: J
        Jugador jugador2 = new Jugador();
        jugador2.setMano(new Mano(List.of(
                new Carta(Palo.PICAS, ValorCarta.J),
                new Carta(Palo.DIAMANTES, ValorCarta.SEIS)
        )));
        jugador2.setActivo(true);

        // Jugador 3 - carta más alta: 10
        Jugador jugador3 = new Jugador();
        jugador3.setMano(new Mano(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.DIEZ),
                new Carta(Palo.TREBOLES, ValorCarta.CUATRO)
        )));
        jugador3.setActivo(true);

        // Cartas comunitarias sin impacto en combinaciones
        List<Carta> comunitarias = List.of(
                new Carta(Palo.DIAMANTES, ValorCarta.DOS),
                new Carta(Palo.PICAS, ValorCarta.CINCO),
                new Carta(Palo.TREBOLES, ValorCarta.OCHO),
                new Carta(Palo.CORAZONES, ValorCarta.TRES),
                new Carta(Palo.PICAS, ValorCarta.NUEVE)
        );

        // Partida configurada
        Partida partida = new Partida();
        partida.setJugadores(List.of(jugador1, jugador2, jugador3));
        partida.setCartasComunitarias(comunitarias);

        // Evaluar ganador
        Jugador ganador = EvaluadorManos.determinarGanador(partida);

        // Verificación
        assertNotNull(ganador, "El método debe devolver un jugador válido como ganador");
        assertEquals(jugador1, ganador, "El jugador con carta alta K debería ganar");
    }
}
