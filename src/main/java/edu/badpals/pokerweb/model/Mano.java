package edu.badpals.pokerweb.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Mano {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Carta> cartas;

    @OneToOne
    private Jugador jugador;

    public Mano() {}

    public Mano(Jugador jugador, List<Carta> cartas) {
        this.jugador = jugador;
        this.cartas = cartas;
    }

    public Long getId() {
        return id;
    }
    public List<Carta> getCartas() {
        return cartas;
    }
    public Jugador getJugador() {
        return jugador;
    }
}
