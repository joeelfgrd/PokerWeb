
package edu.badpals.pokerweb;

import edu.badpals.pokerweb.domain.services.EvaluadorManos;
import edu.badpals.pokerweb.domain.model.*;
import edu.badpals.pokerweb.domain.enums.Palo;
import edu.badpals.pokerweb.domain.enums.ValorCarta;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SidePotTest{
@Test
public void sidePotEmpate() {
    Jugador j1 = new Jugador();
    j1.setMano(new Mano(List.of(
            new Carta(Palo.PICAS, ValorCarta.K),
            new Carta(Palo.TREBOLES, ValorCarta.CUATRO)
    )));
    j1.setActivo(true);

    Jugador j2 = new Jugador();
    j2.setMano(new Mano(List.of(
            new Carta(Palo.CORAZONES, ValorCarta.K),
            new Carta(Palo.DIAMANTES, ValorCarta.CUATRO)
    )));
    j2.setActivo(true);

    Jugador j3 = new Jugador();
    j3.setMano(new Mano(List.of(
            new Carta(Palo.TREBOLES, ValorCarta.DOS),
            new Carta(Palo.CORAZONES, ValorCarta.TRES)
    )));
    j3.setActivo(true);

    List<Carta> comunitarias = List.of(
            new Carta(Palo.TREBOLES, ValorCarta.SIETE),
            new Carta(Palo.CORAZONES, ValorCarta.DOS),
            new Carta(Palo.PICAS, ValorCarta.CINCO),
            new Carta(Palo.DIAMANTES, ValorCarta.SEIS),
            new Carta(Palo.CORAZONES, ValorCarta.NUEVE)
    );

    Partida partida = new Partida();
    partida.setJugadores(List.of(j1, j2, j3));
    partida.setCartasComunitarias(comunitarias);

    SidePot pot = new SidePot(100);
    pot.añadirParticipante(j1.getId());
    pot.añadirParticipante(j2.getId());
    partida.getSidePots().add(pot);

    List<Jugador> ganadores = EvaluadorManos.determinarGanadoresEntre(List.of(j1, j2), comunitarias);

    assertEquals(2, ganadores.size(), "Debe haber un empate entre dos jugadores");
    assertTrue(ganadores.contains(j1));
    assertTrue(ganadores.contains(j2));
}

    @Test
    void sidePotGanadorUnico() {
        Jugador j1 = new Jugador();
        j1.setMano(new Mano(List.of(
                new Carta(Palo.PICAS, ValorCarta.K),
                new Carta(Palo.TREBOLES, ValorCarta.CUATRO)
        )));
        j1.setActivo(true);

        Jugador j2 = new Jugador();
        j2.setMano(new Mano(List.of(
                new Carta(Palo.CORAZONES, ValorCarta.J),
                new Carta(Palo.DIAMANTES, ValorCarta.CUATRO)
        )));
        j2.setActivo(true);

        List<Carta> comunitarias = List.of(
                new Carta(Palo.TREBOLES, ValorCarta.SIETE),
                new Carta(Palo.CORAZONES, ValorCarta.DOS),
                new Carta(Palo.PICAS, ValorCarta.CINCO),
                new Carta(Palo.DIAMANTES, ValorCarta.SEIS),
                new Carta(Palo.CORAZONES, ValorCarta.NUEVE)
        );

        Partida partida = new Partida();
        partida.setJugadores(List.of(j1, j2));
        partida.setCartasComunitarias(comunitarias);

        SidePot pot = new SidePot(60);
        pot.añadirParticipante(j1.getId());
        pot.añadirParticipante(j2.getId());
        partida.getSidePots().add(pot);

        List<Jugador> ganadores = EvaluadorManos.determinarGanadoresEntre(List.of(j1, j2), comunitarias);

        assertEquals(1, ganadores.size(), "Debe haber un solo ganador");
        assertEquals(j1, ganadores.get(0), "El jugador 1 debe ganar el side pot");
    }
    @Test
    void sidePotConAllInsCruzados() {
        Jugador j1 = new Jugador(); // All-in con 50
        j1.setMano(new Mano(List.of(new Carta(Palo.PICAS, ValorCarta.K), new Carta(Palo.TREBOLES, ValorCarta.DOS))));
        j1.setActivo(true);
        j1.setFichas(0);

        Jugador j2 = new Jugador(); // All-in con 100
        j2.setMano(new Mano(List.of(new Carta(Palo.PICAS, ValorCarta.Q), new Carta(Palo.TREBOLES, ValorCarta.TRES))));
        j2.setActivo(true);
        j2.setFichas(0);

        Jugador j3 = new Jugador(); // Tiene 100 aún, iguala y tiene fichas restantes
        j3.setMano(new Mano(List.of(new Carta(Palo.PICAS, ValorCarta.J), new Carta(Palo.TREBOLES, ValorCarta.CUATRO))));
        j3.setActivo(true);
        j3.setFichas(100);

        Partida partida = new Partida();
        partida.setJugadores(List.of(j1, j2, j3));
        partida.setCartasComunitarias(List.of(
                new Carta(Palo.PICAS, ValorCarta.CINCO),
                new Carta(Palo.TREBOLES, ValorCarta.SIETE),
                new Carta(Palo.DIAMANTES, ValorCarta.NUEVE),
                new Carta(Palo.CORAZONES, ValorCarta.DIEZ),
                new Carta(Palo.PICAS, ValorCarta.OCHO)
        ));
        partida.setBote(50); // El sobrante

        SidePot side1 = new SidePot(150); // J1, J2, J3 = 50 x 3
        side1.añadirParticipante(j1.getId());
        side1.añadirParticipante(j2.getId());
        side1.añadirParticipante(j3.getId());

        SidePot side2 = new SidePot(100); // J2 y J3 = 50 adicionales
        side2.añadirParticipante(j2.getId());
        side2.añadirParticipante(j3.getId());

        partida.getSidePots().add(side1);
        partida.getSidePots().add(side2);

        List<Jugador> ganadoresSide1 = EvaluadorManos.determinarGanadoresEntre(List.of(j1, j2, j3), partida.getCartasComunitarias());
        List<Jugador> ganadoresSide2 = EvaluadorManos.determinarGanadoresEntre(List.of(j2, j3), partida.getCartasComunitarias());

        assertFalse(ganadoresSide1.isEmpty());
        assertFalse(ganadoresSide2.isEmpty());

        System.out.println("Ganadores SidePot1: " + ganadoresSide1.size());
        System.out.println("Ganadores SidePot2: " + ganadoresSide2.size());
    }

}

