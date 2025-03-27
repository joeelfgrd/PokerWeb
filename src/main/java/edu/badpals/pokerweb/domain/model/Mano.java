package edu.badpals.pokerweb.domain.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Mano {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Carta> cartas;


    public Mano() {}

    public Mano(List<Carta> cartas) {
        this.cartas = cartas;
    }

    public Long getId() {
        return id;
    }
    public List<Carta> getCartas() {
        return cartas;
    }

}
