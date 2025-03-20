package edu.badpals.pokerweb.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Mesa {
    @Id
    private String id;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Jugador> jugadores = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Carta> cartasComunes = new ArrayList<>();

    @Transient
    private Baraja baraja;

    public Mesa() {
        this.baraja = new Baraja();
    }

    public Mesa(String id) {
        this.id = id;
        this.baraja = new Baraja();
    }

    public String getId() {
        return id;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public List<Carta> getcartasComunes() {
        return cartasComunes;
    }

    public Baraja getBaraja() {
        return baraja;
    }

    public void agregarCartaComunitaria() {
        if (cartasComunes.size() < 5) {
            Carta nuevaCarta = baraja.repartirCarta();
            if (nuevaCarta != null) {
                cartasComunes.add(nuevaCarta);
            }
        }
    }

    public void reiniciarMesa() {
        cartasComunes.clear();
        baraja.inicializarYBarajar();
        for (Jugador jugador : jugadores) {
            jugador.setMano(new Mano());
            jugador.setFichas(1000);
        }
    }
}
