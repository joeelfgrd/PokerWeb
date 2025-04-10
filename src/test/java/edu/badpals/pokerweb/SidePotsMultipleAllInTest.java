package edu.badpals.pokerweb;

import edu.badpals.pokerweb.domain.enums.Palo;
import edu.badpals.pokerweb.domain.enums.ValorCarta;
import edu.badpals.pokerweb.domain.model.Carta;
import edu.badpals.pokerweb.domain.model.Jugador;
import edu.badpals.pokerweb.domain.model.Mano;
import edu.badpals.pokerweb.domain.model.Partida;
import edu.badpals.pokerweb.domain.model.SidePot;
import edu.badpals.pokerweb.domain.services.GestorApuestas;
import edu.badpals.pokerweb.domain.services.GameSessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test que verifica la formación de múltiples side pots
 * cuando varios jugadores hacen all-in en diferentes momentos
 * y otros siguen subiendo la apuesta.
 */
public class SidePotsMultipleAllInTest {

    private GestorApuestas gestorApuestas;
    private Partida partida;
    private Jugador j1, j2, j3, j4;

    @BeforeEach
    void setUp() {
        gestorApuestas = new GestorApuestas();

        // Creamos la partida y algunas cartas comunitarias (solo por completitud)
        partida = new Partida();

        // Jugadores con fichas distintas
        j1 = crearJugador("j1", 50);
        j2 = crearJugador("j2", 100);
        j3 = crearJugador("j3", 150);
        j4 = crearJugador("j4", 300);

        partida.setJugadores(List.of(j1, j2, j3, j4));

        GameSessionManager.iniciarPartida(partida); // baraja y turno
    }

    private Jugador crearJugador(String id, int fichas) {
        Jugador jug = new Jugador();
        jug.setFichas(fichas);
        jug.setActivo(true);
        // Ojo: "id" en tu Jugador es un @Id. 
        // Si necesitas sets, haz un "setId".
        // Por simplicity, haré reflexion:
        try {
            java.lang.reflect.Field f = Jugador.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(jug, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jug;
    }

    @Test
    void testSidePotsConVariosAllIn() {
        // Escenario de apuestas:
        // Turno j1 -> allIn(50)
        gestorApuestas.allIn(partida, "j1");
        assertTrue(j1.isAllIn());
        assertEquals(0, j1.getFichas());

        // Turno avanza -> j2 iguala(50)
        gestorApuestas.igualar(partida, "j2");
        // j2 fichas = 50
        assertEquals(50, j2.getFichas());

        // Turno -> j3 apuesta(100) => sube 50 extra sobre la base de 50
        gestorApuestas.apostar(partida, "j3", 100);
        // j3 fichas = 50
        assertEquals(50, j3.getFichas());

        // Turno -> j4 iguala(100)
        gestorApuestas.igualar(partida, "j4");
        // j4 fichas = 200
        assertEquals(200, j4.getFichas());

        // Recolectamos side pots
        List<SidePot> sidePots = partida.getSidePots();
        assertFalse(sidePots.isEmpty(), "Deben haberse formado side pots");

        // Con 4 jugadores donde j1 allIn(50), j2 sube(50), j3 sube(100), j4 iguala(100)
        // Deberíamos tener 2 side pots:
        // pot1 con 50*4 = 200 (j1, j2, j3, j4)
        // pot2 con 50*2 = 100 (j3, j4)

        assertTrue(sidePots.size() >= 2, "Debe haber al menos 2 side pots");

        SidePot pot1 = sidePots.get(0);
        SidePot pot2 = sidePots.get(1);

        assertEquals(200, pot1.getCantidad());
        assertEquals(100, pot2.getCantidad());

        assertTrue(pot1.getParticipantes().contains("j1"));
        assertTrue(pot1.getParticipantes().contains("j2"));
        assertTrue(pot1.getParticipantes().contains("j3"));
        assertTrue(pot1.getParticipantes().contains("j4"));

        assertTrue(pot2.getParticipantes().contains("j3"));
        assertTrue(pot2.getParticipantes().contains("j4"));

        System.out.println("Pot1 = " + pot1.getCantidad() + ", participantes = " + pot1.getParticipantes());
        System.out.println("Pot2 = " + pot2.getCantidad() + ", participantes = " + pot2.getParticipantes());
    }
}
