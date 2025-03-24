package edu.badpals.pokerweb.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Mesa {

    @Id
    private String id;

    @OneToMany(mappedBy = "mesa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Jugador> jugadores = new ArrayList<>();

    @OneToMany(mappedBy = "mesa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Partida> partidas = new ArrayList<>();

    public Mesa() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public List<Partida> getPartidas() {
        return partidas;
    }

    public void agregarJugador(Jugador jugador) {
        jugadores.add(jugador);
    }

    public void agregarPartida(Partida partida) {
        partidas.add(partida);
    }
}
