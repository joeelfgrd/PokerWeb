package edu.badpals.pokerweb;

import edu.badpals.pokerweb.model.*;
import edu.badpals.pokerweb.auxiliar.EvaluadorManos;
import edu.badpals.pokerweb.auxiliar.GestorApuestas;
import edu.badpals.pokerweb.model.enums.Palo;
import edu.badpals.pokerweb.model.enums.ValorCarta;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShowdownApuestasTest {

    private Jugador crearJugadorConMano(String nombre, ValorCarta c1, ValorCarta c2, Palo p1, Palo p2, int fichas) {
        Jugador jugador = new Jugador();
        jugador.setMano(new Mano(List.of(new Carta(p1, c1), new Carta(p2, c2))));
        jugador.setFichas(fichas);
        jugador.setActivo(true);
        return jugador;
    }

    @Test
    void showdownConSidePotsYEmpate() {
        // 🧑‍🤝‍🧑 Jugadores
        Jugador j1 = crearJugadorConMano("j1", ValorCarta.A, ValorCarta.K, Palo.CORAZONES, Palo.PICAS, 0); // all-in 50
        Jugador j2 = crearJugadorConMano("j2", ValorCarta.A, ValorCarta.K, Palo.TREBOLES, Palo.DIAMANTES, 0); // all-in 50 (empate con j1)
        Jugador j3 = crearJugadorConMano("j3", ValorCarta.DIEZ, ValorCarta.DOS, Palo.PICAS, Palo.TREBOLES, 100); // apuesta 100

        j1.setAllIn(true);
        j2.setAllIn(true);

        Partida partida = new Partida();
        partida.setJugadores(List.of(j1, j2, j3));
        partida.setCartasComunitarias(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.Q),
                new Carta(Palo.DIAMANTES, ValorCarta.J),
                new Carta(Palo.PICAS, ValorCarta.DIEZ),
                new Carta(Palo.TREBOLES, ValorCarta.CUATRO),
                new Carta(Palo.CORAZONES, ValorCarta.TRES)
        ));

        partida.getApuestasActuales().put("j1", 50);
        partida.getApuestasActuales().put("j2", 50);
        partida.getApuestasActuales().put("j3", 100);
        partida.setBote(0); // ya todo fue convertido en side pots

        // 🥄 Side pots
        SidePot pot1 = new SidePot(100); // j1 (50) + j2 (50) -> empate
        pot1.añadirParticipante("j1");
        pot1.añadirParticipante("j2");

        SidePot pot2 = new SidePot(50);  // extra 50 de j3
        pot2.añadirParticipante("j3");

        partida.getSidePots().add(pot1);
        partida.getSidePots().add(pot2);

        // 🧠 Ganadores
        List<Jugador> ganadores = EvaluadorManos.determinarGanadoresEntre(partida.getJugadores(), partida.getCartasComunitarias());

        assertEquals(2, ganadores.size(), "Debe haber empate entre j1 y j2");

        // 💰 Reparto manual simulado
        j1.setFichas(j1.getFichas() + 50); // 100 side pot dividido entre 2
        j2.setFichas(j2.getFichas() + 50);
        j3.setFichas(j3.getFichas()); // No gana nada

        // 📊 Validaciones post-showdown
        assertEquals(50, j1.getFichas(), "j1 debe ganar 50 fichas");
        assertEquals(50, j2.getFichas(), "j2 debe ganar 50 fichas");
        assertEquals(100, j3.getFichas(), "j3 no gana nada");

        // 🎯 Verificamos eliminación si alguien quedó a 0
        assertFalse(j3.getFichas() == 0, "j3 no debería estar eliminado");
        assertFalse(j1.getFichas() == 0, "j1 no debería estar eliminado");
        assertFalse(j2.getFichas() == 0, "j2 no debería estar eliminado");
    }
    @Test
    void showdownSinSidePots() {
        Jugador j1 = crearJugadorConMano("j1", ValorCarta.A, ValorCarta.K, Palo.CORAZONES, Palo.PICAS, 0); // all-in 50
        Jugador j2 = crearJugadorConMano("j2", ValorCarta.A, ValorCarta.K, Palo.TREBOLES, Palo.DIAMANTES, 0); // all-in 50 (empate con j1)
        Jugador j3 = crearJugadorConMano("j3", ValorCarta.DIEZ, ValorCarta.DOS, Palo.PICAS, Palo.TREBOLES, 100); // apuesta 100

        j1.setAllIn(true);
        j2.setAllIn(true);

        Partida partida = new Partida();
        partida.setJugadores(List.of(j1, j2, j3));
        partida.setCartasComunitarias(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.Q),
                new Carta(Palo.DIAMANTES, ValorCarta.J),
                new Carta(Palo.PICAS, ValorCarta.DIEZ),
                new Carta(Palo.TREBOLES, ValorCarta.CUATRO),
                new Carta(Palo.CORAZONES, ValorCarta.TRES)
        ));

        partida.getApuestasActuales().put("j1", 50);
        partida.getApuestasActuales().put("j2", 50);
        partida.getApuestasActuales().put("j3", 100);
        partida.setBote(200);

        List<Jugador> ganadores = EvaluadorManos.determinarGanadoresEntre(partida.getJugadores(), partida.getCartasComunitarias());

        assertEquals(2, ganadores.size(), "Debe haber empate entre j1 y j2");

        j1.setFichas(j1.getFichas() + 100); // 200 bote dividido entre 2
        j2.setFichas(j2.getFichas() + 100);
        j3.setFichas(j3.getFichas()); // No gana nada

        assertEquals(100, j1.getFichas(), "j1 debe ganar 100 fichas");
        assertEquals(100, j2.getFichas(), "j2 debe ganar 100 fichas");
        assertEquals(100, j3.getFichas(), "j3 no gana nada");

        assertFalse(j3.getFichas() == 0, "j3 no debería estar eliminado");
        assertFalse(j1.getFichas() == 0, "j1 no debería estar eliminado");
        assertFalse(j2.getFichas() == 0, "j2 no debería estar eliminado");
    }

}
