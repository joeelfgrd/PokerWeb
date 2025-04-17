package edu.badpals.pokerweb;

import edu.badpals.pokerweb.domain.enums.FaseJuego;
import edu.badpals.pokerweb.domain.enums.Palo;
import edu.badpals.pokerweb.domain.enums.ValorCarta;
import edu.badpals.pokerweb.domain.model.*;
import edu.badpals.pokerweb.domain.services.GameSessionManager;
import edu.badpals.pokerweb.domain.services.GestorApuestas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GestorApuestasTest {

    private GestorApuestas gestorApuestas;
    private Partida partida;
    private Jugador jugador1;
    private Jugador jugador2;

    @BeforeEach
    public void setUp() {
        gestorApuestas = new GestorApuestas();

        // Crear partida
        partida = new Partida();

        // Crear mesa
        Mesa mesa = new Mesa();

        // Crear usuarios
        Usuario u1 = new Usuario();
        u1.setId("u1");
        u1.setNombreCompleto("Jugador 1");
        u1.setDinero(1000);

        Usuario u2 = new Usuario();
        u2.setId("u2");
        u2.setNombreCompleto("Jugador 2");
        u2.setDinero(1000);

        // Crear jugadores
        jugador1 = new Jugador(u1, mesa, partida);
        jugador2 = new Jugador(u2, mesa, partida);

        partida.setJugadores(List.of(jugador1, jugador2));

        GameSessionManager.iniciarPartida(partida);
    }

    @Test
    public void testApostarYAvanzarTurno() {
        // Asegúrate que ambos jugadores están activos y tienen IDs únicos
        jugador1.setFichas(1000);
        jugador2.setFichas(1000);
        jugador1.setActivo(true);
        jugador2.setActivo(true);

        String primerTurno = GameSessionManager.getJugadorEnTurno(partida.getId(), partida.getJugadores());

        gestorApuestas.apostar(partida, primerTurno, 100);

        String segundoTurno = GameSessionManager.getJugadorEnTurno(partida.getId(), partida.getJugadores());

        // Imprimir para debug
        System.out.println("Primer turno: " + primerTurno);
        System.out.println("Segundo turno: " + segundoTurno);

        assertNotEquals(primerTurno, segundoTurno, "El turno no cambió después de apostar");
    }


    @Test
    void testTurnoSaltaJugadorInactivoTrasRetirarse() {
        // jugador1 está en turno y se retira
        String turnoInicial = GameSessionManager.getJugadorEnTurno(partida.getId(), partida.getJugadores());
        assertEquals(jugador1.getId(), turnoInicial);

        // jugador1 se retira
        gestorApuestas.retirarse(partida, jugador1.getId());

        // Comprobar que está inactivo
        assertFalse(jugador1.isActivo());

        // El turno ahora debería ser de jugador2 (el único activo)
        String nuevoTurno = GameSessionManager.getJugadorEnTurno(partida.getId(), partida.getJugadores());
        assertEquals(jugador2.getId(), nuevoTurno);
    }

    @Test
    public void testPasarSinApuestasPrevias() {
        gestorApuestas.pasar(partida, jugador1.getId());
        assertTrue(partida.getJugadoresQueHanActuado().contains(jugador1.getId()));
    }

    @Test
    public void testRetirarse() {
        gestorApuestas.apostar(partida, jugador1.getId(), 50);
        gestorApuestas.retirarse(partida, jugador2.getId());

        assertFalse(jugador2.isActivo());
        assertNull(jugador2.getMano());
    }

    @Test
    public void testIgualarApuesta() {
        gestorApuestas.apostar(partida, jugador1.getId(), 100);
        gestorApuestas.igualar(partida, jugador2.getId());

        assertEquals(900, jugador2.getFichas());
        assertEquals(100, partida.getApuestasActuales().get(jugador2.getId()));
        assertEquals(200, partida.getBote());
    }

    @Test
    public void testNoPuedePasarConApuestaActiva() {
        gestorApuestas.apostar(partida, jugador1.getId(), 100);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> gestorApuestas.pasar(partida, jugador2.getId()));

        assertEquals("No puedes pasar, hay una apuesta activa.", ex.getMessage());
    }

    @Test
    public void testAllIn() {
        jugador1.setFichas(300);
        gestorApuestas.allIn(partida, jugador1.getId());

        assertEquals(0, jugador1.getFichas());
        assertTrue(jugador1.isAllIn());
        assertEquals(300, partida.getApuestasActuales().get(jugador1.getId()));
    }

    @Test
    public void testCiegaPequenaPuedeIgualarConMinima() {
        jugador1.setFichas(1000);
        jugador2.setFichas(1000);
        jugador1.setActivo(true);
        jugador2.setActivo(true);

        GameSessionManager.iniciarPartida(partida);
        GameSessionManager.avanzarDealer(partida.getId(), partida.getJugadores());
        GameSessionManager.reiniciarFaseYBaraja(partida.getId(), partida.getJugadores());

        // Aplicar ciegas manualmente
        partida.getApuestasActuales().put(jugador1.getId(), 10); // SB
        partida.getApuestasActuales().put(jugador2.getId(), 20); // BB
        jugador1.setFichas(990);
        jugador2.setFichas(980);
        partida.setBote(30);

        // Simular que BB ya ha actuado
        partida.getJugadoresQueHanActuado().add(jugador2.getId());

        // Forzar turno a Joel (jugador1)
        GameSessionManager.forzarTurnoPorJugador(partida.getId(), jugador1.getId(), partida.getJugadores());

        // Joel iguala con la mínima
        gestorApuestas.igualar(partida, jugador1.getId());

        assertEquals(20, partida.getApuestasActuales().get(jugador1.getId()));
        assertEquals(40, partida.getBote()); // ✅ Era 30, y Joel añadió 10

        assertTrue(partida.getJugadoresQueHanActuado().contains(jugador1.getId()));
        assertTrue(partida.getJugadoresQueHanActuado().contains(jugador2.getId()));

        // Avanzar fase manualmente
        GameSessionManager.avanzarFase(partida.getId());
        Baraja baraja = GameSessionManager.getBaraja(partida.getId());
        baraja.repartirCarta(); // quemar
        for (int i = 0; i < 3; i++) {
            partida.getCartasComunitarias().add(baraja.repartirCarta());
        }

        assertEquals(FaseJuego.FLOP, GameSessionManager.getFase(partida.getId()));
        assertEquals(3, partida.getCartasComunitarias().size());
    }

}
